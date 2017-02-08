package vn.com.vng.zalopay.withdraw.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import timber.log.Timber;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.utils.CShareDataWrapper;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.ui.widget.MoneyEditText;
import vn.com.vng.zalopay.utils.CurrencyUtil;
import vn.com.vng.zalopay.withdraw.ui.presenter.WithdrawPresenter;
import vn.com.vng.zalopay.withdraw.ui.view.IWithdrawView;

/**
 * A simple {@link BaseFragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link WithdrawFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WithdrawFragment extends BaseFragment implements IWithdrawView {

    public static WithdrawFragment newInstance() {
        return new WithdrawFragment();
    }

    public WithdrawFragment() {
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_withdraw;
    }

    private long minWithdrawAmount;
    private long maxWithdrawAmount;

    @Inject
    WithdrawPresenter mPresenter;

    @BindView(R.id.tvResourceMoney)
    TextView tvResourceMoney;

    @BindView(R.id.edtAmount)
    MoneyEditText mEdtMoneyView;

    @BindView(R.id.btnContinue)
    View mBtnContinueView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLimitAmount();
    }

    private void initLimitAmount() {
        try {
            minWithdrawAmount = CShareDataWrapper.getMinWithDrawValue();
            maxWithdrawAmount = CShareDataWrapper.getMaxWithDrawValue();
        } catch (Exception e) {
            Timber.w(e, "Get min/max withdraw from paymentSDK exception: [%s]", e.getMessage());
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
        tvResourceMoney.setText(String.format(getResources().getString(R.string.title_min_money),
                CurrencyUtil.formatCurrency(minWithdrawAmount, false)));

        mEdtMoneyView.setMinMaxMoney(minWithdrawAmount, maxWithdrawAmount);

        mBtnContinueView.setEnabled(mEdtMoneyView.isValid());
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    public void onPause() {
        mPresenter.pause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mEdtMoneyView.clearValidators();
        mPresenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mPresenter.destroy();
        CShareDataWrapper.dispose();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnTextChanged(value = R.id.edtAmount, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onAmountChanged(CharSequence s) {
        mBtnContinueView.setEnabled(mEdtMoneyView.isValid());
    }

    @OnClick(R.id.btnContinue)
    public void setOnClickContinue() {
        if (!mEdtMoneyView.validate()) {
            return;
        }

        mPresenter.withdraw(mEdtMoneyView.getAmount());
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
    public void showError(String message) {
        showErrorDialog(message);
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void showAmountError(String error) {
        if (mEdtMoneyView != null) {
            mEdtMoneyView.setError(error);
        }
    }
}
