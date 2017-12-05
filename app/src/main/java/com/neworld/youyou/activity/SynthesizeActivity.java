package com.neworld.youyou.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.bean.LoginBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SynthesizeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView synTitle;
    private EditText synEt;
    private LinearLayout synLlBoy;
    private LinearLayout synLlGirl;
    private ImageView synIvBoy;
    private ImageView synIvGirl;
    private EditText synEtPerson;
    private ImageView ivCancel;
    private TextView ivSave;
    private static final int PHONE = 1001;
    private static final int GENDER = 1002;
    private static final int NAME = 1003;
    private static final int SIGN = 1004;
    private static final int GENDERBOY = 1005;
    private static final int GENDERGIRL = 1006;

    private int itemType = 0;
    private int genderType = 0;
    private String phoneNumber;
    private String personGender;
    private String personName;
    private String personSign;
    private String userId;

    private int num = 50;
    private String userName;
    private String photoNumber;
    private RelativeLayout rlSign;
    private RelativeLayout rlWhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synthesize);
        initUser();
        initView();
        initData();
    }
    private void initUser() {
        userId = Sputil.getString(SynthesizeActivity.this, "userId", "");
    }
    private void initData() {


    }

    private void initView() {
        ivCancel = (ImageView) findViewById(R.id.iv_canle);
        synTitle = (TextView) findViewById(R.id.syn_tv);
        synEt = (EditText) findViewById(R.id.syn_et);
        synLlBoy = (LinearLayout) findViewById(R.id.syn_ll_boy);
        synIvBoy = (ImageView) findViewById(R.id.syn_iv_boy);
        synLlGirl = (LinearLayout) findViewById(R.id.syn_ll_girl);
        synIvGirl = (ImageView) findViewById(R.id.syn_iv_girl);
        synEtPerson = (EditText) findViewById(R.id.syn_et_person);
        ivSave = (TextView) findViewById(R.id.iv_save);
        rlSign = (RelativeLayout) findViewById(R.id.rl_sign);
        rlWhite = (RelativeLayout) findViewById(R.id.rl_white);
        //首先将所有的控件都gone
        synEt.setVisibility(View.GONE);
        synLlBoy.setVisibility(View.GONE);
        synLlGirl.setVisibility(View.GONE);
        synIvGirl.setVisibility(View.GONE);
        synIvBoy.setVisibility(View.GONE);
        synEtPerson.setVisibility(View.GONE);
        synLlBoy.setOnClickListener(this);
        synLlGirl.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        synEtPerson.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Editable editable = synEtPerson.getText();
                int len = editable.length();
                if (len > num) {
                    ToastUtil.showToast("签名不能超过50个字");
                    int selEndIndex = Selection.getSelectionEnd(editable);
                    String str = editable.toString();
                    //截取新字符串
                    String newStr = str.substring(0, num);
                    synEtPerson.setText(newStr);
                    editable = synEtPerson.getText();

                    //新字符串的长度
                    int newLen = editable.length();
                    //旧光标位置超过字符串长度
                    if (selEndIndex > newLen) {
                        selEndIndex = editable.length();
                    }
                    //设置新光标所在的位置
                    Selection.setSelection(editable, selEndIndex);
                    //judgeString(newStr);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //电话
        Bundle bunde = this.getIntent().getExtras();
        int personTag = bunde.getInt("personTag");
        phoneNumber = bunde.getString("phoneNumber");
        String phoneTitle = bunde.getString("personPhone");
        if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(phoneTitle) && personTag == 101) {
            rlWhite.setVisibility(View.VISIBLE);
            synEt.setVisibility(View.VISIBLE);
            synTitle.setText(phoneTitle);
            synEt.setHint(phoneNumber);
            itemType = PHONE;
        }
        //男女
        personGender = bunde.getString("personGender");
        String myGender =  bunde.getString("myGender");
        if (!TextUtils.isEmpty(personGender) && !TextUtils.isEmpty(myGender) && personTag == 102) {
            synLlBoy.setVisibility(View.VISIBLE);
            synLlGirl.setVisibility(View.VISIBLE);
            itemType = GENDER;
            synTitle.setText(myGender);
            if (personGender .equals("男") ) {
                synIvBoy.setVisibility(View.VISIBLE);
                synIvGirl.setVisibility(View.GONE);
                genderType = GENDERBOY;
            } else {
                synIvGirl.setVisibility(View.VISIBLE);
                synIvBoy.setVisibility(View.GONE);
                genderType = GENDERGIRL;
            }
        }

        //昵称
        personName = bunde.getString("personName");
        final String nickName = bunde.getString("nickName");
        if (!TextUtils.isEmpty(personName) && !TextUtils.isEmpty(nickName) && personTag == 103) {
            rlWhite.setVisibility(View.VISIBLE);
            synEt.setVisibility(View.VISIBLE);
            itemType = NAME;
            synTitle.setText(nickName);
            synEt.setHint(personName);
        }
        //个性签名
        personSign = bunde.getString("personSign");
        String mySign = bunde.getString("mySign");
        if (personSign != null && !TextUtils.isEmpty(mySign) && personTag == 104) {
            synEtPerson.setVisibility(View.VISIBLE);
            rlSign.setVisibility(View.VISIBLE);
            itemType = SIGN;
            synTitle.setText(mySign);
            synEtPerson.setHint(personSign);

        }
        //保存的点击事件
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (itemType) {
                    case PHONE:
                        String number = synEt.getText().toString().trim();
                        if (number != null && number.length() > 0) {
                            if (isMobileNo(synEt.getText().toString().trim())) {
                                if (number.equals(phoneNumber) ) {
                                    final AlertDialog.Builder ad=new AlertDialog.Builder(SynthesizeActivity.this);
                                    ad.setMessage("该号码已经被注册");
                                    ad.setNegativeButton("取消",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    ad.show();
                                } else {
                                    judgePhone(number);
                                    initDialog(number);
                                }
                            } else {
                                ToastUtil.showToast("请输入正确的手机号码");
                            }
                        } else {
                            ToastUtil.showToast("请输入电话号码");
                        }

                        break;
                    case GENDER:
                        if (genderType == GENDERBOY) {
                            savePersonData(phoneNumber, 0, personName, personSign);
                        } else {
                            savePersonData(phoneNumber, 1, personName, personSign);
                        }
                        break;
                    case NAME:
                        if (!TextUtils.isEmpty(synEt.getText().toString().trim())) {
                            savePersonData(phoneNumber, genderType, synEt.getText().toString().trim(), personSign);
                        } else {
                            ToastUtil.showToast("请输入昵称");
                        }
                        break;
                    case SIGN:
                        if (!TextUtils.isEmpty(synEtPerson.getText().toString().trim())) {
                            savePersonData(phoneNumber, genderType, personName, synEtPerson.getText().toString().trim());
                        } else {

                        }
                        break;
                }
            }
        });
    }
    //初始化dialog
    private void initDialog(final String number) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(SynthesizeActivity.this);
        dialog.setTitle("请输入验证码");
        final EditText editText = new EditText(SynthesizeActivity.this);
        dialog.setView(editText);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String trim = editText.getText().toString().trim();
                if (trim.equals(userName)) {
                    savePersonData(number, genderType, personName, personSign);
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog builder = dialog.create();  //创建对话框
        builder.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        builder.show();
    }

    //判断手机号码
    private void judgePhone(final String number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"mobile\":\""+number+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "124");
                if (content != null && content.length() > 0) {
                    LoginBean loginBean = GsonUtil.parseJsonToBean(content, LoginBean.class);
                    if (loginBean != null && loginBean.getStatus() == 0) {
                        //int userStatus = loginBean.getUserStatus();
                        String authCode = loginBean.getAuthCode();
                        if (authCode != null && authCode.length() > 0) {
                            userName = authCode;
                            photoNumber = number;
                        }
                    }
                }
            }
        }).start();

    }

    private boolean isMobileNo(String trim) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(trim);
        return m.matches();
    }

    //个人信息保存到网络上
    private void savePersonData(final String phoneNumber, final int personGender, final String personName, final String personSign) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\",\"nickName\":\"" + personName + "\", \"phone\":\"" + phoneNumber + "\", \"sdasd\":\"" + personSign + "\", \"sex\":\"" + personGender + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "128");
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus != null && returnStatus.getStatus() == 0) {
                        if (personName != null && personName.length() > 0) {
                            Sputil.saveString(SynthesizeActivity.this, "personName", personName);
                        }
                        resultData();
                    }
                    finish();
                } else {
                    ToastUtil.showToast(content);
                }
            }
        }).start();
    }
    //保存携带数据
    private void resultData() {
        Intent intent = new Intent();
        SynthesizeActivity.this.setResult(RESULT_OK, intent);
        intent.putExtra("isChange", true);
        SynthesizeActivity.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
        }
        return false;
    }

    //判断是否有内容
    private void back() {
        // String trim = etComment.getText().toString().trim();
        String trim = synEt.getText().toString().trim();
        String sign = synEtPerson.getText().toString().trim();
        if ((trim != null && trim.length() > 0) || (sign!= null && sign.length() > 0)) {
            dialogPublish();
        } else {
            finish();
        }
    }

    private void dialogPublish() {
        final AlertDialog.Builder ad=new AlertDialog.Builder(SynthesizeActivity.this);
        ad.setMessage("确定退出编辑吗");
        ad.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.syn_ll_boy:
                genderType = GENDERBOY;
                synIvGirl.setVisibility(View.GONE);
                synIvBoy.setVisibility(View.VISIBLE);
                break;
            case R.id.syn_ll_girl:
                genderType = GENDERGIRL;
                synIvBoy.setVisibility(View.GONE);
                synIvGirl.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_canle:
                back();
                break;
        }
    }
}
