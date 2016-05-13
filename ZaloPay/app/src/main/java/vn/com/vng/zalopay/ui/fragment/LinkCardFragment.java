package vn.com.vng.zalopay.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import vn.com.vng.zalopay.Constants;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.domain.model.BankCard;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.ui.activity.LinkCardActivity;
import vn.com.vng.zalopay.ui.adapter.LinkCardAdapter;
import vn.com.vng.zalopay.ui.presenter.LinkCardPresenter;
import vn.com.vng.zalopay.ui.view.ILinkCardView;
import vn.com.vng.zalopay.utils.AndroidUtils;
import vn.zing.pay.zmpsdk.merchant.CShareData;

/**
 * Created by AnhHieu on 5/10/16.
 */
public class LinkCardFragment extends BaseFragment implements ILinkCardView, LinkCardAdapter.OnClickBankCardListener {


    public static LinkCardFragment newInstance() {

        Bundle args = new Bundle();

        LinkCardFragment fragment = new LinkCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_link_card;
    }

    @Inject
    Navigator navigator;

    @Bind(R.id.listview)
    RecyclerView recyclerView;

    private LinkCardAdapter mAdapter;

    @Inject
    LinkCardPresenter presenter;

    @Bind(R.id.progressContainer)
    View mLoadingView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new LinkCardAdapter(getContext(), this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        recyclerView.addItemDecoration(new SpacesItemDecoration(AndroidUtils.dp(12), AndroidUtils.dp(8)));
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.getListCard();
    }

    @Override
    public void onDestroyView() {
        presenter.destroyView();
        super.onDestroyView();
    }

    @Override
    public void setData(List<BankCard> bankCards) {
        mAdapter.setData(bankCards);
    }


    @Override
    public void updateData(BankCard bankCard) {
        mAdapter.insert(bankCard);
    }

    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void onClickAddBankCard() {
        navigator.startLinkCardProducedureActivity(this);
    }

    @Override
    public void onClickMenu(BankCard bankCard) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LinkCardActivity.REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                String carname = bundle.getString(Constants.CARDNAME);
                String first6CardNo = bundle.getString(Constants.FIRST6CARDNO);
                String last4CardNo = bundle.getString(Constants.LAST4CARDNO);
                String bankcode =  bundle.getString(Constants.BANKCODE);
                long expiretime  = bundle.getLong(Constants.EXPIRETIME);
                BankCard bankCard = new BankCard(carname, first6CardNo, last4CardNo, bankcode, expiretime);
                bankCard.type = CShareData.getInstance().detectCardType(first6CardNo).toString();
                updateData(bankCard);
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int spaceHorizontal;
        private int spaceVertical;

        public SpacesItemDecoration(int spaceHorizontal, int spaceVertical) {
            this.spaceHorizontal = spaceHorizontal;
            this.spaceVertical = spaceVertical;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            outRect.left = spaceHorizontal;
            outRect.right = spaceHorizontal;
            outRect.bottom = spaceVertical;
            outRect.top = spaceVertical;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 2 * spaceVertical;
            }

        }
    }


  /*  public static class BottomSheetLinkCard extends BottomSheetDialogFragment {

        public BottomSheetLinkCard() {
        }

        private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };

        @Override
        public void setupDialog(Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View contentView = View.inflate(getContext(), R.layout.bottom_sheet_link_card_layout, null);
            dialog.setContentView(contentView);

            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();

            if (behavior != null && behavior instanceof BottomSheetBehavior) {
                ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            }
        }

    }*/

}
