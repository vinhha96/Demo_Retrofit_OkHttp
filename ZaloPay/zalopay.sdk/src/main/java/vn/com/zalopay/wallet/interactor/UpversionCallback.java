package vn.com.zalopay.wallet.interactor;

/**
 * Created by chucvv on 6/8/17.
 */

public class UpversionCallback extends PlatformInfoCallback {
    public boolean forceupdate;
    public String newestappversion;
    public String forceupdatemessage;

    public UpversionCallback(boolean forceupdate, String newestappversion, String forceupdatemessage, long expiretime) {
        super(expiretime);
        this.forceupdate = forceupdate;
        this.newestappversion = newestappversion;
        this.forceupdatemessage = forceupdatemessage;
    }
}
