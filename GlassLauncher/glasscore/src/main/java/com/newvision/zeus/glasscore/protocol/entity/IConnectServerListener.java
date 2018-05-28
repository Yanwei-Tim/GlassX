package com.newvision.zeus.glasscore.protocol.entity;

/**
 * Created by yanjiatian on 2017/7/10.
 */

public interface IConnectServerListener {
    //1.连接服务端是否成功 2.服务端返回的数据
    void getConnectServerStatus(boolean status, GlassDevicesInfo info);

    //连接服务的企图（pair/connect）
    int getType();

    //连接服务器的IP
    String getIP();
}
