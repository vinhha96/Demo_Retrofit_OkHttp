package vn.com.zalopay.wallet.repository.bank;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import vn.com.vng.zalopay.network.API_NAME;
import vn.com.zalopay.analytics.ZPEvents;
import vn.com.zalopay.wallet.constants.Constants;
import vn.com.zalopay.wallet.business.entity.atm.BankConfig;
import vn.com.zalopay.wallet.business.entity.atm.BankConfigResponse;
import vn.com.zalopay.wallet.repository.AbstractLocalStorage;

/**
 * Created by chucvv on 6/7/17.
 */

public class BankStore {
    public interface LocalStorage extends AbstractLocalStorage.LocalStorage{
        void put(BankConfigResponse bankConfigResponse);

        long getExpireTime();

        void setExpireTime(long expireTime);

        Observable<BankConfigResponse> get();

        String getCheckSum();

        Map<String, String> getBankPrefix();

        String getBankCodeList();

        BankConfig getBankConfig(String bankCode);

        void clearCheckSum();

        void clearConfig();
    }

    public interface BankListService {
        @GET(Constants.URL_GET_BANKLIST)
        @API_NAME(https = ZPEvents.API_V001_TPE_GETBANKLIST, connector = ZPEvents.CONNECTOR_V001_TPE_GETBANKLIST)
        Observable<BankConfigResponse> fetch(@Query("platform") String platform, @Query("checksum") String checksum, @Query("appversion") String appversion);
    }
}
