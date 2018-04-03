package com.neworld.youyou.update

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.neworld.youyou.utils.Fields
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

        fun openUpdate(onProgressUpDate: (newProgress: Int) -> Unit, c: Context) {
            this.onProgressUpDate = onProgressUpDate
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

            input = urlConnection.inputStream
            out = BufferedOutputStream(FileOutputStream(apkFile))

            while (input.read(byteArray).also { readLength = it } != -1) {
                readTotal += readLength
                out.write(byteArray, 0, readLength)

                val progress = (readTotal * 100L / length).toInt()

                if (progress != oldProgress) {
                    // 更新UI
                    onProgressUpDate?.invoke(progress)
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
        try {
            val array = listOf("chmod", "777", file.toString())
            val builder = ProcessBuilder(array)
            builder.start()
        } catch (e: Exception) {
        }
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}