package com.neworld.youyou.activity

import android.content.Intent
import android.os.Bundle
import android.view.View

import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.bean.ReturnStatus
import com.neworld.youyou.manager.MyApplication
import com.neworld.youyou.showSnackBar
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.activity_me_settings.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SettingActivity : Activity(), View.OnClickListener {
	
	private var userId by preference("userId", "")
	private var mApplication: MyApplication? = null

    override fun getContentLayoutId() = R.layout.activity_me_settings

    override fun initArgs(bundle: Bundle?): Boolean {
        if (mApplication == null) mApplication = application as MyApplication
        return super.initArgs(bundle)
    }

    override fun initWidget() {
        iv_close.setOnClickListener(this)
        bt_quit.setOnClickListener(this)

        black_list.setOnClickListener(this)
        address_manager.setOnClickListener(this)

//        message_control.setOnClickListener(this)
	    message_control.visibility = View.GONE
    }

	override fun onClick(v: View) {
		when (v.id) {
			R.id.iv_close -> finish()
			R.id.black_list -> startActivity(Intent(this@SettingActivity, BlackNameActivity::class.java))
			R.id.bt_quit -> displayDialog(this, "确定退出登录吗", this@SettingActivity::quit)
			R.id.address_manager -> startActivity(Intent(this, AddressActivity::class.java).putExtra("fromSetting", true))
			R.id.message_control -> {
			// 消息界面: 195 userId (暂时没做)
                showToast("open the message center")
			}
		}
	}

	private fun quit() {
        response(::success, "152", "\"userId\":\"$userId\"")
	}
	
	private fun success(t: ReturnStatus) {
		if (t.status == 0) {
			userId = ""
			startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
			mApplication?.removeALLActivity_()
			finish()
		} else {
			showSnackBar(_parent, "网络出错, 退出失败(如果持续出现此问题请到用户反馈处反馈)", 2000)
		}
	}

	override fun onStart() {
		super.onStart()
        doAsync {
            val response = NetBuild.getResponse("\"userId\":\"$userId\"", 194) ?: "null"
            uiThread { _hint.visibility = if ("1" in response) View.VISIBLE else View.GONE }
        }
	}
}
