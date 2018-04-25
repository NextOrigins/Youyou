package com.neworld.youyou.view.mview.parents

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.TextView
import com.neworld.youyou.R
import com.neworld.youyou.activity.LoginActivity
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.mview.common.MyPagerAdapter
import org.jetbrains.anko.doAsync

/**
 * @author by hhhh on 2018/4/18.
 */
class Topics : Fragment() {

    companion object {
        var OPEN_CACHE = true
        const val CACHE_KEY = "cacheJson"
        const val TO_LOGIN = 666
        const val CACHE_LENGTH = 777
        const val CLEAR_CACHE = 888
    }

    // view
    private lateinit var mTab: TabLayout
    private lateinit var mPager: ViewPager
    private lateinit var mAdapter: MyPagerAdapter
    private lateinit var mErrorPage: TextView

    // property
    private lateinit var typeArray: Array<String>
    private lateinit var obtain: MyEventBus.Configuration
    private var userId by preference("userId", "")
    private var mPostIndex = 0

    override fun getContentLayoutId() = R.layout.fragment_questions

    override fun initArgs(bundle: Bundle?) {
        obtain = MyEventBus.INSTANCE.obtain(TO_LOGIN, {
            doAsync {
                val response = NetBuild.getResponse("{\"userId\":\"$userId\"}", 152)
                        ?: return@doAsync
                if ("0" in response) {
                    userId = ""
                    closeCache()
                    uiThread {
                        startActivity(Intent(context, LoginActivity::class.java)
                                .putExtra("login2", true))
                    }
                }
            }
            null
        })
        obtain.addBus(CACHE_LENGTH, {
            val edit = getPefStorage()
            typeArray.fold("") { total, next ->
                val key = "$CACHE_KEY$next"
                total + edit.getString(key, "")
            }
        })
        obtain.addBus(CLEAR_CACHE, { if (closeCache()) "1" else "0" })
        registerStation(obtain)
        super.initArgs(bundle)
    }

    override fun initWidget(root: View) {
        mTab = root.findViewById(R.id._tab)
        mPager = root.findViewById(R.id._pager)

        mTab.setupWithViewPager(mPager)
        mPager.adapter = MyPagerAdapter(arrayOf(), childFragmentManager,
                ::Questions).also { mAdapter = it }

        // TODO : add page changed listener .
        mPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mPostIndex = position
            }
        })

        mErrorPage = root.findViewById(R.id._error)

        mErrorPage.setOnClickListener {
            showToast("please wait....")
            initData()
            mErrorPage.visibility = View.GONE
        }
    }

    override fun initData() {
        response(::onResponse, "214", "", ::onFailed)
    }

    private fun onResponse(body: ResponseBean.QTitleBody) {
        val temp = body.menuList
                .sortedBy { it.id }
                .flatMap { arrayListOf(it.typeName) }.toTypedArray()

        typeArray = ((0 until temp.size).map { it.toString() }.toTypedArray())

        mAdapter.setArray(temp)
        mAdapter.notifyDataSetChanged()
    }

    // 清除缓存
    private fun closeCache(): Boolean {
        val edit = getPefStorage().edit()
        typeArray.forEach {
            val key = "$CACHE_KEY$it"
            edit.putString(key, "")
        }
        OPEN_CACHE = false
        return edit.commit()
    }

    private fun onFailed(e: String) {
        logE(e)
        showToast("网络请求失败了，请检查网络~")
        mErrorPage.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        unregisterStation()
        super.onDestroy()
    }

    fun refresh() {
        MyEventBus.INSTANCE.postEvent(mPostIndex, "")
    }
}