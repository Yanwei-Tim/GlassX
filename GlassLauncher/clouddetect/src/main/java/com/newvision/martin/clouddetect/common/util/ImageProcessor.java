package com.newvision.martin.clouddetect.common.util;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

/**
 * Created by zhangsong on 17-3-27.
 */

public class ImageProcessor {

    static {
        System.loadLibrary("native-lib");
    }

    // 640 x 480 - 70ms
    public static native void I420ToRGB(int[] dts, byte[] i420, int width, int height);

    // 640 x 480 - 44ms
    public static native void YUV420SPToRGB(int[] dts, byte[] yuv420sp, int width, int height);

    /**
     * 640x480 - 179ms
     * <p>
     * YUV数据转换为rgba格式，YUV数据格式为{@link android.graphics.ImageFormat#NV21}.
     * 该格式YUV数据Y、U、V分量的排列顺序为：YYYYYYYYUVUV
     *
     * @param yuv420sp YUV格式数据
     * @param width    图像的宽度
     * @param height   图像的高度
     * @return
     */
    public static int[] YUV420SPToRGB(byte[] yuv420sp, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {

                int y = (0xff & ((int) yuv420sp[i * width + j]));
                int u = (0xff & ((int) yuv420sp[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) yuv420sp[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }

        return rgba;
    }

    /**
     * 640 x 480 - 150ms
     * <p>
     * YUV数据转换为rgba格式，YUV数据格式为{@link android.graphics.ImageFormat#YUV_420_888}.
     * 该格式YUV数据Y、U、V分量的排列顺序为：YYYYYYYYVVUU
     *
     * @param i420   YUV格式数据
     * @param width  图像的宽度
     * @param height 图像的高度
     * @return
     */
    public static int[] I420ToRGB(byte[] i420, int width, int height) {
        int numOfPixel = width * height;
        int positionOfV = numOfPixel;
        int positionOfU = numOfPixel / 4 + numOfPixel;
        int[] rgba = new int[numOfPixel];
        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = (i / 2) * (width / 2);
            int startU = positionOfV + step;
            int startV = positionOfU + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int U = startU + j / 2;
                int V = startV + j / 2;

                int r = (int) ((i420[Y] & 0xff) + 1.4075 * ((i420[V] & 0xff) - 128));
                int g = (int) ((i420[Y] & 0xff) - 0.3455 * ((i420[U] & 0xff) - 128) - 0.7169 * ((i420[V] & 0xff) - 128));
                int b = (int) ((i420[Y] & 0xff) + 1.779 * ((i420[U] & 0xff) - 128));
                r = (r < 0 ? 0 : r > 255 ? 255 : r);
                g = (g < 0 ? 0 : g > 255 ? 255 : g);
                b = (b < 0 ? 0 : b > 255 ? 255 : b);

                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }
        }

        return rgba;
    }

    /**
     * 640 x 480 - 80ms
     * <p>
     * 大约是{@link #cropImage2}方法速度的1/8.
     *
     * @param src            rgba int 数组
     * @param originalWidth  原始图像的宽度
     * @param originalHeight 原始图像的高度
     * @param x              裁剪图像的x坐标
     * @param y              裁剪图像的y坐标
     * @param width          裁剪图像的宽度
     * @param height         裁剪兔小的高度
     * @return
     */
    public static int[] cropImage(int[] src, int originalWidth, int originalHeight, int x, int y, int width, int height) {
        int[] crop = new int[width * height];
        for (int i = 0; i < originalHeight; i++) {
            if (i < y) continue;
            if (i > (height + y - 1)) break;
            for (int j = 0; j < originalWidth; j++) {
                if (j < x) continue;
                if (j > (width + x - 1)) break;

                crop[(i - y) * width + (j - x)] = src[i * originalWidth + j];
            }
        }
        return crop;
    }

    /**
     * 640 x 480 - 10ms
     * <p>
     * 大约是{@link #cropImage} 方法速度的8倍。
     *
     * @param src            rgba int 数组
     * @param originalWidth  原始图像的宽度
     * @param originalHeight 原始图像的高度
     * @param x              裁剪图像的x坐标
     * @param y              裁剪图像的y坐标
     * @param width          裁剪图像的宽度
     * @param height         裁剪兔小的高度
     * @return
     */
    public static int[] cropImage2(int[] src, int originalWidth, int originalHeight, int x, int y, int width, int height) {
        int[] crop = new int[width * height];

        for (int i = 0; i < originalHeight; i++) {
            if (i < y) continue;
            if (i > (height + y - 1)) break;

            System.arraycopy(src, i * originalWidth + x, crop, (i - y) * width, width);
        }

        return crop;
    }

    // 640 x 480 - 9ms
    public static Bitmap toBitmap(int[] rgba, int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        return bmp;
    }

    public static Bitmap toBitmap(byte[] rgb, int width, int height) {
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmp.copyPixelsFromBuffer(ByteBuffer.wrap(rgb));
        return bmp;
    }
}
