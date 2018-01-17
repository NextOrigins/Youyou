package com.neworld.youyou.view.mview.parents

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.View
import android.view.WindowManager
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import com.neworld.youyou.utils.logE
import com.neworld.youyou.view.icon.ClipViewLayout
import kotlinx.android.synthetic.main.activity_answers.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * @author by user on 2018/1/15.
 */
class Answers : Activity() {
	
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
			if (ContextCompat
					.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
					!= PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
						2)
			}
			
			val intent = Intent(Intent.ACTION_GET_CONTENT)
			intent.addCategory(Intent.CATEGORY_OPENABLE)
			intent.type = "image/*"
			startActivityForResult(Intent.createChooser(intent, "选择图片"), 1)
			
//			val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//			startActivityForResult(Intent.createChooser(intent, "选择图片"), 1)
			
			/*val options = BitmapFactory.Options()
			options.inJustDecodeBounds = true
			BitmapFactory.decodeStream(assets.open("test.png"), Rect(), options)
			
			val ratio = options.outHeight.toFloat() / options.outWidth
			val height = ratio * width
			
			options.inJustDecodeBounds = false
			
			val bitmap = BitmapFactory.decodeStream(assets.open("test.png"), Rect(), options)
			
			val sbs = SpannableString("icon")
			val imageSpan = ImageSpan(this, zoomImg(bitmap, width, height.toInt()))
			sbs.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
			
			insertIntoEditText(sbs)*/
		}
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//		super.onActivityResult(requestCode, resultCode, data)
		when {
			requestCode == 1 && resultCode == RESULT_OK -> {
				val uri = data?.data
				/*val inputStream: InputStream?
				try {
//					val file = ClipViewLayout.getRealFilePathFromUri(baseContext, uri)

//					logE("path : $file")
					inputStream = contentResolver.openInputStream(uri)
				} catch (e: FileNotFoundException) {
					logE("Answer.kt -> FileNotFoundException : line 78")
					return
				}*/
				
				if (uri != null) {
					val bitmap = convertBitmap(uri.path)
					val ss = SpannableString(uri.path)
					val imageSpan = ImageSpan(this, bitmap)
					ss.setSpan(imageSpan, 0, uri.path.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

					insertIntoEditText(ss)

//					bitmap?.recycle()
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
		var ips: InputStream? = null
		val options = BitmapFactory.Options()
		options.inJustDecodeBounds = true
		
		try {
			ips = FileInputStream(File(path))
		} catch (e: Exception) {
			e.printStackTrace()
		}
		
		BitmapFactory.decodeStream(ips, Rect(), options)
		
		val height = options.outHeight.toFloat() / options.outWidth * width
		
		options.inJustDecodeBounds = false
		
		try {
			ips = FileInputStream(File(path))
		} catch (e: Exception) {
			e.printStackTrace()
		}
		
		val bitmap = BitmapFactory.decodeStream(ips, Rect(), options)
		
		if (ips != null) ips.close()
		
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
	}
}