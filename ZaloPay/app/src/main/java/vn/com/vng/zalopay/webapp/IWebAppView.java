package vn.com.vng.zalopay.webapp;

import android.app.Activity;
import android.support.v4.app.Fragment;

import vn.com.vng.zalopay.ui.view.ILoadDataView;

/**
 * Created by longlv on 2/9/17.
 * *
 */

interface IWebAppView extends ILoadDataView {

    Activity getActivity();
    Fragment getFragment();
}
