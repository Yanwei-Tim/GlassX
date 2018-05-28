package com.newvision.zeus.glasscore.protocol.entity;

/**
 * Created by yanjiatian on 2016/12/13.
 */

public class GlassVideoHeader {

    public long pts; //时间戳（微秒为单位）
    public int frame_n;  //帧序列号帧序列号（累加）
    public short pck_n; //packet 序列号，开始为|0x4000 ,末尾为|0x8000,仿照RTP传输协议, 如果是TCP传输,则payload_len为整帧长度，pck_n填为0xffff
    public short frame_type; //视频中帧的类型（主要针对H264和H265），0:非关键帧,1:关键帧
    public int payload_len; //payload 长度

}
