package com.neworld.youyou.view.mview.parents

import android.support.v7.widget.RecyclerView
import android.view.View
import android.webkit.WebView
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.utils.notNullSingleValue

/**
 * @author by user on 2018/1/22.
 */
class AnswerDetail : Fragment() {

	private var recycle by notNullSingleValue<RecyclerView>()
	private var web by notNullSingleValue<WebView>()
	
	override fun getContentLayoutId()
		= R.layout.activity_answers_detail

	override fun initWidget(root: View) {
		recycle = root.findViewById<RecyclerView>(R.id._recycle).apply {
			// TODO : waiting for service
		}

		web = root.findViewById<WebView>(R.id._web).apply {
			// TODO : waiting for url
		}

		// item id : R.layout.item_answers_detail.xml
	}
}