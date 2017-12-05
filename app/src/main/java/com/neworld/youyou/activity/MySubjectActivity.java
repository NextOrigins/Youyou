package com.neworld.youyou.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.adapter.MySubjectAdapter;
import com.neworld.youyou.bean.MySubjectBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class MySubjectActivity extends AppCompatActivity implements View.OnClickListener {

    public ListView lvSubject;
    private ImageView ivCancel;
    private TextView tvCompile;
    public  List<MySubjectBean.MenuListBean> mData = new ArrayList<>();
    private MySubjectAdapter mySubjectAdapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subject);
        initUser();
        initView();
        initData();
    }
    private void initUser() {
        userId = Sputil.getString(MySubjectActivity.this, "userId", "");
    }
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "146");
                if (content != null && content.length() > 0) {
                    MySubjectBean mySubjectBean = GsonUtil.parseJsonToBean(content, MySubjectBean.class);
                    if (mySubjectBean != null && mySubjectBean.getStatus() == 0) {
                        List<MySubjectBean.MenuListBean> menuList = mySubjectBean.getMenuList();
                        mData.addAll(menuList);
                        Util.uiThread(new Runnable() {
                            @Override
                            public void run() {
                                mySubjectAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void initView() {
        lvSubject = (ListView) findViewById(R.id.my_subject_lv);
        ivCancel = (ImageView) findViewById(R.id.my_subject_cancel);
        tvCompile = (TextView) findViewById(R.id.add_compile_save);
        ivCancel.setOnClickListener(this);
        if (mySubjectAdapter == null) {
            mySubjectAdapter = new MySubjectAdapter(MySubjectActivity.this, mData);
            lvSubject.setAdapter(mySubjectAdapter);
        } else {
            mySubjectAdapter.notifyDataSetChanged();
        }

        mySubjectAdapter.setOnSubject(new MySubjectAdapter.OnSubject() {
            @Override
            public void onDelete(final int position) {
                MySubjectBean.MenuListBean menuListBean = mData.get(position);
                final int orderId = menuListBean.getOrderId();
                if (orderId != 0) {

                    final AlertDialog.Builder ad=new AlertDialog.Builder(MySubjectActivity.this);
                    ad.setMessage("是否删除考证");
                    ad.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteSubject(orderId, position);
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
            }

            @Override
            public void onItem(int position) {
                MySubjectBean.MenuListBean menuListBean = mData.get(position);
                int payStatus = menuListBean.getPayStatus();
                if (payStatus == 0) {
                    //待支付
                    pay(position);
                }else {
                    //已经支付
                    orderPay(position);
                }
            }

            @Override
            public void onTest(int position) {
                Intent intent = new Intent();
                MySubjectBean.MenuListBean menuListBean = mData.get(position);
                String ccaaUrl = menuListBean.getCcaaUrl();
                intent.setClass(MySubjectActivity.this, TestActivity.class);
                Bundle bundle = new Bundle();
                if (ccaaUrl != null ) {
                    bundle.putString("ccaaUrl", ccaaUrl);
                }
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onPreTest(int position) {
                MySubjectBean.MenuListBean menuListBean = mData.get(position);
                String admissionUrl = menuListBean.getAdmissionUrl();
                final AlertDialog.Builder ad=new AlertDialog.Builder(MySubjectActivity.this);
                ad.setMessage(admissionUrl);
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
            public void onResult(int position) {
                MySubjectBean.MenuListBean menuListBean = mData.get(position);
                String queryResultUrl = menuListBean.getQueryResultUrl();
                final AlertDialog.Builder ad=new AlertDialog.Builder(MySubjectActivity.this);
                ad.setMessage(queryResultUrl);
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
    //已经支付
    private void orderPay(int position) {
        MySubjectBean.MenuListBean menuListBean = mData.get(position);
        int id = menuListBean.getId();
        Intent intent = new Intent();
        intent.setClass(MySubjectActivity.this, SubjectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("subjectId", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //待支付
    private void pay(int position) {
        MySubjectBean.MenuListBean menuListBean = mData.get(position);
        int subjectId = menuListBean.getId();
        int typeId = menuListBean.getTypeId();
        int orderId = menuListBean.getOrderId();
        String examinee_name = menuListBean.getExaminee_name();
        Intent intent = new Intent();
        intent.setClass(MySubjectActivity.this, PaymentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("orderId", orderId);
        bundle.putInt("subjectId", subjectId);
        bundle.putInt("typeId", typeId);
        bundle.putString("examinee_name", examinee_name);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //删除订单
    private void deleteSubject(final int orderId, final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"orderId\":\""+orderId+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "148");
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus != null && returnStatus.getStatus() == 0) {
                        Util.uiThread(new Runnable() {
                            @Override
                            public void run() {
                                mData.remove(position);
                                mySubjectAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_subject_cancel:
                finish();
                break;
        }
    }
}
