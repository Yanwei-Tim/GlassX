package com.newvision.zeus.glasslauncher.service.installer;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by zhangsong on 10/26/15.
 */
class AppInstaller {

    public interface InstallListener {
        void onStart();

        void onSuccess();

        void onFailed();
    }

    private static final String TAG = "AppInstaller";

    public static void install(final String apkPath, final InstallListener listener) {
        String result = "";
        try {
            if (listener != null)
                listener.onStart();

            String[] args = {"pm", "install", apkPath};
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            Process process = null;
            InputStream errIs = null;
            InputStream inIs = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            data = baos.toByteArray();
            result = new String(data).replace("\n", " ");
            Log.i(TAG, "install() result:" + result);
            if (result.contains("Success")) {
                if (listener != null)
                    listener.onSuccess();
            } else {
                if (listener != null)
                    listener.onFailed();
            }
        } catch (Exception e) {
            result = e.getMessage();
            Log.i(TAG, "install() failed:" + result);
            if (listener != null)
                listener.onFailed();
        }
    }

    private AppInstaller() {
    }
}
