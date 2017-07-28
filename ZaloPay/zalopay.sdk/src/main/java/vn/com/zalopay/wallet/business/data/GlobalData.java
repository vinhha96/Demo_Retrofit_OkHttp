package vn.com.zalopay.wallet.business.data;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

import timber.log.Timber;
import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPEvents;
import vn.com.zalopay.analytics.ZPPaymentSteps;
import vn.com.zalopay.analytics.ZPScreens;
import vn.com.zalopay.wallet.R;
import vn.com.zalopay.wallet.business.entity.base.StatusResponse;
import vn.com.zalopay.wallet.business.entity.enumeration.ELinkAccType;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.PaymentChannel;
import vn.com.zalopay.wallet.business.entity.linkacc.LinkAccInfo;
import vn.com.zalopay.wallet.business.fingerprint.IPaymentFingerPrint;
import vn.com.zalopay.wallet.business.fingerprint.PaymentFingerPrint;
import vn.com.zalopay.wallet.constants.BankFunctionCode;
import vn.com.zalopay.wallet.constants.CardChannel;
import vn.com.zalopay.wallet.constants.CardType;
import vn.com.zalopay.wallet.constants.Link_Then_Pay;
import vn.com.zalopay.wallet.constants.TransactionType;
import vn.com.zalopay.wallet.controller.SDKApplication;
import vn.com.zalopay.wallet.controller.SDKPayment;
import vn.com.zalopay.wallet.feedback.FeedBackCollector;
import vn.com.zalopay.wallet.feedback.IFeedBack;
import vn.com.zalopay.wallet.helper.TransactionHelper;
import vn.com.zalopay.wallet.listener.ZPPaymentListener;
import vn.com.zalopay.wallet.paymentinfo.PaymentInfoHelper;
import vn.com.zalopay.wallet.repository.ResourceManager;
import vn.com.zalopay.wallet.tracker.ZPAnalyticsTrackerWrapper;
import vn.com.zalopay.wallet.ui.BaseActivity;

/***
 * static class contain static data.
 * need to dispose everything after quit sdk
 */
public class GlobalData {
    @CardChannel
    public static int cardChannelType = CardChannel.ATM;
    public static ZPAnalyticsTrackerWrapper analyticsTrackerWrapper;
    public static PaymentInfoHelper paymentInfoHelper;
    @BankFunctionCode
    private static int bankFunction = BankFunctionCode.PAY;
    private static WeakReference<Activity> mMerchantActivity = null;
    private static ZPPaymentListener mListener = null;

    public static String getUserId() {
        return paymentInfoHelper != null ? paymentInfoHelper.getUserId() : "";
    }

    public static
    @TransactionType
    int transtype() {
        return paymentInfoHelper != null ? paymentInfoHelper.getTranstype() : TransactionType.PAY;
    }

    public static boolean updatePaymentInfo(@Link_Then_Pay int bankLink) {
        if (paymentInfoHelper == null) {
            return false;
        }
        switch (bankLink) {
            case Link_Then_Pay.VCB:
                LinkAccInfo linkAccInfo = new LinkAccInfo(CardType.PVCB, ELinkAccType.LINK);
                paymentInfoHelper.setLinkAccountInfo(linkAccInfo);
                paymentInfoHelper.setTranstype(TransactionType.LINK);
                break;
            case Link_Then_Pay.BIDV:
                paymentInfoHelper.setTranstype(TransactionType.LINK);
                break;
            default:
                return false;
        }
        return true;
    }

    /***
     * prevent cross call pay
     */
    private static boolean isAccessRight() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        boolean isRightAccess = false;
        if (stackTraceElements.length >= 5) {
            if (stackTraceElements[4].getClassName().equals(SDKPayment.class.getName()) && stackTraceElements[4].getMethodName().equals("pay")) {
                isRightAccess = true;
            }
        }
        return isRightAccess;
    }

    public static boolean isUserInSDK() {
        return BaseActivity.getActivityCount() > 0;
    }

    @BankFunctionCode
    public static int updateBankFuncByPayType() {
        if (paymentInfoHelper.payByBankAccountMap()) {
            bankFunction = BankFunctionCode.PAY_BY_BANKACCOUNT_TOKEN;
        } else if (paymentInfoHelper.payByCardMap()) {
            bankFunction = BankFunctionCode.PAY_BY_CARD_TOKEN;
        } else {
            bankFunction = BankFunctionCode.PAY_BY_CARD;
        }
        return bankFunction;
    }

    public static void updateBankFuncByTranstype() {
        if (paymentInfoHelper.isBankAccountTrans()) {
            bankFunction = BankFunctionCode.LINK_BANK_ACCOUNT;
            return;
        }
        switch (paymentInfoHelper.getTranstype()) {
            case TransactionType.LINK:
                bankFunction = BankFunctionCode.LINK_CARD;
                break;
            case TransactionType.WITHDRAW:
                bankFunction = BankFunctionCode.WITHDRAW;
                break;
            case TransactionType.MONEY_TRANSFER:
            case TransactionType.TOPUP:
            case TransactionType.PAY:
                bankFunction = BankFunctionCode.PAY;
                break;
        }
    }

    public static void initializeAnalyticTracker(long pAppId, String pAppTransID, @TransactionType int transactionType, int orderSource) {
        analyticsTrackerWrapper = new ZPAnalyticsTrackerWrapper(pAppId, pAppTransID, transactionType, orderSource);
    }

    public static boolean shouldUpdateBankFuncbyPayType() {
        return bankFunction == BankFunctionCode.PAY ||
                bankFunction == BankFunctionCode.PAY_BY_CARD_TOKEN ||
                bankFunction == BankFunctionCode.PAY_BY_BANKACCOUNT_TOKEN ||
                bankFunction == BankFunctionCode.PAY_BY_CARD ||
                bankFunction == BankFunctionCode.PAY_BY_BANK_ACCOUNT;
    }

    @BankFunctionCode
    public static int updateBankFuncByPmc(PaymentChannel pChannel) {
        if (pChannel.isBankAccountMap()) {
            bankFunction = BankFunctionCode.PAY_BY_BANKACCOUNT_TOKEN;
        } else if (pChannel.isMapCardChannel()) {
            bankFunction = BankFunctionCode.PAY_BY_CARD_TOKEN;
        } else if (pChannel.isBankAccount()) {
            bankFunction = BankFunctionCode.PAY_BY_BANK_ACCOUNT;
        } else {
            bankFunction = BankFunctionCode.PAY_BY_CARD;
        }
        return bankFunction;
    }

    /***
     * alwaw call this to set static listener and info.
     */
    public static void setSDKData(Activity pActivity, ZPPaymentListener pPaymentListener) throws Exception {
        if (!isAccessRight()) {
            throw new Exception("Violate Design Pattern! Only 'pay' static method of ZingPayService class can set application!");
        }
        GlobalData.mMerchantActivity = new WeakReference<>(pActivity);
        GlobalData.mListener = pPaymentListener;
    }

    public static void setIFingerPrint(IPaymentFingerPrint pFingerPrintFromMerchant) {
        PaymentFingerPrint.shared().setPaymentFingerPrint(pFingerPrintFromMerchant);
    }

    public static void setFeedBack(IFeedBack pFeedBack) {
        FeedBackCollector.shared().setFeedBack(pFeedBack);
    }

    public static Context getAppContext() {
        return SDKApplication.getApplication();
    }

    public static Activity getMerchantActivity() {
        if (mMerchantActivity != null) {
            return mMerchantActivity.get();
        }
        return null;
    }

    public static ZPPaymentListener getPaymentListener() {
        return mListener;
    }

    public static int getTransProcessingMessage(@TransactionType int pTranstype) {
        return pTranstype == TransactionType.LINK ?
                R.string.sdk_fail_trans_status_link : R.string.sdk_fail_trans_status;
    }

    public static String getStringResource(String pResourceID) {
        try {
            // Try to get string from resource sent from before get from local
            String result = ResourceManager.getInstance(null).getString(pResourceID);
            return (result != null) ? result : getAppContext().getString(RS.getString(pResourceID));
        } catch (Exception e) {
            Log.e(GlobalData.class.getName(), e);
        }
        return null;
    }

    public static boolean shouldNativeWebFlow() {
        return GlobalData.getStringResource(RS.string.sdk_vcb_flow_type).equals("1");
    }

    @BankFunctionCode
    public static int getCurrentBankFunction() {
        return bankFunction;
    }

    public static void setCurrentBankFunction(@BankFunctionCode int pBankFunction) {
        bankFunction = pBankFunction;
    }

    public static void extraJobOnPaymentCompleted(StatusResponse pStatusResponse, String pBankCode) {
        if (pStatusResponse == null || paymentInfoHelper == null) {
            return;
        }
        try {
            boolean success = TransactionHelper.isTransactionSuccess(pStatusResponse);
            //notify to app to do some background task
            if (GlobalData.getPaymentListener() != null) {
                GlobalData.getPaymentListener().onPreComplete(success, pStatusResponse.zptransid, paymentInfoHelper.getAppTransId());
            }
            //track app trans id
            GlobalData.trackingTransactionEvent(success ? ZPPaymentSteps.OrderStepResult_Success : ZPPaymentSteps.OrderStepResult_Fail
                    , pStatusResponse
                    , pBankCode);
            //track screen name
            String screenName = paymentInfoHelper.isLinkTrans() ? ZPScreens.BANK_RESULT : ZPScreens.PAYMENT_RESULT;
            ZPAnalytics.trackScreen(screenName);
            trackEvantResult();

        } catch (Exception e) {
            Timber.w(e);
        }
    }

    private static void trackEvantResult() {
        if (paymentInfoHelper.isLinkTrans()) {
            ZPAnalytics.trackEvent(ZPEvents.LINKBANK_ADD_RESULT);
        } else if (paymentInfoHelper.isTopupTrans()) {
            ZPAnalytics.trackEvent(ZPEvents.BALANCE_ADDCASH_RESULT);
        } else if (paymentInfoHelper.isWithDrawTrans()) {
            ZPAnalytics.trackEvent(ZPEvents.BALANCE_WITHDRAW_RESULT);
        }

    }

    public static void trackingTransactionEvent(int pResult, StatusResponse pResponse, String bankCode) throws Exception {
        if (pResponse == null) {
            return;
        }
        int returnCode = pResponse.returncode;
        if (TextUtils.isEmpty(bankCode)) {
            bankCode = "";
        }
        Long transId;
        try {
            transId = Long.parseLong(pResponse.zptransid);
        } catch (Exception e) {
            transId = -1L;
        }
        if (GlobalData.analyticsTrackerWrapper != null) {
            GlobalData.analyticsTrackerWrapper
                    .step(ZPPaymentSteps.OrderStep_OrderResult)
                    .transId(transId)
                    .bankCode(bankCode)
                    .server_result(returnCode)
                    .step_result(pResult)
                    .track();
        }
    }
    //endregion
}
