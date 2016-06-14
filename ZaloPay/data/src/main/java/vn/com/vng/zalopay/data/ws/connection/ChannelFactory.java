package vn.com.vng.zalopay.data.ws.connection;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.nio.ByteOrder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import timber.log.Timber;

/**
 * Created by HaiNT on 3/27/2016.
 */
public class ChannelFactory extends ChannelInitializer<SocketChannel> {
    private static final String TAG = "ChannelFactory";
    private Context context;
    private Handler handler;

    public ChannelFactory(Context ctx, Handler handler) {
        this.context = ctx;
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("framer", new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN, 8192, 0, 4, 0, 4, true));
        pipeline.addLast("bytesDecoder", new ByteArrayDecoder());
        pipeline.addLast("bytesEncoder", new ByteArrayEncoder());
        pipeline.addLast("handler", new ProcessHandler(context, handler));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        Timber.d("channelRead");
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        Timber.d("channelReadComplete");
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Timber.d("channelActive");
        super.channelActive(ctx);
    }

}
