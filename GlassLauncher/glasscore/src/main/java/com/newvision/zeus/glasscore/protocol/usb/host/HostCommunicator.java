package com.newvision.zeus.glasscore.protocol.usb.host;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessage;
import com.newvision.zeus.glasscore.utils.GlassMessageHeadUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by yanjiatian on 2017/8/22.
 */

public abstract class HostCommunicator {
    private static final String TAG = HostCommunicator.class.getSimpleName();
    private boolean mIsRunning = false;
    private final List<byte[]> mSendBuffer = new CopyOnWriteArrayList<>();


    public HostCommunicator(Context context, UsbDevice device) {
        final UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        UsbEndpoint endpointIn = null;
        UsbEndpoint endpointOut = null;

        final UsbInterface usbInterface = device.getInterface(0);
        Log.d(TAG, "interface size = " + device.getInterfaceCount());

        for (int i = 0; i < device.getInterface(0).getEndpointCount(); i++) {

            final UsbEndpoint endpoint = device.getInterface(0).getEndpoint(i);
            if (endpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                endpointIn = endpoint;  //device to host
            }
            if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                endpointOut = endpoint;  //host to device
            }
        }

        if (endpointIn == null) {
            Log.d(TAG, "未发现数据传入端");
            return;
        }

        if (endpointOut == null) {
            Log.d(TAG, "未发现数据接收端");
            return;
        }

        final UsbDeviceConnection connection = usbManager.openDevice(device);

        if (connection == null) {
            Log.d(TAG, "无法打开设备");
            return;
        }

        final boolean claimResult = connection.claimInterface(usbInterface, true);

        if (!claimResult) {
            Log.d(TAG, "无法连接设备");
            return;
        } else {
            mIsRunning = true;
            new ReceiveThread(connection, endpointIn, usbInterface).start();
            new SendMessageThread(connection, endpointOut, usbInterface).start();
        }
    }

    public void sendMessage(byte[] payload) {
        if (payload != null) {
            mSendBuffer.add(payload);
        } else {
            Log.e(TAG, "payload is null");
        }
    }

    public abstract void onReceive(GlassMessage glassMessage);

    private class SendMessageThread extends Thread {
        private UsbDeviceConnection mConnection;
        private UsbEndpoint mEndpointOut;
        private UsbInterface mUsbInterface = null;

        public SendMessageThread(UsbDeviceConnection connection, UsbEndpoint endpointOut, UsbInterface usbInterface) {
            mConnection = connection;
            mEndpointOut = endpointOut;
            mUsbInterface = usbInterface;
        }

        @Override
        public void run() {
            super.run();
            while (mIsRunning) {
                synchronized (mSendBuffer) {
                    if (mSendBuffer.size() > 0) {
                        if (mSendBuffer.get(0) != null) {
                            int ret = mConnection.bulkTransfer(mEndpointOut, mSendBuffer.get(0), mSendBuffer.get(0).length, GlassConstants.USB_TIMEOUT_IN_MS);
                            if (ret == -1) {
                                Log.e(TAG, "发送失败 ret = " + ret);
                                //是否只要是ret = -1 就会导致之后发送给accessory的消息失败，有待考证，目前依旧能够正常收到accessory的消息
                            }

                        } else {
                            Log.e(TAG, "send message is null, now buffer size is " + mSendBuffer.size());
                        }
                        mSendBuffer.remove(0);
                    }
                }
            }
            Log.e(TAG, "close connect ...");
            mConnection.releaseInterface(mUsbInterface);
            mConnection.close();
        }
    }

    private class ReceiveThread extends Thread {
        private UsbDeviceConnection mConnection;
        private UsbEndpoint mEndpointIn = null;
        private UsbInterface mUsbInterface = null;
        private GlassMessage mGlassMessage = null;
        private byte[] mTotalBody = null;

        public ReceiveThread(UsbDeviceConnection connection, UsbEndpoint endpointIn, UsbInterface usbInterface) {
            mConnection = connection;
            mEndpointIn = endpointIn;
            mUsbInterface = usbInterface;
        }

        @Override
        public void run() {
            super.run();
            byte msg[] = new byte[GlassConstants.BUFFER_SIZE_IN_BYTES];
            Log.d(TAG, "请求接口 - 连接已建立");

            while (mIsRunning) {
                int readable = mConnection.bulkTransfer(mEndpointIn, msg, msg.length, GlassConstants.USB_TIMEOUT_IN_MS);

                if (readable > 0) {
                    if (readable < GlassConstants.GLASS_MESSAGE_HEAD_LENGTH) {
                        Log.e(TAG, "error package ...");
                        return;
                    }
                    mGlassMessage = null;
                    byte[] head = GlassMessageHeadUtils.subBytes(msg, 0, GlassConstants.GLASS_MESSAGE_HEAD_LENGTH);
                    mGlassMessage = GlassMessageHeadUtils.parseCommonHead(head);
                    int messageLength = mGlassMessage.messageLength;
                    Log.d(TAG, "type = "+ Integer.toHexString(mGlassMessage.messageType) + " pkg = " + mGlassMessage.pkgType + " Length = " + mGlassMessage.messageLength);
                    if (messageLength == 0) {
                        onReceive(mGlassMessage);
                    } else if (messageLength > 0 && messageLength <= GlassConstants.PACKAGE_MAX_BODY) {
                        if (mGlassMessage.pkgType == GlassConstants.PACKAGE_HAS_NEXT) {
                            if (mTotalBody == null) {
                                mTotalBody = GlassMessageHeadUtils.subBytes(msg, GlassConstants.GLASS_MESSAGE_HEAD_LENGTH, messageLength);
                            } else {
                                Log.d(TAG, "大容量消息，叠加");
                                mTotalBody = GlassMessageHeadUtils.byteMerger(mTotalBody, GlassMessageHeadUtils.subBytes(msg, GlassConstants.GLASS_MESSAGE_HEAD_LENGTH, messageLength));
                            }
                        } else if (mGlassMessage.pkgType == GlassConstants.PACKAGE_LAST) {
                            Log.d(TAG, "pkgType = " + mGlassMessage.pkgType + " Length = " + mGlassMessage.messageLength);
                            if (mTotalBody != null) {
                                mTotalBody = GlassMessageHeadUtils.byteMerger(mTotalBody, GlassMessageHeadUtils.subBytes(msg, GlassConstants.GLASS_MESSAGE_HEAD_LENGTH, messageLength));
                                mGlassMessage.messageLength = mTotalBody.length;
                                mGlassMessage.messageBody = mTotalBody;
                                onReceive(mGlassMessage);
                                mTotalBody = null;
                            } else {
                                Log.e(TAG, "mTotalBody is null");
                            }
                        } else {
                            mGlassMessage.messageBody = GlassMessageHeadUtils.subBytes(msg, GlassConstants.GLASS_MESSAGE_HEAD_LENGTH, messageLength);
                            onReceive(mGlassMessage);
                            mTotalBody = null;
                        }
                    } else {
                        Log.e(TAG, "不合法的包信息,length  = " + messageLength);
                    }
                }
            }
            Log.e(TAG, "connect close ...");
            mConnection.releaseInterface(mUsbInterface);
            mConnection.close();
        }
    }
}
