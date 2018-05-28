package com.newvision.zeus.glasslauncher.common.helper;

/**
 * Created by zhangsong on 4/29/16.
 */
public abstract class CountDownTimer {
    private android.os.CountDownTimer mTimer;

    public CountDownTimer(int time) {
        mTimer = new android.os.CountDownTimer(time, time) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                CountDownTimer.this.onFinish();
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
