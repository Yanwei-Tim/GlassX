package com.newvision.zeus.glasscore.protocol.entity;

/**
 * Created by yanjiatian on 2017/6/30.
 */

public class GlassErrorCode {

    public static final byte OK = 0;  //无错误，正确
    public static final byte CREATE_JSON_ERR = 1;  //创建Json错误
    public static final byte PARSE_JSON_ERR = 2;  //Parse Jason数据错误
    public static final byte PORT_OCCUPY_ERR = 3;  //端口被占用
    public static final byte VIDEO_ERR = 4;  //端口被占用
    public static final byte PHOTO_ERR = 5;  //端口被占用
    public static final byte CAMERA_ERR = 6;  //摄像头故障
    public static final byte NO_FILE_ERR = 7;  //文件不存在
    public static final byte MIC_ERR = 8;  //麦克风故障
}
