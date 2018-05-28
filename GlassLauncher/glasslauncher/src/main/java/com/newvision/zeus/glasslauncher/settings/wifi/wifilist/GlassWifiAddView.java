package com.newvision.zeus.glasslauncher.settings.wifi.wifilist;

import android.content.Context;
import android.view.View;

import com.newvision.zeus.glasslauncher.R;

public class GlassWifiAddView extends GlassWifiDetailView {

    public GlassWifiAddView(Context context) {
        super(context);

        tipsText.setVisibility(View.VISIBLE);
        wifiStateImg.setImageResource(R.drawable.ic_wifi4_add_big);
        ssidText.setText(R.string.wifi_tips_addnetwork);
        tipsText.setText(R.string.wifi_tips_use_glassmanager);
    }

    @Override
    public void onCardSelected() {
        super.onCardSelected();
        sendGlassEvent(20000, null);
    }
}
