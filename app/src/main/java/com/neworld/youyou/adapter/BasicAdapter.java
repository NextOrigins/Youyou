package com.neworld.youyou.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.neworld.youyou.holder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tt on 2017/3/26.
 */

public abstract class BasicAdapter<T> extends BaseAdapter {

    private List<T> mList  = new ArrayList<>();

    public BasicAdapter(List<T> mList) {
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder<T> holder = null;
        if (convertView == null) {
            holder = createViewHolder(position);
        } else {
            holder = (BaseViewHolder<T>) convertView.getTag();
        }
        //数据绑定
        holder.bindView(mList.get(position));
        return holder.getView();
    }

    public abstract BaseViewHolder<T> createViewHolder(int position);
}
