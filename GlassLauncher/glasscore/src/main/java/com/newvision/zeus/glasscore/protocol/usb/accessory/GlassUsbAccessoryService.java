package com.newvision.zeus.glasscore.protocol.usb.accessory;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.helper.GlassPacketMessageHelper;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glasscore.utils.GlassMessageHeadUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanjiatian on 2017/8/4.
 * USB accessory 接入时启动此服务
 */

public class GlassUsbAccessoryService extends Service {
    private static final String TAG = GlassUsbAccessoryService.class.getSimpleName();
    private AccessoryCommunicator mCommunicator;
    private List<IMessageCallback> listeners = new ArrayList<>();
    private boolean loginStatus = false;
    private long sendCount = 0;
    private long receiveCount = 0;

    public class AccessoryServiceBinder extends Binder {
        public void connectServer() {
            startConnectServer();
        }

        public void disconnectServer() {
            if (mCommunicator != null && loginStatus) {
                mCommunicator.closeAccessory();
            }
        }

        public void sendMessage(GlassMessage glassMessage) {
            sendGlassMessage(glassMessage);
        }

        public boolean getLoginGlassStatus() {
            return loginStatus;
        }

        public void registerListener(IMessageCallback callback) {
            listeners.add(callback);
        }

        public void unregisterListener(IMessageCallback callback) {
            listeners.remove(callback);
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AccessoryServiceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "GlassUsbAccessoryService start ... ");
    }

    private Handler mReceiveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GlassMessageType.GLASS_KEEP_ALIVE:
                    Log.d(TAG, "accessory keep alive");
                    sendGlassMessage(GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_KEEP_ALIVE, GlassErrorCode.OK, null));
                    break;
                case GlassMessageType.GLASS_LOGIN_ASK:
                    Log.d(TAG, "login ask" + " client model is :" + Build.MODEL);
                    sendGlassMessage(GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_LOGIN_ASK, GlassErrorCode.OK, Build.MODEL.getBytes()));
                    break;
                case GlassMessageType.GLASS_LOGIN_ANS:
                    Log.d(TAG, "login ans");
                    Log.d(TAG, "server model is :" + new String(((GlassMessage) msg.obj).messageBody));
                    loginStatus = true;
                    sendGlassMessage(GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_KEEP_ALIVE, GlassErrorCode.OK, null));
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

                case GlassMessageType.LOAD_FILE:
                    GlassMessage photosData = (GlassMessage) msg.obj;
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.LOAD_FILE, photosData);
                    }
                    break;


            }
        }
    };

    private synchronized void sendGlassMessage(GlassMessage glassMessage) {
        Log.d(TAG, "accessory send  type : " + Integer.toHexString(glassMessage.messageType) + " length = " + glassMessage.messageLength + " sendCount ------> " + sendCount++);
        if (mCommunicator != null) {
            if (glassMessage.messageLength == 0) {
                mCommunicator.sendMessage(GlassMessageHeadUtils.packetCommonHeader(glassMessage));
            } else {
                byte[] body = glassMessage.messageBody;
                if (body.length <= GlassConstants.PACKAGE_MAX_BODY) {
                    byte[] messageHead = GlassMessageHeadUtils.packetCommonHeader(glassMessage);
                    byte[] total = GlassMessageHeadUtils.byteMerger(messageHead, glassMessage.messageBody);
                    mCommunicator.sendMessage(total);
                } else {
                    int remainder = body.length % GlassConstants.PACKAGE_MAX_BODY;
                    int packages = remainder == 0 ? (int) (body.length / GlassConstants.PACKAGE_MAX_BODY) : (int) (body.length / GlassConstants.PACKAGE_MAX_BODY) + 1;
                    for (int i = 0; i < packages; i++) {
                        if (i == packages - 1) { //最后一个包
                            Log.d(TAG, "最后一个包 packages count = " + packages);
                            GlassMessage msg = new GlassMessage();
                            msg.messageType = glassMessage.messageType;
                            msg.pkgType = GlassConstants.PACKAGE_LAST;
                            msg.messageLength = remainder;
                            byte[] messageHead = GlassMessageHeadUtils.packetCommonHeader(msg);
                            byte[] total = GlassMessageHeadUtils.byteMerger(messageHead, body);
                            mCommunicator.sendMessage(total);
                        } else {
                            GlassMessage msg = new GlassMessage();
                            msg.messageType = glassMessage.messageType;
                            msg.pkgType = GlassConstants.PACKAGE_HAS_NEXT;
                            msg.messageLength = GlassConstants.PACKAGE_MAX_BODY;
                            byte[] messageHead = GlassMessageHeadUtils.packetCommonHeader(msg);
                            byte[] part = GlassMessageHeadUtils.subBytes(body, 0, GlassConstants.PACKAGE_MAX_BODY);
                            byte[] total = GlassMessageHeadUtils.byteMerger(messageHead, part);
                            mCommunicator.sendMessage(total);
                            body = GlassMessageHeadUtils.subBytes(body, GlassConstants.PACKAGE_MAX_BODY, body.length - GlassConstants.PACKAGE_MAX_BODY);
                        }
                    }
                }

            }
        } else {
            Log.d(TAG, "mCommunicator is null");
        }

    }

    private void startConnectServer() {
        mCommunicator = new AccessoryCommunicator(this) {
            @Override
            public void onReceive(GlassMessage glassMessage) {
                Log.d(TAG, "receive message type = " + Integer.toHexString(glassMessage.messageType) + "  length = " + glassMessage.messageLength + "  receiveCount ----> " + receiveCount++);
                if (glassMessage.messageType == GlassMessageType.GLASS_KEEP_ALIVE) {
                    mReceiveHandler.sendEmptyMessageDelayed(GlassMessageType.GLASS_KEEP_ALIVE, 5000);
                    for (int i = 0; i < listeners.size(); i++) {
                        listeners.get(i).getResult(GlassMessageType.GLASS_KEEP_ALIVE, "usb");
                    }
                } else {
                    Message msg = mReceiveHandler.obtainMessage();
                    msg.what = glassMessage.messageType;
                    msg.obj = glassMessage;
                    mReceiveHandler.sendMessage(msg);
                }
            }

            @Override
            public void onError(String msg) {
                Log.e(TAG, "onError ..." + msg);
            }

            @Override
            public void onConnected() {
                Log.d(TAG, "connected usb server success ...");
                mReceiveHandler.sendEmptyMessage(GlassMessageType.GLASS_LOGIN_ASK);
            }

            @Override
            public void onDisconnected() {
                loginStatus = false;
                mCommunicator = null;
                Log.d(TAG, "disconnected usb server ...");
            }
        };
    }


}
