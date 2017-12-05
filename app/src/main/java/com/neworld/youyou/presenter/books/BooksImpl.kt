package com.neworld.youyou.presenter.books

import com.neworld.youyou.model.ResponseListener
import com.neworld.youyou.model.books.BooksModel
import com.neworld.youyou.model.books.BooksModelImpl
import com.neworld.youyou.view.mview.common.RecyclerDataView

/**
 * @author by user on 2017/11/22.
 */
class BooksImpl<T>(view: RecyclerDataView<T>) : BooksPresenter<T> {

    private var view: RecyclerDataView<T>? = view
    private var model: BooksModel<T> = BooksModelImpl()

    override fun onDestroy() {
        view = null
    }

    override fun down(map: HashMap<CharSequence, CharSequence>, url: Int, clazz: Class<T>) {
        model.request({
            view?.removeAll()
            view?.addAll(it)
            view?.pullRefresh(false)
            view?.notifyData()
        }, {
            view?.showToast(it)
            view?.pullRefresh(false)
        }, map, url, clazz)
    }

    override fun up(map: HashMap<CharSequence, CharSequence>, url: Int, clazz: Class<T>) {
        model.request({
            view?.addAll(it)
            view?.notifyData()
        }, {
            view?.showToast(it)
        }, map, url, clazz)
    }
}