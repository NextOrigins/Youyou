package com.neworld.youyou.view.mview.ebook

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.activity_ebook_detail.*

/**
 * @author by user on 2017/12/22.
 */
class EBookDetail : Activity() {
	
	private val userId by preference("userId", "")
	private var bookId: String by notNullSingleValue()
	private var detailContent: EBookDetailContent? = null
	private var mAdapter by notNullSingleValue<AdapterK<ResponseBean.Type>>()
	
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
	
	override fun initData() = hashMapOf<CharSequence, CharSequence>().run {
		put("userId", userId)
		put("bookId", bookId)
		NetBuild.response(this@EBookDetail::dataSuccess,
				ToastUtil::showToast, 198,
				ResponseBean.EBookContentBody::class.java, this)
	}
	
	override fun initWidget() {
		_recycler.layoutManager = LinearLayoutManager(this,
				LinearLayoutManager.VERTICAL, false)
		_recycler.setHasFixedSize(true)
		_recycler.adapter = AdapterK(this::itemBind,
				arrayOf(R.layout.item_ebook_list), list)
				.also { mAdapter = it }
		
		if (detailContent == null)
			detailContent = EBookDetailContent()
		supportFragmentManager.beginTransaction().apply {
			if (detailContent!!.isAdded)
				show(detailContent)
			else
				add(R.id._content, detailContent)
		}.commit()
	}
	
	private fun dataSuccess(body: ResponseBean.EBookContentBody) {
		list.clear()
		list.addAll(body.typeList)
		detailContent?.setContent(list, 1)
		body.typeList[0].isLoaded = true
		mAdapter.notifyDataSetChanged()
	}
	
	private fun itemBind(holder: Adapter.Holder
	                     , bean: MutableList<ResponseBean.Type>, position: Int) {
		val title = holder.find<TextView>(R.id.item_title)
		detailContent?.run {
			title.text = bean[position].typeName
			title.setOnClickListener {
				addContent(position)
				scrollToIndex(position)
			}
		}
	}
}