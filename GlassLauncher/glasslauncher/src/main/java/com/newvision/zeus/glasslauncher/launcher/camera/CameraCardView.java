package com.newvision.zeus.glasslauncher.launcher.camera;

import android.content.Context;
import android.view.LayoutInflater;

import com.newvision.zeus.glasslauncher.R;

import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 17-6-29.
 */

public class CameraCardView extends GlassCardView {

    public CameraCardView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.card_camera, this);
    }

    @Override
    public void onCardSelected() {
        super.onCardSelected();

        sendGlassEvent(21000, null);
    }
}
