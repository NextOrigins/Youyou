package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import com.umeng.socialize.utils.DeviceConfig
import org.jetbrains.anko.doAsync
import kotlin.properties.Delegates

/**
 * @author by user on 2018/1/22.
 */
class QuestionsAndAnswers : Fragment() {

	// property
	private val map = hashMapOf<CharSequence, CharSequence>()
	private val list = arrayListOf<ResponseBean.AnswerList>()
	private var result: ResponseBean.Result by Delegates.notNull()

    //observer
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
				hashMapOf<CharSequence, CharSequence>().run {
					put("userId", userId)
					put("taskId", "1613") // taskId 暂用1613 其他的没数据
					put("type", "5")
					put("status", if (isChecked) "1" else "0")

					doAsync {
						val response = NetBuild.getResponse(this@run, 112)
						Util.uiThread {
							text = if ("0" in response && isChecked)
								"已收藏"
							else
								if ("0" in response) "${(tag as Int) - 1}人收藏" else "${tag}人收藏"
						}
					}

					Unit
				}
			}
		}.let { star = it }

		root.findViewById<Button>(R.id._answer).apply {
			setOnClickListener {
				startActivity(Intent(context, Answers::class.java)
						.putExtra("uid", result.from_uid)
						.putExtra("taskId", result.id.toString())
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
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
			mAdapter.setFootView(this)
		}

		layoutInflater.inflate(R.layout.header_parent_qa, recycle, false).run {
			headTitle = findViewById(R.id.head_title)
			headIcon = findViewById(R.id.head_img)
			headContent = findViewById(R.id.head_content)
			findViewById<View>(R.id.head_toggle).setOnClickListener {
				it.visibility = View.GONE
				headContent.setSingleLine(false)
			}
			mAdapter.setHeadView(this)
		}

		answerCount = root.findViewById(R.id._answer_count)
	}

	override fun initData() {
		val taskId = arguments.getInt("taskId", 0)
		val date = arguments.getString("date")
		map.run {
			put("userId", userId)
			put("taskId", /*taskId*/"1613") // 1613
			put("createDate", date)
			NetBuild.response(this@QuestionsAndAnswers::success,
					ToastUtil::showToast, 200, ResponseBean.AnswerBody::class.java, this)
		}
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
		read.text = if (data.clickSum > 0) "${data.clickSum}万阅读" else "0阅读"

		Glide.with(icon).load(data.faceImg).into(icon)

		praise.isChecked = data.likeCommentStatus == 0

        img.visibility = if (data.commentImg == null) {
            View.GONE
        } else {
            val options = RequestOptions()
                    .placeholder(R.drawable.deftimg)
                    .error(R.drawable.deftimg)
            Glide.with(img).load(data.commentImg).apply(options).into(img)
            View.VISIBLE
        }

		praise.setOnClickListener {
			hashMapOf<CharSequence, CharSequence>().run {
				put("userId", userId)
				put("commentId", data.commentId.toString())
				put("type", "5")
				put("status", if (praise.isChecked) "1" else "0")

				val response = NetBuild.getResponse(this@run, 193)
				if ("0" !in response) {
					showToast("数据错误, 错误代码 {PtsQA}, 请到用户反馈处反馈此问题. 谢谢")
				}
			}
		}

		holder.find<View>(R.id._parent).setOnClickListener {
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
		val options = RequestOptions()
				.placeholder(R.drawable.deftimg)
				.error(R.drawable.deftimg)
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

    fun setObserver(listener: View.() -> Unit) {
        this@QuestionsAndAnswers.obs = listener
    }
}