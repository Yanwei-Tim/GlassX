package com.newvision.zeus.glasslauncher.settings.wifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;
import com.newvision.zeus.glasslauncher.common.helper.GlassWifiInfoProvider;

import java.util.ArrayList;
import java.util.List;

import cn.ceyes.glasswidget.cardview.GlassCardView;
import cn.ceyes.glasswidget.menuview.GlassMenu;
import cn.ceyes.glasswidget.menuview.GlassMenuEntity;

/**
 * Created by zhangsong on 17-7-3.
 */

public class WifiCardView extends GlassCardView {
    private static final String TAG = "WifiCardView";

    private ImageView wifiStateImg;
    private TextView ssidText;
    private TextView wifiStateText;

    private GlassMenu glassMenu;
    private boolean isWifiConnected;
    private GlassWifiConnector wifiConnector;

    public WifiCardView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.card_setting_wifi, this);

        wifiStateImg = (ImageView) findViewById(R.id.img_wifi_state);
        ssidText = (TextView) findViewById(R.id.txt_ssid);
        wifiStateText = (TextView) findViewById(R.id.txt_connect_state);

        glassMenu = new GlassMenu(context);
    }

    @Override
    public void onCardVisible() {
        super.onCardVisible();
        // Send a msg to notice provider to register receivers.
        sendGlassEvent(10000, null);

        wifiConnector = GlassWifiConnector.get(getContext());
    }

    @Override
    public void onCardSelected() {
        super.onCardSelected();

        final List<GlassMenuEntity> menus = new ArrayList<>();
        menus.add(new GlassMenuEntity(1000, R.drawable.ic_wifi_medium, R.string.wifi_menu_changenetwork));
        if (isWifiConnected) {
            menus.add(new GlassMenuEntity(1001, R.drawable.ic_no_medium, R.string.wifi_menu_disconnect));
        } else {
            menus.get(0).setTitle(R.string.wifi_menu_connect);
        }

        glassMenu.setMenuEntities(menus)
                .setOnMenuSelectCallback(new GlassMenu.IMenuSelectCallback() {
                    @Override
                    public void onMenuSelected(int menuId) {
                        switch (menuId) {
                            case 1000:
                                // Send a msg to show the wifi list activity.
                                sendGlassEvent(20000, null);
                                break;
                            case 1001:
                                wifiConnector.disconnect();
                                break;
                        }
                    }
                })
                .show();
    }

    @Override
    public void onCardInvisible() {
        super.onCardInvisible();
        // Send a msg to notice provider to unregister receivers.
        sendGlassEvent(10001, null);
        glassMenu.dismiss();
    }

    @Override
    public void onCardFinished() {
        super.onCardFinished();
        // Send a activity finish msg.
        sendGlassEvent(50000, null);
    }

    public ImageView getWifiStateImg() {
        return wifiStateImg;
    }

    public void setWifiStateImg(int resId) {
        this.wifiStateImg.setImageResource(resId);
    }

    public TextView getSsidText() {
        return ssidText;
    }

    public void setSsidText(String ssid) {
        this.ssidText.setText(ssid);
    }

    public TextView getWifiStateText() {
        return wifiStateText;
    }

    public void setWifiStateText(int stringId, int colorId) {
        this.wifiStateText.setText(stringId);
        this.wifiStateText.setTextColor(getContext().getResources().getColor(colorId));
    }

    public void setWifiState(int state) {
        switch (state) {
            case GlassWifiInfoProvider.GLASS_WIFI_STATE_CONNECTED:
                String ssid = GlassWifiInfoProvider.getInstance().getCurrWifiInfo().getSSID().replace("\"", "");
                setSsidText(ssid);
                setWifiStateImg(R.drawable.ic_wifi4_big);
                setWifiStateText(R.string.wifi_state_connected, R.color.color_green);

                isWifiConnected = true;
                break;
            case GlassWifiInfoProvider.GLASS_WIFI_STATE_DISCONNECTED:
                setSsidText("Wi-Fi");
                setWifiStateImg(R.drawable.ic_wifi0_big);
                setWifiStateText(R.string.wifi_state_unconnected, R.color.setting_default_color);

                isWifiConnected = false;
                break;
        }
    }

    public void setWifiSignalLevel(int level) {
        int drawableId = 0;
        switch (level) {
            case 0:
                drawableId = R.drawable.ic_wifi1_big;
                break;
            case 1:
                drawableId = R.drawable.ic_wifi2_big;
                break;
            case 2:
                drawableId = R.drawable.ic_wifi3_big;
                break;
            case 3:
            case 4:
                drawableId = R.drawable.ic_wifi4_big;
                break;

            default:
                drawableId = R.drawable.ic_wifi0_big;
                break;
        }
        setWifiStateImg(drawableId);
    }
}
