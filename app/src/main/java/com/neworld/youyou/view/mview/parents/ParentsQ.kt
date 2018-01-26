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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.showSnackBar
import com.neworld.youyou.utils.*
import org.slf4j.MDC.put
import java.util.*

/**
 * @author by user on 2018/1/2.
 */
class ParentsQ : Fragment() {

	private var mRecycle by notNullSingleValue<RecyclerView>()
	private var mSwipe by notNullSingleValue<SwipeRefreshLayout>()
	private var mFootText by notNullSingleValue<TextView>()
	private var mFootPrg by notNullSingleValue<ProgressBar>()
	private var mToolBar by notNullSingleValue<Toolbar>()

	private var admMenu: MenuItem? = null

	private var userId by preference("userId", "")
	private var token by preference("token", "")
	private var cacheJson by preference("cacheJson", "") // 缓存以Json格式存储本地 , 也可以存储到服务器
	private val role by preference("role", 1) // role = 2 为管理员

	private var mAdapter: AdapterK<ResponseBean.QADetail> by notNullSingleValue() // RecyclerView适配器 (HeaderView FooterView)

	private val map = hashMapOf<CharSequence, CharSequence>() // 网络请求map
	private val list = arrayListOf<ResponseBean.QADetail>() // 返回数据集合

	private val imgWidth by lazy {
		// 图片等宽
		val point = Point()
		activity.windowManager.defaultDisplay.getSize(point)
		(point.x - resources.getDimension(R.dimen.dp17)) / 3
	}

	private var endDate = ""
	private var topDate = ""

	private var b = true // 加载完成提示 只显示一次
	private var over = false // 是否读取完缓存列表
	private var openCache = true // 是否开启缓存
	private var isUpdate = false

	private val cacheList = arrayListOf<String>() // 缓存列表, 保存createDate

	private val savedList = arrayListOf<String>() // 避免cacheList读取冲突
	private var cacheIndex = 0 // 读取缓存下标


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
		downRequest {
			val bean = it.menuList
			if (bean.isEmpty() || bean[bean.size - 1].createDate == endDate) {
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

			savedList.add(0, endDate)

			if (topDate.isEmpty())
				topDate = bean[0].createDate
			endDate = bean[bean.size - 1].createDate
		}
	}

	private fun upData() {
        if (!b) return
		mFootPrg.visibility = View.VISIBLE
		mFootText.text = "加载中"
		isUpdate = true
		upRequest {
			val bean = it.menuList
			if (bean.isEmpty() || bean[bean.size - 1].createDate == endDate && over) {
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

			// 缓存
			if (over) {
				savedList.add(endDate)
				endDate = bean[bean.size - 1].createDate
			}

			if (TextUtils.isEmpty(topDate))
				topDate = bean[0].createDate
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
					.putExtra("taskId", data.from_uid)
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
					Glide.with(img1).load(split[0]).into(img1)
					img2.visibility = View.GONE
				}
				2 -> {
					Glide.with(img1).load(split[0]).into(img1)
					Glide.with(img2).load(split[1]).into(img2)
					img2.visibility = View.VISIBLE
					img3.visibility = View.GONE
				}
				3 -> {
					Glide.with(img1).load(split[0]).into(img1)
					Glide.with(img2).load(split[1]).into(img2)
					Glide.with(img3).load(split[2]).into(img3)
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
        logE("map : $map")
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