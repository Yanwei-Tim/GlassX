package com.newvision.zeus.glasslauncher.settings.screenoff;

import android.content.Context;
import android.view.ViewGroup;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;


public class ScreenOffCardProvider extends GlassCardProvider {
    @Override
    public String getCardProviderId() {
        return "GlassScreenOff";
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        return new ScreenOffCardView(context);
    }
}
