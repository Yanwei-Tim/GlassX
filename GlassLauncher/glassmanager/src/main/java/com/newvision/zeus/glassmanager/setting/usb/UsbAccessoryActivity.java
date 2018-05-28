package com.newvision.zeus.glassmanager.setting.usb;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.newvision.zeus.glasscore.base.IBindUsbServiceStatusListener;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.helper.GlassPacketMessageHelper;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glassmanager.ui.BaseActivity;
import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.test.CameraUsbTestActivity;


/**
 * Created by yanjiatian on 2017/8/2.
 */

public class UsbAccessoryActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = UsbAccessoryActivity.class.getSimpleName();
    private boolean bindStatus = false;
    AnimationDrawable mAnimationDrawable;
    private Button btn_connect;
    private Button btn_cancel;
    private Button btn_send;
    private Button btn_camera;
    private static final int SHOW_ANIMATION = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_ANIMATION:
                    mAnimationDrawable.stop();
                    mAnimationDrawable.start();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Usb Accessory onCreate() ...");
        setContentView(R.layout.activity_usb_accessory);
        initViews();
    }

    @Override
    public void initViews() {
        initAnim();

        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_connect.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
    }

    private IMessageCallback keepAlive = new IMessageCallback() {
        @Override
        public void getResult(int tag, Object result) {
            if (tag == GlassMessageType.GLASS_KEEP_ALIVE && ((String) result).equals("usb")) {
                Log.d(TAG, "keep alive callback");
                mHandler.sendEmptyMessage(SHOW_ANIMATION);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        bindUsbService(new IBindUsbServiceStatusListener() {
            @Override
            public void bindSuccess() {
                Log.d(TAG, "bind usb service success ...");
                bindStatus = true;
                mAccessoryBinder.registerListener(keepAlive);
            }

            @Override
            public void unbindSuccess() {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAccessoryBinder != null) {
            mAccessoryBinder.unregisterListener(keepAlive);
        }
        unbindUsbService();

    }

    private void initAnim() {
        ImageView animationImg = (ImageView) findViewById(R.id.keep_alive);
        animationImg.setImageResource(R.drawable.keep_alive);
        mAnimationDrawable = (AnimationDrawable) animationImg.getDrawable();
        mAnimationDrawable.setOneShot(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                if (bindStatus) {
                    mAccessoryBinder.connectServer();
                }
                break;
            case R.id.btn_cancel:
                if (bindStatus) {
                    Log.d(TAG, "disconnectServer ...");
                    mAccessoryBinder.disconnectServer();
                }

                break;
            case R.id.btn_send:
                if (bindStatus) {
                    mAccessoryBinder.sendMessage(GlassPacketMessageHelper.getInstance().packetMessage(GlassMessageType.GLASS_KEEP_ALIVE, GlassErrorCode.OK, null));
                }
                break;

            case R.id.btn_camera:
                startActivity(new Intent(UsbAccessoryActivity.this, CameraUsbTestActivity.class));
                break;
        }
    }

}
