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
import com.neworld.youyou.view.mview.parents.Topics
import kotlinx.android.synthetic.main.activity_me_settings.*

class SettingActivity : Activity(), View.OnClickListener {
	
	private var userId by preference("userId", "")
	private var mApplication: MyApplication? = null
	private var homeCache  = ""
	private var cacheArray: Array<String>? = null

    override fun getContentLayoutId() = R.layout.activity_me_settings

    override fun initArgs(bundle: Bundle?): Boolean {
        if (mApplication == null) mApplication = application as MyApplication
		cacheArray = bundle?.getStringArray("typeArray")
		if (cacheArray != null && cacheArray!!.isNotEmpty()) {
			val edit = getPefStorage()
			cacheArray!!.forEach {
				val key = "${Topics.CACHE_KEY}$it"
				homeCache += edit.getString(key, "")
			}
		}
        homeCache = MyEventBus.INSTANCE.postEvent(Topics.CACHE_LENGTH, "")
                .fold("") { total, next -> total + (next ?: "") }
        return super.initArgs(bundle)
    }

	override fun initWidget() {
        iv_close.setOnClickListener(this)
        bt_quit.setOnClickListener(this)

//        black_list.setOnClickListener(this)
//        address_manager.setOnClickListener(this)

//        message_control.setOnClickListener(this)

		cache_size.text = convertSize()
		_clear.setOnClickListener {
			if (homeCache.isEmpty()) {
				showToast("已经清空啦！")
				return@setOnClickListener
			}
			try {
                val array = MyEventBus.INSTANCE.postEvent(Topics.CLEAR_CACHE)
                if ("1" !in array) {
                    homeCache = MyEventBus.INSTANCE.postEvent(Topics.CACHE_LENGTH, "")
                            .fold("") { total, next -> total + (next ?: "") }
                    cache_size.text = convertSize()
                    showToast("出现未知错误…")
                } else {
                    homeCache = ""
                    cache_size.text = convertSize()
                    showToast("清理成功！")
                }
			} catch (e: Exception) {
				showToast("清理失败！如多次出现请到用户反馈处反馈此问题 : $e")
			}
		}
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

	/*override fun onStart() {
		super.onStart()
        *//*doAsync { // 消息提示
            val response = NetBuild.getResponse("\"userId\":\"$userId\"", 194) ?: "null"
            uiThread { _hint.visibility = if ("1" in response) View.VISIBLE else View.GONE }
        }*//*
	}*/

	private fun convertSize(): CharSequence {
		val p0 = (homeCache.toByteArray().size.toFloat() / 1024f / 1024f).toString()
		return if (p0.length > 4) "${p0.subSequence(0, 4)} Mb" else "$p0 Mb"
	}
}
