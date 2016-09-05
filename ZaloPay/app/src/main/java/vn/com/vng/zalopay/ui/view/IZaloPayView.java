package vn.com.vng.zalopay.ui.view;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import vn.com.vng.zalopay.domain.model.AppResource;
import vn.com.zalopay.wallet.entity.gatewayinfo.DBanner;

/**
 * Created by AnhHieu on 5/9/16.
 */
public interface IZaloPayView {

    Context getContext();

    Activity getActivity();

    void showBannerAds(List<DBanner> banners);

    void setInsideApps(List<AppResource> list);

    void setTotalNotify(int total);

    void showNetworkError();

    void setBalance(long balance);

    void showError(String error);

    void onSessionExpired();

    void showLoading();

    void hideLoading();
}
