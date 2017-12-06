package com.neworld.youyou.view.mview.books

import android.content.Intent
import android.graphics.Paint
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.neworld.youyou.R
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.SpacesItemDecoration
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.PullToAdapter
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.manager.MyApplication
import com.neworld.youyou.presenter.books.BooksImpl
import com.neworld.youyou.showSnackbar
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.mview.common.RecyclerDataView
import java.util.*
import kotlin.collections.HashMap
import kotlin.properties.Delegates

/**
 * @author by user on 2017/11/17.
 */
class BooksViewImpl : Fragment(), RecyclerDataView<ResponseBean.BooksBean> {

    private val spacing = 60F
    private val spanCount = 3
    private val width: Int by lazy {
        val point = Point()
        activity.windowManager.defaultDisplay.getSize(point)
        ((point.x - spacing * (spanCount - 1)) / spanCount).toInt()
    }

    private var swipe: SwipeRefreshLayout? = null

    private val list: ArrayList<ResponseBean.Menu> = ArrayList()
    private var mAdapter: Adapter<ResponseBean.Menu> by notNullSingleValue()
    private var presenter: BooksImpl<ResponseBean.BooksBean>? = null

    private var recycler: RecyclerView by notNullSingleValue()

    private var b = true

    private val token by preference("token", "")
    private val userId by preference("userId", "")

    private val map by lazy {
        hashMapOf<CharSequence, CharSequence>(Pair("type", "0"), Pair("CreateDate", "")
                , Pair("token", token), Pair("userId", userId))
    }

    private var requestCount by Delegates.observable(0) {
        _, old, new ->
        if (old != new) {
            val upMap = HashMap<CharSequence, CharSequence>()
            upMap.put("type", "0")
            upMap.put("CreateDate", list[new].createDate)
            upMap.put("token", token)
            upMap.put("userId", userId)
            presenter?.up(upMap, 178, ResponseBean.BooksBean::class.java)
            b = false
        } else if (b) {
            showSnackbar(swipe!!, "没有更多数据了_", 2000)
            b = false
        }
    }

    private val obs = object : Adapter.AdapterObs<ResponseBean.Menu> {

        override fun onBind(holder: Adapter.Holder?, bean: MutableList<ResponseBean.Menu>?, position: Int) {
            val img = holder!!.find<ImageView>(R.id.iconImg)
            val name = holder.find<TextView>(R.id.name)
            val author = holder.find<TextView>(R.id.author)
            val price = holder.find<TextView>(R.id.price)
            val constPrice = holder.find<TextView>(R.id.const_price)

            constPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
            img.layoutParams.width = width
            img.layoutParams.height = width * 1389 / 1000

            val data = bean!![position]
            val sPrice: CharSequence = "¥${data.price}"
            val sConstPrice: CharSequence = "¥${data.constPrice}"

            img.scaleType = ImageView.ScaleType.CENTER_INSIDE

            val options = RequestOptions()
                    .placeholder(R.drawable.book_place_bg)

            Glide.with(img)
                    .load(data.iconImg)
                    .apply(options)
                    .into(img)

            if (data.author.isEmpty()) {
                author.visibility = View.GONE

            } else author.visibility = View.VISIBLE

            name.text = data.bookName
            author.text = data.author
            price.text = sPrice
            constPrice.text = sConstPrice

            holder.itemView.setOnClickListener {
                val intent = Intent(context, BooksDetailsViewImpl::class.java)
                intent.putExtra("bookId", data.bookId)
                intent.putExtra("iconImg", data.iconImg)
                intent.putExtra("name", data.bookName)
                startActivity(intent)
            }
        }

        override fun layoutId(): Int = R.layout.item_books
    }

    private val scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE)
                if (!recyclerView!!.canScrollVertically(1))
                    requestCount = mAdapter.itemCount - 1
        }
    }

    override fun getContentLayoutId(): Int = R.layout.fragment_books

    override fun initArgs(bundle: Bundle?) {
        presenter = BooksImpl(this@BooksViewImpl)
        // 白底黑字状态栏 . api大于23 (Android6.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.statusBarColor = ContextCompat.getColor(context, R.color.status_bar)
        }
    }

    override fun initWidget(root: View) {
        swipe = root.findViewById(R.id.swipe)
        recycler = root.findViewById(R.id.recycler)
        recycler.layoutManager = GridLayoutManager(activity, spanCount)
        mAdapter = Adapter(obs, list)
        recycler.adapter = mAdapter
        recycler.addItemDecoration(SpacesItemDecoration(spanCount, spacing, false))
    }

    override fun initData() {
        swipe?.setOnRefreshListener {
            presenter?.down(map, 178, ResponseBean.BooksBean::class.java)
        }
        presenter?.down(map, 178, ResponseBean.BooksBean::class.java)
    }

    override fun notifyData() {
        mAdapter.notifyDataSetChanged()
        b = true
    }

    override fun removeData(index: Int) {
        list.removeAt(index)
    }

    override fun addAll(t: ResponseBean.BooksBean) {
        list.addAll(t.menuList)
    }

    override fun removeAll() {
        list.clear()
    }

    override fun showToast(str: String) {
        ToastUtil.showToast(str)
    }

    override fun pullRefresh(b: Boolean) {
        swipe?.post {
            swipe?.isRefreshing = b
        }
    }

    override fun onStart() {
        recycler.addOnScrollListener(scrollListener)
        super.onStart()
    }

    override fun onStop() {
        recycler.removeOnScrollListener(scrollListener)
        super.onStop()
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        presenter = null
        super.onDestroy()
    }
}
