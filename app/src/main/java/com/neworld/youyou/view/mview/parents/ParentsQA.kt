package com.neworld.youyou.view.mview.parents

import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.system.Os.bind
import android.view.Menu
import android.view.View
import android.view.WindowManager
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
	
	override fun initWidget() {
		_recycle.run {
			layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
			adapter = AdapterK(this@ParentsQA::bind, R.layout.item_answer, list).also { mAdapter = it }
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
	
	private fun bind(holder: Adapter.Holder,
	                 mutableList: MutableList<ResponseBean.AnswerList>, position: Int) {
		val content = holder.find<TextView>(R.id.item_content)
		val icon = holder.find<ImageView>(R.id.item_icon)
		
		
		val data = mutableList[position]
		
		content.text = "天生丢爱USD求氨基酸大囧氨基酸大囧氨基酸大囧氨基酸大囧氨基酸大囧氨基酸大囧我的就去玩的一器第几期丢弃的"
		
		Glide.with(icon).load(data.faceImg).into(icon)
		content.post {
			LogUtils.E("lineCount = ${content.lineCount}, position = $position")
			LogUtils.E("text = ${content.text}")
		}
	}
	
	private fun success(t: ResponseBean.AnswerBody) {
		list.addAll(t.stickNamicfoList)
		list.addAll(t.menuList)
		mAdapter.notifyDataSetChanged()
	}
	
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_item, menu)
		return true
	}
}