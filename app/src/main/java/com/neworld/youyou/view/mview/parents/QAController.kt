package com.neworld.youyou.view.mview.parents

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v4.app.SharedElementCallback
import android.support.v4.content.ContextCompat
import android.view.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.base.Fragment
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.mview.common.ImgViewer
import kotlinx.android.synthetic.main.activity_parent_qa.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.HashMap

/**
 * @author by user on 2018/1/4.
 */
class QAController : Activity() {

    private val questionsAndAnswers by lazy {
        QuestionsAndAnswers()
    }
    private val answersDetail by lazy {
        AnswerDetail()
    }
    private var toDetail = false
    private lateinit var eTaskId: String // 第三层
    private lateinit var eCommentId: String // 第二层
    private lateinit var eDate: String
    private lateinit var cId: String
    private lateinit var eTitle: String
    private var mInDynamic = false
    private var mExitPos = -1

    private var userId by preference("userId", "")

    override fun getContentLayoutId() = R.layout.activity_parent_qa

    override fun initWindows() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.run {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this@QAController, R.color.primaryDarkGray)
        }
    }

    override fun initArgs(bundle: Bundle?): Boolean {
        val obtain = MyEventBus.INSTANCE.obtain(ImgViewer.EVENT_IMG, {
            mExitPos = it as Int

            null
        })

        registerStation(obtain)

        if (bundle != null) {
            toDetail = bundle.getBoolean("toDetail", false)
            if (toDetail) {
                eTaskId = bundle.getString("taskId")
                eCommentId = bundle.getString("commentId")
                eDate = bundle.getString("date")
                cId = bundle.getString("cId")
                mInDynamic = bundle.getBoolean("inDynamic")
                eTitle = bundle.getString("title", "优优家长")
            }
        }
        return super.initArgs(bundle)
    }

    @SuppressLint("SetTextI18n")
    override fun initWidget() {
        questionsAndAnswers.setObserver {
            startAnswersDetail()
        }

        if (toDetail) {
            startAnswersDetail()
        } else {
            supportFragmentManager.beginTransaction().also {
                it.replace(questionsAndAnswers, "fragment1")
                it.addToBackStack("fragment1")
            }.commit()
        }

        _toolbar.title = ""
        setSupportActionBar(_toolbar)

        _close.setOnClickListener { onKeyDown(KeyEvent.KEYCODE_BACK, null) }

        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                if (mExitPos != -1) {
                    names?.clear()
                    sharedElements?.clear()

                    val name = getString(R.string.transitionName_pic)
                    val view = questionsAndAnswers.getTempView(mExitPos)

                    names?.add(name)
                    sharedElements?.put(name, view)

                    mExitPos = -1
                }
//                logE("sharedElements = null? ${sharedElements == null}")
//                sharedElements?.put(getString(R.string.transitionName_pic), tempView[viewerPos]) // TODO : 角标对应的view。
            }
        })
    }

    private fun startAnswersDetail() {
        if (questionsAndAnswers.arguments == null && !toDetail) {
            showToast("出现未知错误！！请反馈此问题！")
            return
        }

        val args = if (toDetail) {
            val bundle = Bundle()
            bundle.putString("cId", cId)
            bundle.putBoolean("inDynamic", mInDynamic)
            bundle.putString("taskId", eTaskId)
            bundle.putString("title", eTitle)
            bundle
        } else {
            questionsAndAnswers.arguments!!
        }

        val taskId: String
        val minDate: String
        val array1 = args.getStringArray("nextArray") ?: arrayOf()

        if (toDetail) {
            taskId = eTaskId
            minDate = eDate
        } else {
            taskId = args.getString("taskId")
            minDate = args.getString("minCreateDate") ?: ""
        }

        val map = hashMapOf<String, String>()
        map["userId"] = userId
        map["taskId"] = taskId
        map["createDate"] = minDate

        toDetail = false

        doAsync {
            val response = NetBuild.getResponse(map, 208)
            val menu = Gson().fromJson<CommentIdCollection>(response,
                    object : TypeToken<CommentIdCollection>() {}.type).menuList

            if (menu != null && menu.isNotEmpty()) {
                menu.flatMap { arrayListOf(it["commentId"]!!) }.toTypedArray().let { array2 ->
                    val strLen1 = array1.size
                    val strLen2 = array2.size
                    val newLength = strLen1 + strLen2
                    val nextArray = Arrays.copyOf(array1, newLength)
                    System.arraycopy(array2, 0, nextArray, strLen1, strLen2)
                    args.putStringArray("nextArray", nextArray)
                }
            }

            uiThread {
                val begin = supportFragmentManager.beginTransaction()
                begin.replace(answersDetail.also { it.arguments = args }, "fragment2")
                begin.addToBackStack("fragment2")

                begin.commit()

                answersDetail.loadingListener(onStart = {
                    _progress.visibility = View.VISIBLE
                }, onLoading = {
                    _progress.newProgress = it.toFloat()
                    if (it > 99) {
                        _progress.visibility = View.INVISIBLE
                    }
                }, onStop = {
                    _progress.visibility = View.INVISIBLE
                })
            }
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
        if (questionsAndAnswers.isAdded)
            it.hide(questionsAndAnswers)

        if (answersDetail.isAdded)
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
                    "fragment1" -> {
                        answersDetail.clearWebCache()
                        if (_progress.visibility == View.VISIBLE) {
                            _progress.newProgress = 0F
                            _progress.visibility = View.INVISIBLE
                        }
                        bt.replace(questionsAndAnswers, "fragment1")
                    }
                    "fragment2" -> {
                        bt.replace(answersDetail, "fragment2")
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
            val menuList: MutableList<HashMap<String, String>>?
    )

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        if (questionsAndAnswers.isVisible) {
            questionsAndAnswers.resize()
        } else {
            answersDetail.resize()
        }
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.run {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = ContextCompat.getColor(this@QAController, R.color.colorPrimaryDark)
        }

        unregisterStation()

        super.onDestroy()
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
//				ShareAction(this@QAController)
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