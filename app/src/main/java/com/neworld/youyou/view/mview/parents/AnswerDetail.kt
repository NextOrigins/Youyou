package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
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
    private val options by lazy {
        return@lazy RequestOptions()
                .placeholder(R.drawable.deftimg)
                .error(R.drawable.deftimg)
    }
    private var fromUserId = ""
    private var fromCommentId = ""
    /*private var cacheCreateDate = ""
        set(value) {
            hintText.text = if (value.isEmpty()) {
                getString(R.string.no_more_data)
            } else {
                getString(R.string.load_more_on_click)
            }
            hintProgress.visibility = View.GONE
            field = value
        }*/

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

    //fields
    private val userId by preference("userId", "")
    private var commentId by Delegates.notNull<String>()
    private var itsChecked: Boolean? = null
    private var nextArray: Array<String>? = null
    private var index = 0

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
            hintText.text = getString(R.string.no_more_data)

            return@vetoable true
        }
        hintText.text = getString(R.string.load_more_on_click)

        try {
            val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
//            val format = SimpleDateFormat.getDateTimeInstance(DateFormat.YEAR_FIELD, DateFormat.ERA_FIELD, Locale.CHINA)
            if (old.isNotEmpty()) {
                val one = format.parse(new)
                val two = format.parse(old)
                val result = two.time - one.time
                return@vetoable result > 0
            }
        } catch (e: ParseException) {
            val content: CharSequence = "对比时间出了问题, 请到用户反馈处反馈此问题{错误代码[ad85%95]} 谢谢."
            hintText.text = content
            e.printStackTrace()
        }
        return@vetoable true
    }

    override fun initArgs(bundle: Bundle?) {
        bundle?.let {
            nextArray = it.getStringArray("nextArray")
        }
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
                initData(commentId)
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
                setOnClickListener {
                    if (nextArray != null && nextArray!!.size > index) {
                        val id = nextArray!![index++]
                        commentId = id
                        initData(id)
                    } else {
                        showToast("最后一页啦")
                    }
                }
            }
            findViewById<TextView>(R.id._ac_reply).apply {
                setOnClickListener {
                    isShowSoftInput = true
                    fromUserId = ""
                    fromCommentId = ""
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

        mPublish = root.findViewById(R.id._publish)

        mPublish.setOnClickListener {
            if (mComment.text.isEmpty()) {
                showToast("请输入内容")
                return@setOnClickListener
            }

            val map = hashMapOf<CharSequence, CharSequence>()
            map["userId"] = userId
            map["taskId"] = commentId // 2级页面commentId
            map["comment_id"] = fromCommentId // 评论""  回复别人commentId
            map["from_userId"] = fromUserId // 回复的from_userId else ""
            map["content"] = mComment.text.toString()

            doAsync {
                val response = NetBuild.getResponse(map, 206)
                if ("0" in response) {
                    val insert = ResponseBean.AnswersDetailList(
                            userId.toInt(),
                            0,
                            "",
                            0,
                            0,
                            0,
                            mComment.text.toString(),
                            "",
                            "",
                            "",
                            "",
                            "",
                            commentId.toInt(),
                            0,
                            SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date()).toString()
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

        mWeb = layoutInflater.inflate(R.layout.head_answers_detail, mRecycle, false)
                .also { mAdapter.headView = it }
                .run {
                    mCommentCount = findViewById(R.id.head_comment_count)
                    mPraiseCount = findViewById(R.id.head_praise_count)
                    findViewById<WebView>(R.id.head_web)
                }.apply(this@AnswerDetail::configWeb)

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
    }

    override fun initData() {
        commentId = arguments.getString("cId")
        initData(commentId)
    }

    private fun initData(commentId: String) {
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
            showToast("{数据错误, 请到用户反馈处反馈此问题: 错误代码[202]}")
            return
        }

        if (t.menuList.isNotEmpty()) {
            mCommentCount.text = "评论 ${t.commentBean.commentCount}"
            mPraiseCount.text = "${t.commentBean.commentLike} 赞"
            t.menuList.forEach { lastCreateDate = it.createDate }
        } else {
            mCommentCount.text = "评论 0" // TODO : 测试
            mPraiseCount.text = "0 赞"
            lastCreateDate = ""
        }

        mAdapter.addDataAndClear(t.menuList)
        mAdapter.notifyDataSetChanged()

        logE("lastCreateDate = $lastCreateDate")
    }

    private fun addMore(t: ResponseBean.AnswersDetailBody) {
        if (t.status == 1) {
            showToast("{数据异常, 请反馈此问题: 错误代码[202]}")
            return
        }
        mAdapter.addData(t.menuList)
        mAdapter.notifyDataSetChanged()

        lastCreateDate = if (t.menuList.isNotEmpty()) t.menuList.last().createDate else ""
        if (t.menuList.isNotEmpty())
            t.menuList.forEach { lastCreateDate = it.createDate }
        else
            lastCreateDate = ""
    }

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

        data.remarkContent?.let {
            logE("remarkName = ${data.remarkName}; remarkContent = ${data.remarkContent}")
        }

        content.text = if (data.remarkContent != null) {
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
        praise.text = data.commentLike.toString()
        praise.isChecked = data.likeCommentStatus == 0

        date.text = data.createDate
        reply.text = "回复"

        Glide.with(icon).load(data.faceImg).apply(options).into(icon)

        reply.setOnClickListener {
            fromUserId = data.from_userId.toString()
            fromCommentId = data.commentId.toString()
            isShowSoftInput = true
        }

        delete.visibility = if (data.from_userId.toString() == userId) {
            delete.setOnClickListener {
                val enter: () -> Unit = {
                    val map = hashMapOf<CharSequence, CharSequence>()
                    map["userId"] = userId
                    map["taskId"] = data.taskId.toString()
                    map["commentId"] = data.commentId.toString()

                    doAsync {
                        val response = NetBuild.getResponse(map, 207)
                        uiThread { if ("0" in response) mAdapter.remove(position) }
                    }
                }
                displayDialog(context, "确定删除吗", enter)
            }
            View.VISIBLE
        } else {
            View.GONE
        }

        praise.setOnClickListener {
            praises["commentId"] = data.commentId.toString()
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
        isFocusable = false
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