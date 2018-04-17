package com.neworld.youyou.view.mview.hot

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.neworld.youyou.R
import com.neworld.youyou.activity.HotActivity
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.preference
import com.neworld.youyou.utils.response
import com.neworld.youyou.utils.showToast

/**
 * @author by hhhh on 2018/4/13.
 */
class HotTextFragment : Fragment() {

    // ctrl
    private lateinit var mAdapter: AdapterK<ResponseBean.HotTextModel>

    // View
    private lateinit var mRecycle: RecyclerView
    private lateinit var mSwipe: SwipeRefreshLayout
    private lateinit var mFootText: TextView
    private lateinit var mFootPgs: ProgressBar

    // property
    private var cUserId by preference("userId", "")
    private var cToken by preference("token", "")
    private val mDict by lazy {
        val map = hashMapOf<CharSequence, CharSequence>()
        map["userId"] = cUserId
        map["token"] = cToken
        map["createDate"] = ""

        map
    }
    private val options by lazy {
        val opt = RequestOptions()
                .error(R.drawable.bigimage)
                .placeholder(R.drawable.bigimage)
        opt
    }
    private var loading = true

    override fun getContentLayoutId() = R.layout.fragment_hot_content

    override fun initArgs(bundle: Bundle?) {
        mDict["type"] = bundle?.getString("type") ?: "0"
    }

    override fun initWidget(root: View) {
        mRecycle = root.findViewById(R.id._recycle)
        mSwipe = root.findViewById(R.id._swipe)

        mSwipe.setOnRefreshListener {
            if (loading) {
                if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
                return@setOnRefreshListener
            }

            // 下拉刷新
            mAdapter.clear()
            mDict["createDate"] = ""
            initData()
        }

        // 配置recycle
        mRecycle.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        mRecycle.addItemDecoration(HotItemDecoration(context!!, 2))
        mRecycle.adapter = AdapterK(::itemBind,
                arrayOf(R.layout.item_hot), arrayListOf()).also { mAdapter = it }

        // 设置加载更多
        mAdapter.footView = layoutInflater.inflate(R.layout.footview_load_more,
                mSwipe, false).apply {
            mFootText = findViewById(R.id.foot_loading)
            mFootPgs = findViewById(R.id.foot_progress)
            mFootText.text = "加载中...."
        }

        // 滚动监听
        mRecycle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!mRecycle.canScrollVertically(1))
                        loadMore()
                }
            }
        })
    }

    override fun initData() {
        response(::onResponse, 131, mDict)
    }

    private fun loadMore() {
        if (loading) return

        loading = true
        mFootPgs.visibility = View.VISIBLE
        mFootText.text = getString(R.string.waiting)
        mDict["createDate"] = mAdapter.bean.last().createDate
        initData()
    }

    private fun onResponse(model: ResponseBean.HotTextBody) {
        if (loading) {
            loading = false

            mFootPgs.visibility = View.GONE
            mFootText.text = "加载更多..."
        }
        if (mSwipe.isRefreshing) {
            mSwipe.isRefreshing = false

            showToast("已更新数据")
        }

        if (model.status == 1 || model.menuList.isEmpty()) {
            mFootText.text = "已全部加载"
            mRecycle.clearOnScrollListeners()
//            loading = true
            return
        }

        mAdapter.addData(model.menuList)
        mAdapter.notifyDataSetChanged()
    }

    private fun itemBind(holder: Adapter.Holder,
                         list: MutableList<ResponseBean.HotTextModel>, position: Int) {
        val model = list[position]

        val title = holder.find<TextView>(R.id.item_title)
        val icon = holder.find<ImageView>(R.id.item_icon)
        val author = holder.find<TextView>(R.id.item_author)
        val time = holder.find<TextView>(R.id.item_time)

        title.text = model.title
        author.text = model.source
        time.text = model.createDate

        Glide.with(icon).load(model.imgs).apply(options).into(icon)

        holder.find<View>(R.id.item_parent).setOnClickListener {
            startDetail(model)
        }
    }

    private fun startDetail(model: ResponseBean.HotTextModel) {
        val taskId = model.id
        val intent = Intent()
        intent.setClass(context, HotActivity::class.java)
        val bundle = Bundle()
        bundle.putInt("taskId", taskId)
        bundle.putInt("from_uid", cUserId.toInt())

        val faceImg = model.faceImg
        if (faceImg.isNotEmpty()) {
            bundle.putString("imgs", faceImg)
        }
        val title = model.title
        if (title.isNotEmpty()) {
            bundle.putString("title", title)
        }
        val content = model.attachedContent
        if (content.isNotEmpty()) {
            bundle.putString("content", content)
        }

        intent.putExtras(bundle)
        startActivity(intent)
    }
}