package vn.com.zalopay.wallet.ui;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.view.View;

/**
 * Created by chucvv on 6/13/17.
 */

public abstract class GenericFragment<P extends IPresenter> extends BaseFragment {

    protected P mPresenter;

    public boolean existPresenter() {
        return mPresenter != null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @CallSuper
    @Override
    protected void onViewBound(View view) {
        mPresenter = initializePresenter();
        if (mPresenter != null) {
            mPresenter.onAttach(this);
        }
    }

    @CallSuper
    @Override
    protected void onUnBound() {
        if (mPresenter != null) {
            mPresenter.onDetach();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPresenter != null) {
            mPresenter.onStart();
        }
    }

    @CallSuper
    @Override
    public void onStop() {
        super.onStop();
        if (mPresenter != null) {
            mPresenter.onStop();
        }
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onResume();
        }
    }

    protected abstract P initializePresenter();

    protected P getPresenter() {
        return mPresenter;
    }
}
