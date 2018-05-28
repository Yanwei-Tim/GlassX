/*
 * Copyright (C) 2014 Ceyes Inc. All rights reserved.
 */

package com.newvision.zeus.glasslauncher.settings.bluetooth;

import android.content.Context;
import android.view.ViewGroup;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

public class BluetoothCardProvider extends GlassCardProvider {

    private static final String TAG = "GlassBTSettingCardProvider";

    @Override
    public String getCardProviderId() {
        return "BTSettingCard";
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        return new BluetoothCardView(context);
    }
}
