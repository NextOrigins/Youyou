package com.neworld.youyou.view.mview.books

import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.utils.LogUtils
import com.neworld.youyou.utils.NetBuild
import com.neworld.youyou.utils.SpUtil
import kotlinx.android.synthetic.main.activity_books_pay.*
import org.jetbrains.anko.doAsync
import kotlin.properties.Delegates

/**
 * @author by user on 2017/12/4.
 */
class BooksShopPay : Activity() {

    private val price by lazy { intent.getStringExtra("price").toDouble() }
    private val icon by lazy { intent.getStringExtra("iconImg") }
    private val name by lazy { intent.getStringExtra("name") }

    private val userId by lazy { SpUtil.getString(baseContext, "userId") }

    private var totalPrice by Delegates.observable(1) {
        _, _, new ->

        val d = new * price

        count.text = new.toString()

        var str = "¥$d"
        book_price.text = str

        str = "x$new"
        num.text = str

        str = "共${new}件商品"
        commodity.text = str

        str = "¥${d + 10}"
        total.text = str

        str = "合计: $str"
        total_money.text = str
    }

    override fun getContentLayoutId(): Int = R.layout.activity_books_pay

    override fun initArgs(bundle: Bundle?): Boolean {
        // 白底黑字状态栏 . api大于23 (Android6.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)
        }

        return super.initArgs(bundle)
    }

    override fun initWidget() {
        delivery.setOnClickListener {
            // TODO : 选择学生界面
        }
        description.setOnClickListener {
            // TODO : 编辑界面
        }
        commit.setOnClickListener {
            // TODO : 提交订单
        }
        up.setOnClickListener {
            val i = count.text.toString().toInt() + 1
            totalPrice = i
        }
        down.setOnClickListener {
            val i = count.text.toString().toInt() - 1
            if (i == 0) return@setOnClickListener
            totalPrice = i
        }
        description.clearFocus()
    }

    override fun initData() {
        totalPrice = 1

        val options = RequestOptions()
                .placeholder(R.drawable.book_place_bg)
                .error(R.drawable.book_place_bg)

        Glide.with(book_icon).load(icon).apply(options).into(book_icon)
        book_name.text = name
        doAsync {
            LogUtils.E("userId = $userId")
            val response = NetBuild.getResponse("{\"userId\":$userId", 141)
            LogUtils.LOG_JSON(response)
        }
    }
}