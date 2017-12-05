package com.neworld.youyou.fragment;

import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.activity.AddChildActivity;
import com.neworld.youyou.bean.ChildGenderBean;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.Util;

/**
 * Created by tt on 2017/8/14.
 */

public class ChildGenderFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout llBoy;
    private LinearLayout llGirl;
    private ImageView ivBoy;
    private ImageView ivGirl;
    private AddChildActivity activity;
    private ImageView ivCancel;
    private TextView tvSave;
    private int tempGender = 0;
    private String userId;

    @Override
    public View createView() {
        View view = View.inflate(context, R.layout.fragment_child_gender, null);

        llBoy = ((LinearLayout) view.findViewById(R.id.ll_boy));
        llGirl = ((LinearLayout) view.findViewById(R.id.ll_girl));
        ivBoy = ((ImageView) view.findViewById(R.id.iv_boy_choose));
        ivGirl = ((ImageView) view.findViewById(R.id.iv_girl_choose));
        ivCancel = ((ImageView) view.findViewById(R.id.iv_cancel));
        tvSave = ((TextView) view.findViewById(R.id.tv_save));
        activity = ((AddChildActivity) getActivity());
        initUser();
        initView();
        return view;
    }

    private void initUser() {
        userId = Sputil.getString(context, "userId", "");
    }

    private void initView() {
        llBoy.setOnClickListener(this);
        llGirl.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        ivGirl.setVisibility(View.GONE);
        tvSave.setOnClickListener(this);
    }

    @Override
    public Object getData() {
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_boy:
                chooseBoy();
                break;
            case R.id.ll_girl:
                chooseGirl();
                break;
            case R.id.iv_cancel:
                activity.popBackStack();
                break;
            case R.id.tv_save:
                saveMsg();
                break;
        }
    }
    //选择女
    private void chooseGirl() {
        ivBoy.setVisibility(View.GONE);
        ivGirl.setVisibility(View.VISIBLE);
        tempGender = 1;
    }
    //选择男
    private void chooseBoy() {
        ivGirl.setVisibility(View.GONE);
        ivBoy.setVisibility(View.VISIBLE);
        tempGender = 0;
    }
    //保存信息
    private void saveMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int babyId = activity.babyId;
                String base64 = Base64.encodeToString(("{\"userId\":\""+ userId +"\",\"name\":\"\", \"sex\":\"" + tempGender + "\",\"babyId\":\"" + babyId + "\", \"birthday\":\"\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "140");
                if (content != null && content.length() > 0) {
                    ChildGenderBean childGenderBean = GsonUtil.parseJsonToBean(content, ChildGenderBean.class);
                    if (childGenderBean != null && childGenderBean.getStatus() == 0) {
                        if (tempGender == 0) {
                            activity.gender = "男";
                        } else if (tempGender == 1){
                            activity.gender = "女";
                        }
                    }
                }
                Util.uiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.popBackStack();
                    }
                });
            }
        }).start();
    }
}
