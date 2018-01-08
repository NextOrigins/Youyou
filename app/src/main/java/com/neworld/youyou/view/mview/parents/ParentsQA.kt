package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.system.Os.bind
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.R.id.*
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.showSnackBar
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.activity_parent_qa.*

/**
 * @author by user on 2018/1/4.
 */
class ParentsQA : Activity() {
	
	private val userId by preference("userId", "")
	private val map = hashMapOf<CharSequence, CharSequence>()
	
	// 数据
	private val list = arrayListOf<ResponseBean.AnswerList>()
	// adapter
	private var mAdapter by notNullSingleValue<AdapterK<ResponseBean.AnswerList>>()
	
	private var b = true
	
	private var headTitle by notNullSingleValue<TextView>()
	private var headIcon by notNullSingleValue<ImageView>()
	
	private var footText by notNullSingleValue<TextView>()
	private var footPrg by notNullSingleValue<ProgressBar>()
	
	override fun getContentLayoutId() = R.layout.activity_parent_qa
	
	override fun initWindows() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
			window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)
		}
	}
	
	override fun initWidget() {
		_recycle.run {
			layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
			adapter = AdapterK(this@ParentsQA::bind, R.layout.item_answer, list).also { mAdapter = it }
			addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
		}

		_toolbar.title = ""
		setSupportActionBar(_toolbar)
		
		_close.setOnClickListener { finish() }
		
		_swipe.setOnRefreshListener {
			initData()
			_swipe.isRefreshing = false
		}
		
		_recycle.addOnScrollListener(object: RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					if (!_recycle.canScrollVertically(1)) {
						upData()
					}
				}
			}
		})
		
		layoutInflater.inflate(R.layout.footview_parents_qa, _recycle, false).run {
			footText = findViewById(R.id.foot_loading)
			footPrg = findViewById(R.id.foot_progress)
			mAdapter.setFootView(this)
		}
		
		layoutInflater.inflate(R.layout.header_parent_qa, _recycle, false).run {
			headTitle = findViewById(R.id.head_title)
			headIcon = findViewById(R.id.head_img)
			mAdapter.setHeadView(this)
		}
		
	}
	
	override fun initData() {
		val taskId = intent.getStringExtra("taskId")
		val date = intent.getStringExtra("date")
		map.run {
			put("userId", userId)
			put("taskId", "1613") // 1613
			put("createDate", date)
			NetBuild.response(this@ParentsQA::success,
					ToastUtil::showToast, 200, ResponseBean.AnswerBody::class.java, this)
		}
	}
	
	private fun upData() {
		if (list.isEmpty()) return
		if (b) {
			footText.text = "加载中"
			footPrg.visibility = View.VISIBLE
		}
		map.run {
			put("createDate", list[list.size - 1].createDate)
			NetBuild.response({
				if (it.menuList.isEmpty() || it.menuList[it.menuList.size - 1].createDate == list[list.size - 1].createDate) {
					if (b) {
						footText.text = "没有更多数据了"
						footPrg.visibility = View.GONE
						b = false
					}
					return@response
				}
				mAdapter.addData(it.menuList)
				mAdapter.notifyDataSetChanged()
				footText.text = "加载更多"
				footPrg.visibility = View.GONE
			}, ToastUtil::showToast, 200, ResponseBean.AnswerBody::class.java, this)
		}
	}
	
	@SuppressLint("SetTextI18n")
	private fun bind(holder: Adapter.Holder,
	                 mutableList: MutableList<ResponseBean.AnswerList>, position: Int) {
		val name = holder.find<TextView>(R.id.item_name)
		val about = holder.find<TextView>(R.id.item_about)
		val praise = holder.find<CheckBox>(R.id.item_praise)
		val icon = holder.find<ImageView>(R.id.item_icon)
		val content = holder.find<TextView>(R.id.item_content)
		val praises = holder.find<TextView>(R.id.item_praise_count)
		val read = holder.find<TextView>(R.id.item_read_count)
		val img = holder.find<ImageView>(R.id.item_img)
		
		val data = mutableList[position]
		
		content.text = data.content
		name.text = data.from_nickName
		praises.text = "${data.commentLike}赞"
		read.text = "${data.clickSum}万阅读"
		
		Glide.with(icon).load(data.faceImg).into(icon)
		
		praise.isChecked = data.likeCommentStatus == 0
		
		img.visibility = if (data.commentImg != null && data.commentImg.isNotEmpty()) {
			Glide.with(img).load(data.commentImg).into(img)
			View.VISIBLE
			
		} else View.GONE
		
//		content.post {
//			LogUtils.E("lineCount = ${content.lineCount}, position = $position")
//			LogUtils.E("text = ${content.text}")
//			if (content.lineCount > 5) {
//
//			}
//		}
	}

	@SuppressLint("SetTextI18n")
	private fun success(t: ResponseBean.AnswerBody) {
		mAdapter.addDataAndClear(t.stickNamicfoList)
        mAdapter.addData(t.menuList)
		mAdapter.notifyDataSetChanged()
		
		b = true
		footText.text = "加载更多"
		
		val data = t.result
		
		_answer_count.text = "${data.comment_count}个回答"
		_star.text = "${data.collect_count}人收藏"
		
		headTitle.text = data.title
		Glide.with(headIcon).load(data.imgs).into(headIcon)
		
//		_star.post {
//			val point = Point()
//			val height = resources.getDimension(R.dimen.dp50)
//			val left = windowManager.defaultDisplay.getSize(point).let {
//				(point.x - (_star.measuredWidth + _answer_count.measuredWidth + _answer.measuredWidth)) / 6
//			}
//			val top = ((height - _star.measuredHeight) / 2).toInt()
//
//			_answer_count.setPadding(left, top, left, top)
//			_star.setPadding(left, top, left, top)
//
//			_answer.layoutParams = _answer.layoutParams.also {
//				it.width = left * 2 + _answer.measuredWidth
//				it.height = height.toInt()
//			}
//		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_item, menu)
		return true
	}
}