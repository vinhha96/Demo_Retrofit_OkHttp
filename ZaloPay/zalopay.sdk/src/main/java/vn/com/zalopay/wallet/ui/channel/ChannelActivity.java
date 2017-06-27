package vn.com.zalopay.wallet.ui.channel;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import timber.log.Timber;
import vn.com.zalopay.utility.PermissionUtils;
import vn.com.zalopay.wallet.R;
import vn.com.zalopay.wallet.business.channel.base.AdapterBase;
import vn.com.zalopay.wallet.business.data.Log;
import vn.com.zalopay.wallet.constants.Constants;
import vn.com.zalopay.wallet.ui.BaseFragment;
import vn.com.zalopay.wallet.ui.ToolbarActivity;

/***
 * payment channel list screen.
 */
public class ChannelActivity extends ToolbarActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_toolbar;
    }

    @Override
    protected BaseFragment getFragmentToHost(Bundle bundle) {
        return ChannelFragment.newInstance(bundle);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (getActiveFragment() instanceof ChannelFragment) {
            ((ChannelFragment) getActiveFragment()).onUserInteraction();
        }
    }

    public void requestPermission(Context pContext) {
        if (PermissionUtils.isNeedToRequestPermissionAtRuntime() && !PermissionUtils.checkIfAlreadyhavePermission(pContext)) {
            PermissionUtils.requestForSpecificPermission(this, Constants.REQUEST_CODE_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    Timber.d("permission granted");
                } else {
                    //not granted
                    Timber.d("permission not granted");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public AdapterBase getAdapter() {
        android.support.v4.app.Fragment fragment = getActiveFragment();
        if (fragment instanceof ChannelFragment) {
            return ((ChannelFragment) fragment).sharePresenter().getAdapter();
        }
        return null;
    }
}
