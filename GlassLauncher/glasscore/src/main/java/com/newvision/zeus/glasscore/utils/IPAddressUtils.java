package com.newvision.zeus.glasscore.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by yanjiatian on 2017/6/30.
 */

public class IPAddressUtils {
    private static final String TAG = IPAddressUtils.class.getSimpleName();

    //获取热点的IP地址
    public static String getHotIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo info = wifiManager.getDhcpInfo();
        return int2Ip(info.serverAddress);
    }

    /**
     * 获取本机的ip地址（3种方法都包括）
     *
     * @param context
     * @return
     */
    public static String getIpAddress(Context context) {
        String ip = null;
        try {
            ip = getIp(context);
            if (ip == null) {
                ip = getIpAddress();
                if (ip == null) {
                    ip = getLocalIpAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "ip==" + ip);
        return ip;
    }

    public static String getLocalAddressIndex(String devAddress) {
        if (!devAddress.equals("")) {
            return devAddress.substring(0, devAddress.lastIndexOf(".") + 1);
        }
        return null;
    }


    /**
     * 将int型ip转成String型ip
     *
     * @param intIp
     * @return
     */
    private static String int2Ip(int intIp) {
        byte[] bytes = int2byte(intIp);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(bytes[i] & 0xFF);
            if (i < 3) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    private static byte[] int2byte(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (0xff & i);
        bytes[1] = (byte) ((0xff00 & i) >> 8);
        bytes[2] = (byte) ((0xff0000 & i) >> 16);
        bytes[3] = (byte) ((0xff000000 & i) >> 24);
        return bytes;
    }


    /**
     * wifi获取ip
     *
     * @param context
     * @return
     */
    private static String getIp(Context context) {
        try {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp(ipAddress);
            return ip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化ip地址（192.168.11.1）
     *
     * @param i
     * @return
     */
    private static String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    /**
     * gps获取ip
     *
     * @return
     */
    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 3G/4g网络IP
     */
    private static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //当Android设备被设置为Wi-Fi热点的时候，其IP地址是hardcode写死在源码中的，地址是：“192.168.43.1”，对应的广播地址是："192.168.43.255"
    public static Boolean isWifiApEnabled(Context context) {
        try {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            Method method = manager.getClass().getMethod("isWifiApEnabled");
            return (Boolean) method.invoke(manager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

}
