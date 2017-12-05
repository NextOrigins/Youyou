package com.neworld.youyou.presenter.books

/**
 * @author by user on 2017/11/22.
 */
interface BooksPresenter<T> {
    fun onDestroy()
    fun down(map: HashMap<CharSequence, CharSequence>, url: Int, clazz: Class<T>)
    fun up(map: HashMap<CharSequence, CharSequence>, url: Int, clazz: Class<T>)
}