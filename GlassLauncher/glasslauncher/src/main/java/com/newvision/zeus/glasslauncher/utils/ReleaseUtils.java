package com.newvision.zeus.glasslauncher.utils;

import com.newvision.zeus.glasslauncher.common.decoder.ReleaseAble;

/**
 * Created by Qing Jiwei on 10/25/17.
 */


public class ReleaseUtils {

    public static void releaseQuietly(ReleaseAble releaseAble) {

        if (releaseAble != null) {
            try {
                releaseAble.startRelease();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
