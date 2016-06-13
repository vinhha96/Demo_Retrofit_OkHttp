package vn.com.vng.zalopay.account.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import butterknife.OnClick;
import timber.log.Timber;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.ui.activity.BaseActivity;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.ui.presenter.LoginPresenter;
import vn.com.vng.zalopay.ui.view.ILoginView;


public class LoginZaloActivity extends BaseActivity implements ILoginView {


    @Override
    protected void setupActivityComponent() {
        getAppComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_login_zalo;
    }

    @Override
    public BaseFragment getFragmentToHost() {
        return null;
    }

    protected ProgressDialog mProgressDialog;

    @Inject
    LoginPresenter loginPresenter;

    @Inject
    Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginPresenter.setView(this);
    }

    @OnClick(R.id.layoutLoginZalo)
    public void onClickLogin(View v) {
        loginPresenter.loginZalo(this);
    }

   /* @Override
    public void onResume() {
        super.onResume();
        loginPresenter.pause();
    }

    @Override
    public void onPause() {
        super.onPause();
        loginPresenter.pause();
    }*/

    @Override
    public void onDestroy() {
        loginPresenter.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //   super.onBackPressed();
        finish();
        System.exit(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.tag(TAG).d("onActivityResult................" + requestCode + ";" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        loginPresenter.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void gotoMainActivity() {
        navigator.startHomeActivity(this, true);
        finish();
    }

    @Override
    public void gotoUpdateProfileLevel2() {
        navigator.startUpdateProfileLevel2Activity(this, true);
        finish();
    }

    @Override
    public void showLoading() {
        Timber.d("showLoading");
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getActivity(), getString(R.string.login), getString(R.string.loading));
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    public void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showRetry() {
    }

    @Override
    public void hideRetry() {
    }

    @Override
    public void showError(String message) {
        showToast(message);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
