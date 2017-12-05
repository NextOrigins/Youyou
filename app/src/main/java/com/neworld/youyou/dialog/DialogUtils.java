package com.neworld.youyou.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by asus on 2017/9/29.
 */

public class DialogUtils {

    public static void showDialog(Context context, String message, String positive, String negative, final onDiaLogBtnListener listener) {
        AlertDialog.Builder sBuilder = new AlertDialog.Builder(context);
        sBuilder.setMessage(message);
        sBuilder.setNegativeButton(negative, (dialog, which) -> dialog.dismiss());
        sBuilder.setPositiveButton(positive, listener::onPositiveListener);
        sBuilder.setCancelable(true);
        sBuilder.show();
    }

    public interface onDiaLogBtnListener {
        void onPositiveListener(DialogInterface dialog, int which);
    }

}
