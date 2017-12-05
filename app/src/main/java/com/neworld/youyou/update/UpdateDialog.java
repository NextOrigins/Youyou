package com.neworld.youyou.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.KeyEvent;

import com.neworld.youyou.MainActivity;
import com.neworld.youyou.R;


class UpdateDialog {

    static void show(final Context context, String content, final String downloadUrl) {
        if (isContextValid(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.android_auto_update_dialog_title);
            builder.setMessage(Html.fromHtml(content))
                    .setPositiveButton(R.string.android_auto_update_dialog_btn_download, (dialog, id) -> {
                        goToDownload(context, downloadUrl);
                        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (m != null) m.cancelAll();
                    })
                    .setNegativeButton(R.string.android_auto_update_dialog_btn_cancel, (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            //点击对话框外面,对话框不消失
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    static void show(final Activity context, String content, final String downloadUrl, int version) {
        if (isContextValid(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.android_auto_update_dialog_title);
            builder.setMessage(Html.fromHtml(content))
                    .setPositiveButton(R.string.android_auto_update_dialog_btn_download, (dialog, id) -> {
                        goToDownload(context, downloadUrl);
                        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (m != null) m.cancelAll();
                    });
            AlertDialog dialog = builder.create();
            //点击对话框外面,对话框不消失
            dialog.setCancelable(false);
            dialog.setOnKeyListener((dialog1, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    dialog.dismiss();
                    context.finish();
                }
                return false;
            });
            dialog.show();
        }
    }

    private static boolean isContextValid(Context context) {
        return context instanceof Activity && !((Activity) context).isFinishing();
    }


    private static void goToDownload(Context context, String downloadUrl) {
        Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
        intent.putExtra("updateUrl", downloadUrl);
        context.startService(intent);
    }
}
