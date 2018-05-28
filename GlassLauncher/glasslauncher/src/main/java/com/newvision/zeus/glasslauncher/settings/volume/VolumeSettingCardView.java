/**
 * ****************************************************************************
 * Copyright (C) 2014 Ceyes Inc. All rights reserved.
 * *****************************************************************************
 */

package com.newvision.zeus.glasslauncher.settings.volume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;

import cn.ceyes.glasswidget.cardview.GlassCardView;

public class VolumeSettingCardView extends GlassCardView {

    private static final String TAG = VolumeSettingCardView.class.getSimpleName();

    private TextView mVolumeText;
    private ImageView mVolumeImage;

    private int mMaxVolume = 0;
    private int mCurrentVolume = 0;

    private GlassVolumeHelper mVolumeHelper = null;

    private GlassVolumeSlider mVolumeSlider = null;

    public VolumeSettingCardView(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.card_volume, this);
        mVolumeImage = (ImageView) view.findViewById(R.id.img_volume_main);
        mVolumeText = (TextView) view.findViewById(R.id.txt_volume);

        mVolumeSlider = new GlassVolumeSlider(mContext);
        initVolumeHelper();
        setVolumeInfo();
    }

    @Override
    public void onCardSelected() {
        if (mVolumeSlider != null) {
            mVolumeSlider.setOnVolumeSliderCallback(new GlassVolumeSlider.IGlassVolumeSliderCallback() {
                @Override
                public void onVolumeChanged() {
                    mCurrentVolume = mVolumeHelper.getCurrentVolume();
                    setVolumeInfo();
                }

                @Override
                public void onVolumeSliderFinished() {

                }
            });
            mVolumeSlider.show();
        }
    }

    @Override
    public void onCardVisible() {
        super.onCardVisible();
        mContext.registerReceiver(mHeadsetPlugReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
    }

    @Override
    public void onCardInvisible() {
        if (mVolumeSlider != null) {
            mVolumeSlider.dismiss();
        }
        mContext.unregisterReceiver(mHeadsetPlugReceiver);
    }

    @Override
    public void onCardFinished() {
        super.onCardFinished();

        sendGlassEvent(50000, null);
    }

    private void setVolumeInfo() {
        mVolumeText.setText(mVolumeHelper.getPercentVolume());
        if (mCurrentVolume == 0) {
            mVolumeImage.setImageDrawable(this.getContext().getResources()
                    .getDrawable(R.drawable.ic_volume_0_large));
        } else if (mCurrentVolume > 0 && mCurrentVolume < mMaxVolume - 3) {
            mVolumeImage.setImageDrawable(this.getContext().getResources()
                    .getDrawable(R.drawable.ic_volume_1_large));
        } else {
            mVolumeImage.setImageDrawable(this.getContext().getResources()
                    .getDrawable(R.drawable.ic_volume_2_large));
        }
    }

    private BroadcastReceiver mHeadsetPlugReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                mCurrentVolume = mVolumeHelper.getCurrentVolume();
                setVolumeInfo();
                if (mVolumeSlider != null && mVolumeSlider.isShowing()) {
                    mVolumeSlider.updateUI();
                }
            }
        }
    };

    private void initVolumeHelper() {
        mVolumeHelper = new GlassVolumeHelper(mContext);
        if (mVolumeSlider != null) {
            mVolumeSlider.setVolumeHelper(mVolumeHelper);
        }

        mMaxVolume = mVolumeHelper.getMaxVolume();
        mCurrentVolume = mVolumeHelper.getCurrentVolume();
    }
}
