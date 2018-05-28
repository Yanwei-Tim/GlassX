package com.newvision.martin.clouddetect.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by wb on 17-1-3.
 */
public class WifiStatusUtils {

    public static boolean isNetworkAvailable(Context context) {
        return isWifiConnected(context) || isMobileConnected(context);
    }

    //是否连接WIFI
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            Log.i("status", "wifiNetworkInfo.isConnected()" + wifiNetworkInfo.isConnected());
            return true;
        }
        Log.i("status", "wifiNetworkInfo.isConnected()" + wifiNetworkInfo.isConnected());
        return false;
    }

    public static boolean isMobileConnected(Context context) {
        // TODO Force the user to set the network connected.
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo == null) {
            Log.i("status", "networkInfo" + networkInfo);
            return false;
        }
        if (false == networkInfo.isConnectedOrConnecting()) {
            Log.i("status", "networkInfo.isConnectedOrConnecting()" + networkInfo.isConnectedOrConnecting());
            return false;
        } else {
            Log.i("status", "networkInfo.isConnectedOrConnecting()" + networkInfo.isConnectedOrConnecting());
            return true;
        }
    }
}
