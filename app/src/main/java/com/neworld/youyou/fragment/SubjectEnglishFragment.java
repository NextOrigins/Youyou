package com.neworld.youyou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.neworld.youyou.R;
import com.neworld.youyou.activity.SubjectActivity;
import com.neworld.youyou.adapter.SubjectChineseAdapter;
import com.neworld.youyou.bean.SubjectBean;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.pulltorefresh.PullToRefreshBase;
import com.neworld.youyou.pulltorefresh.PullToRefreshListView;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tt on 2017/7/18.
 */

public class SubjectEnglishFragment extends BaseFragment {

    private PullToRefreshListView prlv;
    private ListView lv;
    private List<SubjectBean.MenuListBean> mDatas = new ArrayList<>();
    private SubjectChineseAdapter subjectChineseAdapter;
    private List<SubjectBean.MenuListBean> menuList;

    @Override
    public View createView() {
        View view = View.inflate(context, R.layout.subject_english, null);
        prlv = (PullToRefreshListView) view.findViewById(R.id.prlv);
        initView();
        return view;
    }

    private void initView() {
        //设置下拉刷新与加载更多可用
        prlv.setPullRefreshEnabled(true);
        prlv.setPullLoadEnabled(true);
        prlv.setScrollLoadEnabled(false);

        lv = prlv.getRefreshableView();
        initAdapter();
        prlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //Toast.makeText(context, "没有下拉刷新的新数据了", Toast.LENGTH_SHORT).show();
                //prlv.onPullDownRefreshComplete();
                mDatas.clear();
                refreshData();
                prlv.onPullUpRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //Toast.makeText(context, "没有加载更多的新数据了", Toast.LENGTH_SHORT).show();

                if (mDatas.size() >= 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDatas != null && mDatas.size() > 0) {
                                SubjectBean.MenuListBean menuListBean = mDatas.get(mDatas.size() - 1);
                                String createDate = menuListBean.getUpdateDate();
                                if (!TextUtils.isEmpty(createDate)) {
                                    String nmsl = Base64.encodeToString(("{\"type\":\"1\", \"CreateDate\":\"" + createDate + "\"}").getBytes(), Base64.DEFAULT);
                                    String wsnd = nmsl.replace("\n", "");
                                    String content = NetManager.getInstance().getContent(wsnd, "136");
                                    if (content != null && content.length() > 0) {
                                        SubjectBean subjectBean = GsonUtil.parseJsonToBean(content, SubjectBean.class);
                                        if (subjectBean != null) {
                                            if (subjectBean.getStatus() == 0) {
                                                List<SubjectBean.MenuListBean> menuList = subjectBean.getMenuList();
                                                mDatas.addAll(menuList);
                                                Util.uiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        subjectChineseAdapter.notifyDataSetChanged();
                                                        //TODO 提示用户最后一条数据
                                                    }
                                                });
                                            } else {
                                                ToastUtil.showToast("没有更多的数据");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }).start();
                    prlv.onPullUpRefreshComplete();
                } else {
                    ToastUtil.showToast("请添加关注");
                }
            }
        });

    }

    @Override
    public Object getData() {
        String base64 = Base64.encodeToString(("{\"type\":\"1\", \"CreateDate\":\"\"}").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        String content = NetManager.getInstance().getContent(replace, "136");
        if (content != null && content.length() > 0) {
            SubjectBean subjectBean = GsonUtil.parseJsonToBean(content, SubjectBean.class);
            if (subjectBean != null && subjectBean.getStatus() == 0) {
                menuList = subjectBean.getMenuList();
                if (menuList != null && menuList.size() > 0) {
                    mDatas.addAll(menuList);
                    Util.uiThread(new Runnable() {
                        @Override
                        public void run() {
                            subjectChineseAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }
        return menuList;
    }

    protected void initAdapter() {
        if (subjectChineseAdapter == null) {
            subjectChineseAdapter = new SubjectChineseAdapter(context, mDatas);
            lv.setAdapter(subjectChineseAdapter);
        } else {
            subjectChineseAdapter.notifyDataSetChanged();
        }

        setItemClick();


     /*   subjectChineseAdapter.setOnRefreshClick(new SubjectChineseAdapter.OnRefreshClick() {
            @Override
            public void onItemClick(int position) {

            }
        });*/
    }

    private AdapterView.OnItemClickListener l = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SubjectBean.MenuListBean menuListBean = mDatas.get(position);

            if (menuListBean != null) {
                int subjectId = menuListBean.getId();
                Intent intent = new Intent();
                intent.setClass(context, SubjectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("subjectId", subjectId);
                intent.putExtras(bundle);
                startActivity(intent);
                lv.setOnItemClickListener(null);
                new Handler().postDelayed(() -> setItemClick(), 800);
            }
        }
    };

    protected void setItemClick() {
        lv.setOnItemClickListener(l);
    }
}
