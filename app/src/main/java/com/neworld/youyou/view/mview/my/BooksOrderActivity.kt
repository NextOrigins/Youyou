package com.neworld.youyou.view.mview.my

import android.content.Intent
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.mview.books.BooksShopPay
import kotlinx.android.synthetic.main.activity_books_order.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * @author by user on 2017/12/7.
 */
class BooksOrderActivity : Activity() {

    private val userId by preference("userId", "")
    private var mAdapter: Adapter<ResponseBean.OrderMenu> by notNullSingleValue()
    private val list = arrayListOf<ResponseBean.OrderMenu>()

    private val obs = object : Adapter.AdapterObs<ResponseBean.OrderMenu> {

        override fun onBind(holder: Adapter.Holder, bean: MutableList<ResponseBean.OrderMenu>, position: Int) {
            val data = bean[position]
            val delete = holder.find<TextView>(R.id.item_delete)
            val date = holder.find<TextView>(R.id.item_date)
            val price = holder.find<TextView>(R.id.item_praise)
            val name = holder.find<TextView>(R.id.item_books_name)
            val icon = holder.find<ImageView>(R.id.item_icon)
            val sum = holder.find<TextView>(R.id.item_num)
            val total = holder.find<TextView>(R.id.item_total)

            holder.find<View>(R.id.item_parent).setOnClickListener {
                startActivity(Intent(this@BooksOrderActivity, BooksShopPay::class.java)
                        .putExtra("orderId", data.orderId)
                        .putExtra("fromOrder", true)
                        .putExtra("count", data.bookCount.toInt()))
            }

            when (data.payStatus) {
                0 -> {
                    delete.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.delete_address, 0)
                    delete.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.dp10)
                    delete.text = "待支付"
                    delete.setOnClickListener {
                        displayDialog(this@BooksOrderActivity, "确定删除吗", {
                            hashMapOf<CharSequence, CharSequence>().run {
                                put("orderId", data.orderId)
                                doAsync {
                                    val response = NetBuild.getResponse(this@run, 148)
                                    if (response.contains("0"))
                                        uiThread { mAdapter.remove(position) }
                                    else
                                        ToastUtil.showToast("网络错误, 请重试")
                                }
                            }
                        })
                    }
                }
                1 -> {
                    delete.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    delete.text = "已支付"
                    delete.setOnClickListener(null)
                }
                else -> {
                    delete.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    delete.text = "已发货"
                    delete.setOnClickListener(null)
                }
            }

            val s1 = "¥${data.price}"
            val s2 = "数量: x${data.bookCount}"
            val s3 = "共${data.bookCount}件商品 合计:¥${data.orderMoney}(含运费¥${data.expressFee})"
            date.text = data.updateDate
            name.text = data.bookName
            price.text = s1
            sum.text = s2
            total.text = s3

            Glide.with(icon).load(data.iconImg).into(icon)
        }

        override fun layoutId(): Int = R.layout.item_books_order
    }

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

        mAdapter = Adapter(obs, list)
        _recycler.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        _recycler.adapter = mAdapter
        _recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun initData() = with(hashMapOf<CharSequence, CharSequence>()) {
        put("userId", userId)
        doAsync {
            NetBuild.response({
                list.addAll(it.menuList)
                mAdapter.notifyDataSetChanged()
            }, ToastUtil::showToast, 190, ResponseBean.BooksOrderBody::class.java, this@with)
        }
        Unit
    }
}