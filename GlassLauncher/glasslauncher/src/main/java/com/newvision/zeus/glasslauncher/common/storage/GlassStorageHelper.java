package com.newvision.zeus.glasslauncher.common.storage;

import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * Created by yanjiatian on 15/1/22.
 */
public class GlassStorageHelper {

    private static final String TAG = "GlassStorageHelper";

    private static final int SYSTEM_RESERVE_SPACE = 200;
    public static float innerStorageRemainingSpace = 20.f;
    public static float extendStorageRemainingSpace = 20.f;

    public static int TAKE_PICTURE_RESERVED_SPACE = 10;
    public static int RECORD_VIDEO_RESERVED_SPACE = 20;
    private static final int STORAGE_MOUNTED = 0;
    private static final int STORAGE_EJECT = 1;
    public static int storageMountStatus = STORAGE_EJECT;
    private static long lastCalculateStorageTime = 0;
    private static long CalculateIntervalTime = 20 * 1000;

    private static final String INNER_STORAGE_PATH = "/mnt/sdcard/";
    private static final String EXTEND_STORAGE_PATH = "/mnt/ext_sdcard";

    private static GlassStorageHelper sharedInstance = new GlassStorageHelper();

    public static GlassStorageHelper getInstance() {
        return sharedInstance;
    }

    public void updateStorageInfo() {
        Log.i(TAG, "calculate storage space start!!!");
        if ((System.currentTimeMillis() - lastCalculateStorageTime) < CalculateIntervalTime) {
            lastCalculateStorageTime = System.currentTimeMillis();
            return;
        }
        String[] storageLists = getExtendCardPath();
        innerStorageRemainingSpace = (getAvailableStorageSize(INNER_STORAGE_PATH) / (1024 * 1024)) > SYSTEM_RESERVE_SPACE ? (getAvailableStorageSize(INNER_STORAGE_PATH) / (1024 * 1024)) - SYSTEM_RESERVE_SPACE : 0; //系统预留200M空间

//        if (storageLists.length > 1) {
//            extendStorageRemainingSpace = getAvailableStorageSize(EXTEND_STORAGE_PATH) / (1024 * 1024);
//        }
        Log.i(TAG, "calculate storage space end!!!");

    }

    public String getInnerStorageSpace() {
        if (innerStorageRemainingSpace > 1024) {
            return formatStorage(innerStorageRemainingSpace / 1024) + "GB";
        } else {
            return formatStorage(innerStorageRemainingSpace) + "MB";
        }
    }

//    public String getExtendStorageSpace() {
//        if (extendStorageRemainingSpace > 1024) {
//            return "SD卡：" + formatStorage(extendStorageRemainingSpace / 1024) + "GB";
//        } else {
//            return "SD卡：" + formatStorage(extendStorageRemainingSpace) + "MB";
//        }
//    }

    /**
     * 外部存储是否可用
     *
     * @return
     */
    public boolean externalStorageAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 通过Runtime来获取存在的存储路径 如果没有安装TF卡，就会得到/mnt/sdcard这个路径
     * 如果安装了TF卡，将会得到一个路径数组，分别为/mnt/ext_sdcard 和/mnt/sdcard
     *
     * @return
     */
    private String[] getExtendCardPath() {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            String mount = new String();
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;

                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat("*" + columns[1] + "\n");
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        mount = mount.concat(columns[1] + "\n");
                    }
                }
            }
            Log.d("sdcardmemory", "sdcard 路径：" + mount);
            mount = mount.replace("*", "");
            String[] cards = mount.split("\\n");
            Log.d("sdcard", "count:" + cards.length);
            return cards;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取可用空间大小
     *
     * @return 单位 bit
     */
    private long getAvailableStorageSize(String path) {
        if (externalStorageAvailable()) {
            StatFs stat = new StatFs(path);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    private String formatStorage(float formatStr) {
        // 如果小数不足2位,会以0补足.
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        if (formatStr == 0) {
            return "0";
        }
        return decimalFormat.format(formatStr);
    }

    private GlassStorageHelper() {
        updateStorageInfo();
    }
}
