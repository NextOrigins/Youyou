package com.neworld.youyou.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.neworld.youyou.R;
import com.neworld.youyou.activity.MySubjectActivity;
import com.neworld.youyou.adapter.SubjectAdapter;
import com.neworld.youyou.adapter.SubjectInfo;
import com.neworld.youyou.select.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tt on 2017/7/18.
 */

public class SubjectFragment extends BaseFragment implements View.OnClickListener {

    private String[] subjectTtiles = {"全部", "英语", "语文", "数学", "才艺"};
    private TabLayout tableLayout;
    private ViewPager viewPager;
    private List<SubjectInfo> mShowItems = new ArrayList<>();
    private View view;
    private ImageView ivFav;

    private void initView() {
        tableLayout = (TabLayout) view.findViewById(R.id.tl_subject);
        viewPager = (ViewPager) view.findViewById(R.id.vp_subject);
        ivFav = ((ImageView) view.findViewById(R.id.iv_fav));
        ivFav.setOnClickListener(this);
        mShowItems.add(new SubjectInfo(subjectTtiles[0], new SubjectOtherFragment()));
        mShowItems.add(new SubjectInfo(subjectTtiles[1], new SubjectEnglishFragment())); //
        mShowItems.add(new SubjectInfo(subjectTtiles[2], new SubjectChineseFragment())); //
        mShowItems.add(new SubjectInfo(subjectTtiles[3], new SubjectMathFragment()));
        mShowItems.add(new SubjectInfo(subjectTtiles[4], new SubjectActFragment()));

        //初始化tablayout
        viewPager.setAdapter(new SubjectAdapter(getChildFragmentManager(), mShowItems));

        tableLayout.setupWithViewPager(viewPager);

        //设置颜色
        tableLayout.setTabTextColors(Color.parseColor("#161615"), Color.parseColor("#f85a5a"));

        //设置指示器的颜色
        //tableLayout.setSelectedTabIndicatorColor(Color.parseColor("#ff0000"));

    }

    @Override
    public View createView() {
        view = View.inflate(context, R.layout.fragment_subject, null);
        initView();
        return view;
    }

    @Override
    public Object getData() {

        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_fav:
                startActivity(new Intent(context, MySubjectActivity.class));
                break;
        }
    }
}
