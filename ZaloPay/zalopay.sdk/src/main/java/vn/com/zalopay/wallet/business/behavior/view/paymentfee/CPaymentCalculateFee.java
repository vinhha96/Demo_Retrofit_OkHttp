package vn.com.zalopay.wallet.business.behavior.view.paymentfee;

import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.data.Log;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.MiniPmcTransType;
import vn.com.zalopay.wallet.constants.FeeType;

/***
 * payment fee
 */
public class CPaymentCalculateFee implements ICalculateFee {
    private MiniPmcTransType mChannel;

    public CPaymentCalculateFee(MiniPmcTransType pChannel) {
        this.mChannel = pChannel;
    }

    @Override
    public double calculateFee() {
        if (mChannel == null) {
            return 0;
        }
        double orderFee = 0;
        if (mChannel.feerate > 0) {
            try {
                orderFee = mChannel.feerate * GlobalData.getPaymentInfo().amount;
            } catch (Exception e) {
                Log.e(this, e);
            }
        }
        if (mChannel.minfee > 0) {
            switch (mChannel.feecaltype) {
                case FeeType.MAX:
                    orderFee = (orderFee > mChannel.minfee) ? orderFee : mChannel.minfee;
                    break;
                case FeeType.SUM:
                    orderFee += mChannel.minfee;
                    break;
            }
        }
        return orderFee;
    }
}
