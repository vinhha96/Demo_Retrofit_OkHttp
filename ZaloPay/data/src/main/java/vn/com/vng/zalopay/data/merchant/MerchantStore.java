package vn.com.vng.zalopay.data.merchant;

import java.util.Collection;
import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import vn.com.vng.zalopay.data.api.response.GetMerchantUserInfoResponse;
import vn.com.vng.zalopay.data.api.response.ListMUIResponse;
import vn.com.vng.zalopay.data.cache.model.MerchantUser;
import vn.com.vng.zalopay.domain.model.MerchantUserInfo;

/**
 * Created by AnhHieu on 9/21/16.
 * *
 */
public interface MerchantStore {
    interface LocalStorage {
        void put(MerchantUser entity);

        void put(List<MerchantUser> entities);

        MerchantUser get(long appId);

        void removeAll();

        boolean existIn(Collection<Long> appIds);
    }

    interface RequestService {
        @GET("ummerchant/getmerchantuserinfo")
        Observable<GetMerchantUserInfoResponse> getmerchantuserinfo(@Query("appid") long appid,
                                                                    @Query("userid") String userid,
                                                                    @Query("accesstoken") String accesstoken);

        @GET("ummerchant/getlistmerchantuserinfo")
        Observable<ListMUIResponse> getlistmerchantuserinfo(@Query("appidlist") String appidlist,
                                                            @Query("userid") String userid,
                                                            @Query("accesstoken") String accesstoken);
    }


    interface Repository {
        Observable<MerchantUserInfo> getMerchantUserInfo(long appId);

        Observable<Boolean> getListMerchantUserInfo(String appIdList);

        Observable<Boolean> removeAll();
    }
}
