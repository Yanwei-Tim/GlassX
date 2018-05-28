package com.newvision.zeus.glassmanager.entity;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by yanjiatian on 2017/9/8.
 */

public interface IWifiListObserver {
    void onWifiListChanged(List<ScanResult> results);

    void onWifiStateChanged(int wifiState);
}
