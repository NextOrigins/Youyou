package com.neworld.youyou.add;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.neworld.youyou.R;
import com.neworld.youyou.add.base.Activity;
import com.neworld.youyou.utils.Fields;

public class AchievementActivity extends Activity {

    private WebView wb;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_achievement;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        wb = findViewById(R.id.query_web);
        wb.loadUrl(Fields.ACHIEVEMENT);

        WebSettings wbs = wb.getSettings();

        //设置自适应屏幕，两者合用
        wbs.setUseWideViewPort(true); //将图片调整到适合webview的大小
        wbs.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

//        wbs.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("");
        dialog.setMessage("请稍等");

        wb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                dialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dialog.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (errorCode == 404) {
                    view.loadUrl(Fields.ACHIEVEMENT);
                }
            }
        });

        wb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                new AlertDialog.Builder(AchievementActivity.this)
                        .setTitle("网页提示")
                        .setMessage(message)
                        .setPositiveButton("确定", (dialog1, which) -> {
                            result.confirm();
//                            dialog.dismiss();
                        })
                        .setCancelable(false)
                        .show();
                return true;
            }
        });

        findViewById(R.id.ac_close).setOnClickListener(v -> finish());
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onResume() {
        super.onResume();
        wb.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        wb.getSettings().setJavaScriptEnabled(false);
    }

    @Override
    protected void onDestroy() {
        if (wb != null) {
            wb.loadDataWithBaseURL(null, ""
                    , "text/html", "utf-8", null);
            wb.clearHistory();
            ((ViewGroup) wb.getParent()).removeView(wb);
            wb.destroy();
            wb = null;
        }
        super.onDestroy();
    }
}
