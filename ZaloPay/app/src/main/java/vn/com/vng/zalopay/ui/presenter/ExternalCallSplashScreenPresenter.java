package vn.com.vng.zalopay.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.Constants;
import vn.com.vng.zalopay.app.ApplicationState;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.data.util.Lists;
import vn.com.vng.zalopay.domain.model.RecentTransaction;
import vn.com.vng.zalopay.event.PaymentDataEvent;
import vn.com.vng.zalopay.event.ReceiveSmsEvent;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.ui.activity.ExternalCallSplashScreenActivity;
import vn.com.vng.zalopay.ui.view.IExternalCallSplashScreenView;

/**
 * Created by hieuvm on 12/4/16.
 */

public class ExternalCallSplashScreenPresenter implements IPresenter<IExternalCallSplashScreenView> {

    private static final int LOGIN_REQUEST_CODE = 100;

    private IExternalCallSplashScreenView mView;

    private UserConfig mUserConfig;

    private ApplicationState mApplicationState;

    private EventBus mEventBus;

    private Navigator mNavigator;

    @Inject
    public ExternalCallSplashScreenPresenter(UserConfig userConfig, ApplicationState applicationState,
                                             EventBus eventBus, Navigator navigator) {
        this.mUserConfig = userConfig;
        this.mApplicationState = applicationState;
        this.mEventBus = eventBus;
        this.mNavigator = navigator;
    }

    @Override
    public void setView(IExternalCallSplashScreenView view) {
        this.mView = view;
    }

    @Override
    public void destroyView() {
        this.mView = null;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        destroyView();
    }

    public void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            Timber.d("Launching with empty action");
            finish();
            return;
        }

        Timber.d("Launching with action: %s", action);
        if ("vn.zalopay.intent.action.SEND_MONEY".equals(action)) {
            if (handleZaloIntegration(intent)) {
                return;
            }
        }

        if (Intent.ACTION_VIEW.equals(action)) {
            handleDeepLink(intent.getData());
        }
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult: requestCode [%s] resultCode [%s]");
        if (requestCode == LOGIN_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK) {
            handleAppToAppPayment(data.getData());
        } else {
            finish();
        }
    }

    private boolean handleZaloIntegration(Intent intent) {
        if (mApplicationState.currentState() != ApplicationState.State.MAIN_SCREEN_CREATED) {
            return false;
        }

        String appId = intent.getStringExtra("android.intent.extra.APPID");
        String receiverId = intent.getStringExtra("vn.zalopay.intent.extra.RECEIVER_ID");
        String receiverName = intent.getStringExtra("vn.zalopay.intent.extra.RECEIVER_NAME");
        String receiverAvatar = intent.getStringExtra("vn.zalopay.intent.extra.RECEIVER_AVATAR");
        String type = intent.getStringExtra("vn.zalopay.intent.extra.TYPE");

        Timber.d("Processing send money on behalf of Zalo request");
        RecentTransaction item = new RecentTransaction();
        item.zaloId = Long.parseLong(receiverId);
        item.displayName = receiverName;
        item.avatar = receiverAvatar;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ARG_MONEY_TRANSFER_MODE, Constants.MoneyTransfer.MODE_ZALO);
        bundle.putParcelable(Constants.ARG_TRANSFERRECENT, item);
        mNavigator.startTransferActivity(mView.getContext(), bundle, true);
        finish();
        return true;
    }

    private void handleDeepLink(Uri data) {
        if (data == null) {
            finish();
            return;
        }

        Timber.d("handle deep links [%s]", data);

        String scheme = data.getScheme();
        String host = data.getHost();

        if (scheme.equalsIgnoreCase("zalopay-1") && host.equalsIgnoreCase("post")) {
            pay(data, false);
        } else if (scheme.equalsIgnoreCase("zalopay")) {

            if (host.equalsIgnoreCase("zalopay.vn")) {
                handleAppToAppPayment(data);
            } else if (host.equalsIgnoreCase("otp")) {
                handleOTPDeepLink(data);
                finish();
            } else {
                finish();
            }

        } else {
            finish();
        }
    }

    private boolean handleOTPDeepLink(Uri data) {
        if (!mUserConfig.hasCurrentUser()) {
            return false;
        }

        List<String> list = data.getPathSegments();
        if (Lists.isEmptyOrNull(list)) {
            return false;
        }

        String otp = list.get(0);
        Timber.d("handleDeepLink: %s", otp);
        if (TextUtils.isEmpty(otp) || !TextUtils.isDigitsOnly(otp)) {
            return false;
        }

       /* Intent intent = mNavigator.intentChangePinActivity((Activity) mView.getContext());
        intent.putExtra("otp", otp);
        mView.getContext().startActivity(intent);*/

        return true;
    }

    private void pay(Uri data, boolean isAppToApp) {
        Timber.d("pay with uri [%s] isAppToTpp [%s]", data, isAppToApp);

        String appid = data.getQueryParameter(vn.com.vng.zalopay.data.Constants.APPID);
        String zptranstoken = data.getQueryParameter(vn.com.vng.zalopay.data.Constants.ZPTRANSTOKEN);

        if (TextUtils.isEmpty(appid) ||
                !TextUtils.isDigitsOnly(appid) ||
                TextUtils.isEmpty(zptranstoken)) {
            finish();
            return;
        }

        mEventBus.postSticky(new PaymentDataEvent(Long.parseLong(appid), zptranstoken, isAppToApp));
        Timber.d("post sticky payment");
        finish();
    }

    private void handleAppToAppPayment(Uri data) {
        String appid = data.getQueryParameter(vn.com.vng.zalopay.data.Constants.APPID);
        String zptranstoken = data.getQueryParameter(vn.com.vng.zalopay.data.Constants.ZPTRANSTOKEN);

        boolean shouldFinishCurrentActivity = true;
        try {
            if (TextUtils.isEmpty(appid) ||
                    !TextUtils.isDigitsOnly(appid) ||
                    TextUtils.isEmpty(zptranstoken)) {
                return;
            }

            if (!mUserConfig.hasCurrentUser()) {
                Timber.d("start login activity");
                mNavigator.startLoginActivityForResult((ExternalCallSplashScreenActivity) mView.getContext(), LOGIN_REQUEST_CODE, data);
                shouldFinishCurrentActivity = false;
                return;
            }

            HandleInAppPayment payment = new HandleInAppPayment((Activity) mView.getContext());
            payment.initialize();
            if (mApplicationState.currentState() != ApplicationState.State.MAIN_SCREEN_CREATED) {
                Timber.d("need load payment sdk");
                payment.loadPaymentSdk();
            }

            payment.start(Long.parseLong(appid), zptranstoken);
            shouldFinishCurrentActivity = false;
        } finally {
            Timber.d("should finish current activity [%s] ", shouldFinishCurrentActivity);
            if (shouldFinishCurrentActivity) {
                finish();
            }
        }
    }

    private void finish() {
        if (mView != null) {
            ((Activity) mView.getContext()).finish();
        }
    }

}
