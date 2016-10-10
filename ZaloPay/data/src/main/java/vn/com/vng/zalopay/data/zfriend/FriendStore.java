package vn.com.vng.zalopay.data.zfriend;

import android.database.Cursor;

import org.json.JSONObject;

import java.util.List;

import de.greenrobot.dao.query.LazyList;
import rx.Observable;
import vn.com.vng.zalopay.data.api.entity.ZaloFriendEntity;
import vn.com.vng.zalopay.data.cache.SqlBaseScope;
import vn.com.vng.zalopay.data.cache.model.ZaloFriendGD;
import vn.com.vng.zalopay.domain.model.ZaloFriend;

/**
 * Created by huuhoa on 7/4/16.
 * Declaration for friend local storage, friend request service, friend repository
 */
public interface FriendStore {
    interface LocalStorage extends SqlBaseScope {

        boolean isHaveZaloFriendDb();

        void put(List<ZaloFriendEntity> val);

        void put(ZaloFriendEntity val);

        List<ZaloFriendEntity> get();

        Cursor zaloFriendList();

        Cursor searchZaloFriendList(String s);
    }

    interface RequestService {
        Observable<List<ZaloFriendEntity>> fetchFriendList();
    }

    interface APICallback {
        void onResult(JSONObject var1);
    }

    interface SDKApi {
        void getFriendList(int pageIndex, int totalCount, APICallback callback);
    }

    /**
     * Declaration for FriendStore.Repository
     */
    interface Repository {

        Observable<Boolean> retrieveZaloFriendsAsNeeded();

        Observable<List<ZaloFriend>> fetchZaloFriends();

        Observable<Cursor> zaloFriendList();

        Observable<Cursor> searchZaloFriend(String s);

        ZaloFriend transform(Cursor cursor);

        Observable<List<ZaloFriend>> getZaloFriendList();
    }
}
