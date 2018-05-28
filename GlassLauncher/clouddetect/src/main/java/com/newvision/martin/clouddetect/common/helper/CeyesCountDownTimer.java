package com.newvision.martin.clouddetect.common.helper;

import android.os.CountDownTimer;

/**
 * Created by zhangsong on 4/29/16.
 */
public abstract class CeyesCountDownTimer {
    private CountDownTimer mTimer;

    public CeyesCountDownTimer(int time) {
        mTimer = new CountDownTimer(time, time) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                CeyesCountDownTimer.this.onFinish();
            }
        };
    }

    public abstract void onFinish();

    public void start() {
        mTimer.cancel();
        mTimer.start();
    }

    public void cancel() {
        mTimer.cancel();
    }
}
