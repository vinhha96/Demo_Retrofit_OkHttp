package vn.com.vng.zalopay.withdraw.ui.activities;

import android.support.annotation.NonNull;

import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.user.UserBaseToolBarActivity;
import vn.com.vng.zalopay.withdraw.ui.fragment.WithdrawConditionFragment;

public class WithdrawConditionActivity extends UserBaseToolBarActivity {

    @Override
    public BaseFragment getFragmentToHost() {
        return WithdrawConditionFragment.newInstance();
    }

    @NonNull
    @Override
    protected String getTrackingScreenName() {
        return "";
    }
}
