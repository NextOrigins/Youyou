package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import java.util.*

/**
 * @author by user on 2018/1/2.
 */
class ParentsQ : Fragment() {
	
	private var mRecycle by notNullSingleValue<RecyclerView>()
	private var swipe by notNullSingleValue<SwipeRefreshLayout>()
	private var footText by notNullSingleValue<TextView>()
	private var footPrg by notNullSingleValue<ProgressBar>()
	
	private var userId by preference("userId", "")
	private var token by preference("token", "")
	
	private var mAdapter: AdapterK<ResponseBean.QADetail> by notNullSingleValue()
	
	private val map = hashMapOf<CharSequence, CharSequence>()
	private val list = arrayListOf<ResponseBean.QADetail>()
	
	private val imgWidth by lazy {
		val point = Point()
		activity.windowManager.defaultDisplay.getSize(point)
		(point.x - resources.getDimension(R.dimen.dp17)) / 3
	}
	
	private var endDate = ""
	private var topDate = ""
	
	private var b = true
	
	private val cacheList = arrayListOf<String>()
	private var cacheJson by preference("cacheJson", "")
	
	override fun getContentLayoutId() = R.layout.fragment_parents_q
	
	override fun initWidget(root: View) {
		mRecycle = root.findViewById(R.id._recycler)
		swipe = root.findViewById(R.id._swipe)
		
		root.findViewById<ConstraintLayout>(R.id._parent)
				.setOnClickListener { startActivity(Intent(context, ParentsQA::class.java)) }
		
		mRecycle.layoutManager = LinearLayoutManager(context,
				LinearLayoutManager.VERTICAL, false)
		mRecycle.adapter = AdapterK(this::itemBind,
				R.layout.item_qa_1, list).also { mAdapter = it }
		
		setScrollChangedListener()
		
		swipe.setOnRefreshListener {
			downData()
			swipe.isRefreshing = false
		}
		
		layoutInflater.inflate(R.layout.footview_parents_qa, mRecycle, false).run {
			footText = findViewById(R.id.foot_loading)
			footPrg = findViewById(R.id.foot_progress)
			mAdapter.setFootView(this)
		}
	}
	
	override fun initData() {
		if (cacheJson.isNotEmpty()) {
			val readCache = Gson()
					.fromJson<ReadCache>(cacheJson, object : TypeToken<ReadCache>() {}.type)
			topDate = readCache.top
			endDate = readCache.end
			cacheList.addAll(readCache.menu)
			
			LogUtils.E("cacheList = $cacheList, topDate = $topDate, endDate = $endDate")
		}
		map.run {
			put("userId", userId)
			put("token", token)
			put("createDate", topDate)
			put("endDate", endDate)
			NetBuild.response(this@ParentsQ::upSuccess,
					ToastUtil::showToast, 199, ResponseBean.QABody::class.java, this)
		}
		
		cacheJson = ""
		LogUtils.E("cacheJson = $cacheJson")
	}
	
	private fun downData() {
		request {
			val bean = it.menuList
			if (bean.isEmpty()) {
				showSnackBar(mRecycle, "没有更多数据了")
				b = false
				footText.text = "没有更多数据了"
				footPrg.visibility = View.GONE
				return@request
			}
			mAdapter.addDataToTop(ArrayList(bean))
			mAdapter.notifyDataSetChanged()
			
			cacheList.add(0, endDate)
			
			if (topDate.isEmpty())
				topDate = bean[0].createDate
			endDate = bean[bean.size - 1].createDate
		}
	}
	
	private fun upData() {
		footPrg.visibility = View.VISIBLE
		footText.text = "加载中"
		initData()
	}
	
	private fun upSuccess(t: ResponseBean.QABody) {
		val bean = t.menuList
		if (bean.isEmpty()) {
			if (b) {
				footText.text = "没有更多数据了"
				footPrg.visibility = View.GONE
				b = false
			}
			return
		}
		mAdapter.addData(bean)
		mAdapter.notifyDataSetChanged()
		// 缓存
		cacheList.add(endDate)
		// 改变FooterView状态
		footText.text = "加载更多"
		footPrg.visibility = View.GONE
		
		if (topDate.isEmpty())
			topDate = bean[0].createDate
		endDate = bean[bean.size - 1].createDate
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
					.putExtra("date", data.createDate))
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
	
	private fun request(success: (t: ResponseBean.QABody) -> Unit) {
		map.run {
			put("createDate", topDate)
			put("endDate", endDate)
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
		hashMapOf<String, Any>().run {
			put("end", endDate)
			put("top", topDate)
			put("menu", cacheList)
			
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
}