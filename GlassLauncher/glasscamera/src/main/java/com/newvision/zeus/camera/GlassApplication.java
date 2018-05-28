package com.newvision.zeus.camera;

import android.app.Application;
import android.content.Context;

/**
 * Created by Qing Jiwei on 6/29/17.
 */


public class GlassApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }

}
