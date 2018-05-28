package com.newvision.zeus.glasslauncher.settings.volume;

import android.content.Context;
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
public class GlassVolumeSlider {

    public interface IGlassVolumeSliderCallback {
        void onVolumeChanged();

        void onVolumeSliderFinished();
    }

    private static final String TAG = "GlassVolumeSlider";

    private Context mContext;

    private ImageView mVolumeImage;
    private ProgressBar mVolumeProgress;

    private GlassBaseDialog mSingleView;

    private IGlassVolumeSliderCallback mVolumeSliderCallback;

    private int mCurrentVolume = 0;
    private int mMaxVolume = 0;

    private GlassVolumeHelper mVolumeHelper = null;

    public GlassVolumeSlider(Context context) {
        mContext = context;
        mSingleView = new GlassBaseDialog(context);

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.card_volume_setting, null);
        mSingleView.setContentView(contentView);
        mVolumeImage = (ImageView) contentView.findViewById(R.id.img_volume);
        mVolumeProgress = (ProgressBar) contentView.findViewById(R.id.progress_volume);

        mSingleView.setOnGestureListener(new GlassGestureListener() {
            @Override
            public void onFlingLeft(View v) {
                super.onFlingLeft(v);
                increaseVolume();
            }

            @Override
            public void onFlingRight(View v) {
                super.onFlingRight(v);
                decreaseVolume();
            }
        });
        mSingleView.setOnKeyEventListener(new GlassKeyEventListener() {
            @Override
            public void onPreviousKey() {
                super.onPreviousKey();
                decreaseVolume();
            }

            @Override
            public void onNextKey() {
                super.onNextKey();
                increaseVolume();
            }

            @Override
            public void onBackKey() {
                super.onBackKey();
                finishSlider();
            }
        });
    }

    public void setVolumeHelper(GlassVolumeHelper volumeHelper) {
        mVolumeHelper = volumeHelper;
        mMaxVolume = mVolumeHelper.getMaxVolume();
        mCurrentVolume = mVolumeHelper.getCurrentVolume();
    }

    public void setOnVolumeSliderCallback(IGlassVolumeSliderCallback callback) {
        mVolumeSliderCallback = callback;
    }


    public void show() {
        updateUI();
        mSingleView.show();
    }

    public void hide() {
        mSingleView.hide();
    }

    public void dismiss() {
        mSingleView.dismiss();
    }

    public boolean isShowing() {
        return mSingleView.isShowing();
    }

    private void increaseVolume() {
        mCurrentVolume = mCurrentVolume + 1;
        mCurrentVolume = mCurrentVolume > mMaxVolume ? mMaxVolume : mCurrentVolume;
        Log.i(TAG, "Act Fling right, mCurrentVolume:" + mCurrentVolume + " , " + (int) mCurrentVolume);
        if (mVolumeHelper.setVolume(mCurrentVolume)) {
            updateUI();
            if (mVolumeSliderCallback != null) {
                mVolumeSliderCallback.onVolumeChanged();
            }
        }
    }

    private void decreaseVolume() {
        mCurrentVolume = mCurrentVolume - 1;
        mCurrentVolume = mCurrentVolume < 0 ? 0 : mCurrentVolume;
        Log.i(TAG, "Act Fling left, mCurrentVolume:" + mCurrentVolume + " , " + mCurrentVolume);
        if (mVolumeHelper.setVolume(mCurrentVolume)) {
            updateUI();
            if (mVolumeSliderCallback != null) {
                mVolumeSliderCallback.onVolumeChanged();
            }
        }
    }

    public void updateUI() {
        mVolumeProgress.setMax(mMaxVolume);
        mVolumeProgress.setProgress(mCurrentVolume);
        setVolumeImage();
    }

    private void setVolumeImage() {
        if (mCurrentVolume == 0) {
            mVolumeImage.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.ic_volume_0_large));
        } else if (mCurrentVolume > 0 && mCurrentVolume < mMaxVolume - 3) {
            mVolumeImage.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.ic_volume_1_large));
        } else {
            mVolumeImage.setImageDrawable(mContext.getResources()
                    .getDrawable(R.drawable.ic_volume_2_large));
        }
    }

    private void finishSlider() {
        if (mVolumeSliderCallback != null) {
            mVolumeSliderCallback.onVolumeSliderFinished();
        }
        GlassVolumeSlider.this.dismiss();
    }
}
