package com.neworld.youyou.utils

import android.content.Context

/**
 * @author by user on 2017/11/22.
 */
object SpUtil {

    fun getString(context: Context, key: String, value: String = ""): String
            = Sputil.getString(context, key, value)

    fun getInt(context: Context, key: String, def: Int)
            = Sputil.getInt(context, key, def)
}