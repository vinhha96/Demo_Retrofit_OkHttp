package vn.com.vng.zalopay.webapp;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import timber.log.Timber;
import vn.com.vng.zalopay.data.balance.BalanceStore;
import vn.com.vng.zalopay.data.transaction.TransactionStore;
import vn.com.vng.zalopay.data.util.NetworkHelper;
import vn.com.vng.zalopay.domain.model.Order;
import vn.com.vng.zalopay.domain.model.ZPTransaction;
import vn.com.vng.zalopay.domain.repository.ZaloPayRepository;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.react.error.PaymentError;
import vn.com.vng.zalopay.service.DefaultPaymentResponseListener;
import vn.com.vng.zalopay.service.PaymentWrapper;
import vn.com.vng.zalopay.service.PaymentWrapperBuilder;
import vn.com.vng.zalopay.ui.presenter.AbstractPresenter;
import vn.com.vng.zalopay.ui.view.ILoadDataView;
import vn.com.vng.zalopay.utils.PaymentHelper;
import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPEvents;
import vn.com.zalopay.wallet.business.entity.base.ZPPaymentResult;

/**
 * Created by longlv on 2/9/17.
 * *
 */

class WebAppPresenter extends AbstractPresenter<IWebAppView> {
    private PaymentWrapper paymentWrapper;
    private PaymentWrapper.IResponseListener mResponseListener;
    @Inject
    Navigator mNavigator;

    @Inject
    public WebAppPresenter(BalanceStore.Repository balanceRepository,
                           ZaloPayRepository zaloPayRepository,
                           TransactionStore.Repository transactionRepository) {
        paymentWrapper = new PaymentWrapperBuilder()
                .setBalanceRepository(balanceRepository)
                .setZaloPayRepository(zaloPayRepository)
                .setTransactionRepository(transactionRepository)
                .setResponseListener(new PaymentResponseListener())
                .setRedirectListener(new PaymentRedirectListener())
                .build();
    }


    public void pay(JSONObject data, PaymentWrapper.IResponseListener listener) {
        if (data == null) {
            Timber.i("Pay fail because json is null.");
            return;
        }
        mResponseListener = listener;
        Timber.d("start to process paying order: %s", data.toString());
        if (!NetworkHelper.isNetworkAvailable(mView.getContext())) {
            listener.onResponseError(PaymentError.ERR_CODE_INTERNET);
            return;
        }

        try {
            showLoadingView();
            if (zpTransaction(data)) {
                return;
            }

            orderTransaction(data);
        } catch (JSONException | IllegalArgumentException e) {
            Timber.i("Invalid JSON input: %s", e.getMessage());
        }
    }

    private boolean zpTransaction(JSONObject jsonObject) throws IllegalArgumentException {
        Timber.d("Trying with zptranstoken");
        ZPTransaction zpTransaction = new ZPTransaction(jsonObject);
        boolean isValidZPTransaction = zpTransaction.isValid();
        if (isValidZPTransaction) {
            paymentWrapper.payWithToken(mView.getActivity(), zpTransaction.appId, zpTransaction.transactionToken);
        }
        return isValidZPTransaction;
    }

    private boolean orderTransaction(JSONObject jsonOrder) throws JSONException, IllegalArgumentException {
        Order order = new Order(jsonOrder);
        boolean isValidOrder = PaymentHelper.validOrder(order);
        if (isValidOrder) {
            paymentWrapper.payWithOrder(mView.getActivity(), order);
            hideLoadingView();
        }
        return isValidOrder;
    }

    private void showLoadingView() {
        if (mView != null) {
            mView.showLoading();
        }
    }

    private void hideLoadingView() {
        if (mView != null) {
            mView.hideLoading();
        }
    }

    private class PaymentResponseListener extends DefaultPaymentResponseListener {

        @Override
        protected ILoadDataView getView() {
            return mView;
        }

        @Override
        public void onParameterError(String param) {
            super.onParameterError(param);
            if (mResponseListener != null) {
                mResponseListener.onParameterError(param);
            }

            if ("token".equalsIgnoreCase(param)) {
                ZPAnalytics.trackEvent(ZPEvents.SCANQR_NOORDER);
            }
        }

        @Override
        public void onResponseError(PaymentError paymentError) {
            if (mResponseListener != null) {
                mResponseListener.onResponseError(paymentError);
            }

            super.onResponseError(paymentError);
            hideLoadingView();
        }

        @Override
        public void onResponseSuccess(ZPPaymentResult zpPaymentResult) {
            if (mResponseListener != null) {
                mResponseListener.onResponseSuccess(zpPaymentResult);
            }
        }

        @Override
        public void onAppError(String msg) {
            if (mResponseListener != null) {
                mResponseListener.onAppError(msg);
            }
            super.onAppError(msg);
        }

        @Override
        public void onNotEnoughMoney() {
            if (mView != null && mView.getFragment() != null) {
                mNavigator.startDepositForResultActivity(mView.getFragment());
            }
        }
    }

    private class PaymentRedirectListener implements PaymentWrapper.IRedirectListener {
        @Override
        public void startUpdateProfileLevel(String walletTransId) {
            if (mView != null && mView.getFragment() != null) {
                Timber.d("startUpdateProfileLevel");
                mNavigator.startUpdateProfile2ForResult(mView.getFragment(), walletTransId);
            }
        }
    }

}
