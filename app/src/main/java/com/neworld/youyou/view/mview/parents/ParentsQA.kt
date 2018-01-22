package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import kotlinx.android.synthetic.main.activity_parent_qa.*

/**
 * @author by user on 2018/1/4.
 */
class ParentsQA : Activity() {
	
	private var questionsAndAnswers: QuestionsAndAnswers? = null
	
	override fun getContentLayoutId() = R.layout.activity_parent_qa
	
	override fun initWindows() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
			window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)
		}
	}
	
	@SuppressLint("SetTextI18n")
	override fun initWidget() {
		
		supportFragmentManager.beginTransaction().also {
			when {
				questionsAndAnswers == null -> QuestionsAndAnswers()
						.apply {
							it.add(R.id._content, this)
							arguments = intent.extras
						}
						.let { questionsAndAnswers = it }
				
				questionsAndAnswers!!.isAdded -> it.show(questionsAndAnswers)
				
				else -> it.add(R.id._content, questionsAndAnswers)
			}
		}.commit()
		
		_toolbar.title = ""
		setSupportActionBar(_toolbar)
		
		_close.setOnClickListener { finish() }
	}
	
//	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//		menuInflater.inflate(R.menu.menu_item, menu)
//		val item = menu?.findItem(R.id.menu_item1)
//		item?.icon = item?.icon?.also {
//			val wrap = DrawableCompat.wrap(it)
//			DrawableCompat.setTint(wrap, ContextCompat.getColor(baseContext, R.color.black))
//		} // 不确定是否管用 (setTint)
//		return true
//	}
	
//	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//		if (item != null && item.itemId == R.id.menu_item1) {
//			logE("条目点击")
//			val url = "http://www.uujz.me:8082/neworld/user/143?taskId=$tskId&userId=$userId"
//			UMWeb(url).run {
//				title = headTitle.text.toString()
//				description = headContent.text.toString()
//				ShareAction(this@ParentsQA)
//						.withMedia(this)
//						.setDisplayList(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ,
//								SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
//						.setCallback(object : UMShareListener {
//							override fun onResult(p0: SHARE_MEDIA?) {
//								logE("分享成功 ?")
//							}
//
//							override fun onCancel(p0: SHARE_MEDIA?) {
//								logE("取消分享")
//							}
//
//							override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
//								logE("分享失败")
//							}
//
//							override fun onStart(p0: SHARE_MEDIA?) {
//								logE("开始分享")
//							}
//						})
//			}
//			return true
//		}
//		return false
//	}
}