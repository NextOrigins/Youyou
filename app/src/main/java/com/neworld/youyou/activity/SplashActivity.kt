package com.neworld.youyou.activity

import android.content.Intent
import android.os.Handler
import com.neworld.youyou.MainActivity
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity

/**
 * @author by user on 2018/3/5.
 */
class SplashActivity : Activity() {

    override fun getContentLayoutId() = R.layout.activity_splash

    override fun initWidget() {
        Handler().postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, 2000)
    }
}