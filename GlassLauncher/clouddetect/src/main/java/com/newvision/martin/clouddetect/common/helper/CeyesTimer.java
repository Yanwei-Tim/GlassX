package com.newvision.martin.clouddetect.common.helper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhangsong on 16-5-25.
 */
public class CeyesTimer {
    private static final String TAG = "CeyesTimer";

    private Timer mTimer;
    private Handler mHandler;

    public CeyesTimer() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        onUpdate();
                        break;
                }
            }
        };
    }

    public void schedule(long delay, long period) {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handleTimeTick();
            }
        }, delay, period);
    }

    public void cancel() {
        if (mTimer == null)
            return;

        mTimer.cancel();
        mTimer = null;
    }

    protected boolean onUpdateAsync() {
        return false;
    }

    protected void onUpdate() {

    }

    private void handleTimeTick() {
        if (!onUpdateAsync())
            mHandler.obtainMessage(0).sendToTarget();
    }
}
