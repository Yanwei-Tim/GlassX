package com.newvision.zeus.glasslauncher.usb;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.newvision.zeus.glasscore.base.GlassCoreServerActivity;
import com.newvision.zeus.glasscore.base.IBindUsbServiceStatusListener;
import com.newvision.zeus.glasscore.protocol.usb.host.GlassUsbHostService;
import com.newvision.zeus.glasslauncher.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yanjiatian on 2017/7/27.
 */

public class UsbHostActivity extends GlassCoreServerActivity {
    private static final String TAG = UsbHostActivity.class.getSimpleName();
    public static final String DEVICE_EXTRA_KEY = "accessory";
    private UsbManager mUsbManager;
    public static final int USB_TIMEOUT_IN_MS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "host ConnectActivity onCreate()....");
        super.onCreate(savedInstanceState);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        bindUsbService(new IBindUsbServiceStatusListener() {
            @Override
            public void bindSuccess() {
                startConnectAccessory();
            }

            @Override
            public void unbindSuccess() {

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindUsbService();
    }

    private void startConnectAccessory() {
        final HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        if (deviceList == null || deviceList.size() == 0) {
            Log.d(TAG, "没有读取到usb设备");
            finish();
            return;
        } else {
            Log.d(TAG, "devicesList size = " + deviceList.size());
        }

        if (searchForUsbAccessory(deviceList)) {
            Log.d(TAG, "searchForUsbAccessory");
            return;
        }

        for (UsbDevice device : deviceList.values()) {
            initAccessory(device);
        }

        finish();
    }

    private boolean searchForUsbAccessory(final HashMap<String, UsbDevice> deviceList) {
        for (UsbDevice device : deviceList.values()) {
            if (isUsbAccessory(device)) {
                mUsbServiceBinder.startCommunicator(device);
                finish();
                return true;
            }
        }

        return false;
    }

    private boolean isUsbAccessory(final UsbDevice device) {
        Log.d(TAG, "productId = " + device.getProductId());
        return (device.getProductId() == 0x2d00) || (device.getProductId() == 0x2d01);
    }

    private boolean initAccessory(final UsbDevice device) {
        Log.d(TAG, "initAccessory...");
        final UsbDeviceConnection connection = mUsbManager.openDevice(device);

        if (connection == null) {
            return false;
        }

        initStringControlTransfer(connection, 0, "quandoo"); // MANUFACTURER
        initStringControlTransfer(connection, 1, "Android2AndroidAccessory"); // MODEL
        initStringControlTransfer(connection, 2, "showcasing android2android USB communication"); // DESCRIPTION
        initStringControlTransfer(connection, 3, "0.1"); // VERSION
        initStringControlTransfer(connection, 4, "http://quandoo.de"); // URI
        initStringControlTransfer(connection, 5, "42"); // SERIAL

        connection.controlTransfer(0x40, 53, 0, 0, new byte[]{}, 0, USB_TIMEOUT_IN_MS);

        connection.close();

        return true;
    }

    private void initStringControlTransfer(final UsbDeviceConnection deviceConnection,
                                           final int index,
                                           final String string) {
        deviceConnection.controlTransfer(0x40, 52, 0, index, string.getBytes(), string.length(), USB_TIMEOUT_IN_MS);
    }
}
