package vn.com.vng.zalopay.navigation;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.facebook.react.bridge.Promise;
import com.zalopay.apploader.internal.ModuleName;
import com.zalopay.ui.widget.dialog.SweetAlertDialog;
import com.zalopay.ui.widget.util.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.Constants;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.account.ui.activities.ChangePinActivity;
import vn.com.vng.zalopay.account.ui.activities.EditAccountNameActivity;
import vn.com.vng.zalopay.passport.OnboardingActivity;
import vn.com.vng.zalopay.passport.LoginZaloActivity;
import vn.com.vng.zalopay.account.ui.activities.ProfileActivity;
import vn.com.vng.zalopay.account.ui.activities.UpdateProfileLevel2Activity;
import vn.com.vng.zalopay.account.ui.activities.UpdateProfileLevel3Activity;
import vn.com.vng.zalopay.authentication.AuthenticationCallback;
import vn.com.vng.zalopay.authentication.AuthenticationDialog;
import vn.com.vng.zalopay.authentication.FingerprintSuggestDialog;
import vn.com.vng.zalopay.authentication.fingerprintsupport.FingerprintManagerCompat;
import vn.com.vng.zalopay.balancetopup.ui.activity.BalanceTopupActivity;
import vn.com.vng.zalopay.bank.models.LinkBankType;
import vn.com.vng.zalopay.bank.ui.BankActivity;
import vn.com.vng.zalopay.bank.ui.BankSupportSelectionActivity;
import vn.com.vng.zalopay.bank.ui.NotificationLinkCardActivity;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.data.util.Lists;
import vn.com.vng.zalopay.domain.model.AppResource;
import vn.com.vng.zalopay.paymentapps.ui.PaymentApplicationActivity;
import vn.com.vng.zalopay.protect.ui.ProtectAccountActivity;
import vn.com.vng.zalopay.react.Helpers;
import vn.com.vng.zalopay.scanners.ui.ScanToPayActivity;
import vn.com.vng.zalopay.searchcategory.SearchCategoryActivity;
import vn.com.vng.zalopay.service.UserSession;
import vn.com.vng.zalopay.transfer.model.TransferObject;
import vn.com.vng.zalopay.transfer.ui.ReceiveMoneyActivity;
import vn.com.vng.zalopay.transfer.ui.TransferActivity;
import vn.com.vng.zalopay.transfer.ui.TransferHomeActivity;
import vn.com.vng.zalopay.transfer.ui.TransferHomeFragment;
import vn.com.vng.zalopay.transfer.ui.TransferViaZaloPayNameActivity;
import vn.com.vng.zalopay.transfer.ui.ZaloContactActivity;
import vn.com.vng.zalopay.ui.activity.BalanceManagementActivity;
import vn.com.vng.zalopay.ui.activity.HomeActivity;
import vn.com.vng.zalopay.ui.activity.InvitationCodeActivity;
import vn.com.vng.zalopay.ui.activity.MiniApplicationActivity;
import vn.com.vng.zalopay.ui.activity.RedPacketApplicationActivity;
import vn.com.vng.zalopay.ui.activity.TutorialConnectInternetActivity;
import vn.com.vng.zalopay.utils.AndroidUtils;
import vn.com.vng.zalopay.utils.CShareDataWrapper;
import vn.com.vng.zalopay.warningrooted.WarningRootedActivity;
import vn.com.vng.zalopay.webapp.WebAppActivity;
import vn.com.vng.zalopay.webview.WebViewConstants;
import vn.com.vng.zalopay.webview.entity.WebViewPayInfo;
import vn.com.vng.zalopay.webview.ui.WebViewActivity;
import vn.com.vng.zalopay.webview.ui.service.ServiceWebViewActivity;
import vn.com.vng.zalopay.withdraw.ui.activities.WithdrawActivity;
import vn.com.vng.zalopay.withdraw.ui.activities.WithdrawConditionActivity;
import vn.com.vng.zalopay.domain.model.zalosdk.ZaloProfile;
import vn.com.zalopay.wallet.business.entity.base.DMapCardResult;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.BankAccount;
import vn.com.zalopay.wallet.business.entity.gatewayinfo.MapCard;

import static vn.com.vng.zalopay.Constants.ARGUMENT_KEY_OAUTHTOKEN;
import static vn.com.vng.zalopay.Constants.ARGUMENT_KEY_ZALOPROFILE;
import static vn.com.vng.zalopay.domain.model.User.MIN_PROFILE_LEVEL;

/*
* Navigator
* */
@Singleton
public class Navigator implements INavigator {

    private static final long INTERVAL_CHECK_PASSWORD = 5 * 60 * 1000;

    private UserConfig mUserConfig;

    private SharedPreferences mPreferences;

    @Inject
    public Navigator(UserConfig userConfig, SharedPreferences preferences) {
        this.mUserConfig = userConfig;
        this.mPreferences = preferences;
    }

    public void startLoginActivity(Activity act, int requestCode, Uri data, long zaloid, String authCode) {
        Intent intent = getIntentLoginExternal(act);
        intent.setData(data);
        if (zaloid > 0 && !TextUtils.isEmpty(authCode)) {
            intent.putExtra("zaloid", zaloid);
            intent.putExtra("zauthcode", authCode);
        }

        act.startActivityForResult(intent, requestCode);
    }

    public void startLoginFromOtherTask(Activity activity, int requestCode, Uri data) {

        Intent i = new Intent();
        i.setData(data);
        PendingIntent pendingIntent = activity.createPendingResult(requestCode, i,
                PendingIntent.FLAG_ONE_SHOT);

        Intent intent = getIntentLoginExternal(activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("pendingResult", pendingIntent);
        intent.setData(data);

        activity.startActivity(intent);
    }

    public void startLoginActivity(Context context) {
        startLoginActivity(context, false);
    }

    public void startLoginActivity(Context context, boolean clearTop) {
        Intent intent = getIntentLogin(context, clearTop);
        context.startActivity(intent);
    }

    private Intent getIntentLoginExternal(Context context) {
        Intent intent = getIntentLogin(context, false);
        intent.putExtra("callingExternal", true);
        return intent;
    }

    public Intent getIntentLogin(Context context, boolean clearTop) {
        Intent intent = new Intent(context, LoginZaloActivity.class);
        if (clearTop) {
            intent.putExtra("finish", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        }
        return intent;
    }

    public void startOnboarding(Activity context, ZaloProfile zaloProfile, String oauthtoken) {
        Intent loginIntent = context.getIntent();

        Intent intent = new Intent(context, OnboardingActivity.class);

        if (loginIntent.getData() != null) {
            intent.setData(loginIntent.getData());
        }

        if (loginIntent.getExtras() != null) {
            intent.putExtras(loginIntent.getExtras());
        }

        intent.putExtra(ARGUMENT_KEY_ZALOPROFILE, zaloProfile);
        intent.putExtra(ARGUMENT_KEY_OAUTHTOKEN, oauthtoken);
        context.startActivity(intent);
    }

    public void startHomeActivity(Context context) {
        startHomeActivity(context, false);
    }

    public void startHomeActivity(Context context, boolean clearTop) {
        Intent intent = intentHomeActivity(context, clearTop);
        context.startActivity(intent);
    }

    public Intent intentHomeActivity(Context context, boolean clearTop) {
        Intent intent = new Intent(context, HomeActivity.class);

        if (clearTop) {
            intent.putExtra("finish", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        }
        return intent;
    }

    public void startUpdateLevel2(Context context, @NonNull String otp) {
        Intent intent = getUpdateProfileLevel2Activity(context);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("otp", otp);
        context.startActivity(intent);
    }


    public void startDepositActivity(Context context) {
        Intent intent = new Intent(context, BalanceTopupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void startDepositForResultActivity(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), BalanceTopupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        fragment.startActivityForResult(intent, Constants.REQUEST_CODE_DEPOSIT);
    }

    @Override
    public void startDepositForResultActivity(Activity activity) {
        startDepositForResultActivity(activity, false);
    }

    public void startDepositForResultActivity(Activity activity, boolean showNotificationLinkCard) {
        Intent intent = new Intent(activity, BalanceTopupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.ARG_SHOW_NOTIFICATION_LINK_CARD, showNotificationLinkCard);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_DEPOSIT);
    }

    public void startScanToPayActivity(Context context) {
        Intent intent = new Intent(context, ScanToPayActivity.class);
        context.startActivity(intent);
    }

    private void showUpdateProfileInfoDialog(final Context context) {
        if (context == null) {
            return;
        }
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE, R.style.alert_dialog)
                .setTitleText(context.getString(R.string.notification))
                .setContentText(context.getString(R.string.txt_need_input_userinfo))
                .setCancelText(context.getString(R.string.txt_close))
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                    }
                })
                .setConfirmText(context.getString(R.string.txt_input_userinfo))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        startUpdateProfileLevel2Activity(context);
                    }
                });
        sweetAlertDialog.show();
    }

    public void startMiniAppActivity(Activity activity, String moduleName) {
        if (ModuleName.RED_PACKET.equals(moduleName)) {
            if (mUserConfig.hasCurrentUser()) {
                if (mUserConfig.getCurrentUser().profilelevel < MIN_PROFILE_LEVEL) {
                    showUpdateProfileInfoDialog(activity);
                    return;
                }
            }
        }
        Intent intent = getIntentMiniAppActivity(activity, moduleName, new HashMap<>());
        activity.startActivity(intent);
    }

    public void startBankSupportSelectionActivity(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), BankSupportSelectionActivity.class);
        fragment.startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_BANK);
    }

    @Override
    public void startLinkCardActivity(Context context) {
        startLinkCardActivity(context, LinkBankType.LINK_BANK_CARD, false, false, false, false);
    }

    public void startLinkCardActivityAndFinish(Context context) {
        startLinkCardActivity(context, LinkBankType.LINK_BANK_CARD, false, false, false, true);
    }

    @Override
    public void startLinkAccountActivity(Context context) {
        startLinkCardActivity(context, LinkBankType.LINK_BANK_ACCOUNT, false, false, false, false);
    }

    public void startBankActivityFromWithdrawCondition(Context context) {
        startLinkCardActivity(context, LinkBankType.LINK_BANK_ACCOUNT, true, false, true, false);
    }

    private void startLinkBankActivityForResult(final Activity activity,
                                                LinkBankType linkBankType,
                                                String bankCode) {
        Timber.d("Start Link  bank for result, type [%s] activity[%s]", linkBankType, activity);
        if (activity == null) {
            return;
        }
        if (!validUserBeforeLinkBank(activity)) {
            return;
        }
        final Intent intent = getBankIntent(activity, linkBankType, bankCode, true, true, false);
        if (hasLinkBank() && shouldShowPinDialog()) {
            showPinDialog(activity, new AuthenticationCallback() {
                @Override
                public void onAuthenticated(String password) {
                    UserSession.mLastTimeCheckPassword = System.currentTimeMillis();
                    UserSession.mHashPassword = password;
                    activity.startActivityForResult(intent, Constants.REQUEST_CODE_LINK_BANK);
                }
            });
        } else {
            activity.startActivityForResult(intent, Constants.REQUEST_CODE_LINK_BANK);
        }
    }

    private void startLinkBankActivityForResult(final Fragment fragment,
                                                LinkBankType linkBankType,
                                                String bankCode) {
        Timber.d("Start Link  bank for result, type [%s] fragment[%s]", linkBankType, fragment);
        if (fragment == null) {
            return;
        }
        if (!validUserBeforeLinkBank(fragment.getContext())) {
            return;
        }
        final Intent intent = getBankIntent(fragment.getContext(), linkBankType, bankCode, true, true, false);
        if (hasLinkBank() && shouldShowPinDialog()) {
            showPinDialog(fragment.getContext(), new AuthenticationCallback() {
                @Override
                public void onAuthenticated(String password) {
                    UserSession.mLastTimeCheckPassword = System.currentTimeMillis();
                    UserSession.mHashPassword = password;
                    fragment.startActivityForResult(intent, Constants.REQUEST_CODE_LINK_BANK);
                }
            });
        } else {
            fragment.startActivityForResult(intent, Constants.REQUEST_CODE_LINK_BANK);
        }
    }

    @Override
    public void startLinkCardActivityForResult(final Activity activity, String bankCode) {
        startLinkBankActivityForResult(activity, LinkBankType.LINK_BANK_CARD, bankCode);
    }

    @Override
    public void startLinkCardActivityForResult(Fragment fragment, String bankCode) {
        startLinkBankActivityForResult(fragment, LinkBankType.LINK_BANK_CARD, bankCode);
    }

    @Override
    public void startLinkAccountActivityForResult(final Activity activity, String bankCode) {
        startLinkBankActivityForResult(activity, LinkBankType.LINK_BANK_ACCOUNT, bankCode);
    }

    @Override
    public void startLinkAccountActivityForResult(final Fragment fragment, String bankCode) {
        startLinkBankActivityForResult(fragment, LinkBankType.LINK_BANK_ACCOUNT, bankCode);
    }

    private Intent getBankIntent(Context context,
                                 LinkBankType linkBankType,
                                 String bankCode,
                                 boolean autoSelectBank,
                                 boolean continuePayment,
                                 boolean continueWithdraw) {
        Intent intent = new Intent(context, BankActivity.class);
        // TODO: 5/29/17 - longlv: hiện tại không còn dùng nữa nhưng cần thiết nên giữ lại.
        intent.putExtra(Constants.ARG_LINK_BANK_TYPE, linkBankType.getValue());
        Timber.d("getBankIntent linkBankType [%s]", linkBankType);
        if (linkBankType == LinkBankType.LINK_BANK_CARD) {
            intent.putExtra(Constants.ARG_LINK_CARD_WITH_BANK_CODE, bankCode);
        } else {
            intent.putExtra(Constants.ARG_LINK_ACCOUNT_WITH_BANK_CODE, bankCode);
        }
        intent.putExtra(Constants.ARG_GOTO_SELECT_BANK_IN_LINK_BANK, autoSelectBank);
        intent.putExtra(Constants.ARG_CONTINUE_PAY_AFTER_LINK_BANK, continuePayment);
        intent.putExtra(Constants.ARG_CONTINUE_WITHDRAW_AFTER_LINK_BANK, continueWithdraw);
        return intent;
    }

    private void startLinkCardActivity(Context context,
                                       LinkBankType linkBankType,
                                       boolean autoSelectBank,
                                       boolean continuePayment,
                                       boolean continueWithdraw,
                                       boolean isFinishActivity) {
        if (!validUserBeforeLinkBank(context)) {
            return;
        }
        Intent intent = getBankIntent(context, linkBankType, null, autoSelectBank, continuePayment, continueWithdraw);
        if (hasLinkBank() && shouldShowPinDialog()) {
            showPinDialog(context, intent, isFinishActivity);
        } else {
            context.startActivity(intent);
            if (isFinishActivity) {
                ((Activity) context).finish();
            }
        }
    }

    private boolean validUserBeforeLinkBank(Context context) {
        if (!mUserConfig.hasCurrentUser()) {
            return false;
        }

        if (mUserConfig.getCurrentUser().profilelevel < MIN_PROFILE_LEVEL) {
            showUpdateProfileInfoDialog(context);
            return false;
        }
        return true;
    }

    private boolean hasLinkBank() {
        boolean hasLinkBank = false;
        try {
            List<MapCard> mapCardLis = CShareDataWrapper
                    .getMappedCardList(mUserConfig.getCurrentUser());
            List<BankAccount> bankAccountList = CShareDataWrapper
                    .getMapBankAccountList(mUserConfig.getCurrentUser());
            hasLinkBank = !Lists.isEmptyOrNull(mapCardLis) || !Lists.isEmptyOrNull(bankAccountList);
        } catch (Exception ex) {
            Timber.w(ex, "startLinkCardActivity getMappedCardList exception");
        }
        return hasLinkBank;
    }

    public void startPaymentApplicationActivity(Context context, AppResource appResource) {
        Map<String, String> options = new HashMap<>();
        options.put("view", "main");
        Intent intent = intentPaymentApp(context, appResource, options);
        if (intent != null) {
            context.startActivity(intent);
        }
    }

    private Intent getUpdateProfileLevel2Activity(Context context,
                                                  Boolean linkAccAfterUpdate) {
        Intent intent = new Intent(context, UpdateProfileLevel2Activity.class);
        if (linkAccAfterUpdate != null) {
            intent.putExtra(Constants.ARG_UPDATE_PROFILE2_AND_LINK_ACC, linkAccAfterUpdate);
        }
        return intent;
    }

    private Intent getUpdateProfileLevel2Activity(Context context) {
        return getUpdateProfileLevel2Activity(context, null);
    }

    public void startUpdateProfileLevelBeforeLinkAcc(Activity activity) {
        Intent intent = getUpdateProfileLevel2Activity(activity, true);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_UPDATE_PROFILE_LEVEL_BEFORE_LINK_ACC);
    }

    public void startUpdateProfileLevelBeforeLinkAcc(Fragment fragment) {
        Intent intent = getUpdateProfileLevel2Activity(fragment.getContext(), true);
        fragment.startActivityForResult(intent, Constants.REQUEST_CODE_UPDATE_PROFILE_LEVEL_BEFORE_LINK_ACC);
    }


    public void startUpdateProfileLevel2Activity(Context context) {
        if (context == null) {
            Timber.w("Cannot start pre-profile activity due to NULL context");
            return;
        }

        Intent intent = getUpdateProfileLevel2Activity(context, null);
        context.startActivity(intent);
    }

    @Override
    public void startUpdateProfile2ForResult(Fragment fragment) {
        if (fragment == null || fragment.getContext() == null) {
            Timber.w("Cannot start pre-profile activity due to NULL context");
            return;
        }

        Intent intent = getUpdateProfileLevel2Activity(fragment.getContext());

        fragment.startActivityForResult(intent, Constants.REQUEST_CODE_UPDATE_PROFILE_LEVEL_2);
    }

    @Override
    public void startUpdateProfile2ForResult(Activity activity) {
        if (activity == null) {
            Timber.w("Cannot start pre-profile activity due to NULL context");
            return;
        }

        Intent intent = getUpdateProfileLevel2Activity(activity);

        activity.startActivityForResult(intent, Constants.REQUEST_CODE_UPDATE_PROFILE_LEVEL_2);
    }

    @Override
    public void startProfileInfoActivity(Context context) {
        if (!mUserConfig.hasCurrentUser()) {
            return;
        }

        if (shouldShowPinDialog()) {
            showPinDialog(context, intentProfile(context));
        } else {
            context.startActivity(intentProfile(context));
        }
    }

    private Intent intentChangePinActivity(Activity activity) {
        return new Intent(activity, ChangePinActivity.class);
    }

    public void startChangePin(Activity activity, String otp) {
        Intent intent = intentChangePinActivity(activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("otp", otp);
        activity.startActivity(intent);
    }

    public void startChangePin(Activity activity) {
        Intent intent = intentChangePinActivity(activity);
        activity.startActivity(intent);
    }

    public void startTransferMoneyActivity(Activity activity) {
        if (!mUserConfig.hasCurrentUser()) {
            return;
        }

        if (mUserConfig.getCurrentUser().profilelevel < MIN_PROFILE_LEVEL) {
            showUpdateProfileInfoDialog(activity);
        } else {
            Intent intent = new Intent(activity, TransferHomeActivity.class);
            activity.startActivity(intent);
        }
    }

    public void startZaloContactActivity(TransferHomeFragment fragment) {
        Intent intent = new Intent(fragment.getContext(), ZaloContactActivity.class);
        fragment.startActivityForResult(intent, Constants.REQUEST_CODE_TRANSFER);
    }

    public void startTransferActivity(Fragment fragment, TransferObject object, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), TransferActivity.class);
        intent.putExtra("transfer", object);
        fragment.startActivityForResult(intent, requestCode);
    }

    public void startTransferActivity(Context context, TransferObject object) {
        Intent intent = new Intent(context, TransferActivity.class);
        intent.putExtra("transfer", object);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public void startTransferActivity(Context context, TransferObject object, boolean forwardResult) {
        Intent intent = new Intent(context, TransferActivity.class);
        intent.putExtra("transfer", object);
        if (forwardResult) {
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        }
        context.startActivity(intent);
    }

    public void startUpdateProfile3Activity(Context context, boolean focusIdentity) {
        if (mUserConfig.hasCurrentUser() && mUserConfig.getCurrentUser().profilelevel == MIN_PROFILE_LEVEL) {
            Intent intent = new Intent(context, UpdateProfileLevel3Activity.class);
            intent.putExtra("focusIdentity", focusIdentity);
            context.startActivity(intent);
        }
    }

    public void startInvitationCodeActivity(Context context) {
        Intent intent = new Intent(context, InvitationCodeActivity.class);
        context.startActivity(intent);
    }

    public void startSearchCategoryActivity(Context context) {
        Intent intent = new Intent(context, SearchCategoryActivity.class);
        context.startActivity(intent);
    }

    @Override
    public Intent intentProfile(Context context) {
        return new Intent(context, ProfileActivity.class);
    }

    @Override
    public Intent intentPaymentApp(Context context, AppResource appResource, Map<String, String> launchOptions) {
        if (appResource == null) {
            return null;
        }
        Intent intent = new Intent(context, PaymentApplicationActivity.class);
        intent.putExtra("appResource", appResource);
        Bundle options = new Bundle();
        for (Map.Entry<String, String> e : launchOptions.entrySet()) {
            options.putString(e.getKey(), e.getValue());
        }
        intent.putExtra("launchOptions", options);
        return intent;
    }

    public void startBalanceManagementActivity(Context context) {
        Intent intent = new Intent(context, BalanceManagementActivity.class);
        context.startActivity(intent);
    }

    public void startWithdrawConditionActivity(Context context) {
        Intent intent = new Intent(context, WithdrawConditionActivity.class);
        context.startActivity(intent);
    }

    public void startWithdrawActivity(Context context) {
        Intent intent = new Intent(context, WithdrawActivity.class);
        context.startActivity(intent);
    }

    public void startWithdrawActivityAndFinish(Activity activity) {
        Intent intent = new Intent(activity, WithdrawActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void startTransactionDetail(Context context, String transid, String notificationid) {
        Map<String, String> launchOptions = new HashMap<>();
        launchOptions.put("view", "history");
        launchOptions.put("transid", transid);
        launchOptions.put("notificationid", notificationid);
        Intent intent = getIntentMiniAppActivity(context, ModuleName.TRANSACTION_LOGS, launchOptions);
        context.startActivity(intent);
    }

    public void startTransactionHistoryList(Context context) {
        Intent intent = getIntentMiniAppActivity(context, ModuleName.TRANSACTION_LOGS, new HashMap<>());
        context.startActivity(intent);
    }

    public void startTermActivity(Context context) {
        Map<String, String> option = new HashMap<>();
        option.put("view", "termsOfUse");
        Intent intent = getIntentMiniAppActivity(context, ModuleName.ABOUT, option);
        context.startActivity(intent);
    }

    private Intent getIntentMiniAppActivity(Context context, String moduleName, Map<String, String> launchOptions) {
        Intent intent;
        if (moduleName.equals(ModuleName.RED_PACKET)) {
            intent = new Intent(context, RedPacketApplicationActivity.class);
        } else {
            intent = new Intent(context, MiniApplicationActivity.class);
        }

        //  Intent intent = new Intent(context, MiniApplicationActivity.class);
        intent.putExtra("moduleName", moduleName);
        Bundle options = new Bundle();
        for (Map.Entry<String, String> e : launchOptions.entrySet()) {
            options.putString(e.getKey(), e.getValue());
        }
        intent.putExtra("launchOptions", options);
        return intent;
    }

    public void startDialSupport(Context context) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + context.getString(R.string.phone_support)));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(callIntent);
        } catch (Exception e) {
            Timber.d(e, "startDialSupport");
        }
    }

    public void startReceiveMoneyActivity(Context context) {
        Intent intent = new Intent(context, ReceiveMoneyActivity.class);
        context.startActivity(intent);
    }


    public void startWebViewActivity(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.ARG_URL, url);
        context.startActivity(intent);
    }

    public void startServiceWebViewActivity(Context context, WebViewPayInfo appGamePayInfo, String host) {
        Intent intent = new Intent(context, ServiceWebViewActivity.class);
        intent.putExtra(WebViewConstants.APPGAMEPAYINFO, appGamePayInfo);
        intent.putExtra(WebViewConstants.WEBURL, host);
        context.startActivity(intent);
    }

    public void startWebAppActivity(Context context, String url) {
        Intent intent = new Intent(context, WebAppActivity.class);
        intent.putExtra(Constants.ARG_URL, url);
        context.startActivity(intent);
    }

    public boolean shouldShowPinDialog() {
        boolean useProtect = mUserConfig.isUseProtectAccount();
        if (!useProtect) {
            return false;
        }

        int profileLevel = mUserConfig.getCurrentUser().profilelevel;
        long now = System.currentTimeMillis();
        return (now - UserSession.mLastTimeCheckPassword >= INTERVAL_CHECK_PASSWORD
                && profileLevel >= MIN_PROFILE_LEVEL);
    }

    private void showPinDialog(Context context, Intent pendingIntent) {
        showPinDialog(context, pendingIntent, false);
    }

    private void showPinDialog(final Context context, final AuthenticationCallback callback) {
        AndroidUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                AuthenticationDialog dialog = AuthenticationDialog.newInstance();
                dialog.setAuthenticationCallback(callback);
                dialog.show(((Activity) context).getFragmentManager(), AuthenticationDialog.TAG);
            }
        }, 200);
    }

    private void showPinDialog(Context context, Intent pendingIntent, boolean isFinish) {
        AuthenticationDialog dialog = AuthenticationDialog.newInstance();
        dialog.setPendingIntent(pendingIntent);
        dialog.setFinishActivity(isFinish);
        dialog.setAuthenticationCallback(new AuthenticationCallback() {
            @Override
            public void onAuthenticated(String password) {
                UserSession.mHashPassword = password;
                UserSession.mLastTimeCheckPassword = System.currentTimeMillis();
            }
        });
        dialog.show(((Activity) context).getFragmentManager(), AuthenticationDialog.TAG);
    }

    private void showPinDialog(final Context context, final Promise promise) {
        AuthenticationDialog dialog = AuthenticationDialog.newInstance();
        dialog.setAuthenticationCallback(new AuthenticationCallback() {
            @Override
            public void onAuthenticated(String password) {
                UserSession.mHashPassword = password;
                UserSession.mLastTimeCheckPassword = System.currentTimeMillis();
                Timber.d("onPinSuccess resolve true");
                Helpers.promiseResolveSuccess(promise, null);
                showSuggestionDialog(((Activity) context));
            }

            @Override
            public void onAuthenticationFailure() {
                Helpers.promiseResolveError(promise, -1, "Sai mật khẩu");
            }
        });
        dialog.show(((Activity) context).getFragmentManager(), AuthenticationDialog.TAG);
    }

    @Override
    public boolean promptPIN(Context context, int channel, final Promise promise) {
        if (!shouldShowPinDialog()) {
            Helpers.promiseResolveSuccess(promise, null);
            return true;
        }

        if (ignorePasswordPrompts(channel)) {
            Helpers.promiseResolveSuccess(promise, null);
            return true;
        }

        showPinDialog(context, promise);
        return false;
    }

    private boolean ignorePasswordPrompts(int channel) {
        if (channel == ChannelDisplay.PROFILE || channel == ChannelDisplay.TRANSACTION) {
            return true;
        }

        if (channel == ChannelDisplay.CARD_LISTS) {
            List<MapCard> mapCardLis = CShareDataWrapper.getMappedCardList(mUserConfig.getCurrentUser());
            return Lists.isEmptyOrNull(mapCardLis);
        }

        return false;
    }

    interface ChannelDisplay {
        int PROFILE = 1;
        int CARD_LISTS = 2;
        int TRANSACTION = 3;
    }

    public void startTransferViaAccountName(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), TransferViaZaloPayNameActivity.class);
        fragment.startActivityForResult(intent, Constants.REQUEST_CODE_TRANSFER_VIA_ZALOPAYID);
    }

    public void startEditAccountActivity(Context context) {
        Intent intent = new Intent(context, EditAccountNameActivity.class);
        context.startActivity(intent);
    }

    public void startTutorialConnectInternetActivity(Context context) {
        Intent intent = new Intent(context, TutorialConnectInternetActivity.class);
        context.startActivity(intent);
    }

    public void startNotificationLinkCardActivity(Context context, DMapCardResult mapCardResult) {
        Timber.d("startNotificationLinkCardActivity context [%s] card [%s]", context, mapCardResult);
        if (context == null || mapCardResult == null) {
            return;
        }
        Intent intent = new Intent(context, NotificationLinkCardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LAST4CARDNO, mapCardResult.getLast4Number());
        bundle.putString(Constants.IMAGE_FILE_PATH, mapCardResult.getCardLogo());
        bundle.putString(Constants.BANKNAME, mapCardResult.getBankName());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void startWarningRootedActivity(Context context) {
        Intent intent = new Intent(context, WarningRootedActivity.class);
        context.startActivity(intent);
    }

    public void startSystemSettingsActivity(Fragment fragment) {
        try {
            Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
            fragment.startActivityForResult(intent, Constants.REQUEST_CODE_SYSTEM_SETTINGS);
        } catch (Exception ignore) {
        }
    }

    public void startProtectAccount(Context context) {
        Intent intent = new Intent(context, ProtectAccountActivity.class);
        context.startActivity(intent);
    }

    public boolean startEmail(@NonNull Activity activity, @NonNull String emailTo, @Nullable String emailCC,
                              @NonNull String subject, @Nullable String emailText, @Nullable ArrayList<Uri> uris) {

        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[]{emailTo});
        if (!TextUtils.isEmpty(emailCC)) {
            emailIntent.putExtra(android.content.Intent.EXTRA_CC,
                    new String[]{emailCC});

        }

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (!TextUtils.isEmpty(emailText)) {
            emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
        }

        if (!Lists.isEmptyOrNull(uris)) {
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }

       /* emailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);*/
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.setPackage("com.google.android.gm");

        if (emailIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(emailIntent);
            return true;
        }

        emailIntent.setPackage("com.google.android.apps.inbox");

        if (emailIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(emailIntent);
            return true;
        }

        try {
            activity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            return true;
        } catch (Exception e) {
            Timber.e(e, "No support send via email");
        }

        return false;
    }

    @Deprecated
    public void showSuggestionDialog(Activity activity, String hashPassword) {
        UserSession.mHashPassword = hashPassword;
        showSuggestionDialog(activity);
    }

    @Deprecated
    public void showSuggestionDialog(Activity activity) {

        if (true) {
            return; // Tạm tắt suggestion
        }

        if (!shouldShowSuggestDialog()) {
            return;
        }

        FingerprintSuggestDialog dialog = new FingerprintSuggestDialog();
        dialog.setPassword(UserSession.mHashPassword);
        dialog.show(activity.getFragmentManager(), FingerprintSuggestDialog.TAG);
        mPreferences.edit()
                .putLong(Constants.PREF_LAST_TIME_SHOW_FINGERPRINT_SUGGEST, System.currentTimeMillis())
                .apply();
    }


    private boolean shouldShowSuggestDialog() {

        if (TextUtils.isEmpty(UserSession.mHashPassword)) {
            return false;
        }

        if (!mPreferences.getBoolean(Constants.PREF_SHOW_FINGERPRINT_SUGGEST, true)) {
            Timber.d("Not show fingerprint suggest");
            return false;
        }

        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(AndroidApplication.instance());

        if (!fingerprintManagerCompat.isFingerprintAvailable()) {
            Timber.d("Fingerprint not available");
            return false;
        }

        String password = mUserConfig.getEncryptedPassword();

        if (!TextUtils.isEmpty(password)) {
            Timber.d("Using fingerprint");
            return false;
        }

        long lastTime = mPreferences.getLong(Constants.PREF_LAST_TIME_SHOW_FINGERPRINT_SUGGEST, 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTime < TimeUtils.DAY) {
            Timber.d("less than one day");
            return false;
        }

        return true;
    }

    private Intent intentProtectAccountActivity(Context context) {
        return new Intent(context, ProtectAccountActivity.class);
    }

    public void startChangePinActivity(Context context) {
        if (shouldShowPinDialog()) {
            showPinDialog(context, intentChangePinActivity((Activity) context), false);
        } else {
            startChangePin((Activity) context);
        }
    }

    public void startProtectAccountActivity(Context context) {
        if (shouldShowPinDialog()) {
            showPinDialog(context, intentProtectAccountActivity(context), false);
        } else {
            startProtectAccount(context);
        }
    }

    public void startChrome(Context context, String url) {

        Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(i);
        } catch (ActivityNotFoundException ignore) {
        }
    }

    public void startFirefox(Context context, String url) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("org.mozilla.firefox");

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
            return;
        }

        intent.setPackage("org.mozilla.firefox_beta");

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public void startBrowser(Context context, String url) {
        AndroidUtils.openBrowser(context, url);
    }
}
