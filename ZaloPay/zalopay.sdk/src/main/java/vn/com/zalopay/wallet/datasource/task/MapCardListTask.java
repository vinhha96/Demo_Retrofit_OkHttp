package vn.com.zalopay.wallet.datasource.task;

import vn.com.zalopay.wallet.business.channel.base.AdapterBase;
import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.data.RS;
import vn.com.zalopay.wallet.business.entity.base.CardInfoListResponse;
import vn.com.zalopay.wallet.business.entity.enumeration.EEventType;
import vn.com.zalopay.wallet.business.entity.user.UserInfo;
import vn.com.zalopay.wallet.datasource.DataParameter;
import vn.com.zalopay.wallet.datasource.implement.LoadMapCardListImpl;
import vn.com.zalopay.wallet.helper.MapCardHelper;
import vn.com.zalopay.wallet.listener.IGetMapCardInfo;
import vn.com.zalopay.wallet.business.data.Log;

/***
 * get map card list
 */
public class MapCardListTask extends BaseTask<CardInfoListResponse> {
    protected AdapterBase mAdapter;
    protected IGetMapCardInfo mGetCardInfoCallBack;
    protected UserInfo mUserInfo;

    public MapCardListTask(AdapterBase pAdapter, UserInfo pUserInfo) {
        super();
        this.mAdapter = pAdapter;
        this.mGetCardInfoCallBack = null;
        this.mUserInfo = pUserInfo;
    }

    public MapCardListTask(IGetMapCardInfo pGetCardInfoCallBack, UserInfo pUserInfo) {
        super();
        this.mAdapter = null;
        this.mGetCardInfoCallBack = pGetCardInfoCallBack;
        this.mUserInfo = pUserInfo;
    }

    @Override
    public void onDoTaskOnResponse(CardInfoListResponse pResponse) {
        Log.d(this, "do task after response");
        if (pResponse == null || pResponse.returncode != 1) {
            Log.d(this, "request not success...stopping saving response to cache");
            return;
        }
        if (MapCardHelper.needUpdateMapCardListOnCache(pResponse.cardinfochecksum)) {
            try {
                MapCardHelper.saveMapCardListToCache(mUserInfo.zaloPayUserId, pResponse.cardinfochecksum, pResponse.cardinfos);
            } catch (Exception e) {
                Log.e(this, e);
            }
        }
    }

    @Override
    public void onRequestSuccess(CardInfoListResponse pResponse) {
        if (mAdapter != null) {
            mAdapter.onEvent(EEventType.ON_GET_CARDINFO_LIST_COMPLETE, pResponse);
        }
        if (mGetCardInfoCallBack != null) {
            mGetCardInfoCallBack.onGetCardInfoComplete(pResponse);
        }
    }

    @Override
    public void onRequestFail(Throwable e) {
        CardInfoListResponse cardInfoListResponse = new CardInfoListResponse();
        cardInfoListResponse.returncode = -1;
        cardInfoListResponse.returnmessage = getDefaulErrorNetwork();
        if (mAdapter != null) {
            mAdapter.onEvent(EEventType.ON_GET_CARDINFO_LIST_COMPLETE, cardInfoListResponse);
        }
        if (mGetCardInfoCallBack != null) {
            mGetCardInfoCallBack.onGetCardInfoComplete(cardInfoListResponse);
        }
    }

    @Override
    public void onRequestInProcess() {
        Log.d(this, "onRequestInProcess");
    }

    @Override
    public String getDefaulErrorNetwork() {
        return GlobalData.getStringResource(RS.string.zpw_alert_network_error_loadmapcardlist);
    }

    @Override
    protected void doRequest() {
        try {
            newDataRepository().setTask(this).loadData(new LoadMapCardListImpl(), getDataParams());
        } catch (Exception ex) {
            onRequestFail(ex);
        }
    }

    @Override
    protected boolean doParams() {
        try {
            DataParameter.prepareGetCardInfoListParams(getDataParams(), mUserInfo);
            return true;
        } catch (Exception e) {
            onRequestFail(e);
            Log.e(this, e);
            return false;
        }
    }
}