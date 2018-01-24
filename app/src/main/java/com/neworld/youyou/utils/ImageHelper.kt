package com.neworld.youyou.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import com.umeng.socialize.utils.DeviceConfig.context
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

    fun uriToPath(context: Context, uri: Uri) = with(uri) {
        when (scheme) {
            ContentResolver.SCHEME_CONTENT -> {
                var str = "null"
                context.contentResolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA),
                        null, null, null)
                        .also {
                            if (it.moveToFirst()) {
                                val index = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                                if (index > -1) str = it.getString(index)
                            }
                        }.close()

                str
            }
            else -> {
                uri.path
            }
        }
    }

}