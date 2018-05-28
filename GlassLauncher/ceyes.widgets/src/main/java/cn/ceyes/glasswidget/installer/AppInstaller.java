package cn.ceyes.glasswidget.installer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by zhangsong on 17-7-11.
 */

public class AppInstaller {
    public interface InstallListener {
        void onStart();

        void onFinish();

        void onError();
    }

    private static final String TAG = "AppInstaller";

    private static final int REQ_APP_UPGRADE = 101;
    private static final int RES_UPGRADE_START = 201;
    private static final int RES_UPGRADE_SUCCESS = 202;
    private static final int RES_UPGRADE_FAILED = 203;

    private Context context;

    private InstallListener installListener;

    private Messenger msgSender;
    private Messenger msgReceiver;

    public static AppInstaller init(Context context) {
        if (context == null)
            throw new IllegalArgumentException("Context can not be null.");

        return new AppInstaller(context);
    }

    public void install(String path, InstallListener listener) throws RemoteException {
        if (TextUtils.isEmpty(path) || !path.endsWith(".apk")) {
            throw new IllegalArgumentException("The apk path is null or not a apk file, file: " + path);
        }

        if (!new File(path).exists()) {
            throw new IllegalArgumentException("Target file does not exist, path: " + path);
        }

        if (msgSender == null) {
            throw new IllegalStateException("Bind the remote service failed, please check!");
        }

        installListener = listener;

        Message msg = handler.obtainMessage(REQ_APP_UPGRADE);
        Bundle b = new Bundle();
        b.putString("apk_path", path);
        msg.setData(b);
        msg.replyTo = msgReceiver;
        msgSender.send(new Message());
    }

    public void destroy() {
        this.context.unbindService(serviceConn);
    }

    private AppInstaller(Context context) {
        this.context = context;

        msgReceiver = new Messenger(handler);

        Intent i = new Intent();
        i.setComponent(new ComponentName("com.newvision.zeus.glasslauncher",
                "com.newvision.zeus.glasslauncher.service.installer.AppInstallService"));

        this.context.bindService(i, serviceConn, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msgSender = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            msgSender = null;
            msgReceiver = null;
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (installListener == null) return;

            switch (msg.what) {
                case RES_UPGRADE_START:
                    installListener.onStart();
                    break;
                case RES_UPGRADE_SUCCESS:
                    installListener.onFinish();
                    break;
                case RES_UPGRADE_FAILED:
                    installListener.onError();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
}
