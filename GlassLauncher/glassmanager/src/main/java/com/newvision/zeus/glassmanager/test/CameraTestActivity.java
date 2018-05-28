package com.newvision.zeus.glassmanager.test;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;


import com.newvision.zeus.glasscore.base.GlassCoreClientActivity;
import com.newvision.zeus.glasscore.base.IBindServiceStatusListener;
import com.newvision.zeus.glasscore.helper.GlassRequestHelper;
import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.common.config.AppConfig;
import com.newvision.zeus.glassmanager.common.decoder.H264StreamDecoder;
import com.newvision.zeus.glassmanager.common.decoder.ReleaseUtils;
import com.newvision.zeus.glassmanager.common.decoder.StreamManager;
import com.newvision.zeus.glassmanager.common.decoder.VideoFrame;
import com.newvision.zeus.glassmanager.utils.MyToast;

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


public class CameraTestActivity extends GlassCoreClientActivity implements SrsEncodeHandler.SrsEncodeListener, SrsRecordHandler.SrsRecordListener, View.OnClickListener {


    private static final String TAG = "CameraTestActivity";

    private RelativeLayout rlContainer;
    private SrsCameraView cameraPreview;
    private SurfaceView showPreview;
    private SrsPublisher srsPublisher;
    private Button startPreview;

    private boolean isRun = false;
    private boolean cameraIsRun = false;

    /**
     * ps: wifi & usb mode:
     * 1> extends GlassCoreServerActivity  see {@link CameraTestActivity} and {@link CameraUsbTestActivity}
     * 2> bindTcpService see {@link CameraTestActivity#onResume()#GlassRequestHelper}
     * 3> register callback listener see {@link CameraTestActivity#onResume()} and {@link CameraTestActivity#callback}
     * 4> binder unregister callback listener {@link CameraTestActivity#onPause()#GlassRequestHelper}
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
        startPreview = (Button) findViewById(R.id.bt_start_preview);
        rlContainer = (RelativeLayout) findViewById(R.id.rl_container);
        cameraPreview.setZOrderOnTop(true);
        cameraPreview.setZOrderMediaOverlay(true);
        rlContainer.setOnClickListener(this);
        startPreview.setOnClickListener(this);
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
        //set encoder type
        srsPublisher.startPublish(RecordMode.SwOnlyVideo);
//        srsPublisher.startPublish(RecordMode.HwOnlyVideo);

    }


    @Override
    protected void onResume() {
        super.onResume();
        bindTcpService(new IBindServiceStatusListener() {
            @Override
            public void bindSuccess() {
                GlassRequestHelper.getInstance().registerListener(mClientServiceBinder, callback);

            }

            @Override
            public void unbindSuccess() {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (srsPublisher != null) {
            srsPublisher.pauseRecord();
        }
        GlassRequestHelper.getInstance().unregisterListener(mClientServiceBinder, callback);
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
        if (isRun) {
            ReleaseUtils.releaseQuietly(H264StreamDecoder.getInstance());
        }
    }

    private IMessageCallback callback = new IMessageCallback() {
        @Override
        public void getResult(int tag, Object result) {
            Log.i(TAG, "getResult: message type=" + Integer.toHexString(tag));
            switch (tag) {
                case GlassMessageType.PHONE_START_PREVIEW_ASK:
                    GlassRequestHelper.getInstance().startCameraPreviewAns(mClientServiceBinder, GlassErrorCode.OK);
                    if (!isRun) {
                        isRun = true;
                        StreamManager.getInstance().onStart(false);
                        H264StreamDecoder.getInstance().initDecoder(showPreview.getHolder().getSurface());
                        H264StreamDecoder.getInstance().startDecodeFrame();
                    }
                    break;
                //Receive response commands sent by client
                case GlassMessageType.GLASS_START_PREVIEW_ANS:
                    startCamera();
                    break;

                case GlassMessageType.PHONE_CLOSE_APP_ASK:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        GlassRequestHelper.getInstance().closeInternalAppAns(mClientServiceBinder, GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
                        MyToast.show("exit app");
                        finish();
                    }
                    break;

                case GlassMessageType.GLASS_CLOSE_APP_ANS:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        MyToast.show("exit app");
                        finish();
                    }
                    break;
                case GlassMessageType.LOAD_H264:
//                    Log.i(TAG, "getResult: get a frame");
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

        if (!cameraIsRun) {
            cameraIsRun = true;
            initPublisher();
        }

    }

    /**
     * exit camera
     */
    private void exitCamera() {
        MyToast.show("exit camera");
        cameraIsRun = false;
        GlassRequestHelper.getInstance().stopCameraPreview(mClientServiceBinder);
    }

    @Override
    public void onBackPressed() {
        if (mClientServiceBinder.getLoginGlassStatus()) {
            exitCamera();
            GlassRequestHelper.getInstance().closeInternalAppAns(mClientServiceBinder, GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
        }
        super.onBackPressed();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start_preview:
                Log.i(TAG, "onClick: bt_start_preview");
                GlassRequestHelper.getInstance().startCameraPreview(mClientServiceBinder);
                break;
        }
    }


    /**
     * Encoder listener
     */

    @Override
    public void onGetStreamFrame(byte[] dst) {
        if (mClientServiceBinder.getLoginGlassStatus()) {
            Log.i(TAG, "onGetStreamFrame: send a frame");
            GlassRequestHelper.getInstance().sendVideoStream(mClientServiceBinder, dst);
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

}
