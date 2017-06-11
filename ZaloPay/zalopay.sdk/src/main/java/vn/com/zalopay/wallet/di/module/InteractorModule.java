package vn.com.zalopay.wallet.di.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import vn.com.zalopay.wallet.interactor.AppInfoInteractor;
import vn.com.zalopay.wallet.interactor.BankInteractor;
import vn.com.zalopay.wallet.interactor.IAppInfo;
import vn.com.zalopay.wallet.interactor.IBank;
import vn.com.zalopay.wallet.interactor.ILink;
import vn.com.zalopay.wallet.interactor.IPlatformInfo;
import vn.com.zalopay.wallet.interactor.LinkInteractor;
import vn.com.zalopay.wallet.interactor.PlatformInfoInteractor;
import vn.com.zalopay.wallet.repository.appinfo.AppInfoStore;
import vn.com.zalopay.wallet.repository.bankaccount.BankAccountStore;
import vn.com.zalopay.wallet.repository.bank.BankStore;
import vn.com.zalopay.wallet.repository.cardmap.CardStore;
import vn.com.zalopay.wallet.repository.platforminfo.PlatformInfoStore;

/**
 * Created by chucvv on 6/7/17.
 */
@Module
public class InteractorModule {
    @Provides
    @Singleton
    IPlatformInfo providePlatformInteractor(PlatformInfoStore.Repository platformInfoRepository) {
        return new PlatformInfoInteractor(platformInfoRepository);
    }

    @Provides
    @Singleton
    IBank provideBankListInteractor(BankStore.Repository bankListRepository) {
        return new BankInteractor(bankListRepository);
    }

    @Provides
    @Singleton
    IAppInfo provideAppInfoInteractor(AppInfoStore.Repository appinfoRepository) {
        return new AppInfoInteractor(appinfoRepository);
    }

    @Provides
    @Singleton
    ILink provideLinkInteactor(CardStore.Repository cardRepository, BankAccountStore.Repository bankAccountRepository) {
        return new LinkInteractor(cardRepository, bankAccountRepository);
    }
}
