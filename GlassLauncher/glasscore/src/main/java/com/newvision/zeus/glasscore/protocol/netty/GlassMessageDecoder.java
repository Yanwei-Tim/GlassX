package com.newvision.zeus.glasscore.protocol.netty;


import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.utils.GlassMessageHeadUtils;

import java.util.List;

import javax.security.auth.login.LoginException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Created by yanjiatian on 2017/6/29.
 */

public class GlassMessageDecoder extends ByteToMessageDecoder {
    private static final String TAG = GlassMessageDecoder.class.getSimpleName();
    private static final int HEAD_LENGTH = 8;
    private GlassMessage mGlassMessage = null;
    private boolean mReadMessageFirst = true;   //是否首次读取一个包内容
    private int mReleaseLength = 0;
    private byte[] mTotalBody = null;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int readable = byteBuf.readableBytes();
        if (mReadMessageFirst) {
            mReadMessageFirst = false;
            mGlassMessage = null;
            mTotalBody = null;
            mReleaseLength = 0;

            if (readable < HEAD_LENGTH) {
                mReadMessageFirst = true;
                return;
            }
            byte[] messageHead = new byte[HEAD_LENGTH];
            byteBuf.readBytes(messageHead);
            mGlassMessage = GlassMessageHeadUtils.parseCommonHead(messageHead);
            int messageLength = mGlassMessage.messageLength;
            if (messageLength == 0) {
                mReadMessageFirst = true;
                list.add(mGlassMessage);
            } else {
                readable = byteBuf.readableBytes();
                if (messageLength <= readable) {
                    byte[] body = new byte[messageLength];
                    byteBuf.readBytes(body);
                    mGlassMessage.messageBody = body;
                    mReadMessageFirst = true;
                    list.add(mGlassMessage);
                } else {
                    byte[] part = new byte[readable];
                    byteBuf.readBytes(part);
                    mTotalBody = part;
                    mReleaseLength = messageLength - readable;
                }
            }
        } else {
            readable = byteBuf.readableBytes();
            if (mReleaseLength <= readable) {
                byte[] other = new byte[mReleaseLength];
                byteBuf.readBytes(other);
                mTotalBody = GlassMessageHeadUtils.byteMerger(mTotalBody, other);
                mGlassMessage.messageBody = mTotalBody;
                mReadMessageFirst = true;
                list.add(mGlassMessage);
            } else {
                byte[] part = new byte[readable];
                byteBuf.readBytes(part);
                mTotalBody = GlassMessageHeadUtils.byteMerger(mTotalBody, part);
                mReleaseLength -= readable;
            }
        }
    }
}
