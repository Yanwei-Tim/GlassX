package com.newvision.zeus.glasscore.protocol.entity;

/**
 * Created by yanjiatian on 2017/6/29.
 */

public class GlassConstants {

    public static final int SIGNAL_PORT = 5801;  //信令通道
    public static final int VIDEO_PORT = 5802;   //视频流通道
    public static final int FILE_PORT = 5803;    //文件通道
    public static boolean STATUS_SERVICE = false;

    //usb
    public static final int USB_TIMEOUT_IN_MS = 15;

    public static final int BUFFER_SIZE_IN_BYTES = 1024 * 16;  //usb  传输最大一次能够传输16K数据

    public static final int GLASS_MESSAGE_HEAD_LENGTH = 8;

    public static final int PACKAGE_MAX_BODY = BUFFER_SIZE_IN_BYTES - GLASS_MESSAGE_HEAD_LENGTH;  //usb  传输最大一次能够携带的有效数据


    public static final byte PACKAGE_NORMAL = 0;  //一个包后面还有片段
    public static final byte PACKAGE_HAS_NEXT = 1;  //一个包后面还有片段
    public static final byte PACKAGE_LAST = 2;  //最后一个片段


    public static final String INNER_APP_CAMERA = "inner_app_camera";
    public static final String INNER_APP_MARTIN = "inner_app_martin";

}
