package com.neworld.youyou.fragment;

import android.content.Intent;
import android.util.Base64;
import android.view.View;
import android.widget.ListView;

import com.neworld.youyou.MainActivity;
import com.neworld.youyou.R;
import com.neworld.youyou.activity.LoginActivity;
import com.neworld.youyou.adapter.HotEveryAdapter;
import com.neworld.youyou.bean.HotBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.MyApplication;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.pulltorefresh.PullToRefreshBase;
import com.neworld.youyou.pulltorefresh.PullToRefreshListView;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tt on 2017/7/18.
 */

public class HotEveryFragment extends BaseFragment {
    private PullToRefreshListView ptrlvPri;
    private List<HotBean.MenuListBean> mData = new ArrayList<>();
    private HotEveryAdapter hotEveryAdapter;
    private ListView refreshableView;
    String userId;
    private String token;
    private MainActivity activity;

    @Override
    public View createView() {
        View view = View.inflate(context, R.layout.hot_every, null);
        initUser();
        ptrlvPri = (PullToRefreshListView) view.findViewById(R.id.ptrlv_pri);
        //设置下拉刷新与加载更多可用
        ptrlvPri.setPullRefreshEnabled(true);
        ptrlvPri.setPullLoadEnabled(true);
        ptrlvPri.setScrollLoadEnabled(false);
        refreshableView = ptrlvPri.getRefreshableView();
        hotEveryAdapter = initAdapter(hotEveryAdapter, new HotEveryAdapter(context, mData));
//        hotEveryAdapter = initAdapter(hotEveryAdapter);
        ptrlvPri.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            //下拉刷新操作
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                new Thread(() -> {
                    String base64 = Base64.encodeToString(("{\"createDate\": \"\",\"type\": \"0\",\"userId\": \"" + userId + "\", \"token\":\"" + token + "\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "131");
                    if (content != null && content.length() > 0) {
                        HotBean hotBean = GsonUtil.parseJsonToBean(content, HotBean.class);
                        if (hotBean != null && hotBean.getStatus() == 0) {
                            int tokenStatus = hotBean.getTokenStatus();
                            judgeToken(tokenStatus);
                            List<HotBean.MenuListBean> menuList = hotBean.getMenuList();
                            if (menuList != null) {
                                mData.clear();
                                mData.addAll(menuList);
                                Util.uiThread(() -> hotEveryAdapter.notifyDataSetChanged());
                            }
                        }
                    }
                }).start();
                ptrlvPri.onPullDownRefreshComplete();
            }

            //加载更多操作
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mData != null && mData.size() > 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String createDate = mData.get(mData.size() - 1).getCreateDate();
                            if (createDate != null && createDate.length() > 0) {
                                String base64 = Base64.encodeToString(("{\"createDate\": \"" + createDate + "\",\"type\": \"0\",\"userId\": \"" + userId + "\", \"token\":\"" + token + "\"}").getBytes(), Base64.DEFAULT);
                                String replace = base64.replace("\n", "");
                                String content = NetManager.getInstance().getContent(replace, "131");
                                if (content != null && content.length() > 0) {
                                    HotBean hotBean = GsonUtil.parseJsonToBean(content, HotBean.class);
                                    if (hotBean != null && hotBean.getStatus() == 0) {
                                        int tokenStatus = hotBean.getTokenStatus();
//                                        judgeToken(tokenStatus);
                                        List<HotBean.MenuListBean> menuList = hotBean.getMenuList();
                                        if (menuList != null && menuList.size() > 0) {
                                            mData.addAll(menuList);
                                            Util.uiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    hotEveryAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            ToastUtil.showToast("没有更多的数据");
                                        }
                                    }
                                }
                            }
                        }
                    }).start();
                    ptrlvPri.onPullUpRefreshComplete();
                }
            }
        });
        return view;
    }

    private void initUser() {
        userId = Sputil.getString(context, "userId", "");
        token = Sputil.getString(context, "token", "");
        activity = ((MainActivity) getActivity());
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
                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "152");
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus != null && returnStatus.getStatus() == 0) {
                        Sputil.saveString(context, "userId", "");
                        startActivity(new Intent(context, LoginActivity.class).putExtra("login2", true));
                        MyApplication mainApplication = activity.getMainApplication();
                        mainApplication.removeALLActivity_();
                    }
                }
            }
        }).start();
    }

    @Override
    public Object getData() {
        List<HotBean.MenuListBean> menuList = null;
        String base64 = Base64.encodeToString(("{\"createDate\": \"\",\"type\": \"0\",\"userId\": \"" + userId + "\", \"token\":\"" + token + "\"}").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        String content = NetManager.getInstance().getContent(replace, "131");
        if (content != null && content.length() > 0) {
            HotBean hotBean = GsonUtil.parseJsonToBean(content, HotBean.class);
            if (hotBean != null && hotBean.getStatus() == 0) {
                int tokenStatus = hotBean.getTokenStatus();
                judgeToken(tokenStatus);
                menuList = hotBean.getMenuList();
                if (menuList != null) {
                    mData.clear();
                    mData.addAll(menuList);
                    Util.uiThread(() -> hotEveryAdapter.notifyDataSetChanged());
                }
            }
        }
        return menuList;
    }

    @Override
    protected List<HotBean.MenuListBean> getListBean() {
        return mData;
    }

    @Override
    protected ListView getListView() {
        return refreshableView;
    }
}
