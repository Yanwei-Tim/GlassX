package com.newvision.zeus.glasslauncher.launcher;

import android.content.Context;
import android.util.Log;

import cn.ceyes.glasswidget.cardview.GlassCardManager;
import cn.ceyes.glasswidget.cardview.IGlassEventListener;

/**
 * Created by zhangsong on 17-6-29.
 */

public class LauncherCardManager extends GlassCardManager {
    private static final String TAG = "LauncherCardManager";

    public LauncherCardManager(Context context, IGlassEventListener listener) {
        super(context, listener);
    }

    @Override
    public boolean onGlassEvent(int eventCode, Object event) {
        Log.i(TAG, "onGlassEvent: code: " + eventCode + ", event: " + event);
        return super.onGlassEvent(eventCode, event);
    }
}
