package com.neworld.youyou.view.mview.books

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neworld.youyou.R
import com.neworld.youyou.activity.AddressActivity
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.showSnackBar
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

    private var price: Double = 0.0
    private val icon by lazy { intent.getStringExtra("iconImg") }
    private val bookId by lazy { intent.getIntExtra("bookId", 0).toString() }
    private var name = ""
    private var expressFee = 0.0
    private val fromOrder by lazy { intent.getBooleanExtra("fromOrder", false) }
    private val bookCount by lazy { intent.getIntExtra("count", 0) }
    private val map by lazy {
        hashMapOf<CharSequence, CharSequence>(Pair("userId", userId),
                Pair("orderId", orderId), Pair("addressId", addressId))
    }

    private val userId by preference("userId", "")

    private var orderId = ""
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

        money = (d + expressFee).toString()
        count.text = new.toString()

        var str = "¥$d"
        book_price.text = str

        str = "x$new"
        num.text = str

        str = "共${new}件商品"
        commodity.text = str

        str = "¥$money"
        total.text = str

        str = "合计: $str"
        total_money.text = str
    }

    override fun initArgs(bundle: Bundle?): Boolean {
        expressFee = bundle?.getDouble("expressFee", 0.0) ?: 0.0
        name = bundle?.getString("name") ?: ""

        return super.initArgs(bundle)
    }

    override fun getContentLayoutId(): Int = R.layout.activity_books_pay

    /*override fun initArgs(bundle: Bundle?): Boolean {
        // 白底黑字状态栏 . api大于23 (Android6.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)
        }

        return super.initArgs(bundle)
    }*/

    override fun initWidget() {
        close.setOnClickListener { finish() }

        commit.setOnClickListener {
            if (ip == null) {
                showSnackBar(_parent, "未检测到WiFi模块", 2000)
                return@setOnClickListener
            }
            if (commit.text == "请稍等..") return@setOnClickListener

            commit.text = "请稍等.."
            val map = hashMapOf<CharSequence, CharSequence>()
            map["userId"] = userId
            map["money"] = money
            map["subjectId"] = "0"
            map["typeId"] = "0"
            map["babyName"] = name
            map["phone"] = _phone.text.toString()
            map["spbill_create_ip"] = ip!!
            map["orderId"] = orderId
            map["count"] = totalPrice.toString()
            map["addressId"] = addressId
            map["userMessage"] = description.text.toString()

            doAsync {
                logE("request map = $map")
                val response = NetBuild.getResponse(map, 188)
                val pay: Pay = Gson().fromJson(response, Pay::class.java)
                if (pay.status == 1) {
                    showSnackBar(_parent, "商品卖完了哦亲~")
                    uiThread { commit.text = if (!fromOrder) "提交订单" else "  付款  " }
                    return@doAsync
                }

                val api = WXAPIFactory.createWXAPI(baseContext, pay.appid)
                if (!api.isWXAppInstalled) {
                    showToast(getString(R.string.text_uninstalled_wchat))
                    uiThread { commit.text = if (!fromOrder) "提交订单" else "  付款  " }
                    return@doAsync
                }

                PayReq().run {
                    appId = pay.appid
                    prepayId = pay.prepayid
                    nonceStr = pay.noncestr
                    timeStamp = pay.timeStamp
                    sign = pay.sign
                    partnerId = "1480432402"
                    packageValue = "Sign=WXPay"
                    api.registerApp(pay.appid)
                    api.sendReq(this@run)

                    uiThread { commit.text = if (!fromOrder) "提交订单" else "  付款  " }
                }
            }
        }
        if (fromOrder) {
            description.isFocusable = false
            commit.text = "  付款  "
            return
        }

        delivery.setOnClickListener {
            startActivityForResult(Intent(this@BooksShopPay, AddressActivity::class.java), totalPrice)
        }
//        description.setOnClickListener {
//            // 没定需求. 同步iOS不需要新界面(买家留言)
//        }
        add.setOnClickListener {
            val i = count.text.toString().toInt() + 1
            totalPrice = i
        }
        reduce.setOnClickListener {
            val i = count.text.toString().toInt() - 1
            if (i == 0) return@setOnClickListener
            totalPrice = i
        }
        description.clearFocus()
    }

    override fun initData() {
        if (fromOrder) {
            val orderId = intent.getStringExtra("orderId")
            val body = "{\"userId\":\"$userId\", \"orderId\":\"$orderId\"}"
            response(this@BooksShopPay::onResponse, "189", body)

            return
        }
        price = intent.getStringExtra("price")?.toDouble() ?: 0.0

        totalPrice = 1
        val str = "快递费用${expressFee}元"
        _express_fee.text = str

        val options = RequestOptions()
                .placeholder(R.drawable.book_place_bg)
                .error(R.drawable.book_place_bg)

        Glide.with(book_icon).load(icon).apply(options).into(book_icon)
        book_name.text = name

        val map = hashMapOf<CharSequence, CharSequence>()
        map["userId"] = userId
        map["bookId"] = bookId
        doAsync {
            val response = NetBuild.getResponse(map, 187)
            val data: Order = Gson().fromJson(response,
                    object : TypeToken<Order>() {}.type)
            orderId = if (data.status == 0) data.orderId else ""
        }

        loadAddress()
    }

    @SuppressLint("SetTextI18n")
    private fun onResponse(response: ResponseBean.BooksOrderModel) {
        val data = response.menuList
        val user = response.UserAddressList

        if (user == null) {
            showToast("出现未知错误")
            return
        }

        _name.text = user.consignee
        _phone.text = user.phone
        _address.text = user.address

        book_name.text = data.bookName // 书名
        Glide.with(book_icon).load(data.iconImg).into(book_icon) // 书的预览图

        _express_fee.text = "快递费用${data.expressFee}元"
        val str: CharSequence = data.userMeassge ?: ""
        description.setText(str)

        name = user.consignee
        addressId = data.addressId.toString()
        orderId = data.orderId
        expressFee = data.expressFee // 快递费
        price = data.price // 设置单价
        totalPrice = bookCount // 设置联动数字


        total.text = "¥${data.money}"
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
        map["userId"] = userId
        NetBuild.response({
            val list = it.menuList
            if (list.isEmpty()) {
                dialog.show()
            } else {
                list.firstOrNull { it.status == 0 } ?: list[0].let {
                    _name.text = it.consignee
                    _phone.text = it.phone
                    _address.text = it.address
                    addressId = it.id
                }
            }
        }, { ToastUtil.showToast(it) }, 180, ResponseBean.AddressBody::class.java, map)
    }

    override fun onBackPressed() {
        doAsync {
            if (!TextUtils.isEmpty(_name.text) && !fromOrder)
                NetBuild.getResponse(map, 184)
        }
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        doAsync {
            if (!TextUtils.isEmpty(_name.text) && !fromOrder)
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
