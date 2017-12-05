package com.neworld.youyou.utils;

/**
 * Created by tt on 2017/9/12.
 */

public class StringUtil {
    public static boolean isNull(String string) {
        if (string != null) {
            return true;
        }
        return false;
    }

    public static boolean isString(String string) {
        if (string != null && string.length() > 0) {
            return true;
        }
        return false;
    }
}
