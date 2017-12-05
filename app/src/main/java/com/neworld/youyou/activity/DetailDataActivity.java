package com.neworld.youyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.bean.DetailDataBean;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.Util;

public class DetailDataActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView tvCancle;
    private TextView tvName;
    private ImageView ivIcon;
    private ImageView ivGender;
    private TextView tvId;
    private TextView tvGender;
    private TextView publishCount;
    private TextView tvSign;
    private String userId;
    private RelativeLayout rlPublish;
    private String fromUid;
    private int count;
    private String faceImg;
    private String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_data);
        initUser();
        initView();
    }

    private void initView() {
        tvCancle = (ImageView) findViewById(R.id.tv_cancle);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        ivGender = (ImageView) findViewById(R.id.iv_gender);
        tvId = (TextView) findViewById(R.id.tv_time);
        tvGender = (TextView) findViewById(R.id.tv_gender);
        publishCount = (TextView) findViewById(R.id.publish_count);
        tvSign = (TextView) findViewById(R.id.tv_sign);
        rlPublish = (RelativeLayout) findViewById(R.id.rl_publish);
        tvCancle.setOnClickListener(this);
        rlPublish.setOnClickListener(this);
        getNetData();
    }
    private void initUser() {
        userId = Sputil.getString(DetailDataActivity.this, "userId", "");
    }
    private void getNetData() {
        //ewogICAgInVzZXJJZCI6ICIxMDg1MCIsCiAgICAiY3JlYXRlRGF0ZSI6ICIiCn0=
        //OkhttpNetManager.getInstance().dataGet("http://192.168.1.123:8080/neworld/android/107")
        //String encodedString = Base64.encodeToString("whoislcj".getBytes(), Base64.DEFAULT);

        Bundle extras = getIntent().getExtras();
        fromUid = extras.getString("from_uid");

        String base64 = Base64.encodeToString(("{\"userId\": \""+userId+"\", \"target_uid\":\""+ fromUid +"\"}").getBytes(), Base64.DEFAULT);
        final String replace = base64.replace("\n", "");
        if (replace != null && replace.length() > 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String content = NetManager.getInstance().getContent(replace, "104");
                        if (content != null && content.length() > 0) {
                            Util.uiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initSuccessData(content);
                                }
                            });
                        }
                    }
                }).start();
        }
    }

    private void initSuccessData(String content) {
        if (content != null && content.length() > 0) {
            DetailDataBean detailDataBean = GsonUtil.parseJsonToBean(content, DetailDataBean.class);
            initNetView(detailDataBean);
        }
    }

    private void initNetView(DetailDataBean detailDataBean) {

        if (detailDataBean != null ) {
            DetailDataBean.MenuListBean menuList = detailDataBean.getMenuList();
            //昵称
            nickName = menuList.getNickName();
            if (nickName != null && nickName.length() > 0 ) {
                tvName.setText(nickName);
            }
            //图像
            faceImg = menuList.getFaceImg();
            if (faceImg != null && faceImg.length() > 0) {
                Glide.with(DetailDataActivity.this).load(faceImg).into(ivIcon);
            }

            //性别
            int sex = menuList.getSex();
            if (sex == 0) {
                Glide.with(DetailDataActivity.this).load(R.mipmap.boy).into(ivGender);
                tvGender.setText("男");
            } else if (sex == 1){
                //ivIcon.setImageResource(R.mipmap.girl);
                Glide.with(DetailDataActivity.this).load(R.mipmap.girl).into(ivGender);
                tvGender.setText("女");
            }

            //id
            int id = menuList.getId();
            tvId.setText("ID-"+id);
            //发表文章
            String sdasd = menuList.getSdasd();
            if (sdasd != null && sdasd.length() > 0) {
                tvSign.setText(sdasd);
            }
            count = detailDataBean.getCount();
            publishCount.setText(count +"");

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancle:
                finish();
                break;
            case R.id.rl_publish:
                Intent intent = new Intent();
                intent.setClass(DetailDataActivity.this, PersonDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("from_uid", fromUid);
                bundle.putInt("count", count);
                bundle.putString("icon", faceImg);
                bundle.putString("name", nickName);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
