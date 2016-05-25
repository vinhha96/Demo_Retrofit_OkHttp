package vn.com.vng.zalopay.internal.di.components;

import dagger.Subcomponent;
import vn.com.vng.zalopay.account.ui.activities.ProfileInfoActivity;
import vn.com.vng.zalopay.account.ui.fragment.EditProfileFragment;
import vn.com.vng.zalopay.account.ui.fragment.PinProfileFragment;
import vn.com.vng.zalopay.account.ui.fragment.PreProfileFragment;
import vn.com.vng.zalopay.account.ui.fragment.ProfileFragment;
import vn.com.vng.zalopay.balancetopup.ui.activity.BalanceTopupActivity;
import vn.com.vng.zalopay.balancetopup.ui.fragment.BalanceTopupFragment;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.domain.repository.AppConfigRepository;
import vn.com.vng.zalopay.domain.repository.ZaloPayRepository;
import vn.com.vng.zalopay.home.ui.activity.MainActivity;
import vn.com.vng.zalopay.internal.di.modules.user.ApiUserModule;
import vn.com.vng.zalopay.internal.di.modules.user.ReactNativeModule;
import vn.com.vng.zalopay.internal.di.modules.user.UserControllerModule;
import vn.com.vng.zalopay.internal.di.modules.user.UserModule;
import vn.com.vng.zalopay.internal.di.modules.user.UserPresenterModule;
import vn.com.vng.zalopay.internal.di.scope.UserScope;
import vn.com.vng.zalopay.paymentapps.ui.PaymentApplicationActivity;
import vn.com.vng.zalopay.ui.activity.MiniApplicationActivity;
import vn.com.vng.zalopay.ui.activity.QRCodeScannerActivity;
import vn.com.vng.zalopay.ui.fragment.LeftMenuFragment;
import vn.com.vng.zalopay.ui.fragment.LinkCardFragment;
import vn.com.vng.zalopay.ui.fragment.LinkCardProdureFragment;
import vn.com.vng.zalopay.ui.fragment.tabmain.ZaloPayFragment;

@UserScope
@Subcomponent(
        modules = {
                UserModule.class,
                ApiUserModule.class,
                UserControllerModule.class,
                UserPresenterModule.class,
                ReactNativeModule.class,

        }
)
public interface UserComponent {

    User currentUser();

    AppConfigRepository appConfigRepository();

    ZaloPayRepository zaloPayRepository();

 /*   ApplicationRepository applicationRepository();*/

    /* inject Fragment */
    void inject(ZaloPayFragment f);

    void inject(LinkCardFragment link);

    void inject(BalanceTopupFragment f);

    void inject(LeftMenuFragment f);

    void inject(LinkCardProdureFragment f);

    void inject(ProfileFragment f);

    void inject(PreProfileFragment f);

    void inject(PinProfileFragment f);

    void inject(EditProfileFragment f);

    /* inject activity */

    void inject(QRCodeScannerActivity activity);

    void inject(MainActivity a);

    void inject(BalanceTopupActivity activity);

    void inject(MiniApplicationActivity activity);

    void inject(PaymentApplicationActivity activity);


    void inject(vn.com.vng.zalopay.ui.activity.MainActivity act);

    void inject(ProfileInfoActivity a);

}
