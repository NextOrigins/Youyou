package com.neworld.youyou.model.books

import com.neworld.youyou.model.ResponseListener
import com.neworld.youyou.utils.NetBuild

/**
 * @author by user on 2017/11/22.
 */
class BooksModelImpl<T> : BooksModel<T> {

    override fun request(obs: ResponseListener<T>, map: HashMap<CharSequence, CharSequence>, url: Int, clazz: Class<T>) {
        NetBuild.response(object : NetBuild.ResponseObs<T> {
            override fun onSuccess(t: T) {
                obs.onSuccess(t)
            }

            override fun onFailed(error: String) {
                obs.onFailed(error)
            }

        }, url, clazz, map)
    }

    override fun request(success: (T) -> Unit, failed: (String) -> Unit,
                         map: HashMap<CharSequence, CharSequence>, url: Int, clazz: Class<T>) {
        NetBuild.response({ success(it) }, { failed(it) }, url, clazz, map)
    }
}