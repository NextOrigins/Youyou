package com.neworld.youyou.model.me

import android.content.Context

/**
 * @author by user on 2017/11/17.
 */
interface PhotoModel {
    fun takePhoto()
    fun choosePhoto(size: Int = 0)
    fun commit(listener: PhotoListener, content: String, array: ArrayList<String>, context: Context)

    interface PhotoListener {
        fun onError(error: String)
        fun onSuccess()
    }
}