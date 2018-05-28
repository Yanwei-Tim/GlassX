package com.newvision.martin.clouddetect.common.data.websocket;

import com.newvision.martin.clouddetect.common.helper.NetworkStateWatcher;
import com.newvision.martin.clouddetect.common.helper.ScreenStateWatcher;
import com.newvision.martin.clouddetect.common.util.LogUtil;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * Created by zhangsong on 17-3-31.
 */

public abstract class WebSocket {
    private static final String TAG = "WebSocket";

    private String wsUrl;

    private WebSocketClient client;

    public WebSocket() {
        wsUrl = Constant.URL_WS;
    }

    public WebSocket(String url) {
        wsUrl = url;
    }

    public WebSocket connect() {
        if (client != null && client.isOpen()) {
            return this;
        }

        initClientAndConnect();
        initScreenStateWatcher();
        return this;
    }

    public boolean send(String text) {
        if (client == null)
            throw new IllegalStateException("Please connect the websocket first.");

        if (!client.isOpen())
            return false;

        client.send(text);
        return true;
    }

    public boolean send(byte[] bytes) {
        if (client == null)
            throw new IllegalStateException("Please connect the websocket first.");

        if (!client.isOpen())
            return false;

        client.send(bytes);
        return true;
    }

    public boolean send(ByteBuffer buffer) {
        if (client == null)
            throw new IllegalStateException("Please connect the websocket first.");

        if (!client.isOpen())
            return false;

        client.send(buffer);
        return true;
    }

    public WebSocket disconnect() {
        client.close();
        NetworkStateWatcher.getInstance().removeListener(onNetworkStateChangeListener);
        ScreenStateWatcher.getInstance().removeListener(onScreenStateChangeListener);
        return this;
    }

    public abstract void onConnect(ServerHandshake handshake);

    public void onMessage(String message) {
    }

    public void onMessage(ByteBuffer buffer) {
    }

    public abstract void onDisconnect(int code);

    public abstract void onException(Exception e);

    private void initClientAndConnect() {
        client = new WebSocketClient(URI.create(wsUrl)) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                LogUtil.i(TAG, "WebSocket connect.");
                // The web socket is connected, so remove the network state watcher.
                NetworkStateWatcher.getInstance().removeListener(onNetworkStateChangeListener);

                onConnect(handshake);
            }

            @Override
            public void onMessage(String message) {
                WebSocket.this.onMessage(message);
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                super.onMessage(bytes);
                WebSocket.this.onMessage(bytes);
            }

            /**
             * 服务端断开socket：code: 1006, reason: , remote: true
             * 客户端断网： code: 1006, reason: , remote: true
             *
             * @see CloseFrame for code value.
             *
             * 如果code是{@link CloseFrame#ABNORMAL_CLOSE}（1006），是web socket已经连接上之后发生了断开现象，
             * 有两种情况： 1.本机断网 2.服务端断开连接
             * 如果code是{@link CloseFrame#NEVER_CONNECTED}（-1），是web socket在网络未连接的状态进行了连接。
             * 所以这两种情况都需要监听网络，尝试进行web socket的重连。
             *
             * @param code
             * @param reason
             * @param remote
             */
            @Override
            public void onClose(int code, String reason, boolean remote) {
                LogUtil.i(TAG, "WebSocket closed, code: " + code + ", reason: " + reason + ", remote: " + remote);
                // Web socket closed because of network, so set a network state watcher to reconnect the web socket.
                if (code == CloseFrame.ABNORMAL_CLOSE || code == CloseFrame.NEVER_CONNECTED)
                    initNetworkStateWatcher();

                onDisconnect(code);
            }

            @Override
            public void onError(Exception ex) {
                LogUtil.i(TAG, "WebSocket error, ex: " + ex.getMessage());
                onException(ex);

                LogUtil.i(TAG, "socket is open? " + client.isOpen());
            }
        };
        client.connect();
    }

    private void initNetworkStateWatcher() {
        NetworkStateWatcher.getInstance().addListener(onNetworkStateChangeListener);
    }

    private void initScreenStateWatcher() {
        ScreenStateWatcher.getInstance().addListener(onScreenStateChangeListener);
    }

    private NetworkStateWatcher.OnNetworkStateChangeListener onNetworkStateChangeListener = new NetworkStateWatcher.OnNetworkStateChangeListener() {
        @Override
        public void onConnect() {
            if (client != null && client.isClosed())
                initClientAndConnect();
        }

        @Override
        public void onDisconnect() {
        }
    };

    private ScreenStateWatcher.OnScreenStateChangeListener onScreenStateChangeListener = new ScreenStateWatcher.OnScreenStateChangeListener() {
        @Override
        public void onScreenOn() {
            if (client != null && client.isClosed())
                initClientAndConnect();
        }

        @Override
        public void onScreenOff() {
        }
    };
}
