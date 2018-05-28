package com.newvision.zeus.glassmanager.ui.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newvision.zeus.glasscore.base.GlassCoreClientActivity;
import com.newvision.zeus.glasscore.base.IBindServiceStatusListener;
import com.newvision.zeus.glasscore.helper.GlassRequestHelper;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.common.decoder.H264StreamDecoder;
import com.newvision.zeus.glassmanager.common.decoder.ReleaseUtils;
import com.newvision.zeus.glassmanager.common.decoder.StreamManager;
import com.newvision.zeus.glassmanager.common.decoder.VideoFrame;
import com.newvision.zeus.glassmanager.entity.TimeShowAdapter;
import com.newvision.zeus.glassmanager.utils.BitmapUtils;
import com.newvision.zeus.glassmanager.utils.CeyesTimer;
import com.newvision.zeus.glassmanager.utils.DisplayUtil;
import com.newvision.zeus.glassmanager.utils.MyToast;
import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


/**
 * Created by Qing Jiwei on 7/6/17.
 */

public class CameraActivity extends GlassCoreClientActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "CameraActivity";
    private ImageView iv_take_picture, iv_take_video, iv_end_video;
    private CheckBox cb_start_stop, cb_camera_type;
    private SurfaceView previewShow;
    private RelativeLayout mPlayerLayout;
    private ImageView iconFocus, take_picture;
    private Context context;
    private TextView tv_camera_time, tv_camera_type, tv_stop;
    private LinearLayout ll_notice;
    private int enterCode = 0;
    private long mdelayTime = 0;
    private boolean isRun;
    private long mStartTime = 0;
    private final int MSG_DISMISSANIMATION = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DISMISSANIMATION:
                    dismissAnimation();
                    take_picture.setVisibility(View.VISIBLE);
//                    take_picture.setImageBitmap(screenshot);
                    break;
            }
        }
    };
    private boolean isVideoing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRun = true;
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

    private IMessageCallback callback = new IMessageCallback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void getResult(int tag, Object result) {
            Log.i(TAG, "getResult: message type=" + Integer.toHexString(tag));
            switch (tag) {
                case GlassMessageType.GLASS_START_PREVIEW_ASK:
                    Log.i(TAG, "getResult: START_PREVIEW_ASK");
//                    GlassAskUtils.sendStartPreviewAns(mClientServiceBinder);
                    GlassRequestHelper.getInstance().startCameraPreviewAns(mClientServiceBinder,GlassErrorCode.OK);
                    if (!isRun) {
                        isRun = true;
                        StreamManager.getInstance().onStart(false);
                        H264StreamDecoder.getInstance().initDecoder(previewShow.getHolder().getSurface());
                        H264StreamDecoder.getInstance().startDecodeFrame();
                    }
                    break;

                case GlassMessageType.GLASS_CLOSE_APP_ASK:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        Log.i(TAG, "getResult: STOP_PREVIEW_ANS");
//                        GlassAskUtils.sendCloseAppAns(mClientServiceBinder, GlassConstants.INNER_APP_CAMERA);
                        GlassRequestHelper.getInstance().closeInternalAppAns(mClientServiceBinder,GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
                        MyToast.show("exit app");
                        finish();
                    }

                    break;

                case GlassMessageType.PHONE_CLOSE_APP_ANS:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        Log.i(TAG, "getResult: PHONE_STOP_PREVIEW_ANS");
                        MyToast.show("exit app");
                        finish();
                    }
                    break;

                case GlassMessageType.LOAD_H264:
                    Log.i(TAG, "getResult: get a frame");

                    if (!isRun) {
                        return;
                    }
                    byte[] messageBody = ((GlassMessage) result).messageBody;
                    if (messageBody != null) {
//                        VideoFrame frame = new VideoFrame();
//                        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//                        bufferInfo.flags = parseFrameType(messageBody);
//                        frame.setBufferInfo(bufferInfo);
//                        frame.setStream(messageBody);
                        StreamManager.getInstance().addStream(VideoFrame.obtain(messageBody));
                    }

                    break;
                case GlassMessageType.LOAD_FILE:
                    Log.i(TAG, "getResult: get a photo");
                    byte[] photoData = ((GlassMessage) result).messageBody;
                    if (photoData != null) {
                        saveBitmap(photoData);
                    }
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private void stopView() {

        tv_stop.setVisibility(View.GONE);
        ll_notice.setVisibility(View.VISIBLE);
        cb_camera_type.setVisibility(View.VISIBLE);
        cb_start_stop.setVisibility(View.GONE);
        iv_take_video.setVisibility(View.VISIBLE);
        iv_end_video.setVisibility(View.GONE);
        stopCallTime();
        tv_camera_time.setVisibility(View.GONE);
    }

    private void startTakepicture() {
        take_picture.setVisibility(View.GONE);
        tv_camera_time.setVisibility(View.GONE);
        showAnimation();
        mHandler.sendEmptyMessageDelayed(MSG_DISMISSANIMATION, 300);

    }

    private void startTakevideo() {

        take_picture.setVisibility(View.GONE);
        cb_camera_type.setVisibility(View.GONE);
        tv_stop.setVisibility(View.VISIBLE);
        ll_notice.setVisibility(View.GONE);
        isVideoing = true;
        cb_start_stop.setVisibility(View.VISIBLE);
        iv_take_video.setVisibility(View.GONE);
        iv_end_video.setVisibility(View.VISIBLE);
        mStartTime = System.currentTimeMillis();
        startTime();
    }

    @Override
    public void onBackPressed() {
        ReleaseUtils.releaseQuietly(StreamManager.getInstance());
        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReleaseUtils.releaseQuietly(H264StreamDecoder.getInstance());
        stopCallTime();

    }

    public void initView() {
        context = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ll_notice = (LinearLayout) findViewById(R.id.ll_notice);
        tv_stop = (TextView) findViewById(R.id.tv_stop);
        previewShow = (SurfaceView) findViewById(R.id.sufferview_show);
        mPlayerLayout = (RelativeLayout) findViewById(R.id.rl_decoder);
        iconFocus = (ImageView) findViewById(R.id.focus_icon);
        take_picture = (ImageView) findViewById(R.id.take_picture);

        tv_camera_time = (TextView) findViewById(R.id.tv_camera_time);
        tv_camera_type = (TextView) findViewById(R.id.tv_camera_type);
        iv_take_picture = (ImageView) findViewById(R.id.iv_take_picture);
        iv_take_video = (ImageView) findViewById(R.id.iv_take_video);
        iv_end_video = (ImageView) findViewById(R.id.iv_end_video);
        cb_start_stop = (CheckBox) findViewById(R.id.cb_start_stop);
        cb_camera_type = (CheckBox) findViewById(R.id.cb_camera_type);

        take_picture.setOnClickListener(this);
        iv_take_picture.setOnClickListener(this);
        iv_take_video.setOnClickListener(this);
        iv_end_video.setOnClickListener(this);
        cb_start_stop.setOnCheckedChangeListener(this);
        cb_camera_type.setOnCheckedChangeListener(this);
        findViewById(R.id.bt_disconnect).setOnClickListener(this);
        findViewById(R.id.bt_reconnect).setOnClickListener(this);
        findViewById(R.id.bt_pause).setOnClickListener(this);
        findViewById(R.id.bt_start).setOnClickListener(this);
    }


//    public void doBusiness(Context mContext) {
//
//        GlassCameraMainCardView.getInstance().setGetBitmapListener(new GlassCameraMainCardView.GetCommandListener() {
//            @Override
//            public void getCommand(int command) {
//                switch (command) {
//
//                    case GlassConstants.CODE_TAKEPICTURE:
//                        statusCode = GlassConstants.CODE_TAKEPICTURE;
//                        startTakepicture();
//                        break;
//                    case GlassConstants.CODE_START_VIDEO:
//                        statusCode = GlassConstants.CODE_VIDEO;
//                        GlassCameraMainCardView.getInstance().startTakeView();
//                        startTakevideo();
//                        break;
//                    case GlassConstants.CODE_VIDEO:
//                        cb_camera_type.setChecked(true);
//                        iv_take_picture.setVisibility(View.GONE);
//                        iv_take_video.setVisibility(View.VISIBLE);
//                        break;
//                    case GlassConstants.CODE_CAMERA:
//                        statusCode = GlassConstants.CODE_CAMERA;
//                        cb_camera_type.setChecked(false);
//                        iv_take_picture.setVisibility(View.VISIBLE);
//                        iv_take_video.setVisibility(View.GONE);
//                        iv_end_video.setVisibility(View.GONE);
//                        cb_start_stop.setVisibility(View.GONE);
//                        stopCallTime();
//                        tv_camera_time.setVisibility(View.GONE);
//                        break;
//                    case GlassConstants.CODE_STOP_VIDEO:
//                        statusCode = GlassConstants.CODE_CAMERA;
//                        stopView();
//                        break;
//                    case GlassConstants.CODE_BACK:
//                        enterCode = GlassConstants.CODE_BACK;
//                        onBackPressed();
//                        break;
//                }
//            }
//        });
//
//    }


    private void showAnimation() {
        iconFocus.setVisibility(View.VISIBLE);
        iconFocus.setImageDrawable(null);
        iconFocus.setBackgroundResource(R.drawable.camera_focus);
        AnimationDrawable rocketAnimation = (AnimationDrawable) iconFocus.getBackground();
        rocketAnimation.start();
    }

    private void dismissAnimation() {
        iconFocus.setVisibility(View.GONE);
    }

    private TimeShowAdapter adapter = new TimeShowAdapter() {
        @Override
        public Object getDate() {
            return (System.currentTimeMillis() - mStartTime);
        }

        @Override
        public SimpleDateFormat getFormat() {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            return format;
        }
    };


    private void startTime() {
        if (mSplashTimer != null) {
            mSplashTimer.cancel();
        }
        mSplashTimer.schedule(1000, 500);
    }

    private CeyesTimer mSplashTimer = new CeyesTimer() {
        @Override
        protected void onUpdate() {
            tv_camera_time.setVisibility(View.VISIBLE);
            tv_camera_time.setText(adapter.getFormat().format(adapter.getDate()));
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
    }

    public void stopCallTime() {
        if (mSplashTimer != null) {
            mSplashTimer.cancel();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Configuration cf = this.getResources().getConfiguration();
        int ori = cf.orientation;
        if (ori == cf.ORIENTATION_LANDSCAPE) {

            ViewGroup.LayoutParams layoutParams1 = iconFocus.getLayoutParams();
            layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams1.height = ViewGroup.LayoutParams.MATCH_PARENT;
            iconFocus.setLayoutParams(layoutParams1);

            ViewGroup.LayoutParams layoutParams2 = take_picture.getLayoutParams();
            layoutParams2.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams2.height = ViewGroup.LayoutParams.MATCH_PARENT;
            take_picture.setLayoutParams(layoutParams2);

            ViewGroup.LayoutParams layoutParams = previewShow.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            previewShow.setLayoutParams(layoutParams);
        } else if (ori == cf.ORIENTATION_PORTRAIT) {

            ViewGroup.LayoutParams layoutParams1 = take_picture.getLayoutParams();
            layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams1.height = DisplayUtil.dip2px(CameraActivity.this, 250);
            take_picture.setLayoutParams(layoutParams1);

            ViewGroup.LayoutParams layoutParams2 = iconFocus.getLayoutParams();
            layoutParams2.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams2.height = DisplayUtil.dip2px(CameraActivity.this, 250);
            iconFocus.setLayoutParams(layoutParams2);
            ViewGroup.LayoutParams layoutParams = previewShow.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = DisplayUtil.dip2px(CameraActivity.this, 250);
            previewShow.setLayoutParams(layoutParams);
        } else {

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (compoundButton != null) {
            switch (compoundButton.getId()) {
                case R.id.cb_camera_type:
                    take_picture.setVisibility(View.GONE);
                    if (b) {
                        tv_camera_type.setText(this.getString(R.string.take_a_video));
                        iv_take_picture.setVisibility(View.GONE);
                        iv_take_video.setVisibility(View.VISIBLE);
                    } else {
                        tv_camera_type.setText(this.getString(R.string.take_a_picture));
                        iv_take_picture.setVisibility(View.VISIBLE);
                        iv_take_video.setVisibility(View.GONE);
                        iv_end_video.setVisibility(View.GONE);
                        cb_start_stop.setVisibility(View.GONE);
                        stopCallTime();
                        tv_camera_time.setVisibility(View.GONE);
                    }
                    break;
                case R.id.cb_start_stop:
                    if (b) {
                        stopCallTime();
                        mdelayTime = System.currentTimeMillis();

                    } else {
                        mStartTime = mStartTime + System.currentTimeMillis() - mdelayTime;
                        startTime();
                    }
                    break;
            }
        }

    }

    /**
     * parse frme type
     *
     * @param buffer frme data
     * @return
     */
    private int parseFrameType(byte[] buffer) {

        // 00 00 00 01
        if (buffer[0] == 0 && buffer[1] == 0
                && buffer[2] == 0 && buffer[3] == 1) {
            if ((buffer[4] & 0x1f) == 7) {
                return 7;
            } else {
                return 10;
            }
        }
        return 10;
    }

    private void saveBitmap(byte[] bytes) {

        try {
            YuvImage image = new YuvImage(bytes, ImageFormat.NV21, 640, 480, null);
            if (image != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, 640, 480), 80, stream);

                final Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                stream.close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        take_picture.setImageBitmap(bmp);
                    }
                });

                BitmapUtils.saveBitmap(bmp);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
