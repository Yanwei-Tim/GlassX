package com.newvision.zeus.glasscore.protocol.netty.client;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.IConnectServerListener;
import com.newvision.zeus.glasscore.protocol.helper.GlassPacketMessageHelper;
import com.newvision.zeus.glasscore.protocol.netty.GlassMessageDecoder;
import com.newvision.zeus.glasscore.protocol.netty.GlassMessageEncoder;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by yanjiatian on 2017/6/29.
 */

public class GlassTcpClient extends Service {

    private static final String TAG = GlassTcpClient.class.getSimpleName();
    private String mHost = "127.0.0.1";

    private NioEventLoopGroup group;
    private Channel mSignalChannel;
    private Channel mVideoChannel;
    private Channel mFileChannel;
    private boolean loginGlassStatus = false;

    private List<IMessageCallback> listeners = new ArrayList<IMessageCallback>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Glass tcp client Service create ...");
        group = new NioEventLoopGroup();
    }

    public class ClientServiceBinder extends Binder {

        public void connectServer(String ip, IConnectServerListener listener) {
            connectSignalServer(ip, listener);
        }

        //Activity直接bind service 获取句柄，调用发送消息的接口即可
        public void sendMessage(GlassMessage message) {
            switch (message.messageType) {
                case GlassMessageType.LOAD_H264:
                case GlassMessageType.LOAD_AUDIO:
                    sendVideoMessage(message);
                    break;
                case GlassMessageType.LOAD_FILE:
                    sendFileMessage(message);
                    break;
                default:
                    Log.i(TAG, "sendMessage: message type : " + Integer.toHexString(message.messageType));
                    sendSignalMessage(message);
                    break;

            }
        }

        public boolean getLoginGlassStatus() {
            return loginGlassStatus;
        }

        public void registerListener(IMessageCallback listener) {
            listeners.add(listener);
        }

        public void unregisterListener(IMessageCallback listener) {
            listeners.remove(listener);
        }
    }


    private void connectSignalServer(final String ip, final IConnectServerListener listener) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Bootstrap signalBootstrap = new Bootstrap();
                    signalBootstrap.group(group);
                    signalBootstrap.channel(NioSocketChannel.class);
                    signalBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                    signalBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("encoder", new GlassMessageEncoder());
                            pipeline.addLast("decoder", new GlassMessageDecoder());

                            pipeline.addLast(new GlassClientSignalHandler(mSignalHandler, listener));
                        }
                    });
                    ChannelFuture channelFuture = signalBootstrap.connect(new InetSocketAddress(ip, GlassConstants.SIGNAL_PORT));
                    mSignalChannel = channelFuture.sync().channel();

                    Log.d(TAG, "信令通道建立成功");
                } catch (Exception e) {
                    Log.d(TAG, "connectSignalServer failed ..." + e.getMessage());
                    listener.getConnectServerStatus(false, null);
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private void connectVideoServer() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Bootstrap videoBootstrap = new Bootstrap();
                    videoBootstrap.group(group);
                    videoBootstrap.channel(NioSocketChannel.class);
                    videoBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                    videoBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("encoder", new GlassMessageEncoder());
                            pipeline.addLast("decoder", new GlassMessageDecoder());

                            pipeline.addLast(new GlassClientVideoHandler(mVideoHandler));
                        }
                    });

                    ChannelFuture channelFuture = videoBootstrap.connect(new InetSocketAddress(mHost, GlassConstants.VIDEO_PORT));
                    mVideoChannel = channelFuture.sync().channel();
                    Log.d(TAG, "视频流通道建立成功");

                } catch (InterruptedException e) {
                    Log.d(TAG, "connectVideoServer failed ..." + e.getMessage());
                    mSignalHandler.sendEmptyMessageDelayed(GlassMessageType.CONNECT_VIDEO_SERVER, 5 * 1000);
                    e.printStackTrace();
                }


            }
        }.start();
    }

    private void connectFileServer() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Bootstrap fileBootstrap = new Bootstrap();
                    fileBootstrap.group(group);
                    fileBootstrap.channel(NioSocketChannel.class);
                    fileBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                    fileBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("encoder", new GlassMessageEncoder());
                            pipeline.addLast("decoder", new GlassMessageDecoder());
                            pipeline.addLast(new GlassClientFileHandler(mFileHandler));
                        }
                    });
                    ChannelFuture channelFuture = fileBootstrap.connect(new InetSocketAddress(mHost, GlassConstants.FILE_PORT));
                    mFileChannel = channelFuture.sync().channel();
                    Log.d(TAG, "文件通道建立成功");

                    sendSignalMessage(GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_LOGIN_ASK, GlassErrorCode.OK, null));
                } catch (InterruptedException e) {
                    Log.d(TAG, "connectFileServer failed ..." + e.getMessage());
                    mVideoHandler.sendEmptyMessageDelayed(GlassMessageType.CONNECT_FILE_SERVER, 5 * 1000);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Handler mSignalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage: msg.what = " + Integer.toHexString(msg.what));
            switch (msg.what) {
                case GlassMessageType.GLASS_PAIR:
                    GlassMessage pair = new GlassMessage();
                    pair.messageType = GlassMessageType.GLASS_PAIR;
                    sendSignalMessage(pair);
                    break;

                case GlassMessageType.CONNECT_VIDEO_SERVER:
                    Log.d(TAG, "mSignalHandler CONNECT_VIDEO_SERVER");
                    mHost = (String) msg.obj;
                    connectVideoServer();
                    break;

                case GlassMessageType.GLASS_LOGIN_ANS:
                    loginGlassStatus = true;
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.GLASS_LOGIN_ANS, 1);
                    }
                    break;

                case GlassMessageType.PHONE_OPEN_APP_ASK:
                    try {
                        String body = new String(((GlassMessage) msg.obj).messageBody, "utf-8");
                        JSONObject obj = new JSONObject(body);
                        String pkg = obj.getString("pkg");
                        for (int i = 0; i < listeners.size(); i++) {
                            listeners.get(i).getResult(GlassMessageType.PHONE_OPEN_APP_ASK, pkg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case GlassMessageType.GLASS_OPEN_APP_ANS:
                    try {
                        String body = new String(((GlassMessage) msg.obj).messageBody, "utf-8");
                        JSONObject obj = new JSONObject(body);
                        String pkg = obj.getString("pkg");
                        for (int i = 0; i < listeners.size(); i++) {
                            listeners.get(i).getResult(GlassMessageType.GLASS_OPEN_APP_ANS, pkg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case GlassMessageType.PHONE_CLOSE_APP_ASK:
                    try {
                        String body = new String(((GlassMessage) msg.obj).messageBody, "utf-8");
                        JSONObject obj = new JSONObject(body);
                        String pkg = obj.getString("pkg");
                        for (int i = 0; i < listeners.size(); i++) {
                            listeners.get(i).getResult(GlassMessageType.PHONE_CLOSE_APP_ASK, pkg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case GlassMessageType.GLASS_CLOSE_APP_ANS:
                    try {
                        String body = new String(((GlassMessage) msg.obj).messageBody, "utf-8");
                        JSONObject obj = new JSONObject(body);
                        String pkg = obj.getString("pkg");
                        for (int i = 0; i < listeners.size(); i++) {
                            listeners.get(i).getResult(GlassMessageType.GLASS_CLOSE_APP_ANS, pkg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                //start preview
                case GlassMessageType.PHONE_START_PREVIEW_ASK:
                    Log.i(TAG, "handleMessage:GLASS_START_PREVIEW_ASK ");
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.PHONE_START_PREVIEW_ASK, null);
                    }
                    break;
                case GlassMessageType.GLASS_START_PREVIEW_ANS:
                    Log.i(TAG, "handleMessage:GLASS_START_PREVIEW_ASK ");
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.GLASS_START_PREVIEW_ANS, null);
                    }
                    break;


            }

        }
    };
    private Handler mVideoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GlassMessageType.CONNECT_FILE_SERVER:
                    Log.d(TAG, "mSignalHandler CONNECT_FILE_SERVER");
                    connectFileServer();
                    break;
                case GlassMessageType.LOAD_H264:
                    Log.i(TAG, "handleMessage: receive a frame");
                    GlassMessage videoData = (GlassMessage) msg.obj;
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.LOAD_H264, videoData);
                    }
                    break;
                case GlassMessageType.LOAD_AUDIO:
                    Log.i(TAG, "handleMessage: receive a frame");
                    GlassMessage audioData = (GlassMessage) msg.obj;
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.LOAD_AUDIO, audioData);
                    }
                    break;
            }

        }
    };

    private Handler mFileHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GlassMessageType.LOAD_FILE:
                    GlassMessage photosData = (GlassMessage) msg.obj;
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.LOAD_FILE, photosData);
                    }
                    break;
            }

        }
    };

    private void sendSignalMessage(final GlassMessage message) {
        mSignalHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mSignalChannel != null && mSignalChannel.isOpen()) {
                    mSignalChannel.writeAndFlush(message);
                    mSignalChannel.read();
                }
            }
        });
    }

    private void sendVideoMessage(final GlassMessage message) {
        mVideoHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mVideoChannel != null && mVideoChannel.isOpen()) {
                    mVideoChannel.writeAndFlush(message);
                    mVideoChannel.read();
                }
            }
        });
    }

    private void sendFileMessage(final GlassMessage message) {
        mFileHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mFileChannel != null && mFileChannel.isOpen()) {
                    mFileChannel.writeAndFlush(message);
                    mFileChannel.read();
                }
            }
        });
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ClientServiceBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loginGlassStatus = false;
    }
}
