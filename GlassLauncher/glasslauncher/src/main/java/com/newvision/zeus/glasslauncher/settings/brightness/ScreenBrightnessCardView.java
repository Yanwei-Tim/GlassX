package com.newvision.zeus.glasslauncher.settings.brightness;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;

import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 3/23/15.
 */
public class ScreenBrightnessCardView extends GlassCardView {
    private static final String TAG = ScreenBrightnessCardView.class.getSimpleName();

    private TextView mBrightness;
    private GlassScreenBrightnessSlider mBrightnessSlider;
    private int brightness = -1;

    public ScreenBrightnessCardView(Context context) {
        super(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.card_screen_brightness, this);
        mBrightness = (TextView) contentView.findViewById(R.id.txt_brightness);

        try {
            brightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            changeView(brightness);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCardSelected() {
        super.onCardSelected();
        mBrightnessSlider = new GlassScreenBrightnessSlider(mContext);
        mBrightnessSlider.setBrightnessValue(255, brightness);
        Log.i(TAG, "before decrease current brightness : " + brightness);
        mBrightnessSlider.setBrightnessSliderCallback(new GlassScreenBrightnessSlider.IGlassVolumeSliderCallback() {
            @Override
            public void onBrightnessChanged(int brightness) {
                changeView(brightness);
            }

            @Override
            public void onBrightnessSliderFinished() {

            }
        });
        mBrightnessSlider.show();
    }

    @Override
    public void onCardFinished() {
        super.onCardFinished();

        sendGlassEvent(50000, null);
    }

    private void changeView(int brightness) {
        this.brightness = brightness;
        String brightStr = "";
        if (brightness < 96) {
            brightStr = mContext.getResources().getString(R.string.lable_brightness_low);
        } else if (brightness >= 96 && brightness < 192) {
            brightStr = mContext.getResources().getString(R.string.lable_brightness_middle);
        } else {
            brightStr = mContext.getResources().getString(R.string.lable_brightness_high);
        }
        mBrightness.setText(brightStr);
    }
}
