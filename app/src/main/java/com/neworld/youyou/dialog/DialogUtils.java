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
        sBuilder.setNegativeButton(negative, (dialog, which) -> listener.onNegativeListener(dialog));
        sBuilder.setPositiveButton(positive, (dialog, which) -> listener.onPositiveListener(dialog));
        sBuilder.setCancelable(true);
        sBuilder.show();
    }

    public interface onDiaLogBtnListener {
        void onPositiveListener(DialogInterface dialog);
        void onNegativeListener(DialogInterface dialog);
    }

}
