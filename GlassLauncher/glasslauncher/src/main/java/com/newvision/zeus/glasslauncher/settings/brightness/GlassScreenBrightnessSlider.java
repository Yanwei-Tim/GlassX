package com.newvision.zeus.glasslauncher.settings.brightness;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.newvision.zeus.glasslauncher.R;

import cn.ceyes.glasswidget.gestures.GlassGestureListener;
import cn.ceyes.glasswidget.keyevents.GlassKeyEventListener;
import cn.ceyes.glasswidget.singleview.GlassBaseDialog;

/**
 * Created by zhangsong on 1/5/15.
 */
public class GlassScreenBrightnessSlider {

    public interface IGlassVolumeSliderCallback {
        void onBrightnessChanged(int brightness);

        void onBrightnessSliderFinished();
    }

    private static final String TAG = "GlassVolumeSlider";

    private static final int DEGREE_SCALE = 32;

    private Context mContext;

    private ImageView mBrightnessImage;
    private ProgressBar mBrightnessProgress;

    private GlassBaseDialog mSingleView;

    private IGlassVolumeSliderCallback mVolumeSliderCallback;

    private int mCurrentBrightness = 0;
    private int mMaxBrightness = 0;

    public GlassScreenBrightnessSlider(Context context) {
        mContext = context;
        mSingleView = new GlassBaseDialog(context);

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.card_volume_setting, null);
        mSingleView.setContentView(contentView);
        mBrightnessImage = (ImageView) contentView.findViewById(R.id.img_volume);
        mBrightnessImage.setImageResource(R.drawable.screen_brightness);
        mBrightnessProgress = (ProgressBar) contentView.findViewById(R.id.progress_volume);

        mSingleView.setOnGestureListener(new GlassGestureListener() {
            @Override
            public void onFlingLeft(View v) {
                super.onFlingLeft(v);
                increaseBrightness();
            }

            @Override
            public void onFlingRight(View v) {
                super.onFlingRight(v);
                decreaseBrightness();
            }
        });
        mSingleView.setOnKeyEventListener(new GlassKeyEventListener() {
            @Override
            public void onPreviousKey() {
                super.onPreviousKey();
                increaseBrightness();
            }

            @Override
            public void onNextKey() {
                super.onNextKey();
                decreaseBrightness();
            }

            @Override
            public void onBackKey() {
                super.onBackKey();
                finishSlider();
            }
        });
    }

    public void setBrightnessSliderCallback(IGlassVolumeSliderCallback callback) {
        mVolumeSliderCallback = callback;
    }

    public void setBrightnessValue(int maxBrightness, int currentBrightness) {
        mMaxBrightness = maxBrightness;
        mCurrentBrightness = getCurrentBrightness(currentBrightness);

        mBrightnessProgress.setMax(maxBrightness);
        mBrightnessProgress.setProgress(mCurrentBrightness);

        setBrightnessImage();
    }

    public void show() {
        mSingleView.show();
    }

    public void dismiss() {
        mSingleView.dismiss();
    }

    public boolean isShowing() {
        return mSingleView.isShowing();
    }

    private void setBrightnessImage() {
        int level = (mCurrentBrightness + 1) / DEGREE_SCALE;
        mBrightnessImage.getDrawable().setLevel(level);
    }

    private void increaseBrightness() {
        mCurrentBrightness = mCurrentBrightness + DEGREE_SCALE;
        mCurrentBrightness = mCurrentBrightness > mMaxBrightness ? mMaxBrightness : mCurrentBrightness;
        Log.i(TAG, "Act Fling right, mCurrentBrightness:" + mCurrentBrightness + " , " + mCurrentBrightness);
        changeBrightness();
    }

    private void decreaseBrightness() {
        mCurrentBrightness = mCurrentBrightness - DEGREE_SCALE;
        mCurrentBrightness = mCurrentBrightness < DEGREE_SCALE - 1 ? DEGREE_SCALE - 1 : mCurrentBrightness;
        Log.i(TAG, "Act Fling left, mCurrentBrightness:" + mCurrentBrightness + " , " + mCurrentBrightness);
        changeBrightness();
    }

    private void changeBrightness() {
        mBrightnessProgress.setProgress(mCurrentBrightness);
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, mCurrentBrightness);
        setBrightnessImage();
        if (mVolumeSliderCallback != null) {
            mVolumeSliderCallback.onBrightnessChanged(mBrightnessProgress.getProgress());
        }
    }

    private void finishSlider() {
        if (mVolumeSliderCallback != null) {
            mVolumeSliderCallback.onBrightnessSliderFinished();
        }
        GlassScreenBrightnessSlider.this.dismiss();
    }

    private int getCurrentBrightness(int brightness) {
        if ((brightness + 1) % DEGREE_SCALE == 0) {
            return brightness;
        } else {
            int remainder = (brightness + 1) % DEGREE_SCALE;
            if (remainder < DEGREE_SCALE / 2) {
                return ((brightness + 1) / DEGREE_SCALE) * DEGREE_SCALE - 1;
            } else {
                return (((brightness + 1) / DEGREE_SCALE) + 1) * DEGREE_SCALE - 1;
            }
        }
    }
}
