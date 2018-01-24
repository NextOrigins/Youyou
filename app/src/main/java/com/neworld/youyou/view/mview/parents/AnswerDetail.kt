package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.webkit.*
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.nine.CircleImageView

/**
 * @author by user on 2018/1/22.
 */
class AnswerDetail : Fragment() {

    //property
    private var mAdapter by notNullSingleValue<AdapterK<ResponseBean.AnswersDetailList>>()

    //View
    private var loading by notNullSingleValue<FrameLayout>()
    private var recycle by notNullSingleValue<RecyclerView>()
    private var web by notNullSingleValue<WebView>()

    //fields
    private val userId by preference("userId", "")

    override fun getContentLayoutId() = R.layout.activity_answers_detail

    @SuppressLint("SetJavaScriptEnabled")
    override fun initWidget(root: View) {
        loading = root.findViewById(R.id._loading)

        recycle = root.findViewById<RecyclerView>(R.id._recycle).apply {
            layoutManager = LinearLayoutManager(context, 1, false)
            addItemDecoration(DividerItemDecoration(context, 1))
            adapter = AdapterK(this@AnswerDetail::itemBind,
                    R.layout.item_answers_detail, arrayListOf()).also { mAdapter = it }
        }

        web = layoutInflater.inflate(R.layout.head_answers_detail, recycle, false)
                .also { mAdapter.setHeadView(it) }
                .findViewById<WebView>(R.id.head_web)
                .apply(this@AnswerDetail::configWeb)
    }

    override fun initData() {
        val commentId = arguments.getInt("commentId", 0)
        val url = "http://192.168.1.123:8080/neworld/android/201?userId=$userId&commentId=$commentId"
        web.loadUrl(url)

        val map = hashMapOf<CharSequence, CharSequence>()
        map["userId"] = userId
        map["commentId"] = commentId.toString()
        map["createDate"] = ""

        NetBuild.response(this@AnswerDetail::onResponse, ToastUtil::showToast,
                202, ResponseBean.AnswersDetailBody::class.java, map)
    }

    private fun onResponse(t: ResponseBean.AnswersDetailBody) {
        if (t.status == 1) {
            showToast("{数据错误, 请到用户反馈处反馈此问题: 错误代码[AD.KT]}")
            return
        }
        mAdapter.addDataAndClear(t.menuList)
        mAdapter.notifyDataSetChanged()
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
                    loading.visibility = View.GONE
                }
            }
        }
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                logE("url = $url")
                view?.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                loading.visibility = View.VISIBLE
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                showToast("error : $errorCode")
            }
        }
    }
}