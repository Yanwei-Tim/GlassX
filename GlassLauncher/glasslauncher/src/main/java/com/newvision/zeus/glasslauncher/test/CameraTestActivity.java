package com.newvision.zeus.glasslauncher.test;


import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.newvision.zeus.glasscore.base.GlassCoreServerActivity;
import com.newvision.zeus.glasscore.base.IBindServiceStatusListener;
import com.newvision.zeus.glasscore.helper.PhoneRequestHelper;
import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glasslauncher.R;
import com.newvision.zeus.glasslauncher.common.config.AppConfig;
import com.newvision.zeus.glasslauncher.common.decoder.H264StreamDecoder;
import com.newvision.zeus.glasslauncher.common.decoder.StreamManager;
import com.newvision.zeus.glasslauncher.common.decoder.VideoFrame;
import com.newvision.zeus.glasslauncher.utils.MyToast;
import com.newvision.zeus.glasslauncher.utils.ReleaseUtils;

import net.ossrs.yasea.RecordMode;
import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;


/**
 * the test of two-way transfer data
 * Created by Qing Jiwei on 7/25/17.
 */

public class CameraTestActivity extends GlassCoreServerActivity implements SrsEncodeHandler.SrsEncodeListener, SrsRecordHandler.SrsRecordListener, View.OnClickListener {

    private static final String TAG = "CameraTestActivity";

    private RelativeLayout rlContainer;
    private SrsCameraView cameraPreview;
    private SurfaceView showPreview;
    private SrsPublisher srsPublisher;

    private boolean isRun = false;
    private boolean cameraIsStatus = false;

    /**
     * ps: wifi & usb mode:
     * 1> extends GlassCoreServerActivity  see {@link CameraTestActivity} and {@link CameraUsbTestActivity}
     * 2> bindTcpService see {@link CameraTestActivity#onResume()#PhoneRequestHelper}
     * 3> register callback listener see {@link CameraTestActivity#onResume()} and {@link CameraTestActivity#callback}
     * 4> binder unregister callback listener {@link CameraTestActivity#onPause()#PhoneRequestHelper}
     * 5> unbindTcpService see {@link CameraTestActivity#onPause()}
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_camera_layout);
        initView();
    }

    private void initView() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        showPreview = (SurfaceView) findViewById(R.id.sv_camera_show);
        cameraPreview = (SrsCameraView) findViewById(R.id.camera_preview);
        rlContainer = (RelativeLayout) findViewById(R.id.rl_container);

        cameraPreview.setZOrderOnTop(true);
        cameraPreview.setZOrderMediaOverlay(true);
        rlContainer.setOnClickListener(this);
    }

    private void initPublisher() {

        srsPublisher = new SrsPublisher(cameraPreview);
        srsPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        srsPublisher.setRecordHandler(new SrsRecordHandler(this));
        srsPublisher.setPreviewRotation(0);
        srsPublisher.setPreviewResolution(Integer.parseInt(AppConfig.getInstance().getVideoWidth()), Integer.parseInt(AppConfig.getInstance().getVideoHeight()));
        srsPublisher.setOutputResolution(Integer.parseInt(AppConfig.getInstance().getVideoWidth()), Integer.parseInt(AppConfig.getInstance().getVideoHeight()));
        //set bps
        srsPublisher.setVideoBps(2000);
        srsPublisher.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
        srsPublisher.startPublish(RecordMode.HwOnlyVideo);
//        srsPublisher.startPublish(RecordMode.SwOnlyVideo);

    }


    @Override
    protected void onResume() {
        super.onResume();

        bindTcpService(new IBindServiceStatusListener() {
            @Override
            public void bindSuccess() {
                PhoneRequestHelper.getInstance().registerListener(mServerServiceBinder, callback);
                PhoneRequestHelper.getInstance().openInternalApp(mServerServiceBinder, GlassConstants.INNER_APP_CAMERA);
            }

            @Override
            public void unbindSuccess() {
                mServerServiceBinder.unregisterListener(callback);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (srsPublisher != null) {
            srsPublisher.pauseRecord();
        }
        if (mServerServiceBinder != null) {
            PhoneRequestHelper.getInstance().unregisterListener(mServerServiceBinder, callback);
        }

        unbindTcpService();
        ReleaseUtils.releaseQuietly(StreamManager.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (srsPublisher != null) {
            srsPublisher.stopRecord();
            srsPublisher.stopPublish();
        }

        ReleaseUtils.releaseQuietly(H264StreamDecoder.getInstance());
    }


    private IMessageCallback callback = new IMessageCallback() {
        @Override
        public void getResult(int tag, Object result) {
            Log.i(TAG, "getResult: message type=" + Integer.toHexString(tag));
            switch (tag) {

                //Receive response commands sent by servers
                case GlassMessageType.PHONE_OPEN_APP_ANS:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        mHandler.sendEmptyMessageDelayed(GlassMessageType.PHONE_START_PREVIEW_ASK, 100);
                    }
                    break;
                case GlassMessageType.PHONE_CLOSE_APP_ANS:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        finish();
                    }
                    break;
                case GlassMessageType.GLASS_OPEN_APP_ASK:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        PhoneRequestHelper.getInstance().openInternalAppAns(mServerServiceBinder, GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
                    }
                    break;
                case GlassMessageType.GLASS_CLOSE_APP_ASK:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
//                        GlassAskUtils.sendCloseAppAns(mServerServiceBinder, GlassConstants.INNER_APP_CAMERA);
                        PhoneRequestHelper.getInstance().closeInternalAppAns(mServerServiceBinder, GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
                        MyToast.show("PHONE_STOP_CAMERA_ASK");
                        finish();
                    }
                    break;
                case GlassMessageType.GLASS_START_PREVIEW_ASK:
                    Log.i(TAG, "getResult: GLASS_START_PREVIEW_ASK");
                    PhoneRequestHelper.getInstance().startCameraPreviewAns(mServerServiceBinder, GlassErrorCode.OK);
                    if (!isRun) {
                        isRun = true;
                        StreamManager.getInstance().onStart(false);
                        H264StreamDecoder.getInstance().initDecoder(showPreview.getHolder().getSurface());
                        H264StreamDecoder.getInstance().startDecodeFrame();
                    }
                    break;
                case GlassMessageType.PHONE_START_PREVIEW_ANS:
                    startCamera();
                    break;
                case GlassMessageType.LOAD_H264:
                    if (!isRun) {
                        return;
                    }
                    byte[] messageBody = ((GlassMessage) result).messageBody;
                    if (messageBody != null) {
                        StreamManager.getInstance().addStream(VideoFrame.obtain(messageBody));
                    }

                    break;

            }
        }
    };

    private void startCamera() {
        if (!cameraIsStatus) {
            initPublisher();
            cameraIsStatus = true;
        }
    }

    /**
     * exit camera
     */
    private void exitCamera() {
        MyToast.show("exit camera");
        PhoneRequestHelper.getInstance().stopCameraPreview(mServerServiceBinder);
        cameraIsStatus = false;
    }

    @Override
    public void onBackPressed() {
        if (mServerServiceBinder.getLoginGlassStatus()) {
            exitCamera();
            PhoneRequestHelper.getInstance().closeInternalApp(mServerServiceBinder, GlassConstants.INNER_APP_CAMERA);
        }
        super.onBackPressed();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                break;
        }
    }


    /**
     * Encoder listener
     */

    @Override
    public void onGetStreamFrame(byte[] dst) {
        if (mServerServiceBinder.getLoginGlassStatus()) {
            Log.i(TAG, "onGetStreamFrame: send a frame");
            PhoneRequestHelper.getInstance().sendVideoStream(mServerServiceBinder, dst);
        }
    }

    @Override
    public void onNetworkWeak() {
        MyToast.show("network is weak!!");
    }

    @Override
    public void onNetworkResume() {

    }

    /**
     * Record listener
     */

    @Override
    public void onRecordPause() {

    }

    @Override
    public void onRecordResume() {

    }

    @Override
    public void onRecordStarted(String msg) {

    }

    @Override
    public void onRecordFinished(String msg) {

    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRecordIOException(IOException e) {

    }

    /**
     * Delay to send message
     */
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GlassMessageType.PHONE_START_PREVIEW_ASK:
                    PhoneRequestHelper.getInstance().startCameraPreview(mServerServiceBinder);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    };

}
