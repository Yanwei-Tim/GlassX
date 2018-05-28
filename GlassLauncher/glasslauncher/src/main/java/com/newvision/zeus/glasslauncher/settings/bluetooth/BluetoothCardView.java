/**
 * ****************************************************************************
 * Copyright (C) 2014 Ceyes Inc. All rights reserved.
 * *****************************************************************************
 */

package com.newvision.zeus.glasslauncher.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.ceyes.glasswidget.cardview.GlassCardView;
import cn.ceyes.glasswidget.menuview.GlassMenu;
import cn.ceyes.glasswidget.menuview.GlassMenuEntity;

public class BluetoothCardView extends GlassCardView {

    private static final String TAG = "GlassBTSettingCardView";

    private TextView btTitleText;
    private TextView btStateText;
    private TextView btConnDeviceNameText;
    private TextView headsetStateText;
    private TextView btDeviceConnText;
    private TextView btVisibilityText;

    private ImageView mBluetoothIconImageView;
    private int visibleTime = 60;
    private int VISIBLEING = 0x00aa01;
    private int mBluetoothState = BluetoothAdapter.STATE_ON;
    private Boolean isCounting = false;
    private GlassMenu mGlassMenu;
    private BluetoothAdapter mBluetoothAdapter;


    public BluetoothCardView(Context context) {
        super(context);
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_bluetooth, this);

        btTitleText = (TextView) view.findViewById(R.id.txt_bt_title);
        btStateText = (TextView) view.findViewById(R.id.txt_bt_state);
        btConnDeviceNameText = (TextView) view.findViewById(R.id.txt_bt_con_devicename);
        headsetStateText = (TextView) view.findViewById(R.id.txt_headset_state);
        btDeviceConnText = (TextView) view.findViewById(R.id.txt_bt_device_connect);

        mBluetoothIconImageView = (ImageView) view.findViewById(R.id.img_bluetooth);
        btVisibilityText = (TextView) view.findViewById(R.id.txt_btvisible);

//        mBluetoothAdapter = GlassBluetoothManager.getInstance().getCurrentBTAdapter();
        Log.d(TAG, "GlassBTSettingCardView created.");
        mGlassMenu = new GlassMenu(mContext);

    }

    @Override
    public void onCardSelected() {
        final int MENU_ID_OPEN_BT = 0;
        final int MENU_ID_CLOSE_BT = 1;
        if (mBluetoothAdapter == null || mBluetoothState == BluetoothAdapter.STATE_TURNING_OFF
                || mBluetoothState == BluetoothAdapter.STATE_TURNING_ON) {
            return;
        }
        List<GlassMenuEntity> menuEntities = new ArrayList<GlassMenuEntity>();

        if (mBluetoothAdapter.isEnabled()) {
            menuEntities.add(new GlassMenuEntity(MENU_ID_CLOSE_BT, R.drawable.bluetooth_close, R.string.bluetooth_menu_close));
        } else {
            menuEntities.add(new GlassMenuEntity(MENU_ID_OPEN_BT, R.drawable.bluetooth_open, R.string.bluetooth_menu_open));
        }

        mGlassMenu.setMenuEntities(menuEntities).setOnMenuSelectCallback(new GlassMenu.IMenuSelectCallback() {
            @Override
            public void onMenuSelected(int menuEntityId) {
                switch (menuEntityId) {
                    case MENU_ID_OPEN_BT:
//                        GlassBluetoothManager.getInstance().setBluetoothEnable(true);
                        break;
                    case MENU_ID_CLOSE_BT:
//                        GlassBluetoothManager.getInstance().setBluetoothEnable(false);
                        break;
                }
            }
        }).show();
    }

    @Override
    public void onCardVisible() {
        Log.d(TAG, "onCardVisible");
        super.onCardVisible();
        if (mBluetoothAdapter == null) {
            return;
        }
//        GlassBluetoothManager.getInstance().getMessageService().registerConnectStateListener(mBTSocketListener);
//        GlassHFPHelper.getInstance().registerListener(mHFStateListener);
//
//        GlassBluetoothManager.getInstance().registerBluetoothStateChanged(mBTStateChanged);

//        if (mBluetoothAdapter.isEnabled()) {
//            setDevDiscoverable(true);
//            setDeviceName();
//        } else {
//            btTitleText.setText(mContext.getString(R.string.label_bluetooth));
//            btVisibilityText.setVisibility(View.GONE);
//            btStateText.setTextColor(mContext.getResources().getColor(R.color.setting_default_color));
//            btStateText.setText(R.string.bluetooth_off);
//        }
    }

    @Override
    public void onCardInvisible() {
        Log.d(TAG, "onCardInvisible");
        super.onCardInvisible();
//        if (mBluetoothAdapter == null) {
//            return;
//        }
//        GlassBluetoothManager.getInstance().getMessageService().unregisterConnectStateListener(mBTSocketListener);
//        GlassBluetoothManager.getInstance().unregisterBluetoothStateChanged(mBTStateChanged);
//        GlassHFPHelper.getInstance().unregisterListener(mHFStateListener);

//        if (mBluetoothAdapter.isEnabled()) {
//            setDevDiscoverable(false);
//        }
    }

    @Override
    public void onCardFinished() {
        super.onCardFinished();

        sendGlassEvent(50000, null);
    }

    // set bluetooth discoverable.
    private void setDevDiscoverable(boolean discoverable) {
        Log.d(TAG, "setDevDiscoverable " + discoverable);
        if (discoverable) {
//            if (GlassBluetoothManager.getInstance().getMessageService().isConnected()) {
//                return;
//            }
            synchronized (isCounting) {
                if (!isCounting) {
                    isCounting = true;
                    setDiscoverableTime(60);
                    btVisibilityText.setVisibility(View.VISIBLE);
                    visibleHandler.sendEmptyMessage(VISIBLEING);
                }
            }
        } else {
            synchronized (isCounting) {
                isCounting = false;
            }
            setDiscoverableTime(-1);
            btVisibilityText.setVisibility(View.GONE);
            visibleHandler.removeMessages(VISIBLEING);
        }
    }

    private void setDiscoverableTime(int timeout) {
        try {
            visibleTime = timeout;
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class, int.class);
            setScanMode.setAccessible(true);

            if (timeout > 0) {
                setDiscoverableTimeout.invoke(mBluetoothAdapter, timeout);
                setScanMode.invoke(mBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, timeout);
            } else {
                setScanMode.invoke(mBluetoothAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE, timeout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler visibleHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == VISIBLEING) {
                synchronized (isCounting) {
                    if (!isCounting) {
                        return;
                    }

                    visibleTime--;
                    if (visibleTime < 0) {
                        setDevDiscoverable(true);
                        visibleTime = 60;
                    }
                    String time = "";
                    int seconds = visibleTime % 60;
                    if (seconds < 10) {
                        time = "00:0" + seconds;
                    } else {
                        time = "00:" + seconds;
                    }

                    btVisibilityText.setText(getResources().getString(R.string.label_bt_search, time.toString()));
                    if (isCounting) {
                        sendEmptyMessageDelayed(VISIBLEING, 1000);
                    }
                }
            }
        }

    };

//    private GlassBluetoothManager.BluetoothStateChanged mBTStateChanged = new GlassBluetoothManager.BluetoothStateChanged() {
//        @Override
//        public void onBluetoothStateChange(int state) {
//            btStateText.setVisibility(View.VISIBLE);
//            mBluetoothState = state;
//            switch (state) {
//                case BluetoothAdapter.STATE_TURNING_ON:
//                    Log.d(TAG, "btStateReceiver STATE_TURNING_ON ");
//                    btStateText.setTextColor(mContext.getResources().getColor(R.color.color_green));
//                    btStateText.setText(R.string.bluetooth_truning_on);
//                    break;
//                case BluetoothAdapter.STATE_ON:
//                    Log.d(TAG, "btStateReceiver STATE_ON ");
//                    setDevDiscoverable(true);
//                    setDeviceName();
//                    break;
//                case BluetoothAdapter.STATE_TURNING_OFF:
//                    Log.d(TAG, "btStateReceiver STATE_TURNING_OFF ");
//                    setDevDiscoverable(false);
//
//                    btStateText.setTextColor(mContext.getResources().getColor(R.color.setting_default_color));
//                    btStateText.setText(R.string.bluetooth_truning_off);
//                    break;
//                case BluetoothAdapter.STATE_OFF:
//                    Log.d(TAG, "btStateReceiver STATE_OFF ");
//                    onBluetoothDisconnect();
//                    onHeadsetDisConnect();
//                    btStateText.setTextColor(mContext.getResources().getColor(R.color.setting_default_color));
//                    btStateText.setText(R.string.bluetooth_off);
//                    btTitleText.setText(mContext.getString(R.string.label_bluetooth));
//                    break;
//            }
//        }
//    };
//
//    private GlassBTSocketConnectStateListener mBTSocketListener = new GlassBTSocketConnectStateListener() {
//        @Override
//        public void onStateChanged(int state) {
//            Log.d(TAG, "onStateChanged: " + state);
//            btStateText.setVisibility(View.VISIBLE);
//
//            if (state == GlassBluetoothServer.STATE_CONNECTED) {
//                onBluetoothConnect();
//                setDevDiscoverable(false);
//            } else {
//                mBluetoothIconImageView.setImageResource(R.drawable.icon_bluetooth_disconnect);
//                if (!mBluetoothAdapter.isEnabled()) {
//                    if (mBluetoothState == BluetoothAdapter.STATE_ON) {
//                        btStateText.setTextColor(mContext.getResources().getColor(R.color.setting_default_color));
//                        btStateText.setText(R.string.bluetooth_off);
//                    }
//                } else {
//                    onBluetoothDisconnect();
//                    // when connection lost, set visible again
//                    setDevDiscoverable(true);
//                }
//            }
//        }
//    };
//
//    private GlassHFPHelper.GlassHFConnectStateListener mHFStateListener = new GlassHFPHelper.GlassHFConnectStateListener() {
//        @Override
//        public void onGlassHFStateChanged(int state) {
//            if (state == BluetoothProfile.STATE_CONNECTED) {
//                onHeadsetConnect(true);
//            } else {
//                onHeadsetDisConnect();
//            }
//        }
//    };
//
//    private void setDeviceName() {
//        GlassBluetoothManager.getInstance().getDeviceName(new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                switch (msg.what) {
//                    case GlassBluetoothManager.STATE_DEVICE_NAME:
//                        btTitleText.setText(mContext.getString(R.string.label_bluetooth) + "  " + msg.obj);
//                        break;
//                }
//            }
//        });
//    }
//
//    private void onBluetoothConnect() {
//        Log.i(TAG, "onBluetoothConnect");
//        btStateText.setVisibility(View.GONE);
//        GlassSystemInfoProvider.getInstance().setRemoteDeviceNameCallback(new GlassSystemInfoProvider.IRemoteDeviceNameChangedCallback() {
//            @Override
//            public void onRemoteDeviceNameChanged(String deviceName) {
//                btConnDeviceNameText.setVisibility(View.VISIBLE);
//                Log.i(TAG, "onBluetoothConnect deviceName " + deviceName);
//                btConnDeviceNameText.setText(deviceName);
//            }
//        });
//        btDeviceConnText.setVisibility(View.VISIBLE);
//        mBluetoothIconImageView.setImageResource(R.drawable.icon_bluetooth);
//    }
//
//    private void onBluetoothDisconnect() {
//        Log.i(TAG, "onBluetoothDisconnect");
//        btConnDeviceNameText.setVisibility(View.GONE); //远程设备蓝牙名称
//        btDeviceConnText.setVisibility(View.GONE);  //提示：已连接眼镜管家
//
//        //判断耳机状态
//        if (!GlassHFPHelper.getInstance().isHeadsetConnected()) {
//            Log.i(TAG, "not isHeadsetConnected");
//            btStateText.setVisibility(View.VISIBLE);
//            btStateText.setTextColor(mContext.getResources().getColor(R.color.setting_default_color));
//            btStateText.setText(R.string.label_bt_nodevice);
//        }
//    }
//
//    private void onHeadsetConnect(boolean handsfreeCon) {
//        Log.i(TAG, "onHeadsetConnect  " + handsfreeCon);
//        btStateText.setVisibility(View.GONE);
//        headsetStateText.setVisibility(View.VISIBLE);
//        if (handsfreeCon) {
//            headsetStateText.setTextColor(mContext.getResources().getColor(R.color.color_green));
//            headsetStateText.setText(R.string.bluetooth_headset_conn);
//        } else {
//            headsetStateText.setTextColor(mContext.getResources().getColor(R.color.setting_default_color));
//            headsetStateText.setText(R.string.bluetooth_headset_disconn);
//        }
//    }
//
//    private void onHeadsetDisConnect() {
//        Log.i(TAG, "onHeadsetDisConnect");
//        headsetStateText.setVisibility(View.GONE);
//        if (!GlassBluetoothManager.getInstance().getMessageService().isConnected()) {
//            btStateText.setVisibility(View.VISIBLE);
//            btStateText.setText(R.string.label_bt_nodevice);
//        }
//    }


}
