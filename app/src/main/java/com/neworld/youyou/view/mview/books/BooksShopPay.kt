package com.neworld.youyou.view.mview.books

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neworld.youyou.R
import com.neworld.youyou.activity.AddressActivity
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.showSnackbar
import com.neworld.youyou.utils.*
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.activity_books_pay.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.properties.Delegates

/**
 * @author by user on 2017/12/4.
 */
class BooksShopPay : Activity() {

    private val price by lazy { intent.getStringExtra("price").toDouble() }
    private val icon by lazy { intent.getStringExtra("iconImg") }
    private val name by lazy { intent.getStringExtra("name") }
    private val bookId by lazy { intent.getStringExtra("bookId") }
    private val map by lazy {
        hashMapOf<CharSequence, CharSequence>(Pair("userId", userId),
                Pair("orderId", orderId), Pair("addressId", addressId))
    }

    private val userId by preference("userId", "")

    private var orderId: String by notNullSingleValue()
    private var addressId: String by Delegates.notNull()
    private var money: String by Delegates.notNull()
    private val ip: String? by lazy { NetUtil.wifiConfig() }

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
        builder.setCancelable(false)
        builder
    }

    private var totalPrice by Delegates.observable(1) { _, _, new ->

        val d = new * price

        money = (d + 10).toString()
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
//        description.setOnClickListener {
//            // 没定需求. 同步iOS不需要新界面(买家留言)
//        }
        commit.setOnClickListener {
            if (ip == null) {
                showSnackbar(_parent, "未检测到WiFi模块, 请到用户反馈处反馈此问题, 我们会尽快解决", 2000)
                return@setOnClickListener
            }
            hashMapOf<CharSequence, CharSequence>().run {
                put("userId", userId)
                put("money", money)
                put("subjectId", "0")
                put("typeId", "0")
                put("babyName", name)
                put("phone", _phone.text)
                put("spbill_create_ip", ip!!)
                put("orderId", orderId)
                doAsync {
                    val response = NetBuild.getResponse(this@run, 188)
                    val pay: Pay = Gson().fromJson(response,
                            object : TypeToken<Pay>() {}.type)
                    pay.let {
                        if (it.status == 0) {
                            val api = WXAPIFactory.createWXAPI(this@BooksShopPay, it.appid)
                            if (!api.isWXAppInstalled) {
                                ToastUtil.showToast(getString(R.string.text_uninstalled_wchat))
                                return@doAsync
                            }
                            PayReq().run {
                                appId = it.appid
                                prepayId = it.prepayid
                                nonceStr = it.noncestr
                                timeStamp = it.timeStamp
                                sign = it.sign
                                partnerId = "1480432402"
                                packageValue = "Sign=WXPay"
                                api.registerApp(it.appid)
                                api.sendReq(this)
                            }
                        } else
                            uiThread { showSnackbar(_parent, "商品卖完了哦亲_") }
                    }
                }
            }
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

        val map = hashMapOf<CharSequence, CharSequence>()
        map.put("userId", userId)
        map.put("bookId", bookId)
        doAsync {
            val response = NetBuild.getResponse(map, 187)
            val data: Order = Gson().fromJson(response,
                    object : TypeToken<Order>() {}.type)
            orderId = if (data.status == 0) data.orderId else ""
        }

        loadAddress()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            20 -> data?.run {
                _address.text = getStringExtra("msg")
                _name.text = getStringExtra("name")
                _phone.text = getStringExtra("phone")
            }
            66 -> {
                loadAddress()
            }
            else -> data?.run {
                _address.text = getStringExtra("address")
                _name.text = getStringExtra("name")
                _phone.text = getStringExtra("phone")
            }
        }
        addressId = data?.getStringExtra("addressId") ?: ""
    }

    private fun loadAddress() {
        val map = hashMapOf<CharSequence, CharSequence>()
        map.put("userId", userId)
        NetBuild.response({
            val list = it.menuList
            if (list.isEmpty()) dialog.show()
            else list.firstOrNull { it.status == 0 } ?: list[0]
                    .let {
                        _name.text = it.consignee
                        _phone.text = it.phone
                        _address.text = it.address
                        addressId = it.id
                    }
        }, { ToastUtil.showToast(it) }, 180, ResponseBean.AddressBean::class.java, map)
    }

    override fun onBackPressed() {
        doAsync {
            if (!TextUtils.isEmpty(_name.text))
                NetBuild.getResponse(map, 184)
        }
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        doAsync {
            if (!TextUtils.isEmpty(_name.text))
                NetBuild.getResponse(map, 184)
        }
        super.onSaveInstanceState(outState)
    }

    private data class Order(val orderId: String,
                             val status: Int)

    private data class Pay(val timeStamp: String,
                           val appid: String,
                           val sign: String,
                           val prepayid: String,
                           val noncestr: String,
                           val status: Int)
}
