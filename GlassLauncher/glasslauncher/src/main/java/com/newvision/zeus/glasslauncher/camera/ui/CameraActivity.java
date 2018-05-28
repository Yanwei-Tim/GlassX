package com.newvision.zeus.glasslauncher.camera.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newvision.zeus.glasscore.helper.PhoneRequestHelper;
import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.base.GlassCoreServerActivity;
import com.newvision.zeus.glasscore.base.IBindServiceStatusListener;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glasslauncher.R;
import com.newvision.zeus.glasslauncher.camera.common.TimeShowAdapter;
import com.newvision.zeus.glasslauncher.utils.CeyesTimer;
import com.newvision.zeus.glasslauncher.utils.GameraConstants;
import com.newvision.zeus.glasslauncher.utils.SaveLocalUtils;
import com.newvision.zeus.glasslauncher.common.config.AppConfig;
import com.newvision.zeus.glasslauncher.utils.MyToast;

import net.ossrs.yasea.RecordMode;
import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class CameraActivity extends GlassCoreServerActivity implements View.OnClickListener, SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener {

    private static final String TAG = "CameraActivity";
    private static final int CAMERA_CODE2 = 12;
    private boolean isPermission = false;


    private TextView takePicture;
    private TextView videoTime;
    private TextView takeVideo;
    private SrsPublisher srsPublisher;
    private SrsCameraView showPreview;

    //status
    private boolean isRecording = false;
    private long startRecordTime;
    private ImageView animationShow;
    private ImageView pictureShow;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GameraConstants.MSG_DISMISS_ANIMATION:
                    dismissAnimation();
                    pictureShow.setVisibility(View.GONE);
                    takePicture.setVisibility(View.VISIBLE);
                    takeVideo.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);
        initView();
        if (Build.VERSION.SDK_INT >= 23) {
            permission();
        } else {
            isPermission = true;
        }
    }


    private void initView() {
        showPreview = (SrsCameraView) findViewById(R.id.preview);
        animationShow = (ImageView) findViewById(R.id.tv_animation_show);
        pictureShow = (ImageView) findViewById(R.id.iv_picture_show);
        takePicture = (TextView) findViewById(R.id.tv_camera_picture);
        videoTime = (TextView) findViewById(R.id.tv_camera_time);
        takeVideo = (TextView) findViewById(R.id.tv_camera_video);
        takePicture.setOnClickListener(this);
        takeVideo.setOnClickListener(this);

    }

    private void permission() {
        List<String> permissionLists = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionLists.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLists.add(Manifest.permission.CAMERA);
        }

        if (!permissionLists.isEmpty()) {//说明肯定有拒绝的权限
            ActivityCompat.requestPermissions(this, permissionLists.toArray(new String[permissionLists.size()]), CAMERA_CODE2);
        } else {
            Toast.makeText(this, "权限都授权了，可以搞事情了", Toast.LENGTH_SHORT).show();
            isPermission = true;
        }
    }


    private void initPublisher() {

        srsPublisher = new SrsPublisher(showPreview);
        showPreview.setZOrderMediaOverlay(true);
        srsPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        srsPublisher.setRecordHandler(new SrsRecordHandler(this));
        srsPublisher.setPreviewRotation(0);

        srsPublisher.setPreviewResolution(Integer.parseInt(AppConfig.getInstance().getVideoWidth()), Integer.parseInt(AppConfig.getInstance().getVideoHeight()));
        srsPublisher.setOutputResolution(Integer.parseInt(AppConfig.getInstance().getVideoWidth()), Integer.parseInt(AppConfig.getInstance().getVideoHeight()));

        //set Bitrate
        srsPublisher.setVideoHDMode();     //Bitrate 1200

        srsPublisher.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
//        srsPublisher.startPublish(RecordMode.HwOnlyVideo);
        //set encoder type
        srsPublisher.startPublish(RecordMode.HwOnlyVideo);
        Log.i(TAG, "initPublisher: camera id =" + srsPublisher.getCamraId());
        Log.i(TAG, "initPublisher: camera number =" + Camera.getNumberOfCameras());
        srsPublisher.switchCameraFace((srsPublisher.getCamraId() + 1) % Camera.getNumberOfCameras());

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_camera_picture:
                startTakePicture();
                Log.i(TAG, "onClick: take photo");
                break;
            case R.id.tv_camera_video:
                startRecorder();
                Log.i(TAG, "onClick: take video");
                break;
        }
    }

    /**
     * take a picture
     */
    private void startTakePicture() {

        takePicture.setVisibility(View.GONE);
        takeVideo.setVisibility(View.GONE);
        videoTime.setVisibility(View.GONE);
        showAnimation();
        Bitmap photo = srsPublisher.getPhotoBitmap();
        pictureShow.setVisibility(View.VISIBLE);
        pictureShow.setImageBitmap(photo);
        if (photo != null) {
            SaveLocalUtils.savePicture(photo);
        }
        mHandler.sendEmptyMessageDelayed(GameraConstants.MSG_DISMISS_ANIMATION, 3000);
        sendPhoto();
    }

    private void showAnimation() {
        animationShow.setVisibility(View.VISIBLE);
        animationShow.setImageDrawable(null);
        animationShow.setBackgroundResource(R.drawable.camera_focus);
        AnimationDrawable rocketAnimation = (AnimationDrawable) animationShow.getBackground();
        rocketAnimation.start();
    }

    private void dismissAnimation() {
        animationShow.setVisibility(View.GONE);
    }

    /**
     * start a recorder
     */
    private void startRecorder() {

        if (!isRecording) {
            isRecording = true;
            String localPath = SaveLocalUtils.getRecordPath();
            srsPublisher.startRecord(localPath);
            startRecordTime = System.currentTimeMillis();
            startTimer();
            takePicture.setVisibility(View.GONE);
            takeVideo.setVisibility(View.GONE);
            videoTime.setVisibility(View.VISIBLE);
        }
    }

    /**
     * start a recorder
     */
    private void stopRecorder() {

        if (isRecording) {
            isRecording = false;
            srsPublisher.stopRecord();
            stopTimer();
            takePicture.setVisibility(View.VISIBLE);
            takeVideo.setVisibility(View.VISIBLE);
            videoTime.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindTcpService(new IBindServiceStatusListener() {
            @Override
            public void bindSuccess() {
                Log.i(TAG, "bindSuccess: ");
                mServerServiceBinder.registerListener(callback);
//                GlassAskUtils.sendOpenAppAsk(mServerServiceBinder, GlassConstants.INNER_APP_CAMERA);
                PhoneRequestHelper.getInstance().openInternalApp(mServerServiceBinder, GlassConstants.INNER_APP_CAMERA);
            }

            @Override
            public void unbindSuccess() {
                Log.i(TAG, "unbindSuccess: ");
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
        mServerServiceBinder.unregisterListener(callback);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (srsPublisher != null) {
            srsPublisher.stopRecord();
            srsPublisher.stopPublish();
        }
    }

    private void startTimer() {
        if (mSplashTimer != null) {
            mSplashTimer.cancel();
        }
        mSplashTimer.schedule(500, 1000);
    }

    private CeyesTimer mSplashTimer = new CeyesTimer() {
        @Override
        protected void onUpdate() {
            videoTime.setText(adapter.getFormat().format(adapter.getDate()));
        }
    };

    public void stopTimer() {
        if (mSplashTimer != null) {
            mSplashTimer.cancel();
        }
    }


    /**
     * encoder video listener
     */
    @Override
    public void onGetStreamFrame(byte[] dst) {

        if (mServerServiceBinder.getLoginGlassStatus()) {
            Log.i(TAG, "onGetStreamFrame: send a frame");
//            GlassAskUtils.startPushH264(mServerServiceBinder, dst);
            PhoneRequestHelper.getInstance().sendVideoStream(mServerServiceBinder,dst);
        }

    }


    private IMessageCallback callback = new IMessageCallback() {
        @Override
        public void getResult(int tag, Object result) {
            Log.i(TAG, "getResult: message type = " + tag);
            switch (tag) {
                case GlassMessageType.PHONE_OPEN_APP_ANS:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
//                        GlassAskUtils.sendStartPreviewAsk(mServerServiceBinder);
                        PhoneRequestHelper.getInstance().startCameraPreview(mServerServiceBinder);
                        break;
                    }
                case GlassMessageType.PHONE_START_PREVIEW_ANS:
                    Log.d(TAG, "GlassMessageType.GLASS_START_PREVIEW_ANS");
                    if (isPermission) {
                        initPublisher();
                    }
                    break;
            }
        }
    };

    /**
     * exit camera
     */
    private void exitCamera() {
        MyToast.show("exit camera");
        onBackPressed();
    }

    /**
     * start video
     */
    private void startVideo() {
        MyToast.show("start video");
        startRecorder();

    }

    /**
     * stop video
     */
    private void stopVideo() {
        MyToast.show("stop video");
        stopRecorder();
    }

    private void sendPhoto() {
        byte[] yuv = srsPublisher.getPhotoByte();
        if (yuv != null) {
//            GlassAskUtils.startPushPhoto(mServerServiceBinder, yuv);
            PhoneRequestHelper.getInstance().sendFileData(mServerServiceBinder,yuv);
        }
    }


    @Override
    public void onNetworkWeak() {
        Toast.makeText(getApplicationContext(), "Network  Weak", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNetworkResume() {
        Toast.makeText(getApplicationContext(), "Record paused", Toast.LENGTH_SHORT).show();
    }

    /**
     * Record listener
     */
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
        Toast.makeText(getApplicationContext(), "MP4 file saved: IOException", Toast.LENGTH_SHORT).show();
        handleException(e);
    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {
        Toast.makeText(getApplicationContext(), "MP4 file saved: IOException", Toast.LENGTH_SHORT).show();
        handleException(e);
    }


    private void handleException(Exception e) {
        Toast.makeText(getApplicationContext(), "Record Exception" + e.getMessage(), Toast.LENGTH_SHORT).show();
        srsPublisher.stopPublish();
        srsPublisher.stopRecord();

    }


    private TimeShowAdapter adapter = new TimeShowAdapter() {
        @Override
        public Object getDate() {
            return (System.currentTimeMillis() - startRecordTime);
        }

        @Override
        public SimpleDateFormat getFormat() {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            return format;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_CODE2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意授权
                    isPermission = true;
                } else {
                    //用户拒绝授权
                }
                break;

        }
    }

}
