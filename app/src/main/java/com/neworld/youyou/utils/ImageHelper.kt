package com.neworld.youyou.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * @author by user on 2018/1/10.
 */
object ImageHelper {
	
	fun minify(path: String): String {
		val bitmap = BitmapFactory.decodeFile(path)
		val bos = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos)//参数100表示不压缩
		val bytes = bos.toByteArray()
//		logE("bytes = ${String(bytes)}")
		val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
		val replace = base64.replace("\n", "")
		return replace.replace("+", "%2B")
	}
}