package com.newvision.zeus.glasslauncher.usb;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yanjiatian on 2017/8/2.
 * bulk transfer buffer size limited to 16K (16384)
 * 一次传输的数据大小不能超过16k
 */

public class ChatActivity extends Activity {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private final AtomicBoolean keepThreadAlive = new AtomicBoolean(true);
    private final List<String> sendBuffer = new ArrayList<>();
    private final List<byte[]> sendList = new ArrayList<>();
    public static final int BUFFER_SIZE_IN_BYTES = 1024 * 16;
    public static final int USB_TIMEOUT_IN_MS = 100;
    TextView contentTextView;
    EditText input;
    Button btn_send;
    byte[] test = new byte[BUFFER_SIZE_IN_BYTES];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "host onCreate()");
        setContentView(R.layout.activity_chat);
        contentTextView = (TextView) findViewById(R.id.content_text);
        input = (EditText) findViewById(R.id.input_edittext);
        for (int i = 0; i < test.length; i++) {
            test[i] = (byte) (i % 128);
        }
        btn_send = (Button) findViewById(R.id.send_button);
        //监听发送消息事件
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String inputString = input.getText().toString();
                if (inputString.length() == 0) {
                    return;
                }
                sendString(inputString);
                input.setText("");
            }
        });

        new Thread(new CommunicationRunnable()).start();
    }

    public void sendString(final String string) {
        printLineToUI("发出消息：" + string);
        sendBuffer.add(string);
    }

    private class CommunicationRunnable implements Runnable {

        @Override
        public void run() {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            final UsbDevice device = getIntent().getParcelableExtra(ConnectActivity.DEVICE_EXTRA_KEY);

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
                printLineToUI("未发现数据传入端");
                return;
            }

            if (endpointOut == null) {
                Log.d(TAG, "未发现数据接收端");
                printLineToUI("未发现数据接收端");
                return;
            }

            final UsbDeviceConnection connection = usbManager.openDevice(device);

            if (connection == null) {
                Log.d(TAG, "无法打开设备");
                printLineToUI("无法打开设备");
                return;
            }

            final boolean claimResult = connection.claimInterface(usbInterface, true);

            if (!claimResult) {
                Log.d(TAG, "无法连接设备");
                printLineToUI("无法连接设备");
            } else {
                final byte buff[] = new byte[BUFFER_SIZE_IN_BYTES];
                Log.d(TAG, "请求接口 - 连接已建立");
                printLineToUI("请求接口 - 连接已建立");
                long count = 0;
                long first = 0;
                while (keepThreadAlive.get()) {
                    final int bytesTransferred = connection.bulkTransfer(endpointIn, buff, buff.length, USB_TIMEOUT_IN_MS);
                    if (bytesTransferred == 5) {
                        Log.d(TAG, "开始计数");
                        count = 0;
                        first = SystemClock.elapsedRealtime();
                        sendList.add("start".getBytes());
                        for (int i = 0; i < 10000; i++) {
                            sendList.add(test);
                        }
                        sendList.add("end".getBytes());
                        Log.d(TAG, "填入数据耗时：" + (SystemClock.elapsedRealtime() - first));
                    }
                    if (bytesTransferred == 3) {
                        double totalTime = (SystemClock.elapsedRealtime() - first) / 1000.0;
                        Log.d(TAG, "收到信息总次数： " + count + " 总耗时:" + totalTime + " 带宽" + ((10000 * 16) / totalTime) / 1024 + "MBps");
                    }
                    if (bytesTransferred > 0) {
//                        Log.d(TAG, "收到信息： " + new String(buff, 0, bytesTransferred));
//                        printLineToUI("收到信息： " + new String(buff, 0, bytesTransferred));
//                        Log.d(TAG, "收到信息： " + bytesTransferred + " count = " + count++);
                        if (count % 100 == 0) {
                            Log.d(TAG, "收到信息： " + bytesTransferred + " count = " + count);
                        }
                        count++;
                    }

                    synchronized (sendList) {
                        if (sendList.size() > 0) {
                            connection.bulkTransfer(endpointOut, sendList.get(0), sendList.get(0).length, USB_TIMEOUT_IN_MS);
                            sendList.remove(0);
                        }
                    }

//                    synchronized (sendBuffer) {
//                        if (sendBuffer.size() > 0) {
//                            final byte[] sendBuff = sendBuffer.get(0).toString().getBytes();
//                            connection.bulkTransfer(endpointOut, sendBuff, sendBuff.length, USB_TIMEOUT_IN_MS);
//                            sendBuffer.remove(0);
//                        }
//                    }
                }
            }

            connection.releaseInterface(usbInterface);
            connection.close();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        keepThreadAlive.set(false);
    }

    public void printLineToUI(final String line) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contentTextView.setText(contentTextView.getText() + "\n" + line);
            }
        });
    }
}
