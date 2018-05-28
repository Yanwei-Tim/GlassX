package com.newvision.zeus.glassmanager.utils;

import android.content.res.Resources;

import com.newvision.zeus.glassmanager.GlassManagerApp;

/**
 * Created by yanjiatian on 2017/9/8.
 */

public class ResourceUtils {
    public static String getString(int resId) {
        Resources res = GlassManagerApp.getContext().getResources();
        return res.getString(resId);
    }
}
