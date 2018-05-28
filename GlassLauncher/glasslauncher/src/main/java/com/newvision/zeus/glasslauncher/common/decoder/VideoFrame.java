package com.newvision.zeus.glasslauncher.common.decoder;

import android.media.MediaCodec;

/**
 * Defines a H264 stream frame
 * <p>
 * Created by Qing Jiwei on 8/25/17.
 */


public class VideoFrame {

    private static final Object sPoolSync = new Object();

    private static VideoFrame sPool;
    private VideoFrame next;
    private MediaCodec.BufferInfo bufferInfo;

    private static int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 50;

    private int flags;
    private byte[] data;

    private VideoFrame() {
    }

    private VideoFrame(byte[] data) {
        this.data = data;
        this.flags = parseFrameType(data);
    }

    private VideoFrame(byte[] data, MediaCodec.BufferInfo bufferInfo) {
        this.data = data;
        this.flags = parseFrameType(data);
        this.bufferInfo = bufferInfo;
    }


    public static VideoFrame obtain(byte[] data) {
        synchronized (sPoolSync) {
            if (sPool != null) {
                VideoFrame frame = sPool;
                sPool = frame.next;
                frame.next = null;
                frame.data = data;
                frame.flags = parseFrameType(data);
                sPoolSize--;
                return frame;
            }
        }
        return new VideoFrame(data);
    }

    public static VideoFrame obtain(byte[] data, MediaCodec.BufferInfo bufferInfo) {
        synchronized (sPoolSync) {
            if (sPool != null) {
                VideoFrame frame = sPool;
                sPool = frame.next;
                frame.next = null;
                frame.data = data;
                frame.bufferInfo = bufferInfo;
                sPoolSize--;
                return frame;
            }
        }
        return new VideoFrame(data, bufferInfo);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    /**
     * parse frame type
     *
     * @param buffer frame data
     * @return 0 means sps&pps
     */
    private static int parseFrameType(byte[] buffer) {

        // 00 00 00 01
        if (buffer[0] == 0 && buffer[1] == 0
                && buffer[2] == 0 && buffer[3] == 1) {
            if ((buffer[4] & 0x1f) == 7) {
                return 0;
            } else {
                return 1;
            }
        }
        return 1;
    }

    public void recycle() {
        data = null;
        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

}
