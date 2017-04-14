package vn.com.vng.zalopay.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import javax.inject.Inject;

import butterknife.BindView;
import vn.com.vng.zalopay.R;
import vn.com.vng.zalopay.react.base.AbstractReactActivity;
import vn.com.vng.zalopay.react.base.HomePagerAdapter;
import vn.com.vng.zalopay.ui.fragment.BaseFragment;
import vn.com.vng.zalopay.ui.presenter.HomePresenter;
import vn.com.vng.zalopay.ui.view.IHomeView;
import vn.com.vng.zalopay.ui.widget.BottomNavigationDrawable;
import vn.com.vng.zalopay.utils.AndroidUtils;
import vn.com.vng.zalopay.utils.BottomNavigationViewHelper;
import vn.com.vng.zalopay.utils.DialogHelper;
import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPEvents;

public class HomeActivity extends AbstractReactActivity implements IHomeView {

    public static final String TAG = "HomeActivity";

    public static boolean EVENT_FIRST_TOUCH_HOME = true;
    public static boolean EVENT_FIRST_TOUCH_NEARBY = true;
    public static boolean EVENT_FIRST_TOUCH_PROMOTION = true;
    public static boolean EVENT_FIRST_TOUCH_ME = true;

    @Inject
    HomePresenter mPresenter;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.navigation)
    BottomNavigationView mBottomNavigationView;

    // TODO: 4/4/17 - longlv: hardcode for test.
    private boolean mShowIconNewPromotion = true;
    private View mIconNewPromotion;

    @Override
    protected int getResLayoutId() {
        return R.layout.activity_home_new;
    }

    @Override
    public Fragment getReactFragment() {
        return null;
    }

    @Override
    public BaseFragment getFragmentToHost() {
        return null;
    }

    @Override
    protected void setupActivityComponent() {
        getUserComponent().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.attachView(this);
        mPresenter.initialize();

        HomePagerAdapter mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mViewPager.getLayoutParams();
        params.setMargins(0, getStatusBarHeight(), 0, (int) AndroidUtils.dpToPixels(this, 56));

        mViewPager.setAdapter(mHomePagerAdapter);
        mViewPager.setOffscreenPageLimit(mHomePagerAdapter.getCount() - 1);

        updateIconFontState(HomePagerAdapter.TAB_MAIN_INDEX);

        //Use this function to show titles of all menu elements when bottomNavigationBar has 4 tabs.
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);

        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    updateIconFontState(HomePagerAdapter.TAB_MAIN_INDEX);
                    mViewPager.setCurrentItem(HomePagerAdapter.TAB_MAIN_INDEX);
                    break;
                case R.id.menu_nearby:
                    updateIconFontState(HomePagerAdapter.TAB_SHOW_SHOW_INDEX);
                    mViewPager.setCurrentItem(HomePagerAdapter.TAB_SHOW_SHOW_INDEX);
                    break;
                case R.id.menu_promotion:
                    updateIconFontState(HomePagerAdapter.TAB_PROMOTION_INDEX);
                    mViewPager.setCurrentItem(HomePagerAdapter.TAB_PROMOTION_INDEX);
                    break;
                case R.id.menu_profile:
                    updateIconFontState(HomePagerAdapter.TAB_PERSONAL_INDEX);
                    mViewPager.setCurrentItem(HomePagerAdapter.TAB_PERSONAL_INDEX);
                    break;
            }
            return true;
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                trackEvent(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        changeBottomNavigationLayout();
    }

    private void changeBottomNavigationLayout() {
        int paddingBottom = (int) getResources().getDimension(R.dimen.spacing_tiny_s);
        View tabHome = mBottomNavigationView.findViewById(R.id.menu_home);
        tabHome.findViewById(android.support.design.R.id.icon).setPadding(0, 0, 0, paddingBottom);
        View tabNearby = mBottomNavigationView.findViewById(R.id.menu_nearby);
        tabNearby.findViewById(android.support.design.R.id.icon).setPadding(0, 0, 0, paddingBottom);
        View tabPromotion = mBottomNavigationView.findViewById(R.id.menu_promotion);
        tabPromotion.findViewById(android.support.design.R.id.icon).setPadding(0, 0, 0, paddingBottom);
        View tabProfile = mBottomNavigationView.findViewById(R.id.menu_profile);
        tabProfile.findViewById(android.support.design.R.id.icon).setPadding(0, 0, 0, paddingBottom);
    }

    private void trackEvent(int position) {
        switch (position) {
            case HomePagerAdapter.TAB_MAIN_INDEX:
                if(EVENT_FIRST_TOUCH_HOME) {
                    ZPAnalytics.trackEvent(ZPEvents.TOUCHTABHOMEFIRST);
                    EVENT_FIRST_TOUCH_HOME = false;
                }
                ZPAnalytics.trackEvent(ZPEvents.TOUCHTABHOME);
                break;
            case HomePagerAdapter.TAB_SHOW_SHOW_INDEX:
                if(EVENT_FIRST_TOUCH_NEARBY) {
                    ZPAnalytics.trackEvent(ZPEvents.TOUCHTABNEARBYFIRST);
                    EVENT_FIRST_TOUCH_NEARBY = false;
                }
                ZPAnalytics.trackEvent(ZPEvents.TOUCHTABNEARBY);
                break;
            case HomePagerAdapter.TAB_PROMOTION_INDEX:
                if(EVENT_FIRST_TOUCH_PROMOTION) {
                    ZPAnalytics.trackEvent(ZPEvents.TOUCHTABPROMOTIONFIRST);
                    EVENT_FIRST_TOUCH_PROMOTION = false;
                }
                ZPAnalytics.trackEvent(ZPEvents.TOUCHTABPROMOTION);
                break;
            case HomePagerAdapter.TAB_PERSONAL_INDEX:
                if(EVENT_FIRST_TOUCH_ME) {
                    ZPAnalytics.trackEvent(ZPEvents.TOUCHTABMEFIRST);
                    EVENT_FIRST_TOUCH_ME = false;
                }
                ZPAnalytics.trackEvent(ZPEvents.TOUCHTABME);
                break;
        }
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
    protected void onDestroy() {
        mPresenter.detachView();
        mPresenter.destroy();
        super.onDestroy();
    }

    private void updateIconFontState(int currentIndex) {
        Menu menu = mBottomNavigationView.getMenu();
        switch (currentIndex) {
            case HomePagerAdapter.TAB_MAIN_INDEX:
                setIconTabMain(menu, true);
                setIconTabShowShow(menu, false);
                setIconTabPromotion(menu, false);
                setIconTabProfile(menu, false);
                break;
            case HomePagerAdapter.TAB_SHOW_SHOW_INDEX:
                setIconTabMain(menu, false);
                setIconTabShowShow(menu, true);
                setIconTabPromotion(menu, false);
                setIconTabProfile(menu, false);
                break;
            case HomePagerAdapter.TAB_PROMOTION_INDEX:
                setIconTabMain(menu, false);
                setIconTabShowShow(menu, false);
                setIconTabPromotion(menu, true);
                setIconTabProfile(menu, false);
                break;
            case HomePagerAdapter.TAB_PERSONAL_INDEX:
                setIconTabMain(menu, false);
                setIconTabShowShow(menu, false);
                setIconTabPromotion(menu, false);
                setIconTabProfile(menu, true);
                break;
        }
    }

    private void setIconTabMain(Menu menu, boolean isActive) {
        BottomNavigationDrawable iconFontDrawable;
        if (isActive) {
            iconFontDrawable = new BottomNavigationDrawable(this)
                    .setIcon(R.string.tab_home_active)
                    .setResourcesColor(R.color.colorPrimary)
                    .setResourcesSize(R.dimen.font_size_tab_icon);
        } else {
            iconFontDrawable = new BottomNavigationDrawable(this)
                    .setIcon(R.string.tab_home)
                    .setResourcesColor(R.color.txt_item_sub)
                    .setResourcesSize(R.dimen.font_size_tab_icon);
        }

        menu.getItem(HomePagerAdapter.TAB_MAIN_INDEX).setIcon(iconFontDrawable);
    }

    private void setIconTabShowShow(Menu menu, boolean isActive) {
        BottomNavigationDrawable iconFontDrawable;
        if (isActive) {
            iconFontDrawable = new BottomNavigationDrawable(this)
                    .setIcon(R.string.tab_showshow_active)
                    .setResourcesColor(R.color.colorPrimary)
                    .setResourcesSize(R.dimen.font_size_tab_icon);
        } else {
            iconFontDrawable = new BottomNavigationDrawable(this)
                    .setIcon(R.string.tab_showshow)
                    .setResourcesColor(R.color.txt_item_sub)
                    .setResourcesSize(R.dimen.font_size_tab_icon);
        }
        menu.getItem(HomePagerAdapter.TAB_SHOW_SHOW_INDEX).setIcon(iconFontDrawable);
    }

    private void setIconTabPromotion(Menu menu, boolean isActive) {
        BottomNavigationDrawable iconFontDrawable;
        FrameLayout tabPromotion = (FrameLayout) mBottomNavigationView.findViewById(R.id.menu_promotion);
        if (isActive) {
            iconFontDrawable = new BottomNavigationDrawable(this)
                    .setIcon(R.string.tab_promotion_active)
                    .setResourcesColor(R.color.colorPrimary)
                    .setResourcesSize(R.dimen.font_size_tab_icon);
            if (mShowIconNewPromotion) {
                mShowIconNewPromotion = false;
                removeIconNew(tabPromotion);
            }
        } else {
            iconFontDrawable = new BottomNavigationDrawable(this)
                    .setIcon(R.string.tab_promotion)
                    .setResourcesColor(R.color.txt_item_sub)
                    .setResourcesSize(R.dimen.font_size_tab_icon);
            if (mShowIconNewPromotion) {
                addIconNew(tabPromotion);
            }
        }
        menu.getItem(HomePagerAdapter.TAB_PROMOTION_INDEX).setIcon(iconFontDrawable);
    }

    private void addIconNew(FrameLayout frameLayout) {
        if (mIconNewPromotion != null) {
            return;
        }
        mIconNewPromotion = getLayoutInflater().inflate(R.layout.icon_new, null);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        frameLayout.addView(mIconNewPromotion, -1, layoutParams);
    }

    private void removeIconNew(FrameLayout frameLayout) {
        if (frameLayout == null || mIconNewPromotion == null) {
            return;
        }
        frameLayout.removeView(mIconNewPromotion);
        mIconNewPromotion = null;
    }

    private void setIconTabProfile(Menu menu, boolean isActive) {
        BottomNavigationDrawable iconFontDrawable;
        if (isActive) {
            iconFontDrawable = new BottomNavigationDrawable(this)
                    .setIcon(R.string.tab_personal_active)
                    .setResourcesColor(R.color.colorPrimary)
                    .setResourcesSize(R.dimen.font_size_tab_icon);
        } else {
            iconFontDrawable = new BottomNavigationDrawable(this)
                    .setIcon(R.string.tab_personal)
                    .setResourcesColor(R.color.txt_item_sub)
                    .setResourcesSize(R.dimen.font_size_tab_icon);
        }
        menu.getItem(HomePagerAdapter.TAB_PERSONAL_INDEX).setIcon(iconFontDrawable);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void showLoading() {
        DialogHelper.showLoading(this);
    }

    @Override
    public void hideLoading() {
        DialogHelper.hideLoading();
    }

    @Override
    public void showError(String message) {
        DialogHelper.showNotificationDialog(this, message);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void refreshIconFont() {
        if (mViewPager != null) {
            updateIconFontState(mViewPager.getCurrentItem());
        }
    }
}
