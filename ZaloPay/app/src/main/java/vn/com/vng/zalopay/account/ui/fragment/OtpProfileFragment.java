package vn.com.vng.zalopay.account.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnTextChanged;
import timber.log.Timber;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.account.ui.presenter.OTPProfilePresenter;
import vn.com.vng.zalopay.account.ui.view.IOTPProfileView;
import vn.com.vng.zalopay.event.ReceiveSmsEvent;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.ui.widget.ClearableEditText;
import vn.com.zalopay.wallet.listener.ZPWOnEventDialogListener;
import vn.com.zalopay.wallet.view.dialog.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnOTPFragmentListener} interface
 * to handle interaction events.
 * Use the {@link OtpProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtpProfileFragment extends BaseFragment implements IOTPProfileView {

    private OnOTPFragmentListener mListener;

    @Inject
    OTPProfilePresenter presenter;

    @Inject
    EventBus mEventBus;

    @BindView(R.id.textInputOTP)
    TextInputLayout textInputOTP;

    @BindView(R.id.edtOTP)
    ClearableEditText edtOTP;

    @BindView(R.id.btnContinue)
    View btnContinue;

    @OnTextChanged(R.id.edtOTP)
    public void onEdtOTPChanged() {
        if (edtOTP == null || TextUtils.isEmpty(edtOTP.getText().toString())) {
            showHideBtnContinue(false);
        } else {
            showHideBtnContinue(true);
        }
    }

    private void showHideBtnContinue(boolean show) {
        if (show) {
            btnContinue.setBackgroundResource(R.drawable.bg_btn_blue);
            btnContinue.setOnClickListener(mOnClickContinueListener);
        } else {
            btnContinue.setBackgroundResource(R.color.bg_btn_gray);
            btnContinue.setOnClickListener(null);
        }
    }

    private void showOTPError() {
        textInputOTP.setErrorEnabled(true);
        if (TextUtils.isEmpty(ClearableEditText.optText(edtOTP))) {
            textInputOTP.setError(getString(R.string.invalid_otp_empty));
        } else {
            textInputOTP.setError(getString(R.string.invalid_otp));
        }
    }

    private void hideOTPError() {
        textInputOTP.setErrorEnabled(false);
        textInputOTP.setError(null);
    }

    public boolean isValidOTP() {
        String otp = ClearableEditText.optText(edtOTP);
        return !TextUtils.isEmpty(otp);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OtpProfileFragment.
     */
    public static OtpProfileFragment newInstance() {
        return new OtpProfileFragment();
    }

    private View.OnClickListener mOnClickContinueListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickContinue();
        }
    };

    public void onClickContinue() {
        if (!isValidOTP()) {
            showOTPError();
            return;
        } else {
            hideOTPError();
        }
        presenter.verifyOtp(ClearableEditText.optText(edtOTP));
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_otp;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
        edtOTP.requestFocus();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOTPFragmentListener) {
            mListener = (OnOTPFragmentListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    @Override
    public void onDestroyView() {
        presenter.destroyView();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Override
    public void confirmOTPSuccess() {
        if (mListener != null) {
            mListener.onConfirmOTPSuccess();
        }
    }

    @Override
    public void onConfirmOTPError() {

    }

    @Override
    public void showLoading() {
        super.showProgressDialog();
    }

    @Override
    public void hideLoading() {
        super.hideProgressDialog();
    }

    @Override
    public void showRetry() {
    }

    @Override
    public void hideRetry() {
    }

    @Override
    public void showError(String message) {
        super.showErrorDialog(message, getContext().getString(R.string.txt_close), new ZPWOnEventDialogListener() {
            @Override
            public void onOKevent() {
                if (edtOTP != null && !edtOTP.isFocused()) {
                    edtOTP.requestFocus();
                }
            }
        });
    }

    public void showError(int messageResource) {
        showToast(messageResource);
    }

    @Subscribe
    public void onReceiveSmsMessages(ReceiveSmsEvent event) {
        String pattern = "(.*)(\\d{6})(.*)";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        for (ReceiveSmsEvent.SmsMessage message : event.messages) {
            Timber.d("Receive SMS: [%s: %s]", message.from, message.body);
            Matcher m = r.matcher(message.body);
            if (m.find()) {
                Timber.d("Found OTP: %s", m.group(2));
                edtOTP.setText(m.group(2));
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnOTPFragmentListener {
        void onConfirmOTPSuccess();
    }
}
