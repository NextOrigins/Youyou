package com.neworld.youyou.model.books

import com.neworld.youyou.model.ResponseListener
import com.neworld.youyou.utils.NetBuild

/**
 * @author by user on 2017/11/22.
 */
interface BooksModel<T> {
    fun request(obs: ResponseListener<T>, map: HashMap<CharSequence, CharSequence>, url: Int, clazz: Class<T>)
    fun request(success: (T) -> Unit, failed: (String) -> Unit, map: HashMap<CharSequence, CharSequence>, url: Int, clazz: Class<T>)
}