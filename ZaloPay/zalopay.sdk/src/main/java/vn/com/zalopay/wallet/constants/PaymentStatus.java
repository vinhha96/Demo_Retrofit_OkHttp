package vn.com.zalopay.wallet.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({PaymentStatus.ZPC_TRANXSTATUS_PROCESSING, PaymentStatus.ZPC_TRANXSTATUS_SUCCESS, PaymentStatus.ZPC_TRANXSTATUS_FAIL,
        PaymentStatus.ZPC_TRANXSTATUS_MONEY_NOT_ENOUGH, PaymentStatus.ZPC_TRANXSTATUS_TOKEN_INVALID, PaymentStatus.ZPC_TRANXSTATUS_INPUT_INVALID,
        PaymentStatus.ZPC_TRANXSTATUS_CLOSE, PaymentStatus.ZPC_TRANXSTATUS_LOCK_USER, PaymentStatus.ZPC_TRANXSTATUS_UPGRADE,
        PaymentStatus.ZPC_TRANXSTATUS_NO_INTERNET, PaymentStatus.ZPC_TRANXSTATUS_SERVICE_MAINTENANCE,
        PaymentStatus.ZPC_UPVERSION, PaymentStatus.ZPC_TRANXSTATUS_NEED_LINKCARD, PaymentStatus.ZPC_TRANXSTATUS_NEED_LINKCARD_BEFORE_PAYMENT,
        PaymentStatus.ZPC_TRANXSTATUS_NEED_LINK_ACCOUNT, PaymentStatus.ZPC_TRANXSTATUS_NEED_LINK_ACCOUNT_BEFORE_PAYMENT,
        PaymentStatus.ZPC_TRANXSTATUS_UPLEVEL_AND_LINK_BANKACCOUNT_CONTINUE_PAYMENT, PaymentStatus.ZPC_TRANXSTATUS_UPGRADE_CMND_EMAIL})
@Retention(RetentionPolicy.SOURCE)
public @interface PaymentStatus {
    int ZPC_TRANXSTATUS_PROCESSING = 0; //order is processing
    int ZPC_TRANXSTATUS_SUCCESS = 1;// transaction is success
    int ZPC_TRANXSTATUS_FAIL = -1; // transaction is fail
    int ZPC_TRANXSTATUS_MONEY_NOT_ENOUGH = -2; // user's wallet not enough money for payment, app redirect user to cash in
    int ZPC_TRANXSTATUS_TOKEN_INVALID = -3; // expire token, maybe user login on many devices, app force user to logout
    int ZPC_TRANXSTATUS_INPUT_INVALID = -4; // order info is invalid
    int ZPC_TRANXSTATUS_CLOSE = -5; // user close transaction
    int ZPC_TRANXSTATUS_LOCK_USER = -6;// zalopay account is locked
    int ZPC_TRANXSTATUS_UPGRADE = 6; // user need to up level, app force user to update numberphone + payment password
    int ZPC_TRANXSTATUS_NO_INTERNET = 8;// device is offline
    int ZPC_TRANXSTATUS_SERVICE_MAINTENANCE = 9;// server is maintenance
    int ZPC_UPVERSION = 10;// there're a newer version on store, app show dialog info and redirect user to newer version
    int ZPC_TRANXSTATUS_NEED_LINKCARD = 11;// app need to redirect user to link card
    int ZPC_TRANXSTATUS_NEED_LINKCARD_BEFORE_PAYMENT = 12;//user using BIDV for payment but hasn't link card yet, app redirect user to link card then auto redirect user to sdk again with previous order info
    int ZPC_TRANXSTATUS_NEED_LINK_ACCOUNT = 13;// app need to redirect user to link account
    int ZPC_TRANXSTATUS_NEED_LINK_ACCOUNT_BEFORE_PAYMENT = 14;//user using Vietcombank for payment but hasn't link account yet, app redirect user to link account then auto redirect user to sdk again with previous order info
    int ZPC_TRANXSTATUS_UPLEVEL_AND_LINK_BANKACCOUNT_CONTINUE_PAYMENT = 15;// user level 1 input Vietcombank card or select bank account channel, app need to redirect user to update level then link bank account and then redirect user to sdk again with previous order info
    int ZPC_TRANXSTATUS_UPGRADE_CMND_EMAIL = 16;// user need to update cmnd and email
}