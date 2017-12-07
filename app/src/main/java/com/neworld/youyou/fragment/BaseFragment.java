package com.neworld.youyou.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.neworld.youyou.activity.HotActivity;
import com.neworld.youyou.adapter.HotEveryAdapter;
import com.neworld.youyou.bean.HotBean;
import com.neworld.youyou.view.LoadPager;

import java.util.List;

public abstract class BaseFragment extends Fragment {
    private LoadPager mLoadPager;
    public Context context;
    public FragmentActivity mActivity;
    protected View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        //得到数据的方法
        if (mLoadPager == null) {   //解决ViewPager复用
            mLoadPager = new LoadPager(getContext()) {
                @Override
                public Object getNetData() { //得到网络数据
                    return getData();
                }

                @Override
                public View createSuccessView() { //创建成功的页面
                    return createView();
                }
            };
        }
        return mLoadPager; //返回加载的页面
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        root = view;
    }

    //返回界面
    public abstract View createView();

    //返回数据
    public abstract Object getData();

    //刷新数据的方法
    public void refreshData() {
        mLoadPager.showPage();
    }

    protected void initAdapter() {}

    protected <T extends HotEveryAdapter> T initAdapter(T t, T t2) {
        ListView listView = getListView(); // TODO : 进一步封装
        if (t == null) {
            t = t2;
            listView.setAdapter(t);
        } else {
            t.notifyDataSetChanged();
        }

        listView.setOnItemClickListener(l);
        return t;
    }

    private AdapterView.OnItemClickListener l = (parent, view, position, id) -> {
            HotBean.MenuListBean menuListBean = getListBean().get(position);
            if (menuListBean != null) {
                int taskId = menuListBean.getId();
                Intent intent = new Intent();
                intent.setClass(context, HotActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("taskId", taskId);
                int from_uid = menuListBean.getFrom_uid();
                bundle.putInt("from_uid", from_uid);

                String faceImg = menuListBean.getFaceImg();
                if (faceImg != null && faceImg.length() > 0) {
                    bundle.putString("imgs", faceImg);
                }
                String title = menuListBean.getTitle();
                if (title != null && title.length() > 0) {
                    bundle.putString("title", title);
                }
                String content = menuListBean.getContent();
                if (content != null && content.length() > 0) {
                    bundle.putString("content", content);
                }

                intent.putExtras(bundle);
                startActivity(intent);
                getListView().setOnItemClickListener(null);
                new Handler().postDelayed(this::setItemClick, 1800);
            }
    };

    protected void setItemClick() {
        getListView().setOnItemClickListener(l);
    }

    protected List<HotBean.MenuListBean> getListBean() {
        return null;
    }
    protected ListView getListView() {
        return null;
    }
}
