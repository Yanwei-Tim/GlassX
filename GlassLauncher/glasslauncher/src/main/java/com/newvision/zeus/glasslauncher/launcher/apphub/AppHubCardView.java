package com.newvision.zeus.glasslauncher.launcher.apphub;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;
import com.newvision.zeus.glasslauncher.common.pm.AppInfo;

import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 17-7-4.
 */

public class AppHubCardView extends GlassCardView {
    private static final String TAG = "AppHubCardView";

    private TextView appNameText;

    private AppInfo appInfo;

    public AppHubCardView(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.card_apphub, this);
        appNameText = (TextView) findViewById(R.id.txt_app_name);
    }

    @Override
    public void onCardSelected() {
        super.onCardSelected();
        // Send a msg to start the target app.
        sendGlassEvent(30000, appInfo);
    }

    public void setAppInfo(AppInfo info) {
        this.appInfo = info;

        appNameText.setText(appInfo.getAppLabel());
    }
}
