package vn.com.vng.zalopay.internal.di.modules.user;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import vn.com.vng.zalopay.data.cache.AccountStore;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.data.cache.model.DaoSession;
import vn.com.vng.zalopay.data.repository.AccountRepositoryImpl;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.internal.di.scope.UserScope;

/**
 * Created by AnhHieu on 7/3/16.
 */

@Module
public class AccountModule {

    @Provides
    @UserScope
    AccountStore.RequestService providesAccountService(@Named("retrofit") Retrofit retrofit) {
        return retrofit.create(AccountStore.RequestService.class);
    }

    @Provides
    @UserScope
    AccountStore.UploadPhotoService providesAccountPhotoService(@Named("retrofitPhoto") Retrofit retrofit) {
        return retrofit.create(AccountStore.UploadPhotoService.class);
    }


    @UserScope
    @Provides
    AccountStore.Repository providesAccountRepository(AccountStore.RequestService service, AccountStore.UploadPhotoService photoService, UserConfig userConfig, User user) {
        return new AccountRepositoryImpl(service, photoService, userConfig, user);
    }

    @UserScope
    @Provides
    AccountStore.LocalStorage providesAccountLocalStorage(@Named("daosession") DaoSession session) {
        return null;
    }
}
