package vn.com.vng.zalopay.bank.ui;

import android.os.Bundle;

import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;

/**
 * Created by datnt10 on 5/25/17.
 * Fragment bank: handle bank ui and events
 */

public class BankFragment extends BaseFragment {
    public static BankFragment newInstance() {
        Bundle args = new Bundle();
        BankFragment fragment = new BankFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_bank;
    }
}