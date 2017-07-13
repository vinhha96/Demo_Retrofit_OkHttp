package vn.com.vng.zalopay.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.BuildConfig;
import vn.com.vng.zalopay.data.balance.BalanceStore;
import vn.com.vng.zalopay.data.util.ObservableHelper;
import vn.com.vng.zalopay.data.util.Strings;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.exception.PaymentWrapperException;
import vn.com.vng.zalopay.internal.di.components.UserComponent;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.pw.AbsPWResponseListener;
import vn.com.vng.zalopay.pw.PaymentWrapper;
import vn.com.vng.zalopay.pw.PaymentWrapperBuilder;
import vn.com.vng.zalopay.ui.view.ILoadDataView;
import vn.com.zalopay.analytics.ZPPaymentSteps;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.AppInfo;
import vn.com.zalopay.wallet.business.entity.user.UserInfo;
import vn.com.zalopay.wallet.controller.SDKApplication;

import static android.app.Activity.RESULT_OK;

/**
 * Created by hieuvm on 12/4/16.
 * *
 */

public class HandleInAppPayment {

    protected final WeakReference<Activity> mActivity;
    private final CompositeSubscription mCompositeSubscription;

    @Inject
    BalanceStore.Repository mBalanceRepository;

    @Inject
    Navigator mNavigator;

    @Inject
    User mUser;

    protected PaymentWrapper paymentWrapper;
    private String mSource;
    String mBrowser;
    protected long mAppId;

    HandleInAppPayment(Activity activity) {
        mActivity = new WeakReference<>(activity);
        mCompositeSubscription = new CompositeSubscription();
    }

    void initialize() {

        UserComponent userComponent = AndroidApplication.instance().getUserComponent();
        if (userComponent != null) {
            userComponent.inject(this);
            SDKApplication.getBuilder().setRetrofit(userComponent.retrofitConnector());
        }

        if (paymentWrapper == null) {
            paymentWrapper = getPaymentWrapper();
        }
    }

    void doPay(final long appId, @NonNull final String zptranstoken, @Nullable String source, @Nullable String browser) {

        Timber.d("pay [appId:%s zptranstoken:%s source:%s browser:%s]", appId, zptranstoken, source, browser);
        mSource = source;
        mBrowser = browser;
        mAppId = appId;

        Subscription subscription = mBalanceRepository.fetchBalance()
                .subscribeOn(Schedulers.io())
                .doOnError(Timber::d)
                .subscribe(new DefaultSubscriber<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        Timber.d("pay with token [pw:%s]", paymentWrapper);
                        if (paymentWrapper != null) {
                            paymentWrapper.payWithToken(mActivity.get(), appId, zptranstoken, ZPPaymentSteps.OrderSource_AppToApp);
                        }
                    }
                });

        mCompositeSubscription.add(subscription);
    }

    private PaymentWrapper getPaymentWrapper() {
        PaymentWrapper wrapper = new PaymentWrapperBuilder()
                .setResponseListener(new AbsPWResponseListener(mActivity) {

                    private String mTransactionId = "";
                    boolean mIsSuccessful;
                    private String mAppTransId = "";

                    @Override
                    protected ILoadDataView getView() {
                        return null;
                    }

                    @Override
                    public void onError(PaymentWrapperException exception) {
                        Timber.d("pay order error %s", exception);
                        if (shouldRedirectToWeb()) {
                            redirectToWeb(mAppId, mIsSuccessful, mTransactionId, mAppTransId);
                            return;
                        }

                        Activity act = mActivity.get();
                        if (act == null) {
                            return;
                        }

                        Intent data = new Intent();
                        data.putExtra("code", exception.getErrorCode());

                        act.setResult(RESULT_OK, data);
                        act.finish();

                    }

                    @Override
                    public void onCompleted() {

                        Timber.d("pay order completed");

                        if (shouldRedirectToWeb()) {
                            redirectToWeb(mAppId, mIsSuccessful, mTransactionId, mAppTransId);
                            return;
                        }

                        Activity act = mActivity.get();
                        if (act == null) {
                            return;
                        }

                        Intent data = new Intent();
                        data.putExtra("code", 1);
                        data.putExtra("transactionId", mTransactionId);

                        act.setResult(RESULT_OK, data);
                        act.finish();
                    }

                    @Override
                    public void onPreComplete(boolean isSuccessful, String pTransId, String pAppTransId) {
                        mTransactionId = pTransId == null ? "" : pTransId;
                        mAppTransId = pAppTransId == null ? "" : pAppTransId;
                        mIsSuccessful = isSuccessful;
                    }
                }).build();
        wrapper.initializeComponents();
        return wrapper;
    }


    boolean shouldRedirectToWeb() {
        Timber.d("should RedirectToWeb : [Source %s Browser %s]", mSource, mBrowser);
        return !(TextUtils.isEmpty(mSource) || TextUtils.isEmpty(mBrowser))
                && "web".equalsIgnoreCase(mSource);

    }

    void redirectToWeb(long appId, boolean isSuccessful, @NonNull String transId, @NonNull String appTransId) {
        Subscription subscription = ObservableHelper
                .makeObservable(() -> SDKApplication.getApplicationComponent()
                        .appInfoInteractor()
                        .get(appId))
                .filter(appInfo -> appInfo != null && !TextUtils.isEmpty(appInfo.webredirecturl))
                .map(appInfo -> createRedirectUrl(appInfo, appId, transId, appTransId))
                .subscribe(new DefaultSubscriber<String>() {
                    @Override
                    public void onNext(String redirectUrl) {
                        Activity activity = mActivity.get();
                        if (activity != null && !activity.isFinishing()) {
                            startBrowser(mActivity.get(), mBrowser, redirectUrl);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        Activity activity = mActivity.get();
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }

    private String createRedirectUrl(@NonNull AppInfo appInfo, long appId, @NonNull String transId, @NonNull String appTransId) {
        if (TextUtils.isEmpty(appInfo.webredirecturl)) {
            return "";
        }

        try {
            Map<String, String> params = new HashMap<>();
            params.put("appid", String.valueOf(appId));
            params.put("apptransid", appTransId);
            params.put("zptransid", transId);

            return Strings.addUrlQueryParams(appInfo.webredirecturl, params);
        } catch (Exception e) {
            return "";
        }

    }

    void startBrowser(Context context, String browser, String redirectUrl) {
        Timber.d("redirect [url:%s]", redirectUrl);
        if ("chrome".equalsIgnoreCase(browser)) {
            mNavigator.startChrome(context, redirectUrl);
        } else if ("firefox".equalsIgnoreCase(browser)) {
            mNavigator.startFirefox(context, redirectUrl);
        } else {
            mNavigator.startBrowser(context, redirectUrl);
        }
    }

    private void loadGatewayInfoPaymentSDK(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.zalo_userid = String.valueOf(user.zaloId);
        userInfo.zalopay_userid = user.zaloPayId;
        userInfo.accesstoken = user.accesstoken;
        String appVersion = BuildConfig.VERSION_NAME;
        List<Subscription> subscriptions = SDKApplication.loadSDKData(userInfo, appVersion, new DefaultSubscriber<>());
        if (subscriptions != null) {
            mCompositeSubscription.addAll(subscriptions.toArray(new Subscription[0]));
        }
    }

    void loadPaymentSdk() {
        loadGatewayInfoPaymentSDK(mUser);
    }

    void cleanUp() {
        mCompositeSubscription.unsubscribe();
    }
}
