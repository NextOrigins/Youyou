package com.neworld.youyou.utils

import android.content.Context
import com.neworld.youyou.bean.ResponseBean

/**
 * @author by hhhh on 2018/3/28.
 */
class UpDate {

    private var versionCode = 0
    private lateinit var context: Context

    fun checkUpdate(version: String, context: Context) {
        this.context = context
        versionCode = version.replace(".", "").toInt()
        response(::onResponse, "171", "")
    }

    private fun onResponse(t: ResponseBean.Version) {
        if (t.status == 0) {
            try {
                val res = t.results ?: return
                val minimum = res.versionNum?.replace(".", "")?.toInt() ?: return
                val newVersion = res.versionName?.replace(".", "")?.toInt() ?: return

                if (versionCode < minimum) {
                    // 强制更新
                } else if (versionCode < newVersion) {
                    // 提示更新
                    displayDialog(context, res.msg ?: "", {
                        // 升级逻辑
                    }, "立刻升级")
                }
            } catch (e: Exception) {
                return
            }
        }
    }
}