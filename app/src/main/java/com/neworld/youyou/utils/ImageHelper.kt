package com.neworld.youyou.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayOutputStream
import java.io.Reader

/**
 * @author by user on 2018/1/10.
 */
object ImageHelper {

	fun imgToBase(path: String): String {
		val bitmap = BitmapFactory.decodeFile(path)
		val bos = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos)
		val base64 = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT)
		val replase = base64.replace("\n", "")
		return replase.replace("+", "%2B")
	}
}