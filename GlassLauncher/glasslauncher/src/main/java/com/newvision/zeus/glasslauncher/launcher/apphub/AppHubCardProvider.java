package com.newvision.zeus.glasslauncher.launcher.apphub;

import android.content.Context;
import android.view.ViewGroup;

import com.newvision.zeus.glasslauncher.common.pm.AppInfo;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 17-7-4.
 */

public class AppHubCardProvider extends GlassCardProvider {
    private static final String TAG = "AppHubCardProvider";

    private String cardProviderId = null;
    private AppHubCardView cardView;
    private AppInfo appInfo;

    @Override
    public String getCardProviderId() {
        return cardProviderId;
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        cardView = new AppHubCardView(context);
        cardView.setAppInfo(appInfo);
        return cardView;
    }

    public AppHubCardProvider init(String id, AppInfo info) {
        cardProviderId = id;
        appInfo = info;
        return this;
    }
}
