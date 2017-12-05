package com.neworld.youyou.add.feed

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.activity.PhotoDetailActivity
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.bean.ReportBean
import com.neworld.youyou.dialog.DialogUtils
import com.neworld.youyou.utils.NetBuild
import com.neworld.youyou.utils.Sputil
import com.neworld.youyou.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_feed_back.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

import java.io.Serializable
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap

/**
 * @author by user on 2017/11/6.
 */

class FeedbackActivity : Activity(), View.OnClickListener {

    private var mAdapter: Adapter<ReportBean.ResultsBean>? = null
    internal var list: MutableList<ReportBean.ResultsBean> = ArrayList()

    /**
     * 回调onBindViewHolder
     */
    private val obs = object : Adapter.AdapterObs<ReportBean.ResultsBean> {
        override fun onBind(holder: Adapter.Holder, bean: MutableList<ReportBean.ResultsBean>, position: Int) {
            val content = holder.find<TextView>(R.id.back_content) // 反馈内容
            val date = holder.find<TextView>(R.id.back_date) // 反馈日期
            val state = holder.find<TextView>(R.id.back_state) // 状态
            val iv = holder.find<ImageView>(R.id.back_img) // 图片
            val line = holder.find<View>(R.id.back_line) // 底线
            val admin = holder.find<TextView>(R.id.back_admin) // 管理员回复
            val data = bean[position]
            content.text = data.content
            date.text = data.createDate

            if (data.status == 1) {
                state.text = "待处理"
                state.setBackgroundResource(R.drawable.feed_back_bg)
                state.setTextColor(Color.parseColor("#AA0000"))
                line.visibility = View.GONE
                admin.visibility = View.GONE
            } else {
                state.text = "已处理"
                state.setBackgroundResource(R.drawable.feed_back_adm_bg)
                state.setTextColor(Color.parseColor("#62CC82"))
                line.visibility = View.VISIBLE
                admin.visibility = View.VISIBLE
                val c = "管理员回复:" + data.imgs[0].content.trim { it <= ' ' }
                val sps = SpannableString(c)
                sps.setSpan(ForegroundColorSpan(Color.parseColor("#CCCCCC")), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                sps.setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 6, c.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                admin.text = sps
            }

            if (data.sumCount > 0) {
                iv.visibility = View.VISIBLE
                val split = data.Iconimgs.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                Glide.with(holder.itemView.context)
                        .load(split[data.sumCount - 1]).into(iv)
            } else {
                iv.visibility = View.GONE
            }

            iv.setOnClickListener {
                val intentImage = Intent()
                intentImage.setClass(applicationContext, PhotoDetailActivity::class.java)
                val split = data.Iconimgs.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                val bundleSimple = Bundle()
                bundleSimple.putSerializable("imageList", Arrays.asList(*split) as Serializable)
                bundleSimple.putString("stringUrl", split[data.sumCount - 1])
                bundleSimple.putInt("currentPosition", data.sumCount - 1)
                bundleSimple.putString("FromActivity", "mainActivity")
                intentImage.putExtras(bundleSimple)
                startActivity(intentImage)
            }

            // 删除条目，暂不需简化
            holder.find<View>(R.id.back_delete).setOnClickListener {
                DialogUtils.showDialog(this@FeedbackActivity, "确定删除吗", "确定", "取消") { dialog, _ ->
                    val userId = Sputil.getString(baseContext, "userId", "")
                    doAsync {
                        val map = HashMap<String, String>()
                        map.put("userId", userId)
                        map.put("bugId", data.bugId.toString())
                        val response = NetBuild.getResponse(map, 175)
                        if (response.contains("{\"status\":0}")) {
                            uiThread {
                                mAdapter?.remove(position)
                            }
                        } else {
                            uiThread { ToastUtil.showToast("删除失败, 请稍后重试") }
                        }
                    }

                    dialog.dismiss()
                }
            }
        }

        override fun layoutId(): Int {
            return R.layout.item_feed_back
        }
    }

    override fun getContentLayoutId(): Int {
        return R.layout.activity_feed_back
    }

    override fun initWidget() {
        feed_back_recycler.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        feed_back_recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mAdapter = Adapter<ReportBean.ResultsBean>(obs, list)
        feed_back_recycler.adapter = mAdapter

        feed_close.setOnClickListener { finish() }
        feed_posted.setOnClickListener(this)
//        feed_posted.setOnClickListener { startActivity(Intent(this@FeedbackActivity, DetailViewImpl::class.java)) }
    }

    override fun initData() {
        val userId = Sputil.getString(this, "userId", "")
        val map = HashMap<String, String>()
        map.put("userId", userId)
        NetBuild.response(object : NetBuild.ResponseObs<ReportBean> {
            override fun onSuccess(t: ReportBean) {
                if (t.status == 0) {
                    val results = t.results
                    if (results != null && !results.isEmpty()) {
                        list.clear()
                        list.addAll(results)
                        mAdapter?.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailed(error: String) {
                ToastUtil.showToast(error)
            }

        }, 172, ReportBean::class.java, map)
    }

    override fun onStart() {
        super.onStart()
        initData()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.feed_posted // 防止帕金森点击
            -> {
                v.setOnClickListener(null)
                startActivity(Intent(this, PostedActivity::class.java))
                Handler().postDelayed({ v.setOnClickListener(this) }, 1500)
            }
        }
    }
}
