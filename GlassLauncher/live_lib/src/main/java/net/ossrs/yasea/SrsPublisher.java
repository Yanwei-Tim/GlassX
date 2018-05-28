package net.ossrs.yasea;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Leo Ma on 2016/7/25.
 */
public class SrsPublisher {
    private static final String TAG = "SrsPublisher";

    private AudioRecord mic;
    private boolean aloop = false;
    // The thread to record audio.
    private Thread aworker;

    private SrsCameraView mCameraView;

    private int videoFrameCount;
    private long lastTimeMillis;
    private double mSamplingFps;

    private SrsMp4Muxer mMp4Muxer;
    // The encoder.
    private SrsEncoder mEncoder = new SrsEncoder();

    // Indicate recode mode.
    private int mRecordMode;
    private Camera currentCamera;
    private byte[] currentData;

    public SrsPublisher(SrsCameraView view) {
        mCameraView = view;
        mCameraView.setPreviewCallback(new SrsCameraView.PreviewCallback() {
            @Override
            public void onGetYuvFrame(byte[] data, Camera camera) {

                currentData = data;
                currentCamera = camera;

                // Calculate YUV sampling FPS
                if (videoFrameCount == 0) {
                    lastTimeMillis = System.nanoTime() / 1000000;
                    videoFrameCount++;
                } else {
                    if (++videoFrameCount >= 48) {
                        long diffTimeMillis = System.nanoTime() / 1000000 - lastTimeMillis;
                        mSamplingFps = (double) videoFrameCount * 1000 / diffTimeMillis;
                        videoFrameCount = 0;
                    }
                }

                if (mRecordMode == RecordMode.HwOnlyAudio || mRecordMode == RecordMode.SwOnlyAudio) {
                    // Do not need to encode video.
                    return;
                }

                mEncoder.onGetYuvFrame(data);
            }
        });
    }

    public void startPublish(int recordMode) {
        mRecordMode = recordMode;

        switch (recordMode) {
            case RecordMode.HwAudioAndVideo:
            case RecordMode.HwOnlyAudio:
            case RecordMode.HwOnlyVideo:
                swithToHardEncoder();
                break;
            case RecordMode.SwAudioAndVideo:
            case RecordMode.SwOnlyAudio:
            case RecordMode.SwOnlyVideo:
                swithToSoftEncoder();
                break;
        }

        startEncode();
    }

    public void stopPublish() {
        stopEncode();
    }

    public void startRecord(String recPath) {
        if (mMp4Muxer != null) {
            mMp4Muxer.record(new File(recPath));
        }
    }

    /**
     * is or not add pps frame,before the key frames
     *
     * @param isAdd is not not
     */
    public void isAddPpsFrame(boolean isAdd) {
        mEncoder.setPpsFrame(isAdd);
    }

    public void stopRecord() {
        if (mMp4Muxer != null) {
            mMp4Muxer.stop();
        }
    }

    public void pauseRecord() {
        if (mMp4Muxer != null) {
            mMp4Muxer.pause();
        }
    }

    public void resumeRecord() {
        if (mMp4Muxer != null) {
            mMp4Muxer.resume();
        }
    }

    public void swithToSoftEncoder() {
        mEncoder.swithToSoftEncoder();
    }

    public void swithToHardEncoder() {
        mEncoder.swithToHardEncoder();
    }

    public boolean isSoftEncoder() {
        return mEncoder.isSoftEncoder();
    }

    public int getPreviewWidth() {
        return mEncoder.getPreviewWidth();
    }

    public int getPreviewHeight() {
        return mEncoder.getPreviewHeight();
    }

    public double getmSamplingFps() {
        return mSamplingFps;
    }

    public int getCamraId() {
        return mCameraView.getCameraId();
    }

    public void setPreviewResolution(int width, int height) {
        int[] resolution = mCameraView.setPreviewResolution(width, height);
        mEncoder.setPreviewResolution(resolution[0], resolution[1]);
    }

    public void setOutputResolution(int width, int height) {
        if (width <= height) {
            mEncoder.setPortraitResolution(width, height);
        } else {
            mEncoder.setLandscapeResolution(width, height);
        }
    }

    public void setScreenOrientation(int orientation) {
        mEncoder.setScreenOrientation(orientation);
    }

    public void setPreviewRotation(int rotation) {
        mCameraView.setPreviewRotation(rotation);
    }

    public void setVideoHHDMode() {
        mEncoder.setVideoHHDMode();
    }

    public void setVideoHDMode() {
        mEncoder.setVideoHDMode();
    }

    public void setVideoSmoothMode() {
        mEncoder.setVideoSmoothMode();
    }

    public void setVideoBps(int bps) {
        mEncoder.setVideoBps(bps);
    }

    public void switchCameraFace(int id) {
        mCameraView.setCameraId(id);
        mCameraView.stopCamera();
        if (id == 0) {
            mEncoder.setCameraBackFace();
        } else {
            mEncoder.setCameraFrontFace();
        }
        mCameraView.startCamera();
    }

    private void startEncode() {
        // Start camera to encode video stream.
        mCameraView.startCamera();

        if (!mEncoder.start()) {
            return;
        }

        mic = mEncoder.chooseAudioRecord();
        if (mic == null) {
            return;
        }

        if (mRecordMode == RecordMode.HwOnlyVideo || mRecordMode == RecordMode.SwOnlyVideo) {
            // Do not need to encode audio.
            return;
        }

        aworker = new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                startAudio();
            }
        });
        aloop = true;
        aworker.start();
    }

    private void stopEncode() {
        stopAudio();
        mCameraView.stopCamera();
        mEncoder.stop();
    }

    private void startAudio() {
        if (mic != null) {
            mic.startRecording();

            byte pcmBuffer[] = new byte[4096];
            while (aloop && !Thread.interrupted()) {
                int size = mic.read(pcmBuffer, 0, pcmBuffer.length);
                if (size <= 0) {
                    break;
                }
                mEncoder.onGetPcmFrame(pcmBuffer, size);
            }
        }
    }

    private void stopAudio() {
        aloop = false;
        if (aworker != null) {
            aworker.interrupt();
            try {
                aworker.join();
            } catch (InterruptedException e) {
                aworker.interrupt();
            }
            aworker = null;
        }

        if (mic != null) {
            mic.setRecordPositionUpdateListener(null);
            mic.stop();
            mic.release();
            mic = null;
        }
    }

    public void switchMute() {
        AudioManager audioManager = (AudioManager) mCameraView.getContext().getSystemService(Context.AUDIO_SERVICE);
        int oldMode = audioManager.getMode();
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        boolean isMute = !audioManager.isMicrophoneMute();
        audioManager.setMicrophoneMute(isMute);
        audioManager.setMode(oldMode);
    }


    public void setRecordHandler(SrsRecordHandler handler) {
        mMp4Muxer = new SrsMp4Muxer(handler);
        mEncoder.setMp4Muxer(mMp4Muxer);
    }

    public void setEncodeHandler(SrsEncodeHandler handler) {
        mEncoder.setEncodeHandler(handler);
    }

    /**
     * get a picture
     *
     * @return bitmap
     */
    public Bitmap getPhotoBitmap() {

        Camera.Size size = currentCamera.getParameters().getPreviewSize();

        Log.i(TAG, "getPhotoByte: photo size width=" + size.width);
        Log.i(TAG, "getPhotoByte: photo size height=" + size.height);

        try {
            YuvImage image = new YuvImage(currentData, ImageFormat.NV21, size.width, size.height, null);
            if (image != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);

                Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                stream.close();
                return bmp;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return null;
    }

    /**
     * get a picture
     *
     * @return byte[]
     */
    public byte[] getPhotoByte() {
        return currentData;
    }
}
