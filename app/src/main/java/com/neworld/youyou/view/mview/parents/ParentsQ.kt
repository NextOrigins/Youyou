package com.neworld.youyou.view.mview.parents

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.utils.notNullSingleValue

/**
 * @author by user on 2018/1/2.
 */
class ParentsQ : Fragment() {
	
	private var mRecycle by notNullSingleValue<RecyclerView>()
	
	override fun getContentLayoutId() = R.layout.fragment_parents_q
	
	override fun initWidget(root: View) {
		root.findViewById<RecyclerView>(R.id._recycler).also {
			it.layoutManager = LinearLayoutManager(context,
					LinearLayoutManager.VERTICAL, false)
			it.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
		}.let { mRecycle = it }
	}
}