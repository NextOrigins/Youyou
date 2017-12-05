package com.neworld.youyou.adapter;

import android.support.v4.app.Fragment;

/**
 * Created by tt on 2017/7/18.
 */
public class HotInfo {
    //显示的fragment
    public Fragment fragment;
    //标题
    public String title;

    public HotInfo(String title, Fragment fragment) {
        this.title=title;
        this.fragment=fragment;
    }
}
