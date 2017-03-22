package vn.com.vng.zalopay.internal.di.modules;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import vn.com.vng.zalopay.Constants;
import vn.com.vng.zalopay.data.qrcode.QRCodeRepository;
import vn.com.vng.zalopay.data.qrcode.QRCodeStore;
import vn.com.vng.zalopay.internal.di.scope.UserScope;
import vn.com.vng.zalopay.utils.AppVersionUtils;

/**
 * Created by longlv on 2/6/17.
 * *
 */
@Module
public class QRCodeModule {

    @Provides
    @UserScope
    QRCodeStore.RequestService provideQRCodeService(@Named("retrofitApi") Retrofit retrofit) {
        return retrofit.create(QRCodeStore.RequestService.class);
    }

    @Provides
    @UserScope
    QRCodeStore.Repository provideQRCodeRepository(QRCodeStore.RequestService requestService) {
        String userAgent = Constants.UserAgent.ZALO_PAY_CLIENT + AppVersionUtils.getMainVersionName();
        return new QRCodeRepository(requestService, userAgent);
    }
}
