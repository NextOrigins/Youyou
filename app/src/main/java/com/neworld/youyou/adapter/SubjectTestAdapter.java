package com.neworld.youyou.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.bean.SubjectDetailBean;
import com.neworld.youyou.activity.SubjectActivity;

import java.util.List;

/**
 * Created by tt on 2017/8/18.
 */

public class SubjectTestAdapter extends BaseAdapter {
    private Context context;
    private List<SubjectDetailBean.ListBean> list;
    public SubjectTestAdapter(SubjectActivity subjectActivity, List<SubjectDetailBean.ListBean> list) {
        this.context = subjectActivity;
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
        TestHolder holder = null;
        if (convertView == null) {
            holder = new TestHolder();
            convertView = View.inflate(context, R.layout.item_subject_test, null);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_grade);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            convertView.setTag(holder);
        } else {
            holder = (TestHolder) convertView.getTag();
        }
        SubjectDetailBean.ListBean listBean = list.get(position);
        if (listBean != null) {
            String type_name = listBean.getType_name();
            if (type_name != null && type_name.length() > 0) {
                holder.tvName.setText(type_name);
            }

            double subject_money = listBean.getSubject_money();
            if (subject_money != 0.0) {
                holder.tvPrice.setText("¥" +subject_money);
            }
        }
        return convertView;
    }

    static class  TestHolder {
        public TextView tvName;
        public TextView tvPrice;
    }
}
