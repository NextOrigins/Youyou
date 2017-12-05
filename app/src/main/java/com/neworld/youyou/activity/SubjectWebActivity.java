package com.neworld.youyou.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neworld.youyou.R;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.bean.SubjectWebBean;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.Fields;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.Util;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

public class SubjectWebActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView ibZan;
    private WebView webView;
    private String userId;
    private int taskId;
    private String baseUrl;
    private String from_uid;
    private String content;
    private String title;

    private ImageView ivZan;
    private ImageView ivFav;

    private int like;
    private int likeStatus;
    private int collectStatus;
    private String imgs;
    private RelativeLayout rlPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_web);
        initUser();
        initView();
        initWeb();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWeb() {
        initData();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        baseUrl = "http://106.14.251.200:8082/neworld/android/158?userId=" + userId + "&taskId=" + taskId + "&maodian=0";
        //baseUrl = "http://192.168.1.123:8080/neworld/android/158?userId="+userId+"&taskId="+ taskId +"&maodian=0";
        webView.loadUrl(baseUrl);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
              /*  view.loadUrl(baseUrl);
                return true;*/
                //String baseUrl = "http://106.14.251.200:8082/neworld/android/108?userId=10850&taskId=730&maodian=";
                //if (url.contains())
                if (url.contains("http://106.14.251.200:8082/neworld/android/158?userId=")) { //本网页的跳转
                    webView.loadUrl(url);
                    return true;
                } else if (url.contains("http://106.14.251.200/neworld/user/154")) {//删除
                    webView.loadUrl(baseUrl);
                    return true;
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //super.onReceivedError(view, errorCode, description, failingUrl);

                webView.setVisibility(View.INVISIBLE);
                rlPage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_parent_focus);
        ImageView ivParentShare = (ImageView) findViewById(R.id.iv_parent_share);
        TextView tvDetail = (TextView) findViewById(R.id.detail_comment);
        ibZan = (TextView) findViewById(R.id.detail_zan);
//        TextView ibfav = (TextView) findViewById(R.id.detail_fav);
//        TextView ibShare = (TextView) findViewById(R.id.detail_share);
        webView = (WebView) findViewById(R.id.parent_webview);
        rlPage = (RelativeLayout) findViewById(R.id.page_tv);
        RelativeLayout rlZan = (RelativeLayout) findViewById(R.id.rl_zan);
        RelativeLayout rlFav = (RelativeLayout) findViewById(R.id.rl_fav);
        RelativeLayout rlShare = (RelativeLayout) findViewById(R.id.rl_share);

        ivZan = (ImageView) findViewById(R.id.iv_zan);
        ivFav = (ImageView) findViewById(R.id.iv_fav);
        ImageView ivShare = (ImageView) findViewById(R.id.iv_share);

        ivBack.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        tvDetail.setOnClickListener(this);
        rlPage.setOnClickListener(this);
        ivParentShare.setOnClickListener(this);
        rlFav.setOnClickListener(this);
        rlZan.setOnClickListener(this);
        rlShare.setOnClickListener(this);
    }

    private void initUser() {
        userId = Sputil.getString(SubjectWebActivity.this, "userId", "");
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        taskId = extras.getInt("taskId");
        from_uid = extras.getString("from_uid");

        title = extras.getString("title", "");//
        content = extras.getString("content", "");
        imgs = extras.getString("img", "");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_parent_focus: //返回
                finish();
                break;
            case R.id.iv_parent_share://分享
                share();
                break;
            case R.id.detail_comment://评论
                Intent intentPhone = new Intent();
                intentPhone.setClass(SubjectWebActivity.this, SubjectCommentActivity.class);
                Bundle bundleSimple = new Bundle();
                bundleSimple.putString("from_userId", from_uid);
                bundleSimple.putString("taskId", String.valueOf(taskId));
                intentPhone.putExtras(bundleSimple);
                startActivityForResult(intentPhone, 16);
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
                webView.loadUrl(baseUrl);
                break;
        }
    }

    private void initData() {
        new Thread(() -> {
            String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"taskId\":\"" + taskId + "\"}").getBytes(), Base64.DEFAULT);
            String replace = base64.replace("\n", "");
            String content = NetManager.getInstance().getContent(replace, "158_1");
            if (content != null && content.length() > 0) {
                final SubjectWebBean subjectWebBean = GsonUtil.parseJsonToBean(content, SubjectWebBean.class);
                if (subjectWebBean != null && subjectWebBean.getStatus() == 0) {
                    Util.uiThread(() -> initStatus(subjectWebBean));
                }
            }
        }).start();
    }

    private void like() {
        new Thread(() -> {
            //type 家长圈 status 是否点赞 typestatus 1是点赞  2是收藏
            String url = Fields.BASEURL + "111?userId=" + userId + "&taskId=" + taskId + "&type=2&status=" + likeStatus + "&typeStatus=1";
            String content = NetManager.getInstance().getDanContent(url);
            if (content != null && content.length() > 0) {
                ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                if (returnStatus != null && returnStatus.getStatus() == 0) {
                    Util.uiThread(this::refeshView);
                }
            }
        }).start();
    }

    //收藏
    private void collect() {
        new Thread(() -> {
            //type 家长圈 status 是否点赞 typestatus 1是点赞  2是收藏
            String url = Fields.BASEURL + "111?userId=" + userId + "&taskId=" + taskId + "&type=2&status=" + collectStatus + "&typeStatus=2";
            String content = NetManager.getInstance().getDanContent(url);
            if (content != null && content.length() > 0) {
                ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                if (returnStatus != null && returnStatus.getStatus() == 0) {
                    Util.uiThread(this::refesCollecthView);
                }
            }
        }).start();
    }

    private void refesCollecthView() {
        if (collectStatus == 1) {
            ivFav.setImageResource(R.mipmap.web_collect_red);
            collectStatus = 0;

        } else {
            ivFav.setImageResource(R.mipmap.web_collect);
            collectStatus = 1;
        }
    }

    //点赞更新
    private void refeshView() {
        if (likeStatus == 1) {
            ivZan.setImageResource(R.mipmap.web_zan_red);
            like = like + 1;
            ibZan.setText(String.valueOf(like));
            likeStatus = 0;
        } else {
            ivZan.setImageResource(R.mipmap.web_zan);
            like = like - 1;
            ibZan.setText(String.valueOf(like));
            likeStatus = 1;
        }
    }

    private void initStatus(SubjectWebBean parentDetailBean) {
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

        SubjectWebBean.MenuListBean menuList = parentDetailBean.getMenuList();
//        int collect_count = menuList.getCollect_count(); // TODO ： 收藏数？
        //transmit_count = menuList.getTransmit_count();
        like = menuList.getLike();
        //收藏数
        //ibfav.setText(collect_count + "");
        //点赞数
        ibZan.setText(String.valueOf(like));
       /* //分享数
        ibShare.setText(transmit_count + "");*/

    }

    private void share() {
        String url = "http://www.uujz.me:8082/neworld/user/162?taskId=" + taskId + "&userId=" + userId;
        UMWeb web = new UMWeb(url);
        web.setTitle(title);
        web.setDescription(content);
        if (imgs != null && imgs.length() > 0) {
            UMImage image = new UMImage(SubjectWebActivity.this, imgs);
            web.setThumb(image);
        }
        new ShareAction(SubjectWebActivity.this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(shareListener)
                .open();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 16) {
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
            Toast.makeText(SubjectWebActivity.this, "成功了", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(SubjectWebActivity.this, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(SubjectWebActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };
}
