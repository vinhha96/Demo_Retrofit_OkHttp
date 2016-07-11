package vn.com.vng.zalopay.internal.di.components;

import android.content.Context;
import android.content.SharedPreferences;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;
import vn.com.vng.zalopay.account.ui.activities.LoginZaloActivity;
import vn.com.vng.zalopay.account.utils.ZaloProfilePreferences;
import vn.com.vng.zalopay.analytics.ZPAnalytics;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.domain.executor.PostExecutionThread;
import vn.com.vng.zalopay.domain.executor.ThreadExecutor;
import vn.com.vng.zalopay.domain.repository.LocalResourceRepository;
import vn.com.vng.zalopay.domain.repository.PassportRepository;
import vn.com.vng.zalopay.internal.di.modules.ApiModule;
import vn.com.vng.zalopay.internal.di.modules.AppControllerModule;
import vn.com.vng.zalopay.internal.di.modules.ApplicationModule;
import vn.com.vng.zalopay.internal.di.modules.NetworkModule;
import vn.com.vng.zalopay.internal.di.modules.user.ReactNativeModule;
import vn.com.vng.zalopay.internal.di.modules.user.UserModule;
import vn.com.vng.zalopay.mdl.BundleService;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.receiver.NetworkReceiver;
import vn.com.vng.zalopay.service.ApplicationSession;
import vn.com.vng.zalopay.service.DownloadService;
import vn.com.vng.zalopay.service.GlobalEventHandlingService;
import vn.com.vng.zalopay.ui.fragment.InvitationCodeFragment;
import vn.com.vng.zalopay.ui.fragment.SplashScreenFragment;
import vn.com.vng.zalopay.notification.NotificationHelper;

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class, ApiModule.class, AppControllerModule.class,
        ReactNativeModule.class})
public interface ApplicationComponent {
    //Exposed to sub-graphs.
    Context context();

    ThreadExecutor threadExecutor();

    PostExecutionThread postExecutionThread();

    UserComponent plus(UserModule userModule);

    EventBus eventBus();

    SharedPreferences sharedPreferences();

    UserConfig userConfig();

    BundleService bundleService();

    ZaloProfilePreferences profilePreferences();

    PassportRepository passportRepository();

    LocalResourceRepository localResourceRepository();

    GlobalEventHandlingService globalEventService();

    Navigator navigator();

    ApplicationSession applicationSession();

    ZPAnalytics zpAnalytics();

    /*INJECT*/

    void inject(SplashScreenFragment f);

    void inject(LoginZaloActivity a);

    void inject(DownloadService service);

    void inject(NetworkReceiver receiver);

    void inject(InvitationCodeFragment f);

}
