package vn.com.vng.zalopay.internal.di.modules;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;
import vn.com.vng.zalopay.BuildConfig;
import vn.com.vng.zalopay.data.cache.model.DaoSession;
import vn.com.vng.zalopay.data.repository.LocalResourceRepositoryImpl;
import vn.com.vng.zalopay.data.repository.datasource.LocalResourceFactory;
import vn.com.vng.zalopay.domain.repository.LocalResourceRepository;
import com.zalopay.apploader.BundleReactConfig;
import com.zalopay.apploader.BundleService;
import vn.com.vng.zalopay.navigation.INavigator;
import com.zalopay.apploader.impl.BundleReactConfigExternalDev;
import com.zalopay.apploader.impl.BundleReactConfigInternalDev;
import com.zalopay.apploader.impl.BundleReactConfigRelease;
import com.zalopay.apploader.impl.BundleServiceImpl;
import vn.com.vng.zalopay.navigation.Navigator;

/**
 * Created by AnhHieu on 5/12/16.
 */
@Module
public class AppReactNativeModule {

    @Provides
    @Singleton
    @Named("rootbundle")
    String providesRootBundle(Context context) {

        StringBuilder builder = new StringBuilder();
        if (BuildConfig.DEBUG) {
            builder.append(context.getCacheDir().getAbsolutePath());
            builder.append(File.separator)
                    .append(context.getPackageName());
        } else {
            builder.append(context.getFilesDir().getAbsolutePath());
            builder.append(File.separator)
                    .append(context.getPackageName());
        }
        builder.append(File.separator)
               .append("bundles");

        Timber.d("rootbundle %s", builder.toString());

        return builder.toString();
    }

    @Singleton
    @Provides
    LocalResourceRepository providesLocalResourceRepository(@Named("daosession") DaoSession daoSession) {
        return new LocalResourceRepositoryImpl(new LocalResourceFactory(daoSession));
    }

    @Provides
    @Singleton
    BundleService providesBundleService(Context context, LocalResourceRepository localResourceRepository, Gson gson, @Named("rootbundle") String rootbundle) {
        return new BundleServiceImpl((Application) context, localResourceRepository, gson, rootbundle);
    }


    @Provides
    @Singleton
    BundleReactConfig provideBundleReactConfig(BundleService service) {
        switch (BuildConfig.REACT_DEVELOP_SUPPORT) {
            case DEV_INTERNAL:
                return new BundleReactConfigInternalDev(service);
            case DEV_EXTERNAL:
                return new BundleReactConfigExternalDev(service);
            case RELEASE:
                return new BundleReactConfigRelease(service);
            default:
                return null;
        }
    }

    @Provides
    @Singleton
    INavigator providesNavigator(Navigator navigator){
        return navigator;
    }

}
