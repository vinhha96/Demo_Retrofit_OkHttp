package vn.com.vng.zalopay.account.ui.presenter;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.account.ui.view.IProfileView;
import vn.com.vng.zalopay.data.cache.AccountStore;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.event.ZaloPayNameEvent;
import vn.com.vng.zalopay.event.ZaloProfileInfoEvent;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.ui.presenter.BaseUserPresenter;
import vn.com.vng.zalopay.ui.presenter.IPresenter;
import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPEvents;
import vn.com.zalopay.wallet.listener.ZPWOnEventConfirmDialogListener;

/**
 * Created by longlv on 25/05/2016.
 * *
 */
public class ProfilePresenter extends BaseUserPresenter implements IPresenter<IProfileView> {

    private IProfileView mView;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private EventBus mEventBus;
    private UserConfig mUserConfig;
    private AccountStore.Repository mAccountRepository;
    private Navigator mNavigator;

    @Inject
    ProfilePresenter(EventBus eventBus, UserConfig userConfig, AccountStore.Repository accountRepository, Navigator navigator) {
        this.mEventBus = eventBus;
        this.mUserConfig = userConfig;
        this.mAccountRepository = accountRepository;
        mNavigator = navigator;
    }

    @Override
    public void setView(IProfileView iProfileView) {
        mView = iProfileView;

        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
    }

    @Override
    public void destroyView() {
        unsubscribeIfNotNull(compositeSubscription);
        mEventBus.unregister(this);
        mView = null;
    }

    @Override
    public void resume() {
        User user = mUserConfig.getCurrentUser();
        if (user != null) {
            updateUserInfo(user);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
    }

    public void getProfile() {
        User user = mUserConfig.getCurrentUser();
        if (user != null) {
            updateUserInfo(user);
            // Neu chua co ZaloPayName hoac Chua get profile level 3
            if (TextUtils.isEmpty(user.zalopayname) ||
                    (user.profilelevel >= 3 && TextUtils.isEmpty(user.identityNumber))) {
                getUserProfile();
            }
        }
    }

    public void checkShowOrHideChangePinView() {
        try {
            boolean isShow = mUserConfig.getCurrentUser().profilelevel >= 2;
            mView.showHideChangePinView(isShow);
        } catch (Exception e) {
            Timber.d(e, "checkShowOrHideChangePinView");
        }
    }

    private int getProfileLevel() {
        User user = mUserConfig.getCurrentUser();
        if (user == null) {
            return 0;
        } else if (mUserConfig.getCurrentUser() == null) {
            return 0;
        } else {
            return mUserConfig.getCurrentUser().profilelevel;
        }
    }

    public void showLoading() {
        mView.showLoading();
    }

    public void hideLoading() {
        mView.hideLoading();
    }

    public void showRetry() {
        mView.showRetry();
    }

    public void hideRetry() {
        mView.hideRetry();
    }


    private void getUserProfile() {
        Subscription subscription = mAccountRepository.getUserProfileLevelCloud()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProfileSubscriber());
        compositeSubscription.add(subscription);
    }

    private void getProfileSuccess() {
        User user = mUserConfig.getCurrentUser();
        if (user != null) {
            updateUserInfo(user);
        }
    }

    public void updateIdentity() {
        updateLevel3(true);
    }

    public void updateEmail() {
        updateLevel3(false);
    }

    private void updateLevel3(boolean isIdentity) {
        if (mView == null) {
            return;
        }

        if (getProfileLevel() < 2) {
            requireUpdateProfileLevel2(mView.getContext().getString(R.string.alert_need_update_level_2));
        } else if (mUserConfig.isWaitingApproveProfileLevel3()) {
            int message = isIdentity ? R.string.waiting_approve_identity : R.string.waiting_approve_email;
            mView.showDialogInfo(mView.getContext().getString(message));
        } else {
            mNavigator.startUpdateProfile3Activity(mView.getContext(), isIdentity);
        }
    }

    public void updateZaloPayID() {
        if (!TextUtils.isEmpty(mUserConfig.getCurrentUser().zalopayname)) {
            return;
        }
        if (mView == null) {
            return;
        }
        if (getProfileLevel() < 2) {
            requireUpdateProfileLevel2(mView.getContext().getString(R.string.alert_need_update_phone));
        } else {
            mNavigator.startEditAccountActivity(mView.getContext());
            ZPAnalytics.trackEvent(ZPEvents.UPDATEZPN_LAUNCH_FROMPROFILE);
        }
    }

    private void requireUpdateProfileLevel2(String message) {
        mView.showConfirmDialog(message,
                new ZPWOnEventConfirmDialogListener() {
                    @Override
                    public void onCancelEvent() {

                    }

                    @Override
                    public void onOKevent() {
                        if (mView != null) {
                            mNavigator.startUpdateProfileLevel2Activity(mView.getContext());
                        }
                    }
                });
    }

    private class ProfileSubscriber extends DefaultSubscriber<Boolean> {

        @Override
        public void onCompleted() {
            ProfilePresenter.this.getProfileSuccess();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onZaloPayNameEventMainThread(ZaloPayNameEvent event) {
        if (mView != null) {
            mView.setZaloPayName(event.zaloPayName);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onEventMainThread(ZaloProfileInfoEvent event) {
        Timber.d("onEventMainThread event %s", event);
        updateUserInfo(mUserConfig.getCurrentUser());
    }

    private void updateUserInfo(User user) {
        if (mView != null) {
            mView.updateUserInfo(user);
        }
    }

    public boolean isWaitingApproveProfileLevel3() {
        return mUserConfig.isWaitingApproveProfileLevel3();
    }
}
