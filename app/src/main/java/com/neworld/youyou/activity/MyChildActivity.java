package com.neworld.youyou.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.neworld.youyou.R;

public class MyChildActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btChild;
    private ImageView ivCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_child);
        initView();
    }

    private void initView() {
        btChild = (Button) findViewById(R.id.bt_my_child);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        btChild.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_my_child:
                startActivity(new Intent(this, AddChildActivity.class));

                break;
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
