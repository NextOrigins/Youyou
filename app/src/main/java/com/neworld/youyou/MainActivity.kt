package com.neworld.youyou


import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle

import android.support.design.widget.Snackbar

import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup

import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

import com.neworld.youyou.fragment.MyFragment
import com.neworld.youyou.manager.MyApplication
import com.neworld.youyou.utils.*
import com.neworld.youyou.view.ParentView
import com.neworld.youyou.view.mview.books.BooksViewImpl
import com.neworld.youyou.view.mview.comment.HProgress
import com.neworld.youyou.view.mview.hot.HotFragment
import com.neworld.youyou.view.mview.parents.Topics
import com.umeng.socialize.UMShareAPI
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

fun showSnackBar(viewGroup: ViewGroup, text: String, duration: Int = 1000) =
        Snackbar.make(viewGroup, text, duration).apply {
            view.setBackgroundColor(ContextCompat.getColor(viewGroup.context, R.color.colorPrimary))
            show()
        }

class MainActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener, ParentView {

    private var frameLayout: FrameLayout? = null

    private var radioGroup: RadioGroup? = null
    private lateinit var rbParent: RadioButton
    //    private var rbSubject: RadioButton? = null
    private var rbHot: RadioButton? = null
    private var rbMy: RadioButton? = null
    //    private var parentFragment: ParentPageFragment? = null
    // -----
    // 暂时关掉
//	private var topics: Questions by Delegates.notNull()
    // -----
    private val topics by lazy {
        Topics()
    }
    //    private var subjectFragment: SubjectFragment? = null
    //    private var hotFragment: HotFragment? = null
    private val hotFragment by lazy {
        HotFragment()
    }
    private val myFragment by lazy {
        MyFragment()
    }
    private var fragmentManager: FragmentManager by Delegates.notNull()
    private var isSuccess = false
    private var homeEnable = true
    private var hotNewsEnable = false
    var mainApplication: MyApplication? = null
        private set
    private var netObs: NetworkObs? = null

<<<<<<< HEAD
    //    private var booksFragment: EBooks? = null
=======
    private var userId: String by preference("userId", "")
    private var token: String by preference("token", "");

    val windowData: WindowManager
        get() = windowManager

>>>>>>> parent of 8d52dad... 17_12_19
    private var booksFragment: BooksViewImpl? = null

    private var mBackPressedTime by Delegates.observable(0L) { _, old, new ->
        if (new - old > 1000) {
            showSnackBar(activity_main, getString(R.string.exit_message))
        }
        if (new - old in 1..1000) {
            finish()
        }
    }

    // update view
    private var mUpTitle by Delegates.notNull<TextView>()
    private var mUpContent by Delegates.notNull<TextView>()
    private var mUpNow by Delegates.notNull<TextView>()
    private var mUpLater by Delegates.notNull<TextView>()
    private var mUpProgress by Delegates.notNull<HProgress>()
    private var mUpProText by Delegates.notNull<TextView>()
    private var mLine by Delegates.notNull<View>()

    private val mDialog by lazy {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setView(upDateView)
        dialog.window.setBackgroundDrawableResource(R.drawable.update_bg)

        dialog
    }

    private val upDateView by lazy {
        val inflate = layoutInflater.inflate(R.layout.update_view,
                activity_main, false)

        mUpTitle = inflate.findViewById(R.id._title)
        mUpContent = inflate.findViewById(R.id._content)
        mUpNow = inflate.findViewById(R.id._now)
        mUpLater = inflate.findViewById(R.id._later)
        mUpProgress = inflate.findViewById(R.id._progress)
        mUpProText = inflate.findViewById(R.id._progress_text)
        mLine = inflate.findViewById(R.id._line1)

        mUpProgress.setColor(Color.parseColor("#E78DAC"))
        inflate
    }

    private var selectedId = R.id.rb_parent

    // ------------------------------

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置当前布局
        setContentView(R.layout.activity_main)
//        parentFragment = ParentPageFragment() // 原 家长圈

//        subjectFragment = SubjectFragment()

        booksFragment = BooksViewImpl()  // 出售图书
//        booksFragment = EBooks() // 电子书

        if (mainApplication == null) {
            mainApplication = application as MyApplication
        }
        mainApplication!!.addActivity_(this)
        initView()
        initData()
        initBroadcast()

        // 版本更新
        checkUpDate()
    }

    private fun checkUpDate() {
        UpDate(onProgressUpDate = {
            // 更新进度
            // 设置禁止关闭、防止误触后看不到进度条。
            mDialog.setCancelable(false)
            mDialog.setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }

            val str = "$it%"
            mUpProText.text = str

            mUpProgress.newProgress = it.toFloat()

            if (it == 100) {
                mDialog.dismiss()
            }
        }, fUpdate = { start, msg ->
            // 强制更新
            // 返回键拦截
            mDialog.setCancelable(false)
            mDialog.setOnKeyListener { _, keyCode, _ ->
                onKeyDown(keyCode, null)
                keyCode == KeyEvent.KEYCODE_BACK
            }

            mDialog.show()
            mUpContent.text = msg ?: "不更新可能会出现不可预测的问题的噢。"

            mUpNow.setOnClickListener {
                mUpContent.visibility = View.INVISIBLE
                mLine.visibility = View.INVISIBLE
                mUpNow.visibility = View.GONE

                mUpProText.visibility = View.VISIBLE
                mUpProgress.visibility = View.VISIBLE
                start.invoke()
            }
        }, pUpdate = { start, msg ->
            // 提示更新
            // 不拦截返回键、点击外部可关闭。
            mDialog.setCancelable(true)
            mDialog.show()
            mUpLater.visibility = View.VISIBLE
            mUpContent.text = msg ?: "有新版本更新啦，您也可以选择下次再说"

            mUpNow.setOnClickListener {
                mUpContent.visibility = View.INVISIBLE
                mUpNow.visibility = View.GONE
                mUpLater.visibility = View.GONE

                mUpProText.visibility = View.VISIBLE
                mUpProgress.visibility = View.VISIBLE

                start.invoke()
            }
            mUpLater.setOnClickListener {
                mDialog.dismiss()
            }
        }, onFailed = {
            mDialog.dismiss()
            showToast("出现未知错误，以为您取消下载；请到用户反馈处反馈此问题。")
        }).checkUpdate(packageManager.getPackageInfo(packageName, 0).versionName)
    }

    private fun initBroadcast() {
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        filter.addAction("android.net.wifi.STATE_CHANGE")
        netObs = NetworkObs(object : NetworkObs.NetworkState {
            override fun onWifiConnected() {
                // wifi 连接上
            }

            override fun onMobileConnected() {
                // 移动数据连接
            }

            override fun onNetworkUnknown() {
                // 没有网络连接
            }
        })
        registerReceiver(netObs, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(netObs)
        super.onDestroy()
    }

    private fun initView() {
        frameLayout = findViewById(R.id.framelayout)
        radioGroup = findViewById(R.id.rg_bottom_bar)
        rbParent = findViewById(R.id.rb_parent)
//        rbSubject = findViewById(R.id.rb_subject)
        rbHot = findViewById(R.id.rb_hot)
        rbMy = findViewById(R.id.rb_my)

        rbParent.setOnClickListener {
            logE("homeEnable = $homeEnable")
            // 点击主页面刷新
            if (rbParent.isChecked) {
                if (!homeEnable) {
                    homeEnable = true
                    return@setOnClickListener
                }
                topics.refresh() // TODO : 点击刷新
            }
        }
        rbHot?.setOnClickListener {
            if (rbHot!!.isChecked) {
                if (!hotNewsEnable) {
                    hotNewsEnable = true
                    return@setOnClickListener
                }
//                hotFragment?.rdRefresh() // 缺少动画效果
            }
        }
    }

    private fun initData() {
        radioGroup!!.setOnCheckedChangeListener(this)

        //获取fragment管理器
        fragmentManager = supportFragmentManager

        //默认显示首页
//        changePage(parentFragment, null)
//        changePage(topics, null)
        fragmentManager.beginTransaction().apply {
            add(R.id.framelayout, topics)
        }.commit()
    }

    //切换页面 并把上一个界面添加到退栈中
    /*private fun changePage(fragment: Fragment?, tag: String?) {
        fragmentManager.beginTransaction()
                .replace(R.id.framelayout, fragment).addToBackStack(tag).commit()
    }*/

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        if (checkedId != R.id.rb_parent) {
            homeEnable = false
        }
        if (checkedId != R.id.rb_hot) {
            hotNewsEnable = false
        }
//        var b = false
        if (checkedId != selectedId) {
            hideAll()
        }
        selectedId = checkedId

        val bt = fragmentManager.beginTransaction()
        when (checkedId) {
            R.id.rb_parent -> {
//                b = false
//                changePage(parentFragment, null)
                if (topics.isAdded) {
                    bt.show(topics)
                } else {
                    bt.add(R.id.framelayout, topics)
                }
//	            changePage(topics, null)
            }
//            R.id.rb_subject -> {
//                b = false
//                changePage(subjectFragment, null)
//            }
            R.id.rb_hot -> {
//                b = false
//                changePage(hotFragment, null)
                if (hotFragment.isAdded) {
                    bt.show(hotFragment)
                } else {
                    bt.add(R.id.framelayout, hotFragment)
                }
            }
            R.id.rb_my -> {
//                b = false
//                changePage(myFragment, null)
                if (myFragment.isAdded) {
                    bt.show(myFragment)
                } else {
                    bt.add(R.id.framelayout, myFragment)
                }
            }
//            R.id.rb_books -> { // TODO : 图书页面 hide
////                b = true
//                changePage(booksFragment, null)
//            }
        }
//        statusBar(b)

        bt.commit()
    }

    private fun hideAll() {
        fragmentManager.beginTransaction().apply {
            if (topics.isAdded) hide(topics)
            if (hotFragment.isAdded) hide(hotFragment)
            if (myFragment.isAdded) hide(myFragment)
        }.commit()
    }

    /*private fun statusBar(b: Boolean) {
        if (b) {
            // 白底黑字状态栏 . api大于23 (Android6.0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)
            }
        } else {
            // 改回蓝底白字 . api大于23 (Android6.0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                window.statusBarColor = ContextCompat.getColor(baseContext, R.color.colorPrimaryDark)
            }
        }
    }*/

    // 实现两秒内连续点击回退按钮退出效果
//    override fun onBackPressed() {
//        if (!finished) {
//            finished = true
//            ToastUtil.showToast("再次点击退出")
//            Handler().postDelayed({ finished = false }, 1500)
//        } else {
//            finish()
//        }
//    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mBackPressedTime = System.currentTimeMillis()
            return true
        }

        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this@MainActivity).onActivityResult(requestCode, resultCode, data)
    }

    override fun isSuccessful(isSuccess: Boolean) {
        this.isSuccess = isSuccess
    }

    /*override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (topics.isVisible) {
            topics.resize()
        }
    }*/
}
