package vn.com.vng.zalopay.react.iap;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.zalopay.apploader.network.NetworkService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.domain.repository.ZaloPayIAPRepository;

/**
 * Created by AnhHieu on 5/16/16.
 * In-app-payment package
 */
public class ReactIAPPackage implements ReactPackage {
    final ZaloPayIAPRepository zaloPayIAPRepository;
    final IPaymentService paymentService;
    final User user;
    private final long appId;
    final NetworkService netwokService;

    public ReactIAPPackage(ZaloPayIAPRepository zaloPayIAPRepository,
                           IPaymentService paymentService,
                           User user, long appId, NetworkService netwokService) {
        this.zaloPayIAPRepository = zaloPayIAPRepository;
        this.paymentService = paymentService;
        this.user = user;
        this.appId = appId;
        this.netwokService = netwokService;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new ZaloPayNativeModule(reactContext, paymentService, appId, netwokService));
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
