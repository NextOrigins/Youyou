package com.neworld.youyou.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.neworld.youyou.manager.MyApplication;

/**
 * Created by user on 2017/11/2.
 */

public class NetUtil {

    /**
     * 网络是否连接
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * WiFi是否连接
     */
    public static boolean isWiFiConnected(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return info != null && info.isConnected();
    }

    /**
     * 是否数据连接
     */
    public static boolean isMobileConnected(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return info != null && info.isConnected();
    }

    public static String wifiConfig() {
        WifiManager m = (WifiManager) MyApplication.sContext.getSystemService(Context.WIFI_SERVICE);
        String ip = null;
        if (m != null) {
            int ipAddress = m.getConnectionInfo().getIpAddress();
            ip = (ipAddress & 0xFF) + "." +
                    ((ipAddress >> 8) & 0xFF) + "." +
                    ((ipAddress >> 16) & 0xFF) + "." +
                    (ipAddress >> 24 & 0xFF);
        }
        return ip;
    }
}
