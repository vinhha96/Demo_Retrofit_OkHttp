package vn.com.vng.zalopay.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.ui.presenter.PinProfilePresenter;
import vn.com.vng.zalopay.ui.view.IPinProfileView;
import vn.com.zalopay.wallet.view.custom.pinview.GridPasswordView;
import vn.com.zalopay.wallet.view.dialog.SweetAlertDialog;

/**
 * Created by AnhHieu on 9/9/16.
 * *
 */
public class PinProfileDialog extends AlertDialog implements IPinProfileView {

    @BindView(R.id.tvHint)
    TextView tvHint;

    @BindView(R.id.sweetDialogRootView)
    LinearLayout mRootView;

    @BindView(R.id.passCodeInput)
    GridPasswordView passCodeInput;

    @Inject
    PinProfilePresenter presenter;

    @Inject
    Navigator navigator;

    public PinProfileDialog(Context context) {
        super(context, R.style.alert_dialog);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplication.instance().getAppComponent().inject(this);
        setContentView(R.layout.pin_profile_dialog);
        ButterKnife.bind(this, this);
        setWidthDialog();
        presenter.setView(this);

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                passCodeInput.forceInputViewGetFocus();
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideLoading();
            }
        });

        passCodeInput.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String s) {

            }

            @Override
            public void onInputFinish(String s) {
                presenter.validatePin(s);
            }
        });
    }

    public void setWidthDialog() {
        Display display = this.getWindow().getWindowManager().getDefaultDisplay();
        int densityDpi = display.getWidth();
        android.view.ViewGroup.LayoutParams params = this.mRootView.getLayoutParams();
        params.width = (int) ((double) densityDpi * 0.85D);
        params.height = -2;
        this.mRootView.setLayoutParams(params);
    }

    @Override
    public void onDetachedFromWindow() {
        presenter.destroyView();
        super.onDetachedFromWindow();
    }

    @Override
    public void setError(String message) {
        tvHint.setText(message);
    }

    @Override
    public void onPinSuccess() {
        Timber.d("onPinSuccess");
        dismiss();
        getContext().startActivity(navigator.intentProfile(getContext()));
    }

    @Override
    public void showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE, R.style.alert_dialog_transparent);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private SweetAlertDialog mProgressDialog;

    @OnClick(R.id.cancel_button)
    public void onClickCancel() {
        dismiss();
    }
}
