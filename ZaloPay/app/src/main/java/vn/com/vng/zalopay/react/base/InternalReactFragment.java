package vn.com.vng.zalopay.react.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.beefe.picker.PickerViewPackage;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.learnium.RNDeviceInfo.RNDeviceInfo;
import com.zalopay.apploader.BundleReactConfig;
import com.zalopay.apploader.ReactBaseFragment;
import com.zalopay.apploader.ReactNativeHostable;

import org.greenrobot.eventbus.Subscribe;
import org.pgsqlite.SQLitePluginPackage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.event.UncaughtRuntimeExceptionEvent;
import vn.com.vng.zalopay.internal.di.components.UserComponent;
import vn.com.vng.zalopay.react.ReactInternalPackage;
import vn.com.vng.zalopay.user.UserBaseActivity;
import vn.com.zalopay.analytics.ZPAnalytics;
import vn.com.zalopay.analytics.ZPScreens;

/**
 * Created by hieuvm on 2/22/17.
 * *
 */

public class InternalReactFragment extends ReactBaseFragment {

    public static final String TAG = "InternalReactFragment";

    private static final String ARG_MODULE_NAME = "moduleName";
    private static final String ARG_LAUNCH_OPTIONS = "launchOptions";

    public static InternalReactFragment newInstance(String moduleName) {
        return newInstance(moduleName, new HashMap<>());
    }

    public static InternalReactFragment newInstance(String moduleName, HashMap<String, String> launchOptions) {

        Bundle options = new Bundle();
        for (Map.Entry<String, String> e : launchOptions.entrySet()) {
            options.putString(e.getKey(), e.getValue());
        }

        Bundle args = new Bundle();
        args.putString(ARG_MODULE_NAME, moduleName);
        args.putBundle(ARG_LAUNCH_OPTIONS, options);

        InternalReactFragment fragment = new InternalReactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupFragmentComponent() {
        getUserComponent().inject(this);
    }

    @Override
    public boolean getUseDeveloperSupport() {
        return bundleReactConfig.isInternalDevSupport();
    }

    @Override
    public List<ReactPackage> getPackages() {
        return Arrays.asList(
                new MainReactPackage(),
                reactInternalPackage(),
                new SQLitePluginPackage(),
                new RNDeviceInfo(),
                new PickerViewPackage());
    }

    protected ReactPackage reactInternalPackage() {
        return mReactInternalPackage;
    }

    @Override
    protected String getMainComponentName() {
        return mModuleName;
    }

    @Inject
    BundleReactConfig bundleReactConfig;

    @Inject
    ReactNativeHostable mReactNativeHostable;

    @Inject
    ReactInternalPackage mReactInternalPackage;

    @Inject
    User mUser;

    private Bundle mLaunchOptions = null;
    private String mModuleName;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArgs(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        nativeInstanceManager().activeCurrentActivity(getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startReactApplication();
    }

    public UserComponent getUserComponent() {
        UserComponent userComponent = AndroidApplication.instance().getUserComponent();
        if (userComponent != null) {
            return userComponent;
        }

        if (!(getContext() instanceof UserBaseActivity)) {
            throw new IllegalStateException("This activity isn't an instance of UserBaseActivity");
        }

        userComponent = ((UserBaseActivity) getContext()).getUserComponent();

        if (userComponent == null) {
            throw new IllegalStateException("UserComponent already release");
        }

        return userComponent;

    }

    protected void initArgs(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            mLaunchOptions = bundle.getBundle(ARG_LAUNCH_OPTIONS);
            mModuleName = bundle.getString(ARG_MODULE_NAME);
        } else {
            mLaunchOptions = savedInstanceState.getBundle(ARG_LAUNCH_OPTIONS);
            mModuleName = savedInstanceState.getString(ARG_MODULE_NAME);
        }

        if (mLaunchOptions == null) {
            mLaunchOptions = new Bundle();
        }

    }

    @Override
    public Bundle getLaunchOptions() {
        mLaunchOptions.putString("zalopay_userid", mUser.zaloPayId);
        return mLaunchOptions;
    }

    @Nullable
    @Override
    public String getJSBundleFile() {
        return bundleReactConfig.getInternalJsBundle();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(ARG_LAUNCH_OPTIONS, mLaunchOptions);
        outState.putString(ARG_MODULE_NAME, mModuleName);
    }

    @Override
    public void onDetach() {
        mCompositeSubscription.clear();
        super.onDetach();
    }

    @Override
    protected ReactNativeHostable nativeInstanceManager() {
        return mReactNativeHostable;
    }

    @Subscribe
    public void onUncaughtRuntimeException(UncaughtRuntimeExceptionEvent event) {
        reactInstanceCaughtError();
        handleException(event.getInnerException());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ZPAnalytics.trackScreen(ZPScreens.TRANSACTIONLOG);
        }
    }
}
