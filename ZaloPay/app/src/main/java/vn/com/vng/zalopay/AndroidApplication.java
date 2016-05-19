package vn.com.vng.zalopay;

import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.frogermcs.androiddevmetrics.AndroidDevMetrics;
import com.squareup.leakcanary.LeakCanary;
import com.zing.zalo.zalosdk.oauth.ZaloSDKApplication;

import java.io.File;

import timber.log.Timber;
import vn.com.vng.iot.debugviewer.DebugViewer;
import vn.com.vng.iot.debugviewer.Level;
import vn.com.vng.zalopay.app.AppLifeCycle;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.internal.di.components.ApplicationComponent;
import vn.com.vng.zalopay.internal.di.components.DaggerApplicationComponent;
import vn.com.vng.zalopay.internal.di.components.UserComponent;
import vn.com.vng.zalopay.internal.di.modules.ApplicationModule;
import vn.com.vng.zalopay.internal.di.modules.user.UserModule;
import vn.zing.pay.zmpsdk.ZingMobilePayApplication;


/**
 * Created by AnhHieu on 3/24/16.
 */
public class AndroidApplication extends MultiDexApplication {

    public static final String TAG = "AndroidApplication";

    public static File extStorageAppBasePath;
    public static File extStorageAppCachePath;


    private ApplicationComponent appComponent;
    private UserComponent userComponent;

    private static AndroidApplication _instance;

    public static AndroidApplication instance() {
        return _instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;

        registerActivityLifecycleCallbacks(new AppLifeCycle());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Timber.tag(TAG);
            AndroidDevMetrics.initWith(this);
            StrictMode.enableDefaults();
            LeakCanary.install(this);
            DebugViewer.registerInstance(this);
            Timber.plant(new Timber.Tree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    DebugViewer.postLog(priority, tag, message);
                }
            });
        }

        initAppComponent();
        initializeFileFolder();

        Timber.d(" onCreate " + appComponent);
        ZaloSDKApplication.wrap(this);
        ZingMobilePayApplication.wrap(this);
    }


    private void initAppComponent() {
        appComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        appComponent.userConfig().loadConfig();
       /* appComponent.threadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                appComponent.bundleService().prepareInternalBundle();
            }
        });*/
    }

    public UserComponent createUserComponent(User user) {
        userComponent = appComponent.plus(new UserModule(user));
        return userComponent;
    }

    public void releaseUserComponent() {
        userComponent = null;
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }

    public UserComponent getUserComponent() {
        return userComponent;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
	}
	
    private void initializeFileFolder() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            File externalStorageDir = Environment.getExternalStorageDirectory();

            if (externalStorageDir != null) {
                extStorageAppBasePath = new File(
                        externalStorageDir.getAbsolutePath() + File.separator
                                + "Android" + File.separator + "data"
                                + File.separator + getPackageName());
            }

            if (extStorageAppBasePath != null) {
                extStorageAppCachePath = new File(
                        extStorageAppBasePath.getAbsolutePath()
                                + File.separator + "cache");

                boolean isCachePathAvailable = true;

                if (!extStorageAppCachePath.exists()) {
                    isCachePathAvailable = extStorageAppCachePath.mkdirs();
                }

                if (!isCachePathAvailable) {
                    extStorageAppCachePath = null;
                }
            }

        }
    }
}
