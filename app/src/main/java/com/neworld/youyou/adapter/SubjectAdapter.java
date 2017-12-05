package com.neworld.youyou.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import java.util.ArrayList;
import java.util.List;


public class SubjectAdapter extends FragmentStatePagerAdapter {

    private List<SubjectInfo> mShowItems = new ArrayList<>();

    public SubjectAdapter(FragmentManager fm, List<SubjectInfo> showItems) {
        super(fm);
        mShowItems = showItems;
    }

    public SubjectAdapter(FragmentManager fm) {
        super(fm);
    }

    //显示的fragment
    @Override
    public Fragment getItem(int position) {
        return mShowItems.get(position).fragment;
    }

    //数据
    @Override
    public int getCount() {
        return mShowItems.size();
    }

    //标题

    @Override
    public CharSequence getPageTitle(int position) {
        return mShowItems.get(position).title;
    }
}
