package vn.com.vng.zalopay.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.analytics.ZPAnalytics;
import vn.com.vng.zalopay.analytics.ZPEvents;
import vn.com.vng.zalopay.balancetopup.ui.activity.BalanceTopupActivity;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.data.eventbus.ServerMaintainEvent;
import vn.com.vng.zalopay.data.eventbus.TokenExpiredEvent;
import vn.com.vng.zalopay.internal.di.components.ApplicationComponent;
import vn.com.vng.zalopay.internal.di.components.UserComponent;
import vn.com.vng.zalopay.transfer.ui.activities.TransferHomeActivity;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.utils.ToastUtil;


/**
 * Created by AnhHieu on 3/24/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected void setupActivityComponent() {
    }

    public abstract BaseFragment getFragmentToHost();

    protected final String TAG = getClass().getSimpleName();

    private Unbinder unbinder;

    final EventBus eventBus = AndroidApplication.instance().getAppComponent().eventBus();

    protected final ZPAnalytics zpAnalytics = AndroidApplication.instance().getAppComponent().zpAnalytics();

    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG);
        createUserComponent();
        setupActivityComponent();
        setContentView(getResLayoutId());

        if (savedInstanceState == null) {
            hostFragment(getFragmentToHost());
        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.logActionLaunch();
    }

    protected int getResLayoutId() {
        return R.layout.activity_common;
    }

    protected void hostFragment(BaseFragment fragment, int id) {
        if (fragment != null && getFragmentManager().findFragmentByTag(fragment.getTag()) == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(id, fragment, fragment.TAG);
            ft.commit();
        }
    }

    protected void hostFragment(BaseFragment fragment) {
        hostFragment(fragment, R.id.fragment_container);
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

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        zpAnalytics.trackScreen(TAG);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        Timber.d("%s onDestroy", TAG);
    }


    @Override
    public void onBackPressed() {
        BaseFragment activeFragment = getActiveFragment();
        if (activeFragment == null || !activeFragment.onBackPressed()) {
            super.onBackPressed();
            this.logActionNavigationBack();
        }
    }


    protected BaseFragment getActiveFragment() {
        return (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
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

    public void showToast(String message) {
        ToastUtil.showToast(this, message);
    }

    public void showToast(int message) {
        ToastUtil.showToast(this, message);
    }

    public ApplicationComponent getAppComponent() {
        return AndroidApplication.instance().getAppComponent();
    }

    public UserComponent getUserComponent() {
        return AndroidApplication.instance().getUserComponent();
    }

    public boolean checkAndRequestPermission(String permission, int requestCode) {
        boolean hasPermission = true;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                requestPermissions(new String[]{permission}, requestCode);
            }
        }
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onTokenExpired(TokenExpiredEvent event) {
        Timber.i("SESSION EXPIRED in Screen %s", TAG);
        getAppComponent().applicationSession().clearUserSession();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenExpiredMain(TokenExpiredEvent event) {
        showToast(R.string.exception_token_expired_message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMaintain(ServerMaintainEvent event) {
        Timber.i("Receive server maintain event");
        getAppComponent().applicationSession().setMessageAtLogin("Hệ thống đang bảo trì. Vui lòng thử lại sau.");
        getAppComponent().applicationSession().clearUserSession();
    }


    private void logActionLaunch() {
//        if (TAG.equals(QRCodeScannerActivity.class.getSimpleName())) {
//            zpAnalytics.trackEvent(SCANQR_LAUNCH);
//        } else
        if (TAG.equals(LinkCardActivity.class.getSimpleName())) {
            zpAnalytics.trackEvent(ZPEvents.MANAGECARD_LAUNCH);
        } else if (TAG.equals(BalanceTopupActivity.class.getSimpleName())) {
            zpAnalytics.trackEvent(ZPEvents.ADDCASH_LAUNCH);
        } else if (TAG.equals(TransferHomeActivity.class.getSimpleName())) {
            zpAnalytics.trackEvent(ZPEvents.MONEYTRANSFER_LAUNCH);
        }
    }

    private void logActionNavigationBack() {
        if (TAG.equals(LinkCardActivity.class.getSimpleName())) {
            zpAnalytics.trackEvent(ZPEvents.MANAGECARD_NAVIGATEBACK);
        } else if (TAG.equals(BalanceTopupActivity.class.getSimpleName())) {
            zpAnalytics.trackEvent(ZPEvents.ADDCASH_NAVIGATEBACK);
        } else if (TAG.equals(TransferHomeActivity.class.getSimpleName())) {
            zpAnalytics.trackEvent(ZPEvents.MONEYTRANSFER_NAVIGATEBACK);
        }
    }

}
