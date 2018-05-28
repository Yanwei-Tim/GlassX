package com.newvision.zeus.glasslauncher.service.installer;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by zhangsong on 10/26/15.
 */
public class AppInstallService extends Service {

    private static final String TAG = "AppInstallService";

    private static final int REQ_APP_UPGRADE = 101;
    private static final int RES_UPGRADE_START = 201;
    private static final int RES_UPGRADE_SUCCESS = 202;
    private static final int RES_UPGRADE_FAILED = 203;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return mMessenger.getBinder();
    }

    private final Messenger mMessenger = new Messenger(new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            if (msg.what == REQ_APP_UPGRADE) {
                Bundle b = msg.getData();
                String apkPath = b.getString("apk_path");
                Log.i(TAG, "Apk path : " + apkPath);

                AppInstaller.install(apkPath, new AppInstaller.InstallListener() {
                    @Override
                    public void onStart() {
                        replay(msg, RES_UPGRADE_START, 0);
                    }

                    @Override
                    public void onSuccess() {
                        replay(msg, RES_UPGRADE_SUCCESS, 0);
                    }

                    @Override
                    public void onFailed() {
                        replay(msg, RES_UPGRADE_FAILED, 0);
                    }
                });
            } else {
                super.handleMessage(msg);
            }
        }
    });

    private void replay(Message msg, int what, int arg1) {
        try {
            msg.replyTo.send(Message.obtain(null, what, arg1, 0));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
