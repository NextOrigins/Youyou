package com.neworld.youyou.view.mview.parents

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.View
import android.view.WindowManager
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.utils.*
import kotlinx.android.synthetic.main.activity_answers.*
import org.jetbrains.anko.doAsync

/**
 * @author by user on 2018/1/15.
 */
class Answers : Activity() {

	private val userId by preference("userId", "")
    private val tskId by preference("taskId", "")

	private val width by lazy {
		val point = Point()
		windowManager.defaultDisplay.getSize(point)
		point.x
	}

	override fun getContentLayoutId()
			= R.layout.activity_answers

	override fun initWindows() {
		// 白底黑字状态栏 . api大于23 (Android6.0)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
			window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)
		}
	}

	override fun initWidget() {
		_close.setOnClickListener { finish() }

		_img.setOnClickListener {
			if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
						2)
			}

			val intent = Intent(Intent.ACTION_GET_CONTENT)
			intent.addCategory(Intent.CATEGORY_OPENABLE)
			intent.type = "image/*"
			startActivityForResult(Intent.createChooser(intent, "选择图片"), 1)
		}

		_publish.setOnClickListener {
			postContent()
		}
	}

	private fun postContent() {
        doAsync {
            StringBuilder().apply {
                _edit.text.split('\n').forEach {
                    when {
                        it.take(5) == "<img>" && it.takeLast(6) == "</img>" -> {
                            val path = it.substring(5, it.length - 6)
                            val src = ImageHelper.imgToBase(path)
                            hashMapOf<CharSequence, CharSequence>().run {
                                put("userId", userId)
                                put("iconString", src)
                                put("imageType", ".jpg")
                                val response = NetBuild.getResponse(this, 204)
                                logE("response : $response")
                                response
                            }.let { if (it.isNotEmpty()) append("<img src=\"$it\"/>") }
                        }
                        else -> if (it.isNotEmpty()) append("<p>$it</p>")
                    }
                    append("</br>")
                }
            }.let {
                        hashMapOf<CharSequence, CharSequence>().run {
                            put("userId", userId)
                            put("taskId", tskId)
                            put("from_uid", intent.getStringExtra("uid"))
                            put("content", it)
                            put("comment_id", "")

                            NetBuild.response({
                                // TODO : 调用NetBuild.getResponse方法查看返回json. 应该是{"status":"0"}这样.
                            }, ToastUtil::showToast, 205, Unit::class.java, this)
                        }
                    }
        }

		/*val sb = StringBuilder()
		_edit.text.split("<切割>".toRegex()).forEach {
//			sb.append("<p>")
			if ("</img>" in it) {
				val take = it.take(it.length - 6)
				hashMapOf<CharSequence, CharSequence>().run {
					put("userId", userId)
					put("iconString", it)
					put("imageType", ".jpg")
				}
				sb.append("<img src=\"$take\"/>")

			} else {
				if ('\n' in it) {
					it.split('\n')
							.filter { it.isNotEmpty() }
							.forEach {
								sb.append("<p>")
								sb.append(it)
								sb.append("</p>")
							}
				} else
					sb.append(it.trim('\n'))
			}
//			sb.append("</p>")
//			sb.append("<p><br/></p>")
		}
		logE("sb = $sb")*/
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when {
			requestCode == 1 && resultCode == RESULT_OK -> {
				data?.data?.let {
//					val source = "<切割>${it.path}</img><切割>"
					val source = "<img>${it.path}</img>"
					val bitmap = convertBitmap(it.path)
					val sps = SpannableString(source)
					val imgSpan = ImageSpan(this, bitmap)

					sps.setSpan(imgSpan, 0, source.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
					val content = insertIntoEditText(sps)
					logE("edit content : $content")
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
		if (text.isNotEmpty()) append("\n")
		val start = selectionStart
		text = text.apply {
			insert(start, ss)
			append("\n")
		}
		setSelection(start + ss.length + 1)

		text.toString()
	}

	override fun onBackPressed() {
		if (_edit.text.isNotEmpty()) {
			displayDialog(this, "关闭将丢失数据, 确定关闭吗",
					{ super.onBackPressed() })
			return
		}
		super.onBackPressed()
	}
}