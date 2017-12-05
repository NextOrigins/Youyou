package com.neworld.youyou.view.mview.books

/**
 * @author by user on 2017/11/27.
 */
interface BooksDetailsView<T> {
    fun showDialog()
    fun closeDialog()
    fun setData(t: T)
    fun showError(str: String = "网络错误, 请重试")
    fun notifyData()
}