package com.neworld.youyou.presenter.me

/**
 * @author by user on 2017/11/17.
 */
interface PhotoPresenter {
    fun onDestroy()
    fun commitFeedBack(content: String, array: ArrayList<String>)
}