package com.neworld.youyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.neworld.youyou.R;
import com.neworld.youyou.adapter.AddStudentAdapter;
import com.neworld.youyou.bean.ChildDetailBean;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class AllStudentActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private AddStudentAdapter adapter ;
    private List<ChildDetailBean.ResultsBean> mDatas = new ArrayList<>();
    private ImageView ivCancel;
    private int selection = 0;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_student);
        initUser();
        initView();
        initData();
    }

    private void initUser() {
        userId = Sputil.getString(AllStudentActivity.this, "userId", "");
    }

    private void initData() {
        new Thread(() -> {
            String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\"}").getBytes(), Base64.DEFAULT);
            String replace = base64.replace("\n", "");
            String content = NetManager.getInstance().getContent(replace, "141");
            if (content != null && content.length() > 0) {
                ChildDetailBean childDetailBean = GsonUtil.parseJsonToBean(content, ChildDetailBean.class);
                if (childDetailBean != null && childDetailBean.getStatus() == 0) {
                    List<ChildDetailBean.ResultsBean> results = childDetailBean.getResults();
                    if (results != null && results.size() > 0) {
                        mDatas.addAll(results);
                        Util.uiThread(() -> adapter.notifyDataSetChanged());
                    }
                }
            }
        }).start();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        ivCancel.setOnClickListener(this);
        if (adapter == null) {
            adapter = new AddStudentAdapter(AllStudentActivity.this, mDatas);
            listView.setAdapter(adapter);
        }
        listView.setOnItemClickListener((parent, view, position, id) -> {
            selection = position;
            adapter.setSelectedPosition(position);
            adapter.notifyDataSetChanged();
            saveData();
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                Intent intent = new Intent();
                intent.putExtra("name", "");
                AllStudentActivity.this.setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
    //保存数据返回给订单页面
    private void saveData() {
        Intent intent = getIntent();
        ChildDetailBean.ResultsBean resultsBean = mDatas.get(selection);
        String name = resultsBean.getName();
        intent.putExtra("name", name);
        AllStudentActivity.this.setResult(RESULT_OK, intent);
        AllStudentActivity.this.finish();
    }
}
