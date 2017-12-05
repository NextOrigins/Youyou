package com.neworld.youyou.model

/**
 * @author by user on 2017/11/22.
 */
interface ResponseListener<T> {

    fun onSuccess(t: T)
    fun onFailed(error: String)
}