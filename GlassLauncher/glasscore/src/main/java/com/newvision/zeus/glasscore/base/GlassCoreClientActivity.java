package com.newvision.zeus.glasscore.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.newvision.zeus.glasscore.protocol.netty.client.GlassTcpClient;
import com.newvision.zeus.glasscore.protocol.usb.accessory.GlassUsbAccessoryService;

/**
 * Created by yanjiatian on 2017/7/6.
 */

public class GlassCoreClientActivity extends AppCompatActivity {
    public GlassTcpClient.ClientServiceBinder mClientServiceBinder;
    private IBindServiceStatusListener mBindServiceStatusListener;

    public GlassUsbAccessoryService.AccessoryServiceBinder mAccessoryBinder;
    private IBindUsbServiceStatusListener mBindUsbServiceStatusListener;

    public void bindTcpService(IBindServiceStatusListener listener) {
        bindService(new Intent(this, GlassTcpClient.class), mTcpConnection, Context.BIND_AUTO_CREATE);
        this.mBindServiceStatusListener = listener;
    }

    public void unbindTcpService() {
        if (mClientServiceBinder != null) {
            unbindService(mTcpConnection);
        }
        mBindServiceStatusListener = null;
    }

    private ServiceConnection mTcpConnection = new ServiceConnection() {
        // 当与service的连接建立后被调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mClientServiceBinder = (GlassTcpClient.ClientServiceBinder) service;
            if (mBindServiceStatusListener != null) {
                mBindServiceStatusListener.bindSuccess();
            }
        }

        // 当与service的连接意外断开时被调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBindServiceStatusListener.unbindSuccess();
        }
    };

    public void bindUsbService(IBindUsbServiceStatusListener listener) {
        bindService(new Intent(this, GlassUsbAccessoryService.class), mUsbConnection, Context.BIND_AUTO_CREATE);
        this.mBindUsbServiceStatusListener = listener;
    }

    public void unbindUsbService() {
        if (mAccessoryBinder != null) {
            unbindService(mUsbConnection);
        }
        mBindUsbServiceStatusListener = null;
    }

    private ServiceConnection mUsbConnection = new ServiceConnection() {
        // 当与service的连接建立后被调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAccessoryBinder = (GlassUsbAccessoryService.AccessoryServiceBinder) service;
            if (mBindUsbServiceStatusListener != null) {
                mBindUsbServiceStatusListener.bindSuccess();
            }
        }

        // 当与service的连接意外断开时被调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBindUsbServiceStatusListener.unbindSuccess();
        }
    };

}
