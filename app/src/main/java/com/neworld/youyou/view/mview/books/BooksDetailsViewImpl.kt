package com.neworld.youyou.view.mview.books

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.presenter.books.DetailImpl
import com.neworld.youyou.utils.LogUtils
import com.neworld.youyou.utils.SpUtil
import com.neworld.youyou.utils.ToastUtil
import com.neworld.youyou.utils.notNullSingleValue
import com.neworld.youyou.view.mview.common.LinearImage
import kotlinx.android.synthetic.main.activity_books_detail.*

/**
 * @author by user on 2017/11/23.
 */
class BooksDetailsViewImpl : Activity(), BooksDetailsView<ResponseBean.BooksDetailBean> {

    private val width by lazy {
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        point.x
    }
    private val padding by lazy {
        praise.measure(0, 0)
        val pw = praise.measuredWidth * 2
        (width / 2 - pw) / 3
    }
    private val pd by lazy {
        val dp15 = resources.getDimensionPixelSize(R.dimen.dp_15) * 2
        tv_1.measure(0, 0)
        (width - tv_1.measuredWidth * 4 - dp15) / 3
    }

    private val contentText by lazy {
        val textView = TextView(this@BooksDetailsViewImpl)
        textView.textSize = 14F
        val dp15 = resources.getDimensionPixelSize(R.dimen.dp_15)
        textView.setPadding(dp15, 0, dp15, 0)
        textView
    }

    private val userId by lazy { SpUtil.getString(baseContext, "userId") }
    private val map by lazy {
        hashMapOf<CharSequence, CharSequence>(Pair("userId", userId), Pair("taskId", bkId.toString())
                , Pair("type", "4"))
    }

    private val loadingPage by lazy {
        val inflate = LayoutInflater.from(baseContext).inflate(R.layout.loading_page, group, false)
        group.addView(inflate)
        inflate.visibility = View.GONE
        inflate
    }

    private val loadingDrawable by lazy {
        val decode = BitmapFactory.decodeResource(resources, R.drawable.book_place_bg)
        BitmapDrawable(resources, decode)
    }

    private var bkId: Int by notNullSingleValue()

    private var presenter: DetailImpl<ResponseBean.BooksDetailBean>? = null

    private var images: ArrayList<ImageView> = arrayListOf()
    private var mAdapter: PageAdapter? = null

    override fun getContentLayoutId(): Int = R.layout.activity_books_detail

    override fun initArgs(bundle: Bundle?): Boolean {
        // 白底黑字状态栏 . api大于23 (Android6.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)
        }
        presenter = DetailImpl(this@BooksDetailsViewImpl)

        return super.initArgs(bundle)
    }

    override fun initWidget() {
        praise.setPadding(padding, 0, padding / 2, 0)
        stars.setPadding(padding / 2, 0, padding, 0)
        tv_2.setPadding(pd, 0, 0, 0)
        tv_3.setPadding(pd, 0, 0, 0)
        shop.layoutParams.width = width / 2

        val sps = SpannableString(price.text.toString())
        sps.setSpan(AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.sp24)),
                2, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        price.text = sps
        const_price.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG

        close.setOnClickListener { finish() }

        praise.setOnClickListener {
            val toInt = praise.text.toString().toInt()
            praise.text = setText(praise.isChecked, toInt)
        }
        stars.setOnClickListener {
            val toInt = stars.text.toString().toInt()
            stars.text = setText(stars.isChecked, toInt)
        }

        shop.setOnClickListener {
            val name = intent.getStringExtra("name")
            val iconImg = intent.getStringExtra("iconImg")
            val intent = Intent(this@BooksDetailsViewImpl, BooksShopPay::class.java)
            intent.putExtra("price", price.text.substring(1, price.text.length))
            intent.putExtra("iconImg", iconImg)
            intent.putExtra("name", name)
            intent.putExtra("bookId", bkId)
            startActivity(intent)
        }

        mAdapter = PageAdapter(images)
        viewpager.adapter = mAdapter
    }

    override fun initData() {
        val bookId = intent.getStringExtra("bookId")
        val map = HashMap<CharSequence, CharSequence>()
        map.put("bookId", bookId)
        map.put("userId", userId)
        presenter?.down(map, 179, ResponseBean.BooksDetailBean::class.java)
    }

    private fun setText(isChecked: Boolean, i: Int): String =
            if (isChecked) "${i + 1}"
            else "${i - 1}"

    override fun showDialog() {
        loadingPage.visibility = View.VISIBLE
    }

    override fun closeDialog() {
        loadingPage.visibility = View.GONE
    }

    override fun setData(t: ResponseBean.BooksDetailBean) {
        val menuList = t.menuList
        val list = menuList.contentImg.split("\\|".toRegex())
                .flatMap {
                    val imageView = ImageView(this)
                    Glide.with(imageView).load(it).into(imageView).onLoadStarted(loadingDrawable)
                    listOf(imageView)
                }
        images.addAll(list)

        val end = menuList.price.toString().split("\\.".toRegex()).toTypedArray()[0].length + 2
        val sps = SpannableString("¥ ${menuList.price}")
        sps.setSpan(AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.sp24)),
                2, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val ep: CharSequence = "快递: ${menuList.expressFee}"
        val cps: CharSequence = "¥ ${menuList.constPrice}"
        val sl: CharSequence = "销售${menuList.count}笔"

        name.text = menuList.bookName                   // 书名
        price.text = sps                                // 售价
        const_price.text = cps                          // 原价
        ems.text = ep                                   // 邮费
        sale.text = sl                                  // 月销
        author_text.text = menuList.author              // 作者
        press_text.text = menuList.publishDate          // 地址

        praise.text = menuList.likeSum.toString()
        stars.text = menuList.collectSum.toString()

        praise.isChecked = t.likeStatus == 0
        stars.isChecked = t.collectStatus == 0

        content.removeAllViews()
        if (menuList.suggest.isNotEmpty()) {
            contentText.text = menuList.suggest
            content.addView(contentText)
        } else {
            if (menuList.introduceImg != null) {
                val images = LinearImage(this)
                content.addView(images)

                menuList.introduceImg
                        .split("\\|".toRegex())
                        .forEach {
                            val imageView = ImageView(this)
                            imageView.scaleType = ImageView.ScaleType.MATRIX
                            Glide.with(baseContext).load(it).into(imageView)
                            images.addView(imageView)
                        }
            }
        }

        bkId = menuList.id
    }

    override fun showError(str: String) {
        ToastUtil.showToast(str)
//        showSnackbar(group, str, 2000)
    }

    override fun notifyData() {
        mAdapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        upload()
        presenter?.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        upload()
        super.onSaveInstanceState(outState)
    }

    private fun upload() {
        presenter?.uploadState(praise.isChecked, stars.isChecked, map, 159)
    }

    private class PageAdapter(array: List<ImageView>) : PagerAdapter() {

        private var images = array

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            container?.removeView(images[position])
        }

        override fun isViewFromObject(view: View?, `object`: Any?): Boolean = view == `object`

        override fun getCount(): Int = images.size

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            container?.addView(images[position])
            return images[position]
        }
    }
}