package vn.com.vng.zalopay.ui.activity;

import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.ui.fragment.LinkCardFragment;

/**
 * Created by AnhHieu on 5/10/16.
 */
public class LinkCardActivity extends BaseToolBarActivity {

    @Override
    public BaseFragment getFragmentToHost() {
        return LinkCardFragment.newInstance();
    }
}
