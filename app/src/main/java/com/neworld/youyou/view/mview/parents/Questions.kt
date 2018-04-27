package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neworld.youyou.R
import com.neworld.youyou.activity.LoginActivity
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.manager.MyApplication
import com.neworld.youyou.showSnackBar
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.mview.common.HorizontalDecoration
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/**
 * @author by user on 2018/1/2.
 */
class Questions : Fragment() {

    // view
    private var mRecycle by notNullSingleValue<RecyclerView>()
    private var mSwipe by notNullSingleValue<SwipeRefreshLayout>()
    private var mFootText by notNullSingleValue<TextView>()
    private var mFootPrg by notNullSingleValue<ProgressBar>()
//    private var mToolBar by notNullSingleValue<Toolbar>()

//    private var admMenu: MenuItem? = null

    // fields
    private var userId by preference("userId", "")
    private var token by preference("token", "")
    private val mContext by lazy { MyApplication.sContext }
    // 缓存以Json格式存储本地 , 也可以存储到服务器 :: UserId当key 区别不同用户缓存
    private val prefs = getPefStorage()
    private lateinit var cacheJson: String
    private lateinit var cacheKey: String

    // RecyclerView适配器 (HeaderView FooterView)
    private var mAdapter: AdapterK<ResponseBean.QADetail> by notNullSingleValue()

    // property & cacheList
    private val mDict = hashMapOf<CharSequence, CharSequence>() // 网络请求map
    private val list = arrayListOf<ResponseBean.QADetail>() // 返回数据集合
    private val cacheList = arrayListOf<String>() // 缓存列表, 保存createDate
    private var savedList = arrayListOf<String>() // 避免cacheList读取冲突
    private val options by lazy {
        return@lazy RequestOptions()
                .placeholder(R.drawable.deftimg)
                .error(R.drawable.deftimg)
    }
    private var maxDate by Delegates.vetoable(Util.getDateFormatInstance()
            .format(Date(System.currentTimeMillis() + 1000))) { _, old, new ->
        if (old.isNotEmpty()) {
            val one = toDateLong(new)
            val two = toDateLong(old)

            return@vetoable one > two
        }

        return@vetoable true
    }
    private var minDate by Delegates.vetoable("") { _, old, new ->
        if (old.isNotEmpty()) {
            val one = toDateLong(new)
            val two = toDateLong(old)

            return@vetoable one < two
        }

        return@vetoable true
    }

    // cache
    private var cacheIndex = 0 // 读取缓存下标
    private var readLength = 6 // 每次读取缓存的个数

    // status
    private var nEnd = true // 加载完成提示 只显示一次
    private var cacheCompleted = false // 是否读取完缓存列表
    private var isUpdate = false
    private lateinit var mType: String

    //    设置内容布局
    override fun getContentLayoutId() = R.layout.fragment_parents_q

    override fun initArgs(bundle: Bundle?) {
        mType = bundle?.getString("type") ?: "0"
        cacheKey = "${Topics.CACHE_KEY}$mType"
        mDict["typeStatus"] = mType
        cacheJson = prefs.getString(cacheKey, "")

        // 相应刷新event
        val obtain = MyEventBus.INSTANCE.obtain(mType.toInt(), {
            mRecycle.smoothScrollToPosition(0)
            downData()
            null
        })
        registerStation(obtain, this@Questions.hashCode())
    }

    //    初始化控件
    override fun initWidget(root: View) {
        mRecycle = root.findViewById(R.id._recycler)
        mSwipe = root.findViewById(R.id._swipe)
//        mToolBar = root.findViewById(R.id._toolbar)

        root.findViewById<ConstraintLayout>(R.id._parent)
                .setOnClickListener { startActivity(Intent(context, QAController::class.java)) }

        mRecycle.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        mRecycle.adapter = AdapterK(this::itemBind,
                arrayOf(R.layout.item_qa_1, R.layout.item_qa_2), list, this::viewType)
                .also { mAdapter = it }
        mRecycle.addItemDecoration(HorizontalDecoration(mContext, 2))

        setScrollChangedListener()

        mSwipe.setOnRefreshListener(::downData)

//        mToolBar.title = ""

        layoutInflater.inflate(R.layout.footview_load_more, mRecycle, false).run {
            mFootText = findViewById(R.id.foot_loading)
            mFootPrg = findViewById(R.id.foot_progress)
            mAdapter.footView = this
        }
    }

    //    读取本地缓存或请求网络
    override fun initData() {
        if (userId.isEmpty()) {
            startActivity(Intent(context, LoginActivity::class.java))
            return
        }
        if (mAdapter.bean.isEmpty()) {

            if (cacheJson.isNotEmpty()) {
                val readCache = Gson()
                        .fromJson<ReadCache>(cacheJson, object : TypeToken<ReadCache>() {}.type)
                maxDate = readCache.top
                minDate = readCache.end
                cacheList.addAll(readCache.menu)
                savedList.addAll(cacheList)
            }

            mDict["userId"] = userId
            mDict["token"] = token

            upData()
        }
    }

    //    下拉加载
    private fun downData() {
        if (isUpdate) {
            if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
            return
        }
        if (!mSwipe.isRefreshing) mSwipe.isRefreshing = true
        inRequest({
            val bean = it.menuList!!

            if (bean.isEmpty()) {
                showSnackBar(mRecycle, "暂时没有新的话题_(:з」∠)_")
                nEnd = false
                mFootText.text = "—我是有底线的—"
                mFootPrg.visibility = View.GONE

                if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
                return@inRequest
            }

            mAdapter.addDataToTop(ArrayList(bean))
            mAdapter.notifyDataSetChanged()

            if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
            nEnd = true

            val temp = arrayListOf<String>()
            bean.forEach { temp.add(it.id.toString()) }
            savedList.addAll(0, temp)
        }, 1)
    }

    //    上拉加载
    private fun upData() {
        if (!nEnd && cacheIndex >= cacheList.size || isUpdate) return

        mFootPrg.visibility = View.VISIBLE
        mFootText.text = "加载中...."
        isUpdate = true

        inRequest(::upReq, 2)
    }

    //    上拉加载详细处理
    private fun upReq(it: ResponseBean.QABody) {
        val bean = it.menuList!!
        isUpdate = false

        if (bean.isEmpty() && cacheCompleted) {
            if (nEnd) {
                mFootText.text = "—我是有底线的—"
                mFootPrg.visibility = View.GONE
                nEnd = false
            }
            return
        }

        //  判断如果有已删除的item则略继续取id。
        if (bean.size < readLength && !cacheCompleted) {
            mAdapter.addData(bean)
            val l = bean.size - 1
            val r = mAdapter.bean.size
            val idList = bean.map { it.id.toString() }
            val temp = arrayListOf<String>()
            cacheList.takeLast(r - l).forEach {
                if (!idList.contains(it)) temp.add(it)
            }
            if (temp.isNotEmpty()) temp.forEach {
                cacheList.remove(it)
                savedList.remove(it)
            }

            cacheIndex -= temp.size
            readLength -= bean.size
            upData()
            return
        } else if (!cacheCompleted) {
//            val count = cacheList.size - cacheIndex // 如果需要加载缓存的时候不混合加载新数据 就启用这段代码。
//            readLength = if (count > 6) 6 else count
            readLength = 6
        }

        mAdapter.addData(bean)
        mAdapter.notifyDataSetChanged()

        // 改变FooterView状态
        mFootText.text = "加载更多"
        mFootPrg.visibility = View.GONE
        nEnd = true

        // 缓存
        if (cacheCompleted) {
            bean.forEach { savedList.add(it.id.toString()) }
        }

        if (mAdapter.bean.size < 7 && cacheList.isNotEmpty()) {
            downData()
        } else if (mAdapter.getSize() < 6 && cacheCompleted) {
            mFootText.text = "—我是有底线的—"
            mFootPrg.visibility = View.GONE
            nEnd = false
        }
    }

    /**
     * 发送消息到Topics处理登录事件
     */
    private fun toLogin() {
        MyEventBus.INSTANCE.postEvent(Topics.TO_LOGIN)
    }

    //    网络请求
    private fun inRequest(s: (ResponseBean.QABody) -> Unit, type: Int) {
        when (type) {
            1 -> {
                mDict["createDate"] = maxDate ?: ""
                mDict["endDate"] = minDate
                mDict["type"] = type.toString()

                response<ResponseBean.QABody>({ onResponse(s, it) }, 199, mDict, ::onFailed)
            }
            2 -> {
                if (cacheList.isNotEmpty() && cacheIndex < cacheList.size) {
                    mDict["id"] = spliceId()

                    response<ResponseBean.QABody>({ onResponse(s, it, true) }, "199_1", mDict, ::onFailed)
                } else {
                    cacheCompleted = true

                    mDict["createDate"] = maxDate ?: ""
                    mDict["endDate"] = minDate
                    mDict["type"] = type.toString()

                    response<ResponseBean.QABody>({ onResponse(s, it) }, 199, mDict, ::onFailed)
                }
            }
        }

        if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
    }

    // 统一处理后分发
    private fun onResponse(s: (ResponseBean.QABody) -> Unit, model: ResponseBean.QABody, isCache: Boolean = false) {
        // 判断是否多端登录
        if (model.tokenStatus > 1) {
            toLogin()
            return
        }
        if (model.menuList == null) {
            // TODO : 这里可以调用用户反馈接口
            showToast("出错啦，如果多次出现此问题请重新登录账号[0b-327]")
            return
        }
        if (!isCache) model.menuList.forEach {
            // ForEach 循环过滤最大 & 最小 createDate
            maxDate = it.createDate
            minDate = it.createDate
        }

        s.invoke(model)
    }

    private fun onFailed(e: String) {
        logE(e)
        isUpdate = false
        cacheIndex -= readLength
        if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
        if (mFootPrg.visibility == View.VISIBLE) {
            mFootPrg.visibility = View.GONE
            mFootText.text = "请检查网络后重试"
        }
        showToast("请检查您的网络")
    }

    //    多type判断
    private fun viewType(model: ResponseBean.QADetail) = with(model.imgs?.split('|')?.size) {
        if (this == null || this >= 3) AdapterK.TYPE_NORMAL
        else AdapterK.TYPE_ONE
    }

    //    填充数据
    @SuppressLint("SetTextI18n")
    private fun itemBind(holder: Adapter.Holder,
                         mutableList: MutableList<ResponseBean.QADetail>, position: Int) {

        val data = mutableList[position]
        val type = viewType(data)

        val parent: ConstraintLayout
        val title: TextView
        val reply: TextView
        val img1: ImageView
        var img2: ImageView? = null
        var img3: ImageView? = null

        when (type) {
            AdapterK.TYPE_NORMAL -> {
                parent = holder.find(R.id.item_parent)
                title = holder.find(R.id.item_title)
                reply = holder.find(R.id.item_reply)
                img1 = holder.find<ImageView>(R.id.item_img1)
                        .also { it.setWidth() }
                img2 = holder.find<ImageView>(R.id.item_img2)
                        .also { it.setWidth() }
                img3 = holder.find<ImageView>(R.id.item_img3)
                        .also { it.setWidth() }
            }
            else -> {
                parent = holder.find(R.id.item_parent)
                title = holder.find(R.id.item_title)
                reply = holder.find(R.id.item_reply)
                img1 = holder.find<ImageView>(R.id.item_img1)
                        .also { it.setWidth() }
            }
        }

        parent.setOnClickListener {
            startActivityForResult(Intent(context, QAController::class.java)
                    .putExtra("position", position)
                    .putExtra("taskId", data.id.toString())
                    .putExtra("commentId", data.id.toString()), 20)
        }

        title.text = data.title
        reply.text = "${data.comment_count}回答"

        if (data.imgs == null || data.imgs.isEmpty()) {
            img1.visibility = View.GONE
            return
        }

        img1.visibility = View.VISIBLE

        val split = data.imgs.split('|')

        if (split.size >= 3) {
            Glide.with(mContext).load(split[0]).apply(options).into(img1)
            if (img2 != null && img3 != null) {
                Glide.with(mContext).load(split[1]).apply(options).into(img2)
                Glide.with(mContext).load(split[2]).apply(options).into(img3)
            }
        } else {
            Glide.with(mContext).load(split[0]).apply(options).into(img1)
        }
    }

    //    加载更多的监听
    private fun setScrollChangedListener() {
        mRecycle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!mRecycle.canScrollVertically(1))
                        upData()
                }
            }
        })
    }

    //    拼接保存的id
    private fun spliceId(): String {
        var temp = 0
        var id = ""
        while (temp++ < readLength && cacheIndex < cacheList.size) {
            id = "$id|${cacheList[cacheIndex++]}"
        }

        return id.trim('|')
    }

    //    意外关闭时做本地储存
    override fun onSaveInstanceState(outState: Bundle) {
        saveCache()
    }

    //    正常关闭时做本地储存
    override fun onDestroy() {
        saveCache()
        unregisterStation()
        super.onDestroy()
    }

    //    本地储存实现
    private fun saveCache() {
        if (!Topics.OPEN_CACHE) return

        val filterArray = arrayListOf<String>()

        var i = 0
        while (i < savedList.size - 1) {
            val temp = savedList[i]

            if (compare(temp, i + 1)) {
                filterArray.add(temp)
            }
            i++
        }
        if (savedList.size > 2) filterArray.add(filterArray.size, savedList.last())

        val map = hashMapOf<String, Any>()
        map["end"] = minDate
        map["top"] = maxDate
        map["menu"] = filterArray

        cacheJson = Gson().toJson(map)
        // 存入本地
        prefs.edit().apply {
            putString(cacheKey, cacheJson)
        }.apply()
    }

    //    设置图片等宽
    private fun View.setWidth() {
        val point = Point()
        activity?.windowManager?.defaultDisplay?.getSize(point)
        val width = (point.x - resources.getDimension(R.dimen.dp30)
                - (resources.getDimensionPixelSize(R.dimen.dp5) * 2)) / 3

        layoutParams = layoutParams.also { it.width = width.toInt() }
    }

    //    本地缓存的model
    private data class ReadCache(val top: String = "",
                                 val end: String = "",
                                 val menu: Array<String> = arrayOf()) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ReadCache

            if (top != other.top) return false
            if (end != other.end) return false
            if (!Arrays.equals(menu, other.menu)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = top.hashCode()
            result = 31 * result + end.hashCode()
            result = 31 * result + Arrays.hashCode(menu)
            return result
        }
    }

    //    如果有一条已删除的话题，打开后自动删除本地缓存id & 更新UI
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 20) {
            if (data == null) return

            val position = data.getIntExtra("position", -1)
            if (data.getBooleanExtra("error", false) && position != -1) {
                val element = mAdapter.bean[position].id.toString()
                mAdapter.remove(position)

                val temp = savedList.filterNot { it == element }

                savedList.clear()
                savedList.addAll(temp)
            }
        }
    }

    private fun compare(p0: String, start: Int): Boolean {
        for (i in start until savedList.size) {
            if (p0 == savedList[i]) return false
        }
        return true
    }
    //    对外提供刷新列表
    /*fun resize() {
        mAdapter.notifyDataSetChanged()
    }

    //    对外提供加载更多
    fun rdRefresh() {
        mRecycle.scrollToPosition(0)
        downData()
    }

    //    对外提供关闭缓存
    fun clearCache() {
        openCache = false
    }*/
}