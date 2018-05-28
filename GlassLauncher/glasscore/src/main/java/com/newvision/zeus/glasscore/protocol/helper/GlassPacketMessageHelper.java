package com.newvision.zeus.glasscore.protocol.helper;

import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yanjiatian on 2017/6/30.
 */

public class GlassPacketMessageHelper {
    private static final String TAG = GlassPacketMessageHelper.class.getSimpleName();

    private static GlassPacketMessageHelper mInstance;

    public static GlassPacketMessageHelper getInstance() {
        if (mInstance == null) {
            mInstance = new GlassPacketMessageHelper();
        }
        return mInstance;
    }

    public GlassMessage packetMessage(short type, byte errorCode, byte[] body) {
        GlassMessage message = new GlassMessage();
        switch (type) {
            case GlassMessageType.GLASS_LOGIN_ASK:
            case GlassMessageType.GLASS_LOGIN_ANS:

            case GlassMessageType.GLASS_KEEP_ALIVE:

            case GlassMessageType.GLASS_START_PREVIEW_ASK:
            case GlassMessageType.GLASS_START_PREVIEW_ANS:
            case GlassMessageType.PHONE_START_PREVIEW_ASK:
            case GlassMessageType.PHONE_START_PREVIEW_ANS:

            case GlassMessageType.GLASS_STOP_PREVIEW_ASK:
            case GlassMessageType.GLASS_STOP_PREVIEW_ANS:
            case GlassMessageType.PHONE_STOP_PREVIEW_ASK:
            case GlassMessageType.PHONE_STOP_PREVIEW_ANS:

            case GlassMessageType.GLASS_SEND_COMMAND_ASK:
            case GlassMessageType.GLASS_SEND_COMMAND_ANS:
            case GlassMessageType.PHONE_SEND_COMMAND_ASK:
            case GlassMessageType.PHONE_SEND_COMMAND_ANS:

            case GlassMessageType.GLASS_START_LOAD_FILE_ASK:
            case GlassMessageType.GLASS_START_LOAD_FILE_ANS:
            case GlassMessageType.PHONE_START_LOAD_FILE_ASK:
            case GlassMessageType.PHONE_START_LOAD_FILE_ANS:

            case GlassMessageType.GLASS_STOP_LOAD_FILE_ASK:
            case GlassMessageType.GLASS_STOP_LOAD_FILE_ANS:
            case GlassMessageType.PHONE_STOP_LOAD_FILE_ASK:
            case GlassMessageType.PHONE_STOP_LOAD_FILE_ANS:

            case GlassMessageType.GLASS_START_AUDIO_ASK:
            case GlassMessageType.GLASS_START_AUDIO_ANS:
            case GlassMessageType.GLASS_STOP_AUDIO_ASK:
            case GlassMessageType.GLASS_STOP_AUDIO_ANS:

            case GlassMessageType.LOAD_H264:
            case GlassMessageType.LOAD_FILE:
            case GlassMessageType.LOAD_AUDIO:

                message.messageType = type;
                message.errorCode = errorCode;
                if (body != null) {
                    message.messageLength = body.length;
                    message.messageBody = body;
                } else {
                    message.messageLength = 0;
                }
                break;
            case GlassMessageType.GLASS_OPEN_APP_ASK:
            case GlassMessageType.GLASS_OPEN_APP_ANS:
            case GlassMessageType.PHONE_OPEN_APP_ASK:
            case GlassMessageType.PHONE_OPEN_APP_ANS:

            case GlassMessageType.GLASS_CLOSE_APP_ASK:
            case GlassMessageType.GLASS_CLOSE_APP_ANS:
            case GlassMessageType.PHONE_CLOSE_APP_ASK:
            case GlassMessageType.PHONE_CLOSE_APP_ANS:
                message.messageType = type;
                message.errorCode = errorCode;
                if (body != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        String pakInfo = new String(body, "utf-8");
                        jsonObject.put("pkg", pakInfo);
                        String data = jsonObject.toString();
                        message.messageLength = data.length();
                        message.messageBody = data.getBytes();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    message.messageLength = 0;
                }
                break;
        }
        return message;
    }

}
