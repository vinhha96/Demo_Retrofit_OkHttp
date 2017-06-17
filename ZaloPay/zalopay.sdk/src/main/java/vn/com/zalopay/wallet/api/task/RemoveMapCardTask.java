package vn.com.zalopay.wallet.api.task;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import vn.com.zalopay.utility.ConnectionUtil;
import vn.com.zalopay.utility.GsonUtils;
import vn.com.zalopay.wallet.business.dao.SharedPreferencesManager;
import vn.com.zalopay.wallet.constants.Constants;
import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.data.Log;
import vn.com.zalopay.wallet.business.data.RS;
import vn.com.zalopay.wallet.business.entity.base.BaseResponse;
import vn.com.zalopay.wallet.business.entity.base.ZPWRemoveMapCardParams;
import vn.com.zalopay.wallet.business.entity.user.UserInfo;
import vn.com.zalopay.wallet.controller.SDKApplication;
import vn.com.zalopay.wallet.api.DataParameter;
import vn.com.zalopay.wallet.api.implement.RemoveMapCardImpl;
import vn.com.zalopay.wallet.listener.ZPWRemoveMapCardListener;

public class RemoveMapCardTask extends BaseTask<BaseResponse> {
    private ZPWRemoveMapCardParams mMapCardParams;
    private ZPWRemoveMapCardListener mListener;

    public RemoveMapCardTask(ZPWRemoveMapCardParams pMapCardParams, ZPWRemoveMapCardListener pListener) {
        super(null);
        mMapCardParams = pMapCardParams;
        mListener = pListener;
    }

    private void reloadMapCardList() {
        UserInfo userInfo = new UserInfo();
        userInfo.zalopay_userid = mMapCardParams.userID;
        userInfo.accesstoken = mMapCardParams.accessToken;

        SDKApplication.getApplicationComponent()
                .linkInteractor()
                .getCards(mMapCardParams.userID, mMapCardParams.accessToken, true, mMapCardParams.appVersion)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> callbackSuccessToMerchant(), new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        onRequestFail(throwable);
                        Log.d(this, throwable);
                    }
                });
    }

    private void callbackSuccessToMerchant() {
        if (mListener != null) {
            mListener.onSuccess(mMapCardParams.mapCard);
        }
    }

    @Override
    public void onDoTaskOnResponse(BaseResponse pResponse) {
        Log.d(this, "onDoTaskOnResponse do nothing");
    }

    @Override
    public void onRequestSuccess(BaseResponse pResponse) {
        if (!(pResponse instanceof BaseResponse)) {
            onRequestFail(null);
        } else if (pResponse.returncode >= 0) {
            try {
                SharedPreferencesManager.getInstance().removeMappedCard(mMapCardParams.userID + Constants.COMMA + mMapCardParams.mapCard.getKey());
                reloadMapCardList();//reload map card list to refresh checksum and map list on cache
            } catch (Exception e) {
                Log.e(this, e);
                callbackSuccessToMerchant();
            }
        } else if (mListener != null) {
            mListener.onError(pResponse);
        } else {
            Log.e(this, "mListener = NULL");
        }
        Log.d(this, "onRequestSuccess");
    }

    @Override
    public void onRequestFail(Throwable e) {
        if (ConnectionUtil.isOnline(GlobalData.getAppContext())) // has error network but device is online::need to reload map list
        {
            reloadMapCardList();
        } else if (mListener != null) {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.returncode = -1;
            baseResponse.returnmessage = getDefaulErrorNetwork();
            mListener.onError(baseResponse);
        } else {
            Log.e(this, "mListener = NULL");
        }
        Log.d(this, e);
    }

    @Override
    public void onRequestInProcess() {
        Log.d(this, "onRequestInProcess " + GsonUtils.toJsonString(mMapCardParams));
    }

    @Override
    public String getDefaulErrorNetwork() {
        return GlobalData.getStringResource(RS.string.zpw_alert_network_error_removemapcard);
    }

    @Override
    protected void doRequest() {
        shareDataRepository().setTask(this).postData(new RemoveMapCardImpl(), getDataParams());
        try {
            SharedPreferencesManager.getInstance().setCardInfoCheckSum(null);
        } catch (Exception e) {
            Log.e(this, e);
        }
    }

    @Override
    protected boolean doParams() {
        try {
            DataParameter.prepareRemoveCard(getDataParams(), mMapCardParams);
            return true;
        } catch (Exception e) {
            onRequestFail(e);
            Log.e(this, e);
            return false;
        }
    }
}