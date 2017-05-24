package vn.com.zalopay.wallet.business.entity.gatewayinfo;

import android.text.TextUtils;

public class PaymentChannel extends MiniPmcTransType {
    public String channel_icon;
    public String channel_next_icon;
    public String l4no;
    public String f6no;

    public PaymentChannel(MiniPmcTransType channel) {
        super(channel);
    }

    public PaymentChannel clone() {
        PaymentChannel paymentChannel = new PaymentChannel(this);
        paymentChannel.channel_icon = this.channel_icon;
        paymentChannel.channel_next_icon = this.channel_next_icon;
        paymentChannel.l4no = this.l4no;
        paymentChannel.f6no = this.f6no;
        return paymentChannel;
    }

    @Override
    public boolean isMapCardChannel() {
        return !TextUtils.isEmpty(this.f6no);
    }

    @Override
    public int hashCode() {
        return this.pmcid;
    }

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;
        if (object instanceof PaymentChannel) {
            PaymentChannel other = (PaymentChannel) object;

            if (!TextUtils.isEmpty(((PaymentChannel) object).pmcname)) {
                sameSame = this.pmcname.equals(other.pmcname);
            }
        }
        return sameSame;
    }

    public boolean compareToCardNumber(String pCardNumber) {
        if (TextUtils.isEmpty(pCardNumber) || pCardNumber.length() < 6) {
            return false;
        }
        String first6cardno = pCardNumber.substring(0, 6);
        String last4cardno = pCardNumber.substring(pCardNumber.length() - 4, pCardNumber.length());
        return !TextUtils.isEmpty(this.l4no) && !TextUtils.isEmpty(this.f6no) && this.f6no.equals(first6cardno) && this.l4no.equals(last4cardno);
    }
}
