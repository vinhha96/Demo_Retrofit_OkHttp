package vn.com.vng.zalopay.protect.ui;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.zalopay.ui.widget.dialog.SweetAlertDialog;
import com.zalopay.ui.widget.password.interfaces.IPasswordCallBack;
import com.zalopay.ui.widget.password.managers.PasswordManager;

import javax.inject.Inject;

import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.authentication.AuthenticationCallback;
import vn.com.vng.zalopay.authentication.AuthenticationChangePassword;
import vn.com.vng.zalopay.authentication.AuthenticationPassword;
import vn.com.vng.zalopay.authentication.fingerprintsupport.FingerprintManagerCompat;
import vn.com.vng.zalopay.authentication.secret.KeyTools;
import vn.com.vng.zalopay.data.cache.AccountStore;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.domain.repository.PassportRepository;
import vn.com.vng.zalopay.ui.presenter.AbstractPresenter;
import vn.com.vng.zalopay.user.UserBaseActivity;
import vn.com.vng.zalopay.utils.PasswordUtil;

import static vn.com.zalopay.wallet.controller.SDKApplication.getContext;

/**
 * Created by hieuvm on 12/26/16.
 */

final class ProtectAccountPresenter extends AbstractPresenter<IProtectAccountView> {

    @Inject
    public Context mContext;

    KeyTools mKeyTools;

    @Inject
    UserConfig mUserConfig;

    private PassportRepository mPassportRepository;

    private final FingerprintManagerCompat mFingerprintManagerCompat;
    private PasswordManager mPassword;

    @Inject
    ProtectAccountPresenter(PassportRepository passportRepository) {
        mKeyTools = new KeyTools();
        mFingerprintManagerCompat = FingerprintManagerCompat.from(AndroidApplication.instance());
        mPassportRepository = passportRepository;
    }

    void useFingerprintToAuthenticate(boolean enable) {
        Timber.d("useFingerprintToAuthenticate: [%s]", enable);
        if (mView == null) {
            return;
        }

        if (enable) {
            enableFingerprint();
        } else {
            disableFingerprint();
        }
    }

    void userProtectAccount(boolean enable) {
        if (mView == null) {
            return;
        }

        if (enable) {
            setUseProtectAccount(true);
            mView.setCheckedProtectAccount(true);
        } else {
            showAuthenticationDialog(mContext.getString(R.string.confirm_off_protect_message), new AuthenticationCallback() {
                @Override
                public void onAuthenticated(String password) {
                    setUseProtectAccount(false);
                    if (mView == null) {
                        return;
                    }

                    mView.setCheckedProtectAccount(false);
                }
            });
        }
    }

    void onViewCreated() {
        if (mView == null) {
            return;
        }

        if (!mFingerprintManagerCompat.isHardwareDetected()) {
            mView.hideFingerprintLayout();
        }

        boolean useProtect = mUserConfig.isUseProtectAccount();

        mView.setCheckedProtectAccount(useProtect);

        String password = mUserConfig.getEncryptedPassword();
        Timber.d("onViewCreated: password [%s] ", password);

        boolean isFingerprintAuthAvailable = mFingerprintManagerCompat.isFingerprintAvailable();

        mView.setCheckedFingerprint(!TextUtils.isEmpty(password) & isFingerprintAuthAvailable);
    }

    public Activity getActivity() {
        if (mView == null) {
            return null;
        }
        return mView.getActivity();
    }

    private void enableFingerprint() {
        if (!mFingerprintManagerCompat.isFingerprintAvailable()) {
            mView.setCheckedFingerprint(false);
            mView.showError(mContext.getString(R.string.tutorial_fingerprint_unavailable));
            return;
        }

        showFingerAuthentication();
    }

    private void showFingerAuthentication() {
        showAuthenticationDialog(mContext.getString(R.string.confirm_on_fingerprint_message), new AuthenticationCallback() {
            @Override
            public void onAuthenticated(String password) {

                boolean result = mKeyTools.storePassword(password);
                Timber.d("encrypt cipher result %s", result);
                if (!result) {
                    return;
                }

                if (mView == null) {
                    return;
                }

                mView.setCheckedFingerprint(true);
            }
        });
    }

    private void showAuthenticationDialog(String message, AuthenticationCallback callback) {
        if (mView == null) {
            return;
        }
        AuthenticationPassword authenticationPassword = new AuthenticationPassword((Activity) mView.getContext(), PasswordUtil.detectSuggestFingerprint(mView.getContext(), mUserConfig), callback);
        authenticationPassword.initialize();
        if (authenticationPassword != null && authenticationPassword.getPasswordManager() != null) {
            try {
                authenticationPassword.getPasswordManager().setTitle(mContext.getString(R.string.input_pin_to_confirm));
            } catch (Exception e) {
                Timber.d("ProtectAccountPresenter setTitle password [%s]", e.getMessage());
            }
        }
      /*  AuthenticationDialog fragment = AuthenticationDialog.newInstance();
        fragment.setStage(Stage.PASSWORD);
        fragment.setMessagePassword(message);
        fragment.setAuthenticationCallback(callback);

        fragment.show(((Activity) mView.getContext()).getFragmentManager(), AuthenticationDialog.TAG);*/
    }

    private void disableFingerprint() {
        showAuthenticationDialog(mContext.getString(R.string.confirm_off_fingerprint_message), new AuthenticationCallback() {
            @Override
            public void onAuthenticated(String password) {
                setUseFingerprint(false);
                if (mView == null) {
                    return;
                }
                mView.setCheckedFingerprint(false);
            }
        });
    }

    private void setUseProtectAccount(boolean enable) {
        mUserConfig.useProtectAccount(enable);
    }

    private void setUseFingerprint(boolean enable) {
        if (enable) {
            return;
        }
        mUserConfig.removeFingerprint();
    }

    public void logout() {
        Subscription subscription = mPassportRepository.logout()
                .subscribeOn(Schedulers.io())
                .subscribe(new DefaultSubscriber<>());
        mSubscription.add(subscription);

        if (mView == null) {
            return;
        }

        ((UserBaseActivity) mView.getContext()).clearUserSession(null);
    }

    void showConfirmSignOut() {
        new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE, R.style.alert_dialog)
                .setContentText(getActivity().getString(R.string.txt_confirm_sigout))
                .setCancelText(getActivity().getString(R.string.cancel))
                .setTitleText(getActivity().getString(R.string.confirm))
                .setConfirmText(getActivity().getString(R.string.txt_leftmenu_sigout))
                .setConfirmClickListener((SweetAlertDialog sweetAlertDialog) -> {
                    sweetAlertDialog.dismiss();
                    logout();
                })
                .show();
    }
}
