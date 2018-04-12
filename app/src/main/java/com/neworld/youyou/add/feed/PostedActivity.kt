package com.neworld.youyou.add.feed

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.neworld.youyou.BuildConfig
import com.neworld.youyou.R
import com.neworld.youyou.activity.ClipImageActivity
import com.neworld.youyou.activity.PublishDetailActivity
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.add.common.Adapter
import com.neworld.youyou.add.SpacesItemDecoration
import com.neworld.youyou.presenter.me.PhotoPresenterImpl
import com.neworld.youyou.select.ImageSelectorUtils
import com.neworld.youyou.utils.LogUtils
import com.neworld.youyou.utils.ToastUtil
import com.neworld.youyou.utils.displayDialog
import com.neworld.youyou.view.mview.PhotoView

import java.io.File
import java.util.ArrayList

/**
 * @author by user on 2017/11/7.
 */

class PostedActivity : Activity(), PhotoView {

    var dialog: ProgressDialog? = null

    private var photoImpl: PhotoPresenterImpl? = null

    override fun close() {
        finished(false)
    }

    override fun showDialog() {
    }

    override fun hideDialog() {
    }

    override fun showProgress() {
        dialog = ProgressDialog.show(this, null, "请稍等")
    }

    override fun hideProgress() {
        dialog?.dismiss()
    }

    override fun showToast(str: String) {
        ToastUtil.showToast(str)
    }

    override fun context(): Context = applicationContext

    private var mAdapter: Adapter<ReportBean>? = null
    private val list = ArrayList<ReportBean>()
    private val cameraPermission = 2
    private val openCamera = 3
    private val selectPhoto = 5
    private val refreshPhotos = 10
    private val cameraAllPermission = 22
    private val writeExternalPermission = 33
    private var tempFile: File? = null

    /**
     * 列数
     */
    private var spanCount: Int = 0
    /**
     * 间距
     */
    private var spacing: Float = 0.toFloat()

    private val compressionPhotos = ArrayList<String>() //  上传加密后的图片
    private val cache = ArrayList<File>() // 记录本地缓存的图片

    internal var i = 0

    private val obs = object : Adapter.AdapterObs<ReportBean> {
        override fun onBind(holder: Adapter.Holder, bean: List<ReportBean>, position: Int) {
            val bitmapImg = holder.find<ImageView>(R.id.item_posted_img)
            val data = bean[position]

            // 给图片做大小处理，否则View会变形导致布局问题
            val point = Point()
            windowManager.defaultDisplay.getSize(point)
            val i = ((point.x - spacing * (spanCount + 1)) / spanCount).toInt()
            val pm = bitmapImg.layoutParams as ConstraintLayout.LayoutParams
            pm.width = i
            pm.height = i
            bitmapImg.layoutParams = pm

            // 处理ImageView显示
            if (data.show) {
                bitmapImg.visibility = View.VISIBLE
                if (!compressionPhotos.isEmpty() && position < compressionPhotos.size && !TextUtils.isEmpty(compressionPhotos[position]))
                    Glide.with(applicationContext).load(compressionPhotos[position]).into(bitmapImg)

            } else
                bitmapImg.visibility = View.INVISIBLE

            // 设置点击事件
            if (bitmapImg.visibility == View.VISIBLE) {
                if (data.scaleType) {
                    bitmapImg.scaleType = ImageView.ScaleType.FIT_CENTER
                    bitmapImg.setOnClickListener { startCamera(bean) }
                } else {
                    bitmapImg.scaleType = ImageView.ScaleType.CENTER_CROP
                    bitmapImg.setOnClickListener {
                        val intent = Intent()
                        intent.setClass(this@PostedActivity, PublishDetailActivity::class.java)
                        val bundle = Bundle()
                        bundle.putStringArrayList("imageList", ArrayList(compressionPhotos))
                        bundle.putString("point", position.toString())
                        intent.putExtras(bundle)
                        startActivityForResult(intent, refreshPhotos)
                    }
                }
            }
        }

        override fun layoutId() = R.layout.item_posted_img
    }

    override fun getContentLayoutId(): Int {
        return R.layout.activity_posted
    }

    override fun initArgs(bundle: Bundle?): Boolean {
        photoImpl = PhotoPresenterImpl(this)
        return super.initArgs(bundle)
    }

    override fun initWidget() {
        super.initWidget()
        // 创建页面后弹出键盘
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        spacing = resources.getDimension(R.dimen.dp15)
        spanCount = 3
        val mRecyclerView = find<RecyclerView>(R.id.posted_recycler)
        mRecyclerView.layoutManager = GridLayoutManager(this, spanCount)
        mAdapter = Adapter(obs, list)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.addItemDecoration(SpacesItemDecoration(spanCount, spacing, true)) // includeEdge表示外部是否与屏幕有边距

        val content = find<EditText>(R.id.posted_content)

        find<View>(R.id.posted_close).setOnClickListener { finished() }
        find<View>(R.id.posted_commit).setOnClickListener {
            // 提交
            photoImpl?.commitFeedBack(content.text.toString(), compressionPhotos)
        }
    }

    override fun initData() {
        super.initData()
        for (i in 0..8) {
            val bean = ReportBean()
            bean.show = i == 0
            bean.scaleType = i == 0
            list.add(bean)
        }
        mAdapter?.notifyDataSetChanged()
    }

    /**
     * Dialog点击事件处理
     *
     * @param bean 主要提供个角标，懒得在上面处理，也无所谓了。
     */
    @SuppressLint("InflateParams")
    private fun startCamera(bean: List<ReportBean>) {
        val mDialog = Dialog(this@PostedActivity, R.style.ActionSheetDialogStyle)
        mDialog.setContentView(LayoutInflater.from(applicationContext)
                .inflate(R.layout.dialog_photo, null))
        // 拍照
        mDialog.findViewById<View>(R.id.takePhoto).setOnClickListener {
            photograph()

            mDialog.dismiss()
        }

        // 相册
        mDialog.findViewById<View>(R.id.choosePhoto).setOnClickListener {
            val x = bean.indices.reversed().firstOrNull { bean[it].show } ?: 0
            ImageSelectorUtils.openPhoto(this@PostedActivity, selectPhoto, false, bean.size - x)

            mDialog.dismiss()
        }

        mDialog.findViewById<View>(R.id.cancel).setOnClickListener { mDialog.dismiss() }
        mDialog.findViewById<View>(R.id.save_photo).visibility = View.GONE

        showWindow(mDialog)
    }

    /**
     * 显示Dialog
     *
     * @param dialog dialog
     */
    private fun showWindow(dialog: Dialog) {
        val dialogWindow = dialog.window ?: throw NullPointerException("The window is null")

        dialogWindow.setGravity(Gravity.BOTTOM)

        val lp = dialogWindow.attributes
        val windowManager = windowManager
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        lp.width = point.x

        dialogWindow.attributes = lp
        dialog.show()
    }

    /**
     * 打开相机
     */
    private fun photograph() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), cameraAllPermission)
            return
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //跳转到调用系统相机
            openCamera()
            return
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) //相机判断
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermission)
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    writeExternalPermission)
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraPermission)
            return
        }

        openCamera()
    }

    /**
     * 图片处理
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val photoShop = 4
        when (requestCode) {
        // 照相意图返回
            openCamera -> if (resultCode == Activity.RESULT_OK) {
                val uri = Uri.fromFile(tempFile)
                val intent = Intent(this, ClipImageActivity::class.java)
                intent.putExtra("type", 2)
                intent.data = uri
                startActivityForResult(intent, photoShop)
            }
        // 图片剪切后
            photoShop -> {
                val uri = data?.data ?: return
                val path = uri.path
                list[compressionPhotos.size].scaleType = false
                compressionPhotos.add(path)
                if (compressionPhotos.size < list.size) {
                    val bean = list[compressionPhotos.size]
                    bean.show = true
                    bean.scaleType = true
                }
                mAdapter!!.notifyDataSetChanged()
            }
            selectPhoto -> if (resultCode == Activity.RESULT_OK) {
                var x = list.size - 1
                val imagePaths = data?.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT)
                if (imagePaths != null && !imagePaths.isEmpty()) {
                    while (x >= 0) {
                        val bean = list[x]
                        if (bean.show) break
                        x--
                    }
                    var y = 0
                    val z = x + imagePaths.size
                    while (x < z) {
                        val bean = list[x]
                        val photoFile = imagePaths[y]
                        bean.photoFile = photoFile
                        bean.scaleType = false
                        bean.show = true
                        compressionPhotos.add(photoFile)
                        x++
                        y++
                    }
                    if (x < list.size) {
                        list[x].show = true
                        list[x].scaleType = true
                    }
                    mAdapter!!.notifyDataSetChanged()
                }
            }
            refreshPhotos -> if (resultCode == Activity.RESULT_OK) {
                val photoPaths = data?.getStringArrayListExtra("publish")
                if (photoPaths != null && !photoPaths.isEmpty()) {
                    compressionPhotos.clear()
                    compressionPhotos.addAll(photoPaths)
                    for (l in list.indices) {
                        val reportBean = list[l]
                        when {
                            l < compressionPhotos.size -> {
                                reportBean.scaleType = false
                                reportBean.show = true
                            }
                            l > compressionPhotos.size -> {
                                reportBean.scaleType = true
                                reportBean.show = false
                            }
                            else -> {
                                reportBean.scaleType = true
                                reportBean.show = true
                            }
                        }
                    }
                    mAdapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            cameraPermission -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openCamera()
            else
                ToastUtil.showToast("请到权限管理中添加照相机权限")
            cameraAllPermission -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                openCamera()
            else
                displayDialog(this, "请到权限管理中添加照相机或存储数据权限, 否则不能调用相机保存照片",
                        {}, "确定", {}, "")
            writeExternalPermission -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openCamera()
            else
                ToastUtil.showToast("请到权限管理中添加存储数据权限")
        }
    }

    private fun openCamera() {
        val path = Environment.getExternalStorageDirectory().path + "/cache/"
        val file = File(path)
        if (!file.exists()) {
            if (!file.mkdirs())
                ToastUtil.showToast("未检测到SD卡")
        }

        tempFile = File(path, System.currentTimeMillis().toString() + "Youyou.jpg")
        cache.add(tempFile!!)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.fileProvider", tempFile!!))
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
        }
        startActivityForResult(intent, openCamera)
    }

    override fun onBackPressed() {
        finished()
    }

    private fun finished(b: Boolean = true) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive)
            if (this@PostedActivity.currentFocus.windowToken != null)
                imm.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        if (b) {
            displayDialog(this, "确定关闭吗", { finished(false) })

        } else super.onBackPressed()
    }

    /**
     * 由于不知道Google什么时候把findViewById也改成泛型了，所以此方法没什么大用，不过懒得改了。
     */
    private inline fun <reified T : View> find(res: Int): T {
        return findViewById(res)
    }

    internal inner class ReportBean {
        var show: Boolean = false
        var scaleType: Boolean = false
        var photoFile: String? = null
    }

    override fun onDestroy() {
        photoImpl?.onDestroy()
        list.clear()
        compressionPhotos.clear()
        for (f in cache)
            LogUtils.E("delete ? " + f.delete())

        super.onDestroy()
    }
}
