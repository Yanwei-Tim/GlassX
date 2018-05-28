package com.newvision.zeus.glasslauncher.settings.wifi.wifilist;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.util.Log;

import com.newvision.zeus.glasslauncher.R;
import com.newvision.zeus.glasslauncher.common.helper.GlassWifiInfoProvider;
import com.newvision.zeus.glasslauncher.settings.wifi.GlassWifiConnector;

import org.json.JSONObject;

import cn.ceyes.glasswidget.alertview.GlassAlert;
import cn.ceyes.glasswidget.alertview.GlassAlertEntity;
import cn.ceyes.glasswidget.cardview.GlassCardListView;
import cn.ceyes.glasswidget.cardview.GlassCardManager;
import cn.ceyes.glasswidget.cardview.IGlassEventListener;

public class GlassWifiListActivity extends Activity {
    private static final String TAG = GlassWifiListActivity.class.getSimpleName();

    private GlassCardManager mCardManager = null;
    private GlassWifiListRegistry wifiListRegistry;
    private GlassCardListView mListView = null;

    private GlassAlert mGlassAlert;

    private static final int ALERT_ID_UNNOTIFY = 0;
    private static final int ALERT_ID_NETWORK_CONNECTING = 1;
    private static final int ALERT_ID_NETWORK_CONNECTED = 2;
    private static final int ALERT_ID_ERROR_AUTHENTICATING = 3;
    private static final int ALERT_ID_ERROR_CURRENT_CONNECTED = 4;
    private static final int ALERT_ID_NETWORK_CONNECT_TIMEOUT = 5;

    private GlassWifiConnector wifiConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCardManager = new GlassCardManager(this, new IGlassEventListener() {
            @Override
            public boolean onGlassEvent(int eventCode, Object event) {
                switch (eventCode) {
                    case 20000:// Start the qr code scanner activity
//                        startActivityForResult(new Intent(GlassWifiListActivity.this, QRCodeScanner.class), 100);
                        break;
                    case 30000:
                        ScanResult result = (ScanResult) event;
                        wifiListRegistry.deactive(result);
                        break;
                    case 50000:// Finish this activity.
                        finish();
                        break;
                }
                return false;
            }
        });

        wifiListRegistry = new GlassWifiListRegistry(this);
        wifiListRegistry.init(mCardManager);

        mListView = new GlassCardListView(this);
        mListView.init(mCardManager);
        setContentView(mListView);

        wifiConnector = GlassWifiConnector.get(this);

        initGlassAlert();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGlassAlert.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // wifi protocol: {"type":0,"ssid":"TP-LINK_OFFICE","pwd":"a1b2c3d4","crypt_type":3}
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Log.i(TAG, scanResult);
            try {
                JSONObject object = new JSONObject(scanResult);

                int QRType = object.optInt("type", -1);
                if (QRType != 0) {
                    mGlassAlert.setAlertEntity(GlassAlertEntity.createVerticalAlert(ALERT_ID_UNNOTIFY, R.drawable.ic_failed,
                            R.string.qr_tips_invalid, R.string.qr_tips_scan_glass_manager)).show();
                    return;
                }
                String SSID = object.optString("ssid");
                String wifiPassword = object.optString("pwd");
                int wifiType = object.optInt("crypt_type");
                Log.i(TAG, "SSID:" + SSID + ",wifiPassword:" + wifiPassword + ",wifiType:" + wifiType);

                if (wifiConnector.isWifiConnected()) {
                    WifiInfo wifiInfo = GlassWifiInfoProvider.getInstance().getCurrWifiInfo();
                    Log.i(TAG, "connected wifi info ssid : " + wifiInfo.getSSID().replace("\"", ""));
                    if (SSID.equals(wifiInfo.getSSID().replace("\"", ""))) {// current wifi is connected.
                        mGlassAlert.setAlertEntity(GlassAlertEntity.createVerticalAlert(ALERT_ID_ERROR_CURRENT_CONNECTED, R.drawable.ic_failed,
                                R.string.wifi_tips_current_wifi_connected, R.string.tips_null)).show();

                        return;
                    }
                }

                mGlassAlert.setAlertEntity(GlassAlertEntity.createHorizontalAlert(ALERT_ID_NETWORK_CONNECTING, R.drawable.ic_wifi_medium,
                        R.string.wifi_connect_state_connecting)).show();

                wifiConnector.connect(SSID, wifiPassword, wifiType, 60000, wifiConnectListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initGlassAlert() {
        mGlassAlert = new GlassAlert(GlassWifiListActivity.this);
        mGlassAlert.setOnAlertDismissCallback(new GlassAlert.IGlassAlertDismissCallback() {
            @Override
            public void onAlertDismissed(int alertEntityId, boolean forced) {
                switch (alertEntityId) {
                    case ALERT_ID_NETWORK_CONNECTED:
                        GlassWifiListActivity.this.finish();
                        break;
                    case ALERT_ID_ERROR_CURRENT_CONNECTED:
                        GlassWifiListActivity.this.finish();
                        break;
                }
            }
        });
    }

    GlassWifiConnector.IWifiConnectListener wifiConnectListener = new GlassWifiConnector.IWifiConnectListener() {
        @Override
        public void onConnected() {
            mGlassAlert.setAlertEntity(GlassAlertEntity.createHorizontalAlert(ALERT_ID_NETWORK_CONNECTED,
                    R.drawable.ic_wifi_medium, R.string.wifi_tips_connect_success))
                    .show();
        }

        @Override
        public void onErrorPWD() {
            mGlassAlert.setAlertEntity(GlassAlertEntity.createHorizontalAlert(ALERT_ID_ERROR_AUTHENTICATING,
                    R.drawable.ic_wifi_medium, R.string.wifi_connect_state_error_auth))
                    .show();
        }

        @Override
        public void onTimeout() {
            mGlassAlert.setAlertEntity(GlassAlertEntity.createHorizontalAlert(ALERT_ID_NETWORK_CONNECT_TIMEOUT,
                    R.drawable.ic_wifi_medium, R.string.wifi_tips_connect_timeout))
                    .show();
        }
    };
}
