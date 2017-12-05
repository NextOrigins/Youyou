package com.neworld.youyou.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;

import com.neworld.youyou.R;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.DialogUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Validator;

import ch.ielse.view.SwitchView;

public class AddNewAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEt_addname;
    private EditText mEt_add_phone;
    private EditText mEtadd_addresscontent;
    private SwitchView mSwitchView;
    private String mUserId;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        initUser();
        initView();
        initData();
    }

    /**
     * 如果是点击编辑开启的activity则把数据传递过来
     */
    private void initData() {
        Intent intent = getIntent();
        if (!intent.getBooleanExtra("edit", false)) return;
        mEt_addname.setText(intent.getStringExtra("name"));
        mEt_add_phone.setText(intent.getStringExtra("phone"));
        mEtadd_addresscontent.setText(intent.getStringExtra("address"));
        switch (intent.getIntExtra("status", 1)) {
            case 0:
                mSwitchView.toggleSwitch(true);
                break;
            case 1:
                mSwitchView.toggleSwitch(false);
                break;
        }
    }

    private void initUser() {
        mUserId = Sputil.getString(this, "userId", "");
    }

    private void initView() {

        mEt_addname = (EditText) findViewById(R.id.et_addname);
        mEt_add_phone = (EditText) findViewById(R.id.et_add_photo);
        mEtadd_addresscontent = (EditText) findViewById(R.id.add_addresscontent);

        mSwitchView = (SwitchView) findViewById(R.id.switchView);//滑动按钮
        //销毁点击事件
        findViewById(R.id.iv_cancel).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);//保存
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                isEmtryData();
                break;
            case R.id.tv_save:
                saveData();
                break;

        }
    }

    private void saveData() {
        final String username = mEt_addname.getText().toString().trim();
        final String userphone = mEt_add_phone.getText().toString().trim();
        final String useraddress = mEtadd_addresscontent.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showToast("请输入收货人姓名");
            mEt_addname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(userphone) || !Validator.isMobile(userphone)) {
            ToastUtil.showToast("请输入正确的手机号码");

            return;
        }
        if (TextUtils.isEmpty(useraddress)) {
            ToastUtil.showToast("请填写详细地址");
            return;
        }
        //上传数据
        if (!TextUtils.isEmpty(mUserId)) {
            mProgressDialog = ProgressDialog.show(this, null, "正在保存中...");
            new Thread(() -> addNewAddressData(username, userphone, useraddress)).start();
        }
    }

    private void addNewAddressData(String username, String userphone, String useraddress) {
        boolean edit = getIntent().getBooleanExtra("edit", false);
        int status = mSwitchView.isOpened() ? 0 : 1;
        String content;
        if (edit) {
            int id = getIntent().getIntExtra("ID", 0);
            String base64 = Base64.encodeToString(("{\"addressId\":\"" + id + "\", \"userId\":\"" + mUserId + "\", \"phone\":\"" + userphone + "\", \"consignee\":\"" + username + "\", \"status\":\"" + status + "\",\"address\":\"" + useraddress + "\" }").getBytes(), Base64.DEFAULT);
            String replace = base64.replace("\n", "");
            content = NetManager.getInstance().getContent(replace, "182");// 182修改
        } else {
            String base64 = Base64.encodeToString(("{\"userId\":\"" + mUserId + "\", \"phone\":\"" + userphone + "\", \"consignee\":\"" + username + "\", \"status\":\"" + status + "\",\"address\":\"" + useraddress + "\" }").getBytes(), Base64.DEFAULT);
            String replace = base64.replace("\n", "");
            content = NetManager.getInstance().getContent(replace, "181");// 181添加
        }
        if (!TextUtils.isEmpty(content)) {
            if (edit) ToastUtil.showToast("修改成功!");
            else ToastUtil.showToast("保存成功!");
            Intent intent = getIntent();
            intent.putExtra("name", username);
            intent.putExtra("phone", userphone);
            intent.putExtra("address", useraddress);
            setResult(5, intent);
            runOnUiThread(this::finish);
        } else {
            ToastUtil.showToast("网络不佳");
        }
        runOnUiThread(() -> {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        });
    }

    public void isEmtryData() {
        String userName = mEt_addname.getText().toString().trim();
        String userPhone = mEt_add_phone.getText().toString().trim();
        String userAddress = mEtadd_addresscontent.getText().toString().trim();
        if (getIntent().getBooleanExtra("edit", false)) {
            String name = getIntent().getStringExtra("name");
            String phone = getIntent().getStringExtra("phone");
            String address = getIntent().getStringExtra("address");
            if (TextUtils.equals(name, userName)
                    && TextUtils.equals(phone, userPhone)
                    && TextUtils.equals(address, userAddress))
                finish();

        } else if (!TextUtils.isEmpty(userName) || !TextUtils.isEmpty(userPhone) || !TextUtils.isEmpty(userAddress)) {
            DialogUtil.setDialog(this, "信息未保存，确认返回?", "确定", "取消", new DialogUtil.OnDialogClickListener() {
                @Override
                public void onPositive(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onNegative(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
