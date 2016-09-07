package vn.com.zalopay.game.ui.webview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.parceler.Parcels;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import timber.log.Timber;
import vn.com.zalopay.game.R;
import vn.com.zalopay.game.businnesslogic.base.AppGameGlobal;
import vn.com.zalopay.game.businnesslogic.entity.pay.AppGamePayInfo;
import vn.com.zalopay.game.businnesslogic.interfaces.dialog.IDialogListener;
import vn.com.zalopay.game.businnesslogic.interfaces.dialog.ITimeoutLoadingListener;
import vn.com.zalopay.game.businnesslogic.interfaces.payment.IPaymentCallback;
import vn.com.zalopay.game.config.AppGameConfig;
import vn.com.zalopay.game.ui.component.activity.AppGameActivity;
import vn.com.zalopay.game.ui.component.activity.AppGameBaseActivity;

public class AppGameWebViewProcessor extends WebViewClient {

    private static final String JAVA_SCRIPT_INTERFACE_NAME = "zalopay_appgame";

    public static boolean hasError;

    //flag to animate activity
    public static boolean canPayment;

    private AppGameWebView mWebView = null;
    private Activity mActivity;
    private ITimeoutLoadingListener mTimeOutListener;

    public AppGameWebViewProcessor(AppGameWebView pWebView) {
        mWebView = pWebView;
        AppGameWebViewProcessor.hasError = false;
        AppGameWebViewProcessor.canPayment = false;
        mWebView.setWebViewClient(this);
        mWebView.addJavascriptInterface(this, JAVA_SCRIPT_INTERFACE_NAME);
    }

    public void start(String pUrl, Activity pActivity, ITimeoutLoadingListener pTimeoutListener) {
        mActivity = pActivity;
        mTimeOutListener = pTimeoutListener;
        if (AppGameGlobal.getDialog() != null)
            AppGameGlobal.getDialog().showLoadingDialog(pActivity, pTimeoutListener);

        AppGameWebViewProcessor.hasError = false;

        mWebView.loadUrl(pUrl);
    }

    private void changePage(String pUrl) {
        if (AppGameGlobal.getDialog() != null)
            AppGameGlobal.getDialog().showLoadingDialog(mActivity, mTimeOutListener);

        AppGameWebViewProcessor.hasError = false;

        mWebView.loadUrl(pUrl);
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        Timber.d("onPageFinished url [%s]", url);
        if (AppGameGlobal.getDialog() != null)
            AppGameGlobal.getDialog().hideLoadingDialog();

        super.onPageFinished(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Timber.e("Webview error %s", description);

        AppGameWebViewProcessor.hasError = true;

        if (AppGameGlobal.getDialog() != null) {
            AppGameGlobal.getDialog().showConfirmDialog(AppGameBaseActivity.getCurrentActivity(),
                    AppGameGlobal.getString(R.string.appgame_error_loading),
                    AppGameGlobal.getString(R.string.appgame_button_dialog_close),
                    AppGameGlobal.getString(R.string.appgame_button_dialog_retry),
                    new IDialogListener() {
                        @Override
                        public void onClose() {
                            AppGameBaseActivity.getCurrentActivity().finish();
                        }
                    },
                    new IDialogListener() {
                        @Override
                        public void onClose() {
                            if (mWebView == null) {
                                return;
                            }
                            if (AppGameGlobal.getDialog() != null) {
                                AppGameGlobal.getDialog().showLoadingDialog(mActivity, mTimeOutListener);
                            }
                            mWebView.reload();
                        }
                    });
        }

        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//        Timber.e("Webview error %s", error != null ? error.getDescription() : null);

        AppGameWebViewProcessor.hasError = true;

        if (AppGameGlobal.getDialog() != null)
            AppGameGlobal.getDialog().showInfoDialog(AppGameBaseActivity.getCurrentActivity(),
                    AppGameGlobal.getString(R.string.appgame_error_loading), AppGameGlobal.getString(R.string.appgame_button_dialog_close),
                    3, new IDialogListener() {
                        @Override
                        public void onClose() {
                            AppGameBaseActivity.getCurrentActivity().finish();
                        }
                    });


        super.onReceivedError(view, request, error);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Timber.d("===shouldOverrideUrlLoading===%s", url);

        //use case for url
        if (!TextUtils.isEmpty(url) && url.equalsIgnoreCase(AppGameConfig.URL_TO_APP) &&
                AppGameBaseActivity.getCurrentActivity() != null) {
            AppGameBaseActivity.getCurrentActivity().setResult(Activity.RESULT_CANCELED);
            AppGameBaseActivity.getCurrentActivity().finish();
        } else if (!TextUtils.isEmpty(url) && url.equalsIgnoreCase(AppGameConfig.URL_TO_LOGIN)
                && AppGameBaseActivity.getCurrentActivity() instanceof AppGameActivity) {
            ((AppGameActivity) AppGameBaseActivity.getCurrentActivity()).logout();
        } else if (url.startsWith("zalopay-1://post")) {
            payOrder(url);
        } else {
            view.loadUrl(url);
        }

        return true;
    }

    private void payOrder(final String url) {
        //Check param valid
        Uri data = Uri.parse(url);
        String muid = data.getQueryParameter("muid");
        String accesstoken = data.getQueryParameter("maccesstoken");
        String appid = data.getQueryParameter("appid");
        String apptransid = data.getQueryParameter("apptransid");
        String appuser = data.getQueryParameter("appuser");
        String apptime = data.getQueryParameter("apptime");
        String item = data.getQueryParameter("item");
        String description = data.getQueryParameter("description");
        String embeddata = data.getQueryParameter("embeddata");
        String amount = data.getQueryParameter("amount");
        String mac = data.getQueryParameter("mac");

        if (TextUtils.isEmpty(muid) ||
                TextUtils.isEmpty(apptransid) ||
                TextUtils.isEmpty(appuser) ||
                TextUtils.isEmpty(amount) ||
                TextUtils.isEmpty(mac)) {
            AppGameGlobal.getDialog().showInfoDialog(AppGameBaseActivity.getCurrentActivity(),
                    AppGameGlobal.getString(R.string.appgame_alert_input_error), AppGameGlobal.getString(R.string.appgame_button_dialog_close),
                    3, new IDialogListener() {
                        @Override
                        public void onClose() {
                            AppGameBaseActivity.getCurrentActivity().finish();
                        }
                    });

            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("muid", muid);
        bundle.putString("accesstoken", accesstoken);
        bundle.putString("appid", appid);
        bundle.putString("apptransid", apptransid);
        bundle.putString("appuser", appuser);
        bundle.putString("apptime", apptime);
        bundle.putString("item", item);
        //decode Base64
        //String decodeDescription = new String(Base64.decode(description.getBytes(), Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE));
        String decodeDescription = description;
        try {
            decodeDescription = URLDecoder.decode(description, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.w(e, "Url decode exception [%s]", e.getMessage());
        }
        bundle.putString("description", decodeDescription);
        bundle.putString("embeddata", embeddata);
        bundle.putString("amount", amount);
        bundle.putString("mac", mac);
        bundle.putParcelable("AppGamePayInfo", Parcels.wrap(AppGameGlobal.getAppGamePayInfo()));

        Timber.d("onResponseSuccess appId [%s]", AppGameGlobal.getAppGamePayInfo().getAppId());
        Timber.d("onResponseSuccess getApptransid [%s]", AppGameGlobal.getAppGamePayInfo().getApptransid());
        Timber.d("onResponseSuccess getUid [%s]", AppGameGlobal.getAppGamePayInfo().getUid());
        Timber.d("onResponseSuccess getAccessToken [%s]", AppGameGlobal.getAppGamePayInfo().getAccessToken());

        AppGameWebViewProcessor.canPayment = true;
        AppGameGlobal.getPaymentService().pay(AppGameBaseActivity.getCurrentActivity(), bundle, new IPaymentCallback() {
            @Override
            public void onResponseSuccess(AppGamePayInfo appGamePayInfo) {
                Timber.d("onResponseSuccess appGamePayInfo [%s]", appGamePayInfo);
                Timber.d("onResponseSuccess getAccessToken [%s]", appGamePayInfo.getAccessToken());
                Timber.d("onResponseSuccess getAppId [%s]", appGamePayInfo.getAppId());
                Timber.d("onResponseSuccess getApptransid [%s]", appGamePayInfo.getApptransid());
                Timber.d("onResponseSuccess getUid [%s]", appGamePayInfo.getUid());
                AppGameGlobal.getAppGamePayInfo().setApptransid(appGamePayInfo.getApptransid());

                final String urlPage = String.format(AppGameConfig.PAY_RESULT_PAGE, AppGameGlobal.getAppGamePayInfo().getApptransid(),
                        AppGameGlobal.getAppGamePayInfo().getUid(), AppGameGlobal.getAppGamePayInfo().getAccessToken());
                Timber.d("onResponseSuccess url [%s]", urlPage);
                changePage(urlPage);
            }
        });
    }

    public void onLoadResource(WebView view, String url) {
        Log.d("onLoadResource ", url);
    }

    @JavascriptInterface
    public void onJsCallBackResult(String pResult) {
        Timber.d("JsCallBackResult [%s]", pResult);
    }

    public void onDestroy() {
        mActivity = null;
        mTimeOutListener = null;
    }
}
