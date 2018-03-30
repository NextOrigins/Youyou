package com.neworld.youyou


import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle

import android.support.design.widget.Snackbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.ViewGroup

import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup

import com.neworld.youyou.fragment.HotFragment
import com.neworld.youyou.fragment.MyFragment
import com.neworld.youyou.fragment.SubjectFragment
import com.neworld.youyou.manager.MyApplication
import com.neworld.youyou.update.UpdateChecker
import com.neworld.youyou.utils.NetworkObs
import com.neworld.youyou.view.ParentView
import com.neworld.youyou.view.mview.books.BooksViewImpl
import com.neworld.youyou.view.mview.parents.QAFragment
import com.umeng.socialize.UMShareAPI
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

fun showSnackBar(viewGroup: ViewGroup, text: String, duration: Int = 1000)
		= Snackbar.make(viewGroup, text, duration).apply {
    view.setBackgroundColor(ContextCompat.getColor(viewGroup.context, R.color.colorPrimary))
    show()
}

class MainActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener, ParentView {

    private var frameLayout: FrameLayout? = null

    private var radioGroup: RadioGroup? = null
    private var rbParent: RadioButton? = null
//    private var rbSubject: RadioButton? = null
    private var rbHot: RadioButton? = null
    private var rbMy: RadioButton? = null
//    private var parentFragment: ParentPageFragment? = null
	private var parentsQA: QAFragment by Delegates.notNull()
    private var subjectFragment: SubjectFragment? = null
    private var hotFragment: HotFragment? = null
    private var myFragment: MyFragment? = null
    private var fragmentManager: FragmentManager by Delegates.notNull()
    private var isSuccess = false
    private var homeEnable = true
    private var hotNewsEnable = false
    var mainApplication: MyApplication? = null
//        private set
    private var netObs: NetworkObs? = null

//    private var booksFragment: EBooks? = null
    private var booksFragment: BooksViewImpl? = null

    private var mBackPressedTime by Delegates.observable(0L) {
        _, old, new ->
        if (new - old > 1000) {
            showSnackBar(activity_main, getString(R.string.exit_message))
        }
        if (new - old in 1..1000) {
            finish()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置当前布局
        setContentView(R.layout.activity_main)
//        parentFragment = ParentPageFragment() // 原 家长圈
	    parentsQA = QAFragment()
        
        subjectFragment = SubjectFragment()
        hotFragment = HotFragment()
        myFragment = MyFragment()

        booksFragment = BooksViewImpl()  // 出售图书
//        booksFragment = EBooks() // 电子书

        if (mainApplication == null) {
            mainApplication = application as MyApplication
        }
        mainApplication!!.addActivity_(this)
        initView()
        initData()
        initBroadcast()

        //进行版本更新
        UpdateChecker.checkForDialog(this)
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

        rbParent!!.setOnClickListener { // 点击主页面刷新
            if (rbParent!!.isChecked) {
                if (!homeEnable) {
                    homeEnable = true
                    return@setOnClickListener
                }
                parentsQA.rdRefresh()
            }
        }
        rbHot?.setOnClickListener {
            if (rbHot!!.isChecked) {
                if (!hotNewsEnable) {
                    hotNewsEnable = true
                    return@setOnClickListener
                }
                hotFragment?.rdRefresh() // 缺少动画效果
            }
        }
        myFragment?.setOnCacheRemoved {
            parentsQA.clearCache()
        }
    }

    private fun initData() {
        radioGroup!!.setOnCheckedChangeListener(this)

        //获取fragment管理器
        fragmentManager = supportFragmentManager

        //默认显示首页
//        changePage(parentFragment, null)
        changePage(parentsQA, null)
    }

    //切换页面 并把上一个界面添加到退栈中
    private fun changePage(fragment: Fragment?, tag: String?) {
        fragmentManager.beginTransaction()
                .replace(R.id.framelayout, fragment).addToBackStack(tag).commit()
    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        if (checkedId != R.id.rb_parent) {
            homeEnable = false
        }
        if (checkedId != R.id.rb_hot) {
            hotNewsEnable = false
        }
//        var b = false
        when (checkedId) {
            R.id.rb_parent -> {
//                b = false
//                changePage(parentFragment, null)
	            changePage(parentsQA, null)
            }
//            R.id.rb_subject -> {
//                b = false
//                changePage(subjectFragment, null)
//            }
            R.id.rb_hot -> {
//                b = false
                changePage(hotFragment, null)
            }
            R.id.rb_my -> {
//                b = false
                changePage(myFragment, null)
            }
//            R.id.rb_books -> { // TODO : 图书页面 hide
////                b = true
//                changePage(booksFragment, null)
//            }
        }
//        statusBar(b)
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
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this@MainActivity).onActivityResult(requestCode, resultCode, data)
    }

    override fun isSuccessful(isSuccess: Boolean) {
        this.isSuccess = isSuccess
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (parentsQA.isVisible) {
            parentsQA.resize()
        }
    }
}
