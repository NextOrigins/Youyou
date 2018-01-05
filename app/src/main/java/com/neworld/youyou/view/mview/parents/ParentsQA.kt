package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.system.Os.bind
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
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
		
		_close.setOnClickListener { finish() }
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
		praises.text = "${data.commentId}赞"
		read.text = "${data.taskId}万阅读"
		
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
	
	private fun success(t: ResponseBean.AnswerBody) {
		list.addAll(t.stickNamicfoList)
		list.addAll(t.menuList)
		mAdapter.notifyDataSetChanged()
		
		val data = t.result
		
		_answer_count.text = "${data.comment_count}个回答"
		_star.text = "${data.collect_count}人收藏"
		
		_star.post {
			val point = Point()
			val height = resources.getDimension(R.dimen.dp50)
			val left = windowManager.defaultDisplay.getSize(point).let {
				(point.x - (_star.measuredWidth + _answer_count.measuredWidth + _answer.measuredWidth)) / 6
			}
			val top = ((height - _star.measuredHeight) / 2).toInt()
			
			_answer_count.setPadding(left, top, left, top)
			_star.setPadding(left, top, left, top)
			
			_answer.layoutParams = _answer.layoutParams.also {
				it.width = left * 2 + _answer.measuredWidth
				it.height = height.toInt()
			}
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_item, menu)
		return true
	}
}