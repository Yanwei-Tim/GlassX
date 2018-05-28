package com.newvision.zeus.glasscore.protocol.netty.server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.IServerChannelCallback;
import com.newvision.zeus.glasscore.protocol.helper.GlassPacketMessageHelper;
import com.newvision.zeus.glasscore.protocol.netty.GlassMessageDecoder;
import com.newvision.zeus.glasscore.protocol.netty.GlassMessageEncoder;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by yanjiatian on 2017/6/29.
 */

public class GlassTcpServer extends Service {

    private static final String TAG = GlassTcpServer.class.getSimpleName();

    private boolean mServerStatus = false;      //tcp 服务端口是否开启监听
    private boolean loginGlassStatus = false;   //login status

    private ChannelHandlerContext mSignalChannelContext;
    private ChannelHandlerContext mVideoChannelContext;
    private ChannelHandlerContext mFileChannelContext;

    private List<IMessageCallback> listeners = new ArrayList<IMessageCallback>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServerServiceBinder();
    }

    public class ServerServiceBinder extends Binder {

        public boolean getServerStatus() {
            return mServerStatus;
        }

        //Activity直接bind service 获取句柄，调用发送消息的接口即可
        public void sendMessage(GlassMessage message) {
            switch (message.messageType) {
                case GlassMessageType.START_SIGNAL_SERVER:
                    mSignalHandler.sendEmptyMessageDelayed(GlassMessageType.START_SIGNAL_SERVER, 1000);
                    break;
                case GlassMessageType.LOAD_H264:
                case GlassMessageType.LOAD_AUDIO:
                    sendVideoMessage(message);
                    break;
                case GlassMessageType.LOAD_FILE:
                    sendFileMessage(message);
                    break;
                default:
                    Log.i(TAG, "sendMessage: message type" + Integer.toHexString(message.messageType));
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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Glass tcp server Service create ...");
        mSignalHandler.sendEmptyMessageDelayed(GlassMessageType.START_SIGNAL_SERVER, 2000);
    }

    private Handler mSignalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage:****** msg what=*****" + Integer.toHexString(msg.what));
            switch (msg.what) {
                case GlassMessageType.START_SIGNAL_SERVER:
                    startSignalServer();
                    break;
                case GlassMessageType.GLASS_LOGIN_ASK:
                    loginGlassStatus = true;
                    sendSignalMessage(GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_LOGIN_ANS, GlassErrorCode.OK, null));
                    break;

                case GlassMessageType.PHONE_OPEN_APP_ANS:
                    try {
                        String body = new String(((GlassMessage) msg.obj).messageBody, "utf-8");
                        JSONObject obj = new JSONObject(body);
                        String pkg = obj.getString("pkg");
                        for (int i = 0; i < listeners.size(); i++) {
                            listeners.get(i).getResult(GlassMessageType.PHONE_OPEN_APP_ANS, pkg);
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

                case GlassMessageType.GLASS_CLOSE_APP_ASK:
                    try {
                        String body = new String(((GlassMessage) msg.obj).messageBody, "utf-8");
                        JSONObject obj = new JSONObject(body);
                        String pkg = obj.getString("pkg");
                        for (int i = 0; i < listeners.size(); i++) {
                            listeners.get(i).getResult(GlassMessageType.GLASS_CLOSE_APP_ASK, pkg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case GlassMessageType.PHONE_CLOSE_APP_ANS:
                    try {
                        String body = new String(((GlassMessage) msg.obj).messageBody, "utf-8");
                        JSONObject obj = new JSONObject(body);
                        String pkg = obj.getString("pkg");
                        for (int i = 0; i < listeners.size(); i++) {
                            listeners.get(i).getResult(GlassMessageType.PHONE_CLOSE_APP_ANS, pkg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                //start preview
                case GlassMessageType.PHONE_START_PREVIEW_ANS:
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.PHONE_START_PREVIEW_ANS, null);
                    }
                    break;
                case GlassMessageType.GLASS_START_PREVIEW_ASK:
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.GLASS_START_PREVIEW_ASK, null);
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
                    GlassMessage photoData = (GlassMessage) msg.obj;
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.LOAD_FILE, photoData);
                    }
                    break;
            }

        }
    };

    private Handler mVideoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GlassMessageType.LOAD_H264:
                    GlassMessage videoData = (GlassMessage) msg.obj;
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.LOAD_H264, videoData);
                    }
                    break;
                case GlassMessageType.LOAD_AUDIO:
                    GlassMessage audioData = (GlassMessage) msg.obj;
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.LOAD_AUDIO, audioData);
                    }
                    break;
            }

        }
    };

    private void startSignalServer() {
        new Thread() {
            @Override
            public void run() {
                EventLoopGroup boss = new NioEventLoopGroup();
                EventLoopGroup worker = new NioEventLoopGroup();
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(boss, worker);
                    bootstrap.channel(NioServerSocketChannel.class);
                    bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
                    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("encoder", new GlassMessageEncoder());
                            pipeline.addLast("decoder", new GlassMessageDecoder());

                            pipeline.addLast(new GlassServerSignalHandler(GlassTcpServer.this, mSignalHandler, mSignalChannelContextCallback));
                        }
                    });
                    ChannelFuture signalFuture = bootstrap.bind(GlassConstants.SIGNAL_PORT).sync();
                    if (signalFuture.isSuccess()) {
                        Log.d(TAG, "启动Netty服务成功，端口号：" + GlassConstants.SIGNAL_PORT);
                        startVideoServer();
                    } else {
                        Log.d(TAG, "启动Netty服务失败，端口号：" + GlassConstants.SIGNAL_PORT);
                    }
                    signalFuture.channel().closeFuture().sync();

                } catch (InterruptedException e) {
                    Log.e(TAG, "启动Netty服务异常" + e.getMessage());
                    e.printStackTrace();
                } finally {
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }

            }
        }.start();
    }


    private void startVideoServer() {

        new Thread() {
            @Override
            public void run() {
                EventLoopGroup boss = new NioEventLoopGroup();
                EventLoopGroup worker = new NioEventLoopGroup();
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(boss, worker);
                    bootstrap.channel(NioServerSocketChannel.class);
                    bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
                    bootstrap.option(ChannelOption.TCP_NODELAY, true);
                    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("encoder", new GlassMessageEncoder());
                            pipeline.addLast("decoder", new GlassMessageDecoder());

                            pipeline.addLast(new GlassServerVideoHandler(GlassTcpServer.this, mVideoHandler, mVideoChannelContextCallback));
                        }
                    });
                    ChannelFuture videoFuture = bootstrap.bind(GlassConstants.VIDEO_PORT).sync();
                    if (videoFuture.isSuccess()) {
                        Log.d(TAG, "启动Netty服务成功，端口号：" + GlassConstants.VIDEO_PORT);
                        startFileServer();
                    } else {
                        Log.d(TAG, "启动Netty服务失败，端口号：" + GlassConstants.VIDEO_PORT);
                    }
                    videoFuture.channel().closeFuture().sync();

                } catch (InterruptedException e) {
                    Log.e(TAG, "启动Netty服务异常" + e.getMessage());
                    e.printStackTrace();
                } finally {
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }
            }
        }.start();

    }

    private void startFileServer() {
        new Thread() {
            @Override
            public void run() {
                EventLoopGroup boss = new NioEventLoopGroup();
                EventLoopGroup worker = new NioEventLoopGroup();
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap();
                    bootstrap.group(boss, worker);
                    bootstrap.channel(NioServerSocketChannel.class);
                    bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
                    bootstrap.option(ChannelOption.TCP_NODELAY, true);
                    bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
                    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("encoder", new GlassMessageEncoder());
                            pipeline.addLast("decoder", new GlassMessageDecoder());

                            pipeline.addLast(new GlassServerFileHandler(GlassTcpServer.this, mFileHandler, mFileChannelContextCallback));
                        }
                    });
                    ChannelFuture fileFuture = bootstrap.bind(GlassConstants.FILE_PORT).sync();
                    if (fileFuture.isSuccess()) {
                        Log.d(TAG, "启动Netty服务成功，端口号：" + GlassConstants.FILE_PORT);
                        //server all port start listening
                        mServerStatus = true;
                    } else {
                        Log.d(TAG, "启动Netty服务失败，端口号：" + GlassConstants.FILE_PORT);
                    }
                    fileFuture.channel().closeFuture().sync();

                } catch (InterruptedException e) {
                    Log.e(TAG, "启动Netty服务异常" + e.getMessage());
                    e.printStackTrace();
                } finally {
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }
            }
        }.start();

    }


    private IServerChannelCallback mSignalChannelContextCallback = new IServerChannelCallback() {
        @Override
        public void getChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
            mSignalChannelContext = channelHandlerContext;
        }
    };

    private IServerChannelCallback mVideoChannelContextCallback = new IServerChannelCallback() {
        @Override
        public void getChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
            mVideoChannelContext = channelHandlerContext;
        }
    };

    private IServerChannelCallback mFileChannelContextCallback = new IServerChannelCallback() {
        @Override
        public void getChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
            mFileChannelContext = channelHandlerContext;
        }
    };

    private void sendSignalMessage(final GlassMessage message) {
        mSignalHandler.post(new Runnable() {
            @Override
            public void run() {
                mSignalChannelContext.writeAndFlush(message);
            }
        });
    }

    private void sendVideoMessage(final GlassMessage message) {
        mVideoHandler.post(new Runnable() {
            @Override
            public void run() {
                mVideoChannelContext.writeAndFlush(message);
            }
        });
    }

    private void sendFileMessage(final GlassMessage message) {
        mFileHandler.post(new Runnable() {
            @Override
            public void run() {
                mFileChannelContext.writeAndFlush(message);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        loginGlassStatus = false;
    }

}
