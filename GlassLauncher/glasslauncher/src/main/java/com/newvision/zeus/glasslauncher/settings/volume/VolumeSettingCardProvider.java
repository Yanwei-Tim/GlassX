package com.newvision.zeus.glasslauncher.settings.volume;

import android.content.Context;
import android.view.ViewGroup;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 1/5/15.
 */
public class VolumeSettingCardProvider extends GlassCardProvider {
    @Override
    public String getCardProviderId() {
        return "GlassVolumeSetting";
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        return new VolumeSettingCardView(context);
    }
}
