package com.newvision.zeus.glassmanager.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Qing Jiwei on 2016/11/12.
 */

public class BitmapUtils {

    private static final String TAG = "BitmapUtils";

    /**
     * 保存方法到本地
     */
    public static void saveBitmap(Bitmap bmp) {
        Log.i(TAG, "保存图片");

        File dir = getAlbumStorageDir("lenovo");
        // 拍照图片按照当前时间命名
        String filename = "CNN" + System.currentTimeMillis() + ".jpg";


        File f = new File(dir, filename);
        Log.i(TAG, "saveBitmap: f path=" + f.getAbsolutePath());
        if (f.exists()) {
            f.delete();
            Log.i(TAG, "saveBitmap: 删除");
        }
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
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
    public static void saveByte(byte[] bytes) {
        Log.i(TAG, "保存图片");

        File dir = getAlbumStorageDir("lenovo");
        // 拍照图片按照当前时间命名
        String filename = "CNN" + System.currentTimeMillis() + ".jpg";


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

    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
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
                    Log.i(TAG, "getAlbumStorageDir:Directory not created ");
                }
            }
            return dir;
        }
        return null;
    }


    public static byte[] rgb2YCbCr420(int[] pixels, int width, int height) {
        int len = width * height;
        // yuv格式数组大小，y亮度占len长度，u,v各占len/4长度。
        byte[] yuv = new byte[len * 3 / 2];
        int y, u, v;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // 屏蔽ARGB的透明度值
                int rgb = pixels[i * width + j] & 0x00FFFFFF;
                // 像素的颜色顺序为bgr，移位运算。
                int r = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 16) & 0xFF;
                // 套用公式
                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
                // rgb2yuv
                // y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                // u = (int) (-0.147 * r - 0.289 * g + 0.437 * b);
                // v = (int) (0.615 * r - 0.515 * g - 0.1 * b);
                // RGB转换YCbCr
                // y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                // u = (int) (-0.1687 * r - 0.3313 * g + 0.5 * b + 128);
                // if (u > 255)
                // u = 255;
                // v = (int) (0.5 * r - 0.4187 * g - 0.0813 * b + 128);
                // if (v > 255)
                // v = 255;
                // 调整
                y = y < 16 ? 16 : (y > 255 ? 255 : y);
                u = u < 0 ? 0 : (u > 255 ? 255 : u);
                v = v < 0 ? 0 : (v > 255 ? 255 : v);
                // 赋值
                yuv[i * width + j] = (byte) y;
                yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
                yuv[len + +(i >> 1) * width + (j & ~1) + 1] = (byte) v;
            }
        }
        return yuv;
    }

    public static void decodeYUV420SP(byte[] rgbBuf, byte[] yuv420sp,
                                      int width, int height) {

        final int frameSize = width * height;
        if (rgbBuf == null)

            throw new NullPointerException("buffer 'rgbBuf' is null");
        if (rgbBuf.length < frameSize * 3)

            throw new IllegalArgumentException("buffer 'rgbBuf' size "
                    + rgbBuf.length + " < minimum " + frameSize * 3);

        if (yuv420sp == null)

            throw new NullPointerException("buffer 'yuv420sp' is null");

        if (yuv420sp.length < frameSize * 3 / 2)

            throw new IllegalArgumentException("buffer 'yuv420sp' size "
                    + yuv420sp.length + " < minimum " + frameSize * 3 / 2);

        int i = 0, y = 0;

        int uvp = 0, u = 0, v = 0;

        int y1192 = 0, r = 0, g = 0, b = 0;

        for (int j = 0, yp = 0; j < height; j++) {
            uvp = frameSize + (j >> 1) * width;
            u = 0;
            v = 0;

            for (i = 0; i < width; i++, yp++) {
                y = (0xff & ((int) yuv420sp[yp])) - 16;

                if (y < 0)
                    y = 0;

                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                y1192 = 1192 * y;
                r = (y1192 + 1634 * v);
                g = (y1192 - 833 * v - 400 * u);
                b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;

                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;

                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;

                rgbBuf[yp * 3] = (byte) (r >> 10);
                rgbBuf[yp * 3 + 1] = (byte) (g >> 10);
                rgbBuf[yp * 3 + 2] = (byte) (b >> 10);
            }
        }
    }


    /*
     * 获取位图的RGB数据
     */
    public static byte[] getRGBByBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int size = width * height;

        int pixels[] = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        byte[] data = convertColorToByte(pixels);

        return data;
    }

    /*
 * 获取位图的YUV数据
 */
    public static byte[] getYUVByBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int size = width * height;

        int pixels[] = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // byte[] data = convertColorToByte(pixels);
        byte[] data = rgb2YCbCr420(pixels, width, height);

        return data;
    }

    /*
     * 像素数组转化为RGB数组
     */
    public static byte[] convertColorToByte(int color[]) {
        if (color == null) {
            return null;
        }

        byte[] data = new byte[color.length * 3];
        for (int i = 0; i < color.length; i++) {
            data[i * 3] = (byte) (color[i] >> 16 & 0xff);
            data[i * 3 + 1] = (byte) (color[i] >> 8 & 0xff);
            data[i * 3 + 2] = (byte) (color[i] & 0xff);
        }

        return data;

    }


}
