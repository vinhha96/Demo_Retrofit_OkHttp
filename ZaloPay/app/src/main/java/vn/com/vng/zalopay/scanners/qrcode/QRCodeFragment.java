package vn.com.vng.zalopay.scanners.qrcode;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;
import vn.com.vng.zalopay.Constants;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.monitors.MonitorEvents;
import vn.com.vng.zalopay.scanners.ui.FragmentLifecycle;
import vn.com.vng.zalopay.ui.view.IQRScanView;
import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPEvents;

/**
 * Created by AnhHieu on 6/7/16.
 * *
 */
public class QRCodeFragment extends AbsQrScanFragment implements IQRScanView, FragmentLifecycle {

    public static QRCodeFragment newInstance() {
        Bundle args = new Bundle();
        QRCodeFragment fragment = new QRCodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Inject
    QRCodePresenter qrCodePresenter;

    @BindView(R.id.tvErrorMessage)
    TextView mErrorMessageCamera;

    private boolean isExecutedOnce = false;

    @Override
    public int getResLayoutId() {
        return R.layout.fragment_qr_code;
    }

    @Override
    protected void handleResult(String result) {
        try {
            vibrate();
        } catch (Exception ex) {
            Timber.d(ex, "vibrate");
        }

        getAppComponent().monitorTiming().finishEvent(MonitorEvents.QR_SCANNING);
        ZPAnalytics.trackEvent(ZPEvents.SCANQR_GETCODE);
        qrCodePresenter.pay(result);
    }

    @Override
    public void onTokenInvalid() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        qrCodePresenter.setView(this);
    }

    @Override
    public void onDestroyView() {
        qrCodePresenter.destroyView();
        super.onDestroyView();
    }

    @Override
    public void showLoading() {
        super.showProgressDialog();
    }

    @Override
    public void hideLoading() {
        super.hideProgressDialog();
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
        hideLoading();
    }

    @Override
    public void resumeScanner() {
        startAndCheckPermission();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            hideLoading();
            startAndCheckPermissionOnce();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    private void startAndCheckPermissionOnce() {
        Timber.d("startAndCheckPermissionOnce: %s", isExecutedOnce);
        if (!isExecutedOnce) {
            isExecutedOnce = true;
            startAndCheckPermission();
        } else {
            if (isPermissionGranted(Manifest.permission.CAMERA)) {
                Timber.d("start without check permission");
                start();
            } else {
                showCameraError(R.string.exception_open_camera_not_allow);
            }
        }
    }

    private void startAndCheckPermission() {
        Timber.d("start with check permission");
        if (checkAndRequestPermission(Manifest.permission.CAMERA, Constants.Permission.REQUEST_CAMERA)) {
            super.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.Permission.REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    super.start();
                } else {
                    showCameraError(R.string.exception_open_camera_not_allow);
                    ZPAnalytics.trackEvent(ZPEvents.SCANQR_ACCESSDENIED);
                }
            }
        }
    }

    @Override
    public void onStartFragment() {
        resumeScanner();
    }

    @Override
    public void onStopFragment() {
        pause();
    }

    @Override
    protected void showCameraError(int message) {
        if (mErrorMessageCamera != null) {
            mErrorMessageCamera.setText(message);
        }
    }

    @Override
    public void previewStarted() {
        super.previewStarted();
        if (mErrorMessageCamera != null) {
            mErrorMessageCamera.setText(null);
        }
    }

    @Override
    public void previewSized() {
        super.previewSized();
        if (mErrorMessageCamera != null) {
            mErrorMessageCamera.setText(null);
        }
    }
}
