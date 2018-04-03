package com.neworld.youyou.utils

import android.content.Context
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.manager.MyApplication
import com.neworld.youyou.update.UpdateService

/**
 * @author by hhhh on 2018/3/28.
 * @property pUpdate ：提示更新
 * @property fUpdate ：强制更新
 * @property onProgressUpDate ：更新进度
 */
class UpDate(private val onProgressUpDate: (newProgress: Int) -> Unit,  // 更新进度
             private val fUpdate: (start: () -> Unit) -> Unit,          // 强制更新的自定义Dialog
             private val pUpdate: (start: () -> Unit) -> Unit           // 提示升级
) {

    private var versionCode = 0
    private val context: Context = MyApplication.sContext

    private val startDownload: () -> Unit = {
        UpdateService.openUpdate(onProgressUpDate, context)
    }

    fun checkUpdate(version: String) {
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
                    fUpdate.invoke(startDownload)
                } else if (versionCode < newVersion) {
                    // 提示更新
                    pUpdate.invoke(startDownload)
                }
            } catch (e: Exception) {
                return
            }
        }
    }
}