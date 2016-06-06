package vn.com.vng.zalopay.account.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.account.ui.presenter.ProfilePresenter;
import vn.com.vng.zalopay.account.ui.view.IProfileView;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.ui.widget.ClearableEditText;
import vn.com.vng.zalopay.utils.CurrencyUtil;
import vn.com.vng.zalopay.utils.ValidateUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends BaseFragment implements IProfileView {
    private OnFragmentInteractionListener mListener;

    @Inject
    ProfilePresenter mPresenter;

    @BindView(R.id.layoutProfileInfo)
    View mLayoutProfileInfo;

    @BindView(R.id.imgAvatar)
    ImageView imgAvatar;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvBalance)
    TextView tvBalance;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvCMND)
    TextView tvCMND;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvBirthday)
    TextView tvBirthday;
    @BindView(R.id.tvGender)
    TextView tvGender;
    @BindView(R.id.tvZaloId)
    TextView tvZaloId;
    @BindView(R.id.tvZaloPayId)
    TextView tvZaloPayId;

    @BindView(R.id.layoutEditProfileInfo)
    View layoutEditProfileInfo;

    @BindView(R.id.btnUpdate)
    View btnUpdate;

    @BindView(R.id.textInputFullName)
    TextInputLayout textInputFullName;
    @BindView(R.id.edtFullName)
    EditText edtFullName;

    @BindView(R.id.textInputEmail)
    TextInputLayout textInputEmail;
    @BindView(R.id.edtEmail)
    EditText edtEmail;

    @BindView(R.id.textInputPhone)
    TextInputLayout textInputPhone;
    @BindView(R.id.edtPhone)
    ClearableEditText edtPhone;

    @BindView(R.id.textInputCMND)
    TextInputLayout textInputCMND;
    @BindView(R.id.edtCmnd)
    EditText edtCmnd;

    @BindView(R.id.textInputAddress)
    TextInputLayout textInputAddress;
    @BindView(R.id.edtAddress)
    EditText edtAddress;

    @BindView(R.id.edtBirthday)
    EditText edtBirthday;
    @BindView(R.id.edtGender)
    EditText edtGender;
    @BindView(R.id.edtZaloId)
    EditText edtZaloId;
    @BindView(R.id.edtZaloPayId)
    EditText edtZaloPayId;

    @OnTextChanged(R.id.edtFullName)
    public void onTextChangedFullName(CharSequence charSequence) {
        if (isValidFullName()) {
            hideFullNameError();
        } else {
            showFullNameError();
        }
    }

    @OnTextChanged(R.id.edtPhone)
    public void onTextChangedPhone(CharSequence charSequence) {
        if (isValidPhone()) {
            hidePhoneError();
        } else {
            showPhoneError();
        }
    }

    @OnTextChanged(R.id.edtEmail)
    public void onTextChangedEmail(CharSequence charSequence) {
        if (isValidEmail()) {
            hideEmailError();
        } else {
            showEmailError();
        }
    }

    @OnTextChanged(R.id.edtCmnd)
    public void onTextChangedCmnd(CharSequence charSequence) {
        if (isValidCmnd()) {
            hideCmndError();
        } else {
            showCmndError();
        }
    }

    @OnTextChanged(R.id.edtAddress)
    public void onTextChangedAddress(CharSequence charSequence) {
        if (isValidAddress()) {
            hideAddressError();
        } else {
            showAddressError();
        }
    }

    public boolean isValidFullName() {
        String fullName = edtFullName.getText().toString();
        if (TextUtils.isEmpty(fullName)) {
            return false;
        }
        return true;
    }

    public boolean isValidPhone() {
        String phone = edtPhone.getString();
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        return ValidateUtil.isMobileNumber(phone);
    }

    public boolean isValidEmail() {
        String email = edtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        return ValidateUtil.isEmailAddress(email);
    }

    public boolean isValidCmnd() {
        String cmnd = edtCmnd.getText().toString();
        if (TextUtils.isEmpty(cmnd)) {
            return false;
        }
        return true;
    }

    public boolean isValidAddress() {
        String address = edtAddress.getText().toString();
        if (TextUtils.isEmpty(address)) {
            return false;
        }
        return true;
    }

    private void showFullNameError() {
        textInputFullName.setErrorEnabled(true);
        if (TextUtils.isEmpty(edtPhone.getText().toString())) {
            textInputFullName.setError(getString(R.string.invalid_fullname_empty));
        }
    }

    private void hideFullNameError() {
        textInputFullName.setErrorEnabled(false);
        textInputFullName.setError(null);
    }

    private void showPhoneError() {
        textInputPhone.setErrorEnabled(true);
        if (TextUtils.isEmpty(edtPhone.getText().toString())) {
            textInputPhone.setError(getString(R.string.invalid_phone_empty));
        } else {
            textInputPhone.setError(getString(R.string.invalid_phone));
        }
    }

    private void hidePhoneError() {
        textInputPhone.setErrorEnabled(false);
        textInputPhone.setError(null);
    }

    private void showEmailError() {
        textInputEmail.setErrorEnabled(true);
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            textInputEmail.setError(getString(R.string.invalid_email_empty));
        } else {
            textInputEmail.setError(getString(R.string.invalid_email));
        }
    }

    private void hideEmailError() {
        textInputEmail.setErrorEnabled(false);
        textInputEmail.setError(null);
    }

    private void showCmndError() {
        textInputCMND.setErrorEnabled(true);
        textInputCMND.setError(getString(R.string.invalid_cmnd));
    }

    private void hideCmndError() {
        textInputCMND.setErrorEnabled(false);
        textInputCMND.setError(null);
    }

    private void showAddressError() {
        textInputAddress.setErrorEnabled(true);
        textInputAddress.setError(getString(R.string.invalid_address_empty));
    }

    private void hideAddressError() {
        textInputAddress.setErrorEnabled(false);
        textInputAddress.setError(null);
    }

    @OnClick(R.id.imgEditInfo)
    public void onClickEditInfo(View view) {
        showEditProfileInfo();
    }

    @OnClick(R.id.btnUpdate)
    public void onClickBtnContinue(View view) {
        boolean isSuccess = true;
        if (!isValidFullName()) {
            showFullNameError();
            isSuccess = false;
        }
        if (!isValidEmail()) {
            showEmailError();
            isSuccess = false;
        }

        if (!isValidPhone()) {
            showPhoneError();
            isSuccess = false;
        }

        if (!isValidCmnd()) {
            showCmndError();
            isSuccess = false;
        }

        if (!isValidAddress()) {
            showAddressError();
            isSuccess = false;
        }
        if (isSuccess) {
            showProfileInfo();
        }
    }

    private void showProfileInfo() {
        mLayoutProfileInfo.setVisibility(View.VISIBLE);
        layoutEditProfileInfo.setVisibility(View.GONE);
    }

    private void showEditProfileInfo() {
        mLayoutProfileInfo.setVisibility(View.GONE);
        layoutEditProfileInfo.setVisibility(View.VISIBLE);
    }

    public void updateBalance(long balance) {
        tvBalance.setText(CurrencyUtil.formatCurrency(balance, false));
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
        return fragment;
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_edit_profile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.setView(this);
        layoutEditProfileInfo.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onBackPressed() {
        if (layoutEditProfileInfo.getVisibility()==View.VISIBLE) {
            showProfileInfo();
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    @Override
    public void onDestroyView() {
        mPresenter.destroyView();
        super.onDestroyView();
    }

    @Override
    public void updateUserInfo(User user) {
        if (user == null) {
            return;
        }
        Date date = new Date(user.birthDate*1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String birthday = simpleDateFormat.format(date);
        String zaloPayId = String.valueOf(user.uid);
        String zaloId = String.valueOf(user.zaloId);
        tvBirthday.setText(birthday);
        tvName.setText(user.dname);
        tvGender.setText(user.getGender());
        Glide.with(this).load(user.avatar)
                .placeholder(R.color.silver)
                .centerCrop()
                .into(imgAvatar);
        tvZaloId.setText(zaloId);
        tvZaloPayId.setText(zaloPayId);

        edtBirthday.setText(birthday);
        edtFullName.setText(user.dname);
        edtGender.setText(user.getGender());
        edtZaloId.setText(zaloId);
        edtZaloPayId.setText(zaloPayId);

        edtBirthday.setOnTouchListener(null);
        edtFullName.setOnTouchListener(null);
        edtGender.setOnTouchListener(null);
        edtZaloId.setOnTouchListener(null);
        edtZaloPayId.setOnTouchListener(null);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
