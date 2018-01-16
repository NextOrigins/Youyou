package com.neworld.youyou.view.mview.parents

import android.graphics.*
import android.os.Build
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.View
import android.view.WindowManager
import com.neworld.youyou.R
import com.neworld.youyou.add.base.Activity
import kotlinx.android.synthetic.main.activity_answers.*

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
			val options = BitmapFactory.Options()
			options.inJustDecodeBounds = true
			val bitmap = BitmapFactory.decodeStream(assets.open("test.png")/*, Rect(), options*/)
			
			val sbs = SpannableString("icon")
			val imageSpan = ImageSpan(this, zoomImg(bitmap, width, bitmap.height))
			
			sbs.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
			
			val text = _edit.text
			val start = _edit.selectionStart
			text.insert(start, sbs)
			text.append("\r\n")
			_edit.text = text
			_edit.setSelection(start + sbs.length)
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
}