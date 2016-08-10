package vn.com.vng.zalopay.data.ws.connection;

import android.content.Context;
import android.text.TextUtils;

import com.google.protobuf.AbstractMessage;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

import io.netty.channel.ConnectTimeoutException;
import timber.log.Timber;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.data.util.NetworkHelper;
import vn.com.vng.zalopay.data.ws.Listener;
import vn.com.vng.zalopay.data.ws.NettyClient;
import vn.com.vng.zalopay.data.ws.SocketClient;
import vn.com.vng.zalopay.data.ws.message.MessageType;
import vn.com.vng.zalopay.data.ws.model.Event;
import vn.com.vng.zalopay.data.ws.parser.Parser;
import vn.com.vng.zalopay.data.ws.protobuf.ZPMsgProtos;
import vn.com.vng.zalopay.domain.Enums;
import vn.com.vng.zalopay.domain.model.User;

/**
 * Created by AnhHieu on 6/14/16.
 */
public class WsConnection extends Connection implements Listener {

    private String gcmToken;

    private final Context context;

    private int numRetry;

    private final Parser parser;
    private final UserConfig userConfig;


    private Timer mTimer;
    private TimerTask timerTask;

    SocketClient socketClient;

    public WsConnection(String host, int port, Context context, Parser parser, UserConfig config) {
        super(host, port);
        this.context = context;
        this.parser = parser;
        this.userConfig = config;
        socketClient = new NettyClient(host, port, this);
    }

    public void setGCMToken(String token) {
        this.gcmToken = token;
    }

    @Override
    public void connect() {
        socketClient.connect();
    }

    @Override
    public void ping() {

    }

    @Override
    public void disconnect() {
        Timber.d("disconnect");
        socketClient.disconnect();
    }

    @Override
    public boolean isConnected() {
        return socketClient.isConnected();
    }

    @Override
    public boolean isConnecting() {
        return socketClient.isConnecting();
    }

    @Override
    public boolean send(int msgType, String data) {
        return false;
    }

    @Override
    public boolean send(int msgType, AbstractMessage msgData) {
        return send(msgType, msgData.toByteArray());
    }

    @Override
    public boolean send(int msgType, byte[] data) {
        ByteBuffer bufTemp = ByteBuffer.allocate(HEADER_LENGTH + data.length);
        bufTemp.putInt(data.length + TYPE_FIELD_LENGTH);
        bufTemp.put((byte) msgType);
        bufTemp.put(data);

        socketClient.send(bufTemp.array());

        return true;
    }


    @Override
    public void onConnected() {
        Timber.d("onConnected");
        mState = State.Connected;
        //    numRetry = 0;
        sendAuthentication();

        stopTimerCheckConnect();
    }

    @Override
    public void onMessage(byte[] data) {
        Timber.d("onReceived");
        Event message = parser.parserMessage(data);
        if (message != null) {
            Timber.d("onReceived message.msgType %s", message.getMsgType());

            if (message.getMsgType() == MessageType.Response.AUTHEN_LOGIN_RESULT) {
                numRetry = 0;
            } else if (message.getMsgType() == MessageType.Response.KICK_OUT) {
                disconnect();
            } else {
                postResult(message);
            }

            sendFeedbackStatus(message);
        }
    }


    @Override
    public void onError(Throwable e) {
        Timber.d("onError %s", e);
        mState = Connection.State.Disconnected;

        if (e instanceof SocketTimeoutException) {
        } else if (e instanceof ConnectTimeoutException) {
        } else if (e instanceof ConnectException) {
        } else if (e instanceof UnknownHostException) {
        }

        startTimerCheckConnect();
    }

    @Override
    public void onDisconnected(int code, String message) {
        Timber.d("onDisconnected %s", code);
        mState = Connection.State.Disconnected;

        //socketClient.disconnect();

        if (NetworkHelper.isNetworkAvailable(context)
                && userConfig.hasCurrentUser()
                && numRetry <= MAX_NUMBER_RETRY_CONNECT) {
            connect();
            numRetry++;
        }
    }


    private boolean sendAuthentication(String token, long uid) {

        Timber.d("send authentication token %s uid %s gcmToken %s", token, uid, gcmToken);

        ZPMsgProtos.MessageLogin.Builder loginMsg = ZPMsgProtos.MessageLogin.newBuilder()
                .setToken(token)
                .setUsrid(uid)
                .setOstype(Enums.Platform.ANDROID.getId());

        if (!TextUtils.isEmpty(gcmToken)) {
            loginMsg.setDevicetoken(gcmToken);
        }

        return send(ZPMsgProtos.MessageType.AUTHEN_LOGIN.getNumber(), loginMsg.build());
    }

    public boolean sendAuthentication() {
        if (userConfig.hasCurrentUser()) {
            User user = userConfig.getCurrentUser();
            return sendAuthentication(user.accesstoken, Long.parseLong(user.uid));
        }
        return false;
    }

    public boolean sendFeedbackStatus(Event event) {
        long mtaid = event.getMtaid();
        long mtuid = event.getMtuid();
        long uid = -1;

        try {
            uid = Long.parseLong(userConfig.getCurrentUser().uid);
        } catch (Exception ex) {
            Timber.d("parse uid exception %s");
        }

        if (mtaid <= 0 && mtuid <= 0) {
            return true;
        }

        Timber.d("Send feedback status with mtaid %s mtuid %s uid %s", mtaid, mtuid, uid);

        ZPMsgProtos.StatusMessageClient.Builder statusMsg = ZPMsgProtos.StatusMessageClient.newBuilder()
                .setStatus(ZPMsgProtos.MessageStatus.RECEIVED.getNumber());

        if (mtaid > 0) {
            statusMsg.setMtaid(mtaid);
        }
        if (mtuid > 0) {
            statusMsg.setMtuid(mtuid);
        }
        if (uid > 0) {
            statusMsg.setUserid(uid);
        }

        return send(ZPMsgProtos.MessageType.FEEDBACK.getNumber(), statusMsg.build());
    }


    private void startTimerCheckConnect() {
        stopTimerCheckConnect();

        mTimer = new Timer();
        timerTask = new CheckConnectionTask();

        mTimer.schedule(timerTask, 0, 5 * 60 * 1000);
    }

    private void stopTimerCheckConnect() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    private class CheckConnectionTask extends TimerTask {

        @Override
        public void run() {
            Timber.d("Begin check connection");
            if (NetworkHelper.isNetworkAvailable(context)) {
                connect();
            }
        }
    }
}
