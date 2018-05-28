package com.newvision.zeus.camera.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newvision.zeus.camera.R;
import com.newvision.zeus.camera.common.TimeShowAdapter;
import com.newvision.zeus.camera.config.AppConfig;
import com.newvision.zeus.camera.config.GlassConstants;
import com.newvision.zeus.camera.utils.CeyesTimer;
import com.newvision.zeus.camera.utils.SaveLocalUtils;

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

public class MainActivity extends BaseActivity implements View.OnClickListener, SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener {

    private static final String TAG = "MainActivity";
    private static final int CAMERA_CODE2 = 12;

    private TextView takepicture;
    private TextView videotime;
    private TextView takevideo;
    private SrsPublisher srsPublisher;
    private SrsCameraView showPreview;

    //status
    private boolean isRecording = false;
    private long startRecordTime;
    private ImageView animationshow;
    private ImageView pictureshow;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GlassConstants.MSG_DISMISS_ANIMATION:
                    dismissAnimation();
                    pictureshow.setVisibility(View.GONE);
                    takepicture.setVisibility(View.VISIBLE);
                    takevideo.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

        showPreview = (SrsCameraView) findViewById(R.id.preview);
        animationshow = (ImageView) findViewById(R.id.tv_animation_show);
        pictureshow = (ImageView) findViewById(R.id.iv_picture_show);
        takepicture = (TextView) findViewById(R.id.tv_camera_picture);
        videotime = (TextView) findViewById(R.id.tv_camera_time);
        takevideo = (TextView) findViewById(R.id.tv_camera_video);
        takepicture.setOnClickListener(this);
        takevideo.setOnClickListener(this);

    }

    @Override
    public void doBusiness(Context mContext) {

        if (Build.VERSION.SDK_INT >= 23) {
            permission();
        } else {
            initPublisher();
        }

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
            initPublisher();
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
        srsPublisher.startPublish(RecordMode.SwOnlyVideo);
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
                startVideo();
                Log.i(TAG, "onClick: take video");
                break;
        }
    }

    /**
     * take a picture
     */
    private void startTakePicture() {

        takepicture.setVisibility(View.GONE);
        takevideo.setVisibility(View.GONE);
        videotime.setVisibility(View.GONE);
        showAnimation();
        Bitmap photo = srsPublisher.getPhotoBitmap();
        pictureshow.setVisibility(View.VISIBLE);
        pictureshow.setImageBitmap(photo);
        if (photo != null) {
            SaveLocalUtils.savePicture(photo);
        }
        mHandler.sendEmptyMessageDelayed(GlassConstants.MSG_DISMISS_ANIMATION, 3000);

    }

    private void showAnimation() {
        animationshow.setVisibility(View.VISIBLE);
        animationshow.setImageDrawable(null);
        animationshow.setBackgroundResource(R.drawable.camera_focus);
        AnimationDrawable rocketAnimation = (AnimationDrawable) animationshow.getBackground();
        rocketAnimation.start();

    }

    private void dismissAnimation() {
        animationshow.setVisibility(View.GONE);
    }

    /**
     * take a video
     */
    private void startVideo() {

        if (!isRecording) {
            isRecording = true;
            String loaclPath = SaveLocalUtils.getRecordPath();
            srsPublisher.startRecord(loaclPath);
            startRecordTime = System.currentTimeMillis();
            startTimer();
            takepicture.setVisibility(View.GONE);
            takevideo.setVisibility(View.GONE);
            videotime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (srsPublisher != null) {
            srsPublisher.pauseRecord();
        }
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
            videotime.setText(adapter.getFormat().format(adapter.getDate()));
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

        // TODO: 6/29/17  H264文件流发送

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
                    initPublisher();
                } else {
                    //用户拒绝授权
                }
                break;

        }
    }

}
