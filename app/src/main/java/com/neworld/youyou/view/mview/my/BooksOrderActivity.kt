package com.neworld.youyou.view.mview.my

import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.NetBuild
import com.neworld.youyou.utils.preference
import kotlinx.android.synthetic.main.activity_books_order.*
import org.jetbrains.anko.doAsync

/**
 * @author by user on 2017/12/7.
 */
class BooksOrderActivity : Activity() {

    val userId by preference("userId", "")

    override fun getContentLayoutId() = R.layout.activity_books_order

    override fun initWindows() {
        // 改回蓝底白字 . api大于23 (Android6.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = ContextCompat.getColor(baseContext, R.color.colorPrimaryDark)
        }
    }

    override fun initWidget() {
        _close.setOnClickListener { finish() }

        _recycler.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        hashMapOf<CharSequence, CharSequence>().run {
            put("userId", userId)
            doAsync {
                NetBuild.response({

                }, {

                }, 190, ResponseBean.OrderMenu::class.java, this@run)
            }
        }
    }

    override fun initData() {

    }
}