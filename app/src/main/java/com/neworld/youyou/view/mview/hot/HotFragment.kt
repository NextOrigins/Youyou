package com.neworld.youyou.view.mview.hot

import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.TextView
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.logE
import com.neworld.youyou.utils.response
import com.neworld.youyou.utils.showToast
import com.neworld.youyou.view.mview.common.MyPagerAdapter

/**
 * @author by hhhh on 2018/4/12.
 */
class HotFragment : Fragment() {

    // view
    private lateinit var mTab: TabLayout
    private lateinit var mPager: ViewPager
    private lateinit var mAdapter: MyPagerAdapter
    private lateinit var mErrorPage: TextView

    override fun getContentLayoutId() = R.layout.fragment_hot_parent

    override fun initWidget(root: View) {
        mTab = root.findViewById(R.id._tab)
        mPager = root.findViewById(R.id._pager)

        mTab.setupWithViewPager(mPager)
        mPager.adapter = MyPagerAdapter(arrayOf(),
                childFragmentManager, ::HotTextFragment).also { mAdapter = it }

        mErrorPage = root.findViewById(R.id._error)
        mErrorPage.setOnClickListener {
            showToast("please wait....")
            initData()
            mErrorPage.visibility = View.GONE
        }
    }

    override fun initData() = response(::onResponse, "132", "", ::onFailed)

    private fun onResponse(body: ResponseBean.HotTitleBody) {
        logE("menuList size = ${body.menuList.size}")
        val temp = body.menuList
                .sortedBy { it.id }
                .flatMap { arrayListOf(it.typeName) }

        mAdapter.setArray(temp.toTypedArray())
        mAdapter.notifyDataSetChanged()
    }

    private fun onFailed(e: String) {
        logE(e)
        showToast("网络请求失败了，请检查网络~")
        mErrorPage.visibility = View.VISIBLE
    }
}
