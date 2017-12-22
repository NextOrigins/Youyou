package com.neworld.youyou.view.mview.ebook

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Window
import android.view.WindowManager
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.NetBuild
import com.neworld.youyou.utils.ToastUtil
import com.neworld.youyou.utils.notNullSingleValue
import com.neworld.youyou.utils.preference
import kotlinx.android.synthetic.main.activity_ebook_detail.*

/**
 * @author by user on 2017/12/22.
 */
class EBookDetail : Activity() {
	
	private val userId by preference("userId", "")
	private var bookId: String by notNullSingleValue()
	private var cf: EBookDetailContent? = null
	
	private val list = arrayListOf<ResponseBean.Type>()
	
	override fun getContentLayoutId() = R.layout.activity_ebook_detail
	
	override fun initWindows() {
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN)
	}
	
	override fun initArgs(bundle: Bundle?): Boolean {
		bookId = intent.getStringExtra("bookId")
		return super.initArgs(bundle)
	}
	
	override fun initWidget() {
		_recycler.layoutManager = LinearLayoutManager(this,
				LinearLayoutManager.VERTICAL, false)
		_recycler.setHasFixedSize(true)
		if (cf == null) {
			cf = EBookDetailContent()
		}
		supportFragmentManager.beginTransaction().apply {
			if (cf!!.isAdded)
				show(cf)
			else
				add(R.id._content, cf)
		}.commit()
	}
	
	override fun initData() {
		hashMapOf<CharSequence, CharSequence>().run {
			put("userId", userId)
			put("bookId", bookId)
			NetBuild.response({
				list.clear()
				list.addAll(it.typeList)
				cf?.setList(it.typeList[0].typeImg)
			}, ToastUtil::showToast, 198, ResponseBean.EBookContentBody::class.java, this)
		}
	}
}