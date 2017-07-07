package com.zalopay.apploader;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.facebook.react.ReactInstanceManagerBuilder;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import timber.log.Timber;

/**
 * Created by huuhoa on 7/15/16.
 * Manage cached version of created ReactInstanceManager
 */
public class ReactNativeHostLongLife implements ReactNativeHostable {
    private Map<String, ReactInstanceManager> mInstance = new HashMap<>();
    private Map<String, Boolean> mNameMapping = new HashMap<>();
    private HashMap<String, WeakReference<Activity>> mWeakHashMapActivity = new HashMap<>();
    private String mCurrentClassActivity;

    public ReactNativeHostLongLife() {
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Timber.i("finalize");
    }

    @Override
    public ReactInstanceManager acquireReactInstanceManager(final ReactInstanceDelegate activity, LifecycleState initialLifecycleState) {
        if (activity == null) {
            return null;
        }

        String mapping = createMapping(activity);

        if (mInstance != null && mInstance.containsKey(mapping)) {
            Timber.i("reuse react instance manager");
//            mInstance.onHostResume(activity, activity);
            return mInstance.get(mapping);
        }

        Timber.i("create new react instance manager");
        ReactInstanceManagerBuilder builder = ReactInstanceManager.builder()
                .setApplication(activity.getApplication())
                .setJSMainModuleName(activity.getJSMainModuleName())
                .setUseDeveloperSupport(activity.getUseDeveloperSupport())
                .setInitialLifecycleState(initialLifecycleState)
                .setNativeModuleCallExceptionHandler(new HandleReactNativeException(this, activity));

        for (ReactPackage reactPackage : activity.getPackages()) {
            builder.addPackage(reactPackage);
        }

        String jsBundleFile = activity.getJSBundleFile();

        if (jsBundleFile != null) {
            builder.setJSBundleFile(jsBundleFile);
        } else {
            builder.setBundleAssetName(activity.getBundleAssetName());
        }

        mInstance.put(mapping, builder.build());
        markMappingInUsed(mapping);
        return mInstance.get(mapping);
    }

    @Override
    public void releaseReactInstanceManager(ReactInstanceDelegate activity, ReactInstanceManager instance, boolean forceRemove) {
        if (mInstance == null) {
            return;
        }

        Timber.i("release react instance manager");
//        final ReactBasedActivity activity = activityReference.get();
        if (activity == null) {
            return;
        }

        String mapping = getAvailableMapping(activity);

        if (!mInstance.containsKey(mapping)) {
            return;
        }

        if (instance == null) {
            return;
        }

        //  instance.onHostDestroy(activity);
        markMappingAvailable(mapping);

        if (forceRemove) {
            instance.destroy();
            mInstance.remove(mapping);
        }
    }

    private void removeInstance(ReactInstanceDelegate activity) {
        if (mInstance == null) {
            return;
        }

        Timber.i("release react instance manager");
//        final ReactBasedActivity activity = activityReference.get();
        if (activity == null) {
            return;
        }

        String mapping = getAvailableMapping(activity);

        if (!mInstance.containsKey(mapping)) {
            return;
        }

        ReactInstanceManager i = mInstance.get(mapping);
        if (i == null) {
            return;
        }

//        if (activity.mReactRootView != null) {
//            i.detachRootView(activity.mReactRootView);
//        }
//
//        i.onHostDestroy();
        mInstance.remove(mapping);
        markMappingAvailable(mapping);
    }

    @NonNull
    private String createMapping(ReactInstanceDelegate activity) {
        String mapping = activity.getJSBundleFile();
        if (mapping == null) {
            mapping = "NULL";
        }

        String alternateMapping = mapping + activity.toString();
        if (mNameMapping.containsKey(alternateMapping)) {
            Timber.d("Alternate mapping: %s", alternateMapping);
            return alternateMapping;
        }

        if (mNameMapping.containsKey(mapping)) {
            Timber.d("Default mapping is in used: %s", alternateMapping);
            return alternateMapping;
        }

        Timber.d("Default mapping: %s", mapping);
        return mapping;
    }

    private String getAvailableMapping(ReactInstanceDelegate activity) {
        String mapping = activity.getJSBundleFile();
        if (mapping == null) {
            mapping = "NULL";
        }

        String alternateMapping = mapping + activity.toString();
        if (mNameMapping.containsKey(alternateMapping)) {
            Timber.d("Alternate mapping: %s", alternateMapping);
            return alternateMapping;
        }

        Timber.d("Default mapping: %s", mapping);
        return mapping;
    }

    private void markMappingInUsed(String mapping) {
        mNameMapping.put(mapping, Boolean.TRUE);
    }

    private void markMappingAvailable(String mapping) {
        mNameMapping.remove(mapping);
    }

    @Override
    public void handleJSException(ReactInstanceDelegate activity, Exception e) {
        Timber.e(e, "Exception! Should not happen with production build");
        if (activity == null) {
            return;
        }

        removeInstance(activity);
        activity.handleException(e);
    }

    @Override
    public Context getActivityContext() {

        Timber.d("getActivityContext: %s", mCurrentClassActivity);

        if (TextUtils.isEmpty(mCurrentClassActivity)) {
            return null;
        }

        Activity activity = getActivity(mCurrentClassActivity);
        Timber.d("activity in cache [%s]", activity);
        return activity;
    }

    @Override
    public void activeCurrentActivity(Activity activity) {
        Timber.d("activeCurrentActivity: %s", activity);
        mCurrentClassActivity = activity.getClass().getSimpleName();
    }

    @Override
    public void setActivityContext(Activity activity) {
        Timber.d("setActivityContext: %s", activity);
        putActivity(activity.getClass().getSimpleName(), activity);
    }

    @Override
    public void destroyActivityContext(Activity activity) {
        Timber.d("destroyActivityContext: %s", activity);
        removeActivity(activity.getClass().getSimpleName());
    }

    @Override
    public void cleanup() {
        Timber.d("cleanup");
        try {
            for (ReactInstanceManager manager : mInstance.values()) {
                manager.destroy();
            }
            mInstance.clear();
            mWeakHashMapActivity.clear();
        } catch (Exception e) {
            Timber.w(e, "Error on cleanup of ReactNativeInstanceManagerLongLife");
        }
    }

    private Activity getActivity(String key) {
        WeakReference<Activity> weakRef = mWeakHashMapActivity.get(key);
        if (weakRef == null) {
            return null;
        }

        Activity result = weakRef.get();
        if (result == null) {
            mWeakHashMapActivity.remove(key);
        }
        return result;
    }

    private void putActivity(String key, Activity value) {
        mWeakHashMapActivity.put(key, new WeakReference<>(value));
    }

    private void removeActivity(String key) {
        mWeakHashMapActivity.remove(key);
    }
}
