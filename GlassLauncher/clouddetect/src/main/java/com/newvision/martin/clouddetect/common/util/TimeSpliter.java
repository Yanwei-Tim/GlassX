package com.newvision.martin.clouddetect.common.util;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangsong on 17-4-25.
 */

public class TimeSpliter {
    private static final String TAG = "TimeSpliter";

    private static final boolean DEBUG = true;

    private static TimeSpliter instance = new TimeSpliter();

    private Map<String, Long> spliters = null;

    public static TimeSpliter getInstance() {
        return instance;
    }

    public void start() {
        if (!DEBUG) return;

        spliters = new HashMap<>();

        long start = System.currentTimeMillis();
        spliters.put("start", start);
    }

    public long count(String currentTag, String startTag) {
        if (!DEBUG) return 0;

        long now = System.currentTimeMillis();

        spliters.put(currentTag, now);

        if (TextUtils.isEmpty(startTag) || !spliters.containsKey(startTag))
            return now - spliters.get("start");

        return now - spliters.get(startTag);
    }

    public void end() {
        if (!DEBUG) return;

        spliters.clear();
        spliters = null;
    }

    private TimeSpliter() {
    }
}
