package com.newvision.zeus.glasslauncher.common.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 1.监听wifi连接性
 * 2.监听当前正在连接的wifi的信号强度
 * 3.获取周围wifi列表
 */
public class GlassWifiInfoProvider {
    private static final String TAG = "GlassWifiInfoProvider";

    public static final int GLASS_WIFI_STATE_DISCONNECTED = 0;
    public static final int GLASS_WIFI_STATE_DISCONNECTING = 1;
    public static final int GLASS_WIFI_STATE_CONNECTING = 2;
    public static final int GLASS_WIFI_STATE_CONNECTED = 3;

    private static int GLASS_WIFI_STATE = GLASS_WIFI_STATE_DISCONNECTED;

    /**
     * Observing the wifi connecting state change.
     * When you connect a wifi by ssid/password, you can
     * register this observer to watch more detail connecting state.
     */
    public interface ConnectingStateObserver {
        void onConnectStateChanged(int state);
    }

    /**
     * Observing the wifi connection state change.
     */
    public interface ConnectionStateObserver {
        void onChanged(int state);
    }

    public interface ScanResultsObserver {
        void onChanged(List<ScanResult> results);
    }

    /**
     * Observing the current connected wifi signal level change.
     * The signalLevel is from 0 to 3.
     */
    public interface SignalLevelChangeObserver {
        void onChanged(int signalLevel);
    }

    private static GlassWifiInfoProvider sharedInstance = new GlassWifiInfoProvider();
    private Object connectionObserverLock = new Object();
    private List<ConnectionStateObserver> mConnectionObservers;
    private Object signalLevelObserverLock = new Object();
    private List<SignalLevelChangeObserver> mSignalLevelObservers;
    private Object scanResultObserverLock = new Object();
    private List<ScanResultsObserver> mScanResultsObservers;
    private BroadcastReceiver mBroadcastReceiver;
    private Context mContext = null;
    private ConnectivityManager connectivityManager = null;
    private WifiManager wifiManager = null;

    // 扫描出的wifi列表
    private List<ScanResult> mWifiScanResults = null;

    // 当前连接的wifi info
    private WifiInfo curWifiInfo = null;

    public static final Comparator<ScanResult> mSignalLevelComparator = new Comparator<ScanResult>() {
        @Override
        public int compare(ScanResult lhs, ScanResult rhs) {
            if (lhs.level > rhs.level) {
                return -1;
            }
            return lhs.level >= rhs.level ? 0 : 1;
        }
    };

    public static GlassWifiInfoProvider getInstance() {
        return sharedInstance;
    }

    private GlassWifiInfoProvider() {
        mConnectionObservers = new ArrayList<ConnectionStateObserver>();
        mSignalLevelObservers = new ArrayList<SignalLevelChangeObserver>();
        mScanResultsObservers = new ArrayList<>();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    handleConnectionAction(context);
                } else if (action.equals(WifiManager.RSSI_CHANGED_ACTION)) {
                    handleSignalLevelAction();
                } else if ((WifiManager.SCAN_RESULTS_AVAILABLE_ACTION).equals(action)) {
                    handleScanResultsAction();
                    return;
                }
            }

            private void handleConnectionAction(Context context) {
                if (connectivityManager == null) {
                    connectivityManager = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                }
                NetworkInfo mNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mNetworkInfo.getState() == State.CONNECTED
                        && mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    GLASS_WIFI_STATE = GLASS_WIFI_STATE_CONNECTED;
                    curWifiInfo = getCurrWifiInfo();
                } else if (mNetworkInfo.getState() == State.CONNECTING
                        && mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    GLASS_WIFI_STATE = GLASS_WIFI_STATE_CONNECTING;
                } else if (mNetworkInfo.getState() == State.DISCONNECTED
                        && mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    GLASS_WIFI_STATE = GLASS_WIFI_STATE_DISCONNECTED;
                } else if (mNetworkInfo.getState() == State.DISCONNECTING
                        && mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    GLASS_WIFI_STATE = GLASS_WIFI_STATE_DISCONNECTING;
                }

                Log.w(TAG, "GLASS_WIFI_STATE --" + GLASS_WIFI_STATE);
                synchronized (connectionObserverLock) {
                    for (ConnectionStateObserver observer : mConnectionObservers) {
                        observer.onChanged(GLASS_WIFI_STATE);
                    }
                }
            }

            private void handleSignalLevelAction() {
                if (curWifiInfo == null) {
                    return;
                }
                int signalLevel = WifiManager.calculateSignalLevel(curWifiInfo.getRssi(), 4);

                synchronized (signalLevelObserverLock) {
                    for (SignalLevelChangeObserver observer : mSignalLevelObservers) {
                        observer.onChanged(signalLevel);
                    }
                }
            }

            private void handleScanResultsAction() {
                mWifiScanResults = wifiManager.getScanResults();
                Log.i(TAG, "scan finished, result size: " + mWifiScanResults.size());
                Collections.sort(mWifiScanResults, mSignalLevelComparator);

                synchronized (scanResultObserverLock) {
                    for (ScanResultsObserver observer : mScanResultsObservers) {
                        observer.onChanged(mWifiScanResults);
                    }
                }
            }
        };
    }

    public void init(Context context) {
        if (mContext != null) {
            Log.w(TAG, "GlassWifiInfoProvider has been inited!!!");
            return;
        }
        mContext = context;

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        registerWifiBroadcast();

        startScan();
    }

    public void destroy() {
        mContext.unregisterReceiver(mBroadcastReceiver);
    }

    public List<ScanResult> getWifiScanResults() {
        return mWifiScanResults;
    }

    public WifiInfo getCurrWifiInfo() {
        curWifiInfo = wifiManager.getConnectionInfo();
        return curWifiInfo;
    }

    public void registerConnectionStateObserver(ConnectionStateObserver observer) {
        synchronized (connectionObserverLock) {
            if (!mConnectionObservers.contains(observer)) {
                mConnectionObservers.add(observer);
                observer.onChanged(GLASS_WIFI_STATE);
            }
        }
    }

    public void unregisterConnectionStateObserver(ConnectionStateObserver observer) {
        synchronized (connectionObserverLock) {
            if (mConnectionObservers.contains(observer)) {
                mConnectionObservers.remove(observer);
            }
        }
    }

    public void registerSignalLevelChangeObserver(SignalLevelChangeObserver observer) {
        synchronized (signalLevelObserverLock) {
            if (!mSignalLevelObservers.contains(observer)) {
                mSignalLevelObservers.add(observer);

                if (curWifiInfo == null) {
                    return;
                }

                int signalLevel = WifiManager.calculateSignalLevel(curWifiInfo.getRssi(), 4);
                observer.onChanged(signalLevel);
            }
        }
    }

    public void unregisterSignalLevelChangeObserver(SignalLevelChangeObserver observer) {
        synchronized (signalLevelObserverLock) {
            if (mSignalLevelObservers.contains(observer)) {
                mSignalLevelObservers.remove(observer);
            }
        }
    }

    public void registerScanResultsObserver(ScanResultsObserver observer) {
        synchronized (scanResultObserverLock) {
            if (mScanResultsObservers.contains(observer))
                return;

            mScanResultsObservers.add(observer);
        }
    }

    public void unregisterScanResultsObserver(ScanResultsObserver observer) {
        synchronized (scanResultObserverLock) {
            if (mScanResultsObservers.contains(observer))
                mScanResultsObservers.remove(observer);
        }
    }

    public boolean isWifiConnected() {
        return GLASS_WIFI_STATE == GLASS_WIFI_STATE_CONNECTED;
    }

    private void startScan() {
        if (!wifiManager.isWifiEnabled()) {
            Log.e(TAG, "startScan: wifi is not enabled");
            return;
        }

        // scan is async...
        wifiManager.startScan();
    }

    private void registerWifiBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(mBroadcastReceiver, filter);
    }
}
