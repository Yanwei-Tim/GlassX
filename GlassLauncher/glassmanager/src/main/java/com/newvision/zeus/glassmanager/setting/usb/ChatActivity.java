package com.newvision.zeus.glassmanager.setting.usb;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.newvision.zeus.glassmanager.R;

/**
 * Created by yanjiatian on 2017/8/2.
 */

public class ChatActivity extends Activity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    private AccessoryCommunicatorTest communicator;

    TextView contentTextView;
    EditText input;
    Button btn_send;
    byte[] test = new byte[1024 * 16];
    int count = 0;
    long first = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "devices onCreate() ...");
        setContentView(R.layout.activity_chat);
        contentTextView = (TextView) findViewById(R.id.content_text);
        for (int i = 0; i < test.length; i++) {
            test[i] = (byte) (i % 128);
        }
        input = (EditText) findViewById(R.id.input_edittext);
        btn_send = (Button) findViewById(R.id.btn_send);
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


        communicator = new AccessoryCommunicatorTest(this) {

            @Override
            public void onReceive(byte[] payload, int length) {
//                printLineToUI("收到消息： " + new String(payload, 0, length));
                if (length == 5) {
                    count = 0;
                    first = SystemClock.elapsedRealtime();
                    Log.d(TAG, "length == 3 , first = " + first);
                }
                if (length > 0) {
                    count++;
                }
                if (count % 100 == 0) {
                    Log.d(TAG, "count = " + count + " length = " + length);
                }
                if (length == 3) {
                    double totalTime = (SystemClock.elapsedRealtime() - first) / 1000.0;
                    Log.d(TAG, "收到信息总次数：" + count + "接收总耗时:" + +totalTime + " 带宽" + ((10000 * 16) / totalTime) / 1024 + "MBps");
                }
            }

            @Override
            public void onError(String msg) {
                printLineToUI("通知：" + msg);
            }

            @Override
            public void onConnected() {
                printLineToUI("已连接");
            }

            @Override
            public void onDisconnected() {
                printLineToUI("已断开");
            }
        };
    }

    public void sendString(String string) {
        printLineToUI("发出消息： " + string);
        long first = SystemClock.elapsedRealtime();
        string = "start";
        communicator.send(string.getBytes());
        for (int i = 0; i < 10000; i++) {
            communicator.send(test);
        }
        communicator.send("end".getBytes());
        Log.d(TAG, "耗时" + (SystemClock.elapsedRealtime() - first));
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
