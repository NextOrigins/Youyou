package com.neworld.youyou.add.feed

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.activity.PhotoDetailActivity
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ReportBean
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.activity_feed_back.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

import java.io.Serializable

/**
 * @author by user on 2017/11/6.
 */

class FeedbackActivity : Activity(), View.OnClickListener {
	
	private var mAdapter: Adapter<ReportBean.ResultsBean>? = null
	internal var list = arrayListOf<ReportBean.ResultsBean>()
	private val userId by preference("userId", "")
	private val role by preference("role", 1)
	
	override fun getContentLayoutId() = R.layout.activity_feed_back
	
	override fun initWidget() {
		feed_back_recycler.run {
			layoutManager = LinearLayoutManager(this@FeedbackActivity,
					LinearLayoutManager.VERTICAL, false)
			adapter = AdapterK(this@FeedbackActivity::itemBind,
					R.layout.item_feed_back, list).also { mAdapter = it }
			addItemDecoration(DividerItemDecoration(this@FeedbackActivity, DividerItemDecoration.VERTICAL))
		}
		
		feed_close.setOnClickListener { finish() }
		feed_posted.setOnClickListener(this)
//        feed_posted.setOnClickListener { startActivity(Intent(this@FeedbackActivity, ReplyViewImpl::class.java)) }
		
		_swipe.setOnRefreshListener {
			initData()
			_swipe.isRefreshing = false
		}
	}
	
	override fun initData() = with(hashMapOf<CharSequence, CharSequence>()) {
		put("userId", userId)
		when (role) {
			1 -> {
				NetBuild.response(this@FeedbackActivity::dataInit,
						ToastUtil::showToast, 172, ReportBean::class.java, this)
			}
			2 -> {
				NetBuild.response(this@FeedbackActivity::dataInit,
						ToastUtil::showToast, "172_1", ReportBean::class.java, this)
			}
		}
	}
	
	private fun dataInit(p0: ReportBean) = with(p0) {
		if (status == 0 && results != null && results.isNotEmpty()) {
			list.clear()
			list.addAll(results)
			mAdapter?.notifyDataSetChanged()
		}
	}
	
	private fun itemBind(holder: Adapter.Holder,
	                     data: MutableList<ReportBean.ResultsBean>,
	                     position: Int) = with(data[position]) {
		
		val mContent = holder.find<TextView>(R.id.back_content) // 反馈内容
		val mDate = holder.find<TextView>(R.id.back_date) // 反馈日期
		val mState = holder.find<TextView>(R.id.back_state) // 状态
		val mIv = holder.find<ImageView>(R.id.back_img) // 图片
		val mLine = holder.find<View>(R.id.back_line) // 底线
		val mAdmin = holder.find<TextView>(R.id.back_admin) // 管理员回复
		
		mContent.text = content
		mDate.text = createDate
		
		if (status == 1) {
			mState.text = "待处理"
			mState.setBackgroundResource(R.drawable.feed_back_bg)
			mState.setTextColor(Color.parseColor("#AA0000"))
			mLine.visibility = View.GONE
			mAdmin.visibility = View.GONE
		} else {
			mState.text = "已处理"
			mState.setBackgroundResource(R.drawable.feed_back_adm_bg)
			mState.setTextColor(Color.parseColor("#62CC82"))
			mLine.visibility = View.VISIBLE
			mAdmin.visibility = View.VISIBLE
			mAdmin.text = ("管理员回复:${if (imgs.isNotEmpty()) imgs[0].content.trim() else "null"}" as CharSequence).let {
				SpannableString(it).apply {
					setSpan(ForegroundColorSpan(Color.parseColor("#CCCCCC")), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
					setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 6, it.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
				}
			}
		}
		
		if (sumCount > 0) mIv.let { v ->
			v.visibility = View.VISIBLE
			Iconimgs.split("\\|".toRegex()).let { Glide.with(v).load(it[it.size - 1]).into(v) }
		} else
			mIv.visibility = View.GONE
		
		mIv.setOnClickListener {
			Intent(applicationContext, PhotoDetailActivity::class.java).let { itt ->
				val split = Iconimgs.split("\\|".toRegex())
				Bundle().apply {
					putSerializable("imageList", split as Serializable)
					putString("stringUrl", split[sumCount - 1])
					putInt("currentPosition", sumCount - 1)
					putString("FromActivity", "mainActivity")
				}.let {
					itt.putExtras(it)
					startActivity(itt)
				}
			}
		}
		
		// 删除条目
		holder.find<View>(R.id.back_delete).setOnClickListener { // TODO : 删除条目管理员改为回复 .
			displayDialog(this@FeedbackActivity, "确定删除吗", {
				hashMapOf<CharSequence, CharSequence>().run {
					put("userId", userId)
					put("bugId", bugId)
					doAsync {
						val response = NetBuild.getResponse(this@run, 175)
						if (response.contains("0"))
							uiThread { mAdapter?.remove(position) }
						else
							uiThread { ToastUtil.showToast("删除失败, 请检查网络后重试") }
					}
				}
			})
		}
	}
	
	override fun onClick(v: View) {
		when (v.id) {
			R.id.feed_posted // 防止帕金森点击
			-> {
				v.setOnClickListener(null)
				startActivity(Intent(this, PostedActivity::class.java))
				Handler().postDelayed({ v.setOnClickListener(this) }, 1500)
			}
		}
	}
}

