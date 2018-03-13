package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.support.v4.app.FragmentTransaction
import android.view.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.utils.NetBuild
import com.neworld.youyou.utils.logE
import com.neworld.youyou.utils.preference
import kotlinx.android.synthetic.main.activity_parent_qa.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * @author by user on 2018/1/4.
 */
class QAParent : Activity() {

    private var questionsAndAnswers: QuestionsAndAnswers? = null
    private var answersDetail: AnswerDetail? = null

    private var userId by preference("userId", "")

    override fun getContentLayoutId() = R.layout.activity_parent_qa

    /*@SuppressLint("PrivateApi")
    override fun initWindows() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)
        }
    }*/

    @SuppressLint("SetTextI18n")
    override fun initWidget() {
        supportFragmentManager.beginTransaction().also {
            it.replace(questionsAndAnswers ?: QuestionsAndAnswers()
                    .also { questionsAndAnswers = it }, "fragment1")
            it.addToBackStack("fragment1")
        }.commit()

        questionsAndAnswers?.setObserver {
            startAnswersDetail()
        }

        _toolbar.title = ""
        setSupportActionBar(_toolbar)

        _close.setOnClickListener { onKeyDown(KeyEvent.KEYCODE_BACK, null) }
    }

    private fun startAnswersDetail() {
        questionsAndAnswers?.arguments?.let { args ->
            val taskId = args.getString("taskId")
            val minDate = args.getString("minCreateDate")
            val array1 = args.getStringArray("nextArray")

            val map = hashMapOf<String, String>()
            map["userId"] = userId
            map["taskId"] = taskId
            map["createDate"] = minDate ?: ""
            doAsync {
                val response = NetBuild.getResponse(map, 208)
                val menu = Gson().fromJson<CommentIdCollection>(response,
                        object : TypeToken<CommentIdCollection>() {}.type).menuList

                if (menu.isNotEmpty()) {
                    menu.flatMap { arrayListOf(it["commentId"]!!) }.toTypedArray().let { array2 ->
                        val strLen1 = array1?.size ?: 0
                        val strLen2 = array2.size
                        val newLength = strLen1 + strLen2
                        val nextArray = Arrays.copyOf(array1, newLength)
                        System.arraycopy(array2, 0, nextArray, strLen1, strLen2)
                        args.putStringArray("nextArray", nextArray)
                    }
                }

                uiThread {
                    val begin = supportFragmentManager.beginTransaction()
                    begin.replace(answersDetail?.also { it.arguments = args }
                            ?: AnswerDetail().also {
                                it.arguments = args
                                answersDetail = it
                            }, "fragment2")
                    begin.addToBackStack("fragment2")

                    begin.commit()
                }
            }

            Unit
        }
    }

    private fun FragmentTransaction.replace(fm: Fragment, tag: String) {
        if (fm.arguments == null) fm.arguments = intent.extras

        hideAll()

        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        when {
            fm.isAdded -> show(fm)
            else -> add(R.id._content, fm, tag)
        }
    }

    private fun hideAll() = supportFragmentManager.beginTransaction().also {
        if (questionsAndAnswers != null && questionsAndAnswers!!.isAdded)
            it.hide(questionsAndAnswers)

        if (answersDetail != null && answersDetail!!.isAdded)
            it.hide(answersDetail)
    }.commit()

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val fm = supportFragmentManager
            val entryCount = fm.backStackEntryCount
            if (entryCount > 1) {
                fm.popBackStackImmediate()
                val tag = fm.getBackStackEntryAt(fm.backStackEntryCount - 1).name
                val bt = fm.beginTransaction()
                when (tag) {
                    "fragment1" -> bt.also {
                        it.replace(questionsAndAnswers
                                ?: QuestionsAndAnswers().also { questionsAndAnswers = it }, "fragment1")
//                        questionsAndAnswers?.refreshData()
                    }
                    "fragment2" -> bt.also {
                        it.replace(answersDetail
                                ?: AnswerDetail().also { answersDetail = it }, "fragment2")
                    }
                }
                bt.commit()
            } else {
                finish()
            }
        }

        return true
    }

    private data class CommentIdCollection(
            val menuList: MutableList<HashMap<String, String>>
    )

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (questionsAndAnswers == null) return

        if (questionsAndAnswers!!.isVisible) {
            questionsAndAnswers?.resize()
        } else {
            answersDetail
        }


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
//				ShareAction(this@QAParent)
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