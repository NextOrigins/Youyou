package com.neworld.youyou.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.neworld.youyou.MainActivity
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.utils.preference
import com.neworld.youyou.utils.showToast
import kotlinx.android.synthetic.main.item_parent.*

/**
 * @author by user on 2018/3/5.
 */
class SplashActivity : Activity() {

    private val userId by preference("userId", "")

    override fun getContentLayoutId() = R.layout.activity_splash

    override fun initWidget() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE),
                    2)
        } else {
            toHome()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED ||
                grantResults[1] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "无读写权限可能会导致部分功能不可用或报错，请尽快到软件管理处添加权限。",
                    Toast.LENGTH_LONG).show()
            Handler().postDelayed({
                finish()
            }, 2000)
        } else {
            toHome()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun toHome() {
        Handler().postDelayed({
            if (userId.isEmpty()) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            finish()
        }, 2000)
    }
}