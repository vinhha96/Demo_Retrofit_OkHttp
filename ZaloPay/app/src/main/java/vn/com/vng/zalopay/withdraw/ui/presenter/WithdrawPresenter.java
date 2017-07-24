package vn.com.vng.zalopay.withdraw.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import vn.com.vng.zalopay.BuildConfig;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.data.api.ResponseHelper;
import vn.com.vng.zalopay.data.balance.BalanceStore;
import vn.com.vng.zalopay.data.eventbus.ChangeBalanceEvent;
import vn.com.vng.zalopay.data.exception.UserInputException;
import vn.com.vng.zalopay.data.util.ConfigLoader;
import vn.com.vng.zalopay.data.util.ConvertHelper;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.domain.model.Order;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.domain.repository.ZaloPayRepository;
import vn.com.vng.zalopay.exception.ErrorMessageFactory;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.network.NetworkConnectionException;
import vn.com.vng.zalopay.pw.DefaultPaymentRedirectListener;
import vn.com.vng.zalopay.pw.DefaultPaymentResponseListener;
import vn.com.vng.zalopay.pw.PaymentWrapper;
import vn.com.vng.zalopay.pw.PaymentWrapperBuilder;
import vn.com.vng.zalopay.react.error.PaymentError;
import vn.com.vng.zalopay.ui.presenter.AbstractPresenter;
import vn.com.vng.zalopay.ui.view.ILoadDataView;
import vn.com.vng.zalopay.utils.CShareDataWrapper;
import vn.com.vng.zalopay.withdraw.ui.view.IWithdrawView;
import vn.com.zalopay.wallet.constants.TransactionType;
import vn.com.zalopay.wallet.controller.SDKApplication;
import vn.com.zalopay.wallet.paymentinfo.IBuilder;
import vn.com.zalopay.wallet.repository.appinfo.AppInfoStore;

/**
 * Created by longlv on 11/08/2016.
 * *
 */
public class WithdrawPresenter extends AbstractPresenter<IWithdrawView> {

    private final BalanceStore.Repository mBalanceRepository;
    protected final ZaloPayRepository mZaloPayRepository;
    protected final Navigator mNavigator;
    private final User mUser;
    private final Context mContext;
    private final EventBus mEventBus;

    private final List<Long> mDenominationMoney = ConfigLoader.getDenominationWithdraw();
    private final long mConfigMinWithdrawAmount = ConfigLoader.getMinMoneyWithdraw();
    private final long mConfigMultipleWithdrawAmount = ConfigLoader.getMultipleMoneyWithdraw();
    private long minWithdrawAmount;
    private long maxWithdrawAmount;

    private PaymentWrapper paymentWrapper;

    @Inject
    WithdrawPresenter(Context context, BalanceStore.Repository balanceRepository,
                      ZaloPayRepository zaloPayRepository,
                      Navigator navigator, User user, EventBus eventBus
    ) {
        this.mBalanceRepository = balanceRepository;
        this.mZaloPayRepository = zaloPayRepository;
        this.mNavigator = navigator;
        this.mContext = context;
        this.mUser = user;
        this.mEventBus = eventBus;

        paymentWrapper = new PaymentWrapperBuilder()
                .setResponseListener(new PaymentResponseListener())
                .setRedirectListener(new DefaultPaymentRedirectListener(mNavigator) {
                    @Override
                    public Object getContext() {
                        if (mView == null) {
                            return null;
                        }
                        return mView.getFragment();
                    }
                })
                .build();
        paymentWrapper.initializeComponents();
        initLimitAmount();
    }

    @Override
    public void attachView(IWithdrawView iWithdrawView) {
        super.attachView(iWithdrawView);
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
    }

    @Override
    public void detachView() {
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
        super.detachView();
    }

    @Override
    public void destroy() {
        CShareDataWrapper.dispose();
        super.destroy();
    }

    public void loadView() {
        loadDenomination();
        getBalance();
    }

    private void loadDenomination() {
        List<Long> denominations = new ArrayList<>();
        for (Long denomination : mDenominationMoney) {
            if (denomination >= minWithdrawAmount && denomination <= maxWithdrawAmount) {
                denominations.add(denomination);
            }
        }
        denominations.add(0l);
        if (mView != null) {
            mView.addDenominationMoney(denominations);
        }
    }

    void setBalanceAndCheckDenomination(long balance) {
        if (mView == null) {
            return;
        }
        mView.setBalance(balance);
        Timber.d("Min denomination money : %s", mConfigMinWithdrawAmount);
        mView.showEnoughView(balance < mConfigMinWithdrawAmount);
    }

    private void getBalance() {
        Subscription subscription = mBalanceRepository.balance()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultSubscriber<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        setBalanceAndCheckDenomination(ConvertHelper.unboxValue(aLong, 0));
                    }
                });

        mSubscription.add(subscription);
    }

    private void initLimitAmount() {
        AppInfoStore.Interactor appInfo = SDKApplication.getApplicationComponent().appInfoInteractor();
        minWithdrawAmount = appInfo.minAmountTransType(TransactionType.WITHDRAW);
        maxWithdrawAmount = appInfo.maxAmountTransType(TransactionType.WITHDRAW);
    }

    public void withdraw(final long amount) {
        if (amount <= 0) {
            mView.showVisibleStubView();
            return;
        }

        Subscription subscription = mBalanceRepository.balanceLocal()
                .flatMap(new Func1<Long, Observable<Order>>() {
                    @Override
                    public Observable<Order> call(Long balance) {
                        if (amount > balance) {
                            return Observable.error(new UserInputException(R.string.withdraw_exceed_balance));
                        } else {
                            return mZaloPayRepository.createwalletorder(BuildConfig.WITHDRAW_APP_ID, amount, TransactionType.WITHDRAW, mUser.zaloPayId, mContext.getString(R.string.withdraw_description));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CreateWalletOrderSubscriber());

        mSubscription.add(subscription);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (paymentWrapper != null) {
            paymentWrapper.onActivityResult(requestCode, resultCode, data);
        }
    }

    private final class CreateWalletOrderSubscriber extends DefaultSubscriber<Order> {

        CreateWalletOrderSubscriber() {
        }

        @Override
        public void onStart() {
            if (mView != null) {
                mView.showLoading();
            }
        }

        @Override
        public void onNext(Order order) {
            Timber.d("CreateWalletOrderSubscriber success with order: %s", order);
            WithdrawPresenter.this.onCreateWalletOrderSuccess(order);
        }

        @Override
        public void onError(Throwable e) {
            if (ResponseHelper.shouldIgnoreError(e)) {
                // simply ignore the error
                // because it is handled from event subscribers
                return;
            }

            WithdrawPresenter.this.onCreateWalletOrderError(e);
        }
    }

    void onCreateWalletOrderError(Throwable e) {
        if (mView == null) {
            return;
        }

        mView.hideLoading();
        if (e instanceof NetworkConnectionException) {
            mView.showNetworkErrorDialog();
        }
        if (e instanceof UserInputException) {
            mView.showInputError(ErrorMessageFactory.create(mContext, e));
        } else {
            Timber.e(e, "Server responses with error when client create withdraw order.");
            mView.showError(ErrorMessageFactory.create(mContext, e));
        }
    }

    void onCreateWalletOrderSuccess(Order order) {
        if (mView == null) {
            return;
        }

        paymentWrapper.withdraw((Activity) mView.getContext(), order);
        mView.hideLoading();
    }

    private class PaymentResponseListener extends DefaultPaymentResponseListener {
        PaymentResponseListener() {
        }

        @Override
        protected ILoadDataView getView() {
            return mView;
        }

        @Override
        public void onResponseError(PaymentError paymentError) {
            if (mView == null) {
                return;
            }
            mView.hideLoading();

            super.onResponseError(paymentError);

            if (paymentError == PaymentError.ERR_TRANXSTATUS_NEED_LINKCARD) {
                mNavigator.startLinkCardActivity(mView.getContext());
                return;
            }
            if (paymentError.value() == PaymentError.ERR_CODE_NON_STATE.value()) {
                closeWithDraw();
            }
        }

        @Override
        public void onResponseSuccess(IBuilder builder) {
            closeWithDraw();
        }
    }

    void closeWithDraw() {
        if (mView != null) {
            mView.finish(Activity.RESULT_OK);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBalanceChange(ChangeBalanceEvent event) {
        setBalanceAndCheckDenomination(event.balance);
    }
}
