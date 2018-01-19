package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMWeb
import kotlinx.android.synthetic.main.activity_parent_qa.*
import org.jetbrains.anko.doAsync
import kotlin.properties.Delegates

/**
 * @author by user on 2018/1/4.
 */
class ParentsQA : Activity() {
	
	private val userId by preference("userId", "")
	private val map = hashMapOf<CharSequence, CharSequence>()
	
	// 数据
	private val list = arrayListOf<ResponseBean.AnswerList>()
	private var result: ResponseBean.Result by Delegates.notNull()
	// adapter
	private var mAdapter by notNullSingleValue<AdapterK<ResponseBean.AnswerList>>()
	
	private var b = true
	private var measured = false
	
	// header
	private var headTitle by notNullSingleValue<TextView>()
	private var headIcon by notNullSingleValue<ImageView>()
	private var headContent by notNullSingleValue<TextView>()
	
	// footer
	private var footText by notNullSingleValue<TextView>()
	private var footPrg by notNullSingleValue<ProgressBar>()
	
	private var tskId by Delegates.notNull<Int>()
	
	override fun getContentLayoutId() = R.layout.activity_parent_qa
	
	override fun initWindows() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
			window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)
		}
	}
	
	@SuppressLint("SetTextI18n")
	override fun initWidget() {
		_recycle.run {
			layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
			adapter = AdapterK(this@ParentsQA::bind, R.layout.item_answer, list).also { mAdapter = it }
			addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
		}
		
		_toolbar.title = ""
		setSupportActionBar(_toolbar)
		
		_star.setOnClickListener {
			hashMapOf<CharSequence, CharSequence>().run {
				put("userId", userId)
				put("taskId", "1613") // taskId 暂用1613 其他的没数据
				put("type", "5")
				put("status", if (_star.isChecked) "1" else "0")
				
				doAsync {
					val response = NetBuild.getResponse(this@run, 112)
					runOnUiThread {
						_star.text = if ("0" in response && _star.isChecked)
							"已收藏"
						else
							"${(_star.tag as Int)}人收藏"
					}
				}
			}
		}
		
		_close.setOnClickListener { finish() }
		
		_swipe.setOnRefreshListener {
			initData()
			_swipe.isRefreshing = false
		}
		_answer.setOnClickListener {
			startActivity(Intent(this, Answers::class.java)
					.putExtra("uid", result.from_uid))
		}
		
		_recycle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE)
					if (!_recycle.canScrollVertically(1))
						upData()
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
			headContent = findViewById(R.id.head_content)
			findViewById<View>(R.id.head_toggle).setOnClickListener {
				it.visibility = View.GONE
				headContent.setSingleLine(false)
			}
			mAdapter.setHeadView(this)
		}
	}
	
	override fun initData() {
		tskId = intent.getIntExtra("taskId", 0)
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
		val praise = holder.find<CheckBox>(R.id.item_praise)
		val icon = holder.find<ImageView>(R.id.item_icon)
		val content = holder.find<TextView>(R.id.item_content)
		val praises = holder.find<TextView>(R.id.item_praise_count)
		val read = holder.find<TextView>(R.id.item_read_count)
		val img = holder.find<ImageView>(R.id.item_img)
		
		val data = mutableList[position]
		
		/*val getter = Html.ImageGetter {
			img.visibility = if (!TextUtils.isEmpty(it)) {
				Glide.with(img).load(it).into(img)
				View.VISIBLE
			} else
				View.GONE

			if (img.visibility == View.VISIBLE) img.drawable else null
		}*/
		
		content.text = data.content
		
		name.text = data.from_nickName
		praises.text = "${data.commentLike}赞"
		read.text = if (data.clickSum > 0) "${data.clickSum}万阅读" else "0阅读"
		
		Glide.with(icon).load(data.faceImg).into(icon)
		
		praise.isChecked = data.likeCommentStatus == 0
		
		img.visibility = View.GONE
		
		praise.setOnClickListener {
			hashMapOf<CharSequence, CharSequence>().run {
				put("userId", userId)
				put("commentId", data.commentId.toString())
				put("type", "5")
				put("status", if (praise.isChecked) "1" else "0")
				
				val response = NetBuild.getResponse(this@run, 193)
				logE("map : $this")
				logE("response : $response")
				if ("0" !in response) {
					showToast("数据错误, 错误代码 {PtsQA}, 请到用户反馈处反馈此问题. 谢谢")
				}
			}
		}

//		content.post {
//			LogUtils.logE("lineCount = ${content.lineCount}, position = $position")
//			LogUtils.logE("text = ${content.text}")
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
		
		result = t.result
		
		_answer_count.text = "${result.comment_count}个回答"
		_star.run {
			text = if (result.collectStatus == 0) {
				isChecked = true
				"已收藏"
			} else {
				isChecked = false
				"${result.collect_count}人收藏"
			}
			tag = result.collect_count
		}
		
		headTitle.text = result.title
		headContent.text = result.content
		val options = RequestOptions()
				.placeholder(R.drawable.deftimg)
				.error(R.drawable.deftimg)
		Glide.with(headIcon).load(result.imgs).apply(options).into(headIcon)
		
		if (!measured)
			_star.post {
				val point = Point()
				val left = windowManager.defaultDisplay.getSize(point).let {
					(point.x - (_star.measuredWidth * 3)) / 6
				}
				
				_answer_count.setPadding(left, 0, left, 0)
				_star.setPadding(left, 0, left, 0)
				
				_answer.layoutParams = _answer.layoutParams.also {
					it.width = left * 2 + _star.measuredWidth
				}
				
				measured = true
			}
	}
	
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_item, menu)
		val item = menu?.findItem(R.id.menu_item1)
		item?.icon = item?.icon?.also {
			val wrap = DrawableCompat.wrap(it)
			DrawableCompat.setTint(wrap, ContextCompat.getColor(baseContext, R.color.black))
		} // 不确定是否管用 (setTint)
		return true
	}
	
	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		if (item != null && item.itemId == R.id.menu_item1) {
			logE("条目点击")
			val url = "http://www.uujz.me:8082/neworld/user/143?taskId=$tskId&userId=$userId"
			UMWeb(url).run {
				title = headTitle.text.toString()
				description = headContent.text.toString()
				ShareAction(this@ParentsQA)
						.withMedia(this)
						.setDisplayList(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ,
								SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
						.setCallback(object : UMShareListener {
							override fun onResult(p0: SHARE_MEDIA?) {
								logE("分享成功 ?")
							}
							
							override fun onCancel(p0: SHARE_MEDIA?) {
								logE("取消分享")
							}
							
							override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
								logE("分享失败")
							}
							
							override fun onStart(p0: SHARE_MEDIA?) {
								logE("开始分享")
							}
						})
			}
			return true
		}
		return false
	}
}