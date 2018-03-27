package com.neworld.youyou.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.neworld.youyou.R;
import com.neworld.youyou.utils.SPUtil;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private String userId;
    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initUser();
        initView();
    }

    private void initUser() {
        userId = SPUtil.getString(TestActivity.this, "userId", "");
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.wv_test);
        ivImage = (ImageView) findViewById(R.id.iv_cancel);
        ivImage.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        final String ccaaUrl = extras.getString("ccaaUrl");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.loadUrl(ccaaUrl);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //String baseUrl = "http://106.14.251.200:8082/neworld/android/108?userId=10850&taskId=730&maodian=";
                if (url.equals("tel:4008812318")) {
                    tanDialog(url);
                    webView.loadUrl(ccaaUrl);
                    return false;
                } else {
                    webView.loadUrl(url);
                    return true;
                }
            }
        });
    }

    private void tanDialog(final String url) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(TestActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage(url);
        dialog.setPositiveButton("拨打", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //用intent启动拨打电话
                if (url.contains("tel:4008812318")) {
                    String replace = url.replace("tel:", "");
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + replace));
                    if (ActivityCompat.checkSelfPermission(TestActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intent);
                }
                //startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
