package com.newvision.zeus.glasslauncher.common.helper;

import android.content.Context;
import android.os.Build;

import com.newvision.zeus.glasslauncher.R;

/**
 * Created by zhangsong on 17-7-10.
 */

public class VersionProvider {
    private static final String TAG = "VersionProvider";

    private static VersionProvider instance;

    private Context mContext;

    public static VersionProvider getInstance(Context context) {
        if (instance == null) {
            synchronized (VersionProvider.class) {
                if (instance == null) {
                    instance = new VersionProvider();
                }
            }
        }
        return instance;
    }

    public String getVersion() {
        String display = Build.DISPLAY;
        String version = null;
        try {
            String currVersion = display.substring(display.indexOf("S") + 1, display.indexOf("D"));
            if (isDebugVersion()) {
                version = mContext.getResources().getString(R.string.lable_version_debug)
                        + " " + formatVersionSign(currVersion);
            } else {
                version = mContext.getResources().getString(R.string.fota_name_current_version)
                        + " " + formatVersionSign(currVersion);
                ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public boolean isDebugVersion() {
        return Build.DISPLAY.startsWith("eng");
    }

    private VersionProvider() {
    }

    public String formatVersionSign(String version) {
        return "V 1.0";
    }
}
