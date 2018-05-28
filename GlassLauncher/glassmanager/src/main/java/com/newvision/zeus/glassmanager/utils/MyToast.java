package com.newvision.zeus.glassmanager.utils;

import android.widget.Toast;

import com.newvision.zeus.glassmanager.GlassManagerApp;


/**
 * Toast 工具类
 *
 * @author Qing
 */
public class MyToast {

    private static Toast mToast;
    public static boolean isDebug = true;

    /**
     * show a toast
     * @param text
     */
    public static void show(String text) {
        if (!isDebug) {
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(GlassManagerApp.getContext(), text, Toast.LENGTH_SHORT);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(text);
        mToast.show();
    }

    /**
     * show a long toast
     * @param text
     */
    public static void showLong(String text) {
        if (!isDebug) {
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(GlassManagerApp.getContext(), text, Toast.LENGTH_LONG);
        }
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setText(text);
        mToast.show();
    }
}
