package vn.com.vng.zalopay.transfer;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.data.Constants;
import vn.com.vng.zalopay.data.cache.SqlZaloPayScope;
import vn.com.vng.zalopay.data.cache.model.ZaloFriendGD;
import vn.com.vng.zalopay.data.cache.model.ZaloFriendGDDao;
import vn.com.vng.zalopay.data.zfriend.FriendStore;
import vn.com.vng.zalopay.domain.model.ZaloFriend;
import vn.vng.uicomponent.widget.util.StringUtils;

/**
 * Created by huuhoa on 7/4/16.
 * Implementation for FriendStore.Repository
 */
public class FriendRepository implements FriendStoreRepository {
    private final int TIME_RELOAD = 5 * 60; //5'

    private FriendRequestService mRequestService;
    private FriendStore.LocalStorage mLocalStorage;
    private SqlZaloPayScope mSqlZaloPayScope;
    private Context mContext;

    public interface IZaloFriendListener {
        void onGetZaloFriendSuccess(List<ZaloFriend> zaloFriends);

        void onGetZaloFriendError();

        void onZaloFriendUpdated();

        void onGetZaloFriendFinish();
    }


    public FriendRepository(FriendRequestService requestService, FriendStore.LocalStorage localStorage, SqlZaloPayScope sqlZaloPayScope, Context context) {
        mRequestService = requestService;
        mLocalStorage = localStorage;
        mSqlZaloPayScope = sqlZaloPayScope;
        mContext = context;
    }

    private ZaloFriendGD convertZaloFriend(ZaloFriend zaloFriend) {
        if (zaloFriend == null) {
            return null;
        }
        String fullTextSearch = StringUtils.diacriticsInVietnameseLowerCase(zaloFriend.getDisplayName());
        return new ZaloFriendGD(zaloFriend.getUserId(), zaloFriend.getUserName(), zaloFriend.getDisplayName(), zaloFriend.getAvatar(), zaloFriend.getUserGender(), "", zaloFriend.isUsingApp(), fullTextSearch);
    }

    @Override
    public ZaloFriend getZaloFriendFrom(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        Long userId = cursor.getLong(cursor.getColumnIndex(ZaloFriendGDDao.Properties.Id.columnName));
        String userName = cursor.getString(cursor.getColumnIndex(ZaloFriendGDDao.Properties.UserName.columnName));
        String displayName = cursor.getString(cursor.getColumnIndex(ZaloFriendGDDao.Properties.DisplayName.columnName));
        String avatar = cursor.getString(cursor.getColumnIndex(ZaloFriendGDDao.Properties.Avatar.columnName));
        Integer userGender = cursor.getInt(cursor.getColumnIndex(ZaloFriendGDDao.Properties.UserGender.columnName));
        boolean usingApp = cursor.getInt(cursor.getColumnIndex(ZaloFriendGDDao.Properties.UsingApp.columnName)) == 1;

        ZaloFriend obj = new ZaloFriend(userId, userName, displayName, avatar, userGender, usingApp);
        return obj;
    }

    List<ZaloFriendGD> convertZaloFriends(List<ZaloFriend> zaloFriends) {
        List<ZaloFriendGD> result = new ArrayList<>();
        if (zaloFriends == null || zaloFriends.size() <= 0) {
            return result;
        }
        for (ZaloFriend zaloFriend : zaloFriends) {
            if (zaloFriend == null) {
                continue;
            }
            ZaloFriendGD zaloFriendTmp = convertZaloFriend(zaloFriend);
            result.add(zaloFriendTmp);
        }
        return result;
    }

    public void insertZaloFriends(List<ZaloFriend> zaloFriends) {
        List<ZaloFriendGD> zaloFriendList = convertZaloFriends(zaloFriends);
        mLocalStorage.writeZaloFriends(zaloFriendList);
    }

    @Override
    public void retrieveZaloFriendsAsNeeded(final IZaloFriendListener listener) {
        AndroidApplication.instance().getAppComponent().threadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                if (mSqlZaloPayScope != null && mLocalStorage.isHaveZaloFriendDb()) {
                    long lasttime = mSqlZaloPayScope.getDataManifest(Constants.MANIF_LASTTIME_UPDATE_ZALO_FRIEND, 0);
                    //check xem moi lay thi thoi
                    long currentTime = System.currentTimeMillis() / 1000;
                    if (currentTime - lasttime >= TIME_RELOAD) {
                        fetchListFromServer(listener);
                    } else {
                        if (listener != null) {
                            listener.onZaloFriendUpdated();
                        }
                    }
                } else {
                    fetchListFromServer(listener);
                }
            }
        });
    }

    @Override
    public void fetchListFromServer(final IZaloFriendListener listener) {
        mRequestService.getFriendListServer(mContext).subscribe(new Subscriber<List<ZaloFriend>>() {
            @Override
            public void onCompleted() {
                mSqlZaloPayScope.insertDataManifest(Constants.MANIF_LASTTIME_UPDATE_ZALO_FRIEND, String.valueOf(System.currentTimeMillis() / 1000));
                if (listener != null) {
                    listener.onGetZaloFriendFinish();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (listener != null) {
                    listener.onGetZaloFriendError();
                }
            }

            @Override
            public void onNext(List<ZaloFriend> zaloFriends) {
                insertZaloFriends(zaloFriends);
                if (listener != null) {
                    listener.onGetZaloFriendSuccess(zaloFriends);
                }
            }
        });
    }
}
