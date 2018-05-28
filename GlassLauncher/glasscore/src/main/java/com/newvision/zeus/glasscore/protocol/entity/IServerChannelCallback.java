package com.newvision.zeus.glasscore.protocol.entity;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by yanjiatian on 2017/7/7.
 */

public interface IServerChannelCallback {
    void getChannelHandlerContext(ChannelHandlerContext channelHandlerContext);
}
