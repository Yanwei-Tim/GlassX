package com.newvision.zeus.glassmanager.common.decoder;

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
