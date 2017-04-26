package vn.com.vng.zalopay.bank.ui;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.zalopay.ui.widget.dialog.listener.ZPWOnEventConfirmDialogListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.bank.models.BankAccount;
import vn.com.vng.zalopay.data.balance.BalanceStore;
import vn.com.vng.zalopay.data.transaction.TransactionStore;
import vn.com.vng.zalopay.data.util.Lists;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.domain.repository.ZaloPayRepository;
import vn.com.vng.zalopay.event.LoadIconFontEvent;
import vn.com.vng.zalopay.event.RefreshBankAccountEvent;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.utils.CShareDataWrapper;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.DBankAccount;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.DBaseMap;
import vn.com.zalopay.wallet.constants.CardType;
import vn.com.zalopay.wallet.merchant.entities.ZPCard;
import vn.com.zalopay.wallet.merchant.listener.IGetCardSupportListListener;

/**
 * Created by longlv on 1/17/17.
 * Logic of LinkAccountFragment.
 */
class LinkAccountPresenter extends AbstractLinkCardPresenter<ILinkAccountView> {

    @Inject
    LinkAccountPresenter(ZaloPayRepository zaloPayRepository,
                         Navigator navigator,
                         BalanceStore.Repository balanceRepository,
                         TransactionStore.Repository transactionRepository,
                         User user, EventBus eventBus) {
        super(zaloPayRepository, navigator, balanceRepository, transactionRepository, user, eventBus);
    }

    void linkAccountIfNotExist(ZPCard zpCard) {
        List<DBankAccount> mapCardLis = CShareDataWrapper.getMapBankAccountList(mUser.zaloPayId);
        if (checkLinkedBankAccount(transformBankAccount(mapCardLis), zpCard.getCardCode())) {
            showAccountHasLinked(zpCard);
        } else {
            linkAccount(zpCard);
        }
    }

    void refreshLinkedBankAccount() {
        List<BankAccount> bankAccounts = getLinkedBankAccount();
        mView.refreshLinkedAccount(bankAccounts);
        checkSupportVcbOnly(bankAccounts);
    }

    @Override
    public void resume() {
        if (mView != null && mView.getUserVisibleHint()) {
            refreshLinkedBankAccount();
        }
    }

    private boolean linkedVcbAccount(List<BankAccount> listLinkedAccount) {
        for (BankAccount bankAccount : listLinkedAccount) {
            if (bankAccount == null || TextUtils.isEmpty(bankAccount.mBankCode)) {
                continue;
            }
            Timber.d("Check linked vcb, bankCode [%s]", bankAccount.mBankCode);
            if (CardType.PVCB.equals(bankAccount.mBankCode)) {
                return true;
            }
        }
        return false;
    }

    private List<ZPCard> getBanksSupportLinkAccount(List<ZPCard> bankList) {
        if (Lists.isEmptyOrNull(bankList)) {
            return Collections.emptyList();
        }
        List<ZPCard> bankSupportLinkAccount = new ArrayList<>();
        for (ZPCard card : bankList) {
            if (card == null || !card.isBankAccount()) {
                continue;
            }
            bankSupportLinkAccount.add(card);
        }
        return bankSupportLinkAccount;
    }

    private void checkSupportVcbOnly(List<BankAccount> listLinkedAccount) {
        if (Lists.isEmptyOrNull(listLinkedAccount) || !linkedVcbAccount(listLinkedAccount)) {
            return;
        }
        getListBankSupport(new IGetCardSupportListListener() {
            @Override
            public void onProcess() {
                Timber.d("Get card support list to check support vcb only on process");
            }

            @Override
            public void onComplete(ArrayList<ZPCard> cardSupportList) {
                hideLoadingView();
                if (mView == null) {
                    return;
                }
                List<ZPCard> banksSupportLinkAcc = getBanksSupportLinkAccount(cardSupportList);
                if (!Lists.isEmptyOrNull(banksSupportLinkAcc)
                        && banksSupportLinkAcc.size() == 1
                        && CardType.PVCB.equals(banksSupportLinkAcc.get(0).getCardCode())) {
                    mView.showSupportVcbOnly();
                } else {
                    mView.hideSupportVcbOnly();
                }
            }

            @Override
            public void onError(String pErrorMess) {
                Timber.d("Get card support to check support vcb only error : message [%s]", pErrorMess);
                hideLoadingView();
            }

            @Override
            public void onUpVersion(boolean forceUpdate, String latestVersion, String message) {
                hideLoadingView();
            }
        });
    }

    void removeLinkAccount(BankAccount bankAccount) {
        if (paymentWrapper == null || mView == null || bankAccount == null) {
            return;
        }
        paymentWrapper.unLinkAccount(mView.getActivity(), bankAccount.mBankCode);
    }

    @Override
    Activity getActivity() {
        if (mView == null) {
            return null;
        }
        return mView.getActivity();
    }

    @Override
    Context getContext() {
        if (mView == null) {
            return null;
        }
        return mView.getContext();
    }

    @Override
    void onPreComplete() {

    }

    @Override
    void onAddCardSuccess(DBaseMap mappedCreditCard) {
        if (mView == null || !(mappedCreditCard instanceof DBankAccount)) {
            return;
        }

        DBankAccount dBankAccount = ((DBankAccount) mappedCreditCard);
        mView.insertData(transformBankAccount(dBankAccount));

        if (mPayAfterLinkAcc) {
            mView.showConfirmPayAfterLinkAcc();
        }
    }

    @Override
    void onLoadIconFontSuccess() {
        if (mView != null) {
            mView.refreshLinkedAccount();
        }
    }

    @Override
    void onDownloadPaymentSDKComplete() {
        if (mView != null) {
            mView.refreshBanksSupport();
        }
    }

    @Override
    protected void showLoadingView() {
        if (mView == null) {
            return;
        }
        mView.showLoading();
    }

    @Override
    protected void hideLoadingView() {
        if (mView == null) {
            return;
        }
        mView.hideLoading();
    }

    @Override
    protected void showErrorView(String message) {
        if (mView == null) {
            return;
        }
        mView.hideLoading();
        mView.showError(message);
    }

    private void showErrorView(int msgResource) {
        if (mView == null || mView.getContext() == null) {
            return;
        }
        mView.hideLoading();
        mView.showError(getContext().getString(msgResource));
    }

    @Override
    void showNetworkErrorDialog() {
        if (mView == null) {
            return;
        }
        mView.hideLoading();
        mView.showNetworkErrorDialog();
    }

    @Override
    void showRetryDialog(String message, ZPWOnEventConfirmDialogListener listener) {
        if (mView == null) {
            return;
        }
        mView.showRetryDialog(message, listener);
    }

    @Override
    void onUpdateVersion(boolean forceUpdate, String latestVersion, String message) {
        if (mView == null) {
            return;
        }
        mView.onUpdateVersion(forceUpdate, latestVersion, message);
    }

    @Override
    void onGetCardSupportSuccess(ArrayList<ZPCard> cardSupportList) {
        if (cardSupportList == null || cardSupportList.size() <= 0) {
            return;
        }
        ArrayList<ZPCard> cards = new ArrayList<>();
        for (ZPCard card : cardSupportList) {
            if (card == null || !card.isBankAccount()) {
                continue;
            }
            cards.add(card);
        }
        if (Lists.isEmptyOrNull(cards)) {
            showErrorView(R.string.link_account_bank_support_empty);
        } else if (cards.size() == 1) {
            linkAccount(cards.get(0));
        } else {
            if (mView != null) {
                mView.showListBankDialog(cards);
            }
        }
    }

    private void showAccountHasLinked(ZPCard zpCard) {
        hideLoadingView();
        if (mView == null || mView.getContext() == null) {
            return;
        }

        String message = String.format(mView.getContext().getString(R.string.bank_account_has_linked),
                zpCard.getCardLogoName());
        mView.showError(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadIconFontSuccess(LoadIconFontEvent event) {
        Timber.d("Load icon font success.");
        if (event != null && mView != null) {
            mView.refreshLinkedAccount();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshBankAccount(RefreshBankAccountEvent event) {
        if (!event.mIsError) {
            refreshLinkedBankAccount();
        }
    }
}
