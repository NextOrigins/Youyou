package com.neworld.youyou.view.mview.common

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.SharedElementCallback
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.utils.MyEventBus
import com.neworld.youyou.utils.showToast
import kotlinx.android.synthetic.main.activity_img_viewer.*

/**
 * @author by hhhh on 2018/4/29.
 */
class ImgViewer : Activity() {

    // fields
    private lateinit var url: String
    private var mPosition = 0
    private val dp5 by lazy { resources.getDimensionPixelOffset(R.dimen.dp5) }
    private var enterPosition = 0

    // controller
    private lateinit var mAdapter: Adapter

    companion object {
        const val EXTRA_URL = "extra_url"
        const val EXTRA_POS = "extra_pos"
        const val EVENT_IMG = 51
        fun launch(activity: AppCompatActivity, srcView: View, url: String, position: Int) {
            val intent = Intent(activity, ImgViewer::class.java)
            intent.putExtra(EXTRA_URL, url)
            intent.putExtra(EXTRA_POS, position)
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, srcView, activity.getString(R.string.transitionName_pic))
            ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle())
        }
    }

    override fun getContentLayoutId() = R.layout.activity_img_viewer

    override fun initWindows() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.primaryDarkGray)
        }
    }

    override fun initArgs(bundle: Bundle?): Boolean {
        url = bundle?.getString(EXTRA_URL) ?: ""
        mPosition = bundle?.getInt(EXTRA_POS) ?: 0
        enterPosition = mPosition

        if (url.isEmpty()) showToast(this, "出现未知错误")
        return url.isNotEmpty()
    }

    override fun initWidget() {
        val arrays = url.split('|').flatMap {
            val img = ImageView(this@ImgViewer)
            img.scaleType = ImageView.ScaleType.FIT_CENTER
            img.setOnClickListener { onBackPressed() }
            Glide.with(img).load(it).into(img)
            listOf(img)
        }.toTypedArray()

        val p = _pager
        p.adapter = Adapter(arrays).also { mAdapter = it }
        p.currentItem = mPosition
        setChanged()

        for (i in 0 until arrays.size) {
            val v = View(this)
            val lp = LinearLayout.LayoutParams(dp5, dp5)
            lp.setMargins(0, 0, dp5, 0)
            v.layoutParams = lp
            v.setBackgroundResource(R.drawable.indicator)

            _indicator.addView(v)
        }
        _indicator.getChildAt(mPosition).setBackgroundResource(R.drawable.indicator_p)

        setEnterSharedElementCallback(object: SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                if (enterPosition != mPosition) {
                    names?.clear()
                    sharedElements?.clear()
                    val name = getString(R.string.transitionName_pic)
                    names?.add(name)
                    sharedElements?.put(name, arrays[mPosition])
                }
            }
        })
    }

    private class Adapter(val views: Array<ImageView>) : PagerAdapter() {

        override fun getCount() = views.size

        override fun isViewFromObject(view: View, `object`: Any) = view == `object`

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) =
                container.removeView(`object` as View)

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val child = views[position]
            container.addView(child)
            return child
        }
    }

    override fun onBackPressed() {
        MyEventBus.INSTANCE.postEvent(EVENT_IMG, if (mPosition < 3) mPosition else 2)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        } else {
            finish()
        }
    }

    private fun setChanged() {
        _pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                _indicator.getChildAt(mPosition).setBackgroundResource(R.drawable.indicator)
                _indicator.getChildAt(position).setBackgroundResource(R.drawable.indicator_p)
                mPosition = position
            }
        })
    }
}