package com.newvision.zeus.glasslauncher.settings.wifi.wifilist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;

import cn.ceyes.glasswidget.cardview.GlassCardView;

public class GlassWifiDetailView extends GlassCardView {

    protected ImageView wifiStateImg;
    protected TextView ssidText, tipsText;

    public GlassWifiDetailView(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.card_wifi_detail, this);
        wifiStateImg = (ImageView) view.findViewById(R.id.wifistate);
        ssidText = (TextView) view.findViewById(R.id.ssid);
        tipsText = (TextView) view.findViewById(R.id.tips);
    }

    @Override
    public void onCardFinished() {
        super.onCardFinished();
        // Send a finish msg.
        sendGlassEvent(50000, null);
    }
}
