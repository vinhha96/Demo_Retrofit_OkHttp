package vn.com.vng.zalopay.internal.di.modules;

import android.content.Context;

import com.google.gson.Gson;
import com.zalopay.apploader.ReactNativeHostLongLife;
import com.zalopay.apploader.ReactNativeHostable;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;
import vn.com.vng.zalopay.BuildConfig;
import vn.com.vng.zalopay.data.apptransidlog.ApptransidLogStore;
import vn.com.vng.zalopay.data.balance.BalanceStore;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.data.cache.model.DaoSession;
import vn.com.vng.zalopay.data.filelog.FileLogStore;
import vn.com.vng.zalopay.data.transfer.TransferLocalStorage;
import vn.com.vng.zalopay.data.transfer.TransferRepository;
import vn.com.vng.zalopay.data.transfer.TransferStore;
import vn.com.vng.zalopay.data.ws.connection.Connection;
import vn.com.vng.zalopay.data.ws.connection.NotificationService;
import vn.com.vng.zalopay.data.ws.connection.WsConnection;
import vn.com.vng.zalopay.data.ws.parser.MessageParser;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.internal.di.scope.UserScope;
import vn.com.vng.zalopay.service.UserSession;

@Module
public class UserModule {

    private final User user;

    public UserModule(User user) {
        Timber.d("Create new instance of UserModule");
        this.user = user;
    }

    @Provides
    @UserScope
    User provideUser() {
        return user;
    }

    @Provides
    @UserScope
    UserSession providesUserSession(Context context, UserConfig userConfig, EventBus eventBus,
                                    NotificationService service,
                                    BalanceStore.Repository repository, FileLogStore.Repository fileLogRepository,
                                    ApptransidLogStore.Repository apptransidLogRepository) {
        return new UserSession(context, user, userConfig, eventBus, service, repository, fileLogRepository, apptransidLogRepository);
    }

    @Provides
    @UserScope
    TransferStore.LocalStorage provideTransferLocalStorage(@Named("daosession") DaoSession session) {
        return new TransferLocalStorage(session);
    }

    @Provides
    @UserScope
    TransferStore.Repository provideTransferRepository(TransferStore.LocalStorage localStorage) {
        return new TransferRepository(localStorage);
    }


    @Provides
    @UserScope
    ReactNativeHostable provideReactNativeInstanceManager() {
        Timber.d("Create new instance of ReactNativeInstanceManagerLongLife");
        return new ReactNativeHostLongLife();
    }

}