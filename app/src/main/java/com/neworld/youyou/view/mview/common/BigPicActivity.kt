package com.neworld.youyou.view.mview.common

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.neworld.youyou.R
import kotlinx.android.synthetic.main.activity_big_pig.*

/**
 * @author by user on 2017/11/30.
 */
class BigPicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.run {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this@BigPicActivity, R.color.primaryDarkGray)
        }
        super.onCreate(savedInstanceState)
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_big_pig)

        val url = intent.getStringExtra(EXTRA_URL)
        val opt = RequestOptions()
                .placeholder(R.drawable.deftimg)
                .error(R.drawable.deftimg)
        Glide.with(this).load(url).apply(opt).into(big_pic)
        big_pic.setOnClickListener { onBackPressed() }
    }

    companion object {
        const val EXTRA_URL = "extra_url"
        fun launch(activity: AppCompatActivity, srcView: View, url: String) {
            val intent = Intent(activity, BigPicActivity::class.java)
            intent.putExtra(EXTRA_URL, url)
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, srcView, activity.getString(R.string.transitionName_pic))
            ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle())
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        } else {
            finish()
        }
    }
}