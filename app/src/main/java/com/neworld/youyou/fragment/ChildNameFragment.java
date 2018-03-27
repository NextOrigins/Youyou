package com.neworld.youyou.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.activity.AddChildActivity;
import com.neworld.youyou.bean.ChildReturnBean;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.SPUtil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;

/**
 * Created by tt on 2017/8/14.
 */

public class ChildNameFragment extends BaseFragment implements View.OnClickListener, FragmentBackHandler {

    private int babyId;
    private ImageView ivCancel;
    private TextView tvTitle;
    private TextView tvSave;
    private EditText etName;

    private AddChildActivity activity;
    private String userId;
    private RequestOb ob;

   /* public ChildNameFragment(int babyId) {
        this.babyId = babyId;
    }*/

    @Override
    public View createView() {
        View view = View.inflate(context, R.layout.fragment_my_child_msg, null);
        ivCancel = ((ImageView) view.findViewById(R.id.iv_canel));
        tvTitle = ((TextView) view.findViewById(R.id.tv_title));
        tvSave = ((TextView) view.findViewById(R.id.tv_save));
        etName = ((EditText) view.findViewById(R.id.et_name));
        activity = ((AddChildActivity) getActivity());
        initUser();
        initView();
        return view;
    }

    private void initUser() {
        userId = SPUtil.getString(context, "userId", "");
    }

    private void initView() {
        ivCancel.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        if (activity.name != null) {
            etName.setHint(activity.name);
        }
    }

    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            if (activity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public Object getData() {

        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_canel:
                if (etName.getText().toString().trim() != null && etName.getText().toString().trim().length() > 0) {
                    tanDialog();
                } else {
                    hintKbTwo();
                    activity.popBackStack();
                }
                break;
            case R.id.tv_save:
                saveName();

                break;
        }
    }

    private int isBack = 100;

    private void tanDialog() {
        final AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setMessage("确定退出编辑吗");
        ad.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isBack = 1;
                        dialog.dismiss();
                        hintKbTwo();
                        activity.popBackStack();
                    }
                });
        ad.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isBack = 2;
                        dialog.dismiss();
                    }
                });
        ad.show();
    }

    private void saveName() {
        hintKbTwo();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = etName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\",\"name\":\"" + name + "\", \"sex\":\"\",\"babyId\":\"" + babyId + "\", \"birthday\":\"\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "140");
                    if (content != null && content.length() > 0) {
                        ChildReturnBean childReturnBean = GsonUtil.parseJsonToBean(content, ChildReturnBean.class);
                        if (childReturnBean != null && childReturnBean.getStatus() == 0) {
                            activity.babyId = childReturnBean.getBabyId();
                            activity.name = name;
                            if (ob != null)
                                ob.onResponse(childReturnBean.getBabyId());
                        }
                    }
                    Util.uiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.popBackStack();
                        }
                    });
                } else {
                    ToastUtil.showToast("姓名不能为空");
                }
            }
        }).start();
    }

    @Override
    public boolean onBackPressed() {
        if (etName.getText().toString().trim() != null && etName.getText().toString().trim().length() > 0) {
            tanDialog();
            if (isBack == 1) {
                return true;
            } else {
                return false;
            }
        } else {
            hintKbTwo();
            return true;
        }
    }

    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }

    public void setObserver(RequestOb b) {
        ob = b;
    }

    public interface RequestOb {
        void onResponse(int babyId);
    }
}
