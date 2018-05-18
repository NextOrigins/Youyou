package com.neworld.youyou.view.mview.books

import android.content.Intent
import android.graphics.Paint
import android.graphics.Point
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.neworld.youyou.R
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.SpacesItemDecoration
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.presenter.books.BooksImpl
import com.neworld.youyou.showSnackBar
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.mview.common.RecyclerDataView
import java.util.*
import kotlin.properties.Delegates

/**
 * @author by user on 2017/11/17.
 */
class BooksViewImpl : Fragment(), RecyclerDataView<ResponseBean.BooksBean> {

    private val spacing = 40F
    private val spanCount = 3
    private val width: Int by lazy {
        val point = Point()
        activity?.windowManager?.defaultDisplay?.getSize(point)
        ((point.x - spacing * (spanCount - 1)) / spanCount).toInt() - 20
    }

    private var swipe: SwipeRefreshLayout? = null

    private val list: ArrayList<ResponseBean.Menu> = ArrayList()
    private var mAdapter: Adapter<ResponseBean.Menu> by notNullSingleValue()
    private var presenter: BooksImpl<ResponseBean.BooksBean>? = null

    private var recycler: RecyclerView by notNullSingleValue()
    private var progress: ProgressBar by notNullSingleValue()

    private val token by preference("token", "")
    private val userId by preference("userId", "")

    private val map by lazy {
        hashMapOf<CharSequence, CharSequence>(Pair("type", "0"), Pair("CreateDate", "")
                , Pair("token", token), Pair("userId", userId))
    }

    private var b: Boolean = true

    private var requestCount by Delegates.observable(0) { _, _, new ->
        when (new) {
            1 -> {
                if (b && list.isNotEmpty()) hashMapOf<CharSequence, CharSequence>().run {
                    put("type", "0")
                    put("CreateDate", list[list.size - 1].createDate)
                    put("token", token)
                    put("userId", userId)
                    presenter?.up(this, 178, ResponseBean.BooksBean::class.java)
                    b = false
                }
            }
            2 -> showSnackBar(swipe!!, "没有更多数据了")
        }
    }

    private val obs = object : Adapter.AdapterObs<ResponseBean.Menu> {

        override fun onBind(holder: Adapter.Holder, bean: MutableList<ResponseBean.Menu>, position: Int) {
            val img = holder.find<ImageView>(R.id.iconImg)
            val cd = holder.find<CardView>(R.id.cd)
            val name = holder.find<TextView>(R.id.name)
            val author = holder.find<TextView>(R.id.author)
            val price = holder.find<TextView>(R.id.price)
            val constPrice = holder.find<TextView>(R.id.const_price)

            constPrice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
            cd.layoutParams.width = width
            cd.layoutParams.height = width * 1420 / 1000

            val data = bean[position]
            val sPrice: CharSequence = "¥${data.price}"
            val sConstPrice: CharSequence = "¥${data.constPrice}"

            img.scaleType = ImageView.ScaleType.FIT_CENTER

            val options = RequestOptions()
                    .placeholder(R.drawable.book_place_bg)

            Glide.with(img)
                    .load(data.iconImg)
                    .apply(options)
                    .into(img)

            author.visibility = if (data.author.isEmpty()) View.GONE else View.VISIBLE

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

        override fun layoutId() = R.layout.item_books
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE)
                if (!recyclerView!!.canScrollVertically(1))
                    requestCount = 1
        }
    }

    override fun getContentLayoutId() = R.layout.fragment_books

    override fun initArgs(bundle: Bundle?) {
        presenter = BooksImpl(this@BooksViewImpl)
    }

    override fun initWidget(root: View) {
        swipe = root.findViewById(R.id.swipe)
        recycler = root.findViewById(R.id.recycler)
        progress = root.findViewById(R.id._progress)
        recycler.layoutManager = GridLayoutManager(activity, spanCount)
        mAdapter = Adapter(obs, list)
        recycler.adapter = mAdapter
        recycler.addItemDecoration(SpacesItemDecoration(spanCount, spacing, false))
        recycler.addOnScrollListener(scrollListener)
    }

    override fun initData() {
<<<<<<< HEAD
        if (mAdapter.bean.isEmpty()) {
            swipe?.setOnRefreshListener {
                presenter?.down(map, 178, ResponseBean.BooksBody::class.java)
            }
            presenter?.down(map, 178, ResponseBean.BooksBody::class.java)
        }
=======
        swipe?.setOnRefreshListener {
            presenter?.down(map, 178, ResponseBean.BooksBean::class.java)
        }
        presenter?.down(map, 178, ResponseBean.BooksBean::class.java)
>>>>>>> parent of 8d52dad... 17_12_19
    }

    override fun notifyData() {
        mAdapter.notifyDataSetChanged()
        b = true
    }

    override fun removeData(index: Int) {
        list.removeAt(index)
    }

    override fun addAll(t: ResponseBean.BooksBean) {
        if (list.isNotEmpty()) {
            list.run {
                val menu = get(size - 1)
                val p0 = t.menuList
                val p1: ResponseBean.Menu? = if (p0.isNotEmpty()) p0[p0.size - 1] else null
                if (p1 == null || menu.createDate == p1.createDate) {
                    requestCount = 2
                } else {
                    list.addAll(p0)
                    notifyData()
                }
            }
        } else
            list.addAll(t.menuList)
    }

    override fun removeAll() {
        list.clear()
    }

    override fun showToast(str: String) {
        ToastUtil.showToast(str)
    }

    override fun showProgress() = progress.run { visibility = View.VISIBLE }

    override fun hideProgress() = progress.run { visibility = View.GONE }

    override fun pullRefresh(b: Boolean) {
        swipe?.post {
            swipe?.isRefreshing = b
        }
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        presenter = null
        super.onDestroy()
    }
}
