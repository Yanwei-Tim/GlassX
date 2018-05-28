package com.newvision.zeus.glasslauncher.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Qing Jiwei on 6/29/17.
 */

public class SaveLocalUtils {

    private static final String TAG = "SaveLocalUtils";

    /**
     * 保存方法到本地
     */
    public static void savePicture(Bitmap bmp) {

        File dir = getAlbumStorageDir("lenovo");
        // 拍照图片按照当前时间命名
        String filename = "P" + DateUtils.getcurrentTime() + ".jpg";
        File file = new File(dir, filename);

        if (file.exists()) {
            file.delete();
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 保存方法到本地
     */
    public static void savePicture(byte[] bytes) {
        Log.i(TAG, "保存图片");

        File dir = getAlbumStorageDir("lenovo");
        String filename = "P" + DateUtils.getcurrentTime() + ".jpg";


        File f = new File(dir, filename);
        Log.i(TAG, "saveBitmap: f path=" + f.getAbsolutePath());
        if (f.exists()) {
            f.delete();
            Log.i(TAG, "saveBitmap: 删除");
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
            bos.write(bytes, 0, bytes.length);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static File getAlbumStorageDir(String albumName) {

        // Get the directory for the user's public pictures directory.
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在

        if (sdCardExist) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + albumName;

            File dir = new File(path);
            if (!dir.exists()) {//判断文件目录是否存在
                if (!dir.mkdirs()) {
                }
            }
            return dir;
        }
        return null;
    }

    /**
     * Access to local storage path
     *
     * @return
     */
    public static String getRecordPath() {

        // Get the directory for the user's public pictures directory.
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        if (sdCardExist) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lenovo";
            File dir = new File(path);
            if (!dir.exists()) {//判断文件目录是否存在
                if (!dir.mkdirs()) {
                }
            }

            Log.i(TAG, "getRecordPath: getAbsolutePath = " + dir.getAbsolutePath());
            String filepath = dir.getAbsolutePath() + "/V" + DateUtils.getcurrentTime() + ".MP4";

            Log.i(TAG, "getRecordPath: filepath=" + filepath);

            File file = new File(filepath);
            if (file.exists()) {//判断文件目录是否存在
                dir.delete();
            }

            return filepath;
        }

        return "/sdcard/" + DateUtils.getcurrentTime() + ".MP4";
    }
}
