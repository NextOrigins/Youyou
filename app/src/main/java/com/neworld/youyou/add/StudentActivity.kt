package com.neworld.youyou.add

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView

import com.neworld.youyou.R
import com.neworld.youyou.activity.AddChildActivity
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.bean.ChildDetailBean
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.activity_student.*

import java.util.ArrayList

/**
 * @author by user on 2017/11/9.
 */

class StudentActivity : AppCompatActivity() {

    private val list = ArrayList<ChildDetailBean.ResultsBean>()
    private var mAdapter: Adapter<ChildDetailBean.ResultsBean>? = null

    private val obs = object : Adapter.AdapterObs<ChildDetailBean.ResultsBean> {
        override fun onBind(holder: Adapter.Holder, bean: List<ChildDetailBean.ResultsBean>, position: Int) {
            val data = bean[position]
            holder.find<View>(R.id.item_student_edit).setOnClickListener {
                // 编辑信息
                val intent = Intent(this@StudentActivity, AddChildActivity::class.java)
                intent.putExtra("edit", true)
                intent.putExtra("id", data.id)
                startActivity(intent)
            }
            holder.itemView.setOnClickListener {
                // return名字到付款页面
                val intent = intent
                intent.putExtra("studentName", data.name)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            val tvName = holder.find<TextView>(R.id.item_student_name)

            tvName.text = data.name
        }

        override fun layoutId(): Int {
            return R.layout.item_student
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
        view()
        data()
    }

    private fun view() {
        student_close.setOnClickListener { finish() }
        student_add.setOnClickListener {
            // 点击添加Child
            val intent = Intent(this, AddChildActivity::class.java)
            intent.putExtra("pay", true)
            startActivity(intent)
        }
        student_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = Adapter<ChildDetailBean.ResultsBean>(obs, list)
        student_recycler.adapter = mAdapter
        student_recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun data() {
        val userId = SPUtil.getString(this, "userId", "")
        val map = HashMap<String, String>()
        map.put("userId", userId)
        NetBuild.response(object : NetBuild.ResponseObs<ChildDetailBean> {
            override fun onSuccess(t: ChildDetailBean) {
                list.clear()
                list.addAll(t.results)
                mAdapter?.notifyDataSetChanged()
            }

            override fun onFailed(error: String) {
                ToastUtil.showToast(error)
            }

        }, 141, ChildDetailBean::class.java, map)
    }

    override fun onStart() {
        super.onStart()
        data()
    }

    override fun onDestroy() {
        super.onDestroy()
        list.clear()
    }
}
