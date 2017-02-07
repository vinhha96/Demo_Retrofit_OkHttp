package vn.com.vng.zalopay.data.qrcode;

import android.text.TextUtils;

import com.google.gson.JsonObject;

import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by longlv on 2/6/17.
 * *
 */

public class QRCodeRepository implements QRCodeStore.Repository {

    private QRCodeStore.RequestService mRequestService;

    public QRCodeRepository(QRCodeStore.RequestService requestService) {
        mRequestService = requestService;
    }

    @Override
    public Observable<JsonObject> getPaymentInfo(@Url String url) {
        if (TextUtils.isEmpty(url)) {
            Observable.just(null);
        }
        return mRequestService.getPaymentInfo(url, "ZaloPayClient/2.8");
    }
}
