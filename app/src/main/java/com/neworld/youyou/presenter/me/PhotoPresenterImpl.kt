package com.neworld.youyou.presenter.me

import com.neworld.youyou.model.me.PhotoModelImpl
import com.neworld.youyou.model.me.PhotoModel
import com.neworld.youyou.presenter.me.PhotoPresenter
import com.neworld.youyou.view.mview.PhotoView

/**
 * @author by user on 2017/11/17.
 */
class PhotoPresenterImpl(photoView: PhotoView) : PhotoPresenter {

    private var photoView: PhotoView? = photoView
    private var photoModel: PhotoModel? = null

    init {
        photoModel = PhotoModelImpl()
    }

    override fun onDestroy() {
        photoView = null
    }

    override fun commitFeedBack(content: String, array: ArrayList<String>) {
//        photoView?.hideDialog()
        photoView?.showProgress()
        photoModel?.commit(object: PhotoModel.PhotoListener {
            override fun onError(error: String) {
                photoView?.showToast(error)
                photoView?.hideProgress()
            }

            override fun onSuccess() {
                photoView?.showToast()
                photoView?.hideProgress()
                photoView?.close()
            }
        }, content, array, photoView!!.context())
    }

}
