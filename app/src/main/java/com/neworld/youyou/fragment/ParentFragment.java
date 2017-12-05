package com.neworld.youyou.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.neworld.youyou.MainActivity;
import com.neworld.youyou.R;
import com.neworld.youyou.activity.DetailDataActivity;
import com.neworld.youyou.activity.LoginActivity;
import com.neworld.youyou.activity.ParentDetailActivity;
import com.neworld.youyou.adapter.ParentAdapter;
import com.neworld.youyou.bean.ParentBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.Base64Manager;
import com.neworld.youyou.manager.DataTransmissionManager;
import com.neworld.youyou.manager.MyApplication;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.pulltorefresh.PullToRefreshBase;
import com.neworld.youyou.pulltorefresh.PullToRefreshListView;
import com.neworld.youyou.update.UpdateChecker;
import com.neworld.youyou.utils.Fields;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.LogUtils;
import com.neworld.youyou.utils.NetBuild;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParentFragment extends BaseFragment implements View.OnClickListener {

    private PullToRefreshListView ptlv;
    private View view;
    private ParentAdapter parentAdapter;
    private List<ParentBean.MenuListBean> newMenuList = new ArrayList<>();
    private MainActivity activity;
    private Dialog dialog;

    private String fav_taskId;
    private int collectStatus;
    private String fav_replace;
    private String userId;
    private String url;
    private ListView lvParent;
    private int clickPosition = -1;
    private String token;
    private static int PARENTDOWN = 1;
    private static int PARENTUP = 2;
    private String clickTask;

    @Override
    public View createView() {
        view = View.inflate(context, R.layout.fragment_parent, null);
        // UmengTool.checkWx(getContext());
        initUser();
        initView();
        return view;
    }

    private void initUser() {
        userId = Sputil.getString(context, Fields.USERID, Fields.YOUYOU);
        token = Sputil.getString(context, Fields.TOKEN, Fields.YOUYOU);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isSuccess = Sputil.getBoolean(context, getString(R.string.android_success), false);
        if (isSuccess) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getParent(PARENTDOWN);
                    Sputil.saveBoolean(context, getString(R.string.android_success), false);
                }
            }).start();
        }

        boolean wxShare = Sputil.getBoolean(context, "WXShare", false);
        if (wxShare) {
            shareNet(clickTask, clickPosition);
            Sputil.saveBoolean(context, "WXShare", false);
        }
    }

    private void initView() {
        activity = (MainActivity) getActivity();
        ptlv = (PullToRefreshListView) view.findViewById(R.id.lv_parent);
        //设置下拉刷新与加载更多可用
        ptlv.setPullRefreshEnabled(true);
        ptlv.setPullLoadEnabled(true);
        ptlv.setScrollLoadEnabled(false);
        lvParent = ptlv.getRefreshableView();
        //进行版本更新
        UpdateChecker.checkForDialog(mActivity);

        if (parentAdapter == null) {
            parentAdapter = new ParentAdapter(context, newMenuList);
            lvParent.setAdapter(parentAdapter);
        } else {
            parentAdapter.notifyDataSetChanged();
        }
        initAdapter();
        //上拉刷新 下拉加载
        ptlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            //下拉刷新操作
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                new Thread(() -> getParent(PARENTDOWN)).start();
                ptlv.onPullDownRefreshComplete();
            }

            //加载更多操作
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                new Thread(() -> getParent(PARENTUP)).start();
                ptlv.onPullUpRefreshComplete();
            }
        });

        //条目点击 进入详情页面 h5页面
        lvParent.setOnItemClickListener(lvItemClick);
    }

    /**
     * 条目点击监听
     */
    private AdapterView.OnItemClickListener lvItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            openH5(position, false);

            lvParent.setOnItemClickListener(null);

            new Handler().postDelayed(() -> {
                lvParent.setOnItemClickListener(lvItemClick);
            }, 2000);
        }
    };

    private void openH5(int position, boolean b) {
        clickPosition = position;

        HashMap<String, String> map = new HashMap<>();
        ParentBean.MenuListBean menuListBean = newMenuList.get(position);

        String from_uid = menuListBean.getFrom_uid();
        if (!TextUtils.isEmpty(from_uid)) {
            map.put(Fields.FROM_UID, from_uid);
        }

        String taskId = menuListBean.getTaskId();
        if (!TextUtils.isEmpty(taskId)) {
            map.put(Fields.TASKID, taskId);
        }

        String title = menuListBean.getTitle();
        if (!TextUtils.isEmpty(title)) {
            map.put(Fields.TITLE, title);
        }
        String content = menuListBean.getContent();
        if (!TextUtils.isEmpty(content)) {
            map.put(Fields.CONTENT, content);
        }
        String imgs = menuListBean.getImgs();
        if (!TextUtils.isEmpty(imgs)) {
            String[] split = imgs.split("\\|");
            if (split != null && split.length > 0) {
                map.put(Fields.IMAGE, split[0]);
            }
        }
        DataTransmissionManager.putString(new Intent().putExtra("dian", b)
                        .putExtra("sort", menuListBean.sort)
                        .putExtra("video", menuListBean.getImgs()),
                ParentFragment.this,
                ParentDetailActivity.class,
                map, 4);
    }

    //获取家长圈网络数据
    @Override
    public Object getData() {
        return getParent(PARENTDOWN);
    }

    private ParentBean getParent(int type) {
        ParentBean parentBean = getParentBean(type);
        if (parentBean != null && parentBean.getStatus() == 0) {
            int tokenStatus = parentBean.getTokenStatus();
            judgeToken(tokenStatus);
            List<ParentBean.MenuListBean> menuList = parentBean.getMenuList();
            if (menuList != null && menuList.size() > 0) {
                if (type == PARENTDOWN) {
                    newMenuList.clear();
                }
                newMenuList.addAll(menuList);
                Util.uiThread(() -> parentAdapter.notifyDataSetChanged());
            } else if (menuList != null && menuList.size() == 0) {
                ToastUtil.showToast(getString(R.string.parent_not_more_data));
            }
        } else {
//            ToastUtil.showToast(getString(R.string.parent_net_pool));
            if (parentBean != null && parentBean.getStatus() != 0)
                quit(new Intent(context, LoginActivity.class), false); // 如果没登录 status != 0 . 不会返回数据，所以直接跳出登录界面 // TODO : 暂时注释244 245 246
            else
                ToastUtil.showToast(getString(R.string.parent_net_pool));
        }
        return parentBean;
    }

//    private void quit() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String userId = Sputil.getString(getContext(), "userId", "");
//                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\"}").getBytes(), Base64.DEFAULT);
//                String replace = base64.replace("\n", "");
//                String content = NetManager.getInstance().getContent(replace, "152");
//
//                if (content != null && content.length() > 0) {
//                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
//                    if (returnStatus != null && returnStatus.getStatus() == 0) {
//                        Sputil.saveString(getContext(), "userId", "");
//                        startActivity(new Intent(getContext(), LoginActivity.class));
//                        MyApplication mainApplication = activity.getMainApplication();
//                        mainApplication.removeALLActivity_();
//                    }
//                } else {
//                    LogUtils.E(" content = null ? " + (content == null));
//                }
//            }
//        }).start();
//    }

    //从网络中返回的bean
    private ParentBean getParentBean(int type) {
        HashMap<String, String> map = new HashMap<>();
        if (type == PARENTDOWN) {
            map.put("createDate", "");
        } else if (type == PARENTUP) {
            if (newMenuList != null && !newMenuList.isEmpty()) {
                ParentBean.MenuListBean menuListBean = newMenuList.get(newMenuList.size() - 1);
                String createDate = menuListBean.getCreateDate();
                map.put("createDate", createDate);
            }
        }
        map.put("userId", userId);
        map.put("token", token);
        return Base64Manager.getToBean(Fields.PARENTURL, ParentBean.class, map);
    }

    //根据token挤掉线
    private void judgeToken(final int tokenStatus) {
        if (tokenStatus == 2) {
            Util.uiThread(new Runnable() {
                @Override
                public void run() {
                    quit(new Intent(context, LoginActivity.class), true);
                }
            });
        }
    }

    private void quit(Intent intent, boolean login2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "152");
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus != null && returnStatus.getStatus() == 0) {
                        Sputil.saveString(context, Fields.USERID, Fields.YOUYOU);
                        if (login2) intent.putExtra("login2", true);
                        startActivity(intent);
                        MyApplication mainApplication = activity.getMainApplication();
                        mainApplication.removeALLActivity_();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(getActivity(), mPermissionList, 123);
        }
    }

    //获取数据进行展示
    protected void initAdapter() {
        parentAdapter.setOnParentClick(new ParentAdapter.OnParentClick() {
            //进入个人界面
            @Override
            public void onItemIcon(int position) {
                Intent intentPhone = new Intent();
                intentPhone.setClass(context, DetailDataActivity.class);
                Bundle bundleSimple = new Bundle();
                bundleSimple.putString("from_uid", String.valueOf(newMenuList.get(position).getFrom_uid()));
                intentPhone.putExtras(bundleSimple);
                startActivity(intentPhone);
            }

            @Override
            public void onItemName(int position) {
            }

            //删除 加入黑名单
            @Override
            public void onItemAddBlack(int position) {
                parentPosition = position;
                addBlackName(position);
            }

            //收藏
            @Override
            public void onItemFav(int position) {
                addFav(position);
            }

            //评论
            @Override
            public void onItemComment(View v, int position, ParentBean.MenuListBean bean) {
//                clickPosition = position;
//                Intent intentPhone = new Intent();
//                intentPhone.setClass(context,CommentActivity.class);
//                Bundle bundleSimple = new Bundle();
//                bundleSimple.putString("from_userId", String.valueOf(newMenuList.get(position).getFrom_uid()));
//                bundleSimple.putString("taskId", String.valueOf(newMenuList.get(position).getTaskId()));
//                bundleSimple.putString("userId", userId);
//                intentPhone.putExtras(bundleSimple);
//                startActivityForResult(intentPhone, 5);

                if (bean.sort == 2) {
                    // TODO : startActivity -> Video

                    return;
                }

                if (bean.getComment_count() > 0)
                    openH5(position, true);
                else
                    openH5(position, false);

                v.setClickable(false);
                lvParent.setOnItemClickListener(null);
                new Handler().postDelayed(() -> {
                    lvParent.setOnItemClickListener(lvItemClick);
                    v.setClickable(true);
                }, 2000);
            }

            //分享
            @Override
            public void onItemShare(final int position) {
                ParentBean.MenuListBean menuListBean = newMenuList.get(position);
                String taskId = menuListBean.getTaskId();
                clickTask = taskId;
                clickPosition = position;
                url = "http://www.uujz.me:8082/neworld/user/143?taskId=" + taskId + "&userId=" + userId;
                share(url, position, taskId);
            }
        });
    }

    //分享  //TODO 2017-9-27
    private void share(String url, final int position, final String taskId) {
        UMWeb web = new UMWeb(url);

        ParentBean.MenuListBean menuListBean = newMenuList.get(position);
        String title = menuListBean.getTitle();
        if (title != null && title.length() > 0) {
            web.setTitle(title);
        } else {
            web.setTitle("");
        }
        String imgs = menuListBean.getImgs();
        if (imgs != null) {
            String[] split = imgs.split("\\|");
            String s = split[0];
            if (s != null && s.length() > 0) {
                UMImage image = new UMImage(context, s);
                web.setThumb(image);
            }
        }

        String content = menuListBean.getContent();
        if (content != null && content.length() > 0) {
            web.setDescription(content);
        } else {
            web.setDescription("");
        }

        new ShareAction(getActivity())
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        shareNet(taskId, position);
                        Toast.makeText(context, "分享成功", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        ToastUtil.showToast("分享失败" + throwable.getMessage());
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        ToastUtil.showToast("取消分享");
                    }
                })
                .open();
    }

    private void shareNet(final String taskId, final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String encodeToString = Base64.encodeToString(("{\"taskId\":\"" + taskId + "\", \"type\":\"1\"}").getBytes(), Base64.DEFAULT);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            if (data != null) {
                if (data.getBooleanExtra("isPublish", false)) {
                    ParentBean.MenuListBean menuListBean = newMenuList.get(clickPosition);
                    if (menuListBean != null) {
                        int comment_count = menuListBean.getComment_count();
                        menuListBean.setComment_count(comment_count + 1);
                        parentAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (requestCode == 4) {
            //mIntent.putExtra("isCollectStatus", collectStatus);
            // mIntent.putExtra("isLikeStatus", likeStatus);
            if (clickPosition != -1) {
                ParentBean.MenuListBean menuListBean = newMenuList.get(clickPosition);
                if (menuListBean != null && data != null) {

                    int isCollectStatus = data.getIntExtra("isCollectStatus", menuListBean.getCollectStatus());
                    int isLikeStatus = data.getIntExtra("isLikeStatus", menuListBean.getSex());
                    if (isCollectStatus != menuListBean.getCollectStatus() || isLikeStatus != menuListBean.getSex() && parentAdapter != null) {
                        menuListBean.setCollectStatus(isCollectStatus);
                        menuListBean.setSex(isLikeStatus);
                        parentAdapter.notifyDataSetChanged();
                    }
                }


            }


        } else
            UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    //收藏
    private void addFav(int position) {
        if (newMenuList != null) {
            ParentBean.MenuListBean menuListBean = newMenuList.get(position);
            fav_taskId = menuListBean.getTaskId();
            collectStatus = menuListBean.getCollectStatus();
            // 1 是未收藏0 是收藏
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String base64 = Base64.encodeToString(("{\"userId\": \"" + userId + "\", \"taskId\":\"" + fav_taskId + "\", \"type\":\"1\", \"status\":\"" + collectStatus + "\"}").getBytes(), Base64.DEFAULT);
                    fav_replace = base64.replace("\n", "");
                    System.out.println("{\"userId\"");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_photo:
                addBlackAndDelete();
                dialog.dismiss();
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
        }
    }

    //  删除或者拉黑
    private void addBlackAndDelete() {
        if (parentPosition == -1) {
            ToastUtil.showToast(getString(R.string.android_delete_false));
        } else {
            ParentBean.MenuListBean menuListBean = newMenuList.get(parentPosition);
            String from_uid = menuListBean.getFrom_uid();
            if (from_uid.equals(userId)) {
                //删除
                delteDynamic(parentPosition, menuListBean);
            } else {
                //拉黑
                addBlack(menuListBean);
            }
        }
    }

    //拉黑
    private void addBlack(final ParentBean.MenuListBean menuListBean) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String from_uid = menuListBean.getFrom_uid();
                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\",\"target_uid\":\"" + from_uid + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "150");
                ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                if (returnStatus.getStatus() == 0) {
                    //刷新
                    getParent(PARENTDOWN);
                } else {
                    //失败
                    ToastUtil.showToast(getString(R.string.parent_net_pool));
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
                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\",\"taskId\":\"" + taskId + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "110");
                ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                if (returnStatus.getStatus() == 0) {
                    //刷新
                    Util.uiThread(new Runnable() {
                        @Override
                        public void run() {
                            newMenuList.remove(parentPosition);
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

    private int parentPosition = -1;

    private void addBlackName(int position) {
        if (true) {
            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            //填充对话框的布局
            View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_photo, null);

            TextView choosePhoto = (TextView) inflate.findViewById(R.id.choosePhoto);
            TextView takePhoto = (TextView) inflate.findViewById(R.id.takePhoto);
            TextView savePhoto = (TextView) inflate.findViewById(R.id.save_photo);
            TextView cancel = (TextView) inflate.findViewById(R.id.cancel);

            choosePhoto.setVisibility(View.GONE);
            takePhoto.setVisibility(View.GONE);
            initDialog(dialog, inflate);
            ParentBean.MenuListBean menuListBean = newMenuList.get(position);
            String from_uid = menuListBean.getFrom_uid();
            if (from_uid.equals(userId)) {
                savePhoto.setText("删除");
            } else {
                savePhoto.setText("加入黑名单");
            }
            savePhoto.setOnClickListener(this);
            cancel.setOnClickListener(this);
        } else {
            // TODO : 等服务器添加.
            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            ParentBean.MenuListBean bean = newMenuList.get(position);

            View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_parents,
                    (ViewGroup) view.getParent(), false);

            initDialog(dialog, inflate);

            inflate.findViewById(R.id.cancel).setOnClickListener(v -> dialog.dismiss());
            TextView black = inflate.findViewById(R.id.black);

            if (bean.getFrom_uid().equals(userId)) {
                black.setText("删除");

            } else black.setText(getResources().getString(R.string.black_list));

            inflate.findViewById(R.id.hide).setOnClickListener(v -> {
                new Thread(() -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", userId);
                    map.put("taskId", bean.getTaskId());
                    map.put("status", String.valueOf(bean.getStatus()));
                    String response = NetBuild.getResponse(map, 155);
                    LogUtils.LOG_JSON(" 屏蔽本条信息:response: " + response);
                }).start();

                new Thread(() -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", userId);
                    map.put("fromUserId", bean.getFrom_uid());
                    String response = NetBuild.getResponse(map, 186);
                    LogUtils.LOG_JSON(" 屏蔽所有信息:response: " + response);
                }).start();

                new Thread(() -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", userId);
                    String response = NetBuild.getResponse(map, "186_2");
                    LogUtils.LOG_JSON(" 显示屏蔽的信息:response: " + response);
                }).start();
            });
        }

    }

    private void initDialog(Dialog dialog, View inflate) {
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        WindowManager windowManager = activity.getWindowData();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        lp.width = (int) (defaultDisplay.getWidth());
        //lp.y = 20;//设置Dialog距离底部的距离
        //       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

}
