package com.newvision.zeus.glasscore.protocol.netty.server;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.IServerChannelCallback;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by yanjiatian on 2017/6/29.
 */

public class GlassServerFileHandler extends SimpleChannelInboundHandler<GlassMessage> {
    private static final String TAG = GlassServerFileHandler.class.getSimpleName();
    private IServerChannelCallback mChannelHandlerContextCallback;
    private Handler mHandler;
    private Context mContext;

    public GlassServerFileHandler(Context context, Handler handler, IServerChannelCallback channelCallback) {
        this.mHandler = handler;
        this.mChannelHandlerContextCallback = channelCallback;
        this.mContext = context;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.d(TAG, "GlassServerFileHandler active ...");
        mChannelHandlerContextCallback.getChannelHandlerContext(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GlassMessage glassMessage) throws Exception {
//        Log.d(TAG, "GlassServerFileHandler " + Integer.toHexString(glassMessage.messageType) );
        Message msg = mHandler.obtainMessage();
        msg.what = glassMessage.messageType;
        msg.obj = glassMessage;
        mHandler.sendMessage(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
