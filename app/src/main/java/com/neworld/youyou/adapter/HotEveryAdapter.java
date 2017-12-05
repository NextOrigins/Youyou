package com.neworld.youyou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.bean.HotBean;
import com.neworld.youyou.utils.TimeUtil;

import java.util.List;

/**
 * Created by tt on 2017/7/24.
 */
public class HotEveryAdapter extends BaseAdapter {
    private final Context context;
    private final List<HotBean.MenuListBean> menuList;

    public HotEveryAdapter(Context context, List<HotBean.MenuListBean> parentList) {
        this.context = context;
        this.menuList = parentList;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HotEveryHolder holder = null;
        if (holder == null) {
            holder = new HotEveryHolder();
            convertView = View.inflate(context, R.layout.item_hot_both, null);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tvSplace = (TextView) convertView.findViewById(R.id.place_parent);
            holder.tvComment = (TextView) convertView.findViewById(R.id.tv_comment);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(holder);
        } else {
            holder = (HotEveryHolder) convertView.getTag();
        }

        updateDate(holder, position);
        return convertView;
    }

    private void updateDate(HotEveryHolder holder, int position) {
        if (menuList != null && menuList.size() > 0) {
            HotBean.MenuListBean menuListBean = menuList.get(position);
            //设置图片
            if (menuListBean.getImgs() != null && menuListBean.getImgs().length() > 0) {
                Glide.with(context).load(menuListBean.getImgs()).into(holder.ivIcon);
            }
            //设置标题
            if (menuListBean.getTitle() != null && menuListBean.getTitle().length() > 0) {
                holder.tvContent.setText(menuListBean.getTitle());
            }
            //设置来源
            if (menuListBean.getSource() != null && menuListBean.getSource().length() > 0) {
                holder.tvSplace.setText(menuListBean.getSource());
            }
           /* //设置评论
            holder.tvComment.setText("评论"+menuListBean.getComment_count());*/
            //设置时间

            if (menuListBean.getCreateDate() != null && menuListBean.getCreateDate().length() > 0) {
                String time = TimeUtil.formatDisplayTime(menuListBean.getCreateDate(), "yyyy-MM-dd HH:mm:ss");
                //holder.tvTime.setText(menuListBean.getCreateDate());
               holder.tvTime.setText(time);
            }
        }
    }

    static class HotEveryHolder{
        public TextView tvContent;
        public ImageView ivIcon;
        public TextView tvSplace;
        public TextView tvComment;
        public TextView tvTime;

    }
}
