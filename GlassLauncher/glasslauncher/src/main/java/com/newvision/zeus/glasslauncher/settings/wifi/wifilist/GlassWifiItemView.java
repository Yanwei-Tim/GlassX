package com.newvision.zeus.glasslauncher.settings.wifi.wifilist;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;

import com.newvision.zeus.glasslauncher.R;
import com.newvision.zeus.glasslauncher.common.helper.GlassWifiInfoProvider;
import com.newvision.zeus.glasslauncher.settings.wifi.GlassWifiConnector;

import java.util.ArrayList;
import java.util.List;

import cn.ceyes.glasswidget.alertview.GlassAlert;
import cn.ceyes.glasswidget.alertview.GlassAlertEntity;
import cn.ceyes.glasswidget.menuview.GlassMenu;
import cn.ceyes.glasswidget.menuview.GlassMenuEntity;

public class GlassWifiItemView extends GlassWifiDetailView {
    private static final String TAG = GlassWifiItemView.class.getSimpleName();

    private ScanResult mScanResult = null;

    private static final int ALERT_ID_NETWORK_CONNECTING = 1;
    private static final int ALERT_ID_NETWORK_CONNECTED = 2;
    private static final int ALERT_ID_NETWORK_CONNECT_TIMEOUT = 3;
    private static final int ALERT_ID_NETWORK_ALREADY_CONNECTED = 4;

    private GlassAlert mGlassAlert;
    private GlassMenu mGlassMenu;

    private GlassWifiConnector wifiConnector;

    public GlassWifiItemView(Context context) {
        super(context);

        wifiConnector = GlassWifiConnector.get(context);

        initGlassAlert(context);
        mGlassMenu = new GlassMenu(mContext);
    }

    public void initData(ScanResult scanResult) {
        mScanResult = scanResult;

        wifiStateImg.setImageResource(getItemIconId(mScanResult));
        ssidText.setText(mScanResult.SSID);
    }

    @Override
    public void onCardSelected() {
        super.onCardSelected();

        final int MENU_ID_CONNECT = 0;
        final int MENU_ID_UNSAVE = 1;

        List<GlassMenuEntity> menuEntities = new ArrayList<GlassMenuEntity>();
        menuEntities.add(new GlassMenuEntity(MENU_ID_CONNECT, R.drawable.ic_wifi_medium, R.string.wifi_menu_connect));
        if (wifiConnector.existInConfiguration(mScanResult)) {
            menuEntities.add(new GlassMenuEntity(MENU_ID_UNSAVE, R.drawable.ic_no_medium, R.string.wifi_menu_unsave));
        }

        mGlassMenu.setMenuEntities(menuEntities).setOnMenuSelectCallback(new GlassMenu.IMenuSelectCallback() {
            @Override
            public void onMenuSelected(int menuEntityId) {
                switch (menuEntityId) {
                    case MENU_ID_CONNECT:

                        if (GlassWifiInfoProvider.getInstance().getCurrWifiInfo().getSSID().equals("\"" + mScanResult.SSID + "\"")) {
                            mGlassAlert.setAlertEntity(GlassAlertEntity.createVerticalAlert(ALERT_ID_NETWORK_ALREADY_CONNECTED, R.drawable.ic_failed,
                                    R.string.wifi_tips_current_wifi_connected, "")).show();
                            return;
                        }

                        mGlassAlert.setAlertEntity(GlassAlertEntity.createHorizontalAlert(ALERT_ID_NETWORK_CONNECTING,
                                R.drawable.ic_wifi_medium, R.string.wifi_tips_connecting)
                                .setRefreshStyle(GlassAlertEntity.STYLE_DEFAULT))
                                .show();

                        wifiConnector.connect(mScanResult, 60000, new GlassWifiConnector.IWifiConnectListener() {
                            @Override
                            public void onConnected() {
                                Log.i(TAG, "onConnected: ");
                                mGlassAlert.setAlertEntity(GlassAlertEntity.createHorizontalAlert(ALERT_ID_NETWORK_CONNECTED,
                                        R.drawable.ic_wifi_medium, R.string.wifi_tips_connect_success))
                                        .show();
                            }

                            @Override
                            public void onErrorPWD() {
                            }

                            @Override
                            public void onTimeout() {
                                Log.i(TAG, "onTimeout: ");
                                mGlassAlert.setAlertEntity(GlassAlertEntity.createHorizontalAlert(ALERT_ID_NETWORK_CONNECT_TIMEOUT,
                                        R.drawable.ic_wifi_medium, R.string.wifi_tips_connect_timeout))
                                        .show();
                            }
                        });
                        break;

                    case MENU_ID_UNSAVE:
                        wifiConnector.removeNetwork(mScanResult);
                        sendGlassEvent(30000, mScanResult);
                        break;
                    default:
                        break;
                }
            }
        }).show();
    }

    @Override
    public void onCardInvisible() {
        super.onCardInvisible();
        Log.i(TAG, "onCardInvisible");
        mGlassAlert.dismiss();
        mGlassMenu.dismiss();
    }

    private void initGlassAlert(Context context) {
        mGlassAlert = new GlassAlert(context);
        mGlassAlert.setOnAlertDismissCallback(new GlassAlert.IGlassAlertDismissCallback() {
            @Override
            public void onAlertDismissed(int id, boolean forced) {
                switch (id) {
                    case ALERT_ID_NETWORK_CONNECTING:
                        mGlassAlert.setAlertEntity(GlassAlertEntity.createHorizontalAlert(0, R.drawable.ic_failed,
                                R.string.wifi_tips_connect_timeout).setTimeoutMillis(5000)).show();
                        break;
                    case ALERT_ID_NETWORK_CONNECTED:
                        ((Activity) mContext).finish();
                        break;
                }
            }
        });
    }

    private int getItemIconId(ScanResult result) {
        int level = wifiConnector.calculateSignalLevel(result);
        int drawableResId = 0;
        switch (level) {
            case 0:
                drawableResId = R.drawable.ic_wifi1_big;
                break;
            case 1:
                drawableResId = R.drawable.ic_wifi2_big;
                break;
            case 2:
                drawableResId = R.drawable.ic_wifi3_big;
                break;
            case 3:
            case 4:
                drawableResId = R.drawable.ic_wifi4_big;
                break;

            default:
                drawableResId = R.drawable.ic_wifi0_big;
                break;
        }
        return drawableResId;
    }
}
