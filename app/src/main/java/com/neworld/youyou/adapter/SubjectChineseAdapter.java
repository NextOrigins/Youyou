package com.neworld.youyou.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.bean.SubjectBean;

import java.util.List;

/**
 * Created by tt on 2017/7/24.
 */

public class SubjectChineseAdapter extends BaseAdapter {
    private Context context;
    private List<SubjectBean.MenuListBean> menuList;

    public SubjectChineseAdapter(Context context, List<SubjectBean.MenuListBean> list) {
        this.context = context;
        this.menuList = list;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        SubjectCnHolder holder = null;
        if (holder == null) {
            holder = new SubjectCnHolder();
            convertView = View.inflate(context, R.layout.item_subject, null);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.tv_cn_icon);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content_cn);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time_cn);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.subject_price_cn);
            holder.tvHave = (TextView) convertView.findViewById(R.id.subject_have);
            holder.bt = (Button) convertView.findViewById(R.id.bt_cn);
            updateData(holder, position);
            convertView.setTag(holder);
        } else {
            holder = (SubjectCnHolder) convertView.getTag();
        }
        return convertView;
    }

    private void updateData(SubjectCnHolder holder, int position) {
        if (menuList != null && menuList.size() > 0) {
            SubjectBean.MenuListBean menuListBean = menuList.get(position);
            //标题
            if (!TextUtils.isEmpty(menuListBean.getTitle())) {
                holder.tvContent.setText(menuListBean.getTitle());
            }
            //图片
            if (menuListBean.getComment_img() != null && menuListBean.getComment_img().length() > 0) {
                Glide.with(context).load(menuListBean.getComment_img()).into(holder.ivIcon);
            }
            //考试时间
            String endDate = menuListBean.getEndDate();
            if (endDate != null && endDate.length() > 0) {
                //holder.tvTime.setText(menuListBean.getExamDate());
                holder.tvTime.setText("截止时间：" + endDate);
                Time time = new Time();
                time.setToNow();
                int year = time.year;
                int month = time.month + 1;
                int monthDay = time.monthDay;
                int hour = time.hour;
                String[] split = endDate.split("-");
                int yearEnd = Integer.parseInt(split[0]);
                int monthEnd = Integer.parseInt(split[1]);
                int dayEnd = Integer.parseInt(split[2]);
                //当前时间 比较 截止时间
                if (yearEnd > year) {
                } else if (yearEnd == year) {
                    if (monthEnd > month) {
                    } else if (monthEnd == month) {
                        if (dayEnd >= monthDay) {
                        } else {
                            holder.bt.setText("报名结束");
                            holder.bt.setBackgroundColor(Color.parseColor("#9c9c9c"));
                            holder.bt.setBackgroundResource(R.drawable.subject_no_shape);
                        }
                    } else {
                        holder.bt.setText("报名结束");
                        holder.bt.setBackgroundResource(R.drawable.subject_no_shape);
                    }
                } else {
                    holder.bt.setText("报名结束");
                    holder.bt.setBackgroundResource(R.drawable.subject_no_shape);
                }
            }
            //价格
            if (menuListBean.getMoney() != null && menuListBean.getMoney().length() > 0) {
                holder.tvPrice.setText("¥" + menuListBean.getMoney());
            }
            //已有人数
            int apply_count = menuListBean.getApply_count();
            holder.tvHave.setText("已经报名" + apply_count + "人");
        }
    }

    static class SubjectCnHolder {
        public ImageView ivIcon;
        public TextView tvContent;
        public TextView tvTime;
        public TextView tvPrice;
        public TextView tvHave;
        public Button bt;
    }

/*    public interface OnRefreshClick{
        void onItemClick(int position);
    }

    public OnRefreshClick onRefreshClick;

    public void setOnRefreshClick(OnRefreshClick onRefreshClick) {
        this.onRefreshClick = onRefreshClick;
    }*/
}
