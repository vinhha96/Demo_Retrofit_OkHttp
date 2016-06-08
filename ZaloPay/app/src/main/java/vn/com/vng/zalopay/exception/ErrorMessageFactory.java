
package vn.com.vng.zalopay.exception;

import android.content.Context;

import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.data.exception.BodyException;
import vn.com.vng.zalopay.data.exception.NetworkConnectionException;
import vn.com.vng.zalopay.data.exception.VideoNotFoundException;

public class ErrorMessageFactory {

    private ErrorMessageFactory() {
        //empty
    }

    public static String create(Context context, Throwable exception) {
        if (context == null) {
            return null;
        }
        String message = context.getString(R.string.exception_generic);

        if (exception instanceof NetworkConnectionException) {
            message = context.getString(R.string.exception_no_connection);
        } else if (exception instanceof VideoNotFoundException) {
            message = context.getString(R.string.exception_video_not_found);
        } else if (exception instanceof BodyException) {
            message = context.getString(R.string.exception_generic);
            int errorCode = ((BodyException) exception).errorCode;
            switch (errorCode) {
               
            }
        }

        return message;
    }


    public static final int EXCEPTION = 0;
    public static final int ZK_NODE_EXIST_EXCEPTION = -1;
    public static final int APPID_INVALID = -2;
    public static final int APP_NOT_AVAILABLE = -3;
    public static final int APP_TIME_INVALID = -4;
    public static final int AMOUNT_INVALID = -5;
    public static final int PLATFORM_INVALID = -6;
    public static final int PLATFORM_NOT_AVAILABLE = -7;
    public static final int DSCREEN_TYPE_INVALID = -8;
    public static final int PMCID_INVALID = -9;
    public static final int PMC_INACTIVE = -10;
    public static final int APPTRANSID_EXIST = -70;
    public static final int DUPLICATE_ZPTRANSID = -69;
    public static final int GET_TRANSID_FAIL = -13;
    public static final int SET_CACHE_FAIL = -14;
    public static final int GET_CACHE_FAIL = -15;
    public static final int UPDATE_RESULT_FAIL = -16;
    public static final int EXCEED_MAX_NOTIFY = -17;
    public static final int DEVICEID_NOT_MATCH = -18;
    public static final int APPID_NOT_MATCH = -19;
    public static final int PLATFORM_NOT_MATCH = -20;
    public static final int PMC_FACTORY_NOT_FOUND = -21;
    public static final int ZALO_LOGIN_FAIL = -71;
    public static final int ZALO_LOGIN_EXPIRE = -72;
    public static final int TOKEN_INVALID = -73;
    public static final int CARDINFO_INVALID = -74;
    public static final int CARDINFO_EXIST = -75;
    public static final int SDK_INVALID = -26;
    public static final int CARDINFO_NOT_FOUND = -76;
    public static final int UM_TOKEN_NOT_FOUND = -77;
    public static final int ATM_CREATE_ORDER_DBG_FAIL = -29;
    public static final int UM_TOKEN_EXPIRE = -78;
    public static final int REQUEST_FORMAT_INVALID = -79;
    public static final int CARD_INVALID = -31;
    public static final int APP_INACTIVE = -32;
    public static final int APP_MAINTENANCE = -33;
    public static final int PMC_MAINTENANCE = -34;
    public static final int PMC_NOT_AVAILABLE = -35;
    public static final int OVER_LIMIT = -36;
    public static final int DUPLICATE = 2;
    public static final int CREATE_ORDER_SUCCESSFUL = 3;
    public static final int IN_NOTIFY_QUEUE = 4;
    public static final int PROCESSING = 5;
    public static final int TRANS_NOT_FINISH = -80;
    public static final int ATM_WAIT_FOR_CHARGE = 9;
    public static final int INIT = 10;
    public static final int USER_NOT_MATCH = -81;
    public static final int NOT_FOUND_SMS_SERVICE_PHONE = -39;
    public static final int MAX_RETRY_GET_DBG_STATUS = -40;
    public static final int ATM_CREATE_ORDER_FAIL = -41;
    public static final int ATM_BANK_INVALID = -42;
    public static final int ATM_BANK_MAINTENANCE = -43;
    public static final int DUPLICATE_APPTRANSID = -68;
    public static final int ATM_VERIFY_CARD_SUCCESSFUL = 12;
    public static final int ATM_VERIFY_OTP_SUCCESS = 13;
    public static final int ATM_VERIFY_CARD_FAIL = -44;
    public static final int ATM_MAX_RETRY_OTP_FAIL = -45;
    public static final int ATM_QUERY_ORDER_FAIL = -46;
    public static final int ATM_BANK_SRC_INVALID = -47;
    public static final int DESERIALIZE_TRANS_FAIL = -67;
    public static final int IN_GET_STATUS_ATM_QUEUE = 15;
    public static final int ATM_CHARGE_FAIL = -48;
    public static final int ATM_RETRY_CAPTCHA = 16;
    public static final int ATM_RETRY_OTP = 17;
    public static final int ATM_CHARGE_SUCCESSFUL = 18;
    public static final int TRANS_INFO_NOT_FOUND = -49;
    public static final int ATM_CAPTCHA_INVALID = -50;
    public static final int ATM_COST_RATE_INVALID = -51;
    public static final int ITEMS_INVALID = -52;
    public static final int HMAC_INVALID = -53;
    public static final int TIME_INVALID = -54;
    public static final int CAL_NET_CHARGE_AMT_FAIL = -55;
    public static final int ATM_VERIFY_OTP_FAIL = -56;
    public static final int APP_USER_INVALID = -57;
    public static final int ZPW_GETTRANSID_FAIL = -58;
    public static final int ZPW_PURCHASE_FAIL = -59;
    public static final int ZPW_ACCOUNT_NAME_INVALID = -60;
    public static final int ZPW_ACCOUNT_SUSPENDED = -61;
    public static final int ZPW_ACCOUNT_NOT_EXIST = -62;
    public static final int ZPW_BALANCE_NOT_ENOUGH = -63;
    public static final int ZPW_GET_BALANCE_FAIL = -64;
    public static final int ZPW_WRONG_PASSWORD = -65;
    public static final int USER_INVALID = -66;
    public static final int CARD_NOT_MATCH = -82;
    public static final int TRANSID_FORMAT_INVALID = -83;
    public static final int CARD_TOKEN_INVALID = -84;
    public static final int CARD_TOKEN_EXPIRE = -85;
    public static final int TRANSTYPE_INVALID = -86;
    public static final int TRANSTYPE_INACTIVE = -87;
    public static final int TRANSTYPE_MAINTENANCE = -88;
    public static final int APPTRANSID_GEN_ERROR = -93;
    public static final int MAP_APPID_APPTRANSID_FAIL = -89;
    public static final int EXCEED_MAX_NOTIFY_WALLET_FEE = -90;
    public static final int UPDATE_RESULT_FAIL_WALLET_FEE = -91;
    public static final int APPTRANSID_INVALID = -92;
    public static final int TRANSTYPE_AMOUNT_INVALID = -94;
    public static final int CARD_ALREADY_MAP = -95;
}
