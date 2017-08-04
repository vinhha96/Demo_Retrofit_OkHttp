package vn.com.vng.zalopay.transfer.ui.friendlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import vn.com.vng.zalopay.Constants;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.data.api.ResponseHelper;
import vn.com.vng.zalopay.data.util.Lists;
import vn.com.vng.zalopay.data.zfriend.FriendConfig;
import vn.com.vng.zalopay.data.zfriend.FriendStore;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.domain.model.FavoriteData;
import vn.com.vng.zalopay.domain.model.ZPProfile;
import vn.com.vng.zalopay.exception.ErrorMessageFactory;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.transfer.model.TransferObject;
import vn.com.vng.zalopay.ui.presenter.AbstractPresenter;
import vn.com.vng.zalopay.utils.DialogHelper;

/**
 * Created by AnhHieu on 10/10/16.
 * *
 */

final class ZaloPayContactListPresenter extends AbstractPresenter<IZaloFriendListView> {

    private static final int MAX_FAVORITE = 10;

    protected final FriendStore.Repository mFriendRepository;
    protected final Context mContext;
    protected final Navigator mNavigator;

    @ZpcViewType
    private int mViewType = ZpcViewType.ZPC_All;

    @Inject
    ZaloPayContactListPresenter(Context context, Navigator navigator, FriendStore.Repository friendRepository) {
        this.mFriendRepository = friendRepository;
        this.mContext = context;
        this.mNavigator = navigator;
    }

    private boolean isPhoneBook() {
        return mViewType == ZpcViewType.ZPC_PhoneBook;
    }

    void refreshFriendList() {
        Subscription subscription = mFriendRepository.fetchZaloFriendFullInfo()
                .flatMap(aBoolean -> mFriendRepository.getZaloFriendsCursor(isPhoneBook()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FriendListSubscriber(false));

        mSubscription.add(subscription);
    }

    private boolean isEnableSyncContact() {
        return FriendConfig.sEnableSyncContact;
    }

    void initialize(@Nullable String keySearch, @ZpcViewType int viewType) {
        mViewType = viewType;
        if (!TextUtils.isEmpty(keySearch)) {
            doSearch(keySearch);
        } else {
            getFriendList();
        }
        getFavorite(MAX_FAVORITE);
        initView();
    }

    private void initView() {
        if (mView == null) {
            return;
        }
        mView.setMaxFavorite(MAX_FAVORITE);

        if (isEnableSyncContact()) {
            mView.requestReadContactsPermission();
        }
    }

    private void getFriendList() {
        Subscription subscription = mFriendRepository.getZaloFriendsCursor(isPhoneBook())
                .concatWith(retrieveZaloFriendsAsNeeded(isPhoneBook()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FriendListSubscriber(false));

        mSubscription.add(subscription);
    }

    private Observable<Cursor> retrieveZaloFriendsAsNeeded(boolean isTopup) {
        return mFriendRepository.shouldUpdateFriendList()
                .filter(Boolean::booleanValue)
                .flatMap(aBoolean -> mFriendRepository.fetchZaloFriendFullInfo())
                .flatMap(aBoolean -> mFriendRepository.getZaloFriendsCursor(isTopup))
                ;
    }

    void syncContact() {
        Subscription subscription = mFriendRepository.syncContact()
                .subscribeOn(Schedulers.io())
                .subscribe(new DefaultSubscriber<>());
        mSubscription.add(subscription);
    }

    void doSearch(String s) {
        Subscription subscription = mFriendRepository.findFriends(s, isPhoneBook())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FriendListSubscriber(true));

        mSubscription.add(subscription);
    }

    void clickItemContact(Fragment fragment, Cursor cursor) {
        ZPProfile profile = mFriendRepository.transform(cursor);
        if (profile == null) {
            Timber.d("click contact profile is null");
            return;
        }

        if (isPhoneBook()) {
            backTopup(fragment, profile);
        } else {
            startTransfer(fragment, profile);
        }
    }

    void clickItemContact(Fragment fragment, FavoriteData favoriteData) {
        if(favoriteData == null) {
            Timber.d("click contact favorite data is null");
            return;
        }

        ZPProfile profile = new ZPProfile();
        profile.avatar = favoriteData.avatar;
        profile.displayName = favoriteData.displayName;
        profile.phonenumber = favoriteData.phoneNumber;

        if (profile == null) {
            Timber.d("click contact profile is null");
            return;
        }

        if (isPhoneBook()) {
            backTopup(fragment, profile);
        } else {
            startTransfer(fragment, profile);
        }
    }

    private void backTopup(Fragment fragment, ZPProfile profile) {
        Activity activity = fragment.getActivity();
        Intent data = new Intent();
        data.putExtra("profile", profile);
        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }

    private void startTransfer(Fragment fragment, ZPProfile profile) {
        if (profile.status != 1) {
            Timber.d("user profile [status %s]", profile.status);
            showDialogNotUsingApp(profile);
            return;
        }

        TransferObject object = new TransferObject(profile);
        object.transferMode = Constants.TransferMode.TransferToZaloFriend;
        object.activateSource = Constants.ActivateSource.FromTransferActivity;
        mNavigator.startTransferActivity(fragment, object, Constants.REQUEST_CODE_TRANSFER);
    }

    private void showDialogNotUsingApp(ZPProfile zaloProfile) {
        if (mView != null) {
            String message = String.format(mContext.getString(R.string.account_not_use_zalopay), zaloProfile.displayName, zaloProfile.displayName);
            DialogHelper.showNotificationDialog((Activity) mView.getContext(),
                    message,
                    null);
        }
    }

    private class FriendListSubscriber extends DefaultSubscriber<Cursor> {
        boolean mIsSearch = false;

        FriendListSubscriber(boolean isSearch) {
            mIsSearch = isSearch;
        }

        @Override
        public void onNext(Cursor cursor) {

            if (cursor == null || cursor.isClosed()) {
                return;
            }

            if (mView == null) {
                return;
            }

            mView.swapCursor(cursor);
            mView.hideLoading();
            mView.setRefreshing(false);
            mView.checkIfEmpty();

            if (!mIsSearch) {
                mView.setSubTitle(String.format("(%s)", cursor.getCount()));
            }
        }

        @Override
        public void onError(Throwable e) {
            Timber.d(e, "Get friend zalo error");
            if (ResponseHelper.shouldIgnoreError(e)) {
                return;
            }

            if (mView == null) {
                return;
            }

            mView.showError(ErrorMessageFactory.create(mContext, e));
            mView.setRefreshing(false);
            mView.hideLoading();
        }
    }

    void favorite(boolean isFavorite, FavoriteData data) {
        Observable<Boolean> observable;
        if (isFavorite) {
            observable = mFriendRepository.addFavorite(data.phoneNumber, data.zaloId);
        } else {
            observable = mFriendRepository.removeFavorite(data.phoneNumber, data.zaloId);
        }

        Subscription subscription = observable
                .doOnError(Timber::d)
                .subscribeOn(Schedulers.io())
                .subscribe(new DefaultSubscriber<>());
        mSubscription.add(subscription);
    }

    private void getFavorite(int limitFavorite) {
        Subscription subscription = mFriendRepository.getFavorites(limitFavorite)
                .filter(data -> !Lists.isEmptyOrNull(data))
                .doOnError(Timber::d)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultSubscriber<List<FavoriteData>>() {
                    @Override
                    public void onNext(List<FavoriteData> data) {
                        if (mView != null) {
                            mView.setFavorite(data);
                        }
                    }
                });
        mSubscription.add(subscription);
    }
}
