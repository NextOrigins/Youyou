package com.neworld.youyou.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neworld.youyou.R;

import java.util.List;

/**
 * Created by tt on 2017/7/24.
 */

public class MyOrderAdapter extends BaseAdapter {
    private  Context context;
    private  List list;

    public MyOrderAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubjectCnHolder holder = null;
        if (holder == null) {
            holder = new SubjectCnHolder();
            convertView = View.inflate(context, R.layout.item_my_subject, null);
            holder.ivIcon =  (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvSubject =  (TextView) convertView.findViewById(R.id.item_my_subject);
            holder.tvTime =  (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvPrice =  (TextView) convertView.findViewById(R.id.tv_price);
            holder.tvFinish =  (TextView) convertView.findViewById(R.id.item_my_finish);
            convertView.setTag(holder);
        } else {
            holder = (SubjectCnHolder) convertView.getTag();
        }
        return convertView;
    }

    static class SubjectCnHolder{
        public ImageView ivIcon;
        public TextView tvSubject;
        public TextView tvTime;
        public TextView tvPrice;
        public TextView tvName;
        public TextView tvFinish;
    }
}
