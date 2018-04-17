package com.neworld.youyou.view.mview.hot

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.response

/**
 * @author by hhhh on 2018/4/12.
 */
class HotFragment : Fragment() {

    // view
    private lateinit var mTab: TabLayout
    private lateinit var mPager: ViewPager

    override fun getContentLayoutId() = R.layout.fragment_hot_parent

    override fun initWidget(root: View) {
        mTab = root.findViewById(R.id._tab)
        mPager = root.findViewById(R.id._pager)

        mTab.setupWithViewPager(mPager)
    }

    override fun initData() {
        response(::onResponse, "132", "")
    }

    private fun onResponse(body: ResponseBean.HotTitleBody) {
        val temp = body.menuList
                .sortedBy { it.id }
                .flatMap { arrayListOf(it.typeName) }

        mPager.adapter = PagerAdapter(temp.toTypedArray(), fragmentManager)
    }

    private class PagerAdapter
    (private val titles: Array<String>, fm: FragmentManager?) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val content = HotTextFragment()
            val bundle = Bundle()
            bundle.putString("type", position.toString())
            content.arguments = bundle
            return content
        } // TODO : 写一个复用fragment

        override fun getCount() = titles.size

        override fun getPageTitle(position: Int) = titles[position]
    }
}
