package com.neworld.youyou.view.mview.parents

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.activity_answers.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.concurrent.TimeUnit

/**
 * @author by user on 2018/1/15.
 */
class Answers : Activity() {

    private val userId by preference("userId", "")

    private var cacheImgPath: String = ""

    private var title: String? = null

    private val width by lazy {
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        point.x
    }

    override fun getContentLayoutId() = R.layout.activity_answers

    /*override fun initWindows() {
        // 白底黑字状态栏 . api大于23 (Android6.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)
        }
    }*/

    override fun initArgs(bundle: Bundle?): Boolean {
        title = bundle?.getString("answerTitle")
        return super.initArgs(bundle)
    }

    override fun initWidget() {
        if (title == null) { // QUESTION TITLE
            _title.visibility = View.GONE
        } else {
            _title.text = title
        }

        _close.setOnClickListener { onBackPressed() }

        _img.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        2)
            }

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(Intent.createChooser(intent, "选择图片"), 1)
        }

        _soft_input.setOnClickListener {
            _edit.toggleSoftInput(false)
        }

        _publish.setOnClickListener {
            _loading.visibility = View.VISIBLE
            postContent()
        }
    }

    private fun postContent() {
        doAsync {
            val sb = StringBuilder()
            StringBuilder().apply {
                _edit.text.split("<切割>".toRegex()).forEach {
                    when {
                        it.take(5) == "<img>" && it.takeLast(6) == "</img>" -> {
                            val path = it.substring(5, it.length - 6)
                            val src = ImageHelper.minify(path)
                            hashMapOf<CharSequence, CharSequence>().run {
                                put("userId", userId)
                                put("iconString", src)
                                put("imageType", "jpg")
                                val response = commitImg(this)
                                val data = Gson().fromJson<ImgUrl>(response,
                                        object : TypeToken<ImgUrl>() {}.type)
                                data.imgUrl
                            }.let { if (it.isNotEmpty()) append("<img src=\"$it\"/>"); cacheImgPath = "$cacheImgPath|$it" }
                        }
                        it.isNotEmpty() -> {
                            it.split('\n').forEach {
                                val str = it.replace("+", "%2B")
                                if (it.isNotEmpty()) {
                                    "<p>$str</p>"
                                } else {
                                    "</br>"
                                }.let(this::append)
                                sb.append("$it\n")
                            }
                            if (endsWith("</br>")) {
                                val s = substring(0, length - 5)
                                setLength(0)
                                append(s)
                            }
                        }
                    }
                }
            }.let {
                val map = hashMapOf<CharSequence, CharSequence>().apply {
                    val bundle = intent.extras
                    put("userId", userId)
                    put("taskId", bundle.getString("taskId"))
                    put("from_uid", bundle.getString("uid"))
                    put("comment_id", "")
                    put("attachedContent", sb.subSequence(0, sb.length - 1))
                    put("content", it)
                    put("commentImg", cacheImgPath.trim('|'))
                }
                val response = NetBuild.getResponse(map, 205)
                uiThread {
                    _loading.visibility = View.GONE
                    _edit.toggleSoftInput(false)
                    if ("0" in response) {
                        finish()
                        "上传成功"
                    } else {
                        "上传失败"
                    }.let(ToastUtil::showToast)
                }
                Unit
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == 1 && resultCode == RESULT_OK -> {
                data?.data?.let {
                    val path = ImageHelper.uriToPath(baseContext, it)
                    val source = "<切割><img>$path</img><切割>"
                    val bitmap = convertBitmap(path)
                    val sps = SpannableString(source)
                    val imgSpan = ImageSpan(this, bitmap)

                    sps.setSpan(imgSpan, 0, source.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    insertIntoEditText(sps)
                }
            }
        }
    }

    private fun zoomImg(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    private fun convertBitmap(path: String): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        BitmapFactory.decodeFile(path, options)

        val height = options.outHeight.toFloat() / options.outWidth * width

        options.inJustDecodeBounds = false

        val bitmap = BitmapFactory.decodeFile(path, options)

        return zoomImg(bitmap, width, height.toInt())
    }

    private fun insertIntoEditText(ss: SpannableString) = with(_edit) {
        if (text.isNotEmpty() && !text.endsWith('\n')) append("\n")
        val start = selectionStart
        text = text.apply {
            insert(start, ss)
        }
        setSelection(start + ss.length)
        _edit.post {
            _edit.toggleSoftInput(true)
        }
        Unit
    }

    override fun onBackPressed() {
        if (_edit.text.isNotEmpty()) {
            displayDialog(this, "关闭将丢失数据, 确定关闭吗",
                    { super.onBackPressed() })
            return
        }
        _edit.toggleSoftInput(false)
        super.onBackPressed()
    }

    private fun commitImg(map: HashMap<CharSequence, CharSequence>): String {
        val body = Base64.encodeToString(Gson().toJson(map).toByteArray(), Base64.DEFAULT)
                .replace("\n", "")
        val url = "http://106.14.251.200:8083/neworld/android/204"

        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()
        val type = MediaType.parse("application/json; charset=utf-8")
        val requestBody = RequestBody.create(type, body)
        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

        return try {
            client.newCall(request).execute().body()?.string() ?: "null"
        } catch (e: Exception) {
            e.printStackTrace()
            "null"
        }
    }

    private data class ImgUrl(
            val imgUrl: String,
            val status: Int
    )

    private fun EditText.toggleSoftInput(show: Boolean) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) {
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        } else {
            imm.hideSoftInputFromWindow(this.windowToken, 0)
        }
    }
}