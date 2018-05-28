package com.newvision.zeus.glasslauncher.utils;

import android.widget.Toast;

import com.newvision.zeus.glasslauncher.LauncherApplication;


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
     *
     * @param context
     * @param text
     */
    public static void show(String text) {
        if (!isDebug) {
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(LauncherApplication.getContext(), text, Toast.LENGTH_SHORT);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(text);
        mToast.show();
    }

    /**
     * show a long toast
     *
     * @param context
     * @param text
     */
    public static void showLong(String text) {
        if (!isDebug) {
            return;
        }
        if (mToast == null) {
            mToast = Toast.makeText(LauncherApplication.getContext(), text, Toast.LENGTH_LONG);
        }
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setText(text);
        mToast.show();
    }
}
