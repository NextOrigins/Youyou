package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.common.AdapterK
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.utils.*

/**
 * @author by user on 2018/1/2.
 */
class ParentsQ : Fragment() {
	
	private var mRecycle by notNullSingleValue<RecyclerView>()
	private var userId by preference("userId", "")
	private var token by preference("token", "")
	
	private var mAdapter: AdapterK<ResponseBean.QADetail> by notNullSingleValue()
	
	private val map = hashMapOf<CharSequence, CharSequence>()
	private val list = arrayListOf<ResponseBean.QADetail>()
	
	private val imgWidth by lazy {
		val point = Point()
		activity.windowManager.defaultDisplay.getSize(point)
		(point.x - resources.getDimension(R.dimen.dp17)) / 3
	}
	
	override fun getContentLayoutId() = R.layout.fragment_parents_q
	
	override fun initWidget(root: View) {
		mRecycle = root.findViewById(R.id._recycler)
		mRecycle.layoutManager = LinearLayoutManager(context,
				LinearLayoutManager.VERTICAL, false)
		mRecycle.adapter = AdapterK(this::itemBind,
				R.layout.item_qa_1, list).also { mAdapter = it }
	}
	
	override fun initData() {
		map.run {
			put("userId", userId)
			put("token", token)
			put("createDate", "")
			put("endDate", "")
			NetBuild.response(this@ParentsQ::downSuccess,
					ToastUtil::showToast, 199, ResponseBean.QABody::class.java, this)
		}
	}
	
	private fun downSuccess(t: ResponseBean.QABody) {
		list.clear()
		list.addAll(t.menuList)
		mAdapter.notifyDataSetChanged()
	}
	
	@SuppressLint("SetTextI18n")
	private fun itemBind(holder: Adapter.Holder,
	                     mutableList: MutableList<ResponseBean.QADetail>, position: Int) {
		val parent = holder.find<ConstraintLayout>(R.id.item_parent)
		val title = holder.find<TextView>(R.id.item_title)
		val reply = holder.find<TextView>(R.id.item_reply)
		val img1 = holder.find<ImageView>(R.id.item_img1).also { it.layoutParams = it.layoutParams.also { it.width = imgWidth.toInt() } }
		val img2 = holder.find<ImageView>(R.id.item_img2).also { it.layoutParams = it.layoutParams.also { it.width = imgWidth.toInt() } }
		val img3 = holder.find<ImageView>(R.id.item_img3).also { it.layoutParams = it.layoutParams.also { it.width = imgWidth.toInt() } }
		
		val data = mutableList[position]
		
		parent.setOnClickListener {
			startActivity(Intent(context, ParentsQA::class.java)
					.putExtra("taskId", data.from_uid)
					.putExtra("date", data.createDate))
		}
		
		title.text = data.title
		reply.text = "${data.comment_count}回答"
		
		img1.visibility = if (data.imgs.isEmpty()) View.GONE else View.VISIBLE
		
		if (data.imgs.isNotEmpty()) {
			val split = data.imgs.split("\\|".toRegex())
			when (split.size) {
				1 -> {
					Glide.with(img1).load(split[0]).into(img1)
					img2.visibility = View.GONE
				}
				2 -> {
					Glide.with(img1).load(split[0]).into(img1)
					Glide.with(img2).load(split[1]).into(img2)
					img2.visibility = View.VISIBLE
					img3.visibility = View.GONE
				}
				3 -> {
					Glide.with(img1).load(split[0]).into(img1)
					Glide.with(img2).load(split[1]).into(img2)
					Glide.with(img3).load(split[2]).into(img3)
					img2.visibility = View.VISIBLE
					img3.visibility = View.VISIBLE
				}
			}
		} else {
			img1.visibility = View.GONE
		}
	}
}