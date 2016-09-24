package vn.com.vng.zalopay.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.zing.zalo.zalosdk.oauth.LoginVia;
import com.zing.zalo.zalosdk.oauth.ZaloSDK;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.account.network.listener.LoginListener;
import vn.com.vng.zalopay.data.api.ResponseHelper;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.data.exception.InvitationCodeException;
import vn.com.vng.zalopay.data.exception.ServerMaintainException;
import vn.com.vng.zalopay.data.util.NetworkHelper;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.domain.repository.PassportRepository;
import vn.com.vng.zalopay.exception.ErrorMessageFactory;
import vn.com.vng.zalopay.ui.view.ILoginView;
import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPEvents;

/**
 * Created by AnhHieu on 3/26/16.
 * *
 */

public final class LoginPresenter extends BaseAppPresenter implements IPresenter<ILoginView>, LoginListener.ILoginZaloListener {

    private ILoginView mView;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private LoginListener mLoginListener = new LoginListener(this);
    private Context mApplicationContext;
    private UserConfig mUserConfig;
    private PassportRepository mPassportRepository;

    @Inject
    public LoginPresenter(Context applicationContext,
                          UserConfig userConfig,
                          PassportRepository passportRepository) {
        this.mApplicationContext = applicationContext;
        this.mUserConfig = userConfig;
        this.mPassportRepository = passportRepository;
    }

    @Override
    public void setView(ILoginView view) {
        this.mView = view;
        Timber.d("setView: mview %s", mView);
    }

    @Override
    public void destroyView() {
        hideLoadingView();
        this.mView = null;
        Timber.d("destroyView:");
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
        this.destroyView();
        this.unsubscribe();
    }

    private void unsubscribe() {
        unsubscribeIfNotNull(compositeSubscription);
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        try {
            ZaloSDK.Instance.onActivityResult(activity, requestCode, resultCode, data);
        } catch (Exception ex) {
            Timber.w(ex, " message " + ex.getMessage());
        }
    }

    public void loginZalo(Activity activity) {
        if (NetworkHelper.isNetworkAvailable(mApplicationContext)) {
            //   showLoadingView();
            ZaloSDK.Instance.authenticate(activity, LoginVia.APP_OR_WEB, mLoginListener);
        } else {
            showErrorView(mApplicationContext.getString(R.string.exception_no_connection_try_again));
        }
    }

    @Override
    public void onAuthError(int errorCode, String message) {

     /*   zaloProfilePreferences.setUserId(0);
        zaloProfilePreferences.setAuthCode("");*/
        Timber.d(" Authen Zalo Error message %s error %s", message, errorCode);
        if (mView != null) { // chua destroy view
            if (!NetworkHelper.isNetworkAvailable(mApplicationContext)) {
                showErrorView(mApplicationContext.getString(R.string.exception_no_connection_try_again));
                ZPAnalytics.trackEvent(ZPEvents.LOGINFAILED_NONETWORK);
            } else if (errorCode == -1111) {
                Timber.d("onAuthError User click backpress");
            } else {
                if (TextUtils.isEmpty(message)) {
                    message = mApplicationContext.getString(R.string.exception_login_zalo_error);
                }
                showErrorView(message);
                ZPAnalytics.trackEvent(ZPEvents.LOGINFAILED_USERDENIED);
            }
            hideLoadingView();
        }
    }

    @Override
    public void onGetOAuthComplete(long zaloId, String authCode, String channel) {
        Timber.d("OAuthComplete uid: %s authCode: %s", zaloId, authCode);

        mUserConfig.saveUserInfo(zaloId, "", "", 0, 0);
        if (mView != null) {
            this.getZaloProfileInfo(mApplicationContext, mUserConfig);
            this.loginPayment(zaloId, authCode);
        }

        ZPAnalytics.trackEvent(ZPEvents.LOGINSUCCESS_ZALO);
    }


    private void showLoadingView() {
        if (mView != null) {
            mView.showLoading();
        }
    }

    private void hideLoadingView() {
        if (mView != null) {
            mView.hideLoading();
        }
    }

    private void loginPayment(long zuid, String zalooauthcode) {
        showLoadingView();
        Subscription subscriptionLogin = mPassportRepository.login(zuid, zalooauthcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoginPaymentSubscriber());
        compositeSubscription.add(subscriptionLogin);
    }

    private void showErrorView(String message) {
        mView.showError(message);
    }

    private void gotoHomeScreen() {
        mView.gotoMainActivity();
    }

    private void onLoginSuccess(User user) {
        Timber.d("session %s zaloPayId %s need_invitation %s", user.accesstoken, user.zaloPayId, user.need_invitation);
        // Khởi tạo user component
        hideLoadingView();
        if (user.need_invitation == 1) {
            ZPAnalytics.trackEvent(ZPEvents.NEEDINVITATIONCODE);
            ZPAnalytics.trackEvent(ZPEvents.INVITATIONFROMLOGIN);
        } else {
            AndroidApplication.instance().createUserComponent(user);
            this.gotoHomeScreen();
            ZPAnalytics.trackEvent(ZPEvents.APPLAUNCHHOMEFROMLOGIN);
        }
    }

    private void onLoginError(Throwable e) {
        hideLoadingView();
        if (e instanceof InvitationCodeException) {
            mView.gotoInvitationCode();
        } else {

            Timber.w(e, "exception  ");
            String message = ErrorMessageFactory.create(mApplicationContext, e);
            showErrorView(message);
            ZPAnalytics.trackEvent(ZPEvents.LOGINFAILED_API_ERROR);
        }

    }

    private final class LoginPaymentSubscriber extends DefaultSubscriber<User> {

        @Override
        public void onNext(User user) {
            Timber.d("login success %s", user);
            // TODO: Use your own attributes to track content views in your app
            Answers.getInstance().logLogin(new LoginEvent().putSuccess(true));

            LoginPresenter.this.onLoginSuccess(user);
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            if (ResponseHelper.shouldIgnoreError(e)) {
                // simply ignore the error
                // because it is handled from event subscribers

                if (e instanceof ServerMaintainException) {
                    LoginPresenter.this.hideLoadingView();
                }

                return;
            }

            LoginPresenter.this.onLoginError(e);
        }
    }
}
