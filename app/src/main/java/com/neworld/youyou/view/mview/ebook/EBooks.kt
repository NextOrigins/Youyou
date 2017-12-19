package com.neworld.youyou.view.mview.ebook

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.utils.notNullSingleValue

/**
 * @author by user on 2017/12/18.
 */
class EBooks : Fragment() {

    private var recycler: RecyclerView by notNullSingleValue()

    override fun getContentLayoutId() = R.layout.activity_ebook

    override fun initWidget(root: View) {
        recycler = root.findViewById(R.id._recycler)
        val textView = root.findViewById<TextView>(R.id._text)
        recycler.visibility = View.GONE
        val i = 0x1F602
    }
}