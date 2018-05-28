package com.newvision.martin.clouddetect.app;

import android.app.Application;
import android.content.Context;

import com.newvision.martin.clouddetect.common.helper.NetworkStateWatcher;
import com.newvision.martin.clouddetect.common.helper.ScreenStateWatcher;

/**
 * Created by zhangsong on 16-11-24.
 */

public class HomeApplication extends Application {
    private static final String TAG = "HomeApplication";

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        NetworkStateWatcher.getInstance().init(this);
        ScreenStateWatcher.getInstance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetworkStateWatcher.getInstance().deInit();
        ScreenStateWatcher.getInstance().deInit();
    }
}
