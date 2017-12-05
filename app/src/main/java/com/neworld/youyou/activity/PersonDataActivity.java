package com.neworld.youyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.bean.PersonDataBean;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;
import com.neworld.youyou.view.nine.CircleImageView;

public class PersonDataActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivCancle;
    private RelativeLayout rlIcon;
    private RelativeLayout rlPhone;
    private RelativeLayout rlGender;
    private RelativeLayout rlNickName;
    private ConstraintLayout rlPersonalSign;
    private CircleImageView circleImageView;
    private TextView tvPhone;
    private TextView tvGender;
    private TextView tvName;
    private TextView tvSign;
    private String phone;
    private int sex;
    private String name;
    private String sign;

    private String url;
    private String userId;
    private TextView tvId;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_data);
        initUser();
        initView();
        initNetData();
    }
    private void initUser() {
        userId = Sputil.getString(PersonDataActivity.this, "userId", "");
    }
    private void initNetData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                final String content = NetManager.getInstance().getContent(replace, "126");
                if (!TextUtils.isEmpty(content)) {
                    PersonDataBean personDataBean = GsonUtil.parseJsonToBean(content, PersonDataBean.class);
                    if (personDataBean!=null && personDataBean.getStatus() == 0) {
                        final PersonDataBean.MenuListBean menuList = personDataBean.getMenuList();
                        if (menuList != null) {
                            Util.uiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initData(menuList);
                                }
                            });
                        }
                    } else {
                        ToastUtil.showToast("未知异常");
                    }
                }

            }
        }).start();
    }

    private void initData(PersonDataBean.MenuListBean menuList) {

        String faceImg = menuList.getFaceImg();
        if (faceImg != null && faceImg.length() > 0) {
            url = faceImg;
                Glide.with(PersonDataActivity.this).load(faceImg).into(circleImageView);
        } else {
            Glide.with(PersonDataActivity.this).load(R.mipmap.my_icon).into(circleImageView);
        }
        String nickName = menuList.getNickName();
        String userAccount = menuList.getUserAccount();
            //设置昵称
            if (nickName != null && nickName.length() > 0) {
                name = nickName;
                tvName.setText(nickName);
            } else if (userAccount != null && userAccount.length() > 0){
                tvName.setText(userAccount);
                name = userAccount;
            } else {
                tvName.setText("");
            }
            //设置电话
            //String userName = menuList.getUserName();
        String userName = menuList.getUserName();
        String phoneNumber = menuList.getPhone();
            if (userName != null && userName.length() > 0) {
                tvPhone.setText(userName);
                this.phone = userName;
            } else if (phoneNumber != null && phoneNumber.length() > 0){
                this.phone = phoneNumber;
            } else {
                tvPhone.setText("");
            }
            //设置性别
            int sex = menuList.getSex();
                if (sex == 0) {
                    tvGender.setText("男");
                    this.sex = 0;
                } else {
                    tvGender.setText("女");
                    this.sex = 1;
                }

            //设置个性签名
        String sdasd = menuList.getSdasd();
        if (!TextUtils.isEmpty(sdasd)) {
                tvSign.setText(sdasd);
                sign = sdasd;
            } else {
                tvSign.setText("");
            }
        if (userAccount != null) {
            tvId.setText(userAccount);
        } else {
            tvId.setText("");
        }

    }

    private void initView() {
        tvSign = (TextView) findViewById(R.id.person_tv_sign);
        tvName = (TextView) findViewById(R.id.person_tv_name);
        tvGender = (TextView) findViewById(R.id.person_tv_gender);
        tvPhone = (TextView) findViewById(R.id.person_tv_phone);
        circleImageView = (CircleImageView) findViewById(R.id.person_data_icon);
        ivCancle = (ImageView) findViewById(R.id.person_cancle);
        rlIcon = (RelativeLayout) findViewById(R.id.person_rl_icon);
        rlPhone = (RelativeLayout) findViewById(R.id.person_phone);
        rlGender = (RelativeLayout) findViewById(R.id.person_rl_gender);
        rlNickName = (RelativeLayout) findViewById(R.id.person_rl_nick_name);
        rlPersonalSign = (ConstraintLayout) findViewById(R.id.person_rl_personal_sign);
        tvId = (TextView) findViewById(R.id.tv_id);
        ivCancle.setOnClickListener(this);
        rlIcon.setOnClickListener(this);
        rlPhone.setOnClickListener(this);
        rlGender.setOnClickListener(this);
        rlNickName.setOnClickListener(this);

        rlPersonalSign.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                if (data.getBooleanExtra("isChange", false)) {
                    initNetData();
                }
            }
        } else if (requestCode == 22) {
            initNetData();
            if (data != null) {
                isRefresh = data.getBooleanExtra("isRefresh", false);

            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("isRefresh", isRefresh);
        intent.putExtra("url", url);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_cancle:
                Intent canIntent = new Intent();
                canIntent.putExtra("isRefresh", isRefresh);
                canIntent.putExtra("url", url);
                setResult(RESULT_OK, canIntent);
                finish();
                break;
            case R.id.person_rl_icon:
                Intent intent = new Intent();
                intent.setClass(PersonDataActivity.this, PhotoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("imageUrl",url);
                intent.putExtras(bundle);
                startActivityForResult(intent, 22);

                break;
            case R.id.person_phone:
                bundleData(101);
                break;
            case R.id.person_rl_gender:
                bundleData(102);
                break;
            case R.id.person_rl_nick_name:
                bundleData(103);
                break;
            case R.id.person_rl_personal_sign:

                bundleData(104);
                break;
        }
    }

    private void bundleData(int tag) {
        Intent intentPhone = new Intent();
        intentPhone.setClass(PersonDataActivity.this,SynthesizeActivity.class);
        Bundle bundleSimple = new Bundle();
        bundleSimple.putInt("personTag", tag);
        //设置电话
        if (!TextUtils.isEmpty(phone)) {
            bundleSimple.putString("phoneNumber", phone);
        } else {
            bundleSimple.putString("phoneNumber", "请设置电话号码");
        }
        bundleSimple.putString("personPhone", "电话");
        //设置性别
        if (sex == 0) {
            bundleSimple.putString("personGender", "男");
        } else if (sex == 1) {
            bundleSimple.putString("personGender", "女");
        }
        bundleSimple.putString("myGender", "性别");
        //设置昵称
        if (!TextUtils.isEmpty(name)) {
            bundleSimple.putString("personName", name);
        }
        bundleSimple.putString("nickName", "昵称");
        //设置个性签名
        if (!TextUtils.isEmpty(sign) ) {
            bundleSimple.putString("personSign", sign);
        } else {
            bundleSimple.putString("personSign","");
        }
        bundleSimple.putString("mySign", "个性签名");

        intentPhone.putExtras(bundleSimple);
        startActivityForResult(intentPhone, 1);
    }
}
