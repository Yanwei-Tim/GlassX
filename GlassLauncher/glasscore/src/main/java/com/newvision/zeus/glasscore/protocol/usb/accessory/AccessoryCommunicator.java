package com.newvision.zeus.glasscore.protocol.usb.accessory;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.utils.GlassMessageHeadUtils;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by yanjiatian on 2017/8/7.
 */

public abstract class AccessoryCommunicator {
    private static final String TAG = AccessoryCommunicator.class.getSimpleName();
    private UsbManager mUsbManager;
    private Context mContext;
    private ParcelFileDescriptor mParcelFileDescriptor;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private boolean mIsRunning = false;
    private final List<byte[]> mSendBuffer = new CopyOnWriteArrayList<>();

    public AccessoryCommunicator(Context context) {
        this.mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (mUsbManager == null) {
            Log.e(TAG, "设备无USB服务！");
            return;
        }
        UsbAccessory[] accessoryList = mUsbManager.getAccessoryList();
        if (accessoryList == null || accessoryList.length == 0) {
            Log.e(TAG, "未发现Host设备！");
        } else {
            openAccessory(accessoryList[0]);
        }
    }

    public void sendMessage(byte[] payload) {
        if (payload != null) {
            mSendBuffer.add(payload);
        } else {
            Log.d(TAG, "payload is null");
        }
    }

    public void closeAccessory() {
        Log.d(TAG, "closeAccessory() ");
        if (mParcelFileDescriptor != null) {
            try {
                mParcelFileDescriptor.close();
                if (mFileInputStream != null) {
                    mFileInputStream.close();
                }
                if (mFileOutputStream != null) {
                    mFileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mParcelFileDescriptor = null;
                mFileInputStream = null;
                mFileOutputStream = null;
                mIsRunning = false;
            }
        }
        onDisconnected();
    }

    public abstract void onReceive(GlassMessage glassMessage);

    public abstract void onError(String msg);

    public abstract void onConnected();

    public abstract void onDisconnected();


    private void receive(GlassMessage glassMessage) {
        onReceive(glassMessage);
    }

    private void openAccessory(UsbAccessory accessory) {
        mParcelFileDescriptor = mUsbManager.openAccessory(accessory);
        if (mParcelFileDescriptor != null) {
            FileDescriptor fileDescriptor = mParcelFileDescriptor.getFileDescriptor();
            mFileInputStream = new FileInputStream(fileDescriptor);
            mFileOutputStream = new FileOutputStream(fileDescriptor);
            mIsRunning = true;
            new CommunicationThread().start();
            new SendMessageThread().start();
            onConnected();
        } else {
            Log.d(TAG, "无法打开usb设备");
        }
    }

    private class SendMessageThread extends Thread {
        @Override
        public void run() {
            super.run();
            Log.d(TAG, "send thread start ...");
            while (mIsRunning) {
                synchronized (mSendBuffer) {
                    if (mSendBuffer.size() > 0) {
                        try {
                            if (mSendBuffer.get(0) != null) {
                                mFileOutputStream.write(mSendBuffer.get(0));
                            } else {
                                Log.d(TAG, "send message is null");
                            }
                            mSendBuffer.remove(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            mSendBuffer.remove(0);
                            Log.d(TAG, "消息发送失败" + e.getMessage());
                        }
                    }
                }
            }
            Log.e(TAG, "send message thread is over !!!");

        }
    }


    private class CommunicationThread extends Thread {
        private GlassMessage mGlassMessage = null;
        private byte[] mTotalBody = null;
        byte[] msg;

        @Override
        public void run() {
            Log.d(TAG, "receive thread start ...");
            try {
                msg = new byte[GlassConstants.BUFFER_SIZE_IN_BYTES];
                //处理收到的数据
                int readable = 0;
                readable = mFileInputStream.read(msg);
                while (mFileInputStream != null && readable > 0 && mIsRunning) {
                    if (readable < GlassConstants.GLASS_MESSAGE_HEAD_LENGTH) {
                        Log.e(TAG, "error package ... ");
                        continue;
                    }
                    mGlassMessage = null;
                    byte[] head = GlassMessageHeadUtils.subBytes(msg, 0, GlassConstants.GLASS_MESSAGE_HEAD_LENGTH);
                    mGlassMessage = GlassMessageHeadUtils.parseCommonHead(head);
                    int messageLength = mGlassMessage.messageLength;
                    if (messageLength == 0) {
                        receive(mGlassMessage);
                    } else if (messageLength > 0 && messageLength <= (GlassConstants.BUFFER_SIZE_IN_BYTES - GlassConstants.GLASS_MESSAGE_HEAD_LENGTH)) {
                        if (mGlassMessage.pkgType == GlassConstants.PACKAGE_HAS_NEXT) {
                            if (mTotalBody == null) {
                                mTotalBody = GlassMessageHeadUtils.subBytes(msg, GlassConstants.GLASS_MESSAGE_HEAD_LENGTH, messageLength);
                            } else {
                                Log.d(TAG, "大容量消息，叠加");
                                mTotalBody = GlassMessageHeadUtils.byteMerger(mTotalBody, GlassMessageHeadUtils.subBytes(msg, GlassConstants.GLASS_MESSAGE_HEAD_LENGTH, messageLength));
                            }
                        } else if (mGlassMessage.pkgType == GlassConstants.PACKAGE_LAST) {
                            if (mTotalBody != null) {
                                mTotalBody = GlassMessageHeadUtils.byteMerger(mTotalBody, GlassMessageHeadUtils.subBytes(msg, GlassConstants.GLASS_MESSAGE_HEAD_LENGTH, messageLength));
                                mGlassMessage.messageLength = mTotalBody.length;
                                mGlassMessage.messageBody = mTotalBody;
                                receive(mGlassMessage);
                                mTotalBody = null;
                            } else {
                                Log.e(TAG, "mTotalBody is null");
                            }
                        } else {
                            mGlassMessage.messageBody = GlassMessageHeadUtils.subBytes(msg, GlassConstants.GLASS_MESSAGE_HEAD_LENGTH, messageLength);
                            receive(mGlassMessage);
                            mTotalBody = null;
                        }
                    } else {
                        Log.e(TAG, "不合法的包信息 length = " + messageLength);
                    }
                    readable = mFileInputStream.read(msg);
                }

            } catch (IOException e) {
                e.printStackTrace();
                onError("接收信息错误");
                closeAccessory();
            }
            Log.e(TAG, "receive thread is over !!!");

        }
    }
}
