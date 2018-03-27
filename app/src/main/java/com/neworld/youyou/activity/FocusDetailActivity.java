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

import com.neworld.youyou.R;
import com.neworld.youyou.adapter.ParentAdapter;
import com.neworld.youyou.bean.ParentBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.pulltorefresh.PullToRefreshBase;
import com.neworld.youyou.pulltorefresh.PullToRefreshListView;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.SPUtil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.List;

public class FocusDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivCancel;
    private TextView tvTitle;
    private PullToRefreshListView ptrlv;
    private List<ParentBean.MenuListBean> mData = new ArrayList<>();
    private ParentAdapter parentAdapter;
    private String userId;
    private ListView listView;
    private int typeId;
    private String fav_from_uid;
    private String fav_taskId;
    private int collectStatus;
    private int tempStatue;
    private String fav_replace;
    private int parentPosition;
    private Dialog dialog;
    private String clickTask;
    private int clickPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_detail);
        initUser();
        initView();
        initData();
    }
    private void initUser() {
        userId = SPUtil.getString(FocusDetailActivity.this, "userId", "");
    }
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\", \"createDate\":\"\", \"typeId\":\"" + typeId + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "118");
                if (content != null && content.length() > 0) {
                    ParentBean parentBean = GsonUtil.parseJsonToBean(content, ParentBean.class);
                    if (parentBean != null && parentBean.getStatus() == 0) {
                            parseData(parentBean);
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean wxShare = SPUtil.getBoolean(this, "WXShare", false);
        if (wxShare) {
            shareNet(clickTask, clickPosition);
            SPUtil.saveBoolean(this, "WXShare", false);
        }
    }

    private void parseData(ParentBean parentBean) {
            final List<ParentBean.MenuListBean> menuList = parentBean.getMenuList();
            if (menuList != null && menuList.size() > 0) {
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

    private void initView() {
        ivCancel = (ImageView) findViewById(R.id.iv_focus);
        tvTitle = (TextView) findViewById(R.id.tv_focus);
        ptrlv = (PullToRefreshListView) findViewById(R.id.lv);
        //设置下拉刷新与加载更多可用
        ptrlv.setPullRefreshEnabled(true);
        ptrlv.setPullLoadEnabled(true);
        ptrlv.setScrollLoadEnabled(false);

        Bundle extras = getIntent().getExtras();
        typeId = extras.getInt("typeId");
        String title = extras.getString("title");
        if (title != null && title.length() > 0) {
            tvTitle.setText(title);
        }
        listView = ptrlv.getRefreshableView();
        if (parentAdapter == null)  {
           parentAdapter = new ParentAdapter(FocusDetailActivity.this, mData);
            listView.setAdapter(parentAdapter);
       }
        ivCancel.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentPhone = new Intent();
                intentPhone.setClass(FocusDetailActivity.this,ParentDetailActivity.class);
                Bundle bundleSimple = new Bundle();
                ParentBean.MenuListBean menuListBean = mData.get(position);
                String from_uid = menuListBean.getFrom_uid();
                bundleSimple.putString("from_uid", from_uid);
                if (menuListBean != null) {
                    String taskId = menuListBean.getTaskId();
                    bundleSimple.putString("taskId", String.valueOf(taskId));
                }
                intentPhone.putExtras(bundleSimple);
                startActivity(intentPhone);
            }
        });
        //设置上拉 下拉
        ptrlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\", \"createDate\":\"\", \"typeId\":\"" + typeId + "\"}").getBytes(), Base64.DEFAULT);
                        String replace = base64.replace("\n", "");
                        String content = NetManager.getInstance().getContent(replace, "118");
                        if (content != null && content.length() > 0) {
                            ParentBean parentBean = GsonUtil.parseJsonToBean(content, ParentBean.class);
                            if (parentBean != null) {
                                if (parentBean.getStatus() == 0) {
                                    parseData(parentBean);
                                } else {
                                    ToastUtil.showToast("传回来的数据异常");
                                }
                            }
                        }
                    }
                }).start();
                ptrlv.onPullDownRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ParentBean.MenuListBean menuListBean = mData.get(mData.size() - 1);
                        if (menuListBean != null ) {
                            String createDate = menuListBean.getCreateDate();
                            String base64 = Base64.encodeToString(("{\"userId\":\""+userId+"\", \"createDate\":\"" + createDate + "\", \"typeId\":\"" + typeId + "\"}").getBytes(), Base64.DEFAULT);
                            String replace = base64.replace("\n", "");
                            String content = NetManager.getInstance().getContent(replace, "118");
                            if (content != null && content.length() > 0) {
                                final ParentBean parentBean = GsonUtil.parseJsonToBean(content, ParentBean.class);
                                if (parentBean != null) {
                                    if (parentBean.getStatus() == 0) {
                                        Util.uiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                List<ParentBean.MenuListBean> menuList = parentBean.getMenuList();
                                                if (menuList != null && menuList.size() > 0) {
                                                    mData.addAll(menuList);
                                                    parentAdapter.notifyDataSetChanged();
                                                } else {
                                                    ToastUtil.showToast("没有更多的数据");
                                                }
                                            }
                                        });
                                    } else {
                                        ToastUtil.showToast("传回来的数据异常");
                                    }
                                }
                            }
                        }
                    }
                }).start();
                ptrlv.onPullUpRefreshComplete();
            }
        });

        parentAdapter.setOnParentClick(new ParentAdapter.OnParentClick() {
            @Override
            public void onItemIcon(int position) {
                Intent intentPhone = new Intent();
                intentPhone.setClass(FocusDetailActivity.this,DetailDataActivity.class);
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
                parentPosition = position;
                addBlackName(position);
            }

            @Override
            public void onItemFav(int position) {
                addFav(position);
            }

            @Override
            public void onItemComment(View v, int position, ParentBean.MenuListBean bean) {
                Intent intentPhone = new Intent();
                intentPhone.setClass(FocusDetailActivity.this,CommentActivity.class);
                Bundle bundleSimple = new Bundle();
                bundleSimple.putString("from_userId", String.valueOf(mData.get(position).getFrom_uid()));
                bundleSimple.putString("taskId", String.valueOf(mData.get(position).getTaskId()));
                bundleSimple.putString("userId", userId);
                intentPhone.putExtras(bundleSimple);
                startActivityForResult(intentPhone, 5);
            }

            @Override
            public void onItemShare(final int position) {
                        ParentBean.MenuListBean menuListBean = mData.get(position);
                        String taskId = menuListBean.getTaskId();
                        clickPosition = position;
                        clickTask = taskId;
                        String url = "http://www.uujz.me:8082/neworld/user/143?taskId=" + taskId + "&userId=" +userId;
                        share(url, position, taskId);

            }
        });
    }

    private void share(String url, final int position, final String taskId) {
        UMWeb web = new UMWeb(url);

        ParentBean.MenuListBean menuListBean = mData.get(position);
        String title = menuListBean.getTitle();
        if (title != null && title.length() > 0) {
            web.setTitle(title);
        } else {
            web.setTitle("");
        }
        String imgs = menuListBean.getImgs();
        String[] split = imgs.split("\\|");
        String s = split[0];
        UMImage image = new UMImage(FocusDetailActivity.this, s);
        web.setThumb(image);
        String content = menuListBean.getContent();
        if (content != null && content.length() > 0) {
            web.setDescription(content);
        } else {
            web.setDescription("");
        }

        new ShareAction(FocusDetailActivity.this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.QZONE,SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        shareNet(taskId, position);
                        ToastUtil.showToast("分享成功");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        ToastUtil.showToast("分享失败");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        ToastUtil.showToast("分享取消");
                    }
                })
                .open();
    }

    private void shareNet(final String taskId, final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String encodeToString = Base64.encodeToString(("{\"taskId\":\""+taskId+"\", \"type\":\"1\"}").getBytes(), Base64.DEFAULT);
                String replace = encodeToString.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "144");
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus != null && returnStatus.getStatus() == 0) {
                        ParentBean.MenuListBean menuListBean1 = mData.get(position);
                        menuListBean1.setTransmit_count(menuListBean1.getTransmit_count() + 1);
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
                    //ToastUtil.showToast(tempStatue + "");
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_focus:
                finish();
                break;
            case R.id.save_photo:
                addBlackAndDelete();
                dialog.dismiss();
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
        }
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

    //拉黑
    private void addBlack(final int parentPosition, final ParentBean.MenuListBean menuListBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String from_uid = menuListBean.getFrom_uid();
                String base64 = Base64.encodeToString(("{\"userId\":\""+ userId +"\",\"target_uid\":\""+from_uid+"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "150");
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus.getStatus() == 0) {

                        //TODO 刷新
                        initData();
                    String base = Base64.encodeToString(("{\"userId\":\""+userId+"\", \"createDate\":\"\", \"typeId\":\"" + typeId + "\"}").getBytes(), Base64.DEFAULT);
                    String replac = base.replace("\n", "");
                    String conten = NetManager.getInstance().getContent(replac, "118");
                    if (conten != null && conten.length() > 0) {
                        ParentBean parentBean = GsonUtil.parseJsonToBean(conten, ParentBean.class);
                        if (parentBean != null && parentBean.getStatus() == 0) {
                            List<ParentBean.MenuListBean> menuList = parentBean.getMenuList();
                            if (menuList != null) {
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
                    } else {
                        //失败
                        ToastUtil.showToast("拉黑出现未知错误");
                    }

                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void addBlackName(int position) {
        dialog = new Dialog(FocusDetailActivity.this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(FocusDetailActivity.this).inflate(R.layout.dialog_photo, null);

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

}
