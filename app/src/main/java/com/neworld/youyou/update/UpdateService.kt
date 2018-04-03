package com.neworld.youyou.update

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import com.neworld.youyou.utils.Fields
import com.neworld.youyou.utils.logE
import com.neworld.youyou.utils.showToast
import com.neworld.youyou.utils.uiThread
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * @author by hhhh on 2018/4/2.
 */
class UpdateService : IntentService("UpdateService") {

    companion object {
        private var onProgressUpDate: ((newProgress: Int) -> Unit)? = null
        private var onFailed: (() -> Unit)? = null

        fun openUpdate(c: Context, onProgressUpDate: (newProgress: Int) -> Unit,
                       onFailed: (() -> Unit)? = null) {
            this.onProgressUpDate = onProgressUpDate
            this.onFailed = onFailed
            c.startService(Intent(c, UpdateService::class.java))
        }
    }

    override fun onHandleIntent(p0: Intent?) {
        var input: InputStream? = null
        var out: BufferedOutputStream? = null
        try {
            val url = URL(Fields.APK_URL)
            val urlConnection = url.openConnection() as HttpURLConnection

            urlConnection.requestMethod = "GET"
            urlConnection.doOutput = false
            urlConnection.connectTimeout = 10 * 1000
            urlConnection.readTimeout = 10 * 1000
            urlConnection.setRequestProperty("Connection", "Keep-Alive")
            urlConnection.setRequestProperty("Charset", "UTF-8")
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate")

            urlConnection.connect()

            val length = urlConnection.contentLength
            var readTotal = 0
            var readLength = 0
            var oldProgress = 0
            val byteArray = ByteArray(10 * 1024)

            val apkFile = File(StorageUtils.getCacheDirectory(this), "uujz.apk")

            // 如果不存在则创建文件夹，如果出现未知错误则取消下载。
            /*if (!apkFile.exists()) {
                apkFile.parentFile.mkdirs()
                try {
                    apkFile.createNewFile()
                } catch (e: Exception) {
                    uiThread { showToast("创建文件失败，出现未知错误") }
                    onFailed?.invoke()
                    logE("未知错误：：：：：：：：：：$e")
                    return
                }
            }*/

            input = urlConnection.inputStream
            out = BufferedOutputStream(FileOutputStream(apkFile))

            while (input.read(byteArray).also { readLength = it } != -1) {
                readTotal += readLength
                out.write(byteArray, 0, readLength)

                val progress = (readTotal * 100L / length).toInt()

                if (progress != oldProgress) {
                    // 更新UI
                    uiThread { onProgressUpDate?.invoke(progress) }
                }
                oldProgress = progress
            }

            // 下载完成
            installAPK(apkFile)
        } catch (e: Exception) {

        } finally {
            try {
                input?.close()
            } catch (e: Exception) {

            }
            try {
                out?.close()
            } catch (e: Exception) {

            }
        }
    }

    /**
     * 下载完成后安装apk
     */
    private fun installAPK(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "com.neworld.youyou.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }

        intent.setDataAndType(data, "application/vnd.android.package-archive")
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}