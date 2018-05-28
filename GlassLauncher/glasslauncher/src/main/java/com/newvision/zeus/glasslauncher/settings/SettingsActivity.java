package com.newvision.zeus.glasslauncher.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.newvision.zeus.glasslauncher.launcher.LauncherCardManager;
import com.newvision.zeus.glasslauncher.settings.bluetooth.BluetoothCardProvider;
import com.newvision.zeus.glasslauncher.settings.deviceinfo.DeviceInfoCardProvider;
import com.newvision.zeus.glasslauncher.settings.brightness.ScreenBrightnessCardProvider;
import com.newvision.zeus.glasslauncher.settings.screenoff.ScreenOffCardProvider;
import com.newvision.zeus.glasslauncher.settings.volume.VolumeSettingCardProvider;
import com.newvision.zeus.glasslauncher.settings.wifi.WifiCardProvider;
import com.newvision.zeus.glasslauncher.settings.wifi.wifilist.GlassWifiListActivity;

import cn.ceyes.glasswidget.cardview.GlassCardListView;
import cn.ceyes.glasswidget.cardview.IGlassEventListener;

/**
 * Created by zhangsong on 17-6-29.
 */

public class SettingsActivity extends Activity {
    private static final String TAG = "LauncherActivity";

    GlassCardListView glassCardListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LauncherCardManager manager = new LauncherCardManager(this, new IGlassEventListener() {
            @Override
            public boolean onGlassEvent(int eventCode, Object event) {
                Log.i(TAG, "onGlassEvent: code: " + eventCode + ", event: " + event);
                switch (eventCode) {
                    case 20000:
                        startActivity(new Intent(SettingsActivity.this, GlassWifiListActivity.class));
                        break;
                    case 22000:
                        break;
                    case 50000:
                        finish();
                        break;
                }
                return false;
            }
        });

        manager.activateGlassCard(new BluetoothCardProvider());
        manager.activateGlassCard(new WifiCardProvider());
        manager.activateGlassCard(new DeviceInfoCardProvider());
        manager.activateGlassCard(new VolumeSettingCardProvider());
        manager.activateGlassCard(new ScreenOffCardProvider());
        manager.activateGlassCard(new ScreenBrightnessCardProvider());
//        manager.activateGlassCard(new PairCardProvider());

        glassCardListView = new GlassCardListView(this);
        glassCardListView.init(manager);
        setContentView(glassCardListView);
    }
}
