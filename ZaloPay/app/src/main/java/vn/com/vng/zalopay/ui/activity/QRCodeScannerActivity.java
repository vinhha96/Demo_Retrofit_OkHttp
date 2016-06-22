package vn.com.vng.zalopay.ui.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.domain.model.Order;
import vn.com.vng.zalopay.internal.di.components.ApplicationComponent;
import vn.com.vng.zalopay.internal.di.components.UserComponent;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.qrcode.activity.AbsQRScanActivity;
import vn.com.vng.zalopay.ui.presenter.QRCodePresenter;
import vn.com.vng.zalopay.ui.view.IQRScanView;

/**
 * Created by AnhHieu on 4/21/16.
 */
public class QRCodeScannerActivity extends AbsQRScanActivity implements IQRScanView {

    private ProgressDialog mProgressDialog;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @Nullable
    @BindView(R.id.title_toolbar)
    TextView mTitleToolbar;

    @Inject
    Navigator navigator;

    @Inject
    QRCodePresenter qrCodePresenter;

    public int getResLayoutId() {
        return R.layout.activity_qr_scaner;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createUserComponent();
        setupActivityComponent();
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        customToolbar();

        qrCodePresenter.setView(this);
    }

    private void customToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getToolbar().getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mTitleToolbar != null) {
            mTitleToolbar.setText(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        if (mTitleToolbar != null) {
            mTitleToolbar.setText(titleId);
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodePresenter.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodePresenter.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qrCodePresenter.destroy();
        hideLoading();
        mProgressDialog = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void handleResult(String result) {
        Timber.tag(TAG).i("result:" + result);

        try {
            vibrate();
        } catch (Exception ex) {
        }

        qrCodePresenter.pay(result);
    }

    protected void setupActivityComponent() {
        AndroidApplication.instance().getUserComponent().inject(this);
    }

    @Override
    public void showOrderDetail(Order order) {

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onTokenInvalid() {
    }

    @Override
    public void showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.loading));
        }
        mProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    @Override
    public void showRetry() {

    }

    @Override
    public void hideRetry() {

    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        hideLoading();
    }

    @Override
    public Context getContext() {
        return this;
    }


    private void createUserComponent() {

        Timber.d(" user component %s", getUserComponent());

        if (getUserComponent() != null)
            return;

        UserConfig userConfig = getAppComponent().userConfig();
        Timber.d(" userConfig %s", userConfig.isSignIn());
        if (userConfig.isSignIn()) {
            userConfig.loadConfig();
            AndroidApplication.instance().createUserComponent(userConfig.getCurrentUser());
        }
    }

    public ApplicationComponent getAppComponent() {
        return AndroidApplication.instance().getAppComponent();
    }

    public UserComponent getUserComponent() {
        return AndroidApplication.instance().getUserComponent();
    }

}

