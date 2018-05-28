package com.newvision.zeus.glassmanager.setting.usb;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by yanjiatian on 2017/8/2.
 * UsbAccessory和UsbDevice的区别
 * UsbDevice：正常的，USB的Host和USB的Device架构中的USB的Device
 * 所以，此时：Android设备是USB的Host，外接的USB设备是USB的Device
 * 此时，Android设备作为USB的Host，要做USB Host该干的事情：
 * 给USB外接设备供电
 * 负责管理USB总线
 * UsbAccessory：和标准的USB的概念相反
 * USB设备是USB的Host
 * 所以，此时USB设备，也要干其作为USB的Host的事情
 * USB设备，要给作为USB的Device的Android设备供电
 * USB设备要负责管理USB总线
 * 而Android设备是USB的Device
 * 此时，从概念上说，相当于把Android设备，当做Accessory附件，挂在USB设备上
 */

public abstract class UsbCommunicator {
    private static final String TAG = UsbCommunicator.class.getSimpleName();
    private Context mContext;
    private UsbManager mUsbManager;
    private ParcelFileDescriptor mFileDescriptor;
    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;
    private boolean mFlagRunning;
    private static final int SEND_MSG = 1;

    public UsbCommunicator(Context context) {
        this.mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (mUsbManager == null) {
            Log.d(TAG, "无USB设备");
        } else {
            UsbAccessory[] accessoryList = mUsbManager.getAccessoryList();
            if (accessoryList == null || accessoryList.length == 0) {
                Log.d(TAG, "未检测到usb host 设备");
            } else {
                openAccessory(accessoryList[0]);
            }
        }

    }

    public abstract void onReceive(final byte[] payload, final int length);

    public abstract void onError(String msg);

    public abstract void onConnected();

    public abstract void onDisconnected();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_MSG:
                    try {
                        mOutputStream.write((byte[]) msg.obj);
                    } catch (IOException e) {
                        onError("Accessory 发送数据失败 -- " + e.toString());
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void sendMessage(byte[] msg) {
        Message message = mHandler.obtainMessage();
        message.what = SEND_MSG;
        message.obj = msg;
        mHandler.sendMessage(message);
    }

    private void openAccessory(UsbAccessory accessory) {
        mFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mFileDescriptor != null) {
            FileDescriptor fileDescriptor = mFileDescriptor.getFileDescriptor();
            mInputStream = new FileInputStream(fileDescriptor);
            mOutputStream = new FileOutputStream(fileDescriptor);

            new CommunicationThread().start();

            onConnected();
        } else {
            onError("建立连接失败");
        }
    }

    private void closeAccessory() {
        mFlagRunning = false;

        try {
            if (mFileDescriptor != null) {
                mFileDescriptor.close();
            }

        } catch (Exception e) {

        } finally {
            mFileDescriptor = null;
        }
        onDisconnected();
    }

    private class CommunicationThread extends Thread {
        @Override
        public void run() {
            super.run();
            mFlagRunning = true;
            while (mFlagRunning) {
                byte[] msg = new byte[128];
                try {
                    int len = mInputStream.read(msg);
                    while (mInputStream != null && len > 0 && mFlagRunning) {
                        onReceive(msg, len);
                        len = mInputStream.read(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onError("Usb Accessory 接收数据失败 -- " + e.toString());
                    closeAccessory();
                }
            }
        }
    }

}
