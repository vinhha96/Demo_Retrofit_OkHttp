package vn.com.zalopay.wallet.event;

import vn.com.zalopay.wallet.entity.response.StatusResponse;

/**
 * Created by chucvv on 7/19/17.
 */

public class SdkSubmitOrderEvent {
    public StatusResponse response;

    public SdkSubmitOrderEvent(StatusResponse response) {
        this.response = response;
    }
}
