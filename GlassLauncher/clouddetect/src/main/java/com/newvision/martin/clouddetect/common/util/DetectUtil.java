package com.newvision.martin.clouddetect.common.util;

import android.os.Build;

/**
 * Created by zhangsong on 17-4-28.
 */

public class DetectUtil {
    public static String getId() {
        String serial = Build.SERIAL;
        String id = "";
        if (serial.length() > 7) {
            id = serial.substring(serial.length() - 8);
        } else {
            int emptyNum = 8 - serial.length();
            for (int i = 0; i < emptyNum; i++) {
                id += "0";
            }
            id += serial;
        }
        return id;
    }

    public static String getUserId() {
        return "123";
    }
}
