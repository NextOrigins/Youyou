package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
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
import com.umeng.socialize.utils.DeviceConfig
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.properties.Delegates

/**
 * @author by user on 2018/1/22.
 */
class QuestionsAndAnswers : Fragment() {

	// property
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

    // observer
    private lateinit var obs: View.() -> Unit

	// adapter
	private var mAdapter by notNullSingleValue<AdapterK<ResponseBean.AnswerList>>()
	// fields
	private val userId by preference("userId", "")
    private lateinit var taskId: String

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
        bundle?.let {
            taskId = bundle.getString("taskId", "")
        }
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
            headShowAll.visibility = View.GONE
        }
        if (TextUtils.isEmpty(result.imgs) || result.imgs.split('|').first().isEmpty()) {
            headIcon.visibility = View.GONE
        } else {
            Glide.with(headIcon).load(result.imgs).apply(options).into(headIcon)
        }

        if (!measured)
            star.post {
                val point = Point()
                val left = activity.windowManager.defaultDisplay.getSize(point).let {
                    (point.x - (star.measuredWidth * 3)) / 6
                }

                answerCount.setPadding(left, 0, left, 0)
                star.setPadding(left, 0, left, 0)

                answer.layoutParams = answer.layoutParams.also {
                    it.width = left * 2 + star.measuredWidth
                }

                measured = true
            }
    }

	@SuppressLint("SetTextI18n")
	private fun bind(holder: Adapter.Holder,
	                 mutableList: MutableList<ResponseBean.AnswerList>, position: Int) {
		val name = holder.find<TextView>(R.id.item_name)
		val praise = holder.find<CheckBox>(R.id.item_praise)
		val icon = holder.find<ImageView>(R.id.item_icon)
		val content = holder.find<TextView>(R.id.item_content)
		val praises = holder.find<TextView>(R.id.item_praise_count)
		val read = holder.find<TextView>(R.id.item_read_count)
		val img = holder.find<ImageView>(R.id.item_img)

		val data = mutableList[position]

		content.text = data.attachedContent?.replace("￼", "") ?: "null"

		name.text = data.from_nickName
		praises.text = "${data.commentLike}赞"
		read.text = if (data.clickSum > 0) "${data.clickSum} 阅读" else "0 阅读"

		Glide.with(icon).load(data.faceImg).into(icon)

		praise.isChecked = data.likeCommentStatus == 0

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

		praise.setOnClickListener {
            val map = hashMapOf<CharSequence, CharSequence>()
            map["userId"] = userId
            map["commentId"] = data.commentId.toString()
            map["type"] = "5"
            map["status"] = if (praise.isChecked) "1" else "0"

            doAsync {
                val response = NetBuild.getResponse(map, 193)
                if ("0" !in response) {
                    uiThread {
                        praise.isChecked = !praise.isChecked
                        showToast("未知错误, 请到用户反馈处反馈此问题: 错误代码[193]")
                    }
                }
            }
		}

		holder.find<View>(R.id._parent).setOnClickListener {
            val array = mutableList
                    .drop(position + 1)
                    .flatMap { arrayListOf(it.commentId.toString()) }
                    .toTypedArray()
            val minDate = mutableList
                    .forEach { dateFilter = it.createDate }
                    .let { dateFilter }

            arguments.putString("cId", data.commentId.toString())
            arguments.putString("taskId", data.taskId.toString())
            arguments.putString("fromUID", data.from_userId.toString())
            arguments.putString("minCreateDate", minDate)
			arguments.putStringArray("nextArray", array)
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

    /*fun refreshData() {
        requestData()
    }*/

    fun setObserver(listener: View.() -> Unit) {
        this@QuestionsAndAnswers.obs = listener
    }
}