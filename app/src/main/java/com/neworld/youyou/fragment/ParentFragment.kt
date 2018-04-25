package com.neworld.youyou.fragment

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView

import com.neworld.youyou.MainActivity
import com.neworld.youyou.R
import com.neworld.youyou.activity.DetailDataActivity
import com.neworld.youyou.activity.LoginActivity
import com.neworld.youyou.activity.ParentDetailActivity
import com.neworld.youyou.adapter.ParentAdapter
import com.neworld.youyou.bean.ParentBean
import com.neworld.youyou.bean.PersonDataBean
import com.neworld.youyou.manager.DataTransmissionManager
import com.neworld.youyou.pulltorefresh.PullToRefreshBase
import com.neworld.youyou.pulltorefresh.PullToRefreshListView
import com.neworld.youyou.utils.*
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

import kotlin.properties.Delegates

class ParentFragment : BaseFragment(), View.OnClickListener {

    private var ptlv: PullToRefreshListView by notNullSingleValue()
    private var mView: View by Delegates.notNull()
    private var parentAdapter: ParentAdapter? = null
    private var newMenuList = ArrayList<ParentBean.MenuListBean>()
    private var bean: ParentBean? = ParentBean()
    private var activity: MainActivity? = null
    private var dialog: Dialog by Delegates.notNull()

    private var favTaskId: String = ""
    private var collectStatus: Int = 0
    private var url: String? = null
    private var lvParent: ListView by Delegates.notNull()
    private var clickPosition = -1
    private var clickTask: String? = null
    private var dyStatus: Int = 0

    private var userId by preference("userId", "")
    private var token by preference("token", "")
    private var role: Int by Delegates.notNull()

    private val map by lazy { hashMapOf<CharSequence, CharSequence>() }
    private var index: Int by Delegates.notNull()
    private var currentOnClick by Delegates.observable(0L) {
        _, old, new ->

        if (new - old in 1..2000) {
            ToastUtil.showToast("waiting...")
        }
        if (new - old > 2000) {
            openH5(index, false)
        }
    }

    /**
     * 条目点击监听
     */
    private val lvItemClick = AdapterView.OnItemClickListener { _, _, position, _ ->
        index = position
        currentOnClick = System.currentTimeMillis()
    }

    private var parentPosition = -1

    override fun createView(): View? {
        mView = View.inflate(context, R.layout.fragment_parent, null)
        // UmengTool.checkWx(getContext());
        initView()
        return mView
    }

    override fun onResume() {
        super.onResume()
        val isSuccess = SPUtil.getBoolean(context, getString(R.string.android_success), false)
        if (isSuccess) {
            Thread {
                getParent(PARENTDOWN)
                SPUtil.saveBoolean(context, getString(R.string.android_success), false)
            }.start()
        }

        val wxShare = SPUtil.getBoolean(context, "WXShare", false)
        if (wxShare) {
            shareNet(clickTask, clickPosition)
            SPUtil.saveBoolean(context, "WXShare", false)
        }
    }

    private fun initView() {
        activity = getActivity() as MainActivity
        ptlv = mView.findViewById<View>(R.id.lv_parent) as PullToRefreshListView
        //设置下拉刷新与加载更多可用
        ptlv.isPullRefreshEnabled = true
        ptlv.isPullLoadEnabled = true
        ptlv.isScrollLoadEnabled = false
        lvParent = ptlv.refreshableView
        if (parentAdapter == null) {
            parentAdapter = ParentAdapter(context, bean)
            lvParent.adapter = parentAdapter
        } else {
            parentAdapter!!.notifyDataSetChanged()
        }
        initAdapter()
        //上拉刷新 下拉加载
        ptlv.setOnRefreshListener(object : PullToRefreshBase.OnRefreshListener<ListView> {
            //下拉刷新操作
            override fun onPullDownToRefresh(refreshView: PullToRefreshBase<ListView>) {
                Thread { getParent(PARENTDOWN) }.start()
                ptlv.onPullDownRefreshComplete()
            }

            //加载更多操作
            override fun onPullUpToRefresh(refreshView: PullToRefreshBase<ListView>) {
                Thread { getParent(PARENTUP) }.start()
                ptlv.onPullUpRefreshComplete()
            }
        })

        //条目点击 进入详情页面 h5页面
        lvParent.onItemClickListener = lvItemClick
    }

    private fun openH5(position: Int, b: Boolean) {
        clickPosition = position
        val list = newMenuList[position]
        map.run {
            clear()
            put("from_uid", list.from_uid)
            put("taskId", list.taskId)
            put("title", list.title)
            put("content", list.content)
            if (!TextUtils.isEmpty(list.imgs)) {
                put("image", list.imgs.split("\\|".toRegex())[0])
            }
            DataTransmissionManager.putString(Intent()
                    .putExtra("dian", b)
                    .putExtra("sort", list.sort)
                    .putExtra("video", list.imgs),
                    this@ParentFragment,
                    ParentDetailActivity::class.java,
                    this@run, 4)
        }
    }

    //获取家长圈网络数据
    override fun getData(): Any? {
        return getParent(PARENTDOWN)
    }

    private fun getParent(type: Int): ParentBean? {
        hashMapOf<CharSequence, CharSequence>().run {
            put("userId", userId)
            put("taskId", favTaskId)
            NetBuild.response({
                dyStatus = if (it.menuList != null) it.menuList.dynamic_status else 0
                role = if (it.menuList != null) it.menuList.role else 1
            }, ToastUtil::showToast, 126, PersonDataBean::class.java, this@run)
        }
        val parentBean = getParentBean(type)
        this.bean = parentBean
        if (parentBean != null && parentBean.status == 0) {
            val tokenStatus = parentBean.tokenStatus
            if (tokenStatus == 2)
                Util.uiThread { quit(Intent(context, LoginActivity::class.java), true) }
            val menuList = parentBean.menuList
            if (menuList != null && menuList.size > 0) {
                if (type == PARENTDOWN) {
                    parentAdapter?.setBean(parentBean)
                } else
                    parentAdapter?.addBean(parentBean)

                newMenuList.clear()
                newMenuList.addAll(parentAdapter!!.menuList)
                Util.uiThread { parentAdapter!!.notifyDataSetChanged() }
            } else if (menuList != null && menuList.size == 0) {
                ToastUtil.showToast(getString(R.string.parent_not_more_data))
            }
        } else {
            if (parentBean != null && parentBean.status != 0)
                quit(Intent(context, LoginActivity::class.java), false) // 如果没登录 status != 0 . 不会返回数据，所以直接跳出登录界面 //
            else
                ToastUtil.showToast(getString(R.string.parent_net_pool))
        }
        return parentBean
    }

    private fun getParentBean(type: Int) = with(map) {
        clear()
        val date = if (type == PARENTUP && newMenuList.isNotEmpty())
            newMenuList[newMenuList.size - 1].createDate
        else
            ""
        put("createDate", date)
        put("userId", userId)
        put("token", token)
        NetBuild.enqueue(this, ParentBean::class.java, "107")
    }

    private fun quit(intent: Intent, login2: Boolean) {
        map.run {
            clear()
            put("userId", userId)
            NetBuild.getResponse(this, 152).let {
                SPUtil.saveString(context, "userId", "")
                if (login2) intent.putExtra("login2", true)
                startActivity(intent)
                activity?.mainApplication?.removeALLActivity_()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT >= 23) {
            val mPermissionList = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS)
            ActivityCompat.requestPermissions(getActivity()!!, mPermissionList, 123)
        }
    }

    //获取数据进行展示
    override fun initAdapter() {
        parentAdapter!!.setOnParentClick(object : ParentAdapter.OnParentClick {
            //进入个人界面
            override fun onItemIcon(position: Int) {
                val intentPhone = Intent()
                intentPhone.setClass(context, DetailDataActivity::class.java)
                val bundleSimple = Bundle()
                bundleSimple.putString("from_uid", newMenuList[position].from_uid.toString())
                intentPhone.putExtras(bundleSimple)
                startActivity(intentPhone)
            }

            override fun onItemName(position: Int) {}

            //删除 加入黑名单
            override fun onItemAddBlack(position: Int) {
                parentPosition = position
                addBlackName(position)
            }

            //收藏
            override fun onItemFav(position: Int) {
                addFav(position)
            }

            //评论
            override fun onItemComment(v: View, position: Int, bean: ParentBean.MenuListBean) {

                if (bean.comment_count > 0)
                    openH5(position, true)
                else
                    openH5(position, false)

                v.isClickable = false
                lvParent.onItemClickListener = null
                Handler().postDelayed({
                    lvParent.onItemClickListener = lvItemClick
                    v.isClickable = true
                }, 2000)
            }

            //分享
            override fun onItemShare(position: Int) {
                val menuListBean = newMenuList[position]
                val taskId = menuListBean.taskId
                clickTask = taskId
                clickPosition = position
                url = "http://www.uujz.me:8082/neworld/user/143?taskId=$taskId&userId=$userId"
                share(url, position, taskId)
            }
        })
    }

    private fun share(url: String?, position: Int, taskId: String) = with(UMWeb(url)) {
        val menuListBean = newMenuList[position]
        title = if (!TextUtils.isEmpty(menuListBean.title)) menuListBean.title else ""
        if (!TextUtils.isEmpty(menuListBean.imgs))
            setThumb(menuListBean.imgs.split("\\|".toRegex())[0].let { UMImage(context, it) })
        description = if (!TextUtils.isEmpty(menuListBean.content)) menuListBean.content else ""
        ShareAction(getActivity())
                .withMedia(this)
                .setDisplayList(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(object : UMShareListener {
                    override fun onResult(p0: SHARE_MEDIA?)
                            = shareNet(taskId, position)

                    override fun onError(p0: SHARE_MEDIA?, p1: Throwable?)
                            = ToastUtil.showToast("哎呀, 分享失败了!")

                    override fun onCancel(p0: SHARE_MEDIA?) {
                    }

                    override fun onStart(p0: SHARE_MEDIA?) {
                    }
                }).open()
    }

    private fun shareNet(taskId: String?, position: Int) {
        map.run {
            clear()
            put("taskId", taskId.toString())
            put("type", "1")
            doAsync {
                NetBuild.getResponse(map, 144).let {
                    if (it.contains("0")) {
                        newMenuList[position].transmit_count++
                        uiThread { parentAdapter?.notifyDataSetChanged() }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            5 -> {
                if (data != null && data.getBooleanExtra("isPublish", false)) {
                    val menuListBean = newMenuList[clickPosition]
                    menuListBean.comment_count++
                    parentAdapter?.notifyDataSetChanged()
                }
            }
            4 -> {
                if (clickPosition != -1 && data != null) {
                    val list = newMenuList[clickPosition]
                    val collectStatus = data.getIntExtra("isCollectStatus", list.collectStatus)
                    val likeStatus = data.getIntExtra("isLikeStatus", list.sex)
                    if (collectStatus != list.collectStatus || likeStatus != list.sex) {
                        list.collectStatus = collectStatus
                        list.sex = likeStatus
                        parentAdapter?.notifyDataSetChanged()
                    }
                }
            }
            else ->
                UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data)
        }
    }

    //收藏
    private fun addFav(position: Int) {
        val menuListBean = newMenuList[position]
        favTaskId = menuListBean.taskId
        collectStatus = menuListBean.collectStatus
        // 1 是未收藏0 是收藏
        map.run {
            clear()
            put("userId", userId)
            put("taskId", favTaskId)
            put("type", "1")
            put("status", collectStatus.toString())
            doAsync {
                NetBuild.getResponse(this@run, 112).let {
                    if (it.contains("0"))
                        uiThread { parentAdapter?.notifyDataSetChanged() }
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.save_photo -> {
                addBlackAndDelete()
                dialog.dismiss()
            }
            R.id.cancel -> dialog.dismiss()
        }
    }

    //  删除或者拉黑
    private fun addBlackAndDelete() {
        if (parentPosition == -1) {
            ToastUtil.showToast(getString(R.string.android_delete_false))
        } else {
            val menuListBean = newMenuList[parentPosition]
            val fromUid = menuListBean.from_uid
            if (fromUid == userId) {
                //删除
                deleteDynamic(parentPosition, menuListBean)
            } else {
                //拉黑
                addBlack(menuListBean)
            }
        }
    }

    //拉黑
    private fun addBlack(menuListBean: ParentBean.MenuListBean) {
        map.run {
            clear()
            put("userId", userId)
            put("target_uid", menuListBean.from_uid)
            doAsync {
                val response = NetBuild.getResponse(this@run, 150)
                if (response.contains("0"))
                    getParent(1)
                else
                    ToastUtil.showToast(getString(R.string.parent_net_pool))
            }
        }
    }

    //删除
    private fun deleteDynamic(parentPosition: Int, menuListBean: ParentBean.MenuListBean) {
        map.run {
            clear()
            put("userId", userId)
            put("taskId", menuListBean.taskId)
            doAsync {
                val response = NetBuild.getResponse(this@run, 110)
                uiThread {
                    if (response.contains("0")) {
                        newMenuList.removeAt(parentPosition)
                        parentAdapter?.menuList?.removeAt(parentPosition)
                        parentAdapter?.notifyDataSetChanged()
                    } else
                        ToastUtil.showToast("删除出现未知错误")
                    dialog.dismiss()
                }
            }
        }
    }

    private fun addBlackName(position: Int) {
        val bean = parentAdapter!!.menuList[position]
        when (role) {
            1 -> {
                dialog = Dialog(context, R.style.ActionSheetDialogStyle)
                //填充对话框的布局
                val inflate = LayoutInflater.from(context).inflate(R.layout.dialog_photo,
                        mView.parent as ViewGroup, false)

                val choosePhoto = inflate.findViewById<View>(R.id.choosePhoto) as TextView
                val takePhoto = inflate.findViewById<View>(R.id.takePhoto) as TextView
                val savePhoto = inflate.findViewById<View>(R.id.save_photo) as TextView
                val cancel = inflate.findViewById<View>(R.id.cancel) as TextView

                choosePhoto.visibility = View.GONE
                takePhoto.visibility = View.GONE
                initDialog(dialog, inflate)
                val fromUid = bean.from_uid
                if (fromUid == userId) {
                    savePhoto.text = "删除"
                } else {
                    savePhoto.text = "加入黑名单"
                }
                savePhoto.setOnClickListener(this)
                cancel.setOnClickListener(this)
            }
            2 -> {
                dialog = Dialog(context, R.style.ActionSheetDialogStyle)

                val inflate = LayoutInflater.from(context).inflate(R.layout.dialog_parents,
                        mView.parent as ViewGroup, false)
                initDialog(dialog, inflate)

                inflate.findViewById<View>(R.id.cancel).setOnClickListener { dialog.dismiss() }
                val status = bean.status

                val l = View.OnClickListener {
                    val map = HashMap<String, String>()
                    val msg: String
                    val url: String
                    when (it.id) {
                        R.id._top -> {
                            map.clear()
                            map.put("taskId", bean.taskId)
                            msg = if (bean.stickStatus == 1) "<置顶>" else "<取消置顶>"
                            url = "196"
                        }
                        R.id.hide -> {
                            map.clear()
                            map.put("userId", userId)
                            map.put("taskId", bean.taskId)
                            map.put("status", status.toString())
                            msg = if (status == 0) "<屏蔽>" else "<取消屏蔽>"
                            url = "155"
                        }
                        R.id.hide_all -> {
                            map.clear()
                            map.put("userId", userId)
                            map.put("fromUserId", bean.from_uid)
                            msg = if (bean.maskStatus == 0) "<屏蔽>" else "<取消屏蔽>"
                            url = "186"
                        }
                        R.id.display -> {
                            map.clear()
                            map.put("userId", userId)
                            msg = if (dyStatus == 1) "<隐藏>" else "<显示>"
                            url = "186_2"
                        }
                        else -> {
                            map.clear()
                            url = ""
                            msg = ""
                        }
                    }
                    doAsync {
                        NetBuild.getResponse(map, url).let {
                            if (it.contains("0")) {
                                ToastUtil.showToast("已$msg")
                                getParent(1)
                            } else {
                                ToastUtil.showToast("${msg}失败")
                            }
                        }
                    }
                    dialog.dismiss()
                }

                val hide = inflate.findViewById<TextView>(R.id.hide)
                val hideAll = inflate.findViewById<TextView>(R.id.hide_all)
                val display = inflate.findViewById<TextView>(R.id.display)
                val black = inflate.findViewById<TextView>(R.id.black)
                val top = inflate.findViewById<TextView>(R.id._top)

                hide.setOnClickListener(l)
                hideAll.setOnClickListener(l)
                display.setOnClickListener(l)
                inflate.findViewById<View>(R.id._top).setOnClickListener(l)
                black.setOnClickListener { addBlackAndDelete() }

                black.text = if (bean.from_uid == userId) "删除" else resources.getString(R.string.black_list)
                hide.text = if (status == 0) "<屏蔽>本条信息" else "<取消屏蔽>本条信息"
                hideAll.text = if (bean.maskStatus == 0) "<屏蔽>所有信息" else "<取消屏蔽>所有信息"
                display.text = if (dyStatus == 1) "<隐藏>屏蔽的信息" else "<显示>屏蔽的信息"
                top.text = if (bean.stickStatus == 1) "置顶" else "<取消>置顶"
            }
        }
    }

    private fun initDialog(dialog: Dialog, inflate: View) {
        //将布局设置给Dialog
        dialog.setContentView(inflate)
        //获取当前Activity所在的窗体
        val dialogWindow = dialog.window
        //设置Dialog从窗体底部弹出
        dialogWindow!!.setGravity(Gravity.BOTTOM)
        //获得窗体的属性
        val lp = dialogWindow.attributes
        val point = Point()
        activity!!.windowManager.defaultDisplay.getSize(point)
        lp.width = point.x
        //lp.y = 20;//设置Dialog距离底部的距离
        //       将属性设置给窗体
        dialogWindow.attributes = lp
        dialog.show()//显示对话框
    }

    companion object {
        private val PARENTDOWN = 1
        private val PARENTUP = 2
    }
}
