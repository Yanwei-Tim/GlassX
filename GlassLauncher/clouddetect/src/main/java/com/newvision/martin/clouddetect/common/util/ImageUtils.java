package com.newvision.martin.clouddetect.common.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zhangsong on 17-4-25.
 */

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    static {
        System.loadLibrary("imageutil");
    }

    /**
     * Saves a Bitmap object to disk for analysis.
     *
     * @param bitmap The bitmap to save.
     */
    public static void saveBitmap(final Bitmap bitmap) {
        saveBitmap(bitmap, "preview.png");
    }

    /**
     * Saves a Bitmap object to disk for analysis.
     *
     * @param bitmap   The bitmap to save.
     * @param filename The location to save the bitmap to.
     */
    public static void saveBitmap(final Bitmap bitmap, final String filename) {
        final String root =
                Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "martin";
        Log.i(TAG, String.format("Saving %dx%d bitmap to %s.", bitmap.getWidth(), bitmap.getHeight(), root));
        final File myDir = new File(root);

        if (!myDir.mkdirs()) {
            Log.i(TAG, "Make dir failed");
        }

        final String fname = filename;
        final File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }
        try {
            final FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 99, out);
            out.flush();
            out.close();
        } catch (final Exception e) {
            Log.i(TAG, "Exception!");
        }
    }

    /**
     * Converts YUV420 semi-planar data to ARGB 8888 data using the supplied width
     * and height. The input and output must already be allocated and non-null.
     * For efficiency, no error checking is performed.
     *
     * @param input    The array of YUV 4:2:0 input data.
     * @param output   A pre-allocated array for the ARGB 8:8:8:8 output data.
     * @param width    The width of the input image.
     * @param height   The height of the input image.
     * @param halfSize If true, downsample to 50% in each dimension, otherwise not.
     */
    public static native void convertYUV420SPToARGB8888(
            byte[] input, int[] output, int width, int height, boolean halfSize);

    /**
     * Converts YUV420 semi-planar data to ARGB 8888 data using the supplied width
     * and height. The input and output must already be allocated and non-null.
     * For efficiency, no error checking is performed.
     *
     * @param y
     * @param u
     * @param v
     * @param uvPixelStride
     * @param width         The width of the input image.
     * @param height        The height of the input image.
     * @param halfSize      If true, downsample to 50% in each dimension, otherwise not.
     * @param output        A pre-allocated array for the ARGB 8:8:8:8 output data.
     */
    public static native void convertYUV420ToARGB8888(
            byte[] y,
            byte[] u,
            byte[] v,
            int[] output,
            int width,
            int height,
            int yRowStride,
            int uvRowStride,
            int uvPixelStride,
            boolean halfSize);

    /**
     * Converts YUV420 semi-planar data to RGB 565 data using the supplied width
     * and height. The input and output must already be allocated and non-null.
     * For efficiency, no error checking is performed.
     *
     * @param input  The array of YUV 4:2:0 input data.
     * @param output A pre-allocated array for the RGB 5:6:5 output data.
     * @param width  The width of the input image.
     * @param height The height of the input image.
     */
    public static native void convertYUV420SPToRGB565(
            byte[] input, byte[] output, int width, int height);

    /**
     * Converts 32-bit ARGB8888 image data to YUV420SP data.  This is useful, for
     * instance, in creating data to feed the classes that rely on raw camera
     * preview frames.
     *
     * @param input  An array of input pixels in ARGB8888 format.
     * @param output A pre-allocated array for the YUV420SP output data.
     * @param width  The width of the input image.
     * @param height The height of the input image.
     */
    public static native void convertARGB8888ToYUV420SP(
            int[] input, byte[] output, int width, int height);

    /**
     * Converts 16-bit RGB565 image data to YUV420SP data.  This is useful, for
     * instance, in creating data to feed the classes that rely on raw camera
     * preview frames.
     *
     * @param input  An array of input pixels in RGB565 format.
     * @param output A pre-allocated array for the YUV420SP output data.
     * @param width  The width of the input image.
     * @param height The height of the input image.
     */
    public static native void convertRGB565ToYUV420SP(
            byte[] input, byte[] output, int width, int height);
}
