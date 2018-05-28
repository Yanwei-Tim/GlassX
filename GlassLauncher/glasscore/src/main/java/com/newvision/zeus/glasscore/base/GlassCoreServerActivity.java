package com.newvision.zeus.glasscore.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.newvision.zeus.glasscore.protocol.netty.server.GlassTcpServer;
import com.newvision.zeus.glasscore.protocol.usb.host.GlassUsbHostService;

/**
 * Created by yanjiatian on 2017/7/6.
 */

public class GlassCoreServerActivity extends AppCompatActivity {
    public GlassTcpServer.ServerServiceBinder mServerServiceBinder;
    private IBindServiceStatusListener mBindServiceStatusListener;
    public GlassUsbHostService.HostServiceBinder mUsbServiceBinder;
    private IBindUsbServiceStatusListener mBindUsbServiceStatusListener;

    public void bindTcpService(IBindServiceStatusListener listener) {
        bindService(new Intent(this, GlassTcpServer.class), mTcpConnection, Context.BIND_AUTO_CREATE);
        this.mBindServiceStatusListener = listener;
    }

    public void unbindTcpService() {
        if (mServerServiceBinder != null) {
            unbindService(mTcpConnection);
        }
        mBindServiceStatusListener = null;
    }

    private ServiceConnection mTcpConnection = new ServiceConnection() {
        // 当与service的连接建立后被调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServerServiceBinder = (GlassTcpServer.ServerServiceBinder) service;
            if(mBindServiceStatusListener != null){
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
        bindService(new Intent(this, GlassUsbHostService.class), mUsbConnection, Context.BIND_AUTO_CREATE);
        this.mBindUsbServiceStatusListener = listener;
    }

    public void unbindUsbService() {
        if (mUsbServiceBinder != null) {
            unbindService(mUsbConnection);
        }
        mBindUsbServiceStatusListener = null;
    }

    private ServiceConnection mUsbConnection = new ServiceConnection() {
        // 当与service的连接建立后被调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mUsbServiceBinder = (GlassUsbHostService.HostServiceBinder) service;
            if(mBindUsbServiceStatusListener != null){
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
