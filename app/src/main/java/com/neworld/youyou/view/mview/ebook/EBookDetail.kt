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
				R.layout.item_ebook_list, list)
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
//		if (body == null)
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
//				index = position
//				val mid = bean[position]
//				LogUtils.E("index = $index, count = ${getCount()}")
//				if (mid.isLoaded) {
//					val i = list.take(position)
//							.dropLastWhile { !it.isLoaded }
//							.fold(0) { total, next ->
//								total + next.typeImg.split("\\|".toRegex()).size
//							}
//					scrollToIndex(i)
//				} else {
//					/*
//					 TODO : 没加载过
//					 如果刚进来加载的不是第一页, 比如有 0 1 2 3 4 , 进来加载的3. 那么如果点了0
//					 那么3是已经加载过的 但是中间空了1和2 ,
//					 */
//				}
//				if (position in 1 until bean.size - 1) {
//					val top = bean[position - 1]
//					val end = bean[position + 1]
//					LogUtils.E("1 if : top load ? ${top.isLoaded}, end load ? ${end.isLoaded}")
//					if (!top.isLoaded) {
//						addContent(top.typeImg, EBookDetailContent.Type.START, top.typeName)
//						top.isLoaded = true
//					}
//					if (!end.isLoaded) {
//						addContent(end.typeImg, EBookDetailContent.Type.END, end.typeName)
//						end.isLoaded = true
//					}
//				} else if (position == bean.size - 1 && bean.size - 2 != -1) {
//					val top = bean[position - 1]
//					LogUtils.E("2 if : top load ? ${top.isLoaded}")
//					if (!top.isLoaded) {
//						addContent(top.typeImg, EBookDetailContent.Type.START, top.typeName)
//						top.isLoaded = true
//					}
//				} else if (position == 0 && position + 1 != bean.size) {
//					val end = bean[position + 1]
//					LogUtils.E("3 if : end load ? ${end.isLoaded}")
//					if (!end.isLoaded) {
//						addContent(end.typeImg, EBookDetailContent.Type.END, end.typeName)
//						end.isLoaded = true
//					}
//				}
			}
		}
	}
}