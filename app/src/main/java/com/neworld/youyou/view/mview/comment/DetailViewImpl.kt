package com.neworld.youyou.view.mview.comment

import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.view.mview.common.RecyclerDataView
import kotlinx.android.synthetic.main.activity_detail_comment.*

/**
 * @author by user on 2017/11/21.
 * 回复页面
 */
class DetailViewImpl<T> : Activity(), RecyclerDataView<T> {

    override fun pullRefresh(b: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addAll(t: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun notifyData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeData(index: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showToast(str: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentLayoutId() = R.layout.activity_detail_comment

    override fun onWindowFocusChanged(hasFocus: Boolean) {

    }

    override fun initWidget() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = resources.getColor(android.R.color.white)
        }

        close.setOnClickListener { onBackPressed() }
        recycler.layoutManager = LinearLayoutManager(this@DetailViewImpl,
                LinearLayoutManager.VERTICAL, false)

    }
}
