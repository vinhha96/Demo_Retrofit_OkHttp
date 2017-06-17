package vn.com.zalopay.wallet.helper;

import android.content.Context;

import vn.com.vng.zalopay.network.NetworkConnectionException;
import vn.com.zalopay.wallet.R;
import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.data.Log;
import vn.com.zalopay.wallet.business.data.RS;
import vn.com.zalopay.wallet.business.entity.base.StatusResponse;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.AppInfo;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.MiniPmcTransType;
import vn.com.zalopay.wallet.constants.Constants;
import vn.com.zalopay.wallet.constants.OrderStatus;
import vn.com.zalopay.wallet.constants.PaymentStatus;
import vn.com.zalopay.wallet.constants.TransAuthenType;
import vn.com.zalopay.wallet.constants.TransactionType;
import vn.com.zalopay.wallet.exception.RequestException;
import vn.com.zalopay.wallet.paymentinfo.AbstractOrder;

import static vn.com.zalopay.wallet.constants.Constants.PAGE_FAIL;
import static vn.com.zalopay.wallet.constants.Constants.PAGE_FAIL_NETWORKING;
import static vn.com.zalopay.wallet.constants.Constants.PAGE_FAIL_PROCESSING;
import static vn.com.zalopay.wallet.constants.Constants.PAGE_SUCCESS;
import static vn.com.zalopay.wallet.constants.Constants.PAGE_SUCCESS_SPECIAL;

/**
 * Created by chucvv on 6/13/17.
 */

public class TransactionHelper {
    public static String getMessage(Throwable throwable) {
        String message = null;
        if (throwable instanceof RequestException) {
            RequestException requestException = (RequestException) throwable;
            message = requestException.getMessage();
            switch (requestException.code) {
                case RequestException.NULL:
                    message = GlobalData.getStringResource(RS.string.zingpaysdk_alert_network_error);
                    break;
            }
        } else if (throwable instanceof NetworkConnectionException) {
            message = GlobalData.getStringResource(RS.string.zingpaysdk_alert_network_error);
        }
        return message;
    }

    public static String getAppNameByTranstype(Context context, @TransactionType int transtype) {
        String appName = null;
        switch (transtype) {
            case TransactionType.MONEY_TRANSFER:
                appName = context.getString(R.string.sdk_tranfer_money_service);
                break;
            case TransactionType.WITHDRAW:
                appName = context.getString(R.string.sdk_withdraw_service);
                break;
            case TransactionType.TOPUP:
                appName = context.getString(R.string.sdk_topup_service);
                break;
        }
        return appName;
    }

    public static boolean needUserPasswordPayment(MiniPmcTransType pChannel, AbstractOrder pOrder) {
        Log.d("needUserPasswordPayment", "start check require for using password", pChannel);
        if (pChannel == null || pOrder == null) {
            return false;
        }
        int transAuthenType = TransAuthenType.PIN;
        if (pChannel.isNeedToCheckTransactionAmount() && pOrder.amount_total > pChannel.amountrequireotp) {
            transAuthenType = pChannel.overamounttype;
        } else if (pChannel.isNeedToCheckTransactionAmount() && pOrder.amount_total < pChannel.amountrequireotp) {
            transAuthenType = pChannel.inamounttype;
        }
        return transAuthenType == TransAuthenType.PIN || transAuthenType == TransAuthenType.BOTH;
    }

    /**
     * Check transaction status
     *
     * @param pStatusResponse data response
     */
    public static
    @OrderStatus
    int submitTransStatus(StatusResponse pStatusResponse) {
        if (pStatusResponse != null && pStatusResponse.returncode == Constants.PIN_WRONG_RETURN_CODE) {
            return OrderStatus.INVALID_PASSWORD;
        } else if (pStatusResponse != null && pStatusResponse.returncode < 0) {
            return OrderStatus.FAILURE;
        }
        //transaction is success
        else if (isTransactionSuccess(pStatusResponse)) {
            return OrderStatus.SUCCESS;
        }
        //order still need to continue processing
        else if (isOrderProcessing(pStatusResponse)) {
            return OrderStatus.PROCESSING;
        } else {
            return OrderStatus.FAILURE;
        }
    }

    public static boolean isOrderProcessing(StatusResponse pResponse) {
        return pResponse != null && pResponse.isprocessing;
    }

    public static boolean isTransactionSuccess(StatusResponse pResponse) {
        return pResponse != null && !pResponse.isprocessing && pResponse.returncode == 1;
    }

    public static String getPageName(@PaymentStatus int status) {
        switch (status) {
            case PaymentStatus.SUCCESS:
                return Constants.PAGE_SUCCESS;
            case PaymentStatus.ERROR_BALANCE:
                return Constants.PAGE_BALANCE_ERROR;
            case PaymentStatus.FAILURE:
                return Constants.PAGE_FAIL;
            case PaymentStatus.PROCESSING:
                return Constants.PAGE_FAIL_PROCESSING;
            default:
                return null;
        }
    }

    public static boolean isTransFail(String pPageName) {
        switch (pPageName) {
            case PAGE_FAIL:
            case PAGE_FAIL_NETWORKING:
            case PAGE_FAIL_PROCESSING:
                return true;
            default:
                return false;
        }
    }

    public static String getPageSuccessByAppType(AppInfo appInfo) {
        if (appInfo == null) {
            return PAGE_SUCCESS;
        }
        switch (appInfo.viewresulttype) {
            case 2:
                return PAGE_SUCCESS_SPECIAL;
            default:
                return PAGE_SUCCESS;
        }
    }

    public static boolean isTransNetworkError(String pMessage) {
        return pMessage.equalsIgnoreCase(GlobalData.getStringResource(RS.string.zpw_alert_networking_error_check_status))
                || pMessage.equalsIgnoreCase(GlobalData.getStringResource(RS.string.zpw_alert_order_not_submit))
                || pMessage.equalsIgnoreCase(GlobalData.getStringResource(RS.string.zingpaysdk_alert_network_error))
                || pMessage.equalsIgnoreCase(GlobalData.getStringResource(RS.string.zpw_alert_networking_off_in_transaction))
                || pMessage.equalsIgnoreCase(GlobalData.getStringResource(RS.string.sdk_alert_networking_off_in_link_account))
                || pMessage.equalsIgnoreCase(GlobalData.getStringResource(RS.string.sdk_alert_networking_off_in_unlink_account));
    }
}