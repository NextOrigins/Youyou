package com.neworld.youyou.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.neworld.youyou.R;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.view.mview.common.QACollectActivity;

public class MyCollectActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivCancel;
    private RelativeLayout rlQuan;
    private RelativeLayout rlHot;
//    private RelativeLayout rlSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        initView();
    }

    private void initView() {
        ivCancel = (ImageView) findViewById(R.id.iv_cancle);
        rlQuan = (RelativeLayout) findViewById(R.id.rl_quan);
        rlHot = (RelativeLayout) findViewById(R.id.rl_hot);
//        rlSubject = (RelativeLayout) findViewById(R.id.rl_subject);
        ivCancel.setOnClickListener(this);
        rlQuan.setOnClickListener(this);
        rlHot.setOnClickListener(this);
//        rlSubject.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancle:
                finish();
                break;
            case R.id.rl_quan:
//                Intent intent = new Intent();
//                intent.setClass(MyCollectActivity.this, CollectDetailActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("collect", "圈子收藏");
//                intent.putExtras(bundle);
//                startActivity(intent);
                startActivity(new Intent(getBaseContext(), QACollectActivity.class));
                break;
            case R.id.rl_hot:
                Intent hotintent = new Intent();
                hotintent.setClass(MyCollectActivity.this, CollectDetailActivity.class);
                Bundle hotbundle = new Bundle();
                hotbundle.putString("collect", "热文收藏");
                hotintent.putExtras(hotbundle);
                startActivity(hotintent);
                break;
//            case R.id.rl_subject:
//                Intent subjectIntent = new Intent();
//                subjectIntent.setClass(MyCollectActivity.this, CollectDetailActivity.class);
//                Bundle subjectBundle = new Bundle();
//                subjectBundle.putString("collect", "考证收藏");
//                subjectIntent.putExtras(subjectBundle);
//                startActivity(subjectIntent);
//                break;
        }
    }
}
