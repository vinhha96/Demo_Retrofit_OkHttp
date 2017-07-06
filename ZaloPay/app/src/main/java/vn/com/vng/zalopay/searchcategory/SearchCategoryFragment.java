package vn.com.vng.zalopay.searchcategory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.zalopay.ui.widget.IconFont;
import com.zalopay.ui.widget.dialog.listener.ZPWOnEventConfirmDialogListener;
import com.zalopay.ui.widget.edittext.ZPEditText;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.domain.model.InsideApp;
import vn.com.vng.zalopay.domain.model.ZPProfile;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.utils.AndroidUtils;
import vn.com.vng.zalopay.utils.DialogHelper;

/**
 * Created by khattn on 3/10/17.
 * Search Category Fragment
 */

public class SearchCategoryFragment extends BaseFragment implements ISearchCategoryView, TextWatcher,
        SearchResultAdapter.OnModelClickListener, CommonAppAdapter.OnModelClickListener {

    public static SearchCategoryFragment newInstance(Bundle bundle) {
        SearchCategoryFragment fragment = new SearchCategoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.fragment_search_category;
    }

    private final static int SPAN_COUNT_APPLICATION = 3;
    private final static int PADDING_TEXT = 46;
    @BindView(R.id.listView)
    RecyclerView mRecyclerView;

    @BindView(R.id.layoutNoResultFound)
    View layoutNoResult;

    @BindView(R.id.search_result_emty)
    TextView mTextSearchEmpty;

    @Inject
    SearchCategoryPresenter mPresenter;

    private IconFont mSearchIcon;

    private CommonAppAdapter mAdapter;
    private SearchResultAdapter mResultAdapter;

    private GridLayoutManager mGridLayoutManager;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchItemDecoration mItemDecoration;
    private AppTypicalItemDecoration mAppTypicalItemDecoration;
    private ZPEditText mEdtSearchView;

  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mAdapter = new CommonAppAdapter();
        mAdapter.setSpanCount(SPAN_COUNT_APPLICATION);
        mResultAdapter = new SearchResultAdapter(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.attachView(this);
        mAppTypicalItemDecoration = new AppTypicalItemDecoration();
        mItemDecoration = new SearchItemDecoration(AndroidUtils.dp(PADDING_TEXT));
        mGridLayoutManager = new GridLayoutManager(getContext(), SPAN_COUNT_APPLICATION);
        mGridLayoutManager.setSpanSizeLookup(mAdapter.getSpanSizeLookup());
        mLayoutManager = new LinearLayoutManager(getContext());
        setCommonRecyclerView();
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setItemAnimator(null);
        mSearchIcon = (IconFont) getActivity().findViewById(R.id.ifSearch);
        mSearchIcon.setIcon(R.string.general_search);
        mEdtSearchView = (ZPEditText) getActivity().findViewById(R.id.edtSearch);
        mEdtSearchView.addTextChangedListener(this);
    }

    private void setCommonRecyclerView() {
        if (mRecyclerView.getAdapter() == mAdapter) {
            return;
        }

        mAdapter.setClickListener(this);

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.removeItemDecoration(mItemDecoration);
        mRecyclerView.addItemDecoration(mAppTypicalItemDecoration);
        mRecyclerView.swapAdapter(mAdapter, false);
    }

    private void setResultRecyclerView() {
        if (mRecyclerView.getAdapter() == mResultAdapter) {
            return;
        }

        mResultAdapter.setClickListener(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(mItemDecoration);
        mRecyclerView.removeItemDecoration(mAppTypicalItemDecoration);
        mRecyclerView.swapAdapter(mResultAdapter, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.getListAppResource();
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
        showToast(message);
    }

    @Override
    public void onDestroyView() {
        mRecyclerView.setAdapter(null);
        mPresenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mPresenter.destroy();
        super.onDestroy();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mSearchIcon.setVisibility(TextUtils.isEmpty(s) ? View.VISIBLE : View.GONE);
        mPresenter.filter(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void showResultView(boolean noResult, boolean hasResult) {
        if (layoutNoResult == null) {
            return;
        }

        if (noResult) {
            mRecyclerView.setVisibility(View.GONE);
            layoutNoResult.setVisibility(View.VISIBLE);
            String message = getString(R.string.search_no_result, mEdtSearchView.getText());
            mTextSearchEmpty.setText(message);
            return;
        }

        mRecyclerView.setVisibility(View.VISIBLE);
        layoutNoResult.setVisibility(View.GONE);
        if (hasResult) {
            setResultRecyclerView();
        } else {
            setCommonRecyclerView();
        }
    }

    @Override
    public void refreshInsideApps(List<InsideApp> list) {
        Timber.d("refreshInsideApps list: [%s]", list.size());
        mAdapter.setAppItem(list);
    }

    @Override
    public void onClickAppItem(InsideApp app, int position) {
        mPresenter.handleLaunchApp(app);
    }

    @Override
    public void onClickSeeMoreApp() {
        mResultAdapter.showAllApp();
    }

    @Override
    public void onClickFriendItem(ZPProfile app, int position) {

    }

    @Override
    public void onClickSeeMoreFriend() {
        mResultAdapter.showAllFriend();
    }

    @Override
    public void showConfirmDialog(String message,
                                  String btnConfirm,
                                  String btnCancel,
                                  ZPWOnEventConfirmDialogListener listener) {
        DialogHelper.showNoticeDialog(getActivity(), message, btnConfirm, btnCancel, listener);
    }

    @Override
    public void setFindResult(List<InsideApp> apps, List<ZPProfile> friends, String key) {
        mResultAdapter.setResult(apps, friends, key);
    }
}