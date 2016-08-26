package vn.com.vng.zalopay.ui.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import timber.log.Timber;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.data.util.Utils;
import vn.com.vng.zalopay.domain.model.RecentTransaction;
import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPEvents;
import vn.com.vng.zalopay.domain.Constants;
import vn.com.vng.zalopay.domain.model.Order;
import vn.com.vng.zalopay.react.error.PaymentError;
import vn.com.vng.zalopay.service.PaymentWrapper;
import vn.com.vng.zalopay.ui.view.IQRScanView;
import vn.com.vng.zalopay.utils.ToastUtil;
import vn.com.zalopay.wallet.entity.base.ZPPaymentResult;

/**
 * Created by longlv on 09/05/2016.
 */

public final class QRCodePresenter extends BaseZaloPayPresenter implements IPresenter<IQRScanView> {

    private IQRScanView mView;

    private PaymentWrapper paymentWrapper;

    public QRCodePresenter() {
        paymentWrapper = new PaymentWrapper(balanceRepository, zaloPayRepository, transactionRepository, new PaymentWrapper.IViewListener() {
            @Override
            public Activity getActivity() {
                if (mView != null) {
                    return mView.getActivity();
                }
                return null;
            }
        }, new PaymentWrapper.IResponseListener() {
            @Override
            public void onParameterError(String param) {
                if (mView == null) {
                    return;
                }

                if ("order".equalsIgnoreCase(param)) {
                    mView.showError(mView.getContext().getString(R.string.order_invalid));
                } else if ("uid".equalsIgnoreCase(param)) {
                    mView.showError(mView.getContext().getString(R.string.user_invalid));
                } else if ("token".equalsIgnoreCase(param)) {
                    hideLoadingView();
                    mView.showError(mView.getContext().getString(R.string.order_invalid));
                    mView.resumeScanner();
                }
            }

            @Override
            public void onResponseError(PaymentError paymentError) {
                if (mView == null) {
                    return;
                }

                if (paymentError == PaymentError.ERR_CODE_INTERNET) {
                    mView.showError(applicationContext.getString(R.string.exception_no_connection_try_again));
                }
                hideLoadingView();
                mView.resumeScanner();
            }

            @Override
            public void onResponseSuccess(ZPPaymentResult zpPaymentResult) {
                if (mView != null && mView.getActivity() != null) {
                    mView.getActivity().finish();
                }
            }

            @Override
            public void onResponseTokenInvalid() {
                if (mView == null) {
                    return;
                }

                mView.onTokenInvalid();
                clearAndLogout();
            }

            @Override
            public void onAppError(String msg) {
                if (mView == null) {
                    return;
                }
                if (mView.getContext() != null) {
                    mView.showError(mView.getContext().getString(R.string.exception_generic));
                }
                hideLoadingView();
                mView.resumeScanner();
            }

            @Override
            public void onNotEnoughMoney() {
                if (mView == null) {
                    return;
                }

                navigator.startDepositActivity(applicationContext);
            }
        });
    }

    @Override
    public void setView(IQRScanView view) {
        this.mView = view;
    }

    @Override
    public void destroyView() {
        this.mView = null;
    }

    @Override
    public void resume() {
        hideLoadingView();
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        this.destroyView();
    }

    private void showLoadingView() {
        mView.showLoading();
    }

    private void hideLoadingView() {
        mView.hideLoading();
    }

//    private void showErrorView(String message) {
//        mView.showError(message);
//    }

    public void pay(String jsonString) {
        Timber.d("about to process payment with order: %s", jsonString);
        try {
            showLoadingView();

            if (transferMoneyViaQrCode(jsonString)) {
                return;
            }

            if (zpTransaction(jsonString)) {
                return;
            }

            if (orderTransaction(jsonString)) {
                return;
            }


            hideLoadingView();

            ZPAnalytics.trackEvent(ZPEvents.SCANQR_WRONGCODE);
            qrDataInvalid();

            mView.resumeScanner();
        } catch (JSONException | IllegalArgumentException e) {
            Timber.i("Invalid JSON input: %s", e.getMessage());
            hideLoadingView();

            ZPAnalytics.trackEvent(ZPEvents.SCANQR_WRONGCODE);
            qrDataInvalid();

            mView.resumeScanner();
        }
    }

    private boolean transferMoneyViaQrCode(String jsonData) throws JSONException {

        Timber.d("transferMoneyViaQrCode");

        JSONObject data = new JSONObject(jsonData);
        int type = data.optInt("type", -1);
        if (type <= 0) {
            return false;
        }
        long zaloId = data.optLong("uid", -1);
        if (zaloId <= 0) {
            return false;
        }
        String checksum = data.optString("checksum");
        if (TextUtils.isEmpty(checksum)) {
            return false;
        }


        String avatar = data.optString("avatar");
        String dName = data.optString("dname");

/*
        if (!checksum.equals(Utils.sha256(String.valueOf(type), String.valueOf(zaloId), avatar, dName))) {

            return false;
        }
*/

        RecentTransaction item = new RecentTransaction();
        item.avatar = avatar;
        item.userId = zaloId;
        item.displayName = dName;
        Bundle bundle = new Bundle();
        bundle.putParcelable(vn.com.vng.zalopay.Constants.ARG_TRANSFERRECENT, Parcels.wrap(item));
        navigator.startTransferActivity(mView.getContext(), bundle);

        hideLoadingView();
        return true;
    }

    private boolean zpTransaction(String jsonOrder) throws JSONException, IllegalArgumentException {
        Timber.d("trying to get transaction token from: %s", jsonOrder);
        JSONObject jsonObject = new JSONObject(jsonOrder);
        long appId = jsonObject.optInt(Constants.APPID);
        String transactionToken = jsonObject.optString(Constants.ZPTRANSTOKEN);
        if (appId < 0 || TextUtils.isEmpty(transactionToken)) {
            return false;
        }
        paymentWrapper.payWithToken(appId, transactionToken);
        return true;
    }

    private boolean orderTransaction(String jsonOrder) throws JSONException, IllegalArgumentException {
        Order order = new Order(jsonOrder);
        if (order.getAppid() < 0) {
            return false;
        }
        if (TextUtils.isEmpty(order.getApptransid())) {
            return false;
        }
        if (TextUtils.isEmpty(order.getAppuser())) {
            return false;
        }
        if (order.getApptime() <= 0) {
            return false;
        }
        if (TextUtils.isEmpty(order.getItem())) {
            return false;
        }
        if (order.getAmount() < 0) {
            return false;
        }
        if (TextUtils.isEmpty(order.getDescription())) {
            return false;
        }
        if (TextUtils.isEmpty(order.getMac())) {
            return false;
        }
        paymentWrapper.payWithOrder(order);
        hideLoadingView();
        return true;
    }

    private void qrDataInvalid() {
        ToastUtil.showToast(mView.getActivity(), "Dữ liệu không hợp lệ.");
    }

//    private void showOrderDetail(Order order) {
//        mView.showOrderDetail(order);
//    }

}
