package vn.com.zalopay.wallet.entity.gatewayinfo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import vn.com.zalopay.wallet.BuildConfig;
import vn.com.zalopay.wallet.constants.Constants;
import vn.com.zalopay.wallet.constants.FeeType;
import vn.com.zalopay.wallet.constants.PaymentChannelStatus;
import vn.com.zalopay.wallet.constants.TransAuthenType;
import vn.com.zalopay.wallet.constants.TransactionType;

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
    @SerializedName("bankcode")
    public String bankcode;

    @SerializedName("pmcid")
    public int pmcid = 0;

    @SerializedName("pmcname")
    public String pmcname = null;

    @SerializedName("status")
    @PaymentChannelStatus
    public int status = PaymentChannelStatus.DISABLE;

    @SerializedName("minvalue")
    public long minvalue = -1;

    @SerializedName("maxvalue")
    public long maxvalue = -1;

    @SerializedName("feerate")
    public double feerate = -1;

    @SerializedName("minfee")
    public double minfee = -1;

    @SerializedName("feecaltype")
    @FeeType
    public String feecaltype = FeeType.SUM;

    @SerializedName("totalfee")
    public double totalfee = 0;

    @SerializedName("amountrequireotp")
    public long amountrequireotp = 0;

    @SerializedName("inamounttype")
    @TransAuthenType
    public int inamounttype;

    @SerializedName("overamounttype")
    @TransAuthenType
    public int overamounttype;

    @SerializedName("isBankAccountMap")
    public boolean isBankAccountMap = false;
    /*
     * Bank version support feature
     * user input card number or select bank channel which not support on older version
     * then need to show dialog into to user know about newer version
     */
    @SerializedName("minappversion")
    private String minappversion;
    /*
     * rule - still show channel not allow in channel list (status = 0) , each channel have 2 policy to allow or not
     * 1. user level - depend on user table map
     * 2. transaction amount not in range supported by channel
     * 3. withdraw need to check fee + amount <= balance
     */
    @Expose
    private boolean allowPmcQuota = true;
    @Expose
    private boolean allowLevel = true;
    @Expose
    private boolean allowOrderAmount = true;
    @Expose
    private boolean allowBankVersion = true;

    public MiniPmcTransType() {
    }

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
        this.allowPmcQuota = channel.allowPmcQuota;
        this.allowLevel = channel.allowLevel;
        this.isBankAccountMap = channel.isBankAccountMap;
        this.minappversion = channel.minappversion;
        this.inamounttype = channel.inamounttype;
        this.overamounttype = channel.overamounttype;
    }

    protected MiniPmcTransType(Parcel in) {
        bankcode = in.readString();
        pmcid = in.readInt();
        pmcname = in.readString();
        status = in.readInt();
        minvalue = in.readLong();
        maxvalue = in.readLong();
        feerate = in.readDouble();
        minfee = in.readDouble();
        feecaltype = in.readString();
        totalfee = in.readDouble();
        amountrequireotp = in.readLong();
        inamounttype = in.readInt();
        overamounttype = in.readInt();
        isBankAccountMap = in.readByte() != 0;
        minappversion = in.readString();
        allowPmcQuota = in.readByte() != 0;
        allowLevel = in.readByte() != 0;
        allowOrderAmount = in.readByte() != 0;
    }

    public static String getPmcKey(long pAppId, @TransactionType int pTranstype, int pPmcId) {
        StringBuilder transtypePmcKey = new StringBuilder();
        transtypePmcKey.append(pAppId)
                .append(Constants.UNDERLINE)
                .append(pTranstype)
                .append(Constants.UNDERLINE)
                .append(pPmcId);
        return transtypePmcKey.toString();
    }

    public boolean isAllowBankVersion() {
        return allowBankVersion;
    }

    public void setAllowBankVersion(boolean allowBankVersion) {
        this.allowBankVersion = allowBankVersion;
    }

    public void resetToDefault() {
        this.status = PaymentChannelStatus.ENABLE;
        this.minvalue = -1;
        this.maxvalue = -1;
        this.feerate = 0;
        this.minfee = 0;
    }

    public boolean isMapCardChannel() {
        return false;
    }

    /*
     * require otp depend on transaction amount
     */
    public boolean isNeedToCheckTransactionAmount() {
        return amountrequireotp > 0;
    }

    public void calculateFee(long amount) {
        this.totalfee = countFee(amount);
    }

    private double countFee(long amount) {
        double orderFee = 0;
        if (feerate > 0) {
            orderFee = feerate * amount;
        }
        if (minfee <= 0) {
            return orderFee;
        }
        switch (feecaltype) {
            case FeeType.MAX:
                orderFee = (orderFee > minfee) ? orderFee : minfee;
                break;
            case FeeType.SUM:
                orderFee += minfee;
                break;
        }
        return orderFee;
    }

    public boolean hasFee() {
        return totalfee > 0;
    }

    public boolean isEnable() {
        return status == PaymentChannelStatus.ENABLE;
    }

    public boolean meetPaymentCondition() {
        return isEnable() && isAllowPmcQuota() && !isMaintenance() && isAllowOrderAmount() && isAllowBankVersion();
    }

    public boolean isDisable() {
        return status == PaymentChannelStatus.DISABLE;
    }

    public void setStatus(@PaymentChannelStatus int pStatus) {
        status = pStatus;
    }

    /*
     * whether transaction amount in range of this channel support
     */
    private boolean isAmountSupport(long pAmount) {
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

    private boolean compareToChannel(int pChannelId) {
        return this.pmcid == pChannelId;
    }

    public boolean isAtmChannel() {
        return compareToChannel(BuildConfig.channel_atm);
    }

    public boolean isZaloPayChannel() {
        return compareToChannel(BuildConfig.channel_zalopay);
    }

    public boolean isLinkChannel() {
        return compareToChannel(Constants.DEFAULT_LINK_ID);
    }

    public boolean isBankAccount() {
        return compareToChannel(BuildConfig.channel_bankaccount);
    }

    public void checkPmcOrderAmount(long pOrderAmount) {
        setAllowPmcQuota(isAmountSupport((long) (pOrderAmount + totalfee)));
    }

    public boolean isAllowPmcQuota() {
        return allowPmcQuota;
    }

    private void setAllowPmcQuota(boolean allowPmcQuota) {
        this.allowPmcQuota = allowPmcQuota;
    }

    public boolean isAllowOrderAmount() {
        return allowOrderAmount;
    }

    public void setAllowOrderAmount(boolean allowOrderAmount) {
        this.allowOrderAmount = allowOrderAmount;
    }

    public boolean isMaintenance() {
        return status == PaymentChannelStatus.MAINTENANCE;
    }

    public boolean isBankAccountMap() {
        return isBankAccountMap;
    }

    private int getMinAppVersionSupport() {
        if (!TextUtils.isEmpty(minappversion)) {
            String clearMinAppVersion = minappversion.replace(".", "");
            return Integer.parseInt(clearMinAppVersion);
        }
        return 0;
    }

    public boolean isVersionSupport(String pAppVersion) {
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
        parcel.writeInt(status);
        parcel.writeLong(minvalue);
        parcel.writeLong(maxvalue);
        parcel.writeDouble(feerate);
        parcel.writeDouble(minfee);
        parcel.writeString(feecaltype);
        parcel.writeDouble(totalfee);
        parcel.writeLong(amountrequireotp);
        parcel.writeInt(inamounttype);
        parcel.writeInt(overamounttype);
        parcel.writeByte((byte) (isBankAccountMap ? 1 : 0));
        parcel.writeString(minappversion);
        parcel.writeByte((byte) (allowPmcQuota ? 1 : 0));
        parcel.writeByte((byte) (allowLevel ? 1 : 0));
        parcel.writeByte((byte) (allowOrderAmount ? 1 : 0));
    }
}