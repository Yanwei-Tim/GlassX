package com.newvision.martin.clouddetect.ui;

import com.google.protobuf.ByteString;
import com.newvision.martin.clouddetect.common.data.proto.ARDetectEntity;
import com.newvision.martin.clouddetect.common.data.websocket.WebSocket;
import com.newvision.martin.clouddetect.common.util.DetectUtil;
import com.newvision.martin.clouddetect.common.util.LogUtil;

import org.java_websocket.handshake.ServerHandshake;

/**
 * Created by zhangsong on 17-4-21.
 */

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";

    private WebSocket webSocket;

    public WebSocketManager() {
        webSocket = new WebSocket() {
            @Override
            public void onConnect(ServerHandshake handshake) {
                LogUtil.i(TAG, "web socket connected.");

                send(geneRequest());
            }

            @Override
            public void onDisconnect(int code) {
                LogUtil.i(TAG, "web socket disconnected.");
            }

            @Override
            public void onException(Exception e) {
            }
        };
    }

    public void connect() {
        webSocket.connect();
    }

    public void send(byte[] bytes) {
        boolean result = webSocket.send(bytes);
        LogUtil.i(TAG, "send request " + result);
    }

    public void disconnect() {
        webSocket.disconnect();
    }

    private byte[] geneRequest() {
        ARDetectEntity.DetectionRequest.Builder reqBuilder = ARDetectEntity.DetectionRequest.newBuilder();
        reqBuilder.setId(DetectUtil.getId());
        reqBuilder.setUserID(DetectUtil.getUserId());
        reqBuilder.setTarget("video_start");
        reqBuilder.setTargetId("video_start_01");
        reqBuilder.setEngine("live_video");
        reqBuilder.setWidth(0);
        reqBuilder.setHeight(0);
        reqBuilder.setImage(ByteString.EMPTY);
        reqBuilder.setLictoken("8dc27e9f6ad80ecd86cfeba7b2b22e31");
        reqBuilder.setAppKey("Y2VkNDY1ODEzZGI5NjU0ZWnfG493csFRvxiRk66OjNZVE2104K0UxP");
        ARDetectEntity.Entity.Builder builder = ARDetectEntity.Entity.newBuilder();
        builder.setEType(3);
        builder.setDetectionReq(reqBuilder);
        return builder.build().toByteArray();
    }
}
