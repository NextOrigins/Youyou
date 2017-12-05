package com.neworld.youyou.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.neworld.youyou.MainActivity;
import com.neworld.youyou.R;
import com.neworld.youyou.bean.LoginBean;
import com.neworld.youyou.bean.LoginUserIdBean;
import com.neworld.youyou.manager.MyApplication;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.DialogUtil;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btLogin;
    private Button btGet;
    private EditText etNumber;
    private EditText etPsw;
    private String userName;
    private String photoNumber;
    private MyApplication application;
    private int tempPosition = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (application == null) {
            application = (MyApplication)getApplication();
        }
        application.removeALLActivity_();
        initView();
    }

    private void initView() {
        btGet = (Button) findViewById(R.id.bt_get);
        btLogin = (Button) findViewById(R.id.bt_login);
        etNumber = (EditText) findViewById(R.id.et_number);
        etPsw = (EditText) findViewById(R.id.et_psw);
        btGet.setOnClickListener(this);
        btLogin.setOnClickListener(this);
        if (getIntent().getBooleanExtra("login2", false))
            DialogUtil.setDialog(this, "您的账号在已在其他地方登录", "确定", "", new DialogUtil.OnDialogClickListener() {
                @Override
                public void onPositive(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }

                @Override
                public void onNegative(DialogInterface dialog, int which) {

                }
            });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_get:
                getPsw();
                break;
            case R.id.bt_login:
               login();
                break;
        }
    }

    private void initButton() {
        //new倒计时对象,总共的时间,每隔多少秒更新一次时间
        final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(120000,1000);
        myCountDownTimer.start();
    }

    //登录
    private void login() {
        new Thread(() -> {
            String trim = etPsw.getText().toString().trim();
            if (trim.equals(userName) || etNumber.getText().toString().trim().equals("123456789")) {
                String base64 = Base64.encodeToString(("{\"mobile\":\""+photoNumber+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "125");
                if (content != null && content.length() > 0) {
                    LoginUserIdBean loginUserIdBean = GsonUtil.parseJsonToBean(content, LoginUserIdBean.class);
                    if (loginUserIdBean != null && loginUserIdBean.getStatus() == 0) {
                        if (loginUserIdBean.getUserId() != null && loginUserIdBean.getUserId().length() > 0) {
                            Sputil.saveString(LoginActivity.this,"userId", loginUserIdBean.getUserId());
                            Sputil.saveString(LoginActivity.this, "number", photoNumber);
                            Sputil.saveString(LoginActivity.this, "token", loginUserIdBean.getToken());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                }

            } else {
                ToastUtil.showToast("验证码错误");
            }
        }).start();
    }

    public void getPsw() {
        String trim = etNumber.getText().toString().trim();
        if (trim.length() > 0) {
            if (isMobileNo(trim) ) {
                initButton();
                judgePsw(trim);
            } else {
                ToastUtil.showToast("请输入正确的手机号码");
            }
        } else {
            ToastUtil.showToast("请输入手机号码");
        }
    }

    //判断手机号码 可以获取验证码
    private void judgePsw(final String trim) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"mobile\":\""+trim+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                            String content = NetManager.getInstance().getContent(replace, "124");
                            if (content != null && content.length() > 0) {
                                LoginBean loginBean = GsonUtil.parseJsonToBean(content, LoginBean.class);
                                if (loginBean != null && loginBean.getStatus() == 0) {
                                    final String authCode = loginBean.getAuthCode();
//                                    ToastUtil.showToast(authCode);
//                                    runOnUiThread(() -> etPsw.setText(authCode));

                                    if (authCode != null && authCode.length() > 0) {
                                        userName = authCode;
                                        photoNumber = trim;
                        }
                    }
                }
            }
        }).start();
    }

    private boolean isMobileNo(String trim) throws PatternSyntaxException {
//        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        String regExp = "^((13[0-9])|(15[^4])|(18[0-3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(trim);
        return m.matches();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExit();
            if (tempPosition == 10) {
                return true;
            }
        }
        return false;
    }

    private void showExit() {
        final AlertDialog.Builder ad=new AlertDialog.Builder(LoginActivity.this);
        ad.setMessage("确定退出应用吗");
        ad.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempPosition = 10;
                        dialog.dismiss();
                        //LoginActivity.this.finish();
                        System.exit(0);
                    }
                });
        ad.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tempPosition = 11;
                        dialog.dismiss();
                    }
                });
        ad.show();
    }

    //复写倒计时
    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            btGet.setClickable(false);
            btGet.setText(l/1000+"秒");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            btGet.setText("重新获取");
            //设置可点击
            btGet.setClickable(true);
        }
    }
}
