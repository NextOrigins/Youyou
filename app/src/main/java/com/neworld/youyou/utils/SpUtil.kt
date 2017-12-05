package com.neworld.youyou.utils

import android.content.Context

/**
 * @author by user on 2017/11/22.
 */
object SpUtil {

    @Synchronized
    fun getString(context: Context, key: String, value: String = ""): String
            = Sputil.getString(context, key, value)
}