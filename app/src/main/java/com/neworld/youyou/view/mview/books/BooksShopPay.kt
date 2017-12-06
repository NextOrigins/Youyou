package com.neworld.youyou.view.mview.books

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.neworld.youyou.R
import com.neworld.youyou.activity.AddressActivity
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.activity_books_pay.*
import kotlin.properties.Delegates

/**
 * @author by user on 2017/12/4.
 */
class BooksShopPay : Activity() {

    private val price by lazy { intent.getStringExtra("price").toDouble() }
    private val icon by lazy { intent.getStringExtra("iconImg") }
    private val name by lazy { intent.getStringExtra("name") }

    private val userId by lazy { SpUtil.getString(baseContext, "userId") }

    private val dialog by lazy {
        val builder = AlertDialog.Builder(this@BooksShopPay)
        builder.setMessage("请添加地址")
        builder.setPositiveButton("前往") { dialog, _ ->
            val intent = Intent(this, AddressActivity::class.java)
            intent.putExtra("fromPay", true)
            startActivityForResult(intent, totalPrice)
            dialog.dismiss()
        }
        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        builder.setCancelable(true)
        builder
    }

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
            startActivityForResult(Intent(this@BooksShopPay, AddressActivity::class.java), totalPrice)
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
        close.setOnClickListener { finish() }
    }

    override fun initData() {
        totalPrice = 1

        val options = RequestOptions()
                .placeholder(R.drawable.book_place_bg)
                .error(R.drawable.book_place_bg)

        Glide.with(book_icon).load(icon).apply(options).into(book_icon)
        book_name.text = name

        loadAddress()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            20 -> {
                _address.text = data?.getStringExtra("msg") ?: "_"
                _name.text = data?.getStringExtra("name") ?: "_"
                _phone.text = data?.getStringExtra("phone") ?: "_"
            }
            66 -> {
                loadAddress()
            }
            else -> {
                _address.text = data?.getStringExtra("address") ?: "_"
                _name.text = data?.getStringExtra("name") ?: "_"
                _phone.text = data?.getStringExtra("phone") ?: "_"
            }
        }
    }

    private fun loadAddress() {
        val map = hashMapOf<CharSequence, CharSequence>()
        map.put("userId", userId)
        NetBuild.response({
            val list = it.menuList
            if (list.isEmpty()) dialog.show()
            else list.firstOrNull { it.status == 0 }
                    .let {
                        val at = it ?: list[0]
                        _name.text = at.consignee
                        _phone.text = at.phone
                        _address.text = at.address
                    }
        }, { ToastUtil.showToast(it) }, 180, ResponseBean.AddressBean::class.java, map)

    }
}
