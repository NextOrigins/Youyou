package com.neworld.youyou.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.bean.FocusBean;

import java.util.ArrayList;

/**
 * Created by tt on 2017/8/4.
 */

public class NoFocusAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FocusBean.MenuListBean> newList;

    public NoFocusAdapter(Context focusActivity, ArrayList<FocusBean.MenuListBean> allList) {
        this.context = focusActivity;
        this.newList = allList;
    }

    @Override
    public int getCount() {
        return newList == null ? 0 : newList.size();
    }

    @Override
    public Object getItem(int position) {

        return newList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ContentNoViewHolder holder1 = null;
        if (convertView == null) {
            holder1 = new ContentNoViewHolder();
            convertView = View.inflate(context, R.layout.item_focus_content, null);
            holder1.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder1.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder1.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            holder1.ivAdd = (ImageView) convertView.findViewById(R.id.iv_add);
            convertView.setTag(holder1);
        } else {
            holder1 = (ContentNoViewHolder) convertView.getTag();
        }
        if (newList != null && newList.size() > 0 && position > 0) {
            showItemData(newList, holder1, position);
        }
        holder1.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNoFocusClick.onItemAdd(position);
            }
        });

        return convertView;

    }

    private void showItemData(ArrayList<FocusBean.MenuListBean> newList, ContentNoViewHolder holder1, int position) {
        FocusBean.MenuListBean menuListBean = newList.get(position);
        //设置标题
        if (menuListBean != null) {


            if (menuListBean != null) {
                String title = menuListBean.getTypeName();
                if (!TextUtils.isEmpty(title)) {
                    holder1.tvContent.setText(title);
                } else {
                    holder1.tvContent.setText("");
                }
            } else {
                holder1.tvContent.setText("");
            }
            //设置图像
            String typeImg = menuListBean.getTypeImg();
            if (!TextUtils.isEmpty(typeImg)) {
                Glide.with(context).load(typeImg).into(holder1.ivIcon);
            }
            //设置类型
            String typeName = menuListBean.getTypeName();
            if (!TextUtils.isEmpty(typeName)) {
                holder1.tvTitle.setText(typeName);
            }
        }
    }

    static class ContentNoViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvContent;
        public ImageView ivAdd;
    }

    public interface OnNoFocusClick {
        void onItemAdd(int positon);
    }

    private OnNoFocusClick onNoFocusClick;

    public void setOnFocusClick(OnNoFocusClick onFocusClick) {
        this.onNoFocusClick = onFocusClick;
    }
}
