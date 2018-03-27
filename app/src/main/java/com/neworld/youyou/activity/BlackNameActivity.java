package com.neworld.youyou.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.neworld.youyou.R;
import com.neworld.youyou.adapter.BlackNameAdapter;
import com.neworld.youyou.bean.BlackNameBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.SPUtil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class BlackNameActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private ImageView ivCancel;
    List<BlackNameBean.MenuListBean> mDatas = new ArrayList<>();
    private BlackNameAdapter blackNameAdapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_name);
        initUser();
        initView();
        initData();
    }
    private void initUser() {
        userId = SPUtil.getString(BlackNameActivity.this, "userId", "");
    }
    //164
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "164");
                if (content != null && content.length() > 0) {
                    final BlackNameBean blackNameBean = GsonUtil.parseJsonToBean(content, BlackNameBean.class);
                    if (blackNameBean != null) {
                        if (blackNameBean.getStatus() == 0) {
                            List<BlackNameBean.MenuListBean> menuList = blackNameBean.getMenuList();
                            if (menuList != null && menuList.size() > 0) {
                                mDatas.addAll(menuList);
                                Util.uiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        blackNameAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        } else {
                            ToastUtil.showToast("没有接受到数据");
                        }
                    } else {
                        ToastUtil.showToast("未知错误");
                    }
                }
            }
        }).start();
    }

    private void refresh() {
        if (blackNameAdapter == null) {
            blackNameAdapter = new BlackNameAdapter(BlackNameActivity.this, mDatas);
            listView.setAdapter(blackNameAdapter);
        } else {
            blackNameAdapter.notifyDataSetChanged();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder ad=new AlertDialog.Builder(BlackNameActivity.this);
                ad.setMessage("确定移除黑名单吗");
                ad.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               deleteName(position);
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
        });
    }
    //删除好友接口
    private void deleteName(final int position) {
     /*   mDatas.remove(position);
        blackNameAdapter.notifyDataSetChanged();*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                BlackNameBean.MenuListBean menuListBean = mDatas.get(position);
                int from_uid = menuListBean.getTaruId();
                String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\",\"target_uid\":\""+from_uid+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "150");
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus != null && returnStatus.getStatus() == 0) {
                        mDatas.remove(position);
                        Util.uiThread(new Runnable() {
                            @Override
                            public void run() {
                                blackNameAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        ivCancel.setOnClickListener(this);
        refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
        }
    }
}
