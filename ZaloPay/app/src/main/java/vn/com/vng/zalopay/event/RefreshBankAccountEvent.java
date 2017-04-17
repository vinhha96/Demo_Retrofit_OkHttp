package vn.com.vng.zalopay.event;

import java.util.List;

import vn.com.zalopay.wallet.business.entity.gatewayinfo.DBankAccount;

/**
 * Created by longlv on 4/13/17.
 * Event to fresh list bank account after PaymentSDK reload bank account.
 * Ref: notify type 115/116
 */

public class RefreshBankAccountEvent {

    public List<DBankAccount> mLinkedBankAccount;
    public boolean mIsError;
    public String mMessageError;

    public RefreshBankAccountEvent(List<DBankAccount> mLinkedBankAccount) {
        this.mIsError = false;
        this.mLinkedBankAccount = mLinkedBankAccount;
    }

    public RefreshBankAccountEvent(String mMessageError) {
        this.mIsError = true;
        this.mMessageError = mMessageError;
    }
}