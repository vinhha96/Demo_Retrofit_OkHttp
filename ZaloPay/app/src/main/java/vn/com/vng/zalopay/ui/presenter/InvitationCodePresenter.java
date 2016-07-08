package vn.com.vng.zalopay.ui.presenter;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.data.api.ResponseHelper;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.exception.ErrorMessageFactory;
import vn.com.vng.zalopay.ui.view.IInvitationCodeView;

/**
 * Created by AnhHieu on 6/27/16.
 */
@Singleton
public class InvitationCodePresenter extends BaseAppPresenter implements IPresenter<IInvitationCodeView> {

    @Inject
    public InvitationCodePresenter() {
    }

    IInvitationCodeView mView;
    CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    public void setView(IInvitationCodeView iInvitationCodeView) {
        mView = iInvitationCodeView;
    }

    @Override
    public void destroyView() {
        mView = null;
        unsubscribeIfNotNull(compositeSubscription);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

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

    public void sendCode(String code) {
        showLoadingView();
        Subscription subscription = passportRepository.verifyCode(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoginPaymentSubscriber());
        compositeSubscription.add(subscription);
    }

    private final class LoginPaymentSubscriber extends DefaultSubscriber<User> {
        public LoginPaymentSubscriber() {
        }

        @Override
        public void onNext(User user) {
            Timber.d("login success " + user);
            // TODO: Use your own attributes to track content views in your app
            Answers.getInstance().logLogin(new LoginEvent().putSuccess(true));

            InvitationCodePresenter.this.onLoginSuccess(user);
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            if (ResponseHelper.shouldIgnoreError(e)) {
                // simply ignore the error
                // because it is handled from event subscribers
                return;
            }
            InvitationCodePresenter.this.onLoginError(e);
        }
    }

    private void showErrorView(String message) {
        mView.showError(message);
    }

    private void gotoHomeScreen() {
        mView.gotoMainActivity();
    }

    private final void onLoginSuccess(User user) {
        this.hideLoadingView();
        AndroidApplication.instance().createUserComponent(user);
        this.gotoHomeScreen();
    }

    private final void onLoginError(Throwable e) {
        hideLoadingView();
        String message = ErrorMessageFactory.create(applicationContext, e);
        showErrorView(message);
    }


}
