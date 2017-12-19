package com.neworld.youyou.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.bean.ParentBean;
import com.neworld.youyou.utils.LogUtils;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.view.gridlayout.NineGridTestLayout;
import com.neworld.youyou.view.nine.CircleImageView;
import com.neworld.youyou.view.nine.FlowLayout;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tt on 2017/7/18.
 */

public class ParentAdapter extends BaseAdapter {
    public Context context;
    public List<ParentBean.MenuListBean> menuList = new ArrayList<>();

    public ParentAdapter(Context context, List<ParentBean.MenuListBean> parentList) {
        this.context = context;
        this.menuList = parentList;
    }

    public ParentAdapter(Context context, ParentBean bean) {
        this.context = context;
        addBean(bean);
    }

    public void setBean(ParentBean bean) {
        menuList.clear();
        if (bean.stickNamicfoList != null) {

            menuList.addAll(bean.stickNamicfoList);
            LogUtils.E("top list size : " + bean.stickNamicfoList.size());
        }
        addBean(bean);
    }

    public void addBean(ParentBean bean) {
        if (bean.getMenuList() != null)
            menuList.addAll(bean.getMenuList());
    }

    public int getBeanSize() {
        return menuList.size();
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
        final ParentHolder holder;
        if (convertView == null) {
            holder = new ParentHolder();
            convertView = View.inflate(context, R.layout.item_parent, null);
            holder.ivIcon = (CircleImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.ivGender = (ImageView) convertView.findViewById(R.id.iv_gender);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvCategory = (TextView) convertView.findViewById(R.id.tv_category);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tvFav = (TextView) convertView.findViewById(R.id.tv_fav);
            holder.tvComment = (TextView) convertView.findViewById(R.id.tv_comment);
            holder.tvShare = (TextView) convertView.findViewById(R.id.tv_share);
            holder.ivBlack = (ImageView) convertView.findViewById(R.id.add_black);
            //holder.customImageView = (FlowLayout) convertView.findViewById(R.id.fl_image);
           /* holder.ivNine = (NineGridlayout)convertView.findViewById(R.id.iv_ngrid_layout);
            holder.ivOne = (CustomImageView) convertView.findViewById(R.id.iv_oneimage);*/

            holder.ivNine = (NineGridTestLayout) convertView.findViewById(R.id.layout_nine_grid);
            //holder.ivOne = (ImageView) convertView.findViewById(R.id.iv_one);
            holder.llComment = (LinearLayout) convertView.findViewById(R.id.ll_comment);
            holder.llFav = (LinearLayout) convertView.findViewById(R.id.ll_fav);
            holder.llShare = (LinearLayout) convertView.findViewById(R.id.ll_share);
            holder.ivFav = (ImageView) convertView.findViewById(R.id.iv_fav);
            holder.video = convertView.findViewById(R.id._video);

            convertView.setTag(holder);

        } else {
            holder = (ParentHolder) convertView.getTag();
        }

        //数据展示
        if (menuList != null) {
            ParentBean.MenuListBean menuListBean = menuList.get(position);
            initData(menuListBean, holder, position);
        }

        holder.ivIcon.setOnClickListener(v -> onParentClick.onItemIcon(position));

        holder.tvName.setOnClickListener(v -> onParentClick.onItemName(position));

        if (menuList.get(position).isVisible) {
            holder.ivBlack.setVisibility(View.INVISIBLE);
            holder.ivBlack.setOnClickListener(null);
        } else {
            holder.ivBlack.setVisibility(View.VISIBLE);
            holder.ivBlack.setOnClickListener(v -> onParentClick.onItemAddBlack(position));
        }

        holder.llFav.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onParentClick.onItemFav(position);
                ParentBean.MenuListBean menuListBean = menuList.get(position);
                int collect_count = menuListBean.getCollect_count();
                int collectStatus = menuListBean.getCollectStatus();
                if (collectStatus == 0) { //1 shi 未收藏
                    menuListBean.setCollectStatus(1);
                    menuListBean.setCollect_count(collect_count - 1);
                    //holder.ivFav.setImageResource(R.mipmap.parent_collect);
                } else if (collectStatus == 1) {
                    //holder.ivFav.setImageResource(R.mipmap.collect_red);
                    menuListBean.setCollectStatus(0);
                    menuListBean.setCollect_count(collect_count + 1);
                }
            }
        });
        holder.llComment.setOnClickListener(v -> onParentClick.onItemComment(v, position, menuList.get(position)));

        //分享
        holder.llShare.setOnClickListener(v -> onParentClick.onItemShare(position));

        return convertView;
    }

    private void initData(ParentBean.MenuListBean menuListBean, final ParentHolder holder, int position) {
        if (menuListBean.getFaceImg() != null) {
            Glide.with(context).load(menuListBean.getFaceImg()).into(holder.ivIcon);

        } else {
            holder.ivIcon.setImageResource(R.mipmap.my_icon);
        }
        holder.tvName.setText(menuListBean.getNickName());

        int collectStatus = menuListBean.getCollectStatus();
        if (collectStatus == 0) { //1 shi 未收藏
            holder.ivFav.setImageResource(R.mipmap.collect_red);
        } else if (collectStatus == 1) {
            holder.ivFav.setImageResource(R.mipmap.parent_collect);
        }

        int sex = menuListBean.getSex();
        if (sex == 0) {
            //性别
            holder.ivGender.setImageResource(R.mipmap.boy);
        } else {
            holder.ivGender.setImageResource(R.mipmap.girl);
        }

        holder.tvTime.setText(menuListBean.getCreateDate());
        holder.tvCategory.setText(menuListBean.getTypeName());
        //设置内容
        String title = menuListBean.getTitle();
        String content = menuListBean.getContent();
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#5184BC"));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(title + "//" + content);
        if (!TextUtils.isEmpty(content)) {
            holder.tvContent.setVisibility(View.VISIBLE);
            if (title != null && title.length() > 0) {
                spannableStringBuilder.setSpan(foregroundColorSpan, 0, menuListBean.getTitle().length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                holder.tvContent.setText(spannableStringBuilder);
            } else {
                holder.tvContent.setText(content);
            }
        } else if (!TextUtils.isEmpty(title)) {
            spannableStringBuilder.clear();
            spannableStringBuilder.append(title);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, menuListBean.getTitle().length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.tvContent.setText(spannableStringBuilder);
        } else
            holder.tvContent.setVisibility(View.GONE);

        //图片
        final String imgs = menuListBean.getImgs();
        if (imgs != null && imgs.length() > 0) {
            if (menuListBean.sort == 1) {
                holder.video.setVisibility(View.GONE);
                holder.ivNine.setVisibility(View.VISIBLE);
                final String[] split = imgs.split("\\|");
                List<String> list = Arrays.asList(split);
                if (list.size() <= 9) {
                    holder.ivNine.setIsShowAll(true);
                } else {
                    holder.ivNine.setIsShowAll(false);
                }
                holder.ivNine.setUrlList(list);
            } else {
//                holder.ivNine.setIsShowAll(true);
//                String[] s = {menuListBean.voideImg};
//                holder.ivNine.setUrlList(Arrays.asList(s));
                holder.ivNine.setVisibility(View.GONE);
                holder.video.setVisibility(View.VISIBLE);
                holder.video.setLockLand(true);
                holder.video.setUp(menuListBean.getImgs(), false, "");
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(imageView).load(menuListBean.voideImg).into(imageView);
                holder.video.setThumbImageView(imageView);
                holder.video.getFullscreenButton().setVisibility(View.INVISIBLE);
//                holder.video.getFullscreenButton().setOnClickListener(v -> {
//                    if (notDelay())
//                        holder.video.startWindowFullscreen(context, false, true);
//                    else
//                        ToastUtil.showToast("点击频率太高, 请请稍等");
//                });
//                holder.video.setBackFromFullScreenListener(v -> {
//                    if (notDelay())
//                        GSYBaseVideoPlayer.backFromWindowFull(context);
//                    else
//                        ToastUtil.showToast("点击频率太高, 请请稍等");
//                });
                if (holder.video.isIfCurrentIsFullscreen())
                    holder.video.getBackButton().setVisibility(View.VISIBLE);
                else
                    holder.video.getBackButton().setVisibility(View.INVISIBLE);
            }
        } else {
            holder.ivNine.setVisibility(View.GONE);
            holder.video.setVisibility(View.GONE);
        }
        //收藏数 评论数 分享数
        int collect_count = menuListBean.getCollect_count();
        holder.tvFav.setText(collect_count + "");
        int comment_count = menuListBean.getComment_count();
        holder.tvComment.setText(comment_count + "");
        int transmit_count = menuListBean.getTransmit_count();
        holder.tvShare.setText(transmit_count + "");
        //if (list.size() == 1) {
        //holder.ivNine.setVisibility(View.GONE);
        //holder.ivOne.setVisibility(View.VISIBLE);
        //Glide.with(context).load(list.get(0)).into(holder.ivOne);
               /* holder.ivOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //onParentClick.onOneImage(split);
                    }
                });*/
          /*  } else {*/
        //holder.ivNine.setVisibility(View.VISIBLE);
        //holder.ivOne.setVisibility(View.GONE);
              /*  if (list.size() <= 9) {
                    holder.ivNine.setIsShowAll(true);
                } else {
                    holder.ivNine.setIsShowAll(false);
                }*/
        //holder.ivNine
              /*  holder.ivNine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //onParentClick.onNienImages(split);
                    }
                });*/
        //holder.ivNine.setUrlList(list);
        // }
        //添加图片
    }

//    private boolean b; TODO : 不知道为什么全屏崩溃, 暂时关闭全屏按钮
//
//    private boolean notDelay() {
//        if (!b) {
//            b = true;
//            new Handler().postDelayed(() -> b = false, 1500);
//            return b;
//        } else return !b;
//    }

/*    private int getListSize(List<String> list) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }*/

    static class ParentHolder {
        public CircleImageView ivIcon;
        public TextView tvName;
        public ImageView ivGender;
        public TextView tvTime;
        public TextView tvCategory;
        public TextView tvContent;
        public TextView tvFav;
        public TextView tvComment;
        public TextView tvShare;
        public ImageView ivBlack;
        public FlowLayout customImageView;
        public NineGridTestLayout ivNine;
       /* public CustomImageView ivOne;
        public NineGridlayout ivNine;*/
        //public ImageView ivOne;

        public LinearLayout llFav;
        public LinearLayout llShare;
        public LinearLayout llComment;

        public ImageView ivFav;

        public StandardGSYVideoPlayer video;
    }


    public interface OnParentClick {
        void onItemIcon(int positon);

        void onItemName(int positon);

        void onItemAddBlack(int positon);

        void onItemFav(int positon);

        /**
         * 家长圈点击评论按钮触发回调
         *
         * @param position 索引
         * @param bean     数据
         * @param v        被点击的View，回调后设置不可点击。
         */
        void onItemComment(View v, int position, ParentBean.MenuListBean bean);

        void onItemShare(int positon);

       /* void onOneImage(String[] list);

        void onNienImages(String[] list);*/
    }

    private OnParentClick onParentClick;

    public void setOnParentClick(OnParentClick onParentClick) {
        this.onParentClick = onParentClick;
    }
}
