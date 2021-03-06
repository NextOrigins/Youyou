package com.neworld.youyou.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.neworld.youyou.MainActivity;
import com.neworld.youyou.R;
import com.neworld.youyou.activity.AddChildActivity;
import com.neworld.youyou.activity.LoginActivity;
import com.neworld.youyou.activity.MyCollectActivity;
import com.neworld.youyou.activity.PersonDataActivity;
import com.neworld.youyou.activity.PhotoActivity;
import com.neworld.youyou.activity.SettingActivity;
import com.neworld.youyou.add.feed.FeedbackActivity;
import com.neworld.youyou.bean.HotBean;
import com.neworld.youyou.bean.PersonDataBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.MyApplication;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
<<<<<<< HEAD
import com.neworld.youyou.utils.SPUtil;
=======
import com.neworld.youyou.utils.Sputil;
>>>>>>> parent of 8d52dad... 17_12_19
import com.neworld.youyou.utils.Util;
import com.neworld.youyou.view.mview.my.BooksOrderActivity;
import com.neworld.youyou.view.mview.my.DynamicActivity;
import com.neworld.youyou.view.nine.CircleImageView;

import java.util.List;

/**
 * Created by tt on 2017/7/18.
 */

public class MyFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private ImageView ivTitle;
    private RelativeLayout rlPerson;
    private RelativeLayout rlSubject;
    private RelativeLayout rlChild;
    private RelativeLayout rlFav;
    private RelativeLayout rlSetting;
    private RelativeLayout rlMoving;
    private CircleImageView circleImageView;

    private String imageUrl;
    private TextView tvName;
    private String userId;
    private String token;
    private MainActivity activity;
    private TextView mTv_name;

    private LinearLayout parent;
    private View dialogView;

<<<<<<< HEAD
    private View msgHint;

    private int newMsg;
    private String[] typeArrays;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        if (arguments != null) {
            typeArrays = arguments.getStringArray("typeArray");
        }
    }
=======
>>>>>>> parent of 8d52dad... 17_12_19

    @Override
    public View createView() {
        view = View.inflate(context, R.layout.fragment_my, null);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        activity = ((MainActivity) getActivity());
        initUser();
        initView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parent = root.findViewById(R.id._parent);
        dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_order, parent, false);
    }

    private void initUser() {
        userId = SPUtil.getString(context, "userId", "");
        token = SPUtil.getString(context, "token", "");
    }

	@Override
	public void onStart() {
		super.onStart();
        msgHint.setVisibility(View.GONE);
//		new Thread(() -> {
//			String response = NetBuild.getResponse("{\"userId\":\"" + userId + "\"}", 194); // 194 是否收到消息
//            NewStatusBody b;
//			try {
//			    b = new Gson().fromJson(response, new TypeToken<NewStatusBody>(){}.getType());
//            } catch (Exception e) {
//			    b = null;
//            }
//			if (b == null) {
//                ToastUtil.showToast("数据错误, 请到用户反馈处反馈此问题[MF116]");
//				return;
//			}
//			if (b.status == 0) {
//				newMsg = b.newMeStatus;
//				toggleRound(b.newMeStatus == 1);
//			}
//		}).start();
	}

	@Override
    public void onResume() {
        super.onResume();
        String personName = SPUtil.getString(context, "personName", "");
        if (personName != null && personName.length() > 0) {
            tvName.setText(personName);
            SPUtil.saveString(context, "personName", "");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 21) {
            if (data != null) {
                boolean isRefresh = data.getBooleanExtra("isRefresh", false);
                if (isRefresh) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getData();
                        }
                    }).start();
                }
            }
        } else if (requestCode == 5) {
            if (data != null) {
                boolean isRefresh = data.getBooleanExtra("isRefresh", false);
                String url = data.getStringExtra("url");
                if (isRefresh) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getData();
                        }
                    }).start();
                } else {
                    if (!TextUtils.isEmpty(url)) {
                        RequestOptions MyFragmentOptions = new RequestOptions();
                        MyFragmentOptions.error(R.mipmap.my_icon);
                        Glide.with(context).load(url).apply(MyFragmentOptions).into(circleImageView);
                    }
                }
            }
        }
    }

    private void initView() {
        ivTitle = (ImageView) view.findViewById(R.id.iv_my_cicle);
        rlPerson = (RelativeLayout) view.findViewById(R.id.my_person_data);
        rlChild = (RelativeLayout) view.findViewById(R.id.my_child);
        rlSubject = (RelativeLayout) view.findViewById(R.id.my_subject);
        rlFav = (RelativeLayout) view.findViewById(R.id.my_favorites);
        rlSetting = (RelativeLayout) view.findViewById(R.id.my_setting);
        circleImageView = (CircleImageView) view.findViewById(R.id.iv_my_cicle);
        msgHint = view.findViewById(R.id._msg_hint);
        rlMoving = view.findViewById(R.id.my_moving);
        //mTv_name = (TextView) view.findViewById(R.id.tv_name);
        $(view, R.id.feed_back).setOnClickListener(this);
        ivTitle.setOnClickListener(this);
        rlPerson.setOnClickListener(this);
        rlChild.setOnClickListener(this);
        rlSubject.setOnClickListener(this);
        rlFav.setOnClickListener(this);
        rlSetting.setOnClickListener(this);
        rlMoving.setOnClickListener(this);

<<<<<<< HEAD
//        view.findViewById(R.id.chengji) // TODO : 成绩查询暂时去掉
//                .setOnClickListener(v -> startActivity(new Intent(getContext(), AchievementActivity.class)));
=======
        view.findViewById(R.id.chengji).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AchievementActivity.class));
        });
>>>>>>> parent of 8d52dad... 17_12_19
    }

    @Override
    public Object getData() {
        String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"token\":\"" + token + "\"}").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        final String content = NetManager.getInstance().getContent(replace, "126");
//        System.out.println(content);
        if (!TextUtils.isEmpty(content)) {
            PersonDataBean personDataBean = GsonUtil.parseJsonToBean(content, PersonDataBean.class);
            if (personDataBean != null && personDataBean.getStatus() == 0) {
                int tokenStatus = personDataBean.getTokenStatus();
                judgeToken(tokenStatus);
                final PersonDataBean.MenuListBean menuList = personDataBean.getMenuList();
                if (menuList != null) {
                    Util.uiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(menuList.getFaceImg())) {
                                imageUrl = menuList.getFaceImg();
                                Glide.with(context).load(menuList.getFaceImg()).into(circleImageView);
                            } else {
                                Glide.with(context).load(R.mipmap.my_icon).into(circleImageView);
                            }

<<<<<<< HEAD
                        if (!TextUtils.isEmpty(menuList.getNickName())) {
                            tvName.setText(menuList.getNickName());
                        } else if (!TextUtils.isEmpty(menuList.getUserAccount())) {
                            tvName.setText(menuList.getUserAccount());
                        } else if (!TextUtils.isEmpty(menuList.getUserName())) {
                            tvName.setText(menuList.getUserName());
                        } else {
                            tvName.setText("Nil");
=======
                            if (!TextUtils.isEmpty(menuList.getNickName())) {
                                tvName.setText(menuList.getNickName());
                            } else if (!TextUtils.isEmpty(menuList.getUserAccount())) {
                                tvName.setText(menuList.getUserAccount());
                            } else if (!TextUtils.isEmpty(menuList.getUserName())) {
                                tvName.setText(menuList.getUserName());
                            } else {
                                tvName.setText("fjhjh");
                            }
>>>>>>> parent of 8d52dad... 17_12_19
                        }
                    });
                }
            } else {
                Util.uiThread(new Runnable() {
                    @Override
                    public void run() {
                        circleImageView.setImageResource(R.mipmap.my_icon);
                    }
                });
            }
        }
        return content;
    }

    @Override
    protected List<HotBean.MenuListBean> getListBean() {
        return null;
    }

    @Override
    protected ListView getListView() {
        return null;
    }

    //根据token挤掉线
    private void judgeToken(final int tokenStatus) {
        if (tokenStatus == 2) {
            Util.uiThread(new Runnable() {
                @Override
                public void run() {
                    quit();
                }
            });
        }
    }

    private void quit() {
        new Thread(() -> {
            String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\"}").getBytes(), Base64.DEFAULT);
            String replace = base64.replace("\n", "");
            String content = NetManager.getInstance().getContent(replace, "152");

            if (content != null && content.length() > 0) {
                ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                if (returnStatus != null && returnStatus.getStatus() == 0) {
                    SPUtil.saveString(context, "userId", "");
                    startActivity(new Intent(context, LoginActivity.class).putExtra("login2", true));
                    MyApplication mainApplication = activity.getMainApplication();
                    mainApplication.removeALLActivity_();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_my_cicle:
                Intent intent = new Intent();
                intent.setClass(context, PhotoActivity.class);
                Bundle bundle = new Bundle();
                if (imageUrl != null && imageUrl.length() > 0) {
                    bundle.putString("imageUrl", imageUrl);
                } else {
                    bundle.putString("imageUrl", "");
                }
                intent.putExtras(bundle);
                startActivityForResult(intent, 21);
                break;
            case R.id.my_person_data:
                startActivityForResult(new Intent(context, PersonDataActivity.class), 5);
                break;
            case R.id.my_child:
                startActivity(new Intent(context, AddChildActivity.class));
                break;
            case R.id.my_subject:
                // 暂时取消考证订单
//	            startActivity(new Intent(context, MySubjectActivity.class));
                // 图书订单
                startActivity(new Intent(context, BooksOrderActivity.class));
                // 暂时没有图书订单
                /*AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);
                AlertDialog show = builder.show();
                show.getWindow().setBackgroundDrawableResource(R.drawable.dialog_fillet);
                dialogView.findViewById(R.id._study_order).setOnClickListener(study -> {
                    startActivity(new Intent(context, MySubjectActivity.class));
                    show.dismiss();
                });
                dialogView.findViewById(R.id._books_order).setOnClickListener(books -> {
                    startActivity(new Intent(context, BooksOrderActivity.class));
                    show.dismiss();
                });*/
                break;
            case R.id.my_favorites:
                startActivity(new Intent(context, MyCollectActivity.class));
                break;
            case R.id.my_setting:
	            startActivity(new Intent(context, SettingActivity.class));
                break;
            case R.id.feed_back:
                startActivity(new Intent(context, FeedbackActivity.class));
                break;
            case R.id.my_moving:
                startActivity(new Intent(context, DynamicActivity.class));
                break;
        }
    }

    /*public void toggleRound(boolean b) {
	    Util.uiThread(() -> msgHint.setVisibility(b ? View.VISIBLE : View.GONE));
    }*/

    private <T extends View> T $(View v, int res) {
        return v.findViewById(res);
    }

    private static class NewStatusBody {
    	int newMeStatus;
    	int status;
    }
}
