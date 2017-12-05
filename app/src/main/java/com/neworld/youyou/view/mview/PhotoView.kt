package com.neworld.youyou.view.mview

import android.content.Context

/**
 * @author by user on 2017/11/17.
 */
interface PhotoView {
    fun showDialog()
    fun hideDialog()
    fun showProgress()
    fun hideProgress()
    fun showToast(str: String = "上传成功")
    fun close()
    fun context(): Context
}