package vn.com.vng.zalopay.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.BindView;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.menu.listener.MenuItemClickListener;
import vn.com.vng.zalopay.menu.model.MenuItem;
import vn.com.vng.zalopay.menu.ui.adapter.MenuItemAdapter;
import vn.com.vng.zalopay.menu.utils.MenuItemUtil;
import vn.com.vng.zalopay.ui.presenter.LeftMenuPresenter;
import vn.com.vng.zalopay.ui.view.ILeftMenuView;
import vn.com.vng.zalopay.utils.CurrencyUtil;

/**
 * Created by AnhHieu on 5/10/16.
 */
public class LeftMenuFragment extends BaseFragment implements AdapterView.OnItemClickListener, ILeftMenuView {

    public LeftMenuFragment() {
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected int getResLayoutId() {
        return R.layout.left_menu_layout;
    }

    private MenuItemAdapter mAdapter;

    @BindView(android.R.id.list)
    ListView listView;

    public ImageView imageAvatar;

    public TextView tvName;

    public TextView tvBalance;

    private MenuItemClickListener mMenuListener;

    @Inject
    LeftMenuPresenter presenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MenuItemClickListener) {
            mMenuListener = (MenuItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnClickMenuItemListener");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MenuItemAdapter(getContext(), MenuItemUtil.getMenuItems());
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
        addHeader(listView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        presenter.initialize();
    }

    private void addHeader(ListView listView) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.nav_header_main, listView, false);
        imageAvatar = (ImageView) view.findViewById(R.id.im_avatar);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvBalance = (TextView) view.findViewById(R.id.tv_balance);
        listView.addHeaderView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.getBalance();
    }


    @Override
    public void onDestroyView() {
        presenter.destroyView();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMenuListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            return; // Header
        }

        MenuItem item = mAdapter.getItem(position - 1);
        if (item == null) return;

        if (mMenuListener != null) {
            mMenuListener.onMenuItemClick(item);
        }

    }


    public void setUserInfo(User user) {
        if (user == null) return;

        setAvatar(user.avatar);
        setDisplayName(user.dname);
    }

    private void loadImage(final ImageView imageView, String url) {
        Glide.with(this).load(url)
                .placeholder(R.color.silver)
                .centerCrop()
                .into(imageView);
    }

    public void setBalance(long balance) {
        tvBalance.setText(CurrencyUtil.formatCurrency(balance, false));
    }

    @Override
    public void setAvatar(String avatar) {
        loadImage(imageAvatar, avatar);
    }

    @Override
    public void setDisplayName(String displayName) {
        tvName.setText(displayName);
    }


}
