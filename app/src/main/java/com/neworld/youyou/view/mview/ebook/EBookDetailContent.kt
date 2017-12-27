package com.neworld.youyou.view.mview.ebook

import android.support.v4.view.ViewPager
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.notNullSingleValue

/**
 * @author by user on 2017/12/22.
 */
class EBookDetailContent : Fragment() {
	
	private val list = arrayListOf<View>()
	private val parentData = arrayListOf<ResponseBean.Type>()
	private var mViewPager by notNullSingleValue<ViewPager>()
	private var mAdapter by notNullSingleValue<MyAdapter<View>>()
	private var indexArray: SparseIntArray by notNullSingleValue()
	
	private var scrolled = 0
	
	private var start = 0
	private var end = 0
	
	private val l = object : ViewPager.OnPageChangeListener {
		override fun onPageScrollStateChanged(state: Int) {
			if (state == 0) {
				val index = indexArray.get(scrolled)
				addContent(index)
			}
		}
		
		override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
		}
		
		override fun onPageSelected(position: Int) {
			scrolled = position
		}
	}
	
	override fun getContentLayoutId() =
			R.layout.fragment_ebook_content
	
	override fun initWidget(root: View) {
		mViewPager = root.findViewById(R.id._pager)
		mViewPager.adapter = MyAdapter(list).also { mAdapter = it }
		mViewPager.addOnPageChangeListener(l)
	}
	
	fun setContent(data: MutableList<ResponseBean.Type>, position: Int) {
		data.flatMap { parentData.add(it); it.typeImg.split("\\|".toRegex()) }
				.forEach { url ->
					LayoutInflater.from(context)
							.inflate(R.layout.item_ebook_detail, mRoot as ViewGroup, false)
							.let { it.tag = url; list.add(it) }
				}
		
		SparseIntArray().apply {
			var i = 0
			data.let {
				it.forEachIndexed { index, type ->
					type.typeImg.split("\\|".toRegex()).forEach {
						put(i++, index)
					}
				}
			}
		}.let { indexArray = it }
		
		parentData.take(position).fold(0) { total, next ->
			total + next.typeImg.split("\\|".toRegex()).size
		}.let {
			val type = parentData[position]
			start = it
			end = start + type.typeImg.split("\\|".toRegex()).size - 1
			(start..end).forEach {
				val view = list[it]
				val icon = view.findViewById<ImageView>(R.id._icon)
				view.findViewById<TextView>(R.id._title).text = type.typeName
				val url = view.tag as String
				Glide.with(icon).load(url).into(icon)
			}.let { mAdapter.notifyDataSetChanged() }
			mViewPager.setCurrentItem(it, false)
		}
		
		when (position) {
			0 -> addContent(1)
			parentData.size - 1 -> addContent(parentData.size - 2)
			else -> {
				addContent(position - 1)
				addContent(position + 1)
			}
		}
	}
	
	fun scrollToIndex(position: Int) = with(parentData.take(position)) {
		fold(0) { total, next ->
			total + next.typeImg.split("\\|".toRegex()).size
		}.let { mViewPager.currentItem = it }
	}
	
	fun addContent(position: Int) {
		if (position >= parentData.size || position < 0)
			return
		
		val p0 = parentData[position]
		
		if (!p0.isLoaded) {
			var index = 0
			parentData.take(position).fold(0) { total, next ->
				total + next.typeImg.split("\\|".toRegex()).size
			}.let { index = it }
			
			if (index < start)
				setChapter(index, start).let { start = index }
			
			if (index >= end) {
				index += parentData[position].typeImg.split("\\|".toRegex()).size
				setChapter(end, index).let { end = index }
			}
			
			p0.isLoaded = true
			
			mAdapter.notifyDataSetChanged()
		}
	}
	
	private fun setChapter(p0: Int, p1: Int) = (p0 until p1).forEach {
		val view = list[it]
		val type = parentData[indexArray[it]]
		val url = view.tag as String
		val image = view.findViewById<ImageView>(R.id._icon)
		val chapter = view.findViewById<TextView>(R.id._title)
		Glide.with(image).load(url).into(image)
		chapter.text = type.typeName
	}
	
	private class MyAdapter<out T : View>(val list: List<T>) : android.support.v4.view.PagerAdapter() {
		
		override fun instantiateItem(container: ViewGroup, position: Int)
				= list[position].also {
			if (it.parent != null) {
				(it.parent as ViewGroup).removeView(it)
			}
			container.addView(it)
		}
		
		override fun destroyItem(container: ViewGroup, position: Int, `object`: Any?)
				= with(container) { if (position < count) removeView(list[position]) }
		
		override fun isViewFromObject(view: View?, `object`: Any?) =
				view == `object`
		
		override fun getCount() = list.size
		
		override fun getItemPosition(`object`: Any?) = POSITION_NONE
	}
}