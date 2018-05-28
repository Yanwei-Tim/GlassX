package com.newvision.zeus.glassmanager.setting.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.newvision.zeus.glassmanager.GlassManagerApp;
import com.newvision.zeus.glassmanager.R;
import com.newvision.zeus.glassmanager.entity.IWifiListObserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by yanjiatian on 2017/7/6.
 */

public class WifiHelper {
    private static final String TAG = WifiHelper.class.getSimpleName();
    private Context mContext;
    private WifiManager mWifiManager = null;
    private List<IWifiListObserver> mWifiListObservers;
    private List<ScanResult> mScanResults = null;
    private List<WifiConfiguration> mWifiConfigurations = null;

    private static int mTimerStatus = -1;
    private Timer mWifiScanTimer = null;
    private TimerTask mWifiScanTimerTask = null;

    private static final int TIMER_STATE_STARTED = 0;
    private static final int TIMER_STATE_CANCELED = 1;
    private static WifiHelper mInstance;
    public static final int WIFI_CIPHER_NO_PASS = 1;
    public static final int WIFI_CIPHER_WEP = 2;
    public static final int WIFI_CIPHER_WPA = 3;

    private final String PSK = "PSK";
    private final String WEP = "WEP";
    private final String EAP = "EAP";
    private final String OPEN = "Open";

    public static WifiHelper getInstance() {
        if (mInstance == null) {
            mInstance = new WifiHelper();
        }
        return mInstance;
    }

    public void init(Context context) {
        if (mContext != null) {
            Log.w(TAG, "GlassWifiInfoProvider has been inited!!!");
            return;
        }
        mContext = context;
        mWifiListObservers = new ArrayList<>();
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);// 一次扫描结束后发送该广播
        context.registerReceiver(resultReceiver, mFilter);
    }

    public void registerObserver(IWifiListObserver observer) {
        if (!mWifiListObservers.contains(observer)) {
            mWifiListObservers.add(observer);
            mWifiManager.startScan();

            if (mTimerStatus != TIMER_STATE_STARTED) {
                mWifiScanTimer = new Timer(true);
                mWifiScanTimerTask = new WifiTimerTask();
                mWifiScanTimer.schedule(mWifiScanTimerTask, 1000, 12 * 1000);
                mTimerStatus = TIMER_STATE_STARTED;
            }
        }
    }

    public void unregisterObserver(IWifiListObserver observer) {
        if (mWifiListObservers.contains(observer)) {
            mWifiListObservers.remove(observer);
            if (mWifiListObservers.size() == 0) {
                if (mTimerStatus == TIMER_STATE_STARTED) {
                    mWifiScanTimer.cancel();
                    mWifiScanTimerTask.cancel();
                    mTimerStatus = TIMER_STATE_CANCELED;
                }
            }
        }
    }

    public boolean isWifiConnected() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return wifiInfo.getNetworkId() != -1;
    }

    public boolean isWLANSharingOn() {
        Method method = null;
        int i = 0;
        try {
            method = mWifiManager.getClass().getMethod("getWifiApState");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            i = (Integer) method.invoke(mWifiManager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "wifi sharing state -> " + i);
        // 10---正在关闭；11---已关闭；12---正在开启；13---已开启
        return i == 13;
    }

    public boolean openWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            if (isWLANSharingOn()) {
                if (setWifiApEnabled(false)) {
                    bRet = mWifiManager.setWifiEnabled(true);
                }
            } else {
                bRet = mWifiManager.setWifiEnabled(true);
            }
        }
        return bRet;
    }

    public boolean setWifiApEnabled(boolean enabled) {
        Method method = null, configMethod = null;
        boolean result = false;
        if (mWifiManager == null) {
            Log.i(TAG, "mWifiManager is null  -> " + result);
            return result;
        }
        try {
            configMethod = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            WifiConfiguration apConfig = (WifiConfiguration) configMethod.invoke(mWifiManager);
            result = (boolean) method.invoke(mWifiManager, new Object[]{apConfig, enabled});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "setWifiApEnabled -> " + result);
        return result;
    }

    BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                mScanResults = null;
                mScanResults = mWifiManager.getScanResults();

                Collections.sort(mScanResults, SCAN_RESULT_COMPARATOR);

                for (IWifiListObserver observer : mWifiListObservers) {
                    observer.onWifiListChanged(mScanResults);
                }
            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    // This method must called after wifi enabled.
                    getConfiguration();
                    if (mTimerStatus != TIMER_STATE_STARTED) {
                        mWifiScanTimer = new Timer(true);
                        mWifiScanTimerTask = new WifiTimerTask();
                        mWifiScanTimer.schedule(mWifiScanTimerTask, 1000, 12 * 1000);
                        mTimerStatus = TIMER_STATE_STARTED;
                    }
                } else {
                    if (mTimerStatus == TIMER_STATE_STARTED) {
                        mWifiScanTimer.cancel();
                        mWifiScanTimerTask.cancel();
                        mTimerStatus = TIMER_STATE_CANCELED;
                    }
                }
                for (IWifiListObserver observer : mWifiListObservers) {
                    observer.onWifiStateChanged(wifiState);
                }
            }
        }
    };


    public int getWifiType(ScanResult result) {
        int wep = result.capabilities.toLowerCase().indexOf("wep");
        int wpa = result.capabilities.toLowerCase().indexOf("wpa");
        int type = -1;
        if (wep == -1 && wpa == -1) {
            type = WIFI_CIPHER_NO_PASS;
        } else if (wep != -1 && wpa == -1) {
            type = WIFI_CIPHER_WEP;
        } else if (wep == -1 && wpa != -1) {
            type = WIFI_CIPHER_WPA;
        }
        return type;
    }

    public String getWifiApSSID() {
        Method method = null;
        String SSID = null;
        try {
            method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            WifiConfiguration apConfig = (WifiConfiguration) method.invoke(mWifiManager);
            SSID = apConfig.SSID;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "getWifiApSSID -> " + SSID);
        return SSID;
    }

    public String getWifiApSharedKey() {
        Method method = null;
        String SharedKey = null;
        try {
            method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            WifiConfiguration apConfig = (WifiConfiguration) method.invoke(mWifiManager);
            SharedKey = apConfig.preSharedKey;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return SharedKey == null ? "" : SharedKey;
    }


    public String parseReadableScanResultCapability(Context context, ScanResult scanResult) {

        final String[] securityModes = {WEP, PSK, EAP};

        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (scanResult.capabilities.contains(securityModes[i])) {
                if (securityModes[i].equals(OPEN))
                    return context.getString(R.string.wifi_security_open);
                else if (securityModes[i].equals(WEP))
                    return context.getString(R.string.wifi_security_wep);
                else if (securityModes[i].equals(PSK))
                    return context.getString(R.string.wifi_security_psk);
                else if (securityModes[i].equals(EAP))
                    return context.getString(R.string.wifi_security_eap);
            }
        }
        return context.getString(R.string.wifi_security_unknown);

    }

    public int getWifiItemIconResId(ScanResult scanResult) {
        int level = WifiManager.calculateSignalLevel(scanResult.level, 4);
        int wifiType = WifiHelper.getInstance().getWifiType(scanResult);
        int drawableResId = 0;
        if (wifiType == WifiHelper.WIFI_CIPHER_NO_PASS) {
            switch (level) {
                case 0:
                    drawableResId = R.drawable.icon_wifi_4;
                    break;
                case 1:
                    drawableResId = R.drawable.icon_wifi_3;
                    break;
                case 2:
                    drawableResId = R.drawable.icon_wifi_2;
                    break;
                case 3:
                case 4:
                    drawableResId = R.drawable.icon_wifi_1;
                    break;

                default:
                    drawableResId = R.drawable.icon_wifi_4;
                    break;
            }
        } else {
            switch (level) {
                case 0:
                    drawableResId = R.drawable.icon_wifi_locked_4;
                    break;
                case 1:
                    drawableResId = R.drawable.icon_wifi_locked_3;
                    break;
                case 2:
                    drawableResId = R.drawable.icon_wifi_locked_2;
                    break;
                case 3:
                case 4:
                    drawableResId = R.drawable.icon_wifi_locked_1;
                    break;

                default:
                    drawableResId = R.drawable.icon_wifi_locked_1;
                    break;
            }
        }
        return drawableResId;
    }


    private List<WifiConfiguration> getConfiguration() {
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
        return mWifiConfigurations;
    }

    private class WifiTimerTask extends TimerTask {
        @Override
        public void run() {
            mWifiManager.startScan();
        }
    }

    private static final Comparator<ScanResult> SCAN_RESULT_COMPARATOR = new Comparator<ScanResult>() {

        public int compare(ScanResult scanResult1, ScanResult scanResult2) {
            if (scanResult1.level > scanResult2.level)
                return -1;
            return scanResult1.level >= scanResult2.level ? 0 : 1;
        }
    };
}
