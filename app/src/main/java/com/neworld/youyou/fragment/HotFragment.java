package com.neworld.youyou.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.neworld.youyou.R;
import com.neworld.youyou.activity.CollectDetailActivity;
import com.neworld.youyou.adapter.HotAdapter;
import com.neworld.youyou.adapter.HotInfo;
import com.neworld.youyou.bean.HotBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tt on 2017/7/18.
 */

public class HotFragment extends BaseFragment implements View.OnClickListener {

    private TabLayout tableLayout;
    private ViewPager viewPager;
    private String[] subjectTtiles = {"全部", "幼升小", "小升初", "中考", "资讯", "育儿", "生活", "学习资料"};
    private List<HotInfo> mShowItems = new ArrayList<>();
    private HotAdapter hotAdapter;
    private View view;
    private ImageView ivHot;

    private void initData() {
        tableLayout = (TabLayout) view.findViewById(R.id.tl_hot);
        viewPager = (ViewPager) view.findViewById(R.id.vp_hot);
        ivHot = ((ImageView) view.findViewById(R.id.iv_hot));
        ivHot.setOnClickListener(this);
        if (mShowItems != null) {
            mShowItems.clear();
            mShowItems.add(new HotInfo(subjectTtiles[0], new HotEveryFragment()));//全部
            mShowItems.add(new HotInfo(subjectTtiles[1], new HotYoungFragment()));//幼升小
            mShowItems.add(new HotInfo(subjectTtiles[2], new HotPrimaryFragment()));//小升初
            mShowItems.add(new HotInfo(subjectTtiles[3], new HotMiddleFragment()));//中考
            mShowItems.add(new HotInfo(subjectTtiles[4], new HotNewsFragment()));//资讯
            mShowItems.add(new HotInfo(subjectTtiles[5], new HotChildFragment()));//育儿
            mShowItems.add(new HotInfo(subjectTtiles[6], new HotLifeFragment()));//生活
            mShowItems.add(new HotInfo(subjectTtiles[7], new HotStudyFragment()));//学习资料
        }
        //初始化tablayout
        hotAdapter = new HotAdapter(getChildFragmentManager(), mShowItems);

        viewPager.setAdapter(hotAdapter);

        tableLayout.setupWithViewPager(viewPager);
        //设置颜色
        tableLayout.setTabTextColors(Color.parseColor("#161615"), Color.parseColor("#f85a5a"));
    }

    @Override
    public View createView() {
        view = View.inflate(context, R.layout.fragment_hot, null);
        initData();
        return view;
    }

    @Override
    public Object getData() {
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_hot:
                Intent hotintent = new Intent();
                hotintent.setClass(context, CollectDetailActivity.class);
                Bundle hotbundle = new Bundle();
                hotbundle.putString("collect", "热文收藏");
                hotintent.putExtras(hotbundle);
                startActivity(hotintent);
                break;
        }
    }
}
