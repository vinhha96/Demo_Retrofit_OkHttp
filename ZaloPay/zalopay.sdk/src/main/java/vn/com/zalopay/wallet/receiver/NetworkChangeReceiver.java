package vn.com.zalopay.wallet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;
import vn.com.zalopay.utility.ConnectionUtil;
import vn.com.zalopay.wallet.controller.SDKApplication;
import vn.com.zalopay.wallet.event.SdkNetworkEvent;

import static vn.com.zalopay.wallet.constants.Constants.RECEIVER;

/***
 * receiver for networking is changing system event.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("networking is changing");
        SdkNetworkEvent networkEventMessage = new SdkNetworkEvent(RECEIVER, ConnectionUtil.isOnline(context));
        SDKApplication.getApplicationComponent().eventBus().post(networkEventMessage);
    }
}
