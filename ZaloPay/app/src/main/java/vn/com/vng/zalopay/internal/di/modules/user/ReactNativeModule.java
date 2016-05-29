package vn.com.vng.zalopay.internal.di.modules.user;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import vn.com.vng.zalopay.BuildConfig;
import vn.com.vng.zalopay.internal.di.scope.UserScope;
import vn.com.vng.zalopay.mdl.BundleReactConfig;
import vn.com.vng.zalopay.mdl.BundleService;
import vn.com.vng.zalopay.mdl.impl.BundleReactConfigInternalDev;
import vn.com.vng.zalopay.mdl.impl.BundleReactConfigExternalDev;
import vn.com.vng.zalopay.mdl.impl.BundleReactConfigRelease;

/**
 * Created by AnhHieu on 5/12/16.
 */
@Module
public class ReactNativeModule {

    //Todo : heavy process
    /*@UserScope
    @Provides
    @Named("bundleservice")
    BundleService providesBundleService(Context context) {
        BundleServiceImpl bundleService = new BundleServiceImpl((Application) context);
        bundleService.prepareInternalBundle();
        Timber.d("internalBundle %s", bundleService.mCurrentInternalBundleFolder);
        return bundleService;
    }*/

    @UserScope
    @Provides
    BundleReactConfig provideBundleReactConfig(Context context, BundleService service) {
        switch (BuildConfig.REACT_DEVELOP_SUPPORT) {
            case DEV_INTERNAL:
                return new BundleReactConfigInternalDev();
            case DEV_EXTERNAL:
                return new BundleReactConfigExternalDev(service);
            case RELEASE:
                return new BundleReactConfigRelease(service);
            default:
                return null;
        }
    }
}
