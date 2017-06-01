package vn.com.zalopay.wallet.tracker;

import java.util.Date;

import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPApptransidLog;
import vn.com.zalopay.analytics.ZPPaymentSteps;
import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.data.Log;
import vn.com.zalopay.wallet.business.objectmanager.SingletonBase;
import vn.com.zalopay.wallet.constants.TransactionType;


/**
 * Created by lytm on 04/05/2017.
 */

public class ZPAnalyticsTrackerWrapper extends SingletonBase {
    ZPApptransidLog mZPApptransidLog;

    public ZPAnalyticsTrackerWrapper(String pAppTransID, @TransactionType int transactionType) {
        super();
        initialize(GlobalData.appID, pAppTransID, transactionType);
    }

    protected void initialize(long pAppId, String pAppTransId, int pTransType) {
        mZPApptransidLog = new ZPApptransidLog();
        mZPApptransidLog.appid = pAppId;
        mZPApptransidLog.apptransid = pAppTransId;
        mZPApptransidLog.transtype = pTransType;
    }

    public void trackUserCancel() {
        if (mZPApptransidLog.status == 1) {
            Log.d(this, "skip tracking because status is finish");
            return;
        }
        mZPApptransidLog.step_result = ZPPaymentSteps.OrderStepResult_UserCancel;
        ZPAnalytics.trackApptransidEvent(mZPApptransidLog);
        Log.d(this, "tracking translogid ", mZPApptransidLog);
    }

    /***
     * The params order mush be
     * @param step
     * @param step_result
     * @param pcmid
     * @param transid
     * @param server_result
     * @param status
     * @param bank_code
     */
    public void track(Object... params) {
        if (params == null || params.length <= 0) {
            Log.d(this, "skip tracking because params is empty");
            return;
        }
        if (mZPApptransidLog.status == 1) {
            Log.d(this, "skip tracking because status is finish");
            return;
        }
        for (int i = 0; i < params.length; i++) {
            Object object = params[i];
            switch (i) {
                case 0:
                    mZPApptransidLog.step = (int) object;
                    mZPApptransidLog.finish_time = new Date().getTime();
                case 1:
                    mZPApptransidLog.step_result = (int) object;
                    break;
                case 2:
                    mZPApptransidLog.pcmid = (int) object;
                    break;
                case 3:
                    try {
                        mZPApptransidLog.transid = Long.parseLong(String.valueOf(object));
                    } catch (Exception e) {

                    }
                    break;
                case 4:
                    mZPApptransidLog.server_result = (int) object;
                    break;
                case 5:
                    mZPApptransidLog.status = (int) object;
                    break;
                case 6:
                    mZPApptransidLog.bank_code = (String) object;
                    break;
                default:
                    Log.d(this, "invalid params", object);
            }
        }
        ZPAnalytics.trackApptransidEvent(mZPApptransidLog);
        Log.d(this, "tracking translogid ", mZPApptransidLog);
    }
}
