package com.neworld.youyou.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neworld.youyou.R;
import com.neworld.youyou.bean.HotStatusBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.Fields;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.SPUtil;
import com.neworld.youyou.utils.Util;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

public class HotActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView webView;
    private String userId;
    private ImageView ivCancel;
    private TextView ibZan;
    private TextView ibfav;
    private TextView ibShare;

    private RelativeLayout rlZan;
    private RelativeLayout rlFav;
    private RelativeLayout rlShare;
    private ImageView ivZan;
    private ImageView ivFav;
    private ImageView ivShare;
    private int collectStatus;
    private int likeStatus;
    private int collect_count;
    private int transmit_count;
    private int like;
    private int taskId;
    private int from_uid;
    private TextView tvComment;
    private ProgressDialog dialog = null;
    private String baseUrl;
    private String content;
    private String imgs;
    private String title;
    private String dianUrl;
    private RelativeLayout rlPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot);
        // 白底黑字状态栏 . api大于23 (Android6.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
        }
        initUder();
        initView();
    }

    private void initUder() {
        userId = SPUtil.getString(HotActivity.this, "userId", "");
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.parent_webview);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);

        ibZan = (TextView) findViewById(R.id.detail_zan);
        ibfav = (TextView) findViewById(R.id.detail_fav);
        ibShare = (TextView) findViewById(R.id.detail_share);

        rlZan = (RelativeLayout) findViewById(R.id.rl_zan);
        rlFav = (RelativeLayout) findViewById(R.id.rl_fav);
        rlShare = (RelativeLayout) findViewById(R.id.rl_share);

        ivZan = (ImageView) findViewById(R.id.iv_zan);
        ivFav = (ImageView) findViewById(R.id.iv_fav);
        ivShare = (ImageView) findViewById(R.id.iv_share);
        rlPage = (RelativeLayout) findViewById(R.id.page_tv);
        tvComment = (TextView) findViewById(R.id.detail_comment);
        tvComment.setOnClickListener(this);
        rlPage.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        rlFav.setOnClickListener(this);
        rlZan.setOnClickListener(this);
        rlShare.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        taskId = extras.getInt("taskId", 0);
        from_uid = extras.getInt("from_uid", 0);
        content = extras.getString("content", "");
        imgs = extras.getString("imgs", "");
        title = extras.getString("title", "");
        initData();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        baseUrl = "http://106.14.251.200:8082/neworld/android/133?userId=" + userId + "&taskId=" + taskId + "&maodian=0";
        dianUrl = "http://106.14.251.200:8082/neworld/android/133?userId=" + userId + "&taskId=" + taskId + "&maodian=1";
        webView.loadUrl(dianUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //String baseUrl = "http://106.14.251.200:8082/neworld/android/108?userId=10850&taskId=730&maodian=";
                //System.out.println(url + "我是url+++++++++++++++++++++++++++++++++++++++++++++");
               /* if (url.contains("http://106.14.251.200/neworld/user/154?collectStatus")) {
                   view.loadUrl(baseUrl);
                    return true;
                }
                return false;*/
                if (url.contains("http://106.14.251.200:8082/neworld/android/133?userId=")) { //本网页的跳转
                    webView.loadUrl(dianUrl);
                    return true;
                } else if (url.contains("http://lk.wccwin.com/neworld/user/143?from_userId")) {//图像跳转
                    //http://lk.wccwin.com/neworld/user/143?from_userId=11939
                    //webView.loadUrl(baseUrl);
                    String replace = url.replace("http://lk.wccwin.com/neworld/user/143?from_userId=", "");
                    Intent intentPhone = new Intent();
                    intentPhone.setClass(HotActivity.this, DetailDataActivity.class);
                    Bundle bundleSimple = new Bundle();
                    bundleSimple.putString("from_uid", replace);
                    intentPhone.putExtras(bundleSimple);
                    startActivity(intentPhone);
                    return true;
                } else if (url.contains("http://106.14.251.200/neworld/user/154")) {//删除
                    webView.loadUrl(baseUrl);
                    return true;
                } else if (url.contains("http://106.14.251.200/neworld/user/143?fr_userId=")) {
                    String name = getName(url);
                    String comment = getComment(url);
                    String fromId = getUid(url);
                    Intent intentPhone = new Intent();
                    intentPhone.setClass(HotActivity.this, HotCommentActivity.class);
                    Bundle bundleSimple = new Bundle();
                    bundleSimple.putString("nickName", name);
                    bundleSimple.putString("from_userId", fromId);
                    bundleSimple.putString("taskId", String.valueOf(taskId));
                    bundleSimple.putString("commentId", comment);
                    intentPhone.putExtras(bundleSimple);
                    startActivityForResult(intentPhone, 15);
                    webView.loadUrl(baseUrl);
                }
                return false;

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //super.onPageFinished(view, url);
                dialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //super.onPageStarted(view, url, favicon);
                dialog = ProgressDialog.show(HotActivity.this, null, "页面加载中，请稍后..");
                new Handler().postDelayed(() -> dialog.dismiss(), 1500);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webView.setVisibility(View.INVISIBLE);
                rlPage.setVisibility(View.VISIBLE);
            }
        });
        //返回
    }

    //截取uid
    private String getUid(String url) {
        String comment = "";
        String replace = url.replace("http://106.14.251.200/neworld/user/143?", "");
        String[] split = replace.split("&");
        if (split.length > 1) {
            String s = split[0];
            if (s.contains("fr_userId")) {
                String comment_id = s.replace("fr_userId=", "");
                comment = comment_id;
            }
        }
        return comment;
    }

    //截取comment
    private String getComment(String url) {
        String comment = "";
        String replace = url.replace("http://106.14.251.200/neworld/user/143?", "");
        String[] split = replace.split("&");
        if (split.length > 2) {
            String s = split[1];
            if (s.contains("comment_id")) {
                String comment_id = s.replace("comment_id=", "");
                comment = comment_id;
            }
        }
        return comment;
    }

    private String getName(String url) {
        String urlDecoderString = "";
        String replace = url.replace("http://106.14.251.200/neworld/user/143?", "");
        //fr_userId=14213
        // comment_id=974
        // remarkName=
        // nickName=%E6%B8%B8%E5%AE%A2728769
        // taskId=1425
        String[] split = replace.split("&");
        if (split.length > 4) {
            String s = split[3];
            if (s.contains("nickName=")) {
                String nameUrl = s.replace("nickName=", "");
                urlDecoderString = Util.getURLDecoderString(nameUrl);
            }
        }
        return urlDecoderString;
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"taskId\":\"" + taskId + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "133_1");
                if (content != null && content.length() > 0) {
                    final HotStatusBean hotStatusBean = GsonUtil.parseJsonToBean(content, HotStatusBean.class);
                    if (hotStatusBean != null && hotStatusBean.getStatus() == 0) {
                        Util.uiThread(new Runnable() {
                            @Override
                            public void run() {
                                initStatus(hotStatusBean);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void initStatus(HotStatusBean parentDetailBean) {
        collectStatus = parentDetailBean.getCollectStatus();
        if (collectStatus == 0) {
            ivFav.setImageResource(R.mipmap.web_collect_red);
        } else {
            ivFav.setImageResource(R.mipmap.web_collect);
        }

        //点赞
        likeStatus = parentDetailBean.getLikeStatus();
        if (likeStatus == 0) {
            ivZan.setImageResource(R.mipmap.web_zan_red);
        } else {
            ivZan.setImageResource(R.mipmap.web_zan);
        }

        HotStatusBean.MenuListBean menuList = parentDetailBean.getMenuList();

        collect_count = menuList.getCollect_count();
        transmit_count = menuList.getTransmit_count();
        like = menuList.getLike();
        //收藏数
        //ibfav.setText(collect_count + "");
        //点赞数
        ibZan.setText(like + "");
        //分享数
        //ibShare.setText(transmit_count + "");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.detail_comment://评论
                Intent intentPhone = new Intent();
                intentPhone.setClass(HotActivity.this, HotCommentActivity.class);
                Bundle bundleSimple = new Bundle();
                bundleSimple.putString("from_uid", String.valueOf(from_uid));
                bundleSimple.putString("taskId", String.valueOf(taskId));
                intentPhone.putExtras(bundleSimple);
                startActivityForResult(intentPhone, 15);
                break;
            case R.id.rl_zan: //点赞
                like();
                break;
            case R.id.rl_fav: //收藏
                collect();
                break;
            case R.id.rl_share: //分享
                share();
                break;
            case R.id.page_tv:
                webView.setVisibility(View.VISIBLE);
                rlPage.setVisibility(View.GONE);
                webView.loadUrl(dianUrl);
                break;
        }
    }

    //收藏
    private void collect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //type 家长圈 status 是否点赞 typestatus 1是点赞  2是收藏
                String url = Fields.BASEURL + "111?userId=" + userId + "&taskId=" + taskId + "&type=3&status=" + collectStatus + "&typeStatus=2";
                String content = NetManager.getInstance().getDanContent(url);
                if (content != null && content.length() > 0) {
                    ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                    if (returnStatus != null && returnStatus.getStatus() == 0) {
                        Util.uiThread(new Runnable() {
                            @Override
                            public void run() {
                                refesCollecthView();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void refesCollecthView() {
        if (collectStatus == 1) {
            ivFav.setImageResource(R.mipmap.web_collect_red);
            collectStatus = 0;
            //collect_count += 1;
            //ibfav.setText(collect_count + "");
        } else {
            ivFav.setImageResource(R.mipmap.web_collect);
            collectStatus = 1;
            //collect_count -= 1;
            //ibfav.setText(collect_count + "");
        }
    }

    //点赞
    private void like() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //type 家长圈 status 是否点赞 typestatus 1是点赞  2是收藏
                String url = Fields.BASEURL + "111?userId=" + userId + "&taskId=" + taskId + "&type=3&status=" + likeStatus + "&typeStatus=1";
                String content = NetManager.getInstance().getDanContent(url);
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
            ibZan.setText(like + "");
            likeStatus = 0;
        } else {
            ivZan.setImageResource(R.mipmap.web_zan);
            like = like - 1;
            ibZan.setText(like + "");
            likeStatus = 1;
        }
    }

    private void share() {
        String url = "http://www.uujz.me:8082/neworld/user/149?taskId=" + taskId + "&userId=" + userId;
        UMWeb web = new UMWeb(url);
        web.setTitle(title);
        web.setDescription(content);
        if (imgs != null && imgs.length() > 0) {
            UMImage image = new UMImage(HotActivity.this, imgs);
            web.setThumb(image);
        }
        new ShareAction(HotActivity.this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(shareListener)
                .open();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 15) {
            if (data != null) {
                if (data.getBooleanExtra("isComment", false)) {
                    webView.loadUrl(baseUrl);
                }
            }
        }
    }

    private UMShareListener shareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(HotActivity.this, "分享成功!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(HotActivity.this, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(HotActivity.this, "已取消", Toast.LENGTH_LONG).show();
        }
    };

}
