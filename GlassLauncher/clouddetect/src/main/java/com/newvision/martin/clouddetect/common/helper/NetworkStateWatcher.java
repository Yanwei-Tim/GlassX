package com.newvision.martin.clouddetect.common.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangsong on 16-8-16.
 */
public class NetworkStateWatcher {
    public interface OnNetworkStateChangeListener {
        void onConnect();

        void onDisconnect();

        class Stub implements OnNetworkStateChangeListener {
            @Override
            public void onConnect() {
            }

            @Override
            public void onDisconnect() {
            }
        }
    }

    private static final String TAG = "NetworkStateWatcher";

    private static NetworkStateWatcher instance = new NetworkStateWatcher();

    private Context mContext;
    private List<OnNetworkStateChangeListener> listeners;

    private boolean isNetworkConnected = false;

    public static NetworkStateWatcher getInstance() {
        return instance;
    }

    public void init(Context cxt) {
        if (mContext != null) return;

        mContext = cxt;

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mReceiver, filter);
    }

    public void deInit() {
        mContext.unregisterReceiver(mReceiver);
    }

    public boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    public void addListener(OnNetworkStateChangeListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeListener(OnNetworkStateChangeListener listener) {
        if (listeners.contains(listener))
            listeners.remove(listener);
    }

    private NetworkStateWatcher() {
        listeners = new ArrayList<>();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo.State wifiState = wifiInfo == null ? NetworkInfo.State.DISCONNECTED : wifiInfo.getState();
                NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo.State mobileState = mobileInfo == null ? NetworkInfo.State.DISCONNECTED : mobileInfo.getState();

                if (wifiState != null && mobileState != null && NetworkInfo.State.CONNECTED != wifiState
                        && NetworkInfo.State.CONNECTED != mobileState) {
                    // 手机没有任何的网络
                    isNetworkConnected = false;

                    for (OnNetworkStateChangeListener listener : listeners) {
                        listener.onDisconnect();
                    }

                    return;
                }

                if ((wifiState != null && NetworkInfo.State.CONNECTED == wifiState) ||
                        (mobileState != null && NetworkInfo.State.CONNECTED == mobileState)) {
                    // 无线网络连接成功
                    isNetworkConnected = true;

                    for (OnNetworkStateChangeListener listener : listeners) {
                        listener.onConnect();
                    }

                    return;
                }
            }
        }
    };
}
