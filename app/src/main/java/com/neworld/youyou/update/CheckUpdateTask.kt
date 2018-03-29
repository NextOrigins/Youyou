package com.neworld.youyou.update

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import com.google.gson.Gson

import com.neworld.youyou.R
import com.neworld.youyou.bean.ResponseBean
import com.neworld.youyou.manager.NetManager
import com.neworld.youyou.utils.Fields
import com.neworld.youyou.utils.LogUtils


/**
 * @author feicien (ithcheng@gmail.com)
 * @since 2016-07-05 19:21
 */
internal class CheckUpdateTask(val context: Activity, private val mType: Int, showProgressDialog: Boolean)//this.mShowProgressDialog = showProgressDialog;
    : AsyncTask<Void, Void, String>() {

    //正在检查版本
    override fun onPreExecute() {
        //   if (mShowProgressDialog) {
        //dialog = new ProgressDialog(mContext);
        //dialog.setMessage(mContext.getString(R.string.android_auto_update_dialog_checking));
        //dialog.show();
        // }
    }

    //后台操作结束之后
    override fun onPostExecute(result: String?) {
        /*
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }*/

        if (!TextUtils.isEmpty(result)) {
            parseJson(result!!)
//            LogUtils.LOG_JSON("UpDate ? $result")
        }
    }

    private fun parseJson(result: String) {
        val data = Gson().fromJson<ResponseBean.Version>(result, ResponseBean.Version::class.java)
        val c = context
        if (data.status == 0) {
            val res = data.results
            val versionName = c.packageManager.getPackageInfo(c.packageName, 0).versionName
            val l = toInt(versionName)
            val m = toInt(res!!.versionNum!!)
            val r = toInt(res.versionName!!)
//            LogUtils.E("当前版本 : $l, 最低版本 : $m, 最新版本 : $r")
            when {
                (m > l) -> {
                    // 强制更新
                    showOneDialog(context, "重大版本更新, 请升级新版本", url)
                    showNotification(context, "重大版本更新, 请升级新版本", url)
                }
                (res.out == 0 && r > l) -> {
                    // 提示更新
                    showDialog(context, res.msg!!, url)
                    showNotification(context, res.msg, url)
                }
            }
        }
    }

    private fun toInt(str: String): Int {
        return str.split('.').fold(0){ total, next -> total + next.toInt() }
    }

    /**
     * Show dialog
     */
    private fun showDialog(context: Context, content: String, apkUrl: String) {
        UpdateDialog.show(context, content, apkUrl)
    }

    private fun showOneDialog(context: Activity, content: String, apkUrl: String) {
        UpdateDialog.show(context, content, apkUrl)
    }

    /**
     * Show Notification
     */
    private fun showNotification(context: Context, content: String, apkUrl: String) {
        val myIntent = Intent(context, DownloadService::class.java)
        myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        myIntent.putExtra("updateUrl", apkUrl)
        val pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val smallIcon = context.applicationInfo.icon
        val notify = NotificationCompat.Builder(context)
                .setTicker(context.getString(R.string.android_auto_update_notify_ticker)) //发现新版本,点击进行升级
                .setContentTitle(context.getString(R.string.android_auto_update_notify_content)) //发现新版本,点击进行升级
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setContentIntent(pendingIntent).build()

        notify.flags = Notification.FLAG_AUTO_CANCEL
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(3, notify)
    }

    override fun doInBackground(vararg args: Void): String? {
        return NetManager.getInstance().getContent("", "171")
    }

    companion object {
        // private boolean mShowProgressDialog;
        private val url = Fields.APK_URL
    }
}
