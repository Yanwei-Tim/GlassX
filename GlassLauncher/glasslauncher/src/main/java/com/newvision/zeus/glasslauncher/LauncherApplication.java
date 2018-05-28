package com.newvision.zeus.glasslauncher;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.newvision.zeus.glasscore.protocol.netty.client.GlassTcpClient;
import com.newvision.zeus.glasscore.protocol.netty.server.GlassTcpServer;
import com.newvision.zeus.glasscore.protocol.usb.host.GlassUsbHostService;
import com.newvision.zeus.glasslauncher.common.helper.GlassBatteryInfoProvider;
import com.newvision.zeus.glasslauncher.common.helper.GlassTimeTickObserver;
import com.newvision.zeus.glasslauncher.common.helper.GlassWifiInfoProvider;

/**
 * Created by zhangsong on 17-6-29.
 */

public class LauncherApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        GlassBatteryInfoProvider.getInstance().init(this);
        GlassTimeTickObserver.getInstance().init(this);
        GlassWifiInfoProvider.getInstance().init(this);

        startService(new Intent(this, GlassTcpServer.class)); //start tcp service
        startService(new Intent(this, GlassUsbHostService.class)); //start usb service
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        try {
            GlassBatteryInfoProvider.getInstance().destroy();
            GlassTimeTickObserver.getInstance().destroy();
            GlassWifiInfoProvider.getInstance().destroy();
            stopService(new Intent(this, GlassTcpClient.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Context getContext() {
        return context;
    }
}
