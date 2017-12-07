package com.neworld.youyou.view.mview.my

import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.NetBuild
import com.neworld.youyou.utils.ToastUtil
import com.neworld.youyou.utils.notNullSingleValue
import com.neworld.youyou.utils.preference
import kotlinx.android.synthetic.main.activity_books_order.*
import org.jetbrains.anko.doAsync

/**
 * @author by user on 2017/12/7.
 */
class BooksOrderActivity : Activity() {

    private val userId by preference("userId", "")
    private var mAdapter: Adapter<ResponseBean.OrderMenu> by notNullSingleValue()
    private val list = arrayListOf<ResponseBean.OrderMenu>()

    val obs = object : Adapter.AdapterObs<ResponseBean.OrderMenu> {

        override fun onBind(holder: Adapter.Holder?, bean: MutableList<ResponseBean.OrderMenu>, position: Int) {
            val data = bean[position]
            val delete = holder!!.find<TextView>(R.id.item_delete)
            val date = holder.find<TextView>(R.id.item_date)
            val price = holder.find<TextView>(R.id.item_price)
            val name = holder.find<TextView>(R.id.item_books_name)
            val icon = holder.find<ImageView>(R.id.item_icon)
            val sum = holder.find<TextView>(R.id.item_num)
            val total = holder.find<TextView>(R.id.item_total)

            delete.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.delete_address, 0)
            delete.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.dp10)

            date.text = data.date
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
    }

    override fun initData() = hashMapOf<CharSequence, CharSequence>().run {
        put("userId", userId)
        doAsync {
            NetBuild.response({
                list.addAll(it.menuList)
                mAdapter.notifyDataSetChanged()
            }, ToastUtil::showToast, 190, ResponseBean.BooksOrderBean::class.java, this@run)
        }
        Unit
    }
}