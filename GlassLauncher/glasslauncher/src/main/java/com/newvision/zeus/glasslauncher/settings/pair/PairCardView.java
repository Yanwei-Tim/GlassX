package com.newvision.zeus.glasslauncher.settings.pair;

import android.content.Context;
import android.view.LayoutInflater;

import com.newvision.zeus.glasslauncher.R;

import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 17-6-29.
 */

public class PairCardView extends GlassCardView {

    public PairCardView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.card_pair, this);
    }

    @Override
    public void onCardSelected() {
        super.onCardSelected();

        sendGlassEvent(22000, null);
    }

    @Override
    public void onCardFinished() {
        super.onCardFinished();
        // Send a activity finish msg.
        sendGlassEvent(50000, null);
    }
}
