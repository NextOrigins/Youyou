package com.neworld.youyou.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import com.neworld.youyou.R
import com.neworld.youyou.bean.ReturnStatus
import com.neworld.youyou.manager.MyApplication
import com.neworld.youyou.showSnackBar
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.activity_me_settings.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SettingActivity : AppCompatActivity(), View.OnClickListener {
	
	private var userId by preference("userId", "")
	private var application: MyApplication? = null
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_me_settings)
		if (application == null) {
			application = getApplication() as MyApplication
		}
		initView()
	}
	
	private fun initView() {
		iv_close.setOnClickListener(this)
		bt_quit.setOnClickListener(this)
		
		black_list.setOnClickListener(this)
		address_manager.setOnClickListener(this)
	}
	
	override fun onClick(v: View) {
		when (v.id) {
			R.id.iv_close -> finish()
			R.id.black_list -> startActivity(Intent(this@SettingActivity, BlackNameActivity::class.java))
			R.id.bt_quit -> displayDialog(this, "确定退出登录吗", this::quit)
			R.id.address_manager -> startActivity(Intent(this, AddressActivity::class.java).putExtra("fromSetting", true))
			R.id.message_control -> {
			// 消息界面: 195 userId
			}
		}
	}
	
	private fun quit() {
		hashMapOf<CharSequence, CharSequence>().run {
			put("userId", userId)
			NetBuild.response(this@SettingActivity::success,
					ToastUtil::showToast, 152, ReturnStatus::class.java, this)
		}
	}
	
	private fun success(t: ReturnStatus) {
		if (t.status == 0) {
			userId = ""
			startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
			application?.removeALLActivity_()
			finish()
		} else {
			showSnackBar(_parent, "状态错误, 请截图到用户反馈处反馈此问题, 我们会尽快处理, 谢谢.", 2000)
		}
	}
	
	override fun onStart() {
		super.onStart()
		doAsync {
			NetBuild.getResponse("\"userId\":\"$userId\"", 194)?.run {
				take(length - 5).contains("1").let { b ->
					uiThread { _hint.visibility = if (b) View.VISIBLE else View.GONE }
				}
			}
		}
	}
}
