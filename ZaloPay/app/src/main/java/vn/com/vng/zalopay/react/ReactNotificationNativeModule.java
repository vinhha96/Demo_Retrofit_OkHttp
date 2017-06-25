package vn.com.vng.zalopay.react;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;

import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.data.eventbus.NotificationChangeEvent;
import vn.com.vng.zalopay.data.notification.NotificationStore;
import vn.com.vng.zalopay.data.ws.connection.NotificationApiHelper;
import vn.com.vng.zalopay.data.ws.connection.NotificationService;
import vn.com.vng.zalopay.data.ws.model.NotificationData;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.react.error.PaymentError;

/**
 * Created by huuhoa on 6/10/16.
 * Notification for react native
 */
class ReactNotificationNativeModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {

    private final NotificationStore.Repository mNotificationRepository;
    private final NotificationService mNotificationService;
    private final User mUser;
    private final EventBus mEventBus;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();


    ReactNotificationNativeModule(ReactApplicationContext reactContext, User user,
                                  NotificationStore.Repository notificationRepository,
                                  EventBus eventBus) {
        super(reactContext);
        this.mNotificationRepository = notificationRepository;
        this.mEventBus = eventBus;
        mUser = user;
        mNotificationService = AndroidApplication.instance().getUserComponent().notificationService();

        getReactApplicationContext().addLifecycleEventListener(this);
        getReactApplicationContext().addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "ZaloPayNotification";
    }

    @ReactMethod
    public void getNotification(int pageIndex, int count, Promise promise) {
        Timber.d("get notification : index [%s] count [%s]", pageIndex, count);
        Subscription subscription = mNotificationRepository.getNotification(pageIndex, count)
                .map(this::transform)
                .subscribeOn(Schedulers.io())
                .subscribe(new NotificationSubscriber(promise));

        mCompositeSubscription.add(subscription);
    }

    @ReactMethod
    public void updateStateReadWithNotificationId(String notificationid, Promise promise) {
        Timber.d("updateStateReadWithNotificationId : notifyId [%s]", notificationid);
        long notifyId;
        try {
            notifyId = Long.parseLong(notificationid);
        } catch (NumberFormatException e) {
            Helpers.promiseResolveError(promise, -1, "Arguments invalid");
            return;
        }

        Subscription subscription = mNotificationRepository.markAsRead(notifyId)
                .subscribeOn(Schedulers.io())
                .subscribe(new DefaultSubscriber<>());
        mCompositeSubscription.add(subscription);

        Helpers.promiseResolveSuccess(promise, null);
    }

    @ReactMethod
    public void removeNotification(String notificationId, Promise promise) {
        long notifyId;

        try {
            notifyId = Long.parseLong(notificationId);
        } catch (NumberFormatException e) {
            Timber.w(e);
            Helpers.promiseResolveError(promise, -1, "Arguments invalid");
            return;
        }

        Subscription subscriptionRemove = mNotificationRepository.getNotify(notifyId)
                .doOnNext(this::removeNotifyServer)
                .flatMap(notificationData -> mNotificationRepository.removeNotification(notifyId))
                .subscribeOn(Schedulers.io())
                .subscribe(new RemoveNotifySubscriber(promise));

        mCompositeSubscription.add(subscriptionRemove);
    }

    private void removeNotifyServer(NotificationData notification) {
        Timber.d("Remove notify : notifyId [%s] mtuid [%s]", notification.notificationId, notification.mtuid);
        if (notification.mtuid <= 0) {
            return;
        }

        mNotificationService.send(NotificationApiHelper.createDeleteNotifyMessage(notification.mtuid, Long.valueOf(mUser.zaloPayId)));
    }

    @ReactMethod
    public void removeAllNotification(Promise promise) {
        Subscription subscription = mNotificationRepository.removeAllNotification()
                .subscribeOn(Schedulers.io())
                .subscribe(new RemoveNotifySubscriber(promise));

        mCompositeSubscription.add(subscription);
    }

    private class NotificationSubscriber extends DefaultSubscriber<WritableArray> {

        WeakReference<Promise> promiseWeakReference;

        NotificationSubscriber(Promise promise) {
            promiseWeakReference = new WeakReference<>(promise);
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Timber.w(e, "error on getting notification logs");
            Helpers.promiseResolveError(promiseWeakReference.get(), PaymentError.ERR_CODE_FAIL.value(), "get notification error");
        }

        @Override
        public void onNext(WritableArray writableArray) {
            Timber.d("notification array %s", writableArray);
            Helpers.promiseResolveSuccess(promiseWeakReference.get(), writableArray);
        }
    }

    private WritableMap transform(NotificationData entity) {
        if (entity == null) {
            return null;
        }

        WritableMap item = Arguments.createMap();
        item.putDouble("transid", entity.transid);
        item.putDouble("appid", entity.appid);
        item.putDouble("timestamp", entity.timestamp);
        item.putString("message", entity.message);
        if (entity.embeddata != null && entity.embeddata.object != null) {
            item.putString("embeddata", entity.embeddata.object.toString());
        }
        item.putDouble("transtype", entity.transtype);
        item.putDouble("notificationtype", entity.notificationtype);
        item.putString("userid", entity.userid);
        item.putString("destuserid", entity.destuserid);
        item.putDouble("area", entity.area);
        item.putBoolean("unread", !entity.isRead());
        item.putString("notificationid", String.valueOf(entity.notificationId));
        return item;
    }

    private WritableArray transform(List<NotificationData> notificationEntities) {
        WritableArray result = Arguments.createArray();
        for (NotificationData entity : notificationEntities) {
            WritableMap item = transform(entity);
            if (item == null) {
                continue;
            }
            result.pushMap(item);
        }
        return result;
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Timber.d("requestCode %s resultCode %s ", requestCode, resultCode);
    }

    /**
     * Called when a new intent is passed to the activity
     *
     * @param intent
     */
    @Override
    public void onNewIntent(Intent intent) {
        Timber.d("onNewIntent called from based");
    }

    @Override
    public void onHostResume() {
        Timber.d("Activity onResume");
        registerEvent();
    }

    @Override
    public void onHostPause() {
        Timber.d("Activity onPause");
        unregisterEvent();
    }

    private void registerEvent() {
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
    }

    private void unregisterEvent() {
        if (mEventBus.isRegistered(this)) {
            mEventBus.unregister(this);
        }
    }

    @Override
    public void onHostDestroy() {
        Timber.d("Activity onDestroy");
        unsubscribeIfNotNull(mCompositeSubscription);

        getReactApplicationContext().removeActivityEventListener(this);
        getReactApplicationContext().removeLifecycleEventListener(this);
    }

    private void unsubscribeIfNotNull(CompositeSubscription subscription) {
        if (subscription != null) {
            subscription.clear();
        }
    }

    private void sendEvent(String eventName) {
        ReactApplicationContext reactContext = getReactApplicationContext();
        if (reactContext == null) {
            return;
        }

        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, null);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNotificationUpdated(NotificationChangeEvent event) {
        Timber.d("on receive notification event");

        sendEvent("zalopayNotificationsAdded");
    }

    private class RemoveNotifySubscriber extends DefaultSubscriber<Boolean> {
        private Promise promise;

        RemoveNotifySubscriber(Promise promise) {
            this.promise = promise;
        }

        @Override
        public void onError(Throwable e) {
            Helpers.promiseResolveError(promise, -1, "Remove notification error");
        }

        @Override
        public void onCompleted() {
            Helpers.promiseResolveSuccess(promise, null);
        }
    }
}
