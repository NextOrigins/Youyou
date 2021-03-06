package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.manager.MyApplication
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.nine.CircleImageView
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.HashMap
import kotlin.properties.Delegates

/**
 * @author by user on 2018/1/22.
 */
class AnswerDetail : Fragment() {

    //property
    private var mAdapter by notNullSingleValue<AdapterK<ResponseBean.AnswersDetailList>>()
    private val praises by lazy {
        val map = hashMapOf<CharSequence, CharSequence>()
        map["userId"] = userId
        map["type"] = "5"
        map
    }
    private val options by lazy {
        return@lazy RequestOptions()
                .placeholder(R.drawable.deftimg)
                .error(R.drawable.deftimg)
    }
    private lateinit var user: ResponseBean.Userbean
    private val umListener = object : UMShareListener {
        override fun onResult(p0: SHARE_MEDIA?) {
            doAsync {
                val dict = hashMapOf<CharSequence, CharSequence>()
                dict["taskId"] = mTaskId
                dict["type"] = "5"
                val body = "{\"taskId\":\"$mTaskId\", \"type\":\"5\"}"
                logE("dict = $dict; body = $body")
                val response = NetBuild.getResponse(body, 144)
                if (response != null && "0" in response) {
                    showToast(mContext, "分享成功!")
                }
            }
        }

        override fun onCancel(p0: SHARE_MEDIA?) {
            showToast("取消分享")
        }

        override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
            Toast.makeText(context, p1.toString(), Toast.LENGTH_LONG).show()
        }

        override fun onStart(p0: SHARE_MEDIA?) {
        }
    }

    //View
    private var mRecycle by notNullSingleValue<RecyclerView>()
    private var mWeb by notNullSingleValue<WebView>()
    private var mComment by notNullSingleValue<EditText>()
    private var mSwipe by notNullSingleValue<SwipeRefreshLayout>()
    private var mPreview by notNullSingleValue<LinearLayout>()
    private var mPublish by notNullSingleValue<TextView>()
    private var mCommentCount by notNullSingleValue<TextView>()
    private var mPraiseCount by notNullSingleValue<TextView>()
    private var mLike by notNullSingleValue<CheckBox>()
    private var hintText by notNullSingleValue<TextView>()
    private var hintProgress by notNullSingleValue<ProgressBar>()
    private var mReply by notNullSingleValue<TextView>()
    private var mReview by notNullSingleValue<ImageView>()
    private var mNext by notNullSingleValue<ImageView>()

    //fields
    private val userId by preference("userId", "")
    private var commentId by Delegates.notNull<String>()
    private var itsChecked: Boolean? = null
    private var nextArray: Array<String>? = null
    private var index = 0
    private var fromUserId = ""
    private var fromCommentId = ""
    private var replyUserName: String? = null
    private var replyContent: String? = null
    private var onStart: (() -> Unit)? = null
    private var onLoading: ((new: Int) -> Unit)? = null
    private var onStop: (() -> Unit)? = null
    private var toPos = 1
    private var mInDynamic = false
    private val mContext by lazy { context!! }
    private lateinit var mTaskId: String
    private lateinit var mCommentBean: ResponseBean.CommentBean
    private lateinit var mTitle: String

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
    private var lastCreateDate by Delegates.vetoable("") { _, old, new ->
        hintProgress.visibility = View.GONE

        if (new.isEmpty()) {
            hintText.text = "没有更多数据了"
            return@vetoable true
        }
        hintText.text = "点击加载更多"

        if (old.isNotEmpty()) {
            val one = toDateLong(new)
            val two = toDateLong(old)

            return@vetoable one < two
        }

        return@vetoable true
    }

    override fun initArgs(bundle: Bundle?) {
        if (bundle == null) return

        index = 0
        commentId = bundle.getString("cId")
        mInDynamic = bundle.getBoolean("inDynamic")
        mTaskId = bundle.getString("taskId", "")
        mTitle = bundle.getString("title", "优优家长")
    }

    override fun getContentLayoutId() = R.layout.fragment_answers_detail

    @SuppressLint("SetJavaScriptEnabled")
    override fun initWidget(root: View) {
        // 评论EditText
        mComment = root.findViewById<EditText>(R.id._comment).apply {
            setOnTouchListener { _, _ ->
                isShowSoftInput = true
                false
            }
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null && s.isNotEmpty()) {
                        ContextCompat.getColor(context, R.color.colorAccent)
                    } else {
                        ContextCompat.getColor(context, android.R.color.darker_gray)
                    }.let(mPublish::setTextColor)
                }
            })
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
                    arrayOf(R.layout.item_answers_detail), arrayListOf()).also { mAdapter = it }
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    isShowSoftInput = false
                }
                false
            }
        }

        // Bottom
        mPreview = root.findViewById<LinearLayout>(R.id._bottom_preview).apply {
            // 滚动到第一条
            mReview = findViewById<ImageView>(R.id._review).apply {
                setOnClickListener {
                    mRecycle.toPosition(toPos)
                    toPos = if (toPos == 1) 0 else 1
                }
            }
            // 点赞
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
            // Next Page
            mNext = findViewById<ImageView>(R.id._next_comment).apply {
                setOnClickListener {
                    toNext()
                }
            }

            val point = Point()
            activity?.windowManager?.defaultDisplay?.getSize(point)
            // 点击清理缓存数据 & 弹出键盘
            mReply = findViewById<TextView>(R.id._ac_reply).apply {
                setOnClickListener {
                    isShowSoftInput = true
                    fromUserId = ""
                    fromCommentId = ""
                    replyContent = null
                    replyUserName = null
                }
                layoutParams = layoutParams
                        .also { it.width = (point.x - resources.getDimensionPixelOffset(R.dimen.dp30)) / 2 }
            }
            mNext.post {
                val width = (point.x - resources.getDimensionPixelOffset(R.dimen.dp40)) / 2
                val x = width / 3
                mReview.layoutParamsWidth(x)
                mLike.layoutParamsWidth(x)
                mNext.layoutParamsWidth(x)
            }
        }

        mPublish = root.findViewById(R.id._publish) // 回复按钮

        // 发布评论
        mPublish.setOnClickListener {
            if (mComment.text.isEmpty()) {
                showToast("请输入内容")
                return@setOnClickListener
            }

            val map = hashMapOf<CharSequence, CharSequence>()
            map["userId"] = userId
            map["taskId"] = commentId // 2级页面commentId
            map["comment_id"] = if (fromCommentId.isEmpty()) commentId else fromCommentId// 评论""  回复别人commentId
            map["from_userId"] = fromUserId // 回复的from_userId else ""  TODO :
            map["content"] = mComment.text.toString()

            fromCommentId = ""

            doAsync {
                val response = NetBuild.getResponse(map, 206)
                logE("206 response = $response")
                val responseMap = Gson().fromJson<HashMap<String, String>>(response,
                        object : TypeToken<HashMap<String, String>>() {}.type)
                if (responseMap["status"] == "0") {
                    val insert = ResponseBean.AnswersDetailList(
                            user.id,
                            0,
                            user.faceImg,
                            0,
                            1,
                            0,
                            mComment.text.toString(),
                            user.nickName,
                            replyContent,
                            replyUserName,
                            "",
                            "",
                            responseMap["comentId"] ?: "",
                            commentId.toInt(),
                            Util.getDateFormatInstance().format(Date())
                    )
                    uiThread {
                        isShowSoftInput = false
                        /*initData(commentId)*/
                        mComment.text.clear()
                        mAdapter.insertData(insert)
                    }
                }
            }
        }

        // webView和其他其他空间初始化
        mWeb = layoutInflater.inflate(R.layout.head_answers_detail, mRecycle, false)
                .also { mAdapter.headView = it }
                .run {
                    mCommentCount = findViewById(R.id.head_comment_count)
                    mPraiseCount = findViewById(R.id.head_praise_count)
                    findViewById(R.id.head_web)
                }

        // 加载更多控件初始化 & 事件处理
        layoutInflater.inflate(R.layout.foot_answers_detail, mRecycle, false).apply {
            hintText = findViewById(R.id._click_load_more)
            hintProgress = findViewById(R.id._click_load_progress)
            findViewById<View>(R.id._parent).setOnClickListener {
                if (hintProgress.visibility == View.VISIBLE ||
                        hintText.text.toString() == getString(R.string.no_more_data))
                    return@setOnClickListener

                hintProgress.visibility = View.VISIBLE
                hintText.text = getString(R.string.please_wait)
                val map = hashMapOf<CharSequence, CharSequence>()
                map["userId"] = userId
                map["commentId"] = commentId
                map["createDate"] = lastCreateDate

                response(::addMore, 202, map, {
                    hintText.text = getString(R.string.load_more_on_click)
                    hintProgress.visibility = View.GONE
                    showToast(it)
                })
            }
        }.let { mAdapter.footView = it }

        setHasOptionsMenu(true)
    }

    override fun initData() {
//        if (!mSwipe.isRefreshing) mSwipe.isRefreshing = true
        val url = "${Fields.BASEURL}201?userId=$userId&commentId=$commentId"
        configWeb(mWeb)
        mWeb.loadUrl(url)

        val map = hashMapOf<CharSequence, CharSequence>()
        map["userId"] = userId
        map["commentId"] = commentId
        map["createDate"] = ""

        response(this@AnswerDetail::onResponse, 202, map)
    }

    @SuppressLint("SetTextI18n")
    private fun onResponse(t: ResponseBean.AnswersDetailBody) {
        if (t.status == 1) {
            toNext()
            return
        }

        if (t.menuList.isNotEmpty()) {
            mCommentCount.text = "评论 ${t.commentBean.commentCount}"
            mPraiseCount.text = "${t.commentBean.commentLike} 赞"
            t.menuList.forEach { lastCreateDate = it.createDate }
            hintText.visibility = View.VISIBLE
        } else {
            mCommentCount.text = "评论 0"
            mPraiseCount.text = "0 赞"
            lastCreateDate = ""
            hintText.visibility = View.GONE
        }

        mAdapter.addDataAndClear(t.menuList)
        mAdapter.notifyDataSetChanged()

        user = t.userbean
        mCommentBean = t.commentBean
        mLike.post { mLike.isChecked = t.commentBean.likeCommentStatus == 0 }

        nextArray = arguments!!.getStringArray("nextArray")
    }

    private fun addMore(t: ResponseBean.AnswersDetailBody) {
        if (t.status == 1) {
            showToast(mContext, "出现未知错误~如反复出现请尝试重新登录账号")
            return
        }
        mAdapter.addData(t.menuList)
        mAdapter.notifyDataSetChanged()

        if (t.menuList.isNotEmpty())
            t.menuList.forEach { lastCreateDate = it.createDate }
        else
            lastCreateDate = ""
    }

    // 设置控件数据
    private fun itemBind(holder: Adapter.Holder,
                         mutableList: MutableList<ResponseBean.AnswersDetailList>, position: Int) {
        val icon = holder.find<CircleImageView>(R.id.item_icon)
        val name = holder.find<TextView>(R.id.item_name)
        val content = holder.find<TextView>(R.id.item_content)
        val praise = holder.find<CheckBox>(R.id.item_praise)
        val date = holder.find<TextView>(R.id.item_date)
        val reply = holder.find<TextView>(R.id.item_reply)
        val delete = holder.find<TextView>(R.id.item_delete)

        val data = mutableList[position]

        content.text = if (data.remarkContent != null && data.remarkContent.isNotEmpty()) {
            SpannableString("${data.content}//@${data.remarkName}: ${data.remarkContent}").apply {
                val start = data.content.length + 2
                val end = start + if (data.remarkName != null) data.remarkName.length + 1 else 0
                val colorSpan = ForegroundColorSpan(Color.parseColor("#0099EE"))
                setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        } else {
            data.content
        }

        name.text = data.from_nickName
        praise.text = if (data.commentLike < 1) "赞" else data.commentLike.toString()
        praise.isChecked = data.likeCommentStatus == 0

        date.text = data.createDate
        reply.text = "回复"

        Glide.with(icon).load(data.faceImg).apply(options).into(icon)

        reply.setOnClickListener {
            fromUserId = data.from_userId.toString()
            fromCommentId = data.commentId // TODO :
            replyContent = data.content
            replyUserName = data.from_nickName
            isShowSoftInput = true
        }

        delete.visibility = if (data.from_userId.toString() == userId) {
            delete.setOnClickListener {
                val enter: () -> Unit = {
                    val map = hashMapOf<CharSequence, CharSequence>()
                    map["userId"] = userId
                    map["taskId"] = data.taskId.toString()
                    map["commentId"] = data.commentId

                    doAsync {
                        val response = NetBuild.getResponse(map, 207)
                        uiThread { if ("0" in response) mAdapter.remove(position) }
                    }
                }
                displayDialog(context ?: MyApplication.sContext, "确定删除吗", enter)
            }
            View.VISIBLE
        } else {
            View.GONE
        }

        praise.setOnClickListener {
            praises["commentId"] = data.commentId
            praises["status"] = if (praise.isChecked) "1" else "0"

            doAsync {
                val response = NetBuild.getResponse(praises, 193)
                if ("0" in response) {
                    uiThread {
                        praise.text = when {
                            data.likeCommentStatus == 0 && !praise.isChecked -> {
                                data.likeCommentStatus = 1
                                if (--data.commentLike < 1) "赞" else data.commentLike.toString()
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

    // 配置WebView
    @SuppressLint(value = ["SetJavaScriptEnabled"])
    private fun configWeb(it: WebView) = with(it) {
        isFocusable = false
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                isShowSoftInput = false
            }
            false
        }
        settings.javaScriptEnabled = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress > 95) {
                    if (mSwipe.isRefreshing) mSwipe.isRefreshing = false
                    if (arguments!!.getBoolean("review", false)) {
                        mRecycle.toPosition(1)
                    } else {
                        mRecycle.toPosition(0)
                    }
                }
                onLoading?.invoke(newProgress)
            }
        }
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    if ("http://106.14.251.200/neworld/user/143" in it) {
                        if (!mInDynamic) startActivity(Intent(context, Answers::class.java)
                                .putExtras(arguments))
                    } else if ("http://106.14.251.200/neworld/user/153" in it) {
//                            mRecycle.toPosition(1)
                        activity?.onKeyDown(KeyEvent.KEYCODE_BACK, null)
                    }
                }
                return true
            }

            override fun onReceivedError(view: WebView?,
                                         errorCode: Int, description: String?, failingUrl: String?) {
                showToast("error : $errorCode")
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                onStart?.invoke()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                onStop?.invoke()
            }
        }
    }

    // 下一个回答
    private fun toNext() {
        if (nextArray != null && nextArray!!.size > index) {
            clearWebCache()
            val id = nextArray!![index++]
            commentId = id
            initData()
        } else {
            showToast("最后一页啦")
        }
    }

    // 跳转到指定item
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // debug
//        val url = "http://192.168.1.123:8080/neworld/user/210?commentId=$commentId&userId=$userId"
        // release TODO : release版本URL不对
        val url = "http://192.168.1.123:8082/neworld/user/210?commentId=$commentId&userId=$userId"
        val um = UMWeb(url)
        um.title = mTitle
        um.description = mCommentBean.attachedContent
        if (!TextUtils.isEmpty(mCommentBean.imgUrl)) {
            um.setThumb(UMImage(context, mCommentBean.imgUrl))
        } else {
            um.setThumb(UMImage(context, R.drawable.bigimage))
        }
        ShareAction(activity)
                .withMedia(um)
                .setDisplayList(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(umListener)
                .open()

        return true
    }

    fun resize() {
        val point = Point()
        activity?.windowManager?.defaultDisplay?.getSize(point)
        val width = (point.x - resources.getDimensionPixelOffset(R.dimen.dp40)) / 2
        val x = width / 3

        mReply.layoutParamsWidth(width)
        mReview.layoutParamsWidth(x)
        mLike.layoutParamsWidth(x)
        mNext.layoutParamsWidth(x)
    }

    fun clearWebCache() {
        mWeb.pauseTimers()
        mWeb.stopLoading()
        mWeb.clearCache(true)
    }

    fun loadingListener(onStart: () -> Unit, onLoading: (new: Int) -> Unit, onStop: () -> Unit) {
        this@AnswerDetail.onStart = onStart
        this@AnswerDetail.onLoading = onLoading
        this@AnswerDetail.onStop = onStop
    }
}