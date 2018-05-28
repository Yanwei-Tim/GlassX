package com.newvision.zeus.glasslauncher.settings.brightness;

import android.content.Context;
import android.view.ViewGroup;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 3/23/15.
 */
public class ScreenBrightnessCardProvider extends GlassCardProvider {
    @Override
    public String getCardProviderId() {
        return ScreenBrightnessCardProvider.this.getClass().getSimpleName();
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        return new ScreenBrightnessCardView(context);
    }
}
