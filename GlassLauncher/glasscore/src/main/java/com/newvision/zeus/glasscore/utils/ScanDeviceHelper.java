package com.newvision.zeus.glasscore.utils;

import android.content.Context;
import android.util.Log;

import com.newvision.zeus.glasscore.protocol.entity.IScanIPFinishListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yanjiatian on 2017/7/10.
 */

public class ScanDeviceHelper {
    private static final String TAG = ScanDeviceHelper.class.getSimpleName();
    private static ScanDeviceHelper mInstance;

    private static final int CORE_POOL_SIZE = 1; //核心池大小
    private static final int MAX_THREAD_POOL_SIZE = 255;  //线程池最大线程数

    private String mDeviceAddress; //本机IP地址
    private String mLocalAddressIndex; //局域网IP地址头，如 "192.168.0."
    private Runtime mRuntime = Runtime.getRuntime(); //获取当前运行环境，来执行ping ，相当于windows的cmd

    private Process mProcess = null; //进程
    private String mPing = "ping -c 1 -w 3 "; //其中 -c 1为发送的次数，-w 表示发送后等待响应的时间

    private List<String> mIpList = new ArrayList<String>();

    private ThreadPoolExecutor mExecutor; //线程池对象


    public static ScanDeviceHelper getInstance() {
        if (mInstance == null) {
            mInstance = new ScanDeviceHelper();
        }
        return mInstance;
    }

    /**
     * 扫描局域网内IP，找到对应服务器
     */
    public void scan(Context context, IScanIPFinishListener listener, boolean isSharing) {
        if (isSharing) {
            mDeviceAddress = "192.168.43.1";
            mLocalAddressIndex = "192.168.43.";
        } else {
            mDeviceAddress = IPAddressUtils.getIpAddress(context);
            mLocalAddressIndex = IPAddressUtils.getLocalAddressIndex(mDeviceAddress);
        }

        Log.d(TAG, "开始扫描设备，本机IP地址为：" + mDeviceAddress);

        if (mLocalAddressIndex == null || mLocalAddressIndex.equals("")) {
            Log.e(TAG, "扫描失败，请检查wifi连接");
            return;
        }
        //1.核心池大小 2.线程池最大线程数 3.没有任务执行时，最多保持多久时间会终止
        //4.参数KeepAliveTime的时间单位，当前为毫秒
        //5.一个阻塞队列，用来存储等待执行的任务，这个参数的选择很重要，会对线程池运行过程产生重大影响。
        mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_THREAD_POOL_SIZE, 2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(CORE_POOL_SIZE));

        for (int i = 2; i < 255; i++) {
            final int lastAddress = i; //存放IP最后一位地址1-255
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String ping = ScanDeviceHelper.this.mPing + mLocalAddressIndex + lastAddress;
                    String currentIp = mLocalAddressIndex + lastAddress;
                    if (mDeviceAddress.equals(currentIp)) {
                        return;
                    }

                    try {
                        mProcess = mRuntime.exec(ping);
                        int result = mProcess.waitFor();
                        Log.d(TAG, "正在扫描的IP地址为：" + currentIp + "返回值为：" + result);
                        if (result == 0) {
                            Log.d(TAG, "扫描成功,Ip地址为：" + currentIp);
                            mIpList.add(currentIp);
                        } else {
                            // 扫描失败
                            Log.d(TAG, "扫描失败");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "扫描异常" + e.toString());
                        e.printStackTrace();
                    } finally {
                        if (mProcess != null) {
                            mProcess.destroy();
                        }
                    }
                }
            };
            mExecutor.execute(runnable);
        }

        mExecutor.shutdown();

        while (true) {
            try {
                if (mExecutor.isTerminated()) {// 扫描结束,开始验证
                    Log.d(TAG, "扫描结束,总共成功扫描到" + mIpList.size() + "个设备.");
                    listener.finish(mIpList);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void destroy() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
        }
    }

}
