package com.newvision.zeus.glasslauncher.launcher.settings;

import android.content.Context;
import android.view.ViewGroup;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 17-6-29.
 */

public class SettingsCardProvider extends GlassCardProvider {
    @Override
    public String getCardProviderId() {
        return "settings-card";
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        return new SettingsCardView(context);
    }
}
