package com.neworld.youyou.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.neworld.youyou.R;
import com.neworld.youyou.adapter.SubjectTestAdapter;
import com.neworld.youyou.add.AchievementActivity;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.bean.SubjectDetailBean;
import com.neworld.youyou.bean.SubjectOrder;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;
import com.neworld.youyou.view.FocusListView;
import com.neworld.youyou.view.nine.FlowLayout;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.ArrayList;
import java.util.List;

public class SubjectActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvHave;
    private TextView tvContent;
    private TextView tvTest;
    private TextView tvLocation;
    private TextView tvShare;
    private TextView tvZan;
    private TextView tvCommetnt;
    private TextView tvBuy;
    private TextView tvActivityTitle;
    private ImageView ivCancel;
    private Dialog dialog;
    private FlowLayout rlChoose;
    private SubjectDetailBean subjectDetailBean;
    private TextView tvPrice;
    private TextView tvSure;
    private int tempId;
    private int subjectId;
    private RelativeLayout rlTest;
    private List<SubjectDetailBean.ListBean> mData = new ArrayList<>();
    private FocusListView focusListView;
    private SubjectTestAdapter adapter;
    private String userId;
    private RelativeLayout rlZan;
    private RelativeLayout rlComment;
    private RelativeLayout rlFav;
    private ImageView ivZan;
    private ImageView ivComment;
    private ImageView ivFav;
    private TextView tvFav;
    private int likeStatus;
    private int collectStatus;
    private int collect_count;
    private int comment_count;
    private int like;
    private int id;
    private int isClick = 0;
    private String activityTitle = "";
    private String title = "";
    private String comment_img = "";

    private boolean pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        initUser();
        initView();
        initData();
    }

    private void initUser() {
        userId = Sputil.getString(SubjectActivity.this, "userId", "");
    }

    // 162 us
    private void initData() {
        Bundle extras = getIntent().getExtras();
        subjectId = extras.getInt("subjectId");
        if (subjectId != 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\",\"subjectId\":\"" + subjectId + "\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "137");
                    System.out.println(content + "");
                    if (content != null && content.length() > 0) {
                        subjectDetailBean = GsonUtil.parseJsonToBean(content, SubjectDetailBean.class);
                        if (subjectDetailBean != null) {
                            if (subjectDetailBean.getStatus() == 0) {
                                Util.uiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        parseData(subjectDetailBean);
                                        List<SubjectDetailBean.ListBean> list = subjectDetailBean.getList();
                                        mData.addAll(list);
                                        adapter.notifyDataSetChanged();
                                        ((ScrollView)findViewById(R.id.subject_scroll)).smoothScrollTo(0, 0);
                                    }
                                });
                            } else {
                                ToastUtil.showToast("未知错误");
                            }
                        }
                    }
                }
            }).start();
        }
    }

    private void parseData(SubjectDetailBean subjectDetailBean) {
        if (subjectDetailBean != null) {
            SubjectDetailBean.MenuListBean menuList = subjectDetailBean.getMenuList();
            if (menuList != null) {
                //设置大标题
                activityTitle = menuList.getActivityTitle();
                if (activityTitle != null && activityTitle.length() > 0) {
                    tvActivityTitle.setText(activityTitle);
                } else {
                    tvActivityTitle.setText("");
                }
                //设置标题
                title = menuList.getTitle();
                if (title != null && title.length() > 0) {
                    tvTitle.setText(title);
                }

                comment_img = menuList.getComment_img();
                //设置时间
                String biginDate = menuList.getEndDate();
                if (biginDate != null && biginDate.length() > 0) {
                    tvTime.setText("截止时间：" + biginDate);
                    Time time = new Time();
                    time.setToNow();
                    int year = time.year;
                    int month = time.month + 1;
                    int monthDay = time.monthDay;
                    int hour = time.hour;
                    String[] split = biginDate.split("-");
                    int yearEnd = Integer.parseInt(split[0]);
                    int monthEnd = Integer.parseInt(split[1]);
                    int dayEnd = Integer.parseInt(split[2]);
                    //当前时间 比较 截止时间
                    if (yearEnd > year) {
                        isClick = 1;
                    } else if (yearEnd == year) {
                        if (monthEnd > month) {
                            isClick = 1;
                        } else if (monthEnd == month) {
                            if (dayEnd >= monthDay) {
                                isClick = 1;
                            } else {
                                isClick = 2;
                                tvBuy.setBackgroundColor(Color.parseColor("#b1b4b3"));
                                tvBuy.setText("报名结束");
                            }
                        } else {
                            isClick = 2;
                            tvBuy.setBackgroundColor(Color.parseColor("#b1b4b3"));
                            tvBuy.setText("报名结束");
                        }
                    } else {
                        isClick = 2;
                        tvBuy.setBackgroundColor(Color.parseColor("#b1b4b3"));
                        tvBuy.setText("报名结束");
                    }
                } else {
                    tvTime.setText("");
                }
                //设置
                int apply_count = menuList.getApply_count();
                tvHave.setText("已经有" + apply_count + "人报名");

                //活动介绍
                String referral = menuList.getReferral();
                if (referral != null && referral.length() > 0) {
                    tvContent.setText(referral);
                } else {
                    tvContent.setText("");
                }
                //考试时间
                String examDate = menuList.getExamDate();
                if (examDate != null && examDate.length() > 0) {
                    tvTest.setText(examDate);
                }
                //考试地点
                String address = menuList.getAddress();
                if (address != null && address.length() > 0) {
                    tvLocation.setText(address);
                }
                //点赞
                like = menuList.getLike();
                tvZan.setText(like + "");

                //评论
                comment_count = menuList.getComment_count();
                tvCommetnt.setText(comment_count + "");
                //收藏
                collect_count = menuList.getCollect_count();
                tvFav.setText(collect_count + "");

                id = menuList.getId();

            }

            //点赞
            likeStatus = subjectDetailBean.getLikeStatus();
            if (likeStatus == 0) {
                ivZan.setImageResource(R.mipmap.web_zan_red);
            } else {
                ivZan.setImageResource(R.mipmap.web_zan);
            }

            collectStatus = subjectDetailBean.getCollectStatus();
            if (collectStatus == 0) {
                ivFav.setImageResource(R.mipmap.web_collect_red);
            } else {
                ivFav.setImageResource(R.mipmap.web_collect);
            }
        }
    }

    private void initView() {
        findViewById(R.id.query).setOnClickListener(v -> {
            startActivity(new Intent(this, AchievementActivity.class));
        });
        rlTest = (RelativeLayout) findViewById(R.id.rl_test);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        tvActivityTitle = (TextView) findViewById(R.id.tv_activity_title);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvHave = (TextView) findViewById(R.id.tv_have);
        tvContent = (TextView) findViewById(R.id.tv_jieshao);
        tvTest = (TextView) findViewById(R.id.tv_test_time);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvShare = (TextView) findViewById(R.id.tv_share);
        rlZan = (RelativeLayout) findViewById(R.id.rl_zan);
        rlComment = (RelativeLayout) findViewById(R.id.rl_comment);
        rlFav = (RelativeLayout) findViewById(R.id.rl_fav);

        rlZan.setOnClickListener(this);
        rlComment.setOnClickListener(this);
        rlFav.setOnClickListener(this);

        ivZan = (ImageView) findViewById(R.id.iv_zan);
        ivComment = (ImageView) findViewById(R.id.iv_comment);
        ivFav = (ImageView) findViewById(R.id.iv_fav);

        tvZan = (TextView) findViewById(R.id.detail_zan);
        tvCommetnt = (TextView) findViewById(R.id.detail_comment);
        tvFav = (TextView) findViewById(R.id.detail_fav);

        tvBuy = (TextView) findViewById(R.id.tv_buy);
        focusListView = (FocusListView) findViewById(R.id.list);
        ivCancel.setOnClickListener(this);
        tvShare.setOnClickListener(this);

        tvBuy.setOnClickListener(this);
        rlTest.setOnClickListener(this);
        if (adapter == null) {
            adapter = new SubjectTestAdapter(SubjectActivity.this, mData);
            focusListView.setAdapter(adapter);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share:
                share();
                break;
            case R.id.rl_zan:
                like();

                break;
            case R.id.rl_comment:
                comment();

                break;
            case R.id.rl_fav:

                collect();
                break;
            case R.id.tv_buy:
                if (isClick == 1)
                    buy();
                else
                    noClick();
                break;
            case R.id.rl_test:
                Intent intent = new Intent();
                intent.setClass(SubjectActivity.this, TestActivity.class);
                Bundle bundle = new Bundle();
                String ccaaUrl = subjectDetailBean.getMenuList().getCcaaUrl();
                if (ccaaUrl != null) {
                    bundle.putString("ccaaUrl", ccaaUrl);
                }
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.tv_sure:
                if (pay) {
                    NetData();
                    dialog.dismiss();
                } else {
                    ToastUtil.showToast("请选择科目");
                }
                break;
        }
    }

    //当不可点的时候设置
    private void noClick() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(SubjectActivity.this);
        ad.setMessage("此报名已结束");
        ad.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        ad.show();
    }


    //收藏
    private void collect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //type 家长圈 status 是否点赞 typestatus 1是点赞  2是收藏
                //String url = Fields.BASEURL + "111?userId="+userId+"&taskId="+taskId + "&type=1&status=" + likeStatus + "&typeStatus=1";
                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"taskId\":\"" + id + "\", \"type\":\"2\", \"status\":\"" + collectStatus + "\",\"typeStatus\":\"2\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "112");
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus != null && returnStatus.getStatus() == 0) {
                        Util.uiThread(new Runnable() {
                            @Override
                            public void run() {
                                refeshCollectView();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    //点赞更新
    private void refeshCollectView() {
        if (collectStatus == 1) {
            ivFav.setImageResource(R.mipmap.web_collect_red);
            collect_count = collect_count + 1;
            tvFav.setText(collect_count + "");
            collectStatus = 0;
        } else {
            ivFav.setImageResource(R.mipmap.web_collect);
            collect_count = collect_count - 1;
            tvFav.setText(collect_count + "");
            collectStatus = 1;
        }
    }

    //评论
    private void comment() {
        Intent intent = new Intent();
        intent.setClass(SubjectActivity.this, SubjectWebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("taskId", id);
        if (activityTitle != null) {
            bundle.putString("title", activityTitle);
        }

        if (title != null) {
            bundle.putString("content", title);
        }

        if (comment_img != null && comment_img.length() > 0) {
            bundle.putString("img", comment_img);
        }
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //点赞
    private void like() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //type 家长圈 status 是否点赞 typestatus 1是点赞  2是收藏
                //String url = Fields.BASEURL + "111?userId="+userId+"&taskId="+taskId + "&type=1&status=" + likeStatus + "&typeStatus=1";
                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"taskId\":\"" + id + "\", \"type\":\"2\", \"status\":\"" + likeStatus + "\",\"typeStatus\":\"1\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "159");
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus != null && returnStatus.getStatus() == 0) {
                        Util.uiThread(new Runnable() {
                            @Override
                            public void run() {
                                refeshView();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    //点赞更新
    private void refeshView() {
        if (likeStatus == 1) {
            ivZan.setImageResource(R.mipmap.web_zan_red);
            like = like + 1;
            tvZan.setText(like + "");
            likeStatus = 0;
        } else {
            ivZan.setImageResource(R.mipmap.web_zan);
            like = like - 1;
            tvZan.setText(like + "");
            likeStatus = 1;
        }
    }

    //分享
    private void share() {
        String url = "http://www.uujz.me:8082/neworld/user/162?taskId=" + id + "&userId=" + userId;
        UMWeb web = new UMWeb(url);
        if (subjectDetailBean != null) {
            SubjectDetailBean.MenuListBean menuList = subjectDetailBean.getMenuList();
            if (activityTitle != null) {
                web.setTitle(activityTitle);
            }

            if (title != null && title.length() > 0) {
                web.setDescription(title);
            }

            if (comment_img != null && comment_img.length() > 0) {
                UMImage image = new UMImage(SubjectActivity.this, comment_img);
                web.setThumb(image);
            }
        }
        new ShareAction(SubjectActivity.this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(shareListener)
                .open();
    }

    private void NetData() {
        if (subjectId != 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"subjectId\":\"" + subjectId + "\", \"typeId\":\"" + tempId + "\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "138");
                    if (content != null && content.length() > 0) {
                        SubjectOrder subjectOrder = GsonUtil.parseJsonToBean(content, SubjectOrder.class);
                        if (subjectOrder != null && subjectOrder.getStatus() == 0) {
                            success(subjectOrder.getOrderId());
                        }
                    }
                }
            }).start();
        }
    }

    private void success(int orderId) {
        Intent intent = new Intent();
        intent.setClass(SubjectActivity.this, PaymentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("orderId", orderId);
        bundle.putInt("subjectId", subjectId);
        bundle.putInt("typeId", tempId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //立即购买
    private void buy() {
        if (subjectDetailBean != null) {
            dialog = new Dialog(SubjectActivity.this, R.style.ActionSheetDialogStyle);
            View inflate = LayoutInflater.from(SubjectActivity.this).inflate(R.layout.dialog_subject, null);
            rlChoose = (FlowLayout) inflate.findViewById(R.id.rl_choose);
            tvPrice = (TextView) inflate.findViewById(R.id.tv_price);
            tvSure = (TextView) inflate.findViewById(R.id.tv_sure);
            tvSure.setOnClickListener(this);
            //将布局设置给Dialog
            dialog.setContentView(inflate);
            //获取当前Activity所在的窗体
            Window dialogWindow = dialog.getWindow();
            //设置Dialog从窗体底部弹出
            dialogWindow.setGravity(Gravity.BOTTOM);
            //获得窗体的属性
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            WindowManager windowManager = getWindowManager();
            Display defaultDisplay = windowManager.getDefaultDisplay();
            lp.width = (int) (defaultDisplay.getWidth());
            //lp.y = 20;//设置Dialog距离底部的距离
            //       将属性设置给窗体
            dialogWindow.setAttributes(lp);
            dialog.show();//显示对话框
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            List<SubjectDetailBean.ListBean> list = subjectDetailBean.getList();
            if (list != null && list.size() > 0) {
                Button button[] = new Button[list.size() + 1];
                int j = -1;
                for (int i = 0; i < list.size(); i++) {
                    button[i] = new Button(this);
                    button[i].setBackgroundResource(R.drawable.subject_button_shape_black);
                    button[i].setTextSize(12.f);
                    button[i].setText(list.get(i).getType_name());
                    if (i % 4 == 0) {
                        j++;
                    }
                    rlChoose.addView(button[i]); //将按钮放入layout组件
                }
                btnClick(button);
            }
        }
    }

    private void btnClick(final Button[] button) {
        for (int i = 0; i < button.length - 1; i++) {
            button[i].setTag(i);
            final int finalI = i;
            button[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (subjectDetailBean != null) {
                        // TODO : 监听选中后才可付款
                        pay = true;
                        ///button[finalI].setBackgroundColor(Color.parseColor("#f37f13"));
                        button[finalI].setBackgroundResource(R.drawable.subject_button_shape);
                        for (int j = 0; j < button.length - 1; j++) {
                            if (j == finalI) {
                                continue;
                            } else {
//                                button[j].setBackgroundColor(Color.parseColor("#e7e7e7"));
                                button[j].setBackgroundResource(R.drawable.subject_button_shape_black);
                            }
                        }
                        int id = subjectDetailBean.getList().get(finalI).getId();
                        tempId = id;
                        double subject_money = subjectDetailBean.getList().get(finalI).getSubject_money();
                        tvPrice.setText("¥" + subject_money);
                    }
                }
            });
        }
    }

    private UMShareListener shareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(SubjectActivity.this, "分享成功!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(SubjectActivity.this, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(SubjectActivity.this, "已取消", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        pay = false;
    }
}
