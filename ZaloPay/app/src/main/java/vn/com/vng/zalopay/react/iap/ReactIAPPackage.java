package vn.com.vng.zalopay.react.iap;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.zalopay.apploader.ReactNativeHostable;
import com.zalopay.apploader.network.NetworkService;
import com.zalopay.apploader.picker.ReactDialogPickerManager;
import com.zalopay.apploader.picker.ReactDropdownPickerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.navigation.Navigator;
import vn.com.vng.zalopay.react.widget.icon.ReactIconTextViewManager;

/**
 * Created by AnhHieu on 5/16/16.
 * In-app-payment package
 */
public class ReactIAPPackage implements ReactPackage {
    private final IPaymentService paymentService;
    private final User mUser;
    private final long appId;
    private final NetworkService mNetworkServiceWithRetry;
    private final Navigator mNavigator;
    private ReactNativeHostable mNativeHost;

    public ReactIAPPackage(IPaymentService paymentService,
                           User user, long appId,
                           NetworkService networkServiceWithRetry,
                           Navigator navigator,
                           ReactNativeHostable nativeHost) {
        this.paymentService = paymentService;
        this.mUser = user;
        this.appId = appId;
        this.mNetworkServiceWithRetry = networkServiceWithRetry;
        this.mNavigator = navigator;
        this.mNativeHost = nativeHost;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new ZaloPayNativeModule(reactContext, mUser, paymentService, appId,
                mNetworkServiceWithRetry, mNavigator));
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        List<ViewManager> viewManagers = new ArrayList<>();
        viewManagers.add(new ReactIconTextViewManager());
        viewManagers.add(new ReactDialogPickerManager(mNativeHost));
        viewManagers.add(new ReactDropdownPickerManager(mNativeHost));
        return viewManagers;
    }
}
