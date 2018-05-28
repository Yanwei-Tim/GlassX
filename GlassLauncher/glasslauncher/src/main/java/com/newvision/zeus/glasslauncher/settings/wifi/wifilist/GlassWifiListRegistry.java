package com.newvision.zeus.glasslauncher.settings.wifi.wifilist;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;

import com.newvision.zeus.glasslauncher.settings.wifi.GlassWifiConnector;

import java.util.ArrayList;
import java.util.List;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardManager;

public class GlassWifiListRegistry {
    private static final String TAG = GlassWifiListRegistry.class.getSimpleName();

    private GlassCardManager mCardManager;
    private List<GlassCardProvider> mCardProviders;
    private GlassWifiConnector wifiConnector;

    public GlassWifiListRegistry(Context context) {
        mCardProviders = new ArrayList<GlassCardProvider>();
        wifiConnector = GlassWifiConnector.get(context);
    }

    public void init(GlassCardManager cardManager) {
        mCardManager = cardManager;

        mCardProviders.add(new GlassWifiAddProvider());
        List<ScanResult> results = wifiConnector.getExistsInConfigurationList();
        for (int i = 0; i < results.size(); i++) {
            ScanResult result = results.get(i);
            if (result.SSID != null || !result.SSID.equals("")) {
                GlassWifiItemProvider itemProvider = new GlassWifiItemProvider();
                itemProvider.setCardProviderId("WifiListItem_" + i);
                Log.i(TAG, "onWifiScanResultChanged, result:" + result.toString());
                itemProvider.initData(result);
                mCardProviders.add(itemProvider);
            }
        }

        for (GlassCardProvider cardProvider : mCardProviders) {
            mCardManager.activateGlassCard(cardProvider);
        }
    }

    public void deactive(ScanResult result) {
        for (int i = 1; i < mCardProviders.size(); i++) {
            GlassWifiItemProvider provider = (GlassWifiItemProvider) mCardProviders.get(i);
            if (result.BSSID.equals(provider.getData().BSSID)) {
                mCardManager.deactivateGlassCard(provider);
                return;
            }
        }
    }
}
