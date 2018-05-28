package com.newvision.zeus.glasscore.protocol.netty.client;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by yanjiatian on 2017/7/3.
 */

public class GlassClientVideoHandler extends SimpleChannelInboundHandler<GlassMessage> {
    private static final String TAG = GlassClientVideoHandler.class.getSimpleName();
    private Handler mHandler;

    public GlassClientVideoHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.d(TAG, "GlassClientVideoHandler channel active ...");
        Message msg = mHandler.obtainMessage();
        msg.what = GlassMessageType.CONNECT_FILE_SERVER;
        mHandler.sendMessage(msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GlassMessage message) throws Exception {
//        Log.i(TAG, "channelRead0: GlassClientVideoHandler" + Integer.toHexString(message.messageType));
        Message msg = mHandler.obtainMessage();
        msg.what = message.messageType;
        msg.obj = message;
        mHandler.sendMessage(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
