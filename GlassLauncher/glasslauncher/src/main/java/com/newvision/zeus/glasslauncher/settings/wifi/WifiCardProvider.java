package com.newvision.zeus.glasslauncher.settings.wifi;

import android.content.Context;
import android.view.ViewGroup;

import com.newvision.zeus.glasslauncher.common.helper.GlassWifiInfoProvider;

import cn.ceyes.glasswidget.cardview.GlassCardProvider;
import cn.ceyes.glasswidget.cardview.GlassCardView;

/**
 * Created by zhangsong on 17-7-3.
 */

public class WifiCardProvider extends GlassCardProvider {
    private static final String TAG = "WifiCardProvider";

    private WifiCardView wifiCardView;

    @Override
    public String getCardProviderId() {
        return "wifi-card-provider";
    }

    @Override
    public GlassCardView onCreateView(Context context, ViewGroup parent) {
        wifiCardView = new WifiCardView(context);
        return wifiCardView;
    }

    @Override
    public boolean onGlassEvent(int eventCode, Object event) {
        switch (eventCode) {
            case 10000:
                registerReceivers();
                return true;
            case 10001:
                unregisterReceivers();
                return true;
        }
        return false;
    }

    private void registerReceivers() {
        GlassWifiInfoProvider.getInstance().registerConnectionStateObserver(connectionStateObserver);
    }

    private void unregisterReceivers() {
        GlassWifiInfoProvider.getInstance().unregisterSignalLevelChangeObserver(signalLevelChangeObserver);
        GlassWifiInfoProvider.getInstance().unregisterConnectionStateObserver(connectionStateObserver);
    }

    private GlassWifiInfoProvider.ConnectionStateObserver connectionStateObserver = new GlassWifiInfoProvider.ConnectionStateObserver() {
        @Override
        public void onChanged(int state) {
            switch (state) {
                case GlassWifiInfoProvider.GLASS_WIFI_STATE_CONNECTED:
                    wifiCardView.setWifiState(state);

                    GlassWifiInfoProvider.getInstance().registerSignalLevelChangeObserver(signalLevelChangeObserver);
                    break;
                case GlassWifiInfoProvider.GLASS_WIFI_STATE_DISCONNECTED:
                    wifiCardView.setWifiState(state);

                    GlassWifiInfoProvider.getInstance().unregisterSignalLevelChangeObserver(signalLevelChangeObserver);
                    break;
            }
        }
    };

    private GlassWifiInfoProvider.SignalLevelChangeObserver signalLevelChangeObserver = new GlassWifiInfoProvider.SignalLevelChangeObserver() {
        @Override
        public void onChanged(int signalLevel) {
            wifiCardView.setWifiSignalLevel(signalLevel);
        }
    };
}
