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
	private val map = hashMapOf<CharSequence, CharSequence>()
	private val list = arrayListOf<ResponseBean.AnswerList>()
	private var result: ResponseBean.Result by Delegates.notNull()
    private val options by lazy {
        return@lazy RequestOptions()
                .placeholder(R.drawable.deftimg)
                .error(R.drawable.deftimg)

    }

    // observer
    private lateinit var obs: View.() -> Unit

	// adapter
	private var mAdapter by notNullSingleValue<AdapterK<ResponseBean.AnswerList>>()
	// fields
	private val userId by preference("userId", "")

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
	// footer
	private var footText by notNullSingleValue<TextView>()
	private var footPrg by notNullSingleValue<ProgressBar>()

	// state
	private var b = true
	private var measured = false

	override fun getContentLayoutId()
			= R.layout.fragment_questions_answers

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
                map["taskId"] = "1613"
                map["type"] = "5"
                map["status"] = if (isChecked) "1" else "0"

                doAsync {
                    val response = NetBuild.getResponse(map, 112)
                    uiThread {
                        text = if ("0" in response && isChecked) {
                            "已收藏"
                        } else {
                            if ("0" in response) "${(tag as Int) - 1}人收藏" else "${tag}人收藏"
                        }
                    }
                }
			}
		}.let { star = it }

		root.findViewById<Button>(R.id._answer).apply {
			setOnClickListener {
				arguments.putString("uid", result.from_uid.toString())
                arguments.putString("taskId", result.id.toString())
				arguments.putString("answerTitle", result.title)
				startActivityForResult(Intent(context, Answers::class.java)
                        .putExtras(arguments), 5)
			}
		}.let { answer = it }

		root.findViewById<SwipeRefreshLayout>(R.id._swipe).apply {
			setOnRefreshListener {
				initData()
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
			findViewById<View>(R.id.head_toggle).setOnClickListener {
				it.visibility = View.GONE
				headContent.setSingleLine(false)
			}
			mAdapter.headView = this
		}

		answerCount = root.findViewById(R.id._answer_count)
	}

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logE("onViewCreated")
    }
	override fun initData() {
        if (!swipe.isRefreshing) swipe.isRefreshing = true
        // TODO : 暂用固定taskID
        val taskId = arguments.getInt("taskId", 0)
        map["userId"] = userId
        map["taskId"] = "1613"
        map["createDate"] = ""
        response(this@QuestionsAndAnswers::success, 200, map)
        Handler().postDelayed({ if (swipe.isRefreshing) swipe.isRefreshing = false },
                2000)
	}

	private fun upData() {
		if (list.isEmpty()) return
        val createDate = list[list.size - 1].createDate
		if (b) {
			footText.text = "加载中"
			footPrg.visibility = View.VISIBLE
		}
		map.run {
			put("createDate", createDate)
			NetBuild.response({
				if (it.menuList.isEmpty() || it.menuList[it.menuList.size - 1].createDate == createDate) {
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
			}, ToastUtil::showToast, 200, ResponseBean.AnswerBody::class.java, this)
		}
	}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 5) {
            initData()
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

		content.text = data.attachedContent ?: "null"

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
			hashMapOf<CharSequence, CharSequence>().run {
				put("userId", userId)
				put("commentId", data.commentId.toString())
				put("type", "5")
				put("status", if (praise.isChecked) "1" else "0")

                doAsync {
                    val response = NetBuild.getResponse(this@run, 193)
                    uiThread {
                        if ("0" !in response) {
                            showToast("数据错误, 错误代码 {PtsQA}, 请到用户反馈处反馈此问题. 谢谢")
                        }
                    }
                }

				Unit
			}
		}

		holder.find<View>(R.id._parent).setOnClickListener {
            val array = mutableList
                    .takeLast(position)
                    .flatMap { arrayListOf(it.commentId.toString()) }
                    .toTypedArray()
            arguments.putString("cId", data.commentId.toString())
            arguments.putBoolean("likeStatus", praise.isChecked)
            arguments.putString("taskId", data.taskId.toString())
            arguments.putString("fromUID", data.from_userId.toString())
			arguments.putStringArray("nextArray", array)
            obs.invoke(it)
		}
	}

	@SuppressLint("SetTextI18n")
	private fun success(t: ResponseBean.AnswerBody) {
        swipe.isRefreshing = false
		mAdapter.addDataAndClear(t.stickNamicfoList)
		mAdapter.addData(t.menuList)
		mAdapter.notifyDataSetChanged()

		b = true
		footText.text = "加载更多"

		result = t.result

		answerCount.text = "${result.comment_count}个回答"
		star.run {
			text = if (result.collectStatus == 0) {
				isChecked = true
				"已收藏"
			} else {
				isChecked = false
				"${result.collect_count}人收藏"
			}
			tag = result.collect_count
		}

		headTitle.text = result.title
		headContent.text = result.content
		Glide.with(headIcon).load(result.imgs).apply(options).into(headIcon)

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

    fun refreshData() {
        initData()
    }

    fun setObserver(listener: View.() -> Unit) {
        this@QuestionsAndAnswers.obs = listener
    }
}