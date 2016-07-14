package vn.com.vng.zalopay.service;

import android.app.Activity;
import android.text.TextUtils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;

import java.lang.ref.WeakReference;
import java.util.Locale;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.data.api.ResponseHelper;
import vn.com.vng.zalopay.data.balance.BalanceStore;
import vn.com.vng.zalopay.data.transaction.TransactionStore;
import vn.com.vng.zalopay.data.exception.BodyException;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.domain.model.MerChantUserInfo;
import vn.com.vng.zalopay.domain.model.Order;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.domain.repository.ZaloPayIAPRepository;
import vn.com.vng.zalopay.mdl.IPaymentService;
import vn.com.vng.zalopay.mdl.error.PaymentError;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.zalopay.wallet.entity.base.ZPPaymentResult;

/**
 * Created by longlv on 02/06/2016.
 */
public class PaymentServiceImpl implements IPaymentService {

    final ZaloPayIAPRepository zaloPayIAPRepository;
    final BalanceStore.Repository mBalanceRepository;
    final User user;
    final TransactionStore.Repository mTransactionRepository;
    private PaymentWrapper paymentWrapper;
    protected final Navigator navigator = AndroidApplication.instance().getAppComponent().navigator();

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public PaymentServiceImpl(ZaloPayIAPRepository zaloPayIAPRepository, BalanceStore.Repository balanceRepository, User user, TransactionStore.Repository transactionRepository) {
        this.zaloPayIAPRepository = zaloPayIAPRepository;
        this.mBalanceRepository = balanceRepository;
        this.user = user;
        mTransactionRepository = transactionRepository;
    }

    @Override
    public void pay(Activity activity, final Promise promise, Order order) {

        final WeakReference<Activity> mWeakReference = new WeakReference<Activity>(activity);

        this.paymentWrapper = new PaymentWrapper(mBalanceRepository, null, new PaymentWrapper.IViewListener() {
            @Override
            public Activity getActivity() {
                return mWeakReference.get();
            }
        }, new PaymentWrapper.IResponseListener() {
            @Override
            public void onParameterError(String param) {
                reportInvalidParameter(promise, param);
            }

            @Override
            public void onResponseError(int status) {
                errorCallback(promise, status);
            }

            @Override
            public void onResponseSuccess(ZPPaymentResult zpPaymentResult) {
                updateTransaction();
                balanceUpdate();
                successCallback(promise, null);
            }

            @Override
            public void onResponseTokenInvalid() {

            }

            @Override
            public void onResponseCancel() {
                errorCallback(promise, PaymentError.ERR_CODE_USER_CANCEL);
                destroyVariable();
            }

            @Override
            public void onNotEnoughMoney() {
                navigator.startDepositActivity(AndroidApplication.instance().getApplicationContext());
            }
        });

        this.paymentWrapper.payWithOrder(order);
    }

    private void unsubscribeIfNotNull(CompositeSubscription subscription) {
        if (subscription != null) {
            subscription.clear();
        }
    }

    private void reportInvalidParameter(Promise promise, String parameterName) {
        if (promise == null) {
            return;
        }

        String message = String.format(Locale.getDefault(), "invalid %s", parameterName);
        Timber.d("Invalid parameter [%s]", parameterName);
        errorCallback(promise, PaymentError.ERR_CODE_INPUT, message);
    }

    private void successCallback(Promise promise, WritableMap object) {
        if (promise == null) {
            return;
        }
        WritableMap item = Arguments.createMap();
        item.putInt("code", PaymentError.ERR_CODE_SUCCESS);
        if (object != null) {
            item.putMap("data", object);
        }
        promise.resolve(item);
    }


    private void errorCallback(Promise promise, int errorCode) {
        errorCallback(promise, errorCode, null);
    }

    private void errorCallback(Promise promise, int errorCode, String message) {
        if (promise == null) {
            return;
        }
        WritableMap item = Arguments.createMap();
        item.putInt("code", errorCode);
        if (!TextUtils.isEmpty(message)) {
            item.putString("message", message);
        }
        promise.resolve(item);
    }

    @Override
    public void getUserInfo(Promise promise, long appId) {

        Timber.d("get user info appId %s", appId);

        Subscription subscription = zaloPayIAPRepository.getMerchantUserInfo(appId)
                .subscribe(new UserInfoSubscriber(promise));
        compositeSubscription.add(subscription);
    }

    public void destroyVariable() {
//        paymentListener = null;
        paymentWrapper = null;
        unsubscribeIfNotNull(compositeSubscription);
    }

    private void updateTransaction() {
        mTransactionRepository.updateTransaction().subscribe(new DefaultSubscriber<Boolean>());
    }

    private void balanceUpdate() {
        // update balance
        mBalanceRepository.updateBalance().subscribe(new DefaultSubscriber<>());
    }

    private final class UserInfoSubscriber extends DefaultSubscriber<MerChantUserInfo> {

        private Promise promise;

        public UserInfoSubscriber(Promise promise) {
            this.promise = promise;
        }

        @Override
        public void onError(Throwable e) {
            if (ResponseHelper.shouldIgnoreError(e)) {
                // simply ignore the error
                // because it is handled from based activity
                return;
            }

            Timber.w(e, "Error on getting merchant user information");

            errorCallback(promise, getErrorCode(e));
        }

        @Override
        public void onNext(MerChantUserInfo merChantUserInfo) {

            Timber.d("get user info %s %s ", merChantUserInfo, merChantUserInfo.muid);

            successCallback(promise, transform(merChantUserInfo));
        }

        private WritableMap transform(MerChantUserInfo merChantUserInfo) {
            if (merChantUserInfo == null) {
                return null;
            }

            WritableMap data = Arguments.createMap();
            data.putString("mUid", merChantUserInfo.muid);
            data.putString("mAccessToken", merChantUserInfo.maccesstoken);
            data.putString("displayName", merChantUserInfo.displayname);
            data.putString("dateOfBirth", merChantUserInfo.birthdate);
            data.putString("gender", String.valueOf(merChantUserInfo.usergender));
            return data;
        }

        private int getErrorCode(Throwable e) {
            if (e instanceof BodyException) {
                return ((BodyException) e).errorCode;
            } else {
                return PaymentError.ERR_CODE_UNKNOWN;
            }
        }
    }
}
