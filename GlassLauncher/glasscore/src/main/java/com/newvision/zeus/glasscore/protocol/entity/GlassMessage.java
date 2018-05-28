package com.newvision.zeus.glasscore.protocol.entity;

/**
 * Created by yanjiatian on 2017/6/26.
 * 眼镜端与手机端之间传递的消息实体
 * 协议传输按照标准的TLV来进行传递
 */

public class GlassMessage {
    public short messageType;   //消息类型
    public byte pkgType;       //是否为一个完整的包
    public byte errorCode;        //错误码
    public int messageLength;   //附加消息体长度
    public byte[] messageBody;  //附加消息体
}
