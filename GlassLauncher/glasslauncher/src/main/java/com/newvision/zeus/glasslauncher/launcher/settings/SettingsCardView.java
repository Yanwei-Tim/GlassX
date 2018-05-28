package com.newvision.zeus.glasslauncher.launcher.settings;

import android.content.Context;
import android.view.LayoutInflater;

import com.newvision.zeus.glasslauncher.R;

import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 17-6-29.
 */

public class SettingsCardView extends GlassCardView {

    public SettingsCardView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.card_settings, this);
    }

    @Override
    public void onCardSelected() {
        super.onCardSelected();

        sendGlassEvent(20000, null);
    }
}
