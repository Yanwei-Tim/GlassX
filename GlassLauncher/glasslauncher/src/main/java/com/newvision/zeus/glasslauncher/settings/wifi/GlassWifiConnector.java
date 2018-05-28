package com.newvision.zeus.glasslauncher.settings.wifi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.newvision.zeus.glasslauncher.common.helper.CountDownTimer;
import com.newvision.zeus.glasslauncher.common.helper.GlassWifiInfoProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 1.打开、关闭设备wifi
 * 2.根据wifi ssid、 wifi password、 wifi 加密方式连接wifi。
 */
public class GlassWifiConnector {

    /**
     * Can be used only once. When the wifi state is enabled, this listener will be set null.
     */
    public interface IWifiEnableListener {
        void onEnable();
    }

    /**
     * Wifi connect state listener.
     *
     * @see #connect(String, String, int, int, IWifiConnectListener)
     */
    public interface IWifiConnectListener {
        void onConnected();

        void onErrorPWD();

        void onTimeout();
    }

    private static final String TAG = "GlassWifiConnector";

    // wifi的加密类型
    private static final int WIFI_CIPHER_NO_PASS = 1;
    private static final int WIFI_CIPHER_WEP = 2;
    private static final int WIFI_CIPHER_WPA = 3;

    private Context mContext;

    private WifiManager mWifiManager = null;

    private IWifiEnableListener wifiEnableListener;

    // 准备连接的wifi ssid。
    private String targetConnectSSID = null;
    // 把targetConnectSSID添加到WifiConfiguration中产生的id，可根据该id将该Configuration移除。
    private int targetNetworkId;
    // 切换wifi前已连接的wifi ssid，用于新的wifi连接失败后，重连到该wifi。
    private String previousConnectedSSID = null;
    // 记录wifi连接超时的timer
    private CountDownTimer connectTimeoutTimer;
    private IWifiConnectListener wifiConnectListener;

    public static GlassWifiConnector get(Context context) {
        return new GlassWifiConnector(context);
    }

    // 打开wifi功能
    public boolean enableWifi(IWifiEnableListener listener) {
        if (mWifiManager.isWifiEnabled()) {
            // already opened.
            listener.onEnable();
            return true;
        }

        registerWifiEnableReceiver();

        wifiEnableListener = listener;

        return mWifiManager.setWifiEnabled(true);
    }

    // 关闭WIFI
    public boolean disableWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            // already closed.
            return true;
        }

        return mWifiManager.setWifiEnabled(false);
    }

    // 判断wifi是否已经连接
    public boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    // 根据WifiConfiguration中的networkId连接网络
    public void connect(String SSID, int timeoutMillis, IWifiConnectListener listener) {
        if (SSID == null) return;

        List<WifiConfiguration> configuredWifis = mWifiManager.getConfiguredNetworks();
        Log.i(TAG, "connect: " + Arrays.toString(configuredWifis.toArray()));

        targetConnectSSID = SSID;

        WifiInfo previousInfo = mWifiManager.getConnectionInfo();
        if (previousInfo != null && !TextUtils.isEmpty(previousInfo.getSSID())) {
            previousConnectedSSID = previousInfo.getSSID().replace("\"", "");
            Log.i(TAG, "previousConnectedSSID: " + previousConnectedSSID);
        }

        registerSupplicantStateReceiver();

        if (timeoutMillis < 10000) // 至少为10s
            timeoutMillis = 10000;

        connectTimeoutTimer = new CountDownTimer(timeoutMillis) {
            @Override
            public void onFinish() {
                // Alert user wifi connecting timeout.
                unregisterSupplicantStateReceiver();
                // 将该未连接成功的configuration移除。
                mWifiManager.removeNetwork(targetNetworkId);
                // 用于重连由于该次连接断开的wifi。
                connect(previousConnectedSSID, 60000, null);

                if (wifiConnectListener != null) {
                    wifiConnectListener.onTimeout();
                    wifiConnectListener = null;
                }
            }
        };

        wifiConnectListener = listener;

        int networkId = -1;
        for (WifiConfiguration wifi : configuredWifis) {
            if (wifi.SSID.equals("\"" + SSID + "\"")) {
                networkId = wifi.networkId;
                break;
            }
        }

        if (networkId == -1) {
            // this wifi is a no password wifi
            WifiConfiguration wifiConfiguration = createWifiConfiguration(SSID, "", WIFI_CIPHER_NO_PASS);
            networkId = mWifiManager.addNetwork(wifiConfiguration);
        }

        Log.i(TAG, "connect: " + networkId);

        // connect now...
        connect(networkId);
        connectTimeoutTimer.start();
    }

    // 指定配置好的网络进行连接
    public void connect(int netId) {
        if (netId == -1) {
            return;
        }

        if (!mWifiManager.isWifiEnabled()) {
            Log.e(TAG, "connect: wifi is not enabled");

            return;
        }

        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(netId, true);
    }

    // 提供一个外部接口，传入要连接的无线网
    public void connect(final String SSID, final String password, final int type, int timeoutMillis, IWifiConnectListener listener) {
        assert (SSID != null);

        if (type != WIFI_CIPHER_NO_PASS && type != WIFI_CIPHER_WEP && type != WIFI_CIPHER_WPA)
            throw new IllegalStateException("Unknown wifi cipher type: " + type + ", only support WIFI_CIPHER_NO_PASS(1), " +
                    "WIFI_CIPHER_WEP(2), WIFI_CIPHER_WPA(3)");

        targetConnectSSID = SSID;

        WifiInfo previousInfo = mWifiManager.getConnectionInfo();
        if (previousInfo != null && !TextUtils.isEmpty(previousInfo.getSSID())) {
            previousConnectedSSID = previousInfo.getSSID().replace("\"", "");
            Log.i(TAG, "previousConnectedSSID: " + previousConnectedSSID);
        }

        registerSupplicantStateReceiver();

        if (timeoutMillis < 10000) // 至少为10s
            timeoutMillis = 10000;

        connectTimeoutTimer = new CountDownTimer(timeoutMillis) {
            @Override
            public void onFinish() {
                // Alert user wifi connecting timeout.
                unregisterSupplicantStateReceiver();
                // 将该未连接成功的configuration移除。
                mWifiManager.removeNetwork(targetNetworkId);
                // 用于重连由于该次连接断开的wifi。
                connect(previousConnectedSSID, 60000, null);

                if (wifiConnectListener != null) {
                    wifiConnectListener.onTimeout();
                    wifiConnectListener = null;
                }
            }
        };

        wifiConnectListener = listener;

        enableWifi(new IWifiEnableListener() {
            @Override
            public void onEnable() {
                WifiConfiguration tempConfig = isExists(SSID);
                if (tempConfig != null) {
                    mWifiManager.removeNetwork(tempConfig.networkId);
                }

                WifiConfiguration wifiConfig = createWifiConfiguration(SSID, password, type);

                int netId = addNetwork(wifiConfig);
                Log.i(TAG, "onEnable: " + netId);

                // wifi连接超时timer启动
                connectTimeoutTimer.start();
            }
        });
    }

    public void connect(ScanResult result, int timeoutMillis, IWifiConnectListener listener) {
        connect(result.SSID, timeoutMillis, listener);
    }

    public void removeNetwork(ScanResult result) {
        List<WifiConfiguration> configuredWifis = mWifiManager.getConfiguredNetworks();

        int networkId = 0;
        for (WifiConfiguration wifiConfiguration : configuredWifis) {
            if (wifiConfiguration.SSID.equals("\"" + result.SSID + "\"")) {
                networkId = wifiConfiguration.networkId;
                mWifiManager.removeNetwork(networkId);
                return;
            }
        }
    }

    public int calculateSignalLevel(ScanResult scanResult) {
        return WifiManager.calculateSignalLevel(scanResult.level, 4);
    }

    public boolean existInConfiguration(ScanResult result) {
        List<WifiConfiguration> configuredWifis = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration configuration : configuredWifis) {
            if (configuration.SSID.equals("\"" + result.SSID + "\"")) {
                return true;
            }
        }
        return false;
    }

    // 断开指定ID的网络
    public void disconnect() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        mWifiManager.disableNetwork(wifiInfo.getNetworkId());
        mWifiManager.disconnect();
    }

    // 得到Wifi网络列表，按信号强度递减排序
    public List<ScanResult> getAllWifiListWithStrongSignalFirst() {
        return GlassWifiInfoProvider.getInstance().getWifiScanResults();
    }

    // 得到Wifi网络列表，无需再输入密码的在前
    public List<ScanResult> getAllWifiListWithNoPasswordFirst() {
        List<ScanResult> list = getNoPasswordWifiList();
        list.addAll(getHasPasswordWifiList());

        return list;
    }

    /**
     * 得到本地存储的并且周围可以扫描到的wifi热点和没有密码的wifi热点
     *
     * @return
     */
    public List<ScanResult> getExistsInConfigurationList() {
        List<ScanResult> list = new ArrayList<ScanResult>();

        List<ScanResult> scanResults = GlassWifiInfoProvider.getInstance().getWifiScanResults();

        if (scanResults == null) {
            Log.i(TAG, "wifi scan list is null");
            return list;
        }

        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();

        for (ScanResult result : scanResults) {
            for (WifiConfiguration configured : configuredNetworks) {
                if (configured.SSID.equals("\"" + result.SSID + "\"")) {
                    if (!list.contains(result)) {
                        list.add(result);
                        break;
                    }
                }
            }
        }
        return list;
    }

    // 构造器
    private GlassWifiConnector(Context context) {
        mContext = context;

        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    private void registerWifiEnableReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mContext.registerReceiver(wifiEnableReceiver, filter);
    }

    private void unregisterWifiEnableReceiver() {
        mContext.unregisterReceiver(wifiEnableReceiver);
    }

    // 添加一个网络并连接,返回添加网络的netid
    private int addNetwork(WifiConfiguration configure) {
        targetNetworkId = mWifiManager.addNetwork(configure);
        boolean enabled = mWifiManager.enableNetwork(targetNetworkId, true);
        Log.i(TAG, "addNetwork: " + enabled);
        return targetNetworkId;
    }

    // 删除特定的已配置好的wifi Configurtion by SSID
    private boolean removeNetwork(String SSID) {
        int networkId = -1;
        List<WifiConfiguration> configuredWifis = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration wifi : configuredWifis) {
            if (wifi.SSID.equals("\"" + SSID + "\"")) {
                networkId = wifi.networkId;
                break;
            }
        }

        return removeNetwork(networkId);
    }

    // 删除特定的已配置好的wifiConfigurtion by netId
    private boolean removeNetwork(int netId) {
        if (netId == -1) {
            return false;
        }

        // 删除配置好的指定ID的网络
        mWifiManager.removeNetwork(netId);
        return mWifiManager.saveConfiguration();
    }

    // 然后是一个实际应用方法，只验证过没有密码的情况：
    // 分为三种情况：1没有密码2用wep加密3用wpa加密
    private WifiConfiguration createWifiConfiguration(String SSID, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        switch (type) {
            case WIFI_CIPHER_NO_PASS:
                config.wepKeys[0] = "";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case WIFI_CIPHER_WEP:
                config.hiddenSSID = true;
                config.wepKeys[0] = "\"" + password + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case WIFI_CIPHER_WPA:
                config.preSharedKey = "\"" + password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
                break;
            default:
                Log.d(TAG, "createWifiConfiguration, unknown wifi cipher type: " + type);
                break;
        }
        return config;
    }

    private WifiConfiguration isExists(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /**
     * 得到没有密码的wifi热点
     *
     * @return
     */
    private List<ScanResult> getNoPasswordWifiList() {
        List<ScanResult> list = new ArrayList<ScanResult>();

        List<ScanResult> scanResults = GlassWifiInfoProvider.getInstance().getWifiScanResults();

        if (scanResults.size() > 0) {
            for (ScanResult result : scanResults) {
                if (getWifiType(result) == WIFI_CIPHER_NO_PASS) {
                    list.add(result);
                }
            }
        }

        List<ScanResult> configurations = getExistsInConfigurationList();
        for (ScanResult scanResult : configurations) {
            for (ScanResult scanResult1 : list) {
                if (scanResult1.SSID.equals(scanResult.SSID)) {
                    list.remove(scanResult1);
                    break;
                }
            }
        }

        return list;
    }

    /**
     * 得到有密码的wifi热点
     *
     * @return
     */
    private List<ScanResult> getHasPasswordWifiList() {
        List<ScanResult> list = new ArrayList<ScanResult>();

        List<ScanResult> scanResults = GlassWifiInfoProvider.getInstance().getWifiScanResults();

        if (scanResults.size() > 0) {
            for (ScanResult result : scanResults) {
                if (getWifiType(result) != WIFI_CIPHER_NO_PASS) {
                    list.add(result);
                }
            }
        }

        return list;
    }

    // 得到某一个wifiinfo的信号强度
    public int getSignalLevel(WifiInfo info) {
        int level = WifiManager.calculateSignalLevel(info.getRssi(), 4);
        return level;

    }

    // 处理wifi热点的加密方式
    @SuppressLint("DefaultLocale")
    private int getWifiType(ScanResult wifi) {
        int wep = wifi.capabilities.toLowerCase().indexOf("wep");
        int wpa = wifi.capabilities.toLowerCase().indexOf("wpa");

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

    private BroadcastReceiver wifiEnableReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if ((WifiManager.WIFI_STATE_CHANGED_ACTION).equals(action)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        if (wifiEnableListener != null) {
                            wifiEnableListener.onEnable();
                            wifiEnableListener = null;
                        }

                        unregisterWifiEnableReceiver();
                        break;
                }
                return;
            }
        }
    };

    private void registerSupplicantStateReceiver() {
        mContext.registerReceiver(supplicantStateReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
    }

    private void unregisterSupplicantStateReceiver() {
        mContext.unregisterReceiver(supplicantStateReceiver);
    }

    private BroadcastReceiver supplicantStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                WifiInfo info = mWifiManager.getConnectionInfo();
                // 过滤掉不是目标ssid返回的状态
                if (!targetConnectSSID.equals(info.getSSID().replace("\"", ""))) {
                    return;
                }
                SupplicantState state = info.getSupplicantState();
                Log.i(TAG, "Supplicant state: " + state);
                if (state == SupplicantState.COMPLETED) {// 已连接
                    Log.i(TAG, "COMPLETED");
                    unregisterSupplicantStateReceiver();
                    // 保存当前已连接wifi的列表
                    mWifiManager.saveConfiguration();

                    if (connectTimeoutTimer != null) {
                        connectTimeoutTimer.cancel();
                        connectTimeoutTimer = null;
                    }

                    if (wifiConnectListener != null) {
                        wifiConnectListener.onConnected();
                        wifiConnectListener = null;
                    }
                } else if (state == SupplicantState.DISCONNECTED) {//已断开
                    final int errorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                    Log.i(TAG, "DISCONNECTED, error code: " + errorCode);
                    if (errorCode != WifiManager.ERROR_AUTHENTICATING)
                        return;

                    Log.i(TAG, "wifi密码验证失败！");
                    unregisterSupplicantStateReceiver();
                    // 将该未连接成功的configuration移除。
                    mWifiManager.removeNetwork(targetNetworkId);
                    // 用于重连由于该次连接断开的wifi。
                    connect(previousConnectedSSID, 20000, null);

                    if (connectTimeoutTimer != null) {
                        connectTimeoutTimer.cancel();
                        connectTimeoutTimer = null;
                    }

                    if (wifiConnectListener != null) {
                        wifiConnectListener.onErrorPWD();
                        wifiConnectListener = null;
                    }
                }
                return;
            }
        }
    };
}