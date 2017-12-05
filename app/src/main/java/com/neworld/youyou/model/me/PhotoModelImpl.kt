package com.neworld.youyou.model.me

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Base64
import com.neworld.youyou.utils.NetBuild
import com.neworld.youyou.utils.Sputil
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.util.HashMap

/**
 * @author by user on 2017/11/17.
 */
class PhotoModelImpl : PhotoModel {

    private var i: Int = 0

    override fun takePhoto() {

    }

    override fun choosePhoto(size: Int) {

    }

    override fun commit(listener: PhotoModel.PhotoListener, content: String, array: ArrayList<String>, context: Context) {
        if (TextUtils.isEmpty(content)) {
            listener.onError("内容不能为空")
            return
        }
        doAsync {
            val map = HashMap<CharSequence, CharSequence>()
            val userId = Sputil.getString(context, "userId", "")
            map.put("userId", userId)
            map.put("content", content)
            map.put("bugId", "0")
            val response = NetBuild.getResponse(map, 173)
            // {"bugId":1156,"status":0}
            val split = response.split("\"".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val bugId = split[2].substring(1, split[2].length - 1)
            val isSuccess = split[4].contains("0")
            if (isSuccess) {
                if (array.isEmpty()) {
                    //  结束并关闭页面 inline
                    uiThread { listener.onSuccess() }
                    return@doAsync
                }
                val commit = postImage(userId, bugId, array)
                if (commit)
                    uiThread { listener.onSuccess() }
                else
                    uiThread { listener.onError("提交图片失败, 请检查网络稍后再试") }
            } else {
                uiThread {  listener.onError("上传失败") }
            }
        }
    }

    private fun postImage(userId: String, bugId: String, array: ArrayList<String>): Boolean {
        val map = HashMap<CharSequence, CharSequence>()
        map.put("userId", userId)
        map.put("bugId", bugId)
        map.put("iconString", minify(array[i]))
        map.put("imageType", ".jpg")
        map.put("sumCount", array.size.toString())
        val response = NetBuild.getResponse(map, 174)
        // {"status":0}
        if (response.contains("{\"status\":0}") && array.size != ++i)
            postImage(userId, bugId, array)
        else if (response.contains("{\"status\":0}")) {
            i = 0
            return true
        } else {
            i = 0
            return false
        }
        return false
    }

    /**
     * 压缩图片并加密
     *
     * @param path 图片路径
     * @return 压缩后的图片（字节）
     */
    private fun minify(path: String): String {
        val bitmap = BitmapFactory.decodeFile(path)
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos)//参数100表示不压缩
        val bytes = bos.toByteArray()
        val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        val replace = base64.replace("\n", "")
        return replace.replace("+", "%2B")
    }
}