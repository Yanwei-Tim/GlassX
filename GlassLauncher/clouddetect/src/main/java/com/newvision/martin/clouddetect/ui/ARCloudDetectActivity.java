package com.newvision.martin.clouddetect.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.newvision.martin.clouddetect.R;
import com.newvision.martin.clouddetect.common.helper.DeviceHelper;
import com.newvision.martin.clouddetect.common.util.DetectUtil;
import com.newvision.martin.clouddetect.common.util.LogUtil;

import net.ossrs.yasea.RecordMode;
import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import org.cnnt.player.Player;
import org.cnnt.player.Surface;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import cn.com.xpai.core.Manager;

/**
 * 流程：
 * 1.推送视频流到服务器端。
 * 2.通过WebSocket发送推流通知给服务器，服务器端会根据这个消息拉取推送到服务器上的视频流并开始识别，生成新的视频流。
 * 3.拉取服务端生成的识别结果视频流进行展示。
 */
public class ARCloudDetectActivity extends Activity implements RtmpHandler.RtmpListener,
        SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener {

    private static final String TAG = "MainActivity";

    private static final String ADDR_RTMP = "rtmp://www.ahcloud.com/live1/";

    private SrsPublisher mPublisher;
    private Manager.Resolution previewResolution;
    private Manager.Resolution outputResolution;

    private FrameLayout mContainer;
    private Player mPlayer;
    private Surface mPlayerSurface;

    private WebSocketManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_detect_cloud);

        SrsCameraView preview = (SrsCameraView) findViewById(R.id.preview);
        preview.setZOrderMediaOverlay(true);
        List<Manager.Resolution> resolutionList = DeviceHelper.getMathResolution(this, 480, 1024);
        selectMatchResolution(resolutionList, true);

        mPublisher = new SrsPublisher(preview);
        mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        mPublisher.setRtmpHandler(new RtmpHandler(this));
        mPublisher.setRecordHandler(new SrsRecordHandler(this));
        mPublisher.setPreviewRotation(0);

        String deviceType = Build.MODEL;
        LogUtil.i("DeviceInfo", deviceType);
        if (deviceType.startsWith("C100")) {
            mPublisher.setPreviewResolution(960, 720);
            mPublisher.setOutputResolution(800, 480);
            mPublisher.setVideoHDMode();     //Bitrate 1M
        } else if (deviceType.startsWith("XT1650-05")) {
            mPublisher.setPreviewResolution(1280, 720);
            mPublisher.setOutputResolution(1280, 720);
            mPublisher.setVideoHHDMode();   //Bitrate 2.5M
        } else {
            mPublisher.setPreviewResolution(previewResolution.width, previewResolution.height);
            mPublisher.setOutputResolution(outputResolution.width, outputResolution.height);
            mPublisher.setVideoHDMode();     //Bitrate 1.2M
        }

        mPublisher.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);

        mPublisher.startPublish(ADDR_RTMP + DetectUtil.getId(), RecordMode.HwOnlyVideo);

        // To watch the rtmp stream.
        openPlayer();

        manager = new WebSocketManager();
        manager.connect();
    }

    private void selectMatchResolution(List<Manager.Resolution> resolutionList, boolean isBest) {
        int size = resolutionList.size();
        switch (size) {
            case 1:
                previewResolution = resolutionList.get(0);
                outputResolution = resolutionList.get(0);
                break;
            default:
                if (isBest) {
                    outputResolution = resolutionList.get(0);
                    previewResolution = resolutionList.get(0);
                } else {
                    outputResolution = resolutionList.get(1);
                    previewResolution = resolutionList.get(1);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPublisher.resumeRecord();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPublisher.pauseRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPublisher.stopPublish();
        mPublisher.stopRecord();
        destroyPlayer();

        manager.disconnect();
    }

    private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            mPublisher.stopPublish();
            mPublisher.stopRecord();
        } catch (Exception e1) {
            // Ignore
        }
    }

    @Override
    public void onRtmpConnecting(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpConnected(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoStreaming() {
    }

    @Override
    public void onRtmpAudioStreaming() {
    }

    @Override
    public void onRtmpStopped() {
//        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpDisconnected() {
//        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {
        LogUtil.i(TAG, String.format("Output Fps: %f", fps));
    }

    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            LogUtil.i(TAG, String.format("Video bitrate: %f kbps", bitrate / 1000));
        } else {
            LogUtil.i(TAG, String.format("Video bitrate: %d bps", rate));
        }
    }

    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            LogUtil.i(TAG, String.format("Audio bitrate: %f kbps", bitrate / 1000));
        } else {
            LogUtil.i(TAG, String.format("Audio bitrate: %d bps", rate));
        }
    }

    @Override
    public void onStreamUploadingSpeedChanged(double speed) {
        LogUtil.i(TAG, "onStreamUploadingSpeedChanged: " + speed + "K");
    }

    @Override
    public void onRtmpSocketException(SocketException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {
        handleException(e);
    }

    // Implementation of SrsRecordHandler.

    @Override
    public void onRecordPause() {
        Toast.makeText(getApplicationContext(), "Record paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordResume() {
        Toast.makeText(getApplicationContext(), "Record resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordStarted(String msg) {
        Toast.makeText(getApplicationContext(), "Recording file: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordFinished(String msg) {
        Toast.makeText(getApplicationContext(), "MP4 file saved: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    // Implementation of SrsEncodeHandler.

    @Override
    public void onNetworkWeak() {
        Toast.makeText(getApplicationContext(), "Network weak", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNetworkResume() {
        Toast.makeText(getApplicationContext(), "Network resume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private void openPlayer() {
        mPlayer = new Player(getApplication(), mHandler, ADDR_RTMP + DetectUtil.getId() + "_dct", new String[]{"-live"});
        mPlayer.setFullscreenMode(Player.FullscreenMode.FULLSCREEN);


        mPlayerSurface = new Surface(getApplication(), mPlayer);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPlayerSurface.setLayoutParams(params);

        mContainer = (FrameLayout) findViewById(R.id.container);
        mContainer.addView(mPlayerSurface);
    }

    private void destroyPlayer() {
        try {
            LogUtil.i(TAG, "destroyPlayer: ");
            if (mPlayer != null) mPlayer.onDestroy();
        } catch (Exception e) {
            LogUtil.i(TAG, "destroyPlayer: 11");
        }
        if (mContainer != null) mContainer.removeView(mPlayerSurface);
    }
}
