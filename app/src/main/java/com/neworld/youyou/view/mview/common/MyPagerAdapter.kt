package com.neworld.youyou.view.mview.common

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import com.neworld.youyou.add.base.Fragment

/**
 * @author by hhhh on 2018/4/18.
 */
class MyPagerAdapter
(array: Array<String>, fm: FragmentManager?,
 private val getFragment: () -> Fragment) : FragmentPagerAdapter(fm) {

    private var titles: Array<String> = array

    override fun getItem(position: Int): Fragment {
        val content = getFragment.invoke()
        val bundle = Bundle()
        bundle.putString("type", position.toString())
        content.arguments = bundle
        return content
    }

    override fun getCount() = titles.size

    override fun getPageTitle(position: Int) = titles[position]

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun setArray(array: Array<String>) {
        titles = array
    }
}