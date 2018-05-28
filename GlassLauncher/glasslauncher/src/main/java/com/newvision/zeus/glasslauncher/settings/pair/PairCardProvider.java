package com.newvision.zeus.glasslauncher.settings.pair;

import android.content.Context;
import android.view.ViewGroup;


import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 17-6-29.
 */

public class PairCardProvider extends GlassCardProvider {
    @Override
    public String getCardProviderId() {
        return "pair-card";
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        return new PairCardView(context);
    }
}
