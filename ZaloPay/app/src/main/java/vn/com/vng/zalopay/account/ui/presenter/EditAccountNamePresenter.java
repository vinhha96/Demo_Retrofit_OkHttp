package vn.com.vng.zalopay.account.ui.presenter;

import android.content.Context;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import vn.com.vng.zalopay.account.ui.view.IEditAccountNameView;
import vn.com.vng.zalopay.data.NetworkError;
import vn.com.vng.zalopay.data.api.ResponseHelper;
import vn.com.vng.zalopay.data.cache.AccountStore;
import vn.com.vng.zalopay.data.exception.BodyException;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.exception.ErrorMessageFactory;
import vn.com.vng.zalopay.ui.presenter.BaseUserPresenter;
import vn.com.vng.zalopay.ui.presenter.IPresenter;
import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPEvents;

/**
 * Created by AnhHieu on 8/12/16.
 */
public class EditAccountNamePresenter extends BaseUserPresenter implements IPresenter<IEditAccountNameView> {

    private IEditAccountNameView mView;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private AccountStore.Repository accountRepository;
    private Context applicationContext;

    @Inject
    public EditAccountNamePresenter(AccountStore.Repository accountRepository, Context applicationContext) {
        this.accountRepository = accountRepository;
        this.applicationContext = applicationContext;
    }

    @Override
    public void attachView(IEditAccountNameView view) {
        mView = view;
    }

    @Override
    public void detachView() {
        unsubscribeIfNotNull(mCompositeSubscription);
        mView = null;
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

    public void existAccountName(String accountName) {
        Timber.d("exist account name %s", accountName);
        Subscription subscription = accountRepository.checkZaloPayNameExist(accountName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CheckAccountNameSubscriber());
        mCompositeSubscription.add(subscription);
    }

    public void updateAccountName(String accountName) {
        Timber.d("update account name %s", accountName);
        Subscription subscription = accountRepository.updateZaloPayName(accountName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UpdateAccountNameSubscriber());
        mCompositeSubscription.add(subscription);
    }

    private class CheckAccountNameSubscriber extends DefaultSubscriber<Boolean> {

        @Override
        public void onStart() {
            if (mView != null) {
                mView.showLoading();
            }
        }

        @Override
        public void onError(Throwable e) {
            Timber.d(e, "check account error");
            if (mView == null) {
                return;
            }
            mView.hideLoading();

            if (ResponseHelper.shouldIgnoreError(e)) {
                return;
            }

            if (e instanceof BodyException &&
                    ((BodyException) e).errorCode == NetworkError.USER_EXISTED) {
                ZPAnalytics.trackEvent(ZPEvents.UPDATEZPN_INUSED);
                mView.accountNameValid(false);
            } else {
                mView.showError(ErrorMessageFactory.create(applicationContext, e));
            }

        }

        @Override
        public void onNext(Boolean resp) {
            if (mView != null) {
                mView.hideLoading();
                mView.accountNameValid(resp);
            }
        }
    }


    private class UpdateAccountNameSubscriber extends DefaultSubscriber<Boolean> {
        @Override
        public void onStart() {
            if (mView != null) {
                mView.showLoading();
            }
        }

        @Override
        public void onError(Throwable e) {
            Timber.d(e, "update account error");
            if (mView == null) {
                return;
            }

            mView.hideLoading();


            if (ResponseHelper.shouldIgnoreError(e)) {
                return;
            }

            if (e instanceof BodyException &&
                    ((BodyException) e).errorCode == NetworkError.USER_EXISTED) {
                ZPAnalytics.trackEvent(ZPEvents.UPDATEZPN_INUSED2);
                mView.accountNameValid(false);
            } else {
                mView.showError(ErrorMessageFactory.create(applicationContext, e));
            }
        }

        @Override
        public void onNext(Boolean resp) {
            if (mView != null) {
                mView.hideLoading();
                mView.editAccountNameSuccess();
            }
        }
    }
}
