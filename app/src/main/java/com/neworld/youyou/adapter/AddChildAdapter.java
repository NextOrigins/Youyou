package com.neworld.youyou.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.activity.AddChildActivity;
import com.neworld.youyou.bean.ChildDetailBean;
import com.neworld.youyou.view.ChildId;

import java.util.List;

/**
 * Created by tt on 2017/8/15.
 */

public class AddChildAdapter extends BaseAdapter {
    private Context context;
    private List<ChildDetailBean.ResultsBean> mDatas;
    public AddChildAdapter(Context addChildActivity, List<ChildDetailBean.ResultsBean> mDatas) {
        this.context = addChildActivity;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AddChildHolder holder = null;
        if (convertView == null) {
            holder = new AddChildHolder();
            convertView = View.inflate(context, R.layout.item_add_child, null);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (AddChildHolder) convertView.getTag();
        }

        ChildDetailBean.ResultsBean resultsBean = mDatas.get(position);
        String name = resultsBean.getName();
        if (name != null) {
            holder.tvName.setText(name);
        }
        return convertView;
    }

   static class AddChildHolder{
        public TextView tvName;
    }
}
