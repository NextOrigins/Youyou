package com.neworld.youyou.view.mview.common

/**
 * @author by user on 2017/11/21.
 */
interface RecyclerDataView<in T> {
    fun notifyData()
    fun removeData(index: Int)
    fun removeAll()
    fun addAll(t: T)
    fun showToast(str: String = "网络错误, 请稍后重试")
    fun pullRefresh(b: Boolean)
    fun showProgress()
    fun hideProgress()
}