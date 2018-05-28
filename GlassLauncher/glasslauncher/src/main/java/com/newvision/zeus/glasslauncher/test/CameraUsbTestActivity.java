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
import com.newvision.zeus.glasscore.base.IBindUsbServiceStatusListener;
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
import com.newvision.zeus.glasslauncher.utils.DisplayUtil;
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


public class CameraUsbTestActivity extends GlassCoreServerActivity implements SrsEncodeHandler.SrsEncodeListener, SrsRecordHandler.SrsRecordListener, View.OnClickListener {

    private static final String TAG = CameraUsbTestActivity.class.getSimpleName();

    private RelativeLayout rlContainer;
    private SrsCameraView cameraPreview;
    private SurfaceView showPreview;
    private SrsPublisher srsPublisher;

    private RelativeLayout.LayoutParams smallParams;
    private RelativeLayout.LayoutParams bigParams;

    private boolean isRun = false;
    private boolean cameraIsStatus = false;


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
        srsPublisher.setVideoBps(3000);
        srsPublisher.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
//        srsPublisher.startPublish(RecordMode.SwOnlyVideo);
        srsPublisher.startPublish(RecordMode.HwOnlyVideo);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: onResume");
        bindUsbService(new IBindUsbServiceStatusListener() {
            @Override
            public void bindSuccess() {
                PhoneRequestHelper.getInstance().registerListener(mUsbServiceBinder, callback);
                PhoneRequestHelper.getInstance().openInternalApp(mUsbServiceBinder, GlassConstants.INNER_APP_CAMERA);
            }

            @Override
            public void unbindSuccess() {
                Log.i(TAG, "unbindSuccess: ");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (srsPublisher != null) {
            srsPublisher.pauseRecord();
        }

        if (mUsbServiceBinder != null) {
            PhoneRequestHelper.getInstance().unregisterListener(mUsbServiceBinder, callback);
        }
        unbindUsbService();
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
            switch (tag) {
                //Receive response commands sent by servers
                case GlassMessageType.PHONE_OPEN_APP_ANS:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        Log.i(TAG, "getResult: GLASS_OPEN_APP_ANS");
                        mHandler.sendEmptyMessageDelayed(GlassMessageType.PHONE_START_PREVIEW_ASK, 100);
                    }
                    break;
                case GlassMessageType.PHONE_START_PREVIEW_ANS:
                    Log.d(TAG, "GlassMessageType.GLASS_START_PREVIEW_ANS");
                    startCamera();
                    break;
                case GlassMessageType.PHONE_CLOSE_APP_ANS:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        MyToast.show("GLASS_STOP_CAMERA_ANS");
                        finish();
                    }
                    break;

                //Receive request commands sent by the servers
                case GlassMessageType.GLASS_OPEN_APP_ASK:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        Log.i(TAG, "getResult: PHONE_OPEN_APP_ASK");
                        PhoneRequestHelper.getInstance().openInternalAppAns(mUsbServiceBinder, GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
                    }
                    break;

                case GlassMessageType.GLASS_CLOSE_APP_ASK:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        PhoneRequestHelper.getInstance().closeInternalAppAns(mUsbServiceBinder, GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
                        MyToast.show("PHONE_STOP_CAMERA_ASK");
                        finish();
                    }
                    break;
                case GlassMessageType.GLASS_START_PREVIEW_ASK:
                    Log.d(TAG, "GlassMessageType.PHONE_START_PREVIEW_ASK");
                    PhoneRequestHelper.getInstance().startCameraPreviewAns(mUsbServiceBinder, GlassErrorCode.OK);
                    if (!isRun) {
                        isRun = true;
                        StreamManager.getInstance().onStart(false);
                        H264StreamDecoder.getInstance().initDecoder(showPreview.getHolder().getSurface());
                        H264StreamDecoder.getInstance().startDecodeFrame();
                    }
                    break;
                case GlassMessageType.LOAD_H264:
                    Log.i(TAG, "getResult: get a frame");
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
            cameraIsStatus = true;
            initPublisher();
        }

    }

    /**
     * exit camera
     */
    private void exitCamera() {
        PhoneRequestHelper.getInstance().stopCameraPreview(mUsbServiceBinder);
        cameraIsStatus = false;
    }

    @Override
    public void onBackPressed() {
        if (mUsbServiceBinder.getLoginGlassStatus()) {
            exitCamera();
            PhoneRequestHelper.getInstance().closeInternalApp(mUsbServiceBinder, GlassConstants.INNER_APP_CAMERA);
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
        if (mUsbServiceBinder.getLoginGlassStatus()) {
            PhoneRequestHelper.getInstance().sendVideoStream(mUsbServiceBinder, dst);
        }
    }

    @Override
    public void onNetworkWeak() {
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
                    PhoneRequestHelper.getInstance().startCameraPreview(mUsbServiceBinder);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    };

}
