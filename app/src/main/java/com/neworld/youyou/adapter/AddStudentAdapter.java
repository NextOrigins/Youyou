package com.neworld.youyou.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.bean.ChildDetailBean;

import java.util.List;

/**
 * Created by tt on 2017/8/15.
 */

public class AddStudentAdapter extends BaseAdapter {
    private Context context;
    private int selectedPosition = -1;
    private List<ChildDetailBean.ResultsBean> mDatas;
    public AddStudentAdapter(Context addChildActivity, List<ChildDetailBean.ResultsBean> mDatas) {
        this.context = addChildActivity;
        this.mDatas = mDatas;
    }

    public void  setSelectedPosition(int position) {
        selectedPosition = position;
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
            convertView = View.inflate(context, R.layout.item_all_student, null);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_dui);
            convertView.setTag(holder);
        } else {
            holder = (AddChildHolder) convertView.getTag();
        }
        ChildDetailBean.ResultsBean resultsBean = mDatas.get(position);
        String name = resultsBean.getName();
        if (name != null) {
            holder.tvName.setText(name);
        }
        if (position % 2 == 0) {
            if (selectedPosition == position) {
                convertView.setSelected(true);
                convertView.setPressed(true);
                holder.imageView.setVisibility(View.VISIBLE);
            } else {
                convertView.setSelected(false);
                convertView.setPressed(false);
                holder.imageView.setVisibility(View.GONE);
            }
        } else {
            if (selectedPosition == position) {
                convertView.setSelected(true);
                convertView.setPressed(true);
                holder.imageView.setVisibility(View.VISIBLE);
            } else {
                convertView.setSelected(false);
                convertView.setPressed(false);
                holder.imageView.setVisibility(View.GONE);
            }
        }



        return convertView;
    }

   static class AddChildHolder{
        public TextView tvName;
       public ImageView imageView;
    }
}
