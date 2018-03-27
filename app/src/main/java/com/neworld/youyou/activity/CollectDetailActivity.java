package com.neworld.youyou.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.adapter.HotEveryAdapter;
import com.neworld.youyou.adapter.ParentAdapter;
import com.neworld.youyou.adapter.SubjectChineseAdapter;
import com.neworld.youyou.bean.HotBean;
import com.neworld.youyou.bean.ParentBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.bean.SubjectBean;
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

public class CollectDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivCancel;
    private TextView tvTitle;
    private PullToRefreshListView prlv;
    private List<ParentBean.MenuListBean> newMenuList = new ArrayList<>();
    private List<SubjectBean.MenuListBean> subjectMenuList= new ArrayList<>();
    private List<HotBean.MenuListBean> hotMenuList= new ArrayList<>();
    private String userId;
    private SubjectChineseAdapter subjectAdapter;
    private HotEveryAdapter hotEveryAdapter;
    private ParentAdapter parentAdapter;
    private int parentPosition;
    private Dialog dialog;
    private String fav_from_uid;
    private String fav_taskId;
    private int collectStatus;
    private int tempStatue;
    private String fav_replace;
    private ListView listView;
    private int clickPosition;
    private String clickTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_detail);
        initUser();
        initView();
    }
    private void initUser() {
        userId = SPUtil.getString(CollectDetailActivity.this, "userId", "");
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

    private void initView() {
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        prlv = (PullToRefreshListView) findViewById(R.id.listview);
        ivCancel.setOnClickListener(this);
        listView = prlv.getRefreshableView();
        prlv.setPullRefreshEnabled(true);
        prlv.setPullLoadEnabled(true);
        prlv.setScrollLoadEnabled(false);
        Bundle extras = getIntent().getExtras();
        String collect = extras.getString("collect");
        tvTitle.setText(collect);
        if (collect != null && collect.length() > 0) {
            switch (collect) {
                case "圈子收藏":
                    if (parentAdapter == null) {
                        parentAdapter = new ParentAdapter(CollectDetailActivity.this, newMenuList);
                        listView.setAdapter(parentAdapter);
                    }

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intentPhone = new Intent();
                            intentPhone.setClass(CollectDetailActivity.this, ParentDetailActivity.class);
                            Bundle bundleSimple = new Bundle();
                            ParentBean.MenuListBean menuListBean = newMenuList.get(position);

                            String from_uid = menuListBean.getFrom_uid();
                            bundleSimple.putString("from_uid", from_uid);
                            if (menuListBean != null) {
                                String taskId = menuListBean.getTaskId();
                                bundleSimple.putString("taskId", String.valueOf(taskId));
                            }
                            bundleSimple.putString("title", newMenuList.get(position).getTitle());
                            bundleSimple.putString("content", newMenuList.get(position).getContent());
                            intentPhone.putExtras(bundleSimple);
                            startActivity(intentPhone);
                        }
                    });

                    prlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                        @Override
                        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                            parentCollect();
                            prlv.onPullDownRefreshComplete();
                        }

                        @Override
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            if (newMenuList != null) {
                                ParentBean.MenuListBean menuListBean = newMenuList.get(newMenuList.size() - 1);
                                final String createDate = menuListBean.getCreateDate();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String base4 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"type\":\"1\", \"createDate\":\"" + createDate + "\"}").getBytes(), Base64.DEFAULT);
                                        String replace = base4.replace("\n", "");
                                        String content = NetManager.getInstance().getContent(replace, "113");
                                        ParentBean parentBean = GsonUtil.parseJsonToBean(content, ParentBean.class);
                                        if (parentBean == null) throw new NullPointerException("parentBean = null");
                                        int status = parentBean.getStatus();
                                        if (status == 0) {
                                            List<ParentBean.MenuListBean> menuList = parentBean.getMenuList();
                                            for (ParentBean.MenuListBean bean : menuList) {
                                                bean.isVisible = true;
                                            }
                                            newMenuList.addAll(menuList);
                                            if (newMenuList != null && newMenuList.size() > 0) {
                                                Util.uiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        parentAdapter.notifyDataSetChanged();
                                                    }
                                                });
                                            } else {
                                                ToastUtil.showToast("没有更多的数据");
                                            }
                                        } else {
                                            ToastUtil.showToast("网络不佳");
                                        }
                                    }
                                }).start();
                            } else {
                                ToastUtil.showToast("没有更多的数据");
                            }
                            prlv.onPullUpRefreshComplete();
                        }
                    });
                    parentCollect();
                    parentAdapter.setOnParentClick(new ParentAdapter.OnParentClick() {
                        @Override
                        public void onItemIcon(int position) {
                            Intent intentPhone = new Intent();
                            intentPhone.setClass(CollectDetailActivity.this, DetailDataActivity.class);
                            Bundle bundleSimple = new Bundle();
                            bundleSimple.putString("from_uid", String.valueOf(newMenuList.get(position).getFrom_uid()));
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
                            intentPhone.setClass(CollectDetailActivity.this, CommentActivity.class);
                            Bundle bundleSimple = new Bundle();
                            bundleSimple.putString("from_userId", String.valueOf(newMenuList.get(position).getFrom_uid()));
                            bundleSimple.putString("taskId", String.valueOf(newMenuList.get(position).getTaskId()));
                            bundleSimple.putString("userId", userId);
                            intentPhone.putExtras(bundleSimple);
                            startActivityForResult(intentPhone, 5);
                        }

                        @Override
                        public void onItemShare(int position) {
                            ParentBean.MenuListBean menuListBean = newMenuList.get(position);
                            String taskId = menuListBean.getTaskId();
                            String url = "http://www.uujz.me:8082/neworld/user/143?taskId=" + taskId + "&userId=" + userId;
                            clickPosition = position;
                            clickTask = taskId;
                            share(url, position, taskId);
                        }
                    });
                    break;
                case "热文收藏":
                    if (hotEveryAdapter == null) {
                        hotEveryAdapter = new HotEveryAdapter(CollectDetailActivity.this, hotMenuList);
                        listView.setAdapter(hotEveryAdapter);
                    }
                    hotCollect();

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            HotBean.MenuListBean menuListBean = hotMenuList.get(position);
                            if (menuListBean != null) {
                                int taskId = menuListBean.getId();
                                Intent intent = new Intent();
                                intent.setClass(CollectDetailActivity.this, HotActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("taskId", taskId);
                                int from_uid = menuListBean.getFrom_uid();
                                bundle.putInt("from_uid", from_uid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    });
                    prlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                        @Override
                        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                            hotCollect();
                            prlv.onPullDownRefreshComplete();
                        }

                        @Override
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            if (hotMenuList != null) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        HotBean.MenuListBean menuListBean = hotMenuList.get(hotMenuList.size() - 1);
                                        if (menuListBean != null && menuListBean.getCreateDate() != null) {
                                            String base4 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"type\":\"3\", \"createDate\":\"" + menuListBean.getCreateDate() + "\"}").getBytes(), Base64.DEFAULT);
                                            String replace = base4.replace("\n", "");
                                            String content = NetManager.getInstance().getContent(replace, "113");
                                            if (content != null && content.length() > 0) {
                                                HotBean hotBean = GsonUtil.parseJsonToBean(content, HotBean.class);
                                                if (hotBean != null && hotBean.getStatus() == 0) {
                                                    List<HotBean.MenuListBean> menuList = hotBean.getMenuList();
                                                    if (menuList != null && menuList.size() > 0) {
                                                        hotMenuList.addAll(menuList);
                                                        Util.uiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                hotEveryAdapter.notifyDataSetChanged();
                                                            }
                                                        });
                                                    } else {
                                                        ToastUtil.showToast("没有更多的数据");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }).start();
                                prlv.onPullUpRefreshComplete();
                            }
                        }
                    });

                    break;
                case "考证收藏":
                    if (subjectAdapter == null) {
                        subjectAdapter = new SubjectChineseAdapter(CollectDetailActivity.this, subjectMenuList);
                        listView.setAdapter(subjectAdapter);
                    }
                    subjectCollect();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            SubjectBean.MenuListBean menuListBean = subjectMenuList.get(position);
                            if (menuListBean != null) {
                                int subjectId = menuListBean.getId();
                                Intent intent = new Intent();
                                intent.setClass(CollectDetailActivity.this, SubjectActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("subjectId", subjectId);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    });
                    prlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                        @Override
                        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                            subjectCollect();
                            prlv.onPullDownRefreshComplete();
                        }

                        @Override
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            if (subjectMenuList != null && subjectMenuList.size() > 0) {
                                final String createDate = subjectMenuList.get(subjectMenuList.size() - 1).getCreateDate();
                                String newDate = subjectMenuList.get(subjectMenuList.size() - 1).newDate;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String base4 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"type\":\"2\", \"createDate\":\"" + newDate + "\"}").getBytes(), Base64.DEFAULT);
                                        String replace = base4.replace("\n", "");
                                        String content = NetManager.getInstance().getContent(replace, "113");
                                        if (content != null && content.length() > 0) {
                                            SubjectBean subjectBean = GsonUtil.parseJsonToBean(content, SubjectBean.class);
                                            if (subjectBean != null && subjectBean.getStatus() == 0) {
                                                List<SubjectBean.MenuListBean> menuList = subjectBean.getMenuList();
                                                if (menuList != null && menuList.size() > 0) {
                                                    subjectMenuList.addAll(menuList);
                                                    Util.uiThread(() -> subjectAdapter.notifyDataSetChanged());
                                                } else {
                                                    ToastUtil.showToast("没有更多的数据");
                                                }
                                            } else {
                                                ToastUtil.showToast("数据有误");
                                            }
                                        }
                                    }
                                }).start();
                                prlv.onPullUpRefreshComplete();
                            }
                        }
                    });
                    break;
            }
        }
    }

    //分享
    private void share(String url, final int position, final String taskId) {
        UMWeb web = new UMWeb(url);

        ParentBean.MenuListBean menuListBean = newMenuList.get(position);
        String title = menuListBean.getTitle();
        if (title != null) {
            web.setTitle(title);
        }
        String imgs = menuListBean.getImgs();
        String[] split = imgs.split("\\|");
        String s = split[0];
        UMImage image = new UMImage(CollectDetailActivity.this, s);
        web.setThumb(image);
        String content = menuListBean.getContent();
        if (content != null ) {
            web.setDescription(content);
        }

        new ShareAction(CollectDetailActivity.this)
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
                        ParentBean.MenuListBean menuListBean1 = newMenuList.get(position);
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

    //收藏
    private void addFav(final int position) {
        if (newMenuList != null) {
            ParentBean.MenuListBean menuListBean = newMenuList.get(position);
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
                                    newMenuList.remove(position);
                                    parentAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }).start();
        }
    }

    //热文收藏
    private void hotCollect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base4 = Base64.encodeToString(("{\"userId\":\""+userId+"\", \"type\":\"3\", \"createDate\":\"\"}").getBytes(), Base64.DEFAULT);
                String replace = base4.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "113");
                if (content != null && content.length() > 0) {
                    HotBean hotBean = GsonUtil.parseJsonToBean(content, HotBean.class);
                    if (hotBean != null && hotBean.getStatus() == 0) {
                        List<HotBean.MenuListBean> menuList = hotBean.getMenuList();
                        hotMenuList.clear();
                        hotMenuList.addAll(menuList);
                        Util.uiThread(new Runnable() {
                            @Override
                            public void run() {
                                hotEveryAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        }).start();

    }
        //考证收藏
    private void subjectCollect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base4 = Base64.encodeToString(("{\"userId\":\""+userId+"\", \"type\":\"2\", \"createDate\":\"\"}").getBytes(), Base64.DEFAULT);
                String replace = base4.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "113");
                if (content != null && content.length() > 0) {
                    SubjectBean subjectBean = GsonUtil.parseJsonToBean(content, SubjectBean.class);
                    if (subjectBean != null && subjectBean.getStatus() == 0) {
                        List<SubjectBean.MenuListBean> menuList = subjectBean.getMenuList();
                        if (menuList != null) {
                            subjectMenuList.clear();
                            subjectMenuList.addAll(menuList);
                            Util.uiThread(new Runnable() {
                                @Override
                                public void run() {
                                   subjectAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } else {
                        ToastUtil.showToast("数据有误");
                    }
                }
            }
        }).start();
    }

    //圈子收藏
    private void parentCollect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base4 = Base64.encodeToString(("{\"userId\":\""+userId+"\", \"type\":\"1\", \"createDate\":\"\"}").getBytes(), Base64.DEFAULT);
                String replace = base4.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "113");
                        //getSuccessData(content);
                ParentBean parentBean = GsonUtil.parseJsonToBean(content, ParentBean.class);
                if (parentBean != null && parentBean.getStatus() == 0) {
                    List<ParentBean.MenuListBean> menuList = parentBean.getMenuList();
                    for (ParentBean.MenuListBean bean : menuList) {
                        bean.isVisible = true;
                    }
                    newMenuList.clear();
                    newMenuList.addAll(menuList);
                    if (newMenuList != null && newMenuList.size() > 0) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.save_photo:
                //addBlackAndDelete();
                //dialog.dismiss();
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
