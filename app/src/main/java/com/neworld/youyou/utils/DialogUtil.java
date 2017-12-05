package com.neworld.youyou.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by tt on 2017/9/12.
 */

public class DialogUtil {

    public static AlertDialog.Builder setDialog (Activity activity,String message, String positive, String negative, final OnDialogClickListener l) {
        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setMessage(message);
        ad.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                l.onPositive(dialog, which);
            }
        });
        ad.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                l.onNegative(dialog, which);
            }
        });
        ad.show();
        return ad;
    }

    public interface OnDialogClickListener {
        void onPositive(DialogInterface dialog, int which);
        void onNegative(DialogInterface dialog, int which);
    }
/*    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setCancelable(false);
                dialog.setMessage("信息未保存，确认返回");
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    });
                dialog.setNeutralButton("确认", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            returnMes();
            dialog.dismiss();
            activity.gender = "";
            activity.name = "";
            activity.birth = "";
            activity.school = "";
            activity.babyId = 0;
        }
    });
                dialog.show();*/

}
