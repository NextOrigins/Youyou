package com.neworld.youyou.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
}
