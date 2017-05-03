package vn.com.vng.zalopay.searchcategory;

import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.ui.activity.BaseToolBarActivity;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;

/**
 * Created by khattn on 3/10/17.
 * Search Category Activity
 */

public class SearchCategoryActivity extends BaseToolBarActivity {
    @Override
    public BaseFragment getFragmentToHost() {
        return SearchCategoryFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_common_searchbox;
    }
}