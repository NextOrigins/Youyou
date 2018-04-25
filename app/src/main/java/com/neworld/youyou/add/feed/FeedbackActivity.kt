package com.neworld.youyou.add.feed

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*

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
        _recycle.layoutManager = LinearLayoutManager(this@FeedbackActivity,
                LinearLayoutManager.VERTICAL, false)
        _recycle.adapter = AdapterK(::itemBind,
                arrayOf(R.layout.item_feed_back), list).also { mAdapter = it }
        _recycle.addItemDecoration(DividerItemDecoration(this@FeedbackActivity, DividerItemDecoration.VERTICAL))

		feed_close.setOnClickListener { finish() }
		feed_posted.setOnClickListener(this)
		_revert_btn.setOnClickListener(this)
//        feed_posted.setOnClickListener { startActivity(Intent(this@FeedbackActivity, ReplyViewImpl::class.java)) }
		
		_revert_edit.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable?) {
			}
			
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
			}
			
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				_revert_btn.text = if ((s?.length ?: -1) > 0) "回复" else "取消"
			}
		})
		
		_swipe.setOnRefreshListener { initData() }
	}
	
	override fun initData() = with(hashMapOf<CharSequence, CharSequence>()) {
		put("userId", userId)
		_swipe.isRefreshing = true
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
		_swipe.isRefreshing = false
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
		val mDelete = holder.find<TextView>(R.id.back_delete) // 删除反馈

		mContent.text = content
		mDate.text = createDate
		mDelete.text = if (role == 1) "删除" else "回复"

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

		// 删除按钮
		mDelete.setOnClickListener {
			if (role == 1) {
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
			} else {
				_revert_edit.visibility = View.VISIBLE
				_revert_btn.visibility = View.VISIBLE
				_revert_edit.isFocusable = true
				_revert_edit.isFocusableInTouchMode = true
				_revert_edit.requestFocus()
				window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
				_revert_btn.tag = bugId
			}
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
			R.id._revert_btn -> {
				_revert_edit.visibility = View.GONE
				_revert_btn.visibility = View.GONE
				_revert_edit.isFocusable = false
				_revert_edit.isFocusableInTouchMode = false
				_revert_edit.clearFocus()
				val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
				if (currentFocus != null && imm.isActive) {
					if (currentFocus!!.windowToken != null) {
						imm.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
					}
				}
				
				if (_revert_btn.text == "取消") return
				
				hashMapOf<CharSequence, CharSequence>().run {
					put("userId", userId)
					put("bugId", v.tag as String)
					put("content", _revert_edit.text.trim())
					doAsync {
						val response = NetBuild.getResponse(this@run, 173)
						if ("0" in response)
							uiThread { initData() }
						else
							ToastUtil.showToast("错误 : $response")
					}
				}
			}
		}
	}
}

