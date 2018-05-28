/**
 * ****************************************************************************
 * Copyright (C) 2014 Ceyes Inc. All rights reserved.
 * *****************************************************************************
 */

package com.newvision.zeus.glasslauncher.settings.deviceinfo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.newvision.zeus.glasslauncher.R;
import com.newvision.zeus.glasslauncher.common.storage.GlassStorageHelper;

import java.util.ArrayList;
import java.util.List;

import cn.ceyes.glasswidget.alertview.GlassAlert;
import cn.ceyes.glasswidget.alertview.GlassAlertEntity;
import cn.ceyes.glasswidget.cardview.GlassCardView;
import cn.ceyes.glasswidget.menuview.GlassMenu;
import cn.ceyes.glasswidget.menuview.GlassMenuEntity;

public class DeviceInfoCardView extends GlassCardView {

    private static final String TAG = "GlassDeviceInfoCard";

    // Glass internal SDCard free space.
    private TextView internalSDCardFreeSpace;
    // Glass external SDCard free space.
    private TextView externalSDCardFreeSpace;
    // Glass serial no.
    private TextView deviceSerial;
    // Glass launcher version
    private TextView launcherVersion;

    private boolean isDebugMode = false;

    private GlassMenu mGlassMenu;
    private GlassAlert mGlassAlert;

    private static final int ALERT_ID_UNNOTIFY = 0;
    private static final int ALERT_ID_RESET = 1;

    public DeviceInfoCardView(Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.card_deviceinfo, this);
        internalSDCardFreeSpace = (TextView) view.findViewById(R.id.txt_device_free);
        deviceSerial = (TextView) view.findViewById(R.id.txt_device_serial);
        launcherVersion = (TextView) view.findViewById(R.id.txt_launcher_version);
        externalSDCardFreeSpace = (TextView) view.findViewById(R.id.txt_extends_device_free);

        isDebugMode = (Settings.Secure.getInt(this.getContext()
                .getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0);

        mGlassMenu = new GlassMenu(context);
        initGlassAlert();
    }

    @Override
    public void onCardVisible() {
        super.onCardVisible();

        // internal storage space
        internalSDCardFreeSpace.setText(mContext.getResources().getString(R.string.deviceinfo_title_innerspace) +
                GlassStorageHelper.getInstance().getInnerStorageSpace());
        String serial = Build.SERIAL;
        deviceSerial.setText(mContext.getResources().getString(R.string.deviceinfo_title_serial) + serial);
    }

    @Override
    public void onCardSelected() {

        final int MENU_ID_DEBUG = 0;
        final int MENU_ID_RESET = 1;
        final int MENU_ID_SETTING = 2;
        final int MENU_ID_ABOUT = 3;

        List<GlassMenuEntity> menuEntities = new ArrayList<GlassMenuEntity>();
        if (isDebugMode) {
            menuEntities.add(new GlassMenuEntity(MENU_ID_DEBUG, R.drawable.icon_bug_large, R.string.deviceinfo_menu_debug_close));
        } else {
            menuEntities.add(new GlassMenuEntity(MENU_ID_DEBUG, R.drawable.icon_bug_large, R.string.deviceinfo_menu_debug_open));
        }
        menuEntities.add(new GlassMenuEntity(MENU_ID_RESET, R.drawable.icon_reset_medium, R.string.deviceinfo_menu_reset));
        menuEntities.add(new GlassMenuEntity(MENU_ID_SETTING, R.drawable.icon_deviceinfo_setting, R.string.deviceinfo_menu_system_setting));
        menuEntities.add(new GlassMenuEntity(MENU_ID_ABOUT, R.drawable.icon_aboutglass, R.string.deviceinfo_menu_about_glass));

        mGlassMenu.setMenuEntities(menuEntities).setOnMenuSelectCallback(new GlassMenu.IMenuSelectCallback() {
            @Override
            public void onMenuSelected(int menuEntityId) {
                switch (menuEntityId) {
                    case MENU_ID_DEBUG:
                        // This operation needs a system permission, so only the system app can handle this operation.
                        // Otherwise, throws a SecurityException.
                        try {
                            if (isDebugMode) {// close debug mode.
                                Settings.Secure.putInt(mContext.getContentResolver(), Settings.Secure.ADB_ENABLED, 0);
                                mGlassAlert.setAlertEntity(GlassAlertEntity.createHorizontalAlert(ALERT_ID_UNNOTIFY, R.drawable.icon_success, R.string.deviceinfo_tips_debug_close_success)).show();
                                isDebugMode = false;
                            } else {// open debug mode.
                                Settings.Secure.putInt(mContext.getContentResolver(), Settings.Secure.ADB_ENABLED, 1);
                                mGlassAlert.setAlertEntity(GlassAlertEntity.createHorizontalAlert(ALERT_ID_UNNOTIFY, R.drawable.icon_success, R.string.deviceinfo_tips_debug_open_success)).show();
                                isDebugMode = true;
                            }
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                        break;
                    case MENU_ID_RESET:
                        mGlassAlert.setAlertEntity(GlassAlertEntity.createVerticalAlert(ALERT_ID_RESET, R.drawable.icon_reset_medium, R.string.deviceinfo_tips_reset_delay,
                                R.string.deviceinfo_tips_reset_cancel).setProgressbarTime(10 * 1000).setCancelable(true)).show();
                        break;
                    case MENU_ID_SETTING:
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
                        mContext.startActivity(intent);
                        break;
                    case MENU_ID_ABOUT:
                        mContext.startActivity(new Intent(mContext, AboutDeviceActivity.class));
                        break;
                }
            }
        }).show();
    }

    @Override
    public void onCardInvisible() {
        super.onCardInvisible();
        mGlassAlert.dismiss();
        mGlassMenu.dismiss();
    }

    @Override
    public void onCardFinished() {
        super.onCardFinished();

        sendGlassEvent(50000, null);
    }

    private void initGlassAlert() {
        mGlassAlert = new GlassAlert(mContext);
        mGlassAlert.setOnAlertDismissCallback(new GlassAlert.IGlassAlertDismissCallback() {
            @Override
            public void onAlertDismissed(int id, boolean forced) {
                switch (id) {
                    case ALERT_ID_RESET:
                        if (!forced) {
                            Log.i(TAG, "onAlertDismissed");
                            mContext.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
                        }
                        break;
                }
            }
        });
    }

    private String currentLanguage() {
        String language = getResources().getConfiguration().locale.getCountry();
        Log.d(TAG, "current language is " + language);
        return language;
    }

}
