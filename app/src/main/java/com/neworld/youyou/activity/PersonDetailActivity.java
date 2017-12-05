package com.neworld.youyou.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.neworld.youyou.R;
import com.neworld.youyou.adapter.ParentAdapter;
import com.neworld.youyou.bean.ParentBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.pulltorefresh.PullToRefreshBase;
import com.neworld.youyou.pulltorefresh.PullToRefreshListView;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;
import com.neworld.youyou.view.nine.CircleImageView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.List;

public class PersonDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivCancel;
    private TextView tvTitle;
    private CircleImageView circleImageView;
    private TextView tvContent;
    private TextView tvName;
    private PullToRefreshListView prlv;
    private String userId;
    private String from_uid;
   private List<ParentBean.MenuListBean> mData = new ArrayList<>();
    private ParentAdapter parentAdapter;
    private int count;
    private ListView listView;
    private int parentPosition;
    private Dialog dialog;
    private String fav_from_uid;
    private String fav_taskId;
    private int collectStatus;
    private int tempStatue;
    private String fav_replace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        initUser();
        initView();
        initData();
        initRresh();
    }

    private void initRresh() {
        //设置下拉刷新与加载更多可用
        prlv.setPullRefreshEnabled(true);
        prlv.setPullLoadEnabled(true);
        prlv.setScrollLoadEnabled(false);
        prlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData();
                prlv.onPullDownRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mData != null ) {
                    ParentBean.MenuListBean menuListBean = mData.get(mData.size() -1);
                    final String createDate = menuListBean.getCreateDate();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\", \"target_uid\":\""+from_uid+"\", \"createDate\":\""+createDate+"\"}").getBytes(), Base64.DEFAULT);
                            String replace = base64.replace("\n", "");
                            String content = NetManager.getInstance().getContent(replace, "119");
                            if (content != null && content.length() > 0) {
                                ParentBean parentBean = GsonUtil.parseJsonToBean(content, ParentBean.class);
                                if (parentBean != null && parentBean.getStatus() == 0) {
                                    List<ParentBean.MenuListBean> menuList = parentBean.getMenuList();
                                    if (menuList != null && menuList.size() > 0) {
                                        for (ParentBean.MenuListBean bean : menuList) {
                                            bean.isVisible = true;
                                        }
                                        mData.addAll(menuList);
                                        Util.uiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                parentAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    } else {
                                        ToastUtil.showToast("没有更多的数据");
                                    }
                                }
                            }
                        }
                    }).start();
                    prlv.onPullUpRefreshComplete();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                } else if (position <= mData.size()){
                    Intent intentPhone = new Intent();
                    intentPhone.setClass(PersonDetailActivity.this,ParentDetailActivity.class);
                    Bundle bundleSimple = new Bundle();
                    ParentBean.MenuListBean menuListBean = mData.get(position - 1);
                    String from_uid = menuListBean.getFrom_uid();
                    bundleSimple.putString("from_uid", from_uid);
                    if (menuListBean != null) {
                        String taskId = menuListBean.getTaskId();
                        bundleSimple.putString("taskId", String.valueOf(taskId));
                    }
                    bundleSimple.putString("title", mData.get(position - 1).getTitle());
                    bundleSimple.putString("content", mData.get(position - 1).getContent());
                    intentPhone.putExtras(bundleSimple);
                    startActivity(intentPhone);
                }

            }
        });

        parentAdapter.setOnParentClick(new ParentAdapter.OnParentClick() {
            @Override
            public void onItemIcon(int position) {

                Intent intentPhone = new Intent();
                intentPhone.setClass(PersonDetailActivity.this,DetailDataActivity.class);
                Bundle bundleSimple = new Bundle();

                    bundleSimple.putString("from_uid", String.valueOf(mData.get(position).getFrom_uid()));
                    intentPhone.putExtras(bundleSimple);
                    startActivity(intentPhone);


            }

            @Override
            public void onItemName(int position) {
            }

            @Override
            public void onItemAddBlack(int position) {
                //parentPosition = position;
                //addBlackName(position);
            }

            @Override
            public void onItemFav(int position) {
                    addFav(position);
            }

            @Override
            public void onItemComment(View v, int position, ParentBean.MenuListBean bean) {
                Intent intentPhone = new Intent();
                intentPhone.setClass(PersonDetailActivity.this,CommentActivity.class);
                Bundle bundleSimple = new Bundle();

                    bundleSimple.putString("from_userId", String.valueOf(mData.get(position).getFrom_uid()));
                    bundleSimple.putString("taskId", String.valueOf(mData.get(position).getTaskId()));
                    bundleSimple.putString("userId", userId);
                    intentPhone.putExtras(bundleSimple);
                    startActivityForResult(intentPhone, 5);


            }

            @Override
            public void onItemShare(int position) {

                    ParentBean.MenuListBean menuListBean = mData.get(position);
                    String taskId = menuListBean.getTaskId();
                    String url = "http://www.uujz.me:8082/neworld/user/143?taskId=" + taskId + "&userId=" +userId;
                    share(url, position);



            }
        });

    }

    private void initUser() {
        userId = Sputil.getString(PersonDetailActivity.this, "userId", "");
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\", \"target_uid\":\""+from_uid+"\", \"createDate\":\"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "119");
                if (content != null && content.length() > 0) {
                    ParentBean parentBean = GsonUtil.parseJsonToBean(content, ParentBean.class);
                    if (parentBean != null && parentBean.getStatus() == 0) {
                        List<ParentBean.MenuListBean> menuList = parentBean.getMenuList();
                        if (menuList != null && menuList.size() > 0) {
                            for (ParentBean.MenuListBean bean : menuList) {
                                bean.isVisible = true;
                            }
                            mData.clear();
                            mData.addAll(menuList);
                            Util.uiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parentAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }

    private void initView() {
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        prlv = (PullToRefreshListView) findViewById(R.id.ptrl);
        ivCancel.setOnClickListener(this);
        listView = prlv.getRefreshableView();
        View view = getLayoutInflater().inflate(R.layout.detail_head, null, false);
        circleImageView =  ((CircleImageView) view.findViewById(R.id.iv_icon));
        tvName = (TextView)view.findViewById(R.id.tv_name);
        tvContent = (TextView)view.findViewById(R.id.tv_content);
        listView.addHeaderView(view);
        Bundle extras = getIntent().getExtras();
        from_uid = extras.getString("from_uid", "");
        count = extras.getInt("count", 0);
        tvContent.setText("文章（" + count + "）");
        String icon = extras.getString("icon", "");
        String name = extras.getString("name", "");

        if (icon != null && icon.length() > 0) {
            Glide.with(PersonDetailActivity.this).load(icon).into(circleImageView);
        } else {
            circleImageView.setImageResource(R.mipmap.my_icon);
        }

        if (name != null && name.length() > 0) {
            tvTitle.setText(name);
            tvName.setText(name);
        } else {
            tvName.setText("");
        }
        if (parentAdapter == null) {
            parentAdapter = new ParentAdapter(PersonDetailActivity.this, mData);
            listView.setAdapter(parentAdapter);
        } else {
            parentAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
        }
    }


    //收藏
    private void addFav(int position) {
        if (mData != null) {
            ParentBean.MenuListBean menuListBean = mData.get(position);
            fav_from_uid = menuListBean.getFrom_uid();
            fav_taskId = menuListBean.getTaskId();
            collectStatus = menuListBean.getCollectStatus();
            // 1 是未收藏0 是收藏
            if (collectStatus == 1) {
                tempStatue = 0;
            } else if (collectStatus == 0){
                tempStatue = 1;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String base64 = Base64.encodeToString(("{\"userId\": \""+ userId +"\", \"taskId\":\""+ fav_taskId +"\", \"type\":\"1\", \"status\":\""+ collectStatus +"\"}").getBytes(), Base64.DEFAULT);
                    fav_replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(fav_replace, "112");
                    if (content != null && content.length() > 0) {
                        ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                        if (returnStatus != null && returnStatus.getStatus() == 0) {
                            Util.uiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parentAdapter.notifyDataSetChanged();

                                }
                            });
                        }
                    }
                }
            }).start();
        }
    }

    private void addBlackName(int position) {
        dialog = new Dialog(PersonDetailActivity.this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(PersonDetailActivity.this).inflate(R.layout.dialog_photo, null);

        TextView  choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
        TextView takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
        TextView savePhoto = (TextView) inflate.findViewById(R.id.save_photo);
        TextView cancel = (TextView) inflate.findViewById(R.id.cancel);

        choosePhoto.setVisibility(View.GONE);
        takePhoto.setVisibility(View.GONE);
        initDialog(dialog, inflate);
        ParentBean.MenuListBean menuListBean = mData.get(position);
        String from_uid = menuListBean.getFrom_uid();
        if (from_uid.equals(userId)) {
            savePhoto.setText("删除");
        } else {
            savePhoto.setText("加入黑名单");
        }
        savePhoto.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void initDialog(Dialog dialog, View inflate) {
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager windowManager = getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        lp.width = (int)(defaultDisplay.getWidth());
        //lp.y = 20;//设置Dialog距离底部的距离
        //       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    private void addBlackAndDelete() {
        if (parentPosition == -1) {
            ToastUtil.showToast("出现未知错误");
        } else {
            ParentBean.MenuListBean menuListBean = mData.get(parentPosition);
            String from_uid = menuListBean.getFrom_uid();
            if (from_uid.equals(userId)){
                //删除
                delteDynamic(parentPosition, menuListBean);
            } else {
                //拉黑
                addBlack(parentPosition, menuListBean);
            }
        }
    }

    //拉黑
    private void addBlack(final int parentPosition, final ParentBean.MenuListBean menuListBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String from_uid = menuListBean.getFrom_uid();
                String base64 = Base64.encodeToString(("{\"userId\":\""+ userId +"\",\"target_uid\":\""+from_uid+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "150");
                ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                if (returnStatus.getStatus() == 0) {
                    //刷新
                    Util.uiThread(new Runnable() {
                        @Override
                        public void run() {
                            mData.remove(parentPosition);
                            parentAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    //失败
                    ToastUtil.showToast("拉黑出现未知错误");
                }
            }
        }).start();
    }

    //删除
    private void delteDynamic(final int parentPosition, final ParentBean.MenuListBean menuListBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String taskId = menuListBean.getTaskId();
                String base64 = Base64.encodeToString(("{\"userId\":\""+ userId +"\",\"taskId\":\""+taskId+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "110");
                ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                if (returnStatus.getStatus() == 0) {
                    //刷新
                    Util.uiThread(new Runnable() {
                        @Override
                        public void run() {
                            mData.remove(parentPosition);
                            parentAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    //失败
                    ToastUtil.showToast("删除出现未知错误");
                }
            }
        }).start();
    }

    //分享
    private void share(String url, int position) {
        UMWeb web = new UMWeb(url);

        ParentBean.MenuListBean menuListBean = mData.get(position);
        String title = menuListBean.getTitle();
        if (title != null) {
            web.setTitle(title);
        }
        String imgs = menuListBean.getImgs();
        if (imgs != null && imgs.length() > 0) {
            String[] split = imgs.split("\\|");
            String s = split[0];
            UMImage image = new UMImage(PersonDetailActivity.this, s);
            web.setThumb(image);
        }

        String content = menuListBean.getContent();
        if (content != null ) {
            web.setDescription(content);
        }

        new ShareAction(PersonDetailActivity.this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(shareListener)
                .open();
    }

    private UMShareListener shareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(PersonDetailActivity.this,"成功了",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(PersonDetailActivity.this,"失败"+t.getMessage(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(PersonDetailActivity.this,"取消了",Toast.LENGTH_LONG).show();
        }
    };
}
