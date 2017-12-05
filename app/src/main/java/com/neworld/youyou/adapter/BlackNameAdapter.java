package com.neworld.youyou.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.activity.BlackNameActivity;
import com.neworld.youyou.bean.BlackNameBean;
import com.neworld.youyou.view.nine.CircleImageView;

import java.util.List;

/**
 * Created by tt on 2017/8/9.
 */

public class BlackNameAdapter extends BaseAdapter {
      private Context context;
    private List<BlackNameBean.MenuListBean> mDatas;

    public BlackNameAdapter(Context context, List<BlackNameBean.MenuListBean> mDatas) {
        this.context = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        BlackNameHolder holder = null;
        if (convertView == null) {
            holder = new BlackNameHolder();
            convertView = View.inflate(context, R.layout.item_black_delete, null);

            holder.ivIcon = (CircleImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            //holder.tvDelete = (TextView) convertView.findViewById(R.id.tv_delete);

            convertView.setTag(holder);
        } else {
            holder = (BlackNameHolder) convertView.getTag();
        }
        BlackNameBean.MenuListBean menuListBean = mDatas.get(position);
        if (menuListBean != null) {
            if (menuListBean.getFaceImg() != null && menuListBean.getFaceImg().length() > 0) {
                Glide.with(context).load(menuListBean.getFaceImg()).into(holder.ivIcon);
            }
            if (menuListBean.getNickName() != null && menuListBean.getNickName().length() > 0) {
                holder.tvName.setText(menuListBean.getNickName());
            }
        }
      /*  holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScrollDelete.onDelete(position);
            }
        });*/

        return convertView;
    }

    static class BlackNameHolder{
        public TextView tvName;
        public CircleImageView ivIcon;
        //public TextView tvDelete;
    }

    public interface OnScrollDelete{
        void onDelete(int position);
    }

    public OnScrollDelete onScrollDelete;

    public void setOnScrollDelete(OnScrollDelete onScrollDelete) {
        this.onScrollDelete = onScrollDelete;
    }
}





