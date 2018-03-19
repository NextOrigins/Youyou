package com.neworld.youyou.view.mview.common

import android.annotation.SuppressLint
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.system.Os.bind
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.activity_collect_qa.*

/**
 * @author by user on 2018/3/19.
 */
class QACollectActivity : Activity() {

//    fields
    private val userId by preference("userId", "")

//    control
    private lateinit var mAdapter: AdapterK<ResponseBean.QAMenu>

//    footView
    private var mFootText by notNullSingleValue<TextView>()
    private var mFootProgress by notNullSingleValue<ProgressBar>()

//    property
    /*private var filterDate by Delegates.vetoable("") {
        _, old, new ->
        if (old.isNotEmpty()) {
            val one = toDateLong(new)
            val two = toDateLong(old)
            return@vetoable one < two
        }

        return@vetoable true
    }*/
    private var endDate = ""

    override fun getContentLayoutId() = R.layout.activity_collect_qa

    override fun initWidget() {
        _close.setOnClickListener { finish() }

        _swipe.setOnRefreshListener {
            initData()
        }
        _recycle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        _recycle.adapter = AdapterK(::bind, R.layout.item_qa_collect, arrayListOf())
                .also { mAdapter = it }
        _recycle.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        layoutInflater.inflate(R.layout.footview_parents_qa, _recycle, false).run {
            mFootProgress = findViewById(R.id.foot_progress)
            mFootText = findViewById(R.id.foot_loading)
            mAdapter.footView = this@run
        }

        _recycle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!_recycle.canScrollVertically(1)) {
                        if (!_swipe.isRefreshing) _swipe.isRefreshing = true
                        mFootText.text = "加载中..."
                        mFootProgress.visibility = View.VISIBLE

                        val map = hashMapOf<CharSequence, CharSequence>()
                        map["userId"] = userId
                        map["createDate"] = endDate
                        map["type"] = "5"

                        response(::refresh, 113, map)
                    }
                }
            }
        })
    }

    override fun initData() {
        if (!_swipe.isRefreshing) _swipe.isRefreshing = true
        mFootText.text = "加载中..."
        mFootProgress.visibility = View.VISIBLE

        val map = hashMapOf<CharSequence, CharSequence>()
        map["userId"] = userId
        map["createDate"] = endDate
        map["type"] = "5"

        response(::success, 113, map)
    }

    private fun success(body: ResponseBean.QACollectBody) {
        if (_swipe.isRefreshing) _swipe.isRefreshing = false

        mFootText.text = "加载更多"
        mFootProgress.visibility = View.GONE

        if (body.status == 1) {
            showToast("出错了 !")
            finish()
            return
        }

        mAdapter.addData(body.menuList)
        mAdapter.notifyDataSetChanged()

        endDate = body.menuList.last().createDate
        /*body.menuList.forEach {
            filterDate = it.createDate
        }*/
    }

    private fun refresh(body: ResponseBean.QACollectBody) {
        if (_swipe.isRefreshing) _swipe.isRefreshing = false

        mFootText.text = "加载更多"
        mFootProgress.visibility = View.GONE

        if (body.status == 1) {
            showToast("出错了 !")
            finish()
            return
        }

        mAdapter.addDataAndClear(body.menuList)
        mAdapter.notifyDataSetChanged()

        endDate = body.menuList.last().createDate
    }

    @SuppressLint("SetTextI18n")
    private fun bind(holder: Adapter.Holder, mutableList: MutableList<ResponseBean.QAMenu>,
                     position: Int) {
        val title = holder.find<TextView>(R.id.item_title)
        val icon = holder.find<ImageView>(R.id.item_icon)
        val commentCount = holder.find<TextView>(R.id.item_cmt_count)

        val data = mutableList[position]

        title.text = data.title
        commentCount.text = "${data.transmit_count}评论"
        Glide.with(icon).load(data.imgs.split('|').first()).into(icon)
    }

    /*private fun loadData() {
        val map = hashMapOf<CharSequence, CharSequence>()
        map["userId"] = userId
        map["createDate"] = endDate
        map["type"] = "5"

        response(::success, 113, map)
    }*/
}