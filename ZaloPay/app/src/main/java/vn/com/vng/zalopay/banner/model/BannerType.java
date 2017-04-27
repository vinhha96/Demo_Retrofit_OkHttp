package vn.com.vng.zalopay.banner.model;

/**
 * Created by longlv on 29/08/2016.
 *
 */
public enum  BannerType {
    InternalFunction(1), ServiceWebView(2), PaymentApp(3), WebPromotion(4);
    int value;

    BannerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}