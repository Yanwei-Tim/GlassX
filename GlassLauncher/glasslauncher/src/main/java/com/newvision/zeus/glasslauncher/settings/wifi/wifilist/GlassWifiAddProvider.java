package com.newvision.zeus.glasslauncher.settings.wifi.wifilist;

import android.content.Context;
import android.view.ViewGroup;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

public class GlassWifiAddProvider extends GlassCardProvider {

    @Override
    public String getCardProviderId() {
        return "GlassWifiAddId";
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        return new GlassWifiAddView(context);
    }
}
