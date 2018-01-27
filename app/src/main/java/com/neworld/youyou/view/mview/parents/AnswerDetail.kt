package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.nine.CircleImageView
import kotlinx.android.synthetic.main.fragment_answers_detail.*
import kotlin.properties.Delegates

/**
 * @author by user on 2018/1/22.
 */
class AnswerDetail : Fragment() {

    //property
    private var mAdapter by notNullSingleValue<AdapterK<ResponseBean.AnswersDetailList>>()

    //View
    private var mRecycle by notNullSingleValue<RecyclerView>()
    private var mWeb by notNullSingleValue<WebView>()
    private var mComment by notNullSingleValue<EditText>()
    private var mSwipe by notNullSingleValue<SwipeRefreshLayout>()
    private var mPreview by notNullSingleValue<LinearLayout>()
    private var mReview by notNullSingleValue<ImageView>()
    private var mLike by notNullSingleValue<ImageView>()
    private var mNext by notNullSingleValue<ImageView>()
    private var mPublish by notNullSingleValue<Button>()
    private var mCommentCount by notNullSingleValue<TextView>()
    private var mPraiseCount by notNullSingleValue<TextView>()

    //fields
    private val userId by preference("userId", "")

    // by observer
    private var isShowSoftInput by Delegates.observable(false) {
        _, _, new ->
        mComment.post {
            if (new) {
                mComment.run {
                    isFocusable = true
                    isFocusableInTouchMode = true
                    visibility = View.VISIBLE
                    mPublish.visibility = View.VISIBLE
                }
                mPreview.visibility = View.GONE
            } else {
                mComment.run {
                    /*isFocusable = false
                    isFocusableInTouchMode = false*/
                    visibility = View.GONE
                    mPublish.visibility = View.GONE
                }
                mPreview.visibility = View.VISIBLE
            }
            mComment.setSoftInput(new)
        }
    }

    override fun getContentLayoutId() = R.layout.fragment_answers_detail

    @SuppressLint("SetJavaScriptEnabled")
    override fun initWidget(root: View) {
        // 评论EditText
        mComment = root.findViewById<EditText>(R.id._comment).apply {
            setOnTouchListener { v, _ ->
                if (!v.isFocusableInTouchMode) {
                    isShowSoftInput = true
                }
                false
            }
        }

        // SwipeRefreshLayout
        mSwipe = root.findViewById<SwipeRefreshLayout>(R.id._swipe).apply {
            setOnRefreshListener {
                initData()
            }
        }

        // RecyclerView
        mRecycle = root.findViewById<RecyclerView>(R.id._recycle).apply {
            layoutManager = LinearLayoutManager(context, 1, false)
            addItemDecoration(DividerItemDecoration(context, 1))
            adapter = AdapterK(this@AnswerDetail::itemBind,
                    R.layout.item_answers_detail, arrayListOf()).also { mAdapter = it }
            setOnTouchListener { _, _ ->
                if (mComment.isFocusableInTouchMode) {
                    isShowSoftInput = false
                }
                false
            }
        }

        // Bottom
        mPreview = root.findViewById<LinearLayout>(R.id._bottom_preview).apply {
            mReview = findViewById(R.id._review)
            mLike = findViewById(R.id._like)
            mNext = findViewById(R.id._next_comment)
            findViewById<Button>(R.id._ac_reply).setOnClickListener {
                isShowSoftInput = true
            }
        }

        mPublish = root.findViewById<Button>(R.id._publish).apply {
            setOnClickListener {
                if (_comment.text.isEmpty()) {
                    showToast("请输入内容")
                    return@setOnClickListener
                }
                showToast("reply pressed")
            }
        }

        mWeb = layoutInflater.inflate(R.layout.head_answers_detail, mRecycle, false)
                .also { mAdapter.headView = it }
                .run {
                    mCommentCount = findViewById(R.id.head_comment_count)
                    mPraiseCount = findViewById(R.id.head_praise_count)
                    findViewById<WebView>(R.id.head_web)
                }.apply(this@AnswerDetail::configWeb)

        layoutInflater.inflate(R.layout.foot_answers_detail, mRecycle, false)
    }

    override fun initData() {
        if (!mSwipe.isRefreshing) mSwipe.isRefreshing = true
        val commentId = arguments.getInt("cId", 0)
        logE("commentId = $commentId")
        val url = "http://192.168.1.123:8080/neworld/android/201?userId=$userId&commentId=$commentId"
        mWeb.loadUrl(url)

        val map = hashMapOf<CharSequence, CharSequence>()
        map["userId"] = userId
        map["commentId"] = commentId.toString()
        map["createDate"] = ""

        response(this@AnswerDetail::onResponse, 202, map)
    }

    @SuppressLint("SetTextI18n")
    private fun onResponse(t: ResponseBean.AnswersDetailBody) {
        if (t.status == 1) {
            showToast("{数据错误, 请到用户反馈处反馈此问题: 错误代码[AD.KT]}")
            return
        }
        mAdapter.addDataAndClear(t.menuList)
        mAdapter.notifyDataSetChanged()

        mCommentCount.text = "评论 66"
        mPraiseCount.text = "304 赞"
    }

    private fun itemBind(holder: Adapter.Holder,
                         mutableList: MutableList<ResponseBean.AnswersDetailList>, position: Int) {
        val icon = holder.find<CircleImageView>(R.id.item_icon)
        val name = holder.find<TextView>(R.id.item_name)
        val content = holder.find<TextView>(R.id.item_content)
        val praise = holder.find<CheckBox>(R.id.item_praise)
        val date = holder.find<TextView>(R.id.item_date)
        val reply = holder.find<TextView>(R.id.item_reply)

        val data = mutableList[position]

        name.text = data.from_nickName
        content.text = data.content
        praise.text = data.commentLike.toString()
        praise.isChecked = data.likeCommentStatus == 0

        date.text = "刚刚"
        reply.text = "回复"

        val options = RequestOptions()
                .placeholder(R.drawable.deftimg)
                .error(R.drawable.deftimg)

        Glide.with(icon).load(data.faceImg).apply(options).into(icon)

        reply.setOnClickListener {
            showToast("回复 pressed")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configWeb(it: WebView) = with(it) {
        settings.javaScriptEnabled = true
//            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS

        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress > 75) {
                    if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
                }
            }
        }
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                logE("url = $url")
                url?.let {
                    if (" http://106.14.251.200/neworld/user/143" in it) {
                        startActivity(Intent(context, Answers::class.java)) // TODO : Put Bundle
                    }
                }
                view?.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                showToast("error : $errorCode")
            }
        }
    }

    private fun EditText.setSoftInput(show: Boolean) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) {
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        } else {
            imm.hideSoftInputFromWindow(this.windowToken, 0)
        }
    }
}