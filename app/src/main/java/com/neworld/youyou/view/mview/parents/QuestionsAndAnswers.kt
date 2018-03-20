package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.mview.common.BigPicActivity
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import com.umeng.socialize.utils.DeviceConfig
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.properties.Delegates

/**
 * @author by user on 2018/1/22.
 */
class QuestionsAndAnswers : Fragment() {

	// property
    private val dp15 by lazy {
        resources.getDimensionPixelOffset(R.dimen.dp15)
    }
	private val list = arrayListOf<ResponseBean.AnswerList>()
	private var result: ResponseBean.Result by Delegates.notNull()
    private val options by lazy {
        return@lazy RequestOptions()
                .placeholder(R.drawable.deftimg)
                .error(R.drawable.deftimg)
    }
    private var dateFilter by Delegates.vetoable("") {
        _, old, new ->
        if (old.isNotEmpty()) {
            val one = toDateLong(new)
            val two = toDateLong(old)
            return@vetoable one < two
        }

        return@vetoable true
    }
    private val selectedBg by lazy {
        ContextCompat.getDrawable(context, R.drawable.push2).also {
            it.setBounds(0, 0, dp15, dp15)
        }
    }
    private val cancelBg by lazy {
        ContextCompat.getDrawable(context, R.drawable.push).also {
            it.setBounds(0, 0, dp15, dp15)
        }
    }
    private val reviewBg by lazy {
        ContextCompat.getDrawable(context, R.drawable.qa_review).also {
            it.setBounds(0, 0, dp15, dp15)
        }
    }
    private val imgWideHigh by lazy {
        val point = Point()
        activity.windowManager.defaultDisplay.getSize(point)
        (point.x - (resources.getDimensionPixelOffset(R.dimen.dp10) - dp15 * 2)) / 3
    }

    // observer
    private lateinit var obs: View.() -> Unit
    private val umListener = object : UMShareListener {
        override fun onResult(p0: SHARE_MEDIA?) {
            doAsync {
                val body = "{\"taskId\":\"$taskId\", \"type\":\"5\"}"
                val response = NetBuild.getResponse(body, 144)
                if (response != null && "0" in response) {
                    uiThread { showToast("分享成功!") }
                } else {
                    uiThread { showToast("分享失败, 错误代码[QAA88]") }
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

	// adapter
	private var mAdapter by notNullSingleValue<AdapterK<ResponseBean.AnswerList>>()
	// fields
	private val userId by preference("userId", "")
    private lateinit var taskId: String
    private var starWidth = 0
    private var answerCountWidth = 0


	// View
	private var recycle by notNullSingleValue<RecyclerView>()
	private var answerCount by notNullSingleValue<TextView>()
	private var star by notNullSingleValue<CheckBox>()
	private var answer by notNullSingleValue<Button>()
	private var swipe by notNullSingleValue<SwipeRefreshLayout>()
	// header
	private var headTitle by notNullSingleValue<TextView>()
	private var headIcon by notNullSingleValue<ImageView>()
	private var headContent by notNullSingleValue<TextView>()
    private var headShowAll by notNullSingleValue<View>()
	// footer
	private var footText by notNullSingleValue<TextView>()
	private var footPrg by notNullSingleValue<ProgressBar>()

	// state
	private var b = true
	private var measured = false

	override fun getContentLayoutId()
			= R.layout.fragment_questions_answers

    override fun initArgs(bundle: Bundle?) {
        if (bundle == null) return
        taskId = bundle.getString("taskId", "")
    }

	override fun initWidget(root: View) {
		root.findViewById<RecyclerView>(R.id._recycle).apply {
			layoutManager = LinearLayoutManager(DeviceConfig.context, LinearLayoutManager.VERTICAL, false)
			adapter = AdapterK(this@QuestionsAndAnswers::bind, R.layout.item_answer, list).also { mAdapter = it }
			addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
			addOnScrollListener(object : RecyclerView.OnScrollListener() {
				override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
					if (newState == RecyclerView.SCROLL_STATE_IDLE)
						if (!canScrollVertically(1))
							upData()
				}
			})
		}.let { recycle = it }

		root.findViewById<CheckBox>(R.id._star).apply {
			setOnClickListener {
				val map = hashMapOf<CharSequence, CharSequence>()
                map["userId"] = userId
                map["taskId"] = taskId
                map["type"] = "5"
                map["status"] = if (isChecked) "1" else "0"

                doAsync {
                    val response = NetBuild.getResponse(map, 112)
                    uiThread {
                        if ("0" !in response) isChecked = !isChecked

                        text = if (isChecked) "已收藏" else "${tag}人收藏"
                    }
                }
			}
		}.let { star = it }

		root.findViewById<Button>(R.id._answer).apply {
			setOnClickListener {
				startActivityForResult(Intent(context, Answers::class.java)
                        .putExtras(arguments), 5)
			}
		}.let { answer = it }

		root.findViewById<SwipeRefreshLayout>(R.id._swipe).apply {
			setOnRefreshListener {
				requestData()
			}
		}.let { swipe = it }

		layoutInflater.inflate(R.layout.footview_parents_qa, recycle, false).run {
			footText = findViewById(R.id.foot_loading)
			footPrg = findViewById(R.id.foot_progress)
			mAdapter.footView = this
		}

		layoutInflater.inflate(R.layout.header_parent_qa, recycle, false).run {
			headTitle = findViewById(R.id.head_title)
			headIcon = findViewById(R.id.head_img)
			headContent = findViewById(R.id.head_content)
			headShowAll = findViewById<View>(R.id.head_toggle).apply {
                setOnClickListener {
                    it.visibility = View.GONE
                    headContent.setSingleLine(false)
                }
            }
			mAdapter.headView = this
		}

		answerCount = root.findViewById(R.id._answer_count)
	}

	override fun initData() {
        if (list.isNotEmpty()) return
        requestData()
	}

    private fun requestData() {
        if (!swipe.isRefreshing) swipe.isRefreshing = true

        val map = hashMapOf<CharSequence, CharSequence>()
        map["userId"] = userId
        map["taskId"] = taskId
        map["createDate"] = ""

        response(this@QuestionsAndAnswers::success, 200, map)
    }

	private fun upData() {
		if (list.isEmpty()) return

        list.forEach { dateFilter = it.createDate }

		if (b) {
			footText.text = "加载中"
			footPrg.visibility = View.VISIBLE
		}

        val map = hashMapOf<CharSequence, CharSequence>()

        map["userId"] = userId
        map["taskId"] = taskId
        map["createDate"] = dateFilter

        response<ResponseBean.AnswerBody>({
            if (it.menuList == null || it.menuList.isEmpty()){
                if (b) {
                    footText.text = "没有更多数据了"
                    footPrg.visibility = View.GONE
                    b = false
                }
                return@response
            }
            mAdapter.addData(it.menuList)
            mAdapter.notifyDataSetChanged()
            footText.text = "加载更多"
            footPrg.visibility = View.GONE
        }, 200, map)
	}

    @SuppressLint("SetTextI18n")
    private fun success(t: ResponseBean.AnswerBody) {
        swipe.isRefreshing = false
        if (t.result == null || t.stickNamicfoList == null || t.menuList == null || t.status == 1) {
            Toast.makeText(context,
                    "服务器无数据, 可能此话题已被关闭, 如有疑问请到用户反馈处反馈此问题",
                    Toast.LENGTH_LONG).show()
            Handler().postDelayed({
                val intent = activity.intent.putExtra("error", true)
                activity.setResult(21, intent)
                activity.finish()
            }, 500)
            return
        }

        dateFilter = ""

        mAdapter.addDataAndClear(t.stickNamicfoList)
        mAdapter.addData(t.menuList)
        mAdapter.notifyDataSetChanged()

        b = true
        footText.text = if (t.stickNamicfoList.custom() && t.menuList.custom()) "没有更多数据了"
        else  "加载更多"

        result = t.result.also {
            arguments.putString("uid", it.from_uid.toString())
            arguments.putString("taskId", it.id.toString())
            arguments.putString("answerTitle", it.title)
        }

        answerCount.text = "${result.comment_count}个回答"
        star.run {
            text = if (result.collectStatus == 0) {
                isChecked = true
                tag = result.collect_count - 1
                "已收藏"
            } else {
                isChecked = false
                tag = result.collect_count
                "${result.collect_count}人收藏"
            }
        }

        headTitle.text = result.title
        headContent.text = result.content
        if (TextUtils.isEmpty(result.content)) {
            headContent.visibility = View.GONE
        } else {
            headShowAll.visibility = View.VISIBLE
        }

        val split = if (!TextUtils.isEmpty(result.imgs)) result.imgs.split('|') else null

        if (split == null || split[0].isEmpty()) {
            headIcon.visibility = View.GONE
        } else {
            Glide.with(headIcon).load(split.first()).apply(options).into(headIcon)
            headIcon.setOnClickListener { BigPicActivity.launch(activity as AppCompatActivity, it, split.first()) }
        }

        if (!measured)
            star.post {
                starWidth = star.measuredWidth
                answerCountWidth = answerCount.measuredWidth

                val point = Point()
                activity.windowManager.defaultDisplay.getSize(point)

                var width = point.x / 3
                answer.layoutParams = answer.layoutParams.also {
                    it.width = width
                }

                width = ((width * 2) - (starWidth + answerCountWidth)) / 4

                answerCount.paddingSet(width)
                star.paddingSet(width)

                measured = true
            }
    }

	@SuppressLint("SetTextI18n")
	private fun bind(holder: Adapter.Holder,
	                 mutableList: MutableList<ResponseBean.AnswerList>, position: Int) {
		val name = holder.find<TextView>(R.id.item_name)                // Authors
		val praise = holder.find<TextView>(R.id.item_praise)            // 点赞状态展示
		val icon = holder.find<ImageView>(R.id.item_icon)               // 头像
		val content = holder.find<TextView>(R.id.item_content)          // 内容
//		val praises = holder.find<TextView>(R.id.item_praise_count)     // 点赞数
		val read = holder.find<TextView>(R.id.item_read_count)          // 阅读数
		val img = holder.find<ImageView>(R.id.item_img)                 // 回复图片
        val commentCount = holder.find<TextView>(R.id.item_comment)     // 评论数
        val shareCount = holder.find<TextView>(R.id.item_share)         // 分享数

		val data = mutableList[position]

		content.text = (data.attachedContent?.replace("￼", "") ?: "null").trim('\n')

		name.text = data.from_nickName
        praise.text = if (data.commentLike > 0) "${data.commentLike}" else "赞"
		read.text = if (data.clickSum > 0) "${data.clickSum} 阅读" else "0 阅读"
        commentCount.text = data.comment_count.toString()
        shareCount.text = if (data.transmit_count > 0) data.transmit_count.toString() else "分享"

        praise.tag = data.commentLike

		Glide.with(icon).load(data.faceImg).into(icon)

        commentCount.setCompoundDrawables(reviewBg, null, null, null)

        var isChecked = data.likeCommentStatus == 0

        if (isChecked) {
            praise.setCompoundDrawables(selectedBg, null, null, null)
        } else {
            praise.setCompoundDrawables(cancelBg, null, null, null)
        }

        holder.find<View>(R.id.item_share_c).setOnClickListener {
            // debug
            val url = "http://192.168.1.123:8080/neworld/user/210?commentId=${data.commentId}&userId=$userId"
            // release
//            val url = "http://192.168.1.123:8080/neworld/user/210?commentId=${data.commentId}&userId=$userId"
            val um = UMWeb(url)
            um.title = result.title
            um.description = data.content
            if (!TextUtils.isEmpty(data.commentImg)) {
                um.setThumb(UMImage(context, data.commentImg))
            }
            ShareAction(activity)
                    .withMedia(um)
                    .setDisplayList(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                    .setCallback(umListener)
                    .open()
        }

        holder.find<View>(R.id.item_comment_c).setOnClickListener {
            if (data.comment_count > 0) {
                newModel(mutableList, position)
                arguments.putBoolean("review", data.comment_count > 0)
                obs.invoke(it)
            } else {
                newModel(mutableList, position)
                arguments.putBoolean("review", false)
                obs.invoke(it)
            }
        }

        holder.find<View>(R.id.item_praise_c).setOnClickListener {
            val map = hashMapOf<CharSequence, CharSequence>()
            map["userId"] = userId
            map["commentId"] = data.commentId.toString()
            map["type"] = "5"
            map["status"] = if (isChecked) "0" else "1"

            doAsync {
                NetBuild.getResponse(map, 193).let {
                    if ("0" in it) {
                        var temp = praise.tag as Int
                        uiThread {
                            if (!isChecked) {
                                praise.setCompoundDrawables(selectedBg, null, null, null)
                                praise.text = "${++temp}"
                            } else {
                                praise.setCompoundDrawables(cancelBg, null, null, null)
                                praise.text = if (--temp == 0) "赞" else temp.toString()
                            }
                            isChecked = !isChecked
                            praise.tag = temp
                        }
                    } else {
                        showToast("出错了!(如果频繁出错请到用户反馈处反馈错误代码[193])")
                    }
                }
            }
        }

        img.visibility = if (TextUtils.isEmpty(data.commentImg)) {
            View.GONE
        } else {
            val options = RequestOptions()
                    .placeholder(R.drawable.deftimg)
                    .error(R.drawable.deftimg)
            Glide.with(img).load(data.commentImg).apply(options).into(img)
            img.setOnClickListener { BigPicActivity.launch(activity as AppCompatActivity,
                    img, data.commentImg!!) }
            View.VISIBLE
        }

        img.post {
            img.layoutParams = img.layoutParams.also {
                it.width = imgWideHigh
                it.height = imgWideHigh
            }
        }

		holder.find<View>(R.id._parent).setOnClickListener {
            newModel(mutableList, position)
            arguments.putBoolean("review", false)
            obs.invoke(it)
		}
	}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 5) {
            requestData()
        }
    }

    private fun <T> List<T>.custom(): Boolean {
        return isEmpty() || size < 10
    }

    private fun newModel(list: MutableList<ResponseBean.AnswerList>, position: Int) {
        val array = list
                .drop(position + 1)
                .flatMap { arrayListOf(it.commentId.toString()) }
                .toTypedArray()
        val minDate = list
                .forEach { dateFilter = it.createDate }
                .let { dateFilter }

        val data = list[position]
        val bundle = Bundle()
        bundle.putString("cId", data.commentId.toString())
        bundle.putString("taskId", data.taskId.toString())
        bundle.putString("fromUID", data.from_userId.toString())
        bundle.putString("minCreateDate", minDate)
        bundle.putStringArray("nextArray", array)
        arguments.putAll(bundle)
    }

    /*fun refreshData() {
        requestData()
    }*/

    fun setObserver(listener: View.() -> Unit) {
        this@QuestionsAndAnswers.obs = listener
    }

    fun resize() {
        val point = Point()
        activity.windowManager.defaultDisplay.getSize(point)

        var width = point.x / 3
        answer.layoutParams = answer.layoutParams.also {
            it.width = width
        }

        width = ((width * 2) - (starWidth + answerCountWidth)) / 4

        answerCount.paddingSet(width)
        star.paddingSet(width)
    }

    private fun View.paddingSet(offset: Int) {
        setPadding(offset, 0, offset, 0)
    }
}