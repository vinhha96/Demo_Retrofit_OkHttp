package vn.com.vng.zalopay.bank.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.daimajia.swipe.util.Attributes;
import com.zalopay.ui.widget.dialog.listener.ZPWOnEventConfirmDialogListener;
import com.zalopay.ui.widget.recyclerview.SpacesItemDecoration;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.internal.DebouncingOnClickListener;
import timber.log.Timber;
import vn.com.vng.zalopay.Constants;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.utils.AndroidUtils;
import vn.com.vng.zalopay.utils.DialogHelper;

/**
 * Created by hieuvm on 7/10/17.
 * *
 */

public class BankListFragment extends BaseFragment implements IBankListView, BankListAdapter.OnBankListClickListener {

    @BindView(R.id.listview)
    RecyclerView mListView;

    @BindView(R.id.link_card_empty_view)
    View mEmptyView;

    public static BankListFragment newInstance(Bundle args) {
        BankListFragment fragment = new BankListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_bank_list;
    }

    @Inject
    BankListPresenter mPresenter;

    protected BankListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new BankListAdapter(getContext(), this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
        mListView.setHasFixedSize(true);
        mListView.addItemDecoration(new SpacesItemDecoration(AndroidUtils.dp(16)));
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.addOnScrollListener(mOnScrollListener);
        mAdapter.setMode(Attributes.Mode.Single);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.loadView();
        mPresenter.handleBundle(this, getArguments());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        if (mAdapter != null) {
            mAdapter.closeAllItems();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        mListView.clearOnScrollListeners();
        mListView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bank, menu);
        MenuItem menuItem = menu.findItem(R.id.action_add_more_bank);
        View view = menuItem.getActionView();
        view.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                mPresenter.startBankSupport(BankListFragment.this);
            }
        });
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
    public void showError(String message) {
        showErrorDialog(message);
    }

    @Override
    public void setData(List<BankData> val) {
        mAdapter.setData(val);
        checkIfEmpty();
    }

    @Override
    public void remove(BankData val) {
        mAdapter.remove(val);
        checkIfEmpty();
    }

    @Override
    public void close(BankData val) {
        if (mAdapter == null) {
            return;
        }

        int index = mAdapter.indexOf(val);
      //  Timber.d("close: [index:%s]", index);

        if (index < 0) {
            return;
        }

        View childView = mListView.getChildAt(index);

        if (childView instanceof BankCardView) {
            ((BankCardView) childView).close();
        }
    }

    @Override
    public void closeAll() {
        if (mAdapter != null) {
            mAdapter.closeAllItems();
        }
    }

    @Override
    public void insert(BankData val) {
        mAdapter.insert(val);
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        mEmptyView.setVisibility(mAdapter.getItemCount() > 1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClickAddCard() {
        mPresenter.startBankSupport(this);
    }

    @Override
    public void onClickRemoveCard(BankData card, int position) {
        mPresenter.confirmAndRemoveBank(card);
    }

    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState != RecyclerView.SCROLL_STATE_SETTLING) {
                return;
            }

            if (mAdapter != null) {
                mAdapter.closeAllItems();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    @Override
    public void showConfirmDialogAfterLinkBank(String message) {
        DialogHelper.showNoticeDialog(getActivity(),
                message,
                getString(R.string.btn_continue),
                getString(R.string.btn_cancel_transaction),
                new ZPWOnEventConfirmDialogListener() {
                    @Override
                    public void onCancelEvent() {
                        finish(Constants.RESULT_END_PAYMENT);
                    }

                    @Override
                    public void onOKEvent() {
                        finish(Activity.RESULT_OK);
                    }
                });
    }

    void finish(int result) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        getActivity().setResult(result);
        getActivity().finish();
    }
}