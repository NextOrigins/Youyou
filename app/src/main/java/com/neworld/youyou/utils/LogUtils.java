package com.neworld.youyou.utils;

import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;


/**
 * @author by user on 2017/10/24.
 */

public class LogUtils {

    private static final boolean LOG = true; // TODO : release版本改为false
    private static int start;
    private static int end;
    private static final Object obj = new Object();

    public static void D(String msg) {
        if (LOG) {
            Log.d("-------LOG_D-------", msg);
        }
    }

    public static void E(String msg) {
        if (LOG) {
            Log.e("-------LOG_E-------", msg);
        }
    }

    public static void LOG_JSON(String msg) {
        synchronized (obj) {
            if (LOG) {
                int strLength = msg.length();
                end += 1000;
                if (strLength > end) {
                    LogUtils.E(msg.substring(start, end));
                    start = end;
                    end += 1000;
                    LOG_JSON(msg);
                } else {
                    LogUtils.E(msg.substring(start, strLength));
                    start = 0;
                    end = 0;
                }
            }
        }
    }
}
