package vn.com.vng.zalopay.transfer.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zalopay.ui.widget.KeyboardFrameLayout;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;
import vn.com.vng.zalopay.Constants;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.domain.model.RecentTransaction;
import vn.com.vng.zalopay.domain.model.ZaloFriend;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.utils.AndroidUtils;
import vn.com.vng.zalopay.utils.VNDCurrencyTextWatcher;
import vn.com.zalopay.wallet.listener.ZPWOnEventDialogListener;
import vn.com.zalopay.wallet.view.dialog.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TransferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransferFragment extends BaseFragment implements ITransferView {

    @Inject
    TransferPresenter mPresenter;

    @BindView(R.id.ScrollView)
    ScrollView mScrollView;

    @BindView(R.id.imgAvatar)
    ImageView imgAvatar;

    @BindView(R.id.tvDisplayName)
    TextView tvDisplayName;

    @BindView(R.id.tvZaloPayName)
    TextView mTextViewZaloPayName;

    @BindView(R.id.textInputAmount)
    TextInputLayout textInputAmount;

    @BindView(R.id.edtAmount)
    EditText edtAmount;

    @BindView(R.id.edtTransferMsg)
    EditText edtTransferMsg;

    @BindView(R.id.rootView)
    KeyboardFrameLayout rootView;

    @BindView(R.id.btnContinue)
    View btnContinue;

    @OnClick(R.id.btnContinue)
    public void onClickContinue() {
        mPresenter.doTransfer();
    }

    @Override
    public void toggleAmountError(String error) {
        if (!TextUtils.isEmpty(error)) {
            textInputAmount.setErrorEnabled(true);
            textInputAmount.setError(error);
        } else {
            textInputAmount.setErrorEnabled(false);
            textInputAmount.setError(null);
        }
    }

    @Override
    public void setInitialValue(long currentAmount, String currentMessage) {
        if (!TextUtils.isEmpty(currentMessage)) {
            edtTransferMsg.setText(currentMessage);
        }
        if (currentAmount > 0) {
            edtAmount.setText(String.valueOf(currentAmount));
            edtAmount.setSelection(edtAmount.getText().toString().length());
        }
    }

    @Override
    public void showDialogThenClose(String message, String cancelText, int dialogType) {
        ZPWOnEventDialogListener onClickCancel = new ZPWOnEventDialogListener() {
            @Override
            public void onOKevent() {
                getActivity().finish();
            }
        };
        if (dialogType == SweetAlertDialog.ERROR_TYPE) {
            super.showErrorDialog(message, cancelText, onClickCancel);
        } else if (dialogType == SweetAlertDialog.WARNING_TYPE) {
            super.showWarningDialog(message, cancelText, onClickCancel);
        }
    }

    @Override
    public void setEnableBtnContinue(boolean isEnable) {
        btnContinue.setEnabled(isEnable);
    }

    @Override
    public void updateReceiverInfo(String displayName, String avatar, String zalopayName) {
        Timber.d("updateReceiverInfo displayName %s avatar %s", displayName, avatar);

        if (!TextUtils.isEmpty(displayName)) {
            tvDisplayName.setText(displayName);
        }

        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(this).load(avatar)
                    .placeholder(R.color.silver)
                    .error(R.drawable.ic_avatar_default)
                    .centerCrop()
                    .into(imgAvatar);
        }

        if (TextUtils.isEmpty(zalopayName)) {
            mTextViewZaloPayName.setText(getString(R.string.not_update_zalopayname));
        } else if (!TextUtils.isEmpty(zalopayName)) {
            mTextViewZaloPayName.setText(zalopayName);
        }
    }

    public TransferFragment() {
        // Required empty public constructor
    }

    public static TransferFragment newInstance(Bundle bundle) {
        TransferFragment fragment = new TransferFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_transfer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle argument = getArguments();
        if (argument == null) {
            return;
        }

        mPresenter.setView(this);

        mPresenter.initView((ZaloFriend) argument.getParcelable(Constants.ARG_ZALO_FRIEND),
                (RecentTransaction) Parcels.unwrap(argument.getParcelable(Constants.ARG_TRANSFERRECENT)),
                argument.getLong(Constants.ARG_AMOUNT),
                argument.getString(Constants.ARG_MESSAGE));

        mPresenter.setTransferMode(argument.getInt(Constants.ARG_MONEY_TRANSFER_MODE, Constants.MoneyTransfer.MODE_DEFAULT));

        edtAmount.addTextChangedListener(new VNDCurrencyTextWatcher(edtAmount) {
            @Override
            public void onValueUpdate(long value) {
                mPresenter.updateAmount(value);
            }
        });
        edtTransferMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Timber.d("beforeTextChanged s [%s] start [%s] count [%s] after [%s]", s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Timber.d("onTextChanged s [%s] start [%s] before [%s] count [%s]", s, start, before, count);
                if (TextUtils.isEmpty(s) || s.length() <= 36) {
                    return;
                }
                edtTransferMsg.setText(s.subSequence(0, 36));
                edtTransferMsg.setSelection(edtTransferMsg.getText().length());
                showWarningDialog(getString(R.string.transfer_message_maxlength), getString(R.string.txt_close), null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.updateMessage(s.toString());
            }
        });

        rootView.setOnKeyboardStateListener(new KeyboardFrameLayout.KeyboardHelper.OnKeyboardStateChangeListener() {
            @Override
            public void onKeyBoardShow(int height) {
                if (edtTransferMsg == null || mScrollView == null) {
                    return;
                }
                Timber.d("onKeyBoardShow: edtTransferMsg.isFocused() %s", edtTransferMsg.isFocused());
                if (edtTransferMsg.isFocused()) {
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    edtTransferMsg.requestFocusFromTouch();
                } else {
                    //Scroll down 24dp (height of error text)
                    mScrollView.scrollBy(0, AndroidUtils.dp(24));
                }
            }

            @Override
            public void onKeyBoardHide() {
                Timber.d("onKeyBoardHide");
            }
        });

        mPresenter.onViewCreated();
    }

    @Override
    public void onPause() {
        mPresenter.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    public void onDestroyView() {
        mPresenter.destroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mPresenter.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        mPresenter.navigateBack();
        return false;
    }

    @Override
    public void onTokenInvalid() {
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
        showToast(message);
    }
}
