package com.neworld.youyou.presenter.books

import com.neworld.youyou.model.books.BooksModel
import com.neworld.youyou.model.books.BooksModelImpl
import com.neworld.youyou.utils.LogUtils
import com.neworld.youyou.utils.NetBuild
import com.neworld.youyou.utils.ToastUtil
import com.neworld.youyou.view.mview.books.BooksDetailsView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * @author by user on 2017/11/27.
 */
class DetailImpl<T>(view: BooksDetailsView<T>) : BooksPresenter<T> {

    private var view: BooksDetailsView<T>? = view
    private val model: BooksModel<T> = BooksModelImpl()

    override fun onDestroy() {
        view = null
    }

    override fun down(map: HashMap<CharSequence, CharSequence>, url: Int, clazz: Class<T>) {
        view?.showDialog()
        model.request({
            view?.closeDialog()
            view?.setData(it)
            view?.notifyData()
        }, {
            view?.closeDialog()
            view?.showError(it)
        }, map, url, clazz)
    }

    fun uploadState(praise: Boolean, stars: Boolean, map: HashMap<CharSequence, CharSequence>, url: Int) {
        doAsync {
            map.put("status", if (praise) "1" else "0")
            map.put("typeStatus", "1")
            var response = NetBuild.getResponse(map, url)
            if ("1" in response)
                uiThread { ToastUtil.showToast("点赞和收藏保存失败_请检查网络或到用户反馈处吐槽~") }
            map.put("status", if (stars) "1" else "0")
            map.put("typeStatus", "2")
            response = NetBuild.getResponse(map, url)
            if ("1" in response)
                uiThread { ToastUtil.showToast("点赞和收藏保存失败_请检查网络或到用户反馈处吐槽~") }
        }
    }

    override fun up(map: HashMap<CharSequence, CharSequence>, url: Int, clazz: Class<T>) {
    }

}