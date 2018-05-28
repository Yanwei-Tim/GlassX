package com.newvision.zeus.glasscore.protocol.entity;

/**
 * Created by yanjiatian on 2017/6/27.
 */

public class GlassMessageType {

    public static final short START_SIGNAL_SERVER = 0x0001;  //信号通道
    public static final short CONNECT_SIGNAL_SERVER = 0x0002;  //信号通道
    public static final short CONNECT_VIDEO_SERVER = 0x0003;  //视频流通道
    public static final short CONNECT_FILE_SERVER = 0x0004;  //文件通道
    public static final short GLASS_PAIR = 0x0005;  //配对

    //按照文档整理命令
    //登录
    public static final short GLASS_LOGIN_ASK = 0x1001;   //登录请求
    public static final short GLASS_LOGIN_ANS = 0x1002;   //登录响应
    //心跳
    public static final short GLASS_KEEP_ALIVE = 0x1003;  //心跳检测
    //系统时间
    public static final short GLASS_GET_SYS_TIME_ASK = 0x1010;
    public static final short GLASS_GET_SYS_TIME_ANS = 0x1011;
    public static final short GLASS_SET_SYS_TIME_ASK = 0x1012;
    public static final short GLASS_SET_SYS_TIME_ANS = 0x1013;
    //系统信息
    public static final short GLASS_GET_SYS_INFO_ASK = 0x1014;
    public static final short GLASS_GET_SYS_INFO_ANS = 0x1015;
    //硬件信息
    public static final short GLASS_GET_DEV_INFO_ASK = 0x1016;
    public static final short GLASS_GET_DEV_INFO_ANS = 0x1017;
    //音量
    public static final short GLASS_GET_VOLUME_ASK = 0x1018;
    public static final short GLASS_GET_VOLUME_ANS = 0x1019;
    public static final short GLASS_SET_VOLUME_ASK = 0x101A;
    public static final short GLASS_SET_VOLUME_ANS = 0x101B;
    //静音
    public static final short GLASS_GET_MUTE_ASK = 0x1020;
    public static final short GLASS_GET_MUTE_ANS = 0x1021;
    public static final short GLASS_SET_MUTE_ASK = 0x1022;
    public static final short GLASS_SET_MUTE_ANS = 0x1023;
    //屏幕亮度
    public static final short GLASS_GET_BRIGHTNESS_ASK = 0x1024;
    public static final short GLASS_GET_BRIGHTNESS_ANS = 0x1025;
    public static final short GLASS_SET_BRIGHTNESS_ASK = 0x1026;
    public static final short GLASS_SET_BRIGHTNESS_ANS = 0x1027;
    //屏幕息屏时间
    public static final short GLASS_GET_LOCK_SCREEN_ASK = 0x1028;
    public static final short GLASS_GET_LOCK_SCREEN_ANS = 0x1029;
    public static final short GLASS_SET_LOCK_SCREEN_ASK = 0x102A;
    public static final short GLASS_SET_LOCK_SCREEN_ANS = 0x102B;
    //Wi-Fi
    public static final short GLASS_GET_WIFI_INFO_ASK = 0x1030;
    public static final short GLASS_GET_WIFI_INFO_ANS = 0x1031;
    //蓝牙
    public static final short GLASS_GET_BT_INFO_ASK = 0x1032;
    public static final short GLASS_GET_BT_INFO_ANS = 0x1033;
    //自定义按键
    public static final short GLASS_GET_KEY_MSG_ASK = 0x1034;
    public static final short GLASS_GET_KEY_MSG_ANS = 0x1035;
    public static final short GLASS_SET_KEY_MSG_ASK = 0x1036;
    public static final short GLASS_SET_KEY_MSG_ANS = 0x1037;
    //应用列表
    public static final short GLASS_GET_APP_LIST_ASK = 0x1038;
    public static final short GLASS_GET_APP_LIST_ANS = 0x1039;
    public static final short GLASS_GET_APP_ICON_ASK = 0x103A;
    public static final short GLASS_GET_APP_ICON_ANS = 0x103B;
    //语音命令词
    public static final short GLASS_GET_VOICE_LIST_ASK = 0x1040;
    public static final short GLASS_GET_VOICE_LIST_ANS = 0x1041;
    public static final short GLASS_SET_VOICE_LIST_ASK = 0x1042;
    public static final short GLASS_SET_VOICE_LIST_ANS = 0x1043;

    //打开关闭内置模块
    public static final short GLASS_OPEN_APP_ASK = 0x1044;
    public static final short GLASS_OPEN_APP_ANS = 0x1045;
    public static final short GLASS_CLOSE_APP_ASK = 0x1046;
    public static final short GLASS_CLOSE_APP_ANS = 0x1047;
    public static final short PHONE_OPEN_APP_ASK = 0x1048;
    public static final short PHONE_OPEN_APP_ANS = 0x1049;
    public static final short PHONE_CLOSE_APP_ASK = 0x104A;
    public static final short PHONE_CLOSE_APP_ANS = 0x104B;
    //系统相机参数设置
    public static final short GLASS_GET_CAMERA_PARA_ASK = 0x1050;
    public static final short GLASS_GET_CAMERA_PARA_ANS = 0x1051;
    public static final short GLASS_SET_CAMERA_PARA_ASK = 0x1052;
    public static final short GLASS_SET_CAMERA_PARA_ANS = 0x1053;
    //获取视频流数据
    public static final short GLASS_START_PREVIEW_ASK = 0x1060;
    public static final short GLASS_START_PREVIEW_ANS = 0x1061;
    public static final short GLASS_STOP_PREVIEW_ASK = 0x1062;
    public static final short GLASS_STOP_PREVIEW_ANS = 0x1063;
    public static final short PHONE_START_PREVIEW_ASK = 0x1064;
    public static final short PHONE_START_PREVIEW_ANS = 0x1065;
    public static final short PHONE_STOP_PREVIEW_ASK = 0x1066;
    public static final short PHONE_STOP_PREVIEW_ANS = 0x1067;
    //获取音频流数据
    public static final short GLASS_START_AUDIO_ASK = 0x1070;
    public static final short GLASS_START_AUDIO_ANS = 0x1071;
    public static final short GLASS_STOP_AUDIO_ASK = 0x1072;
    public static final short GLASS_STOP_AUDIO_ANS = 0x1073;
    //自定义指令
    public static final short GLASS_SEND_COMMAND_ASK = 0x1080;
    public static final short GLASS_SEND_COMMAND_ANS = 0x1081;
    public static final short PHONE_SEND_COMMAND_ASK = 0x1082;
    public static final short PHONE_SEND_COMMAND_ANS = 0x1083;
    //文件传输
    public static final short GLASS_START_LOAD_FILE_ASK = 0x1090;
    public static final short GLASS_START_LOAD_FILE_ANS = 0x1091;
    public static final short GLASS_STOP_LOAD_FILE_ASK = 0x1092;
    public static final short GLASS_STOP_LOAD_FILE_ANS = 0x1093;
    public static final short PHONE_START_LOAD_FILE_ASK = 0x1094;
    public static final short PHONE_START_LOAD_FILE_ANS = 0x1095;
    public static final short PHONE_STOP_LOAD_FILE_ASK = 0x1096;
    public static final short PHONE_STOP_LOAD_FILE_ANS = 0x1097;
    //H264视频流传输
    public static final short LOAD_H264 = 0x2001;
    //Audio音频传输
    public static final short LOAD_AUDIO = 0x2002;
    //文件传输
    public static final short LOAD_FILE = 0x3001;

}
