package com.neworld.youyou.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.bean.ParentDetailBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.Fields;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.LogUtils;
import com.neworld.youyou.utils.NetUtil;
import com.neworld.youyou.utils.SPUtil;
import com.neworld.youyou.utils.Util;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;


public class ParentDetailActivity extends AppCompatActivity implements View.OnClickListener {

	private ImageView ivBack;
	private TextView tvDetail;
	private TextView ibZan;
	private TextView ibfav;
	private TextView ibShare;
	private WebView webView;
	private String userId;
	private int taskId;
	private String dianUrl;
	private String from_uid;
	private String content;
	private String title;
	private Bundle extras;

	private RelativeLayout rlZan;
	private RelativeLayout rlFav;
	private RelativeLayout rlShare;
	private ImageView ivZan;
	private ImageView ivFav;
	private ImageView ivShare;

	private ImageView ivParentShare;
	private int like;

	private int likeStatus;
	private int collectStatus;
	private ProgressDialog dialog = null;
	private String imgs;

	private RelativeLayout rlPage;
	private RelativeLayout rlWeb;
	private String baseUrl;
	private Intent mIntent;

	private boolean errorShowing;
//	private StandardGSYVideoPlayer video;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parent_detail);
		initUser();
		initView();
	}

	private void initUser() {
		userId = SPUtil.getString(ParentDetailActivity.this, "userId", "");
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView(String id) {
		taskId = Integer.parseInt(id);
		/*video = findViewById(R.id.video);
		Intent intent = getIntent();
		if (intent.getIntExtra("sort", 1) == 2) {
			video.setVisibility(View.VISIBLE);
			video.setLockLand(true);
			video.setUp(intent.getStringExtra("video"), false, "");
			video.getFullscreenButton().setOnClickListener(v -> {
				if (back2()) {
					video.startWindowFullscreen(this, false, true);
				} else {
					ToastUtil.showToast("点击频率太高, 请稍等");
				}
			});
			video.setBackFromFullScreenListener(v -> {
				if (back2()) {
					GSYBaseVideoPlayer.backFromWindowFull(this);
				} else {
					ToastUtil.showToast("点击频率太高, 请稍等");
				}
			});
			if (video.isIfCurrentIsFullscreen())
				video.getBackButton().setVisibility(View.VISIBLE);
			else
				video.getBackButton().setVisibility(View.INVISIBLE);
		} else video.setVisibility(View.GONE);*/
		initData();
		final WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		baseUrl = "http://106.14.251.200:8082/neworld/android/108?userId=" + userId + "&taskId=" + taskId + "&maodian=1";
//        baseUrl = "http://192.168.1.123:8080/neworld/android/108?userId=" + userId + "&taskId=" + taskId + "&maodian=1#maodian1";
		dianUrl = "http://106.14.251.200:8082/neworld/android/108?userId=" + userId + "&taskId=" + taskId + "&maodian=0";
//        dianUrl = "http://192.168.1.123:8080/neworld/android/108?userId=" + userId + "&taskId=" + taskId + "&maodian=0"; // 暂时都用本地
		if (getIntent().getBooleanExtra("dian", false)) webView.loadUrl(dianUrl);
		else webView.loadUrl(baseUrl);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
		     /*   view.loadUrl(url);
                return true;*/
				//String dianUrl = "http://106.14.251.200:8082/neworld/android/108?userId=10850&taskId=730&maodian=";
				//if (url.contains())
				// System.out.println(url + "我是------------------------------------------");
				if (url.contains("http://106.14.251.200:8082/neworld/android/108?userId")) { //本网页的跳转
					webView.loadUrl(baseUrl);
					return true;
				} else if (url.contains("http://106.14.251.200/neworld/user/143?from_userId=")) {//图像跳转
					//webView.loadUrl(dianUrl);
					String replace = url.replace("http://106.14.251.200/neworld/user/143?from_userId=", "");
					Intent intentPhone = new Intent();
					intentPhone.setClass(ParentDetailActivity.this, DetailDataActivity.class);
					Bundle bundleSimple = new Bundle();
					bundleSimple.putString("from_uid", replace);
					intentPhone.putExtras(bundleSimple);
					startActivity(intentPhone);
					return true;
				} else if (url.contains("http://106.14.251.200/neworld/user/154")) {//删除
					webView.loadUrl(dianUrl);
					return true;
				} else if (url.contains("http://106.14.251.200/neworld/user/143?fr_userId=")) {
					String name = getName(url);
					String comment = getComment(url);
					String fromId = getUid(url);
					Intent intentPhone = new Intent();
					intentPhone.setClass(ParentDetailActivity.this, CommentActivity.class);
					Bundle bundleSimple = new Bundle();
					bundleSimple.putString("nickName", name);
					bundleSimple.putString("from_userId", fromId);
					bundleSimple.putString("taskId", String.valueOf(taskId));
					bundleSimple.putString("commentId", comment);
					intentPhone.putExtras(bundleSimple);
					startActivityForResult(intentPhone, 5);
					webView.loadUrl(dianUrl);
				}
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				//super.onPageFinished(view, url);
				dialog.dismiss();
				errorShowing = false;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				//super.onPageStarted(view, url, favicon);
				dialog = ProgressDialog.show(ParentDetailActivity.this, null, "页面加载中，请稍后..");
				new Handler().postDelayed(() -> dialog.dismiss(), 1500);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				//super.onReceivedError(view, errorCode, description, failingUrl);
				if (dialog != null) {
					dialog.dismiss();
				}

				showNotInternet();
			}
		});

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				CharSequence pnotfound = "404";

				if (title.contains(pnotfound) || title.equals("找不到网页")) {
					view.stopLoading();
					view.loadUrl(NOT_FOUND_PAGE);
				}
			}

		});
	}

	private void showNotInternet() {
		rlPage.setVisibility(View.VISIBLE);
		webView.setVisibility(View.GONE);
		errorShowing = true;
	}

	int resultCode = 4;

	public static final String NOT_FOUND_PAGE = "file:///android_asset/not_found.html";

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

	//http://106.14.251.200/neworld/user/143?fr_userId=14213&comment_id=974&remarkName=&nickName=%E6%B8%B8%E5%AE%A2728769&taskId=1425
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
				String content = NetManager.getInstance().getContent(replace, "108_1");
				if (content != null && content.length() > 0) {
					final ParentDetailBean parentDetailBean = GsonUtil.parseJsonToBean(content, ParentDetailBean.class);
					if (parentDetailBean != null && parentDetailBean.getStatus() == 0) {
						Util.uiThread(new Runnable() {
							@Override
							public void run() {
								initStatus(parentDetailBean);
							}
						});
					}
				}
			}
		}).start();
	}

	private void initStatus(ParentDetailBean parentDetailBean) {

		collectStatus = parentDetailBean.getCollectStatus();
		if (collectStatus == 0) {
			ivFav.setImageResource(R.mipmap.web_collect_red);
		} else {
			ivFav.setImageResource(R.mipmap.web_collect);

		}
		//点赞
		likeStatus = parentDetailBean.getLikeStatus();
		if (likeStatus == 0)
			ivZan.setImageResource(R.mipmap.web_zan_red);
		else
			ivZan.setImageResource(R.mipmap.web_zan);

		ParentDetailBean.MenuListBean menuList = parentDetailBean.getMenuList();
		if (menuList != null) {
			like = menuList.getLike();
		}
		//点赞数
		ibZan.setText(String.valueOf(like));
	}

	private void initView() {
		ivBack = (ImageView) findViewById(R.id.iv_parent_focus);
		ivParentShare = (ImageView) findViewById(R.id.iv_parent_share);
		tvDetail = (TextView) findViewById(R.id.detail_comment);
		ibZan = (TextView) findViewById(R.id.detail_zan);
		ibfav = (TextView) findViewById(R.id.detail_fav);
		ibShare = (TextView) findViewById(R.id.detail_share);
		webView = (WebView) findViewById(R.id.parent_webview);
		rlWeb = (RelativeLayout) findViewById(R.id.rl_web);

		rlZan = (RelativeLayout) findViewById(R.id.rl_zan);
		rlFav = (RelativeLayout) findViewById(R.id.rl_fav);
		rlShare = (RelativeLayout) findViewById(R.id.rl_share);
		rlPage = (RelativeLayout) findViewById(R.id.page_tv);
		ivZan = (ImageView) findViewById(R.id.iv_zan);
		ivFav = (ImageView) findViewById(R.id.iv_fav);
		ivShare = (ImageView) findViewById(R.id.iv_share);//分享
		rlPage.setOnClickListener(this);
		extras = getIntent().getExtras();
		assert extras != null;
		String taskId = extras.getString("taskId", "");//条目Id
		title = extras.getString("title", "");//
		content = extras.getString("content", "");
		from_uid = extras.getString("from_uid", "");
		imgs = extras.getString("image", "");

		//收藏
		if (!TextUtils.isEmpty(taskId)) {
			if (NetUtil.isNetworkConnected(getApplicationContext())) {
				errorShowing = false;
				initWebView(taskId); // TODO : 增加网络判断。
			} else showNotInternet();
		}
		ivBack.setOnClickListener(this);
		tvDetail.setOnClickListener(this);

		ivParentShare.setOnClickListener(this);
		rlFav.setOnClickListener(this);
		rlZan.setOnClickListener(this);
		rlShare.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_parent_focus: //返回
				if (mIntent == null)
					mIntent = new Intent();
				mIntent.putExtra("isCollectStatus", collectStatus);
				mIntent.putExtra("isLikeStatus", likeStatus);
				// 设置结果，并进行传送
				this.setResult(resultCode, mIntent);
				finish();
				break;
			case R.id.iv_parent_share://分享
				share();
				break;
			case R.id.detail_comment://评论
				Intent intentPhone = new Intent();
				intentPhone.setClass(ParentDetailActivity.this, CommentActivity.class);
				Bundle bundleSimple = new Bundle();
				bundleSimple.putString("from_userId", from_uid);
				bundleSimple.putString("taskId", String.valueOf(taskId));
				intentPhone.putExtras(bundleSimple);
				startActivityForResult(intentPhone, 5);
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
				loadData();
				break;
		}
	}

	private void loadData() {
		webView.setVisibility(View.VISIBLE);
		rlPage.setVisibility(View.GONE);
		webView.loadUrl(baseUrl);
	}

	//收藏
	private void collect() {
		new Thread(() -> {
			//type 家长圈 status 是否点赞 typestatus 1是点赞  2是收藏
			String url = Fields.BASEURL + "111?userId=" + userId + "&taskId=" + taskId + "&type=1&status=" + collectStatus + "&typeStatus=2";
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
		new Thread(() -> {
//			type 家长圈 status 是否点赞 typestatus 1是点赞  2是收藏
			String url = Fields.BASEURL + "111?userId=" + userId + "&taskId=" + taskId + "&type=1&status=" + likeStatus + "&typeStatus=1";
			String content = NetManager.getInstance().getDanContent(url);
			if (content != null && content.length() > 0) {
				LogUtils.E("content : " + content);
				ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
				if (returnStatus != null && returnStatus.getStatus() == 0) {
					Util.uiThread(this::refreshView);
				}
			}
		}).start();
	}

	//点赞更新
	private void refreshView() {
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 5) {
			if (data != null) {
				if (data.getBooleanExtra("isPublish", false)) {
					webView.loadUrl(dianUrl);
				}
			}
		}
	}

	private void share() {
		String url = "http://www.uujz.me:8082/neworld/user/143?taskId=" + taskId + "&userId=" + userId;
		UMWeb web = new UMWeb(url);
		web.setTitle(title);
		web.setDescription(content);
		if (imgs != null && imgs.length() > 0) {
			UMImage image = new UMImage(ParentDetailActivity.this, imgs);
			web.setThumb(image);
		}
		new ShareAction(ParentDetailActivity.this)
				.withMedia(web)
				.setDisplayList(SHARE_MEDIA.QZONE, SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
				.setCallback(shareListener)
				.open();
	}

	@Override
	protected void onResume() {
		super.onResume();
//        if (errorShowing) {
//            if (NetBuild.isNetworkConnected(getApplicationContext()))
//                loadData();
//        }
//		video.onVideoResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
//		video.onVideoPause();
	}

	@Override
	protected void onDestroy() {
//		video.destroyDrawingCache();
		super.onDestroy();
	}

	private UMShareListener shareListener = new UMShareListener() {

		@Override
		public void onStart(SHARE_MEDIA platform) {
			LogUtils.E("onStart");
		}

		@Override
		public void onResult(SHARE_MEDIA platform) {
//            ToastUtil.showToast("onResult");
//            LogUtils.logE("onResult");
//            Map<CharSequence, CharSequence> map = new HashMap<>();
//            map.put("taskId", String.valueOf(taskId));
//            map.put("type", "1");
//            new Thread(() -> NetBuild.getResponse(map, 144)).start();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			LogUtils.E("onError");
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			LogUtils.E("onCancel");
		}
	};

	@Override
	public void onBackPressed() {
		if (mIntent == null) {
			mIntent = new Intent();
		}
		mIntent.putExtra("isCollectStatus", collectStatus);
		mIntent.putExtra("isLikeStatus", likeStatus);
		// 设置结果，并进行传送
		this.setResult(resultCode, mIntent);
		super.onBackPressed();
	}

	private boolean b;

	private boolean back2() {
		if (!b) {
			b = true;
			new Handler().postDelayed(() -> {
				b = false;
			}, 1500);
			return b;
		} else return !b;
	}

	private boolean isConnectInternet() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = manager.getAllNetworkInfo();
		for (NetworkInfo state : info) {
			if (state.getState() == NetworkInfo.State.CONNECTED)
				return true;
		}
		return false;
	}
}
