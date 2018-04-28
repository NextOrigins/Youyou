package com.neworld.youyou.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.UiThread;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.neworld.youyou.R;
import com.neworld.youyou.manager.MyApplication;

public class ToastUtil {

    private static Toast toast;
    private static Toast mToast;

    @SuppressLint("ShowToast")
    public static void showToast(final String text) {

        Util.uiThread(() -> {
            if (toast == null)
                toast = Toast.makeText(MyApplication.sContext, text, Toast.LENGTH_SHORT);
            else
                toast.setText(text);//如果不为空，则直接改变当前toast的文本

            toast.show();
        });
    }

    public static void showToast(Context context, String text) {
        Util.uiThread(() -> {
            if (mToast == null) {
                mToast = new Toast(context);
                mToast.setGravity(Gravity.CENTER, 0, 20);

                View root = LayoutInflater.from(context).inflate(R.layout.toast_1, null);
                LogUtils.E("mToast == null inflater root.");
                TextView content = root.findViewById(R.id.toast_content);
                content.setText(text);

                mToast.setView(root);
                mToast.setDuration(Toast.LENGTH_SHORT);
            } else {
                TextView content = mToast.getView().findViewById(R.id.toast_content);
                content.setText(text);
            }

            mToast.show();
        });
    }
}
