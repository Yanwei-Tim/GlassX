package com.newvision.zeus.glasslauncher.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.newvision.zeus.glasscore.base.IBindUsbServiceStatusListener;
import com.newvision.zeus.glasscore.helper.PhoneRequestHelper;
import com.newvision.zeus.glasscore.protocol.entity.GlassConstants;
import com.newvision.zeus.glasscore.protocol.entity.GlassErrorCode;
import com.newvision.zeus.glasscore.protocol.entity.GlassMessageType;
import com.newvision.zeus.glasscore.base.GlassCoreServerActivity;
import com.newvision.zeus.glasscore.protocol.entity.IMessageCallback;
import com.newvision.zeus.glasslauncher.launcher.camera.CameraCardProvider;
import com.newvision.zeus.glasslauncher.common.pm.AppInfo;
import com.newvision.zeus.glasslauncher.common.pm.GlassPackageManager;
import com.newvision.zeus.glasslauncher.launcher.apphub.AppHubCardProvider;
import com.newvision.zeus.glasslauncher.launcher.home.HomeCardProvider;
import com.newvision.zeus.glasslauncher.launcher.settings.SettingsCardProvider;
import com.newvision.zeus.glasslauncher.settings.SettingsActivity;
import com.newvision.zeus.glasslauncher.test.CameraUsbTestActivity;

import java.util.List;

import cn.ceyes.glasswidget.cardview.GlassCardListView;
import cn.ceyes.glasswidget.cardview.IGlassEventListener;

/**
 * Created by zhangsong on 17-6-29.
 */

public class LauncherActivity extends GlassCoreServerActivity {
    private static final String TAG = LauncherActivity.class.getSimpleName();

    GlassCardListView glassCardListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LauncherCardManager manager = new LauncherCardManager(this, new IGlassEventListener() {
            @Override
            public boolean onGlassEvent(int eventCode, Object event) {
                Log.i(TAG, "onGlassEvent: code: " + eventCode + ", event: " + event);
                switch (eventCode) {
                    case 20000:
                        startActivity(new Intent(LauncherActivity.this, SettingsActivity.class));
                        break;
                    case 21000:
                        if (mServerServiceBinder.getLoginGlassStatus()) {
//                            GlassUsbAskUtils.sendOpenAppAsk(mUsbServiceBinder, GlassConstants.INNER_APP_CAMERA);
                            PhoneRequestHelper.getInstance().openInternalApp(mUsbServiceBinder, GlassConstants.INNER_APP_CAMERA);
                        }
                        break;
                    case 30000:
                        AppInfo info = (AppInfo) event;
                        startActivity(info.getIntent());
                        break;
                }
                return false;
            }
        });

        manager.activateGlassCard(new HomeCardProvider());
        manager.activateGlassCard(new SettingsCardProvider());
        manager.activateGlassCard(new CameraCardProvider());

        glassCardListView = new GlassCardListView(this);
        glassCardListView.init(manager);
        setContentView(glassCardListView);

        List<AppInfo> appInfos = GlassPackageManager.getInstance(this).getSortedAppInfos();
        for (int i = 0; i < appInfos.size(); i++) {
            AppInfo info = appInfos.get(i);
            Log.i(TAG, "Matched app info: " + info.toString());
            manager.activateGlassCard(new AppHubCardProvider().init("app_card_" + i, info));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        bindTcpService(new IBindServiceStatusListener() {
//            @Override
//            public void bindSuccess() {
//                Log.i(TAG, "bindSuccess: ");
//                mServerServiceBinder.registerListener(callback);
//
//            }
//
//            @Override
//            public void unbindSuccess() {
//                Log.i(TAG, "unbindSuccess: ");
//                mServerServiceBinder.unregisterListener(callback);
//            }
//        });
        bindUsbService(new IBindUsbServiceStatusListener() {
            @Override
            public void bindSuccess() {
                Log.i(TAG, "bindSuccess: ");
                mUsbServiceBinder.registerListener(callback);
            }

            @Override
            public void unbindSuccess() {
                Log.i(TAG, "unbindSuccess: ");
                mUsbServiceBinder.unregisterListener(callback);
            }
        });
    }

    private IMessageCallback callback = new IMessageCallback() {
        @Override
        public void getResult(int tag, Object result) {
//            Log.i(TAG, "getResult: message type=" + Integer.toHexString(tag));
            switch (tag) {
                case GlassMessageType.GLASS_OPEN_APP_ANS:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
                        startActivity(new Intent(LauncherActivity.this, CameraUsbTestActivity.class));  //for usb test
                    }
                    break;
                case GlassMessageType.PHONE_OPEN_APP_ASK:
                    if (((String) result).equals(GlassConstants.INNER_APP_CAMERA)) {
//                        GlassAskUtils.sendOpenAppAns(mServerServiceBinder, GlassConstants.INNER_APP_CAMERA);
                        PhoneRequestHelper.getInstance().openInternalAppAns(mServerServiceBinder,GlassConstants.INNER_APP_CAMERA, GlassErrorCode.OK);
                        startActivity(new Intent(LauncherActivity.this, CameraUsbTestActivity.class));   //for usb test
                    }
                    break;
            }
        }
    };
}
