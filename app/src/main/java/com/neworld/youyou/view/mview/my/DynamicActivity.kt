package com.neworld.youyou.view.mview.my

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.mview.common.HorizontalDecoration
import com.neworld.youyou.view.mview.parents.QAController
import kotlinx.android.synthetic.main.activity_dynamic.*
import org.jetbrains.anko.doAsync

/**
 * @author by hhhh on 2018/4/23.
 */
class DynamicActivity : Activity() {

    // view
    private lateinit var mFootText: TextView
    private lateinit var mFootPro: ProgressBar
    private lateinit var mDelete: View
    private lateinit var mCancel: View

    // field
    private val userId by preference("userId", "")
    private lateinit var mAdapter: AdapterK<ResponseBean.DynamicModel>
    private val mDict by lazy {
        hashMapOf<CharSequence, CharSequence>("userId" to userId)
    }
    private val mDialog by lazy {
        val dialog = Dialog(this, R.style.ActionSheetDialogStyle)
        val inflate = layoutInflater.inflate(R.layout.dialog_dynamic, _recycle, false)

        mDelete = inflate.findViewById(R.id.dialog_delete)
        mCancel = inflate.findViewById<View>(R.id.dialog_cancel)

        dialog.setContentView(inflate)
        val w = dialog.window
        w.setGravity(Gravity.BOTTOM)
        val lp = w.attributes
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        lp.width = point.x
        //lp.y = 20;//设置Dialog距离底部的距离
        //       将属性设置给窗体
        w.attributes = lp

        dialog
    }
    private lateinit var mUser: ResponseBean.UserList
    private var over = false

    override fun getContentLayoutId() = R.layout.activity_dynamic

    override fun initWidget() {
        val r = _recycle
        r.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        r.adapter = AdapterK(::onBind, arrayOf(R.layout.item_dynamic), arrayListOf())
                .also { mAdapter = it }
        r.addItemDecoration(HorizontalDecoration(this, 1))
        r.setLoadMore()

        layoutInflater.inflate(R.layout.footview_load_more, r, false).run {
            mFootText = findViewById(R.id.foot_loading)
            mFootPro = findViewById(R.id.foot_progress)
            mAdapter.footView = this
        }

        _swipe.setOnRefreshListener {
            mDict["createDate"] = ""
            mAdapter.clear()
            mAdapter.notifyDataSetChanged()
            initData()
        }

        _close.setOnClickListener { finish() }
    }

    override fun initData() {
        response(::onResponse, 215, mDict)
    }

    private fun onResponse(body: ResponseBean.DynamicBody) {
        if (_swipe.isRefreshing) _swipe.isRefreshing = false

        if (mFootPro.visibility == View.VISIBLE) {
            mFootPro.visibility = View.GONE
            mFootText.text = "加载更多"
        }

        if (body.menuList.size < 10) {
            mFootPro.visibility = View.GONE
            mFootText.text = "没有更多了"
            over = true
        }

        mUser = body.userList

        mAdapter.addData(body.menuList)
        mAdapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    private fun loadMore() {
        if (_swipe.isRefreshing || over) return

        mDict["createDate"] = mAdapter.bean.last().createDate
        mFootPro.visibility = View.VISIBLE
        mFootText.text = "please wait...."
        initData()
    }

    private fun onBind(holder: Adapter.Holder,
                       list: MutableList<ResponseBean.DynamicModel>, position: Int) {
        val model = list[position]

        val icon = holder.find<ImageView>(R.id.item_icon)
        val name = holder.find<TextView>(R.id.item_name)
        val time = holder.find<TextView>(R.id.item_time)
        val content = holder.find<TextView>(R.id.item_content)
        val big = holder.find<ImageView>(R.id.item_big)
        val reply = holder.find<TextView>(R.id.item_reply)
        val other = holder.find<ImageView>(R.id.item_other)

        holder.find<View>(R.id.item_parent).setOnClickListener {
            logE("type = ${model.anStatus}")
            val taskId = when (model.anStatus) {
                1 -> model.commentId
                2 -> model.taskId
                else -> model.answersId
            }.toString()

            logE("dynamic taskId = $taskId")

            val intent = Intent(this, QAController::class.java)
                    .putExtra("taskId", taskId)
                    .putExtra("commentId", model.commentId.toString())
                    .putExtra("date", model.createDate)
                    .putExtra("toDetail", model.anStatus != 1)
                    .putExtra("position", position)
            startActivity(intent)
        }

        val option = RequestOptions()
                .error(R.drawable.deftimg)
                .placeholder(R.drawable.deftimg)

        Glide.with(icon).load(mUser.faceImg).apply(option).into(icon)
        name.text = mUser.nickName
        time.text = model.createDate

        content.visibility = if (model.commentContent.isEmpty()) {
            View.GONE
        } else {
            content.text = if (model.remarkContent.isNotEmpty()) {
                SpannableString("${model.commentContent}//@${model.remarkName}: ${model.remarkContent}").apply {
                    val start = model.commentContent.length + 2
                    val end = start + if (model.remarkName.isNotEmpty()) model.remarkName.length + 1 else 0
                    val colorSpan = ForegroundColorSpan(Color.parseColor("#0099EE"))
                    setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                }
            } else {
                model.commentContent
            }
            View.VISIBLE
        }

        val replyIcon = model.imgs.split('|').first()

        Glide.with(big).load(replyIcon).apply(option).into(big)
        reply.text = model.content

        other.setOnClickListener {
            mDialog.show()

            mDelete.setOnClickListener {
                val map = hashMapOf<String, String>()
                val url: Int
                map["userId"] = userId
                when (model.anStatus) { // TODO : 封装复用一下
                    1 -> {
                        map["taskId"] = model.taskId.toString()
                        url = 211
                    }
                    2 -> {
                        map["commentId"] = model.commentId.toString()
                        url = 212
                    }
                    else -> {
                        map["taskId"] = model.answersId.toString()
                        map["commentId"] = model.commentId.toString()
                        url = 207
                    }
                }
                itemRequest(map, position, url)
            }

            mCancel.setOnClickListener {
                mDialog.dismiss()
            }
        }
    }

    private fun itemRequest(body: HashMap<String, String>, position: Int, url: Int) = doAsync {
        val response = NetBuild
                .getResponse(body, url)
        logE("response = $response")
        uiThread {
            if ("0" in response) {
                mAdapter.remove(position)
                mDialog.dismiss()
            } else {
                showToast("出现未知问题，请稍后重试")
            }
        }
    }

    private fun RecyclerView.setLoadMore() {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!canScrollVertically(1))
                        loadMore()
                }
            }
        })
    }

}