package vn.com.vng.zalopay.bank.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zalopay.ui.widget.dialog.listener.ZPWOnEventConfirmDialogListener;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.data.util.Lists;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.zalopay.wallet.merchant.entities.ZPCard;

/**
 * Created by datnt10 on 5/25/17.
 * Fragment list bank support
 */

public class BankSupportSelectionFragment extends BaseFragment implements IBankSupportSelectionView
        , BankSupportSelectionAdapter.OnClickBankSupportListener {

    private BankSupportSelectionAdapter mAdapter;

    @BindView(R.id.bank_support_selection_list_bank)
    RecyclerView rcvListBankSupport;

    @BindView(R.id.bank_support_selection_dash_line)
    View mDashLine;

    @Inject
    BankSupportSelectionPresenter presenter;

    public static BankSupportSelectionFragment newInstance() {
        Bundle args = new Bundle();
        BankSupportSelectionFragment fragment = new BankSupportSelectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_bank_support_selection;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        presenter.initData(getArguments());

        mAdapter = new BankSupportSelectionAdapter(getContext(), this);

        rcvListBankSupport.setHasFixedSize(true);
        rcvListBankSupport.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcvListBankSupport.setNestedScrollingEnabled(false);
        //mBankRecyclerView.addItemDecoration(new GridSpacingItemDecoration(COLUMN_COUNT, 2, false));
        rcvListBankSupport.setAdapter(mAdapter);
        rcvListBankSupport.setFocusable(false);

        presenter.getCardSupport();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    public void onPause() {
        presenter.pause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        presenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Override
    public void fetchListBank(List<ZPCard> cardSupportList) {
        if (!isAdded()) {
            Timber.d("Refresh Bank Supports error because fragment didn't add.");
            return;
        }
        hideProgressDialog();
        if (mAdapter == null) {
            Timber.d("Refresh Bank Supports error because adapter is null.");
            return;
        }

        if (Lists.isEmptyOrNull(cardSupportList)) {
            mAdapter.setData(Collections.emptyList());
            mDashLine.setVisibility(View.GONE);
        } else {
            List<ZPCard> sortedSupportList = sortList(cardSupportList);
            mAdapter.setData(sortedSupportList);
            mDashLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showRetryDialog(String retryMessage, ZPWOnEventConfirmDialogListener retryListener) {
        if (!isAdded()) {
            return;
        }
        super.showRetryDialog(retryMessage, retryListener);
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
    public void onClickBankSupportListener(ZPCard card, int position) {
        if(card.isBankAccount()) {
            presenter.linkAccount(card.getCardCode());
        } else {
            presenter.linkCard();
        }
    }

    List<ZPCard> sortList(List<ZPCard> list) {
        Collections.sort(list, (card1, card2) -> card1.getCardName().compareTo(card2.getCardName()));
        return list;
    }
}
