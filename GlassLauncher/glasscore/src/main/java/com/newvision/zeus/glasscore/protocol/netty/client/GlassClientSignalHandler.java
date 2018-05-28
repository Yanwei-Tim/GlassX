package com.newvision.zeus.glasscore.protocol.netty.client;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.GlassDevicesInfo;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.IConnectServerListener;

import org.json.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by yanjiatian on 2017/6/30.
 */

public class GlassClientSignalHandler extends SimpleChannelInboundHandler<GlassMessage> {
    private static final String TAG = GlassClientSignalHandler.class.getSimpleName();
    private Handler mHandler;
    private IConnectServerListener mListener;


    public GlassClientSignalHandler(Handler handler, IConnectServerListener listener) {
        this.mHandler = handler;
        this.mListener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.d(TAG, "GlassTcpSignalHandler channel active ...");
        Message msg = mHandler.obtainMessage();
        if (mListener.getType() == GlassMessageType.GLASS_PAIR) {
            msg.what = GlassMessageType.GLASS_PAIR;
        } else {
            msg.what = GlassMessageType.CONNECT_VIDEO_SERVER;
            Log.d(TAG, "msg.obj : " + mListener.getIP());
            msg.obj = mListener.getIP();
        }
        mHandler.sendMessage(msg);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GlassMessage message) throws Exception {
//        Log.d(TAG, "channelRead0 message type : " + Integer.toHexString(message.messageType));
        if (message.messageType == GlassMessageType.GLASS_PAIR) {
            String ip = new String(message.messageBody);
            Log.d(TAG, "receive pair message : " + ip);
            JSONObject object = new JSONObject(ip);
            GlassDevicesInfo info = new GlassDevicesInfo();
            info.ip = object.getString("ip");
            info.sn = object.getString("sn");
            mListener.getConnectServerStatus(true, info);
            channelHandlerContext.close();
        } else {
            Message msg = mHandler.obtainMessage();
            msg.what = message.messageType;
            msg.obj = message;
            mHandler.sendMessage(msg);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
