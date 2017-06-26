package vn.com.zalopay.wallet.di.component;

import android.app.Application;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;
import vn.com.zalopay.wallet.api.IDownloadService;
import vn.com.zalopay.wallet.api.ITransService;
import vn.com.zalopay.wallet.configure.SDKConfiguration;
import vn.com.zalopay.wallet.di.module.ApiServiceModule;
import vn.com.zalopay.wallet.di.module.AppInfoRepositoryModule;
import vn.com.zalopay.wallet.di.module.ApplicationModule;
import vn.com.zalopay.wallet.di.module.BankAccountRepositoryModule;
import vn.com.zalopay.wallet.di.module.BankListRepositoryModule;
import vn.com.zalopay.wallet.di.module.CardRepositoryModule;
import vn.com.zalopay.wallet.di.module.ConfigurationModule;
import vn.com.zalopay.wallet.di.module.InteractorModule;
import vn.com.zalopay.wallet.di.module.PlatformInfoRepositoryModule;
import vn.com.zalopay.wallet.interactor.IAppInfo;
import vn.com.zalopay.wallet.interactor.IBank;
import vn.com.zalopay.wallet.interactor.ILink;
import vn.com.zalopay.wallet.interactor.IPlatformInfo;
import vn.com.zalopay.wallet.ui.channel.ChannelPresenter;
import vn.com.zalopay.wallet.ui.channellist.ChannelListPresenter;

@Singleton
@Component(modules = {ApplicationModule.class,
        ConfigurationModule.class,
        ApiServiceModule.class,
        AppInfoRepositoryModule.class,
        BankListRepositoryModule.class,
        CardRepositoryModule.class,
        BankAccountRepositoryModule.class,
        PlatformInfoRepositoryModule.class,
        InteractorModule.class})
public interface ApplicationComponent {

    void inject(ChannelListPresenter channelListPresenter);

    void inject(ChannelPresenter channelPresenter);

    Application application();

    SDKConfiguration sdkConfiguration();

    EventBus eventBus();

    IPlatformInfo platformInfoInteractor();

    IBank bankListInteractor();

    IAppInfo appInfoInteractor();

    ILink linkInteractor();

    ITransService transService();

    IDownloadService downloadService();
}
