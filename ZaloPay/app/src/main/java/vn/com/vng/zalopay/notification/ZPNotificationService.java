package vn.com.vng.zalopay.notification;

import android.content.Context;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;
import vn.com.vng.zalopay.AndroidApplication;
import vn.com.vng.zalopay.BuildConfig;
import vn.com.vng.zalopay.data.NetworkError;
import vn.com.vng.zalopay.data.eventbus.NotificationChangeEvent;
import vn.com.vng.zalopay.data.eventbus.ReadNotifyEvent;
import vn.com.vng.zalopay.data.eventbus.ThrowToLoginScreenEvent;
import vn.com.vng.zalopay.data.exception.AccountSuspendedException;
import vn.com.vng.zalopay.data.exception.ServerMaintainException;
import vn.com.vng.zalopay.data.exception.TokenException;
import vn.com.vng.zalopay.data.util.Lists;
import vn.com.vng.zalopay.data.util.NetworkHelper;
import vn.com.vng.zalopay.data.ws.callback.OnReceiverMessageListener;
import vn.com.vng.zalopay.data.ws.connection.NotificationApiHelper;
import vn.com.vng.zalopay.data.ws.connection.NotificationApiMessage;
import vn.com.vng.zalopay.data.ws.connection.WsConnection;
import vn.com.vng.zalopay.data.ws.model.AuthenticationData;
import vn.com.vng.zalopay.data.ws.model.Event;
import vn.com.vng.zalopay.data.ws.model.NotificationData;
import vn.com.vng.zalopay.data.ws.model.RecoveryMessageEvent;
import vn.com.vng.zalopay.data.ws.parser.MessageParser;
import vn.com.vng.zalopay.domain.executor.ThreadExecutor;
import vn.com.vng.zalopay.domain.interactor.DefaultSubscriber;
import vn.com.vng.zalopay.domain.model.User;
import vn.com.vng.zalopay.event.NetworkChangeEvent;
import vn.com.vng.zalopay.event.TokenGCMRefreshEvent;
import vn.com.vng.zalopay.internal.di.components.ApplicationComponent;
import vn.com.vng.zalopay.internal.di.components.UserComponent;

public class ZPNotificationService implements OnReceiverMessageListener {

    /*Server API Key: AIzaSyCweupE81mBm3_m8VOoFTUbuhBF82r_GwI
    Sender ID: 386726389536*/

    private boolean mIsSubscribeGcm = false;
    private WsConnection mWsConnection;

    @Inject
    Context mContext;

    @Inject
    EventBus mEventBus;

    @Inject
    User mUser;

    @Inject
    Gson mGson;

    @Inject
    NotificationHelper mNotificationHelper;

    @Inject
    ThreadExecutor mExecutor;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    private static final int NUMBER_NOTIFICATION = 30;

    private long mLastTimeRecovery;

    @Inject
    ZPNotificationService() {
    }

    public void start() {
        Timber.d("Start notification service");

        registerEvent();

        ensureInitializeNetworkConnection();

        if (mExecutor != null) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    connectToServer();
                }
            });
        }
    }

    public void destroy() {
        Timber.d("Destroy notification service");
        mIsSubscribeGcm = false;

        if (mCompositeSubscription != null) {
            mCompositeSubscription.clear();
        }

        unregisterEvent();

        if (mWsConnection != null) {
            mWsConnection.disconnect();
            mWsConnection.clearReceiverListener();
            mWsConnection.cleanup();
            mWsConnection = null;
        }
    }

    private void registerEvent() {
        if (!mEventBus.isRegistered(this)) {
            mEventBus.register(this);
        }
        //    BusComponent.subscribe(APP_SUBJECT, this, new ComponentSubscriber(), AndroidSchedulers.mainThread());
    }

    private void unregisterEvent() {
        mEventBus.unregister(this);
        //  BusComponent.unregister(this);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mNotificationHelper = null;
        Timber.d("Finalize ZPNotificationService");
    }

    private void connectToServer() {
        String token = null;
        try {
            token = GcmHelper.getTokenGcm(mContext);
            subscribeTopics(token);
        } catch (Exception ex) {
            Timber.d(ex, "exception in working with GCM");
        }

        this.connect(token);
    }

    private void connect(String token) {
        Timber.d("connect with token %s", token);
        if (!NetworkHelper.isNetworkAvailable(mContext)) {
            Timber.d("Skip create connection, since OS reports no network connection");
            return;
        }

        ensureInitializeNetworkConnection();

        if (!mWsConnection.isConnected()) {
            Timber.d("Socket is not connected. About to create connection.");
            mWsConnection.setGCMToken(token);
            mWsConnection.connect();
        } else {
            Timber.d("Socket is already connected. Do nothing.");
        }
    }

    private void disconnectServer() {
        Timber.d("Request to disconnect connection with notification server");
        if (mWsConnection == null) {
            return;
        }

        mWsConnection.disconnect();
    }

    @Override
    public void onReceiverEvent(Event event) {
        Timber.d("Notification message: [mtuid: %s]", event.mtuid);
        if (event instanceof AuthenticationData) {
            AuthenticationData authenticationData = (AuthenticationData) event;
            if (authenticationData.result != NetworkError.SUCCESSFUL) {
                handlerAuthenticationError(authenticationData);
            } else {
                Timber.d("Socket authentication succeeded");
                this.recoveryNotification(true);
            }
        } else if (event instanceof NotificationData) {
            if (mNotificationHelper == null) {
                return;
            }

            mNotificationHelper.processImmediateNotification((NotificationData) event);
        } else if (event instanceof RecoveryMessageEvent) {
            if (mTimeoutRecoverySubscription != null) {
                mTimeoutRecoverySubscription.unsubscribe();
            }

            final List<NotificationData> listMessage = ((RecoveryMessageEvent) event).listNotify;
            Timber.d("Receive notification %s", listMessage);

            if (mNotificationHelper != null && !Lists.isEmptyOrNull(listMessage)) {
                //Cần recoveryNotification xong để set lasttime recovery xong,
                // mới tiếp tục sendmessage recovery.
                Subscription sub = mNotificationHelper.recoveryNotification(listMessage)
                        .filter(aVoid -> listMessage.size() >= NUMBER_NOTIFICATION)
                        .flatMap(aVoid -> mNotificationHelper.getOldestTimeRecoveryNotification(false))
                        .subscribeOn(Schedulers.io())
                        .subscribe(new DefaultSubscriber<Long>() {
                            @Override
                            public void onNext(Long time) {
                                sendMessageRecovery(time);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.d(e, "onError: ");
                            }

                            @Override
                            public void onCompleted() {
                                if (listMessage.size() < NUMBER_NOTIFICATION) {
                                    recoveryData();
                                }
                            }
                        });
                mCompositeSubscription.add(sub);
            }
        }
    }


    private Subscription mTimeoutRecoverySubscription;

    private void recoveryNotification(final boolean isFirst) {
        if (mNotificationHelper == null) {
            return;
        }

        Timber.d("Recovery notification: %s", isFirst);

        Subscription subscription = mNotificationHelper.getOldestTimeRecoveryNotification(isFirst)
                .subscribeOn(Schedulers.io())
                .subscribe(new DefaultSubscriber<Long>() {
                    @Override
                    public void onNext(Long time) {
                        sendMessageRecovery(time);
                    }
                });

        mCompositeSubscription.add(subscription);
    }

    private Subscription startTimeoutRecoveryNotification() {
        return Observable.timer(5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(new DefaultSubscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("onCompleted: start recovery transaction");
                        recoveryData();
                    }
                });
    }

    private void recoveryData() {
        this.recoveryTransaction();
        this.recoveryRedPacketStatus();
    }

    private void recoveryTransaction() {
        Timber.d("Begin recovery transaction");
        mNotificationHelper.recoveryTransaction();
    }

    private void recoveryRedPacketStatus() {
        mNotificationHelper.recoveryRedPacketStatus();
    }

    private void sendMessageRecovery(long timeStamp) {
        Timber.d("Send message recovery timeStamp [%s]", timeStamp);
        if (mLastTimeRecovery > 0 && mLastTimeRecovery <= timeStamp) {
            Timber.d("ignore recovery [%s]", timeStamp);
            return;
        }

        mLastTimeRecovery = timeStamp;
        if (mWsConnection != null) {
            mTimeoutRecoverySubscription = startTimeoutRecoveryNotification();
            NotificationApiMessage message = NotificationApiHelper.createMessageRecovery(NUMBER_NOTIFICATION, timeStamp);
            mWsConnection.send(message.messageCode, message.messageContent);
        }
    }

    private void subscribeTopics(String token) throws IOException {
        Timber.d("subscribe Topics mIsSubscribeGcm [%s] token [%s]", mIsSubscribeGcm, token);
        if (mIsSubscribeGcm) {
            return;
        }
        GcmHelper.subscribeTopics(mContext, token);
        mIsSubscribeGcm = true;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNetworkChange(NetworkChangeEvent event) {
        Timber.d("onNetworkChange %s", event.isOnline);
        if (event.isOnline) {
            this.connectToServer();
        } else {
            this.disconnectServer();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadNotify(ReadNotifyEvent event) {
        if (mNotificationHelper == null) {
            return;
        }

        mNotificationHelper.closeNotificationSystem();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onNotificationUpdated(NotificationChangeEvent event) {
        Timber.d("on Notification updated %s", event.isRead());
        if (mNotificationHelper == null) {
            return;
        }

        if (!event.isRead()) {
            mNotificationHelper.showNotificationSystem();
        }
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    public void onTokenGcmRefresh(TokenGCMRefreshEvent event) {
        Timber.d("on Token GCM Refresh event %s", event);
        TokenGCMRefreshEvent stickyEvent = mEventBus.getStickyEvent(TokenGCMRefreshEvent.class);
        // Better check that an event was actually posted before
        if (stickyEvent != null) {
            // "Consume" the sticky event
            mEventBus.removeStickyEvent(stickyEvent);
            mIsSubscribeGcm = false;
            mWsConnection.disconnect();
            start();
        }
    }

    private void ensureInitializeNetworkConnection() {
        if (mWsConnection == null) {
            mWsConnection = new WsConnection(BuildConfig.WS_HOST, BuildConfig.WS_PORT, mContext,
                    new MessageParser(mGson), mUser);
            mWsConnection.addReceiverListener(this);
        }
    }

    public ApplicationComponent getAppComponent() {
        return AndroidApplication.instance().getAppComponent();
    }

    protected UserComponent getUserComponent() {
        return AndroidApplication.instance().getUserComponent();
    }

    private void handlerAuthenticationError(AuthenticationData authentication) {
        Timber.d("handlerAuthenticationError: %s", authentication.code);
        if (authentication.code == NetworkError.UM_TOKEN_NOT_FOUND ||
                authentication.code == NetworkError.UM_TOKEN_EXPIRE ||
                authentication.code == NetworkError.TOKEN_INVALID) {
            // session expired
            Timber.d("Session is expired");
            TokenException exception = new TokenException(authentication.code);
            mEventBus.postSticky(new ThrowToLoginScreenEvent(exception));
        } else if (authentication.code == NetworkError.SERVER_MAINTAIN) {
            Timber.d("Server maintain");
            ServerMaintainException exception = new ServerMaintainException(authentication.code, "");
            mEventBus.postSticky(new ThrowToLoginScreenEvent(exception));
        } else if (authentication.code == NetworkError.ZPW_ACCOUNT_SUSPENDED
                || authentication.code == NetworkError.USER_IS_LOCKED) {
            Timber.d("Account is locked");
            AccountSuspendedException exception = new AccountSuspendedException(authentication.code, "");
            mEventBus.postSticky(new ThrowToLoginScreenEvent(exception));
        }
    }

  /*  private class ComponentSubscriber extends DefaultSubscriber<Object> {
        @Override
        public void onNext(Object event) {
            if (event instanceof NotificationChangeEvent) {
                if (!((NotificationChangeEvent) event).isRead()) {
                    if (mNotificationHelper != null) {
                        mNotificationHelper.showNotificationSystem();
                    }
                }
            }
        }
    }*/
}
