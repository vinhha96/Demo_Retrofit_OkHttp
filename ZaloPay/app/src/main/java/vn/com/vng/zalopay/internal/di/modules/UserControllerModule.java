package vn.com.vng.zalopay.internal.di.modules;

import android.content.Context;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import vn.com.vng.zalopay.data.api.ZaloPayService;
import vn.com.vng.zalopay.data.api.entity.mapper.ZaloPayEntityDataMapper;
import vn.com.vng.zalopay.data.balance.BalanceStore;
import vn.com.vng.zalopay.data.cache.SqlZaloPayScope;
import vn.com.vng.zalopay.data.cache.SqlZaloPayScopeImpl;
import vn.com.vng.zalopay.data.cache.model.DaoSession;
import vn.com.vng.zalopay.data.merchant.MerchantStore;
import vn.com.vng.zalopay.data.repository.ZaloPayRepositoryImpl;
import vn.com.vng.zalopay.data.transaction.TransactionStore;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.domain.repository.ZaloPayRepository;
import vn.com.vng.zalopay.internal.di.scope.UserScope;
import vn.com.vng.zalopay.react.iap.IPaymentService;
import vn.com.vng.zalopay.service.PaymentServiceImpl;

/**
 * Created by AnhHieu on 4/28/16.
 * User controller module
 */
@Module
public class UserControllerModule {

    @UserScope
    @Provides
    SqlZaloPayScope provideSqlZaloPayScope(User user, @Named("daosession") DaoSession session) {
        return new SqlZaloPayScopeImpl(user, session);
    }

    @UserScope
    @Provides
    ZaloPayRepository provideZaloPayRepository(ZaloPayService service, User user, ZaloPayEntityDataMapper mapper) {
        return new ZaloPayRepositoryImpl(mapper, service, user);
    }

    @UserScope
    @Provides
    IPaymentService providesIPaymentService(Context context, MerchantStore.Repository merchantRepository,
                                            BalanceStore.Repository balanceRepository,
                                            User user,
                                            TransactionStore.Repository transactionRepository) {
        return new PaymentServiceImpl(context, merchantRepository, balanceRepository, user, transactionRepository);
    }
}
