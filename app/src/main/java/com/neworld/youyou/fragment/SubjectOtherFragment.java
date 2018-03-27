package com.neworld.youyou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.neworld.youyou.MainActivity;
import com.neworld.youyou.R;
import com.neworld.youyou.activity.LoginActivity;
import com.neworld.youyou.activity.SubjectActivity;
import com.neworld.youyou.adapter.SubjectChineseAdapter;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.bean.SubjectBean;
import com.neworld.youyou.manager.MyApplication;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.pulltorefresh.PullToRefreshBase;
import com.neworld.youyou.pulltorefresh.PullToRefreshListView;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.SPUtil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tt on 2017/7/18.
 */

public class SubjectOtherFragment extends BaseFragment {

    private PullToRefreshListView prlv;
    private ListView lv;
    private List<SubjectBean.MenuListBean> mDatas = new ArrayList<>();
    private SubjectChineseAdapter subjectChineseAdapter;
    private String userId;
    private String token;
    private MainActivity activity;

    @Override
    public View createView() {
        View view = View.inflate(context, R.layout.subject_other, null);
        activity = ((MainActivity) getActivity());
        initUser();
        prlv = (PullToRefreshListView) view.findViewById(R.id.prlv);
        initView();
        return view;
    }

    private void initUser() {
        userId = SPUtil.getString(context, "userId", "");
        token = SPUtil.getString(context, "token", "");
    }

    //根据token挤掉线
    private void judgeToken(final int tokenStatus) {
        if (tokenStatus == 2) {
            Util.uiThread(new Runnable() {
                @Override
                public void run() {
                    quit();
                }
            });
        }
    }

    private void quit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "152");

                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus != null && returnStatus.getStatus() == 0) {
                        SPUtil.saveString(context, "userId", "");
                        startActivity(new Intent(context, LoginActivity.class).putExtra("login2", true));
                        MyApplication mainApplication = activity.getMainApplication();
                        mainApplication.removeALLActivity_();
                    }
                }
            }
        }).start();
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
                getData();
                prlv.onPullDownRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    new Thread(() -> {
                        if (mDatas != null && mDatas.size() > 0) {
                            SubjectBean.MenuListBean menuListBean = mDatas.get(mDatas.size()-1);
                            String updateDate = menuListBean.getUpdateDate();
                            if (!TextUtils.isEmpty(updateDate)) {
                                String nmsl = Base64.encodeToString(("{\"type\":\"0\", \"CreateDate\":\"" + updateDate + "\", , \"userId\":\""+userId+"\", \"token\":\""+token+"\"}").getBytes(), Base64.DEFAULT);
                                String wsnd = nmsl.replace("\n", "");
                                String content = NetManager.getInstance().getContent(wsnd, "136");
                                if (content != null && content.length() > 0) {
                                    SubjectBean subjectBean = GsonUtil.parseJsonToBean(content, SubjectBean.class);
                                    if (subjectBean != null && subjectBean.getStatus() == 0) {
                                        int tokenStatus = subjectBean.getTokenStatus();
                                        judgeToken(tokenStatus);
                                        List<SubjectBean.MenuListBean> menuList = subjectBean.getMenuList();
                                        if (menuList != null && menuList.size() > 0) {
                                            mDatas.addAll(menuList);
//                                                Util.uiThread(() -> {
//
//                                                    //TODO 提示用户最后一条数据
////                                                        lv.setAdapter(null);
////                                                        lv.setAdapter(subjectChineseAdapter);
//                                                });
                                            getActivity().runOnUiThread(() -> {
                                                subjectChineseAdapter.notifyDataSetChanged();
                                            });
                                        } else {
                                            ToastUtil.showToast("没有更多的数据");
                                        }
                                    }
                                }
                            }
                        }
                    }).start();
                    prlv.onPullUpRefreshComplete();
            }
        });

        setItemClick();
    }

    AdapterView.OnItemClickListener l = (parent, view, position, id) -> {
        SubjectBean.MenuListBean menuListBean = mDatas.get(position);
        if (menuListBean != null ) {
            int subjectId = menuListBean.getId();
            Intent intent = new Intent();
            intent.setClass(context, SubjectActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("subjectId", subjectId);
            intent.putExtras(bundle);
            startActivity(intent);
            lv.setOnItemClickListener(null);
            new Handler().postDelayed(this::setItemClick, 800);
        }
    };

    protected void setItemClick() {
        lv.setOnItemClickListener(l);
    }

    @Override
    public Object getData() {
        String base64 = Base64.encodeToString(("{\"type\":\"0\", \"CreateDate\":\"\", \"userId\":\""+userId+"\", \"token\":\""+token+"\"}").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        String content = NetManager.getInstance().getContent(replace, "136");
        if (content != null && content.length() > 0) {
            SubjectBean subjectBean = GsonUtil.parseJsonToBean(content, SubjectBean.class);
            if (subjectBean != null && subjectBean.getStatus() == 0) {
                int tokenStatus = subjectBean.getTokenStatus();
                judgeToken(tokenStatus);
                List<SubjectBean.MenuListBean> menuList = subjectBean.getMenuList();
                if (menuList != null && menuList.size() > 0) {
                    mDatas.clear();
                    mDatas.addAll(menuList);
                    Util.uiThread(() -> subjectChineseAdapter.notifyDataSetChanged());
                    return menuList;
                } else {
                    return menuList;
                }
            }
        } else {
            return null;
        }
        return null;
    }

    protected void initAdapter() {
       if (subjectChineseAdapter == null) {
           subjectChineseAdapter = new SubjectChineseAdapter(context, mDatas);
           lv.setAdapter(subjectChineseAdapter);
       }

        /*subjectChineseAdapter.setOnRefreshClick(new SubjectChineseAdapter.OnRefreshClick() {
            @Override
            public void onItemClick(int position) {

            }
        });*/
    }
}
