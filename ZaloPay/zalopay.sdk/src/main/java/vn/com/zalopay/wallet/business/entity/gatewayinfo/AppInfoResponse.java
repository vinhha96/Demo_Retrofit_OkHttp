package vn.com.zalopay.wallet.business.entity.gatewayinfo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import vn.com.zalopay.wallet.business.entity.base.BaseResponse;

public class AppInfoResponse extends BaseResponse {

    @SerializedName("isupdateappinfo")
    public boolean isupdateappinfo;

    @SerializedName("appinfochecksum")
    public String appinfochecksum;

    @SerializedName("pmctranstypes")
    public List<MiniPmcTransTypeResponse> pmctranstypes;

    @SerializedName("info")
    public AppInfo info;

    @SerializedName("expiredtime")
    public long expiredtime;

    public boolean hasTranstypes() {
        return pmctranstypes != null && !pmctranstypes.isEmpty();
    }

    public boolean needUpdateAppInfo() {
        return returncode == 1 && isupdateappinfo && info != null;
    }
}
