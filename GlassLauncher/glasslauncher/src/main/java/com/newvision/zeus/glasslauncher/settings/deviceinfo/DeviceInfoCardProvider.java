package com.newvision.zeus.glasslauncher.settings.deviceinfo;

import android.content.Context;
import android.view.ViewGroup;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 1/5/15.
 */
public class DeviceInfoCardProvider extends GlassCardProvider {
    @Override
    public String getCardProviderId() {
        return "GlassDeviceInfo";
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        return new DeviceInfoCardView(context);
    }
}
