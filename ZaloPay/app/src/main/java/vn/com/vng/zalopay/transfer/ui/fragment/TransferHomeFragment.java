package vn.com.vng.zalopay.transfer.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;
import vn.com.vng.zalopay.Constants;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.domain.model.Person;
import vn.com.vng.zalopay.domain.model.RecentTransaction;
import vn.com.vng.zalopay.transfer.ui.adapter.TransferRecentAdapter;
import vn.com.vng.zalopay.transfer.ui.presenter.TransferHomePresenter;
import vn.com.vng.zalopay.transfer.ui.view.ITransferHomeView;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.utils.ValidateUtil;

/**
 * A fragment representing a list of Items.
 * <p>
 */
public class TransferHomeFragment extends BaseFragment implements
        TransferRecentAdapter.OnClickTransferRecentListener, ITransferHomeView {


    @Inject
    TransferHomePresenter presenter;

    private TransferRecentAdapter mAdapter;

    @BindView(R.id.tvTileTransactionRecent)
    View mTvTileTransactionRecent;

    @BindView(R.id.list)
    RecyclerView mList;

    @BindView(R.id.viewSeparate)
    View viewSeparate;

    @BindView(R.id.layoutIntroduction)
    View layoutIntroduction;

    @BindView(R.id.imgIntroduction)
    ImageView imgIntroduction;

    @OnClick(R.id.layoutTransferAccZaloPay)
    public void onClickTransferAccZaloPay() {
        navigator.startZaloContactActivity(this);
    }

    @OnClick(R.id.layoutTransferViaAccount)
    public void onClickTransferViaAccountName() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chuyển tiền");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_transfer, null);

        final TextView mInputAccountNameView = (TextView) view.findViewById(R.id.tvMessage);
        final EditText editText = (EditText) view.findViewById(R.id.tvAccountName);

        builder.setView(view);

        builder.setPositiveButton(R.string.btn_continue,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("name %s", editText.getText().toString());

                String s = editText.getText().toString();
                boolean isValid = false;

                if (!ValidateUtil.isValidLengthZPName(s.toString())) {
                    mInputAccountNameView.setText(getString(R.string.exception_account_name_length));
                    mInputAccountNameView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else if (s.toString().indexOf(" ") > 0) {
                    mInputAccountNameView.setText(getString(R.string.exception_account_name_with_space));
                    mInputAccountNameView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else if (!ValidateUtil.isValidZaloPayName(s.toString())) {
                    mInputAccountNameView.setText(getString(R.string.exception_account_name_special_char));
                    mInputAccountNameView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else {
                    mInputAccountNameView.setError(getString(R.string.input_account_zalo_pay));
                    mInputAccountNameView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                    if (!TextUtils.isEmpty(s)) {
                        isValid = true;
                    }
                }

                if (isValid) {
                    presenter.getUserInfo(s);
                    dialog.dismiss();
                }
            }
        });

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TransferHomeFragment() {
    }

    @SuppressWarnings("unused")
    public static TransferHomeFragment newInstance() {
        TransferHomeFragment fragment = new TransferHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_transfer_home;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TransferRecentAdapter(getContext(), this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);

        mList.setLayoutManager(new LinearLayoutManager(getContext()));
        mList.setHasFixedSize(true);
        mList.setAdapter(mAdapter);


        mTvTileTransactionRecent.setVisibility(View.GONE);
        mList.setVisibility(View.GONE);
        layoutIntroduction.setVisibility(View.VISIBLE);


        imgIntroduction.setBackgroundResource(R.drawable.anim_transfer);
        AnimationDrawable animationDrawable = (AnimationDrawable) imgIntroduction.getBackground();
        animationDrawable.start();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.getRecent();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        presenter.destroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == vn.com.vng.zalopay.Constants.REQUEST_CODE_TRANSFER) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showLoading() {
        showProgressDialog();
    }

    @Override
    public void hideLoading() {
        hideProgressDialog();
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

    @Override
    public void setData(List<RecentTransaction> list) {
        mAdapter.setData(list);
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (mAdapter.getItemCount() > 0) {
            mTvTileTransactionRecent.setVisibility(View.VISIBLE);
            mList.setVisibility(View.VISIBLE);
            viewSeparate.setVisibility(View.VISIBLE);
            layoutIntroduction.setVisibility(View.GONE);
            imgIntroduction.clearAnimation();
        } else {
            mTvTileTransactionRecent.setVisibility(View.GONE);
            mList.setVisibility(View.GONE);
            viewSeparate.setVisibility(View.GONE);
            layoutIntroduction.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGetProfileSuccess(Person person, String zaloPayName) {
        RecentTransaction item = new RecentTransaction();
        item.avatar = person.avatar;
        item.zaloPayId = person.uid;
        item.displayName = person.dname;
        item.phoneNumber = String.valueOf(person.phonenumber);
        item.zaloPayName = zaloPayName;
        onItemRecentClick(item);
    }

    @Override
    public void onItemRecentClick(RecentTransaction item) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.ARG_TRANSFERRECENT, Parcels.wrap(item));
        navigator.startTransferActivity(this, bundle);
    }
}
