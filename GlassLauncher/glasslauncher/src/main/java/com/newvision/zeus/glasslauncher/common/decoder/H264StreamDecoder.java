package com.newvision.zeus.glasslauncher.common.decoder;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;
import android.view.TextureView;

import com.newvision.zeus.glasslauncher.common.config.AppConfig;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Qing Jiwei on 2016/12/14.
 */

public class H264StreamDecoder implements ReleaseAble {

    private static H264StreamDecoder instance;

    private MediaCodec mCodec;
    private final static String MIME_TYPE = "video/avc"; // H.264 Advanced Video

    private final static int videoWidth = Integer.parseInt(AppConfig.getInstance().getVideoWidth());
    private final static int videoHeight = Integer.parseInt(AppConfig.getInstance().getVideoHeight());

    //true means to checkout the first sps*pps frame
    private boolean isFirstFrame = false;

    private Thread readFileThread;
    private byte[] frame;
    private VideoFrame videoFrame;

    private boolean readFlag = false;

    public static H264StreamDecoder getInstance() {
        if (instance == null) {
            instance = new H264StreamDecoder();
        }
        return instance;
    }

    /***
     * TextureView show
     *
     * @param mTextureView
     */
    public void initDecoder(TextureView mTextureView) {
        try {
            mCodec = MediaCodec.createDecoderByType(MIME_TYPE);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, getVideoWidth(), getVideoHeight());
            mCodec.configure(mediaFormat, new Surface(mTextureView.getSurfaceTexture()), null, 0);
            mCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * TextureView show
     *
     * @param mSurfaceTexture
     */
    public void initDecoder(SurfaceTexture mSurfaceTexture) {
        try {
            mCodec = MediaCodec.createDecoderByType(MIME_TYPE);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, getVideoWidth(), getVideoHeight());
            mCodec.configure(mediaFormat, new Surface(mSurfaceTexture), null, 0);
            mCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * SurfaceView show
     *
     * @param mSurface
     */
    public void initDecoder(Surface mSurface) {
        try {
            mCodec = MediaCodec.createDecoderByType(MIME_TYPE);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, getVideoWidth(), getVideoHeight());
            mCodec.configure(mediaFormat, mSurface, null, 0);
            mCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDecodeFrame() {
        if (!readFlag) {
            readFlag = true;
            readFileThread = new Thread(decoderStream);
            readFileThread.start();
        }
    }

    Runnable decoderStream = new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                List<VideoFrame> framesList = StreamManager.getInstance().getshowPool();
                if (framesList.size() != 0) {
                    toDecodeFrame();
                }
            }

        }
    };

    private void toDecodeFrame() {

        videoFrame = StreamManager.getInstance().getshowPool().get(0);
        frame = videoFrame.getData();

        //To determine whether the sps frame
//        if (isFirstFrame) {
//            if (videoFrame.getFlags() != 0) {
//                StreamManager.getInstance().removeShow(videoFrame);
//                videoFrame.recycle();
//                return;
//            }
//        } else {
//            if (videoFrame.getFlags() == 0) {
//                StreamManager.getInstance().removeShow(videoFrame);
//                videoFrame.recycle();
//                return;
//            }
//        }
//        isFirstFrame = false;

        try {
            ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
            int inputIndex = mCodec.dequeueInputBuffer(0);
            // If the buffer number is valid use the buffer with that index
            if (inputIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputIndex];
                inputBuffer.clear();

                try {
                    inputBuffer.put(frame);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                // Tell the decoder to process the frame
                mCodec.queueInputBuffer(inputIndex, 0, frame.length, 0, 0);

                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                int outputIndex = mCodec.dequeueOutputBuffer(info, 0);
                if (outputIndex >= 0) {
                    mCodec.releaseOutputBuffer(outputIndex, true);
                }
                try {
                    StreamManager.getInstance().removeShow(videoFrame);
                    videoFrame.recycle();
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {

        }
    }


    /**
     * 设置解码分辨率，以后同步与config终参数
     *
     * @return
     */
    public static int getVideoWidth() {
        return videoWidth;
    }

    public static int getVideoHeight() {
        return videoHeight;
    }

    /**
     * Check if is H264 frame head
     *
     * @param buffer
     * @param offset
     * @return whether the src buffer is frame head
     */
    private boolean checkHead(byte[] buffer, int offset) {
        // 00 00 00 01
        if (buffer[offset] == 0 && buffer[offset + 1] == 0
                && buffer[offset + 2] == 0 && buffer[3] == 1)
            return true;
        // 00 00 01
        if (buffer[offset] == 0 && buffer[offset + 1] == 0
                && buffer[offset + 2] == 1)
            return true;
        return false;
    }

    @Override
    public void startRelease() throws Exception {

        if (readFileThread != null) {
            readFileThread.interrupt();
            mCodec.stop();
            mCodec.release();
            instance = null;
        }
    }
}
