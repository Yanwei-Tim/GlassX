package com.newvision.zeus.glassmanager.utils;

/**
 * Created by Qing Jiwei on 8/31/17.
 */


public class H264Utils {

    /**
     * parse frame type
     * 0 means sps frame
     *
     * @param buffer
     * @return
     */
    private static int parseFrameType(byte[] buffer) {

        // 00 00 00 01
        if (buffer[0] == 0 && buffer[1] == 0
                && buffer[2] == 0 && buffer[3] == 1) {
            if ((buffer[4] & 0x1f) == 7) {
                return 0;
            } else {
                return -1;
            }
        }
        return -1;
    }
}
