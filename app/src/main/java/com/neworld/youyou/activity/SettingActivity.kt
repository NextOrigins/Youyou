package com.neworld.youyou.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.view.View

import com.neworld.youyou.R
import com.neworld.youyou.bean.ReturnStatus
import com.neworld.youyou.manager.MyApplication
import com.neworld.youyou.manager.NetManager
import com.neworld.youyou.utils.GsonUtil
import com.neworld.youyou.utils.Sputil
import com.neworld.youyou.utils.displayDialog
import com.neworld.youyou.utils.preference
import kotlinx.android.synthetic.main.activity_me_settings.*

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
            }
        }
    }

    private fun quit() {
        Thread(Runnable {
            val base64 = Base64.encodeToString("{\"userId\":\"$userId\"}".toByteArray(), Base64.DEFAULT)
            val replace = base64.replace("\n", "")
            val content = NetManager.getInstance().getContent(replace, "152")
            if (content != null && content.length > 0) {
                val returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus::class.java)
                if (returnStatus != null && returnStatus.status == 0) {
                    Sputil.saveString(this@SettingActivity, "userId", "")
                    startActivity(Intent(this@SettingActivity, LoginActivity::class.java))
                    application!!.removeALLActivity_()
                    this@SettingActivity.finish()
                }
            }
        }).start()
    }
}
