package com.newvision.zeus.glassmanager;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.newvision.zeus.glasscore.protocol.netty.client.GlassTcpClient;
import com.newvision.zeus.glasscore.protocol.netty.server.GlassTcpServer;
import com.newvision.zeus.glasscore.protocol.usb.accessory.GlassUsbAccessoryService;
import com.newvision.zeus.glassmanager.setting.wifi.WifiHelper;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by yanjiatian on 2017/7/5.
 */

public class GlassManagerApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initService();
    }

    private void initService() {
        startService(new Intent(this, GlassTcpClient.class)); //start tcp service
        startService(new Intent(this, GlassUsbAccessoryService.class));  //start usb service
        // init wifi helper
        WifiHelper.getInstance().init(this);
        initLeak();

    }

    //内存泄漏
    private void initLeak() {

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stopService(new Intent(this, GlassTcpServer.class));
    }

    public static Context getContext() {
        return mContext;
    }
}
