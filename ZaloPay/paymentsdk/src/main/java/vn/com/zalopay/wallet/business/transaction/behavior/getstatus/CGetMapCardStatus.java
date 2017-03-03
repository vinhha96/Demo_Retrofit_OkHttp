package vn.com.zalopay.wallet.business.transaction.behavior.getstatus;

import vn.com.zalopay.wallet.business.channel.base.AdapterBase;
import vn.com.zalopay.wallet.business.transaction.behavior.interfaces.IGetTransactionStatus;
import vn.com.zalopay.wallet.datasource.request.BaseRequest;
import vn.com.zalopay.wallet.datasource.request.getstatus.GetStatus;

public class CGetMapCardStatus implements IGetTransactionStatus {
    @Override
    public void getStatus(AdapterBase pAdapter, String pZmpTransID, boolean pCheckData, String pMessage) {
        BaseRequest getStatusTask = new GetStatus(pAdapter, pZmpTransID, pCheckData, false, pMessage);
        getStatusTask.makeRequest();
    }
}
