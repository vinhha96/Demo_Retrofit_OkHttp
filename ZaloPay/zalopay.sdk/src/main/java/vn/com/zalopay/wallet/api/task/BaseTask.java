package vn.com.zalopay.wallet.api.task;

import android.os.Build;
import android.util.ArrayMap;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import vn.com.zalopay.wallet.api.ServiceManager;
import vn.com.zalopay.wallet.business.entity.user.UserInfo;
import vn.com.zalopay.wallet.objectmanager.SingletonBase;
import vn.com.zalopay.wallet.controller.SDKApplication;

public abstract class BaseTask<T> extends SingletonBase {
    public UserInfo mUserInfo;
    public EventBus mEventBus;
    protected Map<String, String> mDataParams;

    public BaseTask(UserInfo pUserInfo) {
        super();
        mUserInfo = pUserInfo;
        mEventBus = SDKApplication.getApplicationComponent().eventBus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDataParams = new ArrayMap<>();
        } else {
            mDataParams = new HashMap<>();
        }
    }

    public abstract void onDoTaskOnResponse(T pResponse);//do something after finishing request(save response to cache...)

    public abstract void onRequestSuccess(T pResponse);

    public abstract void onRequestFail(Throwable e);

    public abstract void onRequestInProcess();

    public abstract String getDefaulErrorNetwork();

    protected abstract void doRequest();

    protected abstract boolean doParams();

    public void makeRequest() {
        if (doParams()) {
            doRequest();
        }
    }

    protected Map<String, String> getDataParams() {
        return mDataParams;
    }

    protected ServiceManager shareDataRepository() {
        return ServiceManager.shareInstance();
    }

    protected ServiceManager newDataRepository() {
        return ServiceManager.newInstance();
    }
}
