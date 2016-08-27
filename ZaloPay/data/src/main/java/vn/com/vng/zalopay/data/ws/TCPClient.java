package vn.com.vng.zalopay.data.ws;

import android.os.Handler;
import android.os.HandlerThread;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;

import timber.log.Timber;

/**
 * Created by AnhHieu on 7/24/16.
 */
public class TCPClient implements SocketClient {
    private Listener mListener;

    /**
     * Thread for handling network connection
     * Modification to mConnection will only be happened in this thread
     */
    private HandlerThread mConnectionThread;
    private Handler mConnectionHandler;

    /**
     * Thread for handling commands, network events, read/write queue to network connection
     */
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    private boolean mRun = false;

    private final SocketChannelConnection mConnection;

    public TCPClient(String hostname, int port, Listener listener) {
        mListener = listener;

        mHandlerThread = new HandlerThread("socket-thread");
        mConnectionThread = new HandlerThread("connection-thread");
        mHandlerThread.start();
        mConnectionThread.start();

        mHandler = new Handler(mHandlerThread.getLooper());
        mConnectionHandler = new Handler(mConnectionThread.getLooper());

        mConnection = new SocketChannelConnection(hostname, port, new ConnectionListener());
    }

    public void connect() {
        Timber.d("Request to make connection");
        mConnectionHandler.post(() -> {
            if (mConnection.isConnected() || mConnection.isConnecting()) {
                Timber.d("[CONNECTION] Skip create new connection");
                return;
            }
            Timber.d("Starting new connection");
            try {
                mConnection.startConnect();
                mConnection.run();
            } catch (SocketException e) {
                Timber.e(e, "SocketException");
                postErrorEvent(e);
            } catch (IOException e) {
                Timber.e(e, "IOException");
                postErrorEvent(e);
            } catch (Exception e) {
                Timber.e(e, "Exception");
                postErrorEvent(e);
            } finally {
                Timber.d("Stopping the connection.");
                disposeConnection();
            }
        });
    }

    public void disconnect() {
        mRun = false;
        disposeConnection();
    }

    private void disposeConnection() {
        mHandler.post(mConnection::close);
    }

    public void send(byte[] data) {
        if (isConnected()) {
            sendFrame(data);
        }
    }

    private void sendFrame(final byte[] frame) {
        if (frame == null) {
            return;
        }

        Timber.d("QUEUE: send message");
        postWriteData(frame);
    }

    @Override
    public boolean isConnected() {
        return mConnection != null && mConnection.isConnected();
    }

    @Override
    public boolean isConnecting() {
        return mConnection != null && mConnection.isConnecting();
    }

    /**
     * Created by huuhoa on 8/12/16.
     * Implementation for connection events
     */
    private class ConnectionListener implements SocketChannelConnection.ConnectionListenable {
        @Override
        public void onConnected() {
            postConnectedEvent();
        }

        @Override
        public void onReceived(byte[] data) {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            int messageLength = buffer.getInt();
            Timber.d("Message length: %d", messageLength);
            if (messageLength > 20000) {
                Timber.e("Wrong message length: %s", messageLength);
                return;
            }
            if (messageLength > 0) {
                byte[] dataBuffer = new byte[messageLength];
                buffer.get(dataBuffer);
                Timber.d("Read %s bytes as message body", messageLength);
                postReceivedDataEvent(dataBuffer);
//                mListener.onMessage(dataBuffer);
            } else {
                Timber.d("messageLength is negative!");
            }
        }

        @Override
        public void onDisconnected(int reason) {
            postDisconnectedEvent(reason);
        }
    }

    private void postDisconnectedEvent(int reason) {
        if (mHandler == null || mListener == null) {
            return;
        }

        mHandler.post(() -> mListener.onDisconnected(reason, ""));
    }

    private void postWriteData(byte[] data) {
        mHandler.post(() -> mConnection.write(data));
    }

    private void postReceivedDataEvent(byte[] data) {
        if (mHandler == null || mListener == null) {
            return;
        }

        mHandler.post(() -> mListener.onMessage(data));
    }

    private void postConnectedEvent() {
        if (mHandler == null || mListener == null) {
            return;
        }

        mHandler.post(() -> mListener.onConnected());
    }

    private void postErrorEvent(Exception e) {
        if (mHandler == null || mListener == null) {
            return;
        }

        mHandler.post(() -> mListener.onError(e));
    }
}
