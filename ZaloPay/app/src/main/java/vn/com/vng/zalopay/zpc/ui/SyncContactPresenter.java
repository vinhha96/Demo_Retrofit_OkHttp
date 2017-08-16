package vn.com.vng.zalopay.zpc.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.zalopay.ui.widget.util.TimeUtils;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.data.zpc.ZPCStore;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.exception.ErrorMessageFactory;
import vn.com.vng.zalopay.ui.presenter.AbstractPresenter;

/**
 * Created by hieuvm on 7/21/17.
 * *
 */

public final class SyncContactPresenter extends AbstractPresenter<SyncContactView> {

    protected final Context mContext;
    private final ZPCStore.Repository mFriendRepository;

    @Inject
    SyncContactPresenter(Context context, ZPCStore.Repository friendRepository) {
        mContext = context;
        mFriendRepository = friendRepository;
    }

    public void loadView() {
        getContactCount();
        getLastTimeUpdate();
    }

    private void getContactCount() {
        Subscription subscription = Observable.zip(mFriendRepository.getUserContactBookCount(), mFriendRepository.getZaloFriendListCount(), Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultSubscriber<Pair<Long, Long>>() {
                    @Override
                    public void onNext(Pair<Long, Long> data) {
                        if (mView == null) {
                            return;
                        }

                        // control number contact book hide/show
                        if (data.first > 0) {
                            mView.setContactBookCount(data.first);
                            mView.hideAvatarArrow();
                        } else {
                            mView.hideContactBookCount();
                            mView.showAvatarArrow();
                        }

                        // control number friend list (Zalo)
                        mView.setFriendListCount(data.second);
                    }
                });

        mSubscription.add(subscription);
    }

    private void getLastTimeUpdate() {
        Subscription subscription = mFriendRepository.getLastTimeSyncContact()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultSubscriber<Long>() {
                    @Override
                    public void onNext(Long time) {
                        setLastTimeUpdate(time);
                    }
                });
        mSubscription.add(subscription);
    }

    public void syncContact() {
        Subscription subscription = mFriendRepository.syncImmediateContact()
                .doOnError(Timber::d)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultSubscriber<Boolean>() {
                    @Override
                    public void onStart() {
                        mView.showLoading();
                    }

                    @Override
                    public void onNext(Boolean time) {
                        if (mView == null) {
                            return;
                        }

                        mView.hideLoading();
                        mView.showSyncContactSuccess();
                        loadView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        String message = ErrorMessageFactory.create(mContext, e);
                        if (mView != null) {
                            mView.hideLoading();
                            mView.showError(message);
                        }
                    }
                });
        mSubscription.add(subscription);
    }

    protected void setLastTimeUpdate(@Nullable Long time) {

        String message;
        if (time == null || time <= 0) {
            message = mContext.getString(R.string.not_sync_contact);
        } else {
            message = TimeUtils.formatTime(time, "dd/MM/yyyy HH:mm", "GMT+7");
        }

        if (mView != null) {
            mView.setLastTimeSyncContact(message);
        }
    }
}