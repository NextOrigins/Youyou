package com.neworld.youyou.view.mview.ebook

import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.utils.ToastUtil
import com.neworld.youyou.utils.notNullSingleValue

/**
 * @author by user on 2017/12/22.
 */
class EBookDetailContent : Fragment() {
	
	private var mViewPager by notNullSingleValue<ViewPager>()
	private var mAdapter by notNullSingleValue<MyAdapter<ImageView>>()
	private val list = arrayListOf<ImageView>()
	
	override fun getContentLayoutId() =
			R.layout.fragment_ebook_content
	
	override fun initWidget(root: View) {
		mViewPager = root.findViewById(R.id._pager)
		mViewPager.adapter = MyAdapter(list).also { mAdapter = it }
	}
	
	fun setList(str: String) {
		if (list.isNotEmpty())
			list.clear()
		str.split("\\|".toRegex()).forEach { img ->
			ImageView(context).let {
				Glide.with(it).load(img).into(it)
				list.add(it)
				it.setOnClickListener {
					ToastUtil.showToast("hhhhh")
				}
			}
		}
		mAdapter.notifyDataSetChanged()
	}
	
	private class MyAdapter<out T : View>(val list: List<T>) : android.support.v4.view.PagerAdapter() {
		
		override fun instantiateItem(container: ViewGroup, position: Int)
				= list[position].also { container.addView(it) }
		
		override fun destroyItem(container: ViewGroup, position: Int, `object`: Any?)
				= with(container) { removeView(list[position]) }
		
		override fun isViewFromObject(view: View?, `object`: Any?) =
				view == `object`
		
		override fun getCount() = list.size
	}
}