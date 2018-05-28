package com.newvision.zeus.glasslauncher.common.helper;

import java.text.SimpleDateFormat;

/**
 * Created by zhangsong on 17-6-29.
 */

public class TimeFormatter {
    private static final String TAG = "TimeFormatter";

    private static TimeFormatter instance;

    private SimpleDateFormat dateFormat;

    public static TimeFormatter getInstance(String patten) {
        if (instance == null) {
            synchronized (TimeFormatter.class) {
                if (instance == null) {
                    instance = new TimeFormatter(patten);
                }
            }
        }
        return instance;
    }

    public String format(Object obj) {
        return dateFormat.format(obj);
    }

    private TimeFormatter(String patten) {
        dateFormat = new SimpleDateFormat(patten);
    }
}
