package vn.com.vng.zalopay.bank.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;
import vn.com.vng.zalopay.data.balance.BalanceStore;
import vn.com.vng.zalopay.data.transaction.TransactionStore;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.domain.repository.ZaloPayRepository;
import vn.com.vng.zalopay.event.TokenPaymentExpiredEvent;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.react.error.PaymentError;
import vn.com.vng.zalopay.service.PaymentWrapper;
import vn.com.vng.zalopay.service.PaymentWrapperBuilder;
import vn.com.vng.zalopay.ui.presenter.AbstractPresenter;
import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPEvents;
import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.entity.base.ZPPaymentResult;
import vn.com.zalopay.wallet.business.entity.base.ZPWPaymentInfo;
import vn.com.zalopay.wallet.business.entity.enumeration.ECardType;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.DBaseMap;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.DMappedCard;
import vn.com.zalopay.wallet.business.entity.user.UserInfo;
import vn.com.zalopay.wallet.merchant.CShareData;

/**
 * Created by longlv on 10/25/16.
 * Contains linkCard function
 */

abstract class AbstractLinkCardPresenter<View> extends AbstractPresenter<View> {
    protected PaymentWrapper paymentWrapper;
    private Navigator mNavigator;

    User mUser;

    private SharedPreferences mSharedPreferences;

    protected EventBus mEventBus;

    abstract Activity getActivity();

    abstract Context getContext();

    abstract void onPreComplete();

    abstract void onAddCardSuccess(DBaseMap mappedCreditCard);

    abstract void showLoadingView();

    abstract void hideLoadingView();

    abstract void showErrorView(String message);

    abstract void showNetworkErrorDialog();

    AbstractLinkCardPresenter(ZaloPayRepository zaloPayRepository,
                              Navigator navigator,
                              BalanceStore.Repository balanceRepository,
                              TransactionStore.Repository transactionRepository,
                              User user,
                              SharedPreferences sharedPreferences, EventBus eventBus) {
        mNavigator = navigator;
        this.mUser = user;
        mSharedPreferences = sharedPreferences;
        this.mEventBus = eventBus;
        paymentWrapper = new PaymentWrapperBuilder()
                .setBalanceRepository(balanceRepository)
                .setZaloPayRepository(zaloPayRepository)
                .setTransactionRepository(transactionRepository)
                .setResponseListener(new PaymentResponseListener())
                .build();
    }

    @Override
    public void destroy() {
        //release cache
        CShareData.dispose();
        GlobalData.initApplication(null);

        super.destroy();
    }

    void addLinkCard() {
        if (getContext() == null) {
            return;
        }
        if (mUser.profilelevel < 2) {
            mNavigator.startUpdateProfileLevel2Activity(getContext());
        } else {
            paymentWrapper.linkCard(getActivity());
            hideLoadingView();
            ZPAnalytics.trackEvent(ZPEvents.MANAGECARD_ADDCARD_LAUNCH);
        }
    }

    private class PaymentResponseListener implements PaymentWrapper.IResponseListener {
        @Override
        public void onParameterError(String param) {
            showErrorView(param);
        }

        @Override
        public void onResponseError(PaymentError paymentError) {
            if (paymentError == PaymentError.ERR_CODE_INTERNET) {
                showNetworkErrorDialog();
            }
        }

        @Override
        public void onResponseSuccess(ZPPaymentResult zpPaymentResult) {
            ZPWPaymentInfo paymentInfo = zpPaymentResult.paymentInfo;
            if (paymentInfo == null) {
                Timber.d("onResponseSuccess paymentInfo null");
                return;
            }
            onAddCardSuccess(paymentInfo.mapBank);
        }

        @Override
        public void onResponseTokenInvalid() {
            if (mView == null) {
                return;
            }

            mEventBus.postSticky(new TokenPaymentExpiredEvent());
        }

        @Override
        public void onAppError(String msg) {
            showErrorView(msg);
        }

        @Override
        public void onPreComplete(boolean isSuccessful, String tId, String pAppTransId) {
            Timber.d("onPreComplete isSuccessful [%s]", isSuccessful);
            if (isSuccessful) {
                AbstractLinkCardPresenter.this.onPreComplete();
            }
        }

        @Override
        public void onNotEnoughMoney() {

        }
    }

    String detectCardType(String bankcode, String first6cardno) {
        if (TextUtils.isEmpty(bankcode)) {
            return ECardType.UNDEFINE.toString();
        } else if (bankcode.equals(ECardType.PVTB.toString())) {
            return ECardType.PVTB.toString();
        } else if (bankcode.equals(ECardType.PBIDV.toString())) {
            return ECardType.PBIDV.toString();
        } else if (bankcode.equals(ECardType.PVCB.toString())) {
            return ECardType.PVCB.toString();
        } else if (bankcode.equals(ECardType.PSCB.toString())) {
            return ECardType.PSCB.toString();
        /*} else if (bankcode.equals(ECardType.PEIB.toString())) {
            return ECardType.PEIB.toString();
        } else if (bankcode.equals(ECardType.PAGB.toString())) {
            return ECardType.PAGB.toString();
        } else if (bankcode.equals(ECardType.PTPB.toString())) {
            return ECardType.PTPB.toString();*/
        } else if (bankcode.equals(ECardType.UNDEFINE.toString())) {
            return ECardType.UNDEFINE.toString();
        } else {
            try {
                UserInfo userInfo = new UserInfo();
                userInfo.zaloPayUserId = mUser.zaloPayId;
                userInfo.accessToken = mUser.accesstoken;
                return CShareData.getInstance().setUserInfo(userInfo).
                        detectCardType(first6cardno).toString();
            } catch (Exception e) {
                Timber.w(e, "detectCardType exception [%s]", e.getMessage());
            }
        }
        return ECardType.UNDEFINE.toString();
    }
}
