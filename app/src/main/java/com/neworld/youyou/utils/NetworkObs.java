package com.neworld.youyou.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author by user on 2017/11/3.
 */

public class NetworkObs extends BroadcastReceiver {

    private NetworkState l;

    public NetworkObs(NetworkState l) {
        this.l = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (NetUtil.isWiFiConnected(context))
                l.onWifiConnected();
            else if (NetUtil.isMobileConnected(context))
                l.onMobileConnected();
            else
                l.onNetworkUnknown();
        }
    }

    public interface NetworkState {
        void onWifiConnected();

        void onMobileConnected();

        void onNetworkUnknown();
    }
}
