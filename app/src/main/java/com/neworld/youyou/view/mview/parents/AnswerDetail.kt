package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
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
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.properties.Delegates

/**
 * @author by user on 2018/1/22.
 */
class AnswerDetail : Fragment() {

    //property
    private var mAdapter by notNullSingleValue<AdapterK<ResponseBean.AnswersDetailList>>()
    private val point by lazy {
        val point = Point()
        activity.windowManager.defaultDisplay.getSize(point)
        point
    }
    private val praises by lazy {
        val map = hashMapOf<CharSequence, CharSequence>()
        map["userId"] = userId
        map["type"] = "5"
        map
    }
    private var fromUserId = ""

    //View
    private var mRecycle by notNullSingleValue<RecyclerView>()
    private var mWeb by notNullSingleValue<WebView>()
    private var mComment by notNullSingleValue<EditText>()
    private var mSwipe by notNullSingleValue<SwipeRefreshLayout>()
    private var mPreview by notNullSingleValue<LinearLayout>()
    private var mPublish by notNullSingleValue<Button>()
    private var mCommentCount by notNullSingleValue<TextView>()
    private var mPraiseCount by notNullSingleValue<TextView>()
    private var mLike by notNullSingleValue<CheckBox>()

    //fields
    private val userId by preference("userId", "")
    private val commentId by lazy { arguments.getInt("cId").toString() }
    private var itsChecked: Boolean? = null

    // by observer
    private var isShowSoftInput by Delegates.observable(false) { _, old, new ->
        if (old == new) return@observable
        mComment.setSoftInput(new)
        if (new) {
            mPublish.visibility = View.VISIBLE
            mPreview.visibility = View.INVISIBLE
        } else {
            mPublish.visibility = View.INVISIBLE
            mPreview.visibility = View.VISIBLE
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
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    isShowSoftInput = false
                }
                false
            }
        }

        // Bottom
        mPreview = root.findViewById<LinearLayout>(R.id._bottom_preview).apply {
            val mReview = findViewById<ImageView>(R.id._review).apply {
                setOnClickListener { mRecycle.toPosition(1) }
            }
            mLike = findViewById<CheckBox>(R.id._like).apply {
                setOnClickListener {
                    praises["commentId"] = commentId
                    praises["status"] = if (isChecked) "1" else "0"

                    doAsync {
                        val response = NetBuild.getResponse(praises, 193)
                        if ("0" !in response) {
                            uiThread {
                                isChecked = !isChecked
                                showToast("出现未知错误, 请到用户反馈处反馈此问题: 错误代码{AD.KT-2}")
                            }
                        } else {
                            itsChecked = isChecked
                        }
                    }
                }
            }
            val mNext = findViewById<ImageView>(R.id._next_comment).apply {
                setOnClickListener { showToast("next page pressed") }
            }
            findViewById<TextView>(R.id._ac_reply).apply {
                setOnClickListener {
                    isShowSoftInput = true
                    fromUserId = ""
                }
                layoutParams = layoutParams
                        .also { it.width = (point.x - resources.getDimensionPixelOffset(R.dimen.dp30)) / 2 }
            }
            mNext.post {
                val width = (point.x - resources.getDimensionPixelOffset(R.dimen.dp30)) / 2
                val x = width / 3
                mReview.layoutParamsWidth(x)
                mLike.layoutParamsWidth(x)
                mNext.layoutParamsWidth(x)
            }
        }

        mPublish = root.findViewById(R.id._publish)

        mPublish.setOnClickListener {
            if (mComment.text.isEmpty()) {
                showToast("请输入内容")
                return@setOnClickListener
            }

            val map = hashMapOf<CharSequence, CharSequence>()
            map["userId"] = userId
            map["taskId"] = arguments.getString("taskId")
            map["commentId"] = commentId
            map["content"] = mComment.text.toString()
            map["from_userId"] = fromUserId // TODO : 回复

            logE("map = $map")
            doAsync {
                val response = NetBuild.getResponse(map, 206)
                LogUtils.LOG_JSON("response : $response")
                uiThread {
                    if ("0" in response) {
                        mAdapter.notifyDataSetChanged()
                    }
                }
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
        val url = "http://192.168.1.123:8080/neworld/android/201?userId=$userId&commentId=$commentId"
        mWeb.loadUrl(url)

        val checked = if (itsChecked != null) {
            itsChecked!!
        } else {
            arguments.getBoolean("likeStatus", false)
        }
        mLike.post { mLike.isChecked = checked }

        val map = hashMapOf<CharSequence, CharSequence>()
        map["userId"] = userId
        map["commentId"] = commentId
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

        mCommentCount.text = "评论 0" // TODO : 测试
        mPraiseCount.text = "0 赞"
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

        date.text = data.createDate
        reply.text = "回复"

        val options = RequestOptions()
                .placeholder(R.drawable.deftimg)
                .error(R.drawable.deftimg)

        Glide.with(icon).load(data.faceImg).apply(options).into(icon)

        reply.setOnClickListener {
            showToast("回复 pressed")
            fromUserId = data.from_userId.toString()
        }
        praise.setOnClickListener {
            praises["commentId"] = commentId
            praises["status"] = if (praise.isChecked) "1" else "0"

            doAsync {
                val response = NetBuild.getResponse(praises, 193)
                if ("0" in response) {
                    uiThread {
                        praise.text = when {
                            data.likeCommentStatus == 0 && !praise.isChecked -> {
                                data.likeCommentStatus = 1
                                (--data.commentLike).toString()
                            }

                            data.likeCommentStatus == 1 && praise.isChecked -> {
                                data.likeCommentStatus = 0
                                (++data.commentLike).toString()
                            }

                            else -> data.commentLike.toString()
                        }
                    }
                } else {
                    uiThread { praise.isChecked = !praise.isChecked; showToast("网络错误, 请稍后重试") }
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configWeb(it: WebView) = with(it) {
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                isShowSoftInput = false
            }
            false
        }
        settings.javaScriptEnabled = true
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS

        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress > 75) {
                    if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
                }
            }
        }
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    when {
                        "http://106.14.251.200/neworld/user/143" in it -> {
                            startActivity(Intent(context, Answers::class.java).putExtras(arguments))
                        }
                        "http://106.14.251.200/neworld/user/153" in it -> {
                            mRecycle.toPosition(1)
                        }
                    }
                }
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                showToast("error : $errorCode")
            }
        }
    }

    private fun RecyclerView.toPosition(position: Int) {
        if (adapter.itemCount > position) {
            val layoutManager = layoutManager as LinearLayoutManager
            scrollToPosition(position)
            layoutManager.scrollToPositionWithOffset(position, 20)
        }
    }

    private fun EditText.setSoftInput(show: Boolean) {
        visibility = if (show) View.VISIBLE else View.INVISIBLE
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) {
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        } else {
            imm.hideSoftInputFromWindow(this.windowToken, 0)
        }
    }

    private fun View.layoutParamsWidth(width: Int) {
        layoutParams = layoutParams.also {
            it.width = width
        }
    }
}