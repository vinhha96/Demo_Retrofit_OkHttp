package vn.com.vng.zalopay.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import vn.com.vng.zalopay.ui.fragment.SubIntroSaveCardFragment;

/**
 * Created by longlv on 9/11/2016.
 * Adapter of IntroAppFragment
 */
public class IntroAppPagerAdapter extends FragmentStatePagerAdapter {

    private List<Integer> mResources;

    public IntroAppPagerAdapter(FragmentManager fm, List<Integer> resources) {
        super(fm);
        this.mResources = resources;
    }

    @Override
    public Fragment getItem(int position) {
        return SubIntroSaveCardFragment.newInstance(this.mResources.get(position));
    }

    @Override
    public int getCount() {
        return mResources.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}