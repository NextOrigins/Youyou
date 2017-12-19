package com.neworld.youyou.view.mview.ebook

import android.support.v7.widget.GridLayoutManager
import com.neworld.youyou.R
import com.neworld.youyou.add.SpacesItemDecoration
import com.neworld.youyou.add.base.Activity
import kotlinx.android.synthetic.main.activity_ebook.*

/**
 * @author by user on 2017/12/18.
 */
class EBooks : Activity() {


    override fun getContentLayoutId() = R.layout.activity_ebook

    override fun initWidget() {
        _recycler.layoutManager = GridLayoutManager(this,
                3, GridLayoutManager.VERTICAL, false)
        _recycler.addItemDecoration(SpacesItemDecoration(3,
                40F, false))
    }
}