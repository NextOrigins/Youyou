package com.neworld.youyou.utils;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.neworld.youyou.manager.MyApplication;

public class ToastUtil {

	private static Toast toast;

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
}
