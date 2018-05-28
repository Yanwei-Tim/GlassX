/*
 * Copyright (C) 2014 Ceyes Inc. All rights reserved.
 */

package com.newvision.zeus.glasslauncher.common.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GlassBatteryInfoProvider {
    public interface GlassBatteryStateObserver {
        void onBatteryStateChanged(int state, int level);
    }

    private static final String TAG = GlassBatteryInfoProvider.class.getSimpleName();

    private static GlassBatteryInfoProvider sharedInstance = new GlassBatteryInfoProvider();
    private List<GlassBatteryStateObserver> mObservers;
    private BroadcastReceiver mBroadcastReceiver;
    private Context mContext = null;

    private int mBatteryStatus = BatteryManager.BATTERY_STATUS_UNKNOWN;
    private int mBatteryLevel = 0;

    public static GlassBatteryInfoProvider getInstance() {
        return sharedInstance;
    }

    private GlassBatteryInfoProvider() {
        mObservers = new ArrayList<GlassBatteryStateObserver>();

        mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                if (!intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                    return;
                }

                mBatteryStatus = intent.getIntExtra("status", 0);
                mBatteryLevel = intent.getIntExtra("level", 0);

                if (mBatteryStatus == BatteryManager.BATTERY_STATUS_UNKNOWN) {
                    return;
                }

                for (GlassBatteryStateObserver observer : mObservers) {

                    observer.onBatteryStateChanged(mBatteryStatus, mBatteryLevel);
                }
            }
        };
    }

    public void init(Context context) {
        mContext = context;
        mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void destroy() {
        mContext.unregisterReceiver(mBroadcastReceiver);
    }

    public void registerObserver(GlassBatteryStateObserver observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
            observer.onBatteryStateChanged(mBatteryStatus, mBatteryLevel);
        }

        if (mContext == null) {
            Log.d(TAG, "BroadcastReceiver is not registered into system...");
        }
    }

    public void unregisterObserver(GlassBatteryStateObserver observer) {
        mObservers.remove(observer);
    }

    public int getBatteryStatus() {
        return mBatteryStatus;
    }
}
