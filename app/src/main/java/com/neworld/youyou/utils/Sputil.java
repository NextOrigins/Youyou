package com.neworld.youyou.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

import com.neworld.youyou.activity.LoginActivity;

import java.util.Set;

/**
 * Created by ZHL on 2017/4/5.
 */

@SuppressWarnings("ALL")
public class Sputil {
    private static final String CONFIG = "config";
    private static SharedPreferences mSp;

    //保存boolean
    public static void saveBoolean(Context context, String key, boolean value) {
        if (mSp == null) {
            mSp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        mSp.edit().putBoolean(key, value).apply();
    }

    //获取boolean
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        if (mSp == null) {
            mSp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return mSp.getBoolean(key, defValue);
    }

    //保存String
    public static void saveString(Context context, String key, String value) {
        if (mSp == null) {
            mSp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        mSp.edit().putString(key, value).apply();
    }

    //获取String
    public static String getString(Context context, String key, String defValue) {
        if (mSp == null) {
            mSp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return mSp.getString(key, defValue);
    }

    public static int getInt(Context context, String key, int def) {
        if (mSp == null)
            mSp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        return mSp.getInt(key, def);
    }

    public static void saveInt(Context context, String key, int value) {
        if (mSp == null)
            mSp = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        mSp.edit().putInt(key, value).apply();
    }
}
