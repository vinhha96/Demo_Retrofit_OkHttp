package vn.com.vng.zalopay.transfer.ui;

import vn.com.vng.zalopay.transfer.ui.friendlist.ZaloFriendListFragment;
import vn.com.vng.zalopay.ui.activity.BaseToolBarActivity;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;

public class ZaloContactActivity extends BaseToolBarActivity {

    @Override
    public BaseFragment getFragmentToHost() {
        return ZaloFriendListFragment.newInstance();
    }

}
