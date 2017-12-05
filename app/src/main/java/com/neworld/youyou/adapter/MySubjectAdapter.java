package com.neworld.youyou.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.activity.MySubjectActivity;
import com.neworld.youyou.bean.MySubjectBean;

import java.util.List;

/**
 * Created by tt on 2017/8/18.
 */

public class MySubjectAdapter extends BaseAdapter {
    private List<MySubjectBean.MenuListBean> mData;
    private Context context;

    public MySubjectAdapter(MySubjectActivity mySubjectActivity, List<MySubjectBean.MenuListBean> mData) {
        this.context = mySubjectActivity;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SubjectHolder holder = null;
        if (convertView == null) {
            holder = new SubjectHolder();
            convertView = View.inflate(context, R.layout.item_subject_order, null);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvGrade = (TextView) convertView.findViewById(R.id.tv_grade);
            holder.tvTestTime = (TextView) convertView.findViewById(R.id.tv_test_time);
            holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
            holder.rl1 = (RelativeLayout) convertView.findViewById(R.id.rl_1);
            holder.rl2 = (RelativeLayout) convertView.findViewById(R.id.rl_2);
            holder.rl3 = (RelativeLayout) convertView.findViewById(R.id.rl_3);
            holder.llSubject = (LinearLayout) convertView.findViewById(R.id.ll_subject);
            holder.rlGone = (RelativeLayout) convertView.findViewById(R.id.rl_gone);
            holder.rlDelete = (RelativeLayout) convertView.findViewById(R.id.rl_delete);
            convertView.setTag(holder);
        } else {
            holder = (SubjectHolder) convertView.getTag();
        }
        initData(holder, position);
        //删除
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubject.onDelete(position);
            }
        });
        //考前辅导
        holder.rl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubject.onTest(position);
            }
        });
        //准考证下载
        holder.rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubject.onPreTest(position);
            }
        });
        //成绩查询
        holder.rl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubject.onResult(position);
            }
        });

        holder.llSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubject.onItem(position);
            }
        });

        return convertView;
    }

    private void initData(SubjectHolder holder, int position) {
        if (mData != null) {
            MySubjectBean.MenuListBean menuListBean = mData.get(position);
            if (menuListBean != null && menuListBean.getStatus() == 0) {
                //设置头部时间
                String createDate = menuListBean.getOrderCreateDate();
                if (createDate != null && createDate.length() > 9) {
                    String[] split = createDate.split("\\.");
                    String replace = split[0];
                    holder.tvTime.setText(replace);
                }
                //设置money
                double orderMoney = menuListBean.getOrderMoney();
                holder.tvPrice.setText("¥" + orderMoney);
                //设置支付状态  0是未支付 1是支付
                int payStatus = menuListBean.getPayStatus();
                if (payStatus == 0) {
                    holder.rlGone.setVisibility(View.GONE);
                    holder.rlDelete.setVisibility(View.VISIBLE);
                    //holder.tvStatus.setText("待支付");
                } else {
                    //holder.tvStatus.setText("已支付");
                    //holder.ivDelete.setVisibility(View.INVISIBLE);
                    holder.rlGone.setVisibility(View.VISIBLE);
                    holder.rlDelete.setVisibility(View.GONE);
                }
                //设置内容
                String title = menuListBean.getTitle();
                if (title != null) {
                    holder.tvContent.setText(title);
                }
                //设置名字
                String examinee_name = menuListBean.getExaminee_name();
                if (examinee_name != null ) {
                    holder.tvName.setText("考生名字："+examinee_name);
                }
                //设置级别
                String type_name = menuListBean.getType_name();
                if (type_name != null) {
                    holder.tvGrade.setText("考试级别："+ type_name);
                }
                //设置时间
                String subject_date = menuListBean.getSubject_date();
                if (subject_date != null ) {
                    holder.tvTestTime.setText("考试时间："+subject_date);
                }


            }
        }
    }

    static class SubjectHolder{
        public TextView tvTime;
        public TextView tvPrice;
        public TextView tvStatus;
        public TextView tvContent;
        public TextView tvName;
        public TextView tvGrade;
        public TextView tvTestTime;
        public ImageView ivDelete;
        public RelativeLayout rl1;
        public RelativeLayout rl2;
        public RelativeLayout rl3;
        public LinearLayout llSubject;
        public RelativeLayout rlGone;
        public RelativeLayout rlDelete;
    }

    public interface OnSubject{
        void onDelete(int position);
        void onTest(int position);
        void onPreTest(int position);
        void onResult(int position);
        void onItem(int position);
    }

    private OnSubject onSubject;

    public void setOnSubject(OnSubject onSubject) {
        this.onSubject = onSubject;
    }
}
