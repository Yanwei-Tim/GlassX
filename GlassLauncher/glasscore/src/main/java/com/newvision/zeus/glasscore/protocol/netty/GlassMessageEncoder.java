package com.newvision.zeus.glasscore.protocol.netty;

import android.util.Log;
import android.widget.Toast;

import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.utils.GlassMessageHeadUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by yanjiatian on 2017/6/29.
 */

public class GlassMessageEncoder extends MessageToByteEncoder<GlassMessage> {
    private static final String TAG = GlassMessageEncoder.class.getSimpleName();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, GlassMessage glassMessage, ByteBuf byteBuf) throws Exception {
        if (glassMessage.messageLength == 0) {
            byteBuf.writeBytes(GlassMessageHeadUtils.packetCommonHeader(glassMessage));
        } else {
            byte[] messageHead = GlassMessageHeadUtils.packetCommonHeader(glassMessage);
            byte[] total = GlassMessageHeadUtils.byteMerger(messageHead, glassMessage.messageBody);
            byteBuf.writeBytes(total);
        }
    }
}
