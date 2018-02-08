package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.showSnackBar
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.mview.common.BigPicActivity
import java.util.*
import kotlin.properties.Delegates

/**
 * @author by user on 2018/1/2.
 */
class ParentsQ : Fragment() {

    // view
	private var mRecycle by notNullSingleValue<RecyclerView>()
	private var mSwipe by notNullSingleValue<SwipeRefreshLayout>()
	private var mFootText by notNullSingleValue<TextView>()
	private var mFootPrg by notNullSingleValue<ProgressBar>()
	private var mToolBar by notNullSingleValue<Toolbar>()

	private var admMenu: MenuItem? = null

    // fields
	private var userId by preference("userId", "")
	private var token by preference("token", "")
	private var cacheJson by preference("cacheJson", "") // 缓存以Json格式存储本地 , 也可以存储到服务器
	private val role by preference("role", 1) // role = 2 为管理员

	private var mAdapter: AdapterK<ResponseBean.QADetail> by notNullSingleValue() // RecyclerView适配器 (HeaderView FooterView)

    // property & cacheList
	private val map = hashMapOf<CharSequence, CharSequence>() // 网络请求map
	private val list = arrayListOf<ResponseBean.QADetail>() // 返回数据集合
    private val cacheList = arrayListOf<String>() // 缓存列表, 保存createDate
    private val savedList = arrayListOf<String>() // 避免cacheList读取冲突
	private val options by lazy {
        return@lazy RequestOptions()
                .placeholder(R.drawable.deftimg)
                .error(R.drawable.deftimg)
    }
    private var filterMaxDate by Delegates.vetoable("") { _, old, new ->
        if (new.isEmpty()) return@vetoable true

        if (old.isNotEmpty()) {
            val one = toDateLong(new)
            val two = toDateLong(old)

            return@vetoable one > two
        }

        return@vetoable true
    }
    private var filterMinDate by Delegates.vetoable("") { _, old, new ->
        if (new.isEmpty()) return@vetoable true

        if (old.isNotEmpty()) {
            val one = toDateLong(new)
            val two = toDateLong(old)

            return@vetoable one < two
        }

        return@vetoable true
    }

    // 图片等宽
	private val imgWidth by lazy {
		val point = Point()
		activity.windowManager.defaultDisplay.getSize(point)
		(point.x - resources.getDimension(R.dimen.dp30)) / 3
	}

    // cache
	private var endDate = ""
    private var topDate = ""
    private var cacheIndex = 0 // 读取缓存下标

    // status
	private var b = true // 加载完成提示 只显示一次
	private var over = false // 是否读取完缓存列表
	private var openCache = true // 是否开启缓存
	private var isUpdate = false

	override fun getContentLayoutId() = R.layout.fragment_parents_q

	override fun initWidget(root: View) {
		mRecycle = root.findViewById(R.id._recycler)
		mSwipe = root.findViewById(R.id._swipe)
		mToolBar = root.findViewById(R.id._toolbar)

		root.findViewById<ConstraintLayout>(R.id._parent)
				.setOnClickListener { startActivity(Intent(context, ParentsQA::class.java)) }

		mRecycle.layoutManager = LinearLayoutManager(context,
				LinearLayoutManager.VERTICAL, false)
		mRecycle.adapter = AdapterK(this::itemBind,
				R.layout.item_qa_1, list).also { mAdapter = it }

		setScrollChangedListener()

		mSwipe.setOnRefreshListener {
			downData()
		}

		mToolBar.title = ""

		layoutInflater.inflate(R.layout.footview_parents_qa, mRecycle, false).run {
			mFootText = findViewById(R.id.foot_loading)
			mFootPrg = findViewById(R.id.foot_progress)
			mAdapter.footView = this
		}
	}

	override fun initData() {
		if (cacheJson.isNotEmpty()) {
			val readCache = Gson()
					.fromJson<ReadCache>(cacheJson, object : TypeToken<ReadCache>() {}.type)
			topDate = readCache.top
			endDate = readCache.end
			cacheList.addAll(readCache.menu)
			savedList.addAll(cacheList)
		}

		map["userId"] = userId
		map["token"] = token

		upData()
	}

	private fun downData() {
		if (isUpdate) {
			if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
			return
		}
		if (!mSwipe.isRefreshing) mSwipe.isRefreshing = true
        logE("topDate = $topDate; endDate = $endDate")
		downRequest {
			val bean = it.menuList

            /* top取最大的createDate 也就是最新评论的时间
                如果已存在那么已存在的也需加入过滤比对中.  */
            if (!TextUtils.isEmpty(topDate)) filterMaxDate = topDate
            bean.forEach { // ForEach 循环过滤最大 & 最小 createDate
                filterMaxDate = it.createDate
                filterMinDate = it.createDate
            }

			if (bean.isEmpty()) {
				showSnackBar(mRecycle, "没有更多数据了")
				b = false
				mFootText.text = "没有更多数据了"
				mFootPrg.visibility = View.GONE

				if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
				return@downRequest
			}
			mAdapter.addDataToTop(ArrayList(bean))
			mAdapter.notifyDataSetChanged()

			if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
            b = true

			savedList.add(0, endDate)

            topDate = filterMaxDate
            endDate = filterMinDate

            filterMinDate = ""
            filterMaxDate = ""
			/*if (topDate.isEmpty())
				topDate = bean[0].createDate
			endDate = bean[bean.size - 1].createDate*/
		}
	}

	private fun upData() {
        if (!b && cacheIndex >= cacheList.size) return
		mFootPrg.visibility = View.VISIBLE
		mFootText.text = "加载中"
		isUpdate = true
        logE("topDate = $topDate; endDate = $endDate")
		upRequest {
			val bean = it.menuList.also { logE("empty ? ${it.isEmpty()}") }

            /* top取最大的createDate 也就是最新评论的时间
                如果已存在那么已存在的也需加入过滤比对中.  */
            if (!TextUtils.isEmpty(topDate)) filterMaxDate = topDate
            bean.forEach { // ForEach 循环过滤最大 & 最小 createDate
                filterMaxDate = it.createDate
                filterMinDate = it.createDate
            }

            logE("xinStatus = ${it.xinStatus}")
			if (bean.isEmpty() && /*bean[bean.size - 1].createDate == endDate &&*/ over) {
				if (b) {
					mFootText.text = "没有更多数据了"
					mFootPrg.visibility = View.GONE
					b = false
				}
                isUpdate = false
				return@upRequest
			}
			mAdapter.addData(bean)
			mAdapter.notifyDataSetChanged()

			// 改变FooterView状态
			mFootText.text = "加载更多"
			mFootPrg.visibility = View.GONE
			isUpdate = false
            b = true

			// 缓存
			if (over) {
				savedList.add(endDate)
				endDate = filterMinDate
			}

            topDate = filterMaxDate

            filterMaxDate = ""
            filterMinDate = ""

			/*if (TextUtils.isEmpty(topDate))
				topDate = bean[0].createDate*/
		}
	}

	@SuppressLint("SetTextI18n")
	private fun itemBind(holder: Adapter.Holder,
	                     mutableList: MutableList<ResponseBean.QADetail>, position: Int) {
		val parent = holder.find<ConstraintLayout>(R.id.item_parent)
		val title = holder.find<TextView>(R.id.item_title)
		val reply = holder.find<TextView>(R.id.item_reply)
		val img1 = holder.find<ImageView>(R.id.item_img1)
				.also { it.layoutParams = it.layoutParams.also { it.width = imgWidth.toInt() } }
		val img2 = holder.find<ImageView>(R.id.item_img2)
				.also { it.layoutParams = it.layoutParams.also { it.width = imgWidth.toInt() } }
		val img3 = holder.find<ImageView>(R.id.item_img3)
				.also { it.layoutParams = it.layoutParams.also { it.width = imgWidth.toInt() } }

		val data = mutableList[position]

		parent.setOnClickListener {
			startActivity(Intent(context, ParentsQA::class.java)
					.putExtra("taskId", data.id.toString())
					.putExtra("commentId", data.id.toString()))
		}

		title.text = data.title
		reply.text = "${data.comment_count}回答"

		if (data.imgs == null) {
			img1.visibility = View.GONE
			return
		}

		img1.visibility = if (data.imgs.isEmpty()) View.GONE else View.VISIBLE

		if (data.imgs.isNotEmpty()) {
			val split = data.imgs.split("\\|".toRegex())
			when (split.size) {
				1 -> {
					Glide.with(img1).load(split[0]).apply(options).into(img1)
					img1.setOnClickListener {
                        BigPicActivity.launch(activity as AppCompatActivity, img1, split[0])
                    }
					img2.visibility = View.GONE
				}
				2 -> {
					Glide.with(img1).load(split[0]).apply(options).into(img1)
					Glide.with(img2).load(split[1]).apply(options).into(img2)
                    img1.setOnClickListener {
                        BigPicActivity.launch(activity as AppCompatActivity, img1, split[0])
                    }
                    img2.setOnClickListener {
                        BigPicActivity.launch(activity as AppCompatActivity, img2, split[1])
                    }
					img2.visibility = View.VISIBLE
					img3.visibility = View.GONE
				}
				3 -> {
					Glide.with(img1).load(split[0]).apply(options).into(img1)
					Glide.with(img2).load(split[1]).apply(options).into(img2)
					Glide.with(img3).load(split[2]).apply(options).into(img3)
                    img1.setOnClickListener {
                        BigPicActivity.launch(activity as AppCompatActivity, img1, split[0])
                    }
                    img2.setOnClickListener {
                        BigPicActivity.launch(activity as AppCompatActivity, img2, split[1])
                    }
                    img3.setOnClickListener {
                        BigPicActivity.launch(activity as AppCompatActivity, img3, split[2])
                    }
					img2.visibility = View.VISIBLE
					img3.visibility = View.VISIBLE
				}
			}
		}
	}

	private fun downRequest(success: (ResponseBean.QABody) -> Unit) {
		map.run {
			put("createDate", topDate)
			put("endDate", endDate)
			NetBuild.response(success, { showToast(it); mSwipe.isRefreshing = false },
					199, ResponseBean.QABody::class.java, this)
		}
	}

	private fun upRequest(success: (ResponseBean.QABody) -> Unit) {
		val date = when {
			(cacheList.isNotEmpty() && cacheIndex < cacheList.size) -> {
				cacheList[cacheIndex++]
			}
			else -> {
				over = true
				endDate
			}
		}

		map.run {
			put("createDate", topDate)
			put("endDate", date)
			NetBuild.response(success, ToastUtil::showToast,
					199, ResponseBean.QABody::class.java, this)
		}
	}

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

	override fun onSaveInstanceState(outState: Bundle?) {
		saveCache()
	}

	override fun onDestroy() {
		saveCache()
		super.onDestroy()
	}

	private fun saveCache() {
		if (openCache)
			hashMapOf<String, Any>().run {
				put("end", endDate)
				put("top", topDate)
				put("menu", savedList)

				cacheJson = Gson().toJson(this)
			}
	}

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

	override fun onResume() {
		super.onResume()
		if (role == 2) {
			setHasOptionsMenu(true)
			(activity as AppCompatActivity).setSupportActionBar(mToolBar)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		if (role == 2) {
			inflater?.inflate(R.menu.menu_role_item, menu)
			admMenu = menu?.findItem(R.id.menu_clear)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		if (role == 1) return false

		when (item?.itemId) {
			R.id.menu_clear -> {
				openCache = if (openCache) {
					admMenu?.title = "打开缓存"
					cacheJson = ""
					false
				} else {
					admMenu?.title = "清除缓存并关闭"
					true
				}
			}
		}
		return true
	}
}