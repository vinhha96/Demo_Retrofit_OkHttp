package vn.com.vng.zalopay.domain.model;

/**
 * Created by AnhHieu on 3/25/16.
 */
public final class User extends Person {

    public static final int MIN_PROFILE_LEVEL = 2;

    public String accesstoken;

    public long expirein;

    public String email;

    public String identityNumber;

    public int profilelevel;

    public String profilePermissions;

    public User(String uid) {
        super(uid);
    }

    @Override
    public String toString() {
        return "{" +
                "zaloPayId: " +
                this.zaloPayId +
                ", " +
                "accesstoken: " +
                this.accesstoken +
                "}";
    }
}
