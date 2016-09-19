package vn.com.vng.zalopay.account.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.account.ui.presenter.ProfilePresenter;
import vn.com.vng.zalopay.account.ui.view.IProfileView;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.utils.PhoneUtil;
import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPEvents;
import vn.com.zalopay.wallet.view.dialog.DialogManager;
import vn.com.zalopay.wallet.view.dialog.SweetAlertDialog;

public class ProfileFragment extends BaseFragment implements IProfileView {

    public static ProfileFragment newInstance() {

        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    ProfilePresenter mPresenter;

    @BindView(R.id.tvBirthday)
    TextView tvBirthday;

    @BindView(R.id.tvCMND)
    TextView tvCMND;

    @BindView(R.id.tvGender)
    TextView tvGender;

    @BindView(R.id.tvPhone)
    TextView tvPhone;

    @BindView(R.id.tvEmail)
    TextView tvEmail;

    @BindView(R.id.layoutCmnd)
    View mLayoutCmnd;

    @BindView(R.id.layoutEmail)
    View mLayoutEmail;

    @BindView(R.id.tvZaloPayId)
    TextView tvZaloPayId;

//    @BindView(R.id.tvZaloId)
//    TextView tvZaloId;

    @BindView(R.id.tvAccountName)
    TextView mAccountNameView;

    @BindView(R.id.layoutChangePin)
    View layoutChangePin;

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_profile_layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.setView(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPresenter.getProfile();
        mPresenter.checkShowOrHideChangePinView();
    }

    @Override
    public void onResume() {
        mPresenter.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mPresenter.pause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mPresenter.destroyView();
        super.onDestroyView();
    }

    @Override
    public void updateUserInfo(User user) {

        this.setBirthDay(user.birthDate);
        this.setPhone(user.phonenumber);
        this.setCMND(user.identityNumber);
        this.setEmail(user.email);
        this.setGender(user.getGender());
        this.setZaloPayName(user.zalopayname);

        //    mLayoutCmnd.setClickable(user.profilelevel == 2);
        //    mLayoutEmail.setClickable(user.profilelevel == 2);

        tvZaloPayId.setText(user.zaloPayId);
        //tvZaloId.setText(String.valueOf(user.zaloId));
    }

    private void setBirthDay(long time) {
        Timber.d("setBirthDay: time %s", time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String birthday = simpleDateFormat.format(new Date(time * 1000));
        tvBirthday.setText(birthday);
    }

    private void setPhone(long phone) {
        String strPhoneNumber = PhoneUtil.toString(phone);
        if (!TextUtils.isEmpty(strPhoneNumber)) {
            tvPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            tvPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);
        }
        tvPhone.setText(strPhoneNumber);
    }

    @Override
    public void setZaloPayName(String zaloPayName) {
        if (!TextUtils.isEmpty(zaloPayName)) {
            mAccountNameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            mAccountNameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);
        }
        mAccountNameView.setText(zaloPayName);
    }

    @Override
    public void showDialogInfo(String content) {
        DialogManager.showSweetDialogCustom(getActivity(), content, getString(R.string.txt_close), SweetAlertDialog.INFO_TYPE, null);
    }

    private void setCMND(String cmnd) {
        if (!TextUtils.isEmpty(cmnd)) {
            tvCMND.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            if (mPresenter == null) {
                return;
            }
            boolean isWaitingApproveProfileLevel3 = mPresenter.isWaitingApproveProfileLevel3();
            if (isWaitingApproveProfileLevel3) {
                tvCMND.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tvCMND.setHint(getString(R.string.waiting_approve));
            } else {
                tvCMND.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);
                tvCMND.setHint(getString(R.string.not_update));
            }
        }

        tvCMND.setText(cmnd);
    }

    private void setEmail(String email) {
        if (!TextUtils.isEmpty(email)) {
            tvEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            if (mPresenter == null) {
                return;
            }
            boolean isWaitingApproveProfileLevel3 = mPresenter.isWaitingApproveProfileLevel3();
            if (isWaitingApproveProfileLevel3) {
                tvEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tvEmail.setHint(getString(R.string.waiting_approve));
            } else {
                tvEmail.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);
                tvEmail.setHint(getString(R.string.not_update));
            }
        }

        tvEmail.setText(email);
    }

    private void setGender(String gender) {
        tvGender.setText(gender);
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
        super.showToast(message);
    }

    @OnClick(R.id.layoutPhone)
    public void onClickPhone() {
        if (tvPhone.length() == 0) {
            navigator.startUpdateProfileLevel2Activity(getContext());
        }
    }

    @OnClick(R.id.layoutCmnd)
    public void onClickIdentity() {
        mPresenter.updateIdentity();
    }

    @OnClick(R.id.layoutEmail)
    public void onClickEmail() {
        mPresenter.updateEmail();
    }

    @OnClick(R.id.layoutAccountName)
    public void onClickEditAccountName() {
        if (!TextUtils.isEmpty(mAccountNameView.getText())) {
            return;
        }
        if (userConfig.getCurrentUser().profilelevel < 2) {
            navigator.startUpdateProfileLevel2Activity(getContext());
        } else {
            navigator.startEditAccountActivity(getContext());
            ZPAnalytics.trackEvent(ZPEvents.UPDATEZPN_LAUNCH_FROMPROFILE);
        }
    }

    @OnClick(R.id.layoutChangePin)
    public void onClickChangePin() {
        navigator.startChangePinActivity(getActivity());
    }

    @Override
    public void showHideChangePinView(boolean isShow) {
        if (isShow) {
            layoutChangePin.setVisibility(View.VISIBLE);
        } else {
            layoutChangePin.setVisibility(View.GONE);
        }
    }

}
