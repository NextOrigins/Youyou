package com.neworld.youyou.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.neworld.youyou.R;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.MyApplication;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivClose;
    private Button btQuit;
    private LinearLayout linearLayout;
    private String userId;
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_settings);
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        initUser();
        initView();
    }

    private void initUser() {
        userId = Sputil.getString(SettingActivity.this, "userId", "");
    }

    private void initView() {
        ivClose = $(R.id.iv_close);
        btQuit = $(R.id.bt_quit);
        linearLayout = $(R.id.ll_setting);
        ivClose.setOnClickListener(this);
        btQuit.setOnClickListener(this);

        $(R.id.black_list).setOnClickListener(this);
        $(R.id.address_manager).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.black_list:
                startActivity(new Intent(SettingActivity.this, BlackNameActivity.class));
                break;
            case R.id.bt_quit:
                tanDialog();
                break;
            case R.id.address_manager:
                startActivity(new Intent(this, AddressActivity.class).putExtra("fromSetting", true));
                break;
        }
    }

    //弹出dialog
    private void tanDialog() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(SettingActivity.this);
        ad.setMessage("确定退出登录吗");
        ad.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quit();
                        dialog.dismiss();
                    }
                });
        ad.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        ad.show();
    }

    private void quit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "152");
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus != null && returnStatus.getStatus() == 0) {
                        Sputil.saveString(SettingActivity.this, "userId", "");
                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                        application.removeALLActivity_();
                        SettingActivity.this.finish();
                    }
                }
            }
        }).start();
    }

    private <T extends View> T $(int res) {
        return (T) findViewById(res);
    }
}
