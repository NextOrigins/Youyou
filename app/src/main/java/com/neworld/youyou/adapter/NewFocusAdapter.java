package com.neworld.youyou.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.bean.FocusBean;
import com.neworld.youyou.bean.FocusOldTitleBean;
import com.neworld.youyou.bean.FocusTitleBean;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.view.NewContentFocus;
import com.neworld.youyou.view.NewFocusList;
import com.neworld.youyou.view.NewTitleFocus;
import com.neworld.youyou.view.OldTitleFocus;
import com.neworld.youyou.view.nine.CircleImageView;

import java.util.List;

/**
 * Created by tt on 2017/8/8.
 */

public class NewFocusAdapter extends BaseAdapter {

    private static final int TYPE_TITLE = 0;
    private static final int TYPE_CONTENT = 1;
    private static final int TYPE_OLD = 2;
    private Context context;
    private List<NewFocusList> mDatas;

    public NewFocusAdapter(Context context, List<NewFocusList> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
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
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_TITLE:
                NewTitleViewHolder newTitleViewHolder = null;
                if (convertView == null || convertView.getTag() == null) {
                    newTitleViewHolder = new NewTitleViewHolder();
                    convertView = View.inflate(context, R.layout.item_focus_title, null);
                    newTitleViewHolder.tvFocusTitle = (TextView) convertView.findViewById(R.id.tv_focus);

                    convertView.setTag(newTitleViewHolder);
                } else {
                    newTitleViewHolder = (NewTitleViewHolder) convertView.getTag();
                }

                NewFocusList newFocusList = mDatas.get(position);
                if (newFocusList != null && newFocusList instanceof FocusTitleBean) {
                    FocusTitleBean titleBean = (FocusTitleBean) newFocusList;
                    newTitleViewHolder.tvFocusTitle.setText(titleBean.title);
                }

                return convertView;
            case TYPE_OLD:
                OldTitleViewHolder oldTitleViewHolder = null;
                if (convertView == null) {
                    oldTitleViewHolder = new OldTitleViewHolder();
                    convertView = View.inflate(context, R.layout.item_focus_old_title, null);
                    oldTitleViewHolder.tvOldTitle = (TextView) convertView.findViewById(R.id.tv_old_focus);

                    convertView.setTag(oldTitleViewHolder);
                } else {
                    oldTitleViewHolder = (OldTitleViewHolder) convertView.getTag();
                }
                NewFocusList focusList = mDatas.get(position);
                if (focusList != null && focusList instanceof FocusOldTitleBean) {
                    FocusOldTitleBean focusOldTitleBean = (FocusOldTitleBean) focusList;
                    oldTitleViewHolder.tvOldTitle.setText(focusOldTitleBean.title);
                }

                return convertView;
            case TYPE_CONTENT:
                NewContentViewHolder holder = null;
                if (convertView == null) {
                    holder = new NewContentViewHolder();
                    convertView = View.inflate(context, R.layout.item_focus_content, null);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                    holder.ivIcon = (CircleImageView) convertView.findViewById(R.id.iv_icon);
                    holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
                    holder.ivAdd = (ImageView) convertView.findViewById(R.id.iv_add);
                    holder.add = (FrameLayout) convertView.findViewById(R.id.view_add);

                    convertView.setTag(holder);
                } else {
                    holder = (NewContentViewHolder) convertView.getTag();
                }

                NewFocusList newFocusListContent = mDatas.get(position);
                if (newFocusListContent != null && newFocusListContent instanceof NewContentFocus) {
                    NewContentFocus newContentFocus = (NewContentFocus) newFocusListContent;
                    if (newContentFocus instanceof FocusBean.MenuListBean) {
                        FocusBean.MenuListBean contentFocus = (FocusBean.MenuListBean) newContentFocus;
                        if (contentFocus != null) {
                            //设置类型名字
                                /*String typeImg = contentFocus.getTypeImg();
                                String typeName = contentFocus.getTypeName();*/
                            if (contentFocus.getTypeName() != null && contentFocus.getTypeName().length() > 0) {
                                holder.tvTitle.setText(contentFocus.getTypeName());
                            } else {
                                holder.tvTitle.setText(contentFocus.getTypeName());
                            }
                            //设置类型图片
                            if (contentFocus.getTypeImg() != null && contentFocus.getTypeImg().length() > 0) {
                                Glide.with(context).load(contentFocus.getTypeImg()).into(holder.ivIcon);
                            }
                            //设置内容
                            if (contentFocus.getNamicInfoBean() != null) {
                                String title = contentFocus.getNamicInfoBean().getTitle();
                                if (title != null && title.length() > 0) {
                                    holder.tvContent.setText(title);
                                } else if (title == null || (title != null && title.equals(""))) {
                                    holder.tvContent.setText("");
                                }
                            }

                            //设置加减号
                            //Glide.with(context).load(R.mipmap.guanzhu).into(holder.ivAdd);
                            holder.ivAdd.setImageResource(R.mipmap.guanzhu); // TODO : 此控件控制关注与删除
                            holder.add.setOnClickListener(v -> onNewFocusClick.onItemAdd(position, 1));
//                            holder.ivAdd.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    onNewFocusClick.onItemAdd(position, 1); //设置tag 如果是1 为已经关注
//                                }
//                            });
                        }
                    } else if (newContentFocus != null && newContentFocus instanceof FocusBean.NotCircleListBean) {
                        FocusBean.NotCircleListBean contentFocus = (FocusBean.NotCircleListBean) newContentFocus;
                        if (contentFocus != null) {
                            //设置类型名字
                            if (contentFocus.getTypeName() != null && contentFocus.getTypeName().length() > 0) {
                                holder.tvTitle.setText(contentFocus.getTypeName());
                            } else {
                                holder.tvTitle.setText(contentFocus.getTypeName());
                            }

                            //设置类型图片
                            if (contentFocus.getTypeImg() != null && contentFocus.getTypeImg().length() > 0) {
                                Glide.with(context).load(contentFocus.getTypeImg()).into(holder.ivIcon);
                            }

                            //设置内容
                            if (contentFocus.getNamicInfoBean() != null) {
                                String title = contentFocus.getNamicInfoBean().getTitle();
                                if (title != null && title.length() > 0) {
                                    holder.tvContent.setText(title);
                                } else {
                                    holder.tvContent.setText("");
                                }
                            }

                            //设置加减号
                            //Glide.with(context).load(R.mipmap.guanzhu).into(holder.ivAdd);
                            holder.ivAdd.setImageResource(R.mipmap.guanzhu_add);
                            holder.add.setOnClickListener(v -> onNewFocusClick.onItemAdd(position, 2));
//                            holder.ivAdd.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    onNewFocusClick.onItemAdd(position, 2);//为推荐关注
//                                }
//                            });
                        }
                    }
                }
                return convertView;
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if ((mDatas.get(position) instanceof NewTitleFocus)) {
            return TYPE_TITLE;
        } else if (mDatas.get(position) instanceof NewContentFocus) {
            return TYPE_CONTENT;
        } else if (mDatas.get(position) instanceof OldTitleFocus) {
            return TYPE_OLD;
        }
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    static class NewContentViewHolder {
        public CircleImageView ivIcon;
        public TextView tvTitle;
        public TextView tvContent;
        public ImageView ivAdd;
        public FrameLayout add;
    }

    static class NewTitleViewHolder {
        public TextView tvFocusTitle;
    }

    static class OldTitleViewHolder {
        public TextView tvOldTitle;
    }

    public interface OnNewFocusClick {
        void onItemAdd(int positon, int tag);
    }

    private OnNewFocusClick onNewFocusClick;

    public void setOnFocusClick(OnNewFocusClick onFocusClick) {
        this.onNewFocusClick = onFocusClick;
    }
}
