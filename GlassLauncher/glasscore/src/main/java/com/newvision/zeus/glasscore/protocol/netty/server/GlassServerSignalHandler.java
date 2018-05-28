package com.newvision.zeus.glasscore.protocol.netty.server;


import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.IServerChannelCallback;
import com.newvision.zeus.glasscore.utils.IPAddressUtils;

import org.json.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by yanjiatian on 2017/6/29.
 */

public class GlassServerSignalHandler extends SimpleChannelInboundHandler<GlassMessage> {
    private static final String TAG = GlassServerSignalHandler.class.getSimpleName();
    private Context mContext;
    private Handler mHandler;
    private IServerChannelCallback mChannelHandlerContextCallback;

    public GlassServerSignalHandler(Context context, Handler handler, IServerChannelCallback channelCallback) {
        this.mHandler = handler;
        this.mChannelHandlerContextCallback = channelCallback;
        this.mContext = context;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.d(TAG, "GlassServerSignalHandler active ...");
        mChannelHandlerContextCallback.getChannelHandlerContext(ctx);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GlassMessage glassMessage) throws Exception {
//        Log.d(TAG, "GlassServerSignalHandler " + Integer.toHexString(glassMessage.messageType) );
        if (glassMessage.messageType == GlassMessageType.GLASS_PAIR) {
            sendServerInfo(channelHandlerContext);
        } else {
            Message msg = mHandler.obtainMessage();
            msg.what = glassMessage.messageType;
            msg.obj = glassMessage;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    private void sendServerInfo(ChannelHandlerContext ctx) {
        GlassMessage message = new GlassMessage();
        message.messageType = GlassMessageType.GLASS_PAIR;
        JSONObject object = new JSONObject();
        try {
            object.put("ip", IPAddressUtils.getIpAddress(mContext));
            object.put("sn", Build.MODEL);
            byte[] data = object.toString().getBytes();
            Log.d(TAG, "data length = " + data.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        message.messageBody = object.toString().getBytes();
        message.messageLength = message.messageBody.length;
        ctx.writeAndFlush(message);
    }
}
