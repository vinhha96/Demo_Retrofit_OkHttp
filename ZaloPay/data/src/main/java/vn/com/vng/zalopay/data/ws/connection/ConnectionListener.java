package vn.com.vng.zalopay.data.ws.connection;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import timber.log.Timber;

/**
 * Created by huuhoa on 8/12/16.
 * Implementation for connection events
 */
class ConnectionListener implements ConnectionListenable {
    private WeakReference<TCPClient> mTcpClient;

    ConnectionListener(TCPClient tcpClient) {
        mTcpClient = new WeakReference<>(tcpClient);
    }

    @Override
    public void onConnected() {
        if (mTcpClient.get() != null) {
            mTcpClient.get().postConnectedEvent();
        }
    }

    @Override
    public void onReceived(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int messageLength = buffer.getInt();
//        Timber.d("Message length: %d", messageLength);
        if (messageLength > 20000) {
            Timber.e("Wrong message length: %s", messageLength);
            return;
        }
        if (messageLength > 0) {
            if (messageLength > buffer.remaining()) {
                Timber.e(new Exception("Message length is invalid"),
                        "Message length %s is larger than remaining received data %s",
                        messageLength,
                        buffer.remaining());
                return;
            }

            byte[] dataBuffer = new byte[messageLength];
            buffer.get(dataBuffer);
//            Timber.d("Read %s bytes as message body", messageLength);
            if (mTcpClient.get() != null) {
                mTcpClient.get().postReceivedDataEvent(dataBuffer);
            }
//                mListener.onMessage(dataBuffer);
        } else {
            Timber.e(new Exception("Message length is negative"), "messageLength is negative!");
        }
    }

    @Override
    public void onDisconnected(ConnectionErrorCode reason) {
        if (mTcpClient.get() != null) {
            mTcpClient.get().postDisconnectedEvent(reason);
        }
    }
}
