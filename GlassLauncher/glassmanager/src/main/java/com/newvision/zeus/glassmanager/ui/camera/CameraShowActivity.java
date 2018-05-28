package com.newvision.zeus.glassmanager.ui.camera;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.newvision.zeus.glasscore.base.GlassCoreClientActivity;
import com.newvision.zeus.glasscore.helper.GlassRequestHelper;
import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.base.IBindServiceStatusListener;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.common.decoder.H264StreamDecoder;
import com.newvision.zeus.glassmanager.common.decoder.ReleaseUtils;
import com.newvision.zeus.glassmanager.common.decoder.StreamManager;
import com.newvision.zeus.glassmanager.common.decoder.VideoFrame;
import com.newvision.zeus.glassmanager.utils.BitmapUtils;
import com.newvision.zeus.glassmanager.utils.MyToast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Qing Jiwei on 7/6/17.
 */


public class CameraShowActivity extends GlassCoreClientActivity implements View.OnClickListener {

    private static final String TAG = "CameraShowActivity";
    private Button startVideo;
    private Button stopVideo;
    private Button takePhoto;
    private ImageView imageShow;
    private SurfaceView previewShow;

    private boolean isRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        initView();
    }

    private void initView() {

        previewShow = (SurfaceView) findViewById(R.id.sufferview_show);
        imageShow = (ImageView) findViewById(R.id.image_show);
        startVideo = (Button) findViewById(R.id.bt_start_video);
        stopVideo = (Button) findViewById(R.id.bt_stop_video);
        takePhoto = (Button) findViewById(R.id.bt_take_photo);
        startVideo.setOnClickListener(this);
        stopVideo.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
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
        if (mClientServiceBinder != null) {
            GlassRequestHelper.getInstance().unregisterListener(mClientServiceBinder, callback);
        }
        unbindTcpService();
        ReleaseUtils.releaseQuietly(StreamManager.getInstance());
    }

    @Override
    public void onBackPressed() {
        if (mClientServiceBinder.getLoginGlassStatus()) {
            exitCamera();
            return;
        }
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReleaseUtils.releaseQuietly(H264StreamDecoder.getInstance());
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
                    Log.i(TAG, "getResult: STOP_PREVIEW_ANS");
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
//                        GlassAskUtils.sendCloseAppAns(mClientServiceBinder, GlassConstants.INNER_APP_CAMERA);
                        GlassRequestHelper.getInstance().closeInternalAppAns(mClientServiceBinder,GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
                        MyToast.show("exit app");
                        finish();
                    }
                    break;

                case GlassMessageType.PHONE_CLOSE_APP_ANS:
                    Log.i(TAG, "getResult: PHONE_STOP_PREVIEW_ANS");
                    MyToast.show("exit app");
                    finish();
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

    private void exitCamera() {
//        GlassAskUtils.sendCloseAppAsk(mClientServiceBinder, GlassConstants.INNER_APP_CAMERA);
        GlassRequestHelper.getInstance().closeInternalApp(mClientServiceBinder,GlassConstants.INNER_APP_CAMERA);
    }


    /**
     * take picture
     */
    private void startTakePhoto() {
        MyToast.show("take picture");
    }

    /**
     * start take video
     */
    private void startTakeVideo() {
        MyToast.show("start take video");
    }

    /**
     * stop take video
     */
    private void stopTakeVideo() {
        MyToast.show("stop take video");
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
                        imageShow.setImageBitmap(bmp);
                    }
                });

                BitmapUtils.saveBitmap(bmp);
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }


}
