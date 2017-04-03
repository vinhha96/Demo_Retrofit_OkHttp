package vn.com.vng.zalopay.react.listener;

import com.facebook.react.bridge.Promise;
import com.zalopay.ui.widget.dialog.listener.ZPWOnEventDialogListener;

import vn.com.vng.zalopay.react.Helpers;

/**
 * Created by longlv on 16/09/2016.
 * Simple dialog event handler that will resolve to index value when user clicks OK
 */
public class DialogSimpleEventListener implements ZPWOnEventDialogListener {

    private Promise mPromise;
    private int mIndex;

    public DialogSimpleEventListener(Promise promise, int index) {
        mPromise = promise;
        mIndex = index;
    }

    @Override
    public void onOKevent() {
        if (mPromise == null) {
            return;
        }
        Helpers.promiseResolve(mPromise, mIndex);
    }
}
