package vn.com.zalopay.wallet.business.entity.gatewayinfo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;

import vn.com.zalopay.wallet.business.behavior.view.paymentfee.CBaseCalculateFee;
import vn.com.zalopay.wallet.business.behavior.view.paymentfee.CPaymentCalculateFee;
import vn.com.zalopay.wallet.business.data.Constants;
import vn.com.zalopay.wallet.business.data.GlobalData;
import vn.com.zalopay.wallet.business.data.RS;
import vn.com.zalopay.wallet.business.entity.enumeration.EFeeCalType;
import vn.com.zalopay.wallet.business.entity.enumeration.EPaymentChannelStatus;
import vn.com.zalopay.wallet.constants.TransAuthenType;
import vn.com.zalopay.wallet.utils.Log;
import vn.com.zalopay.wallet.utils.StringUtil;

public class MiniPmcTransType implements Parcelable {
    public static final Creator<MiniPmcTransType> CREATOR = new Creator<MiniPmcTransType>() {
        @Override
        public MiniPmcTransType createFromParcel(Parcel in) {
            return new MiniPmcTransType(in);
        }

        @Override
        public MiniPmcTransType[] newArray(int size) {
            return new MiniPmcTransType[size];
        }
    };
    public String bankcode;
    public int pmcid = 0;
    public String pmcname = null;
    public EPaymentChannelStatus status = EPaymentChannelStatus.DISABLE;
    public long minvalue = -1;
    public long maxvalue = -1;
    public double feerate = -1;
    public double minfee = -1;
    public EFeeCalType feecaltype = EFeeCalType.SUM;
    public double totalfee = 0;
    public long amountrequireotp = 0;
    @TransAuthenType
    public int inamounttype;
    @TransAuthenType
    public int overamounttype;
    public boolean isBankAccountMap = false;
    /***
     * Bank version support feature
     * user input card number or select bank channel which not support on older version
     * then need to show dialog into to user know about newer version
     */
    public String minappversion;
    /***
     * rule - still show channel not allow in channel list (status = 0) , each channel have 2 policy to allow or not
     * 1. user level - depend on user table map
     * 2. transaction amount not in range supported by channel
     * 3. withdraw need to check fee + amount <= balance
     */
    private boolean isAllowByAmount = true;
    private boolean isAllowByLevel = true;
    private boolean isAllowByAmountAndFee = true;

    public MiniPmcTransType() {
    }

    /***
     * copy constructor
     * @param channel
     */
    public MiniPmcTransType(MiniPmcTransType channel) {
        this.bankcode = channel.bankcode;
        this.pmcid = channel.pmcid;
        this.pmcname = channel.pmcname;
        this.status = channel.status;
        this.minvalue = channel.minvalue;
        this.maxvalue = channel.maxvalue;
        this.feecaltype = channel.feecaltype;
        this.minfee = channel.minfee;
        this.feerate = channel.feerate;
        this.totalfee = channel.totalfee;
        this.amountrequireotp = channel.amountrequireotp;
        this.isAllowByAmount = channel.isAllowByAmount;
        this.isAllowByLevel = channel.isAllowByLevel;
        this.isBankAccountMap = channel.isBankAccountMap;
        this.minappversion = channel.minappversion;
        this.inamounttype = channel.inamounttype;
        this.overamounttype = channel.overamounttype;
    }

    protected MiniPmcTransType(Parcel in) {
        bankcode = in.readString();
        pmcid = in.readInt();
        pmcname = in.readString();
        status = EPaymentChannelStatus.fromInt(in.readInt());
        minvalue = in.readLong();
        maxvalue = in.readLong();
        feerate = in.readDouble();
        minfee = in.readDouble();
        feecaltype = EFeeCalType.valueOf(in.readString());
        totalfee = in.readDouble();
        amountrequireotp = in.readLong();
        inamounttype = in.readInt();
        overamounttype = in.readInt();
        isBankAccountMap = in.readByte() != 0;
        minappversion = in.readString();
        isAllowByAmount = in.readByte() != 0;
        isAllowByLevel = in.readByte() != 0;
        isAllowByAmountAndFee = in.readByte() != 0;
    }

    public static String getPmcKey(long pAppId, String pTranstype, int pPmcId) {
        StringBuilder transtypePmcKey = new StringBuilder();
        transtypePmcKey.append(pAppId)
                .append(Constants.UNDERLINE)
                .append(pTranstype)
                .append(Constants.UNDERLINE)
                .append(pPmcId);
        return transtypePmcKey.toString();
    }

    public boolean isMapCardChannel() {
        return false;
    }

    /***
     * require otp depend on transaction amount
     * @return
     */
    public boolean isNeedToCheckTransactionAmount() {
        return amountrequireotp > 0 ? true : false;
    }

    /***
     * calculate fee
     */
    public void calculateFee() {
        this.totalfee = CBaseCalculateFee.getInstance().setCalculator(new CPaymentCalculateFee(this)).countFee();
    }

    public boolean hasFee() {
        return totalfee > 0;
    }

    public MiniPmcTransType fromJsonString(String pJson) {
        if (pJson == null)
            return new MiniPmcTransType();

        return (new Gson()).fromJson(pJson, this.getClass());
    }

    public boolean isEnable() {
        return status == EPaymentChannelStatus.ENABLE;
    }

    public void setStatus(EPaymentChannelStatus pStatus) {
        status = pStatus;
    }

    /***
     * whether transaction amount in range of this channel support
     *
     * @param pAmount
     * @return
     */
    public boolean isAmountSupport(long pAmount) {
        if (pAmount <= 0) {
            return false;
        }
        if (minvalue == -1 && maxvalue == -1) {
            return true;
        }
        if (minvalue == -1 && pAmount <= maxvalue) {
            return true;
        }
        if (maxvalue == -1 && pAmount >= minvalue) {
            return true;
        }
        if (pAmount >= minvalue && pAmount <= maxvalue) {
            return true;
        }

        return false;
    }

    public boolean compareToChannel(String pChannelId) {
        try {
            return this.pmcid == Integer.parseInt(pChannelId);
        } catch (Exception ex) {
            Log.e(this, ex);
        }
        return false;
    }

    public boolean isAtmChannel() {
        return compareToChannel(GlobalData.getStringResource(RS.string.zingpaysdk_conf_gwinfo_channel_atm));
    }

    public boolean isZaloPayChannel() {
        return compareToChannel(GlobalData.getStringResource(RS.string.zingpaysdk_conf_gwinfo_channel_zalopay));
    }

    public boolean isCreditCardChannel() {
        return compareToChannel(GlobalData.getStringResource(RS.string.zingpaysdk_conf_gwinfo_channel_credit_card));
    }

    public boolean isBankAccount() {
        return compareToChannel(GlobalData.getStringResource(RS.string.zingpaysdk_conf_gwinfo_channel_bankaccount));
    }

    public void checkPmcOrderAmount(long pOrderAmount) {
        setAllowByAmount(isAmountSupport((long) (pOrderAmount + totalfee)));
    }

    public String getErrorMessage() {
        String mess = null;
        if (!isAllowByAmount()) {
            mess = GlobalData.getStringResource(RS.string.zpw_string_channel_not_allow_by_amount);

            if ((GlobalData.getOrderAmount() + totalfee) < minvalue) {
                mess = GlobalData.getStringResource(RS.string.zpw_string_channel_not_allow_by_amount_small);
            }
        } else if (isMaintenance() && isMapCardChannel()) {
            mess = GlobalData.getStringResource(RS.string.zpw_string_bank_maintenance);
        } else if (isMaintenance()) {
            mess = GlobalData.getStringResource(RS.string.zpw_string_channel_maintenance);
        } else if (!isAllowByAmountAndFee()) {
            if (hasFee()) {
                mess = GlobalData.getStringResource(RS.string.zpw_string_fee_label)
                        + " " + StringUtil.formatVnCurrence(String.valueOf(totalfee))
                        + " " + GlobalData.getStringResource(RS.string.zpw_string_vnd);
            }

            mess += ". " + GlobalData.getStringResource(RS.string.zpw_string_channel_not_allow_by_fee);
        } else {
            mess = GlobalData.getStringResource(RS.string.zpw_string_channel_not_allow);
        }

        return mess;
    }

    public String getDefaultPmcFee(){
        return (isAtmChannel() && !isMapCardChannel()) ? GlobalData.getStringResource(RS.string.default_message_pmc_fee): GlobalData.getStringResource(RS.string.zpw_string_fee_free);
    }

    /***
     * status must be 0
     * @return
     */
    public boolean isAllowByAmount() {
        return isAllowByAmount;
    }

    protected void setAllowByAmount(boolean allowByAmount) {
        isAllowByAmount = allowByAmount;
    }

    /***
     * status must be 0
     * @return
     */
    public boolean isAllowByLevel() {
        return isAllowByLevel;
    }

    public boolean isAllowByAmountAndFee() {
        return isAllowByAmountAndFee;
    }

    public void setAllowByAmountAndFee(boolean allowByAmountAndFee) {
        isAllowByAmountAndFee = allowByAmountAndFee;
    }

    public boolean isMaintenance() {
        return status == EPaymentChannelStatus.MAINTENANCE;
    }

    public boolean isBankAccountMap() {
        return isBankAccountMap;
    }

    protected int getMinAppVersionSupport() {
        if (!TextUtils.isEmpty(minappversion)) {
            String clearMinAppVersion = minappversion.replace(".", "");
            return Integer.parseInt(clearMinAppVersion);
        }
        return 0;
    }

    public boolean isVersionSupport(String pAppVersion) {
        Log.d(this, "start check support channel version");
        if (TextUtils.isEmpty(pAppVersion)) {
            return true;
        }
        int minAppVersionSupport = getMinAppVersionSupport();
        if (minAppVersionSupport == 0) {
            return true;
        }
        pAppVersion = pAppVersion.replace(".", "");
        return Integer.parseInt(pAppVersion) >= minAppVersionSupport;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bankcode);
        parcel.writeInt(pmcid);
        parcel.writeString(pmcname);
        parcel.writeInt(status.getValue());
        parcel.writeLong(minvalue);
        parcel.writeLong(maxvalue);
        parcel.writeDouble(feerate);
        parcel.writeDouble(minfee);
        parcel.writeString(feecaltype.toString());
        parcel.writeDouble(totalfee);
        parcel.writeLong(amountrequireotp);
        parcel.writeInt(inamounttype);
        parcel.writeInt(overamounttype);
        parcel.writeByte((byte) (isBankAccountMap ? 1 : 0));
        parcel.writeString(minappversion);
        parcel.writeByte((byte) (isAllowByAmount ? 1 : 0));
        parcel.writeByte((byte) (isAllowByLevel ? 1 : 0));
        parcel.writeByte((byte) (isAllowByAmountAndFee ? 1 : 0));
    }
}
