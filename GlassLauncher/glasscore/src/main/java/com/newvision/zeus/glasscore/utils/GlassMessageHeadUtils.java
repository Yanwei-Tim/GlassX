package com.newvision.zeus.glasscore.utils;

import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;

/**
 * Created by yanjiatian on 2017/6/30.
 * byte[] 高位在前，低位在后
 */

public class GlassMessageHeadUtils {
    public static byte[] packetCommonHeader(GlassMessage message) {
        byte[] head = new byte[8];
        head[0] = (byte) ((message.messageType >> 8) & 0xFF);
        head[1] = (byte) (message.messageType & 0xFF);
        head[2] = (byte) (message.pkgType & 0xFF);
        head[3] = (byte) (message.errorCode & 0xFF);
        head[4] = (byte) ((message.messageLength >> 24) & 0xFF);
        head[5] = (byte) ((message.messageLength >> 16) & 0xFF);
        head[6] = (byte) ((message.messageLength >> 8) & 0xFF);
        head[7] = (byte) (message.messageLength & 0xFF);
        return head;
    }

    public static GlassMessage parseCommonHead(byte[] head) {
        GlassMessage message = new GlassMessage();
        message.messageType = (short) ((head[0] << 8) + head[1]);
        message.pkgType = head[2];
        message.errorCode = head[3];
        message.messageLength = ((head[4] & 0xff) << 24) | ((head[5] & 0xff) << 16) | ((head[6] & 0xff) << 8) | (head[7] & 0xff);

        return message;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }


    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

}
