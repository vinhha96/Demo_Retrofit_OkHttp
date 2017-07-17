package vn.com.zalopay.wallet.api.task;

import timber.log.Timber;
import vn.com.zalopay.analytics.ZPEvents;
import vn.com.zalopay.wallet.R;
import vn.com.zalopay.wallet.api.DataParameter;
import vn.com.zalopay.wallet.api.implement.AuthenPayerImpl;
import vn.com.zalopay.wallet.business.channel.base.AdapterBase;
import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.data.Log;
import vn.com.zalopay.wallet.business.entity.base.StatusResponse;
import vn.com.zalopay.wallet.business.entity.enumeration.EEventType;
import vn.com.zalopay.wallet.business.entity.user.UserInfo;
import vn.com.zalopay.wallet.tracker.ZPAnalyticsTrackerWrapper;

public class AuthenPayerTask extends BaseTask<StatusResponse> {
    private AdapterBase mAdapter;
    private String mTransID, mAuthenType, mAuthenValue;
    private long startTime = 0;

    public AuthenPayerTask(AdapterBase pAdapter, String pTransID, String pAuthenType, String pAuthenValue) {
        super(pAdapter.getPaymentInfoHelper().getUserInfo());
        mAdapter = pAdapter;
        mTransID = pTransID;
        mAuthenType = pAuthenType;
        mAuthenValue = pAuthenValue;
    }

    @Override
    public void onDoTaskOnResponse(StatusResponse pResponse) {

    }

    @Override
    public void onRequestSuccess(StatusResponse pResponse) {
        ZPAnalyticsTrackerWrapper.trackApiCall(ZPEvents.CONNECTOR_V001_TPE_ATMAUTHENPAYER, startTime, pResponse);
        if (mAdapter != null) {
            mAdapter.onEvent(EEventType.ON_ATM_AUTHEN_PAYER_COMPLETE, pResponse);
        }
    }

    @Override
    public void onRequestFail(Throwable e) {
        if (mAdapter != null) {
            StatusResponse statusResponse = new StatusResponse();
            statusResponse.returncode = -1;
            statusResponse.returnmessage = getDefaulErrorNetwork();
            mAdapter.onEvent(EEventType.ON_ATM_AUTHEN_PAYER_COMPLETE, statusResponse);
        }
    }

    @Override
    public void onRequestInProcess() {
        if (mAdapter != null) {
            try {
                mAdapter.getView().showLoading(GlobalData.getAppContext().getResources().getString(R.string.sdk_trans_authen_atm_mess));
            } catch (Exception e) {
                Log.e(this, e);
            }
        }
        Timber.d("onRequestInProcess");
    }

    @Override
    public String getDefaulErrorNetwork() {
        return GlobalData.getAppContext().getResources().getString(R.string.sdk_error_networking_authenpayer_mess);
    }

    @Override
    protected void doRequest() {
        if (mAdapter.openSettingNetworking()) {
            startTime = System.currentTimeMillis();
            shareDataRepository().setTask(this).postData(new AuthenPayerImpl(), getDataParams());
        }
    }

    @Override
    protected boolean doParams() {
        try {
            UserInfo userInfo = mAdapter.getPaymentInfoHelper().getUserInfo();
            DataParameter.prepareAtmAuthenPayer(getDataParams(), userInfo.zalopay_userid, userInfo.accesstoken, mTransID, mAuthenType, mAuthenValue);
            return true;
        } catch (Exception e) {
            Log.e(this, e);
            onRequestFail(e);
            return false;
        }
    }
}
