package com.neworld.youyou.fragment;

import android.util.Base64;
import android.view.View;
import android.widget.ListView;

import com.neworld.youyou.R;
import com.neworld.youyou.adapter.HotEveryAdapter;
import com.neworld.youyou.bean.HotBean;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.pulltorefresh.PullToRefreshBase;
import com.neworld.youyou.pulltorefresh.PullToRefreshListView;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tt on 2017/7/18.
 */

public class HotLifeFragment extends BaseFragment {
    private PullToRefreshListView ptrlvPri;
    private List<HotBean.MenuListBean> mData= new ArrayList<>();
    private HotEveryAdapter hotEveryAdapter;
    private ListView refreshableView;
    private String userId;
    private List<HotBean.MenuListBean> menuList;

    @Override
    public View createView() {
        View view = View.inflate(context, R.layout.hot_life, null);
        ptrlvPri = (PullToRefreshListView) view.findViewById(R.id.ptrlv_pri);
        initUser();
        //设置下拉刷新与加载更多可用
        ptrlvPri.setPullRefreshEnabled(true);
        ptrlvPri.setPullLoadEnabled(true);
        ptrlvPri.setScrollLoadEnabled(false);
        refreshableView = ptrlvPri.getRefreshableView();
        hotEveryAdapter = initAdapter(hotEveryAdapter, new HotEveryAdapter(context, getListBean()));
//        hotEveryAdapter = initAdapter(hotEveryAdapter);
        ptrlvPri.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            //下拉刷新操作
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getData();
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
                                String base64 = Base64.encodeToString(("{\"createDate\": \""+createDate+"\",\"type\": \"3\",\"userId\": \""+userId+"\"}").getBytes(), Base64.DEFAULT);
                                String replace = base64.replace("\n", "");
                                String content = NetManager.getInstance().getContent(replace, "131");
                                if (content != null && content.length() > 0) {
                                    HotBean hotBean = GsonUtil.parseJsonToBean(content, HotBean.class);
                                    if (hotBean != null && hotBean.getStatus() == 0) {
                                        menuList = hotBean.getMenuList();
                                        if (menuList != null ) {
                                            mData.addAll(menuList);
                                            Util.uiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    hotEveryAdapter.notifyDataSetChanged();
                                                }
                                            });
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
    }

    @Override
    public Object getData() {
        String base64 = Base64.encodeToString(("{\"createDate\": \"\",\"type\": \"3\",\"userId\": \""+userId+"\"}").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        String content = NetManager.getInstance().getContent(replace, "131");
        if (content != null && content.length() > 0) {
            HotBean hotBean = GsonUtil.parseJsonToBean(content, HotBean.class);
            if (hotBean != null && hotBean.getStatus() == 0) {
                menuList = hotBean.getMenuList();
                if (menuList != null) {
                    mData.clear();
                    mData.addAll(menuList);
                    Util.uiThread(new Runnable() {
                        @Override
                        public void run() {
                            hotEveryAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }
        return menuList;
    }

//    private void initAdapter() {
//        if (hotEveryAdapter == null) {
//            hotEveryAdapter = new HotEveryAdapter(context, mData);
//            refreshableView.setAdapter(hotEveryAdapter);
//        } else {
//            hotEveryAdapter.notifyDataSetChanged();
//        }
//
//
//        refreshableView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                HotBean.MenuListBean menuListBean = mData.get(position);
//                if (menuListBean != null) {
//                    int taskId = menuListBean.getId();
//                    Intent intent = new Intent();
//                    intent.setClass(context, HotActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("taskId", taskId);
//                    int from_uid = menuListBean.getFrom_uid();
//                    bundle.putInt("from_uid", from_uid);
//
//                    String faceImg = menuListBean.getFaceImg();
//                    if (faceImg != null && faceImg.length() > 0) {
//                        bundle.putString("imgs", faceImg);
//                    }
//                    String title = menuListBean.getTitle();
//                    if (title != null && title.length() > 0) {
//                        bundle.putString("title", title);
//                    }
//                    String content = menuListBean.getContent();
//                    if (content != null && content.length() > 0) {
//                        bundle.putString("content", content);
//                    }
//
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
//            }
//        });
//    }

    @Override
    protected List<HotBean.MenuListBean> getListBean() {
        return mData;
    }

    @Override
    protected ListView getListView() {
        return refreshableView;
    }
}
