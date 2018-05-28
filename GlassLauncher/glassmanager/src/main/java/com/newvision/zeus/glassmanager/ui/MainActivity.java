package com.newvision.zeus.glassmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.newvision.zeus.glasscore.base.IBindServiceStatusListener;
import com.newvision.zeus.glasscore.base.IBindUsbServiceStatusListener;
import com.newvision.zeus.glasscore.helper.GlassRequestHelper;
import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.setting.GlassSettingActivity;
import com.newvision.zeus.glassmanager.setting.pair.ScanGlassActivity;
import com.newvision.zeus.glassmanager.setting.usb.UsbAccessoryActivity;
import com.newvision.zeus.glassmanager.setting.wifi.WifiListActivity;
import com.newvision.zeus.glassmanager.test.CameraTestActivity;
import com.newvision.zeus.glassmanager.test.CameraUsbTestActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button connect_wifi;
    private Button connect_glass;
    private Button connect_usb;

    /**
     * THE SWITCH TO OPEN USB MODE
     */
    private boolean isUsbMode = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    public void initViews() {
        setTitle(R.string.main_title);
        setNavigateLeftImage(R.drawable.about_me);
        showNavigateLeftIcon(false);
        setNavigateLeftOnClickListener(this);
        setNavigateRightImage(R.drawable.setting);
        setNavigateRightOnClickListener(this);
        showNavigateRightIcon(true);

        connect_wifi = (Button) findViewById(R.id.connect_wifi);
        connect_wifi.setOnClickListener(this);
        connect_glass = (Button) findViewById(R.id.connect_glass);
        connect_glass.setOnClickListener(this);
        connect_usb = (Button) findViewById(R.id.connect_usb);
        connect_usb.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_navigate_left:
                break;
            case R.id.img_navigate_right:
                startActivity(new Intent(this, GlassSettingActivity.class));
                overridePendingTransition(R.anim.action_rigth_enter, R.anim.action_left_exit);
                break;
            case R.id.connect_wifi:
                startActivity(new Intent(this, WifiListActivity.class));
                overridePendingTransition(R.anim.action_rigth_enter, R.anim.action_left_exit);
                break;
            case R.id.connect_glass:
                startActivity(new Intent(this, ScanGlassActivity.class));
                overridePendingTransition(R.anim.action_rigth_enter, R.anim.action_left_exit);
                break;
            case R.id.connect_usb:
                startActivity(new Intent(this, UsbAccessoryActivity.class));
                overridePendingTransition(R.anim.action_rigth_enter, R.anim.action_left_exit);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isUsbMode) {
            bindUsbService(new IBindUsbServiceStatusListener() {
                @Override
                public void bindSuccess() {
                    Log.i(TAG, "bindSuccess:usb  success");
                    mAccessoryBinder.registerListener(callback);
                }

                @Override
                public void unbindSuccess() {
                    Log.i(TAG, "unbindSuccess: fail");
                    mAccessoryBinder.unregisterListener(callback);
                }
            });
        } else {
            bindTcpService(new IBindServiceStatusListener() {
                @Override
                public void bindSuccess() {
                    Log.i(TAG, "bindSuccess: tcp success");
                    mClientServiceBinder.registerListener(callback);
//                    GlassRequestHelper.getInstance().registerListener(mClientServiceBinder, callback);
                }

                @Override
                public void unbindSuccess() {
                }
            });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isUsbMode) {
            if (mAccessoryBinder != null) {
                mAccessoryBinder.unregisterListener(callback);
            }
            unbindUsbService();
        } else {
            if (mClientServiceBinder != null) {
                mClientServiceBinder.unregisterListener(callback);
//                GlassRequestHelper.getInstance().unregisterListener(mClientServiceBinder, callback);
            }
            unbindTcpService();
        }
    }

    private IMessageCallback callback = new IMessageCallback() {
        @Override
        public void getResult(int tag, Object result) {

            Log.i(TAG, "getResult: message type=" + Integer.toHexString(tag));
            switch (tag) {
                case GlassMessageType.PHONE_OPEN_APP_ASK:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        if (isUsbMode) {
                            startActivity(new Intent(MainActivity.this, CameraUsbTestActivity.class));
                            GlassRequestHelper.getInstance().openInternalAppAns(mAccessoryBinder, GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
                        } else {
                            startActivity(new Intent(MainActivity.this, CameraTestActivity.class));
                            GlassRequestHelper.getInstance().openInternalAppAns(mClientServiceBinder, GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
                        }
                        finish();
                    }
                    break;
                case GlassMessageType.GLASS_OPEN_APP_ANS:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        if (isUsbMode) {
                            startActivity(new Intent(MainActivity.this, CameraUsbTestActivity.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, CameraTestActivity.class));
                        }
                        finish();
                    }
                    break;

            }
        }
    };
}
