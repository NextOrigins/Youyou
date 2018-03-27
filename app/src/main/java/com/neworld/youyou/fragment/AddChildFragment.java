package com.neworld.youyou.fragment;

import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.neworld.youyou.R;
import com.neworld.youyou.activity.AddChildActivity;
import com.neworld.youyou.adapter.AddChildAdapter;
import com.neworld.youyou.adapter.ChildDetailFragment;
import com.neworld.youyou.bean.ChildDetailBean;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.SPUtil;
import com.neworld.youyou.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tt on 2017/8/14.
 */

public class AddChildFragment extends BaseFragment implements View.OnClickListener {

    private ImageView ivCancel;
    private ListView listView;
    private Button button;
    private AddChildActivity activity;
    List<ChildDetailBean.ResultsBean> mDatas = new ArrayList<>();
    private AddChildAdapter adapter;
    private String userId;
    private boolean isFrist = true;
    ChildDetailFragment mChildDetailFragment;
    @Override
    public View createView() {
        View view = View.inflate(context, R.layout.fragment_my_child, null);
        activity = ((AddChildActivity) getActivity());
        ivCancel = ((ImageView) view.findViewById(R.id.iv_cancel));
        listView = ((ListView) view.findViewById(R.id.lv_my_child));
        button = ((Button) view.findViewById(R.id.bt_my_child));
        initUser();
        initView();
        return view;
    }

    private void initUser() {
        userId = SPUtil.getString(context, "userId", "");
    }

    @Override
    public void onPause() {
        super.onPause();
        isFrist = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFrist) {
        } else {
            initNetData();
        }
    }

    private void initView() {
        ivCancel.setOnClickListener(this);
        button.setOnClickListener(this);
        if (adapter == null) {
            adapter = new AddChildAdapter(context, mDatas);
            listView.setAdapter(adapter);
        }
        //点击条目
        listView.setOnItemClickListener((parent, view, position, id) -> {

            ChildDetailBean.ResultsBean resultsBean = mDatas.get(position);
            if (resultsBean != null) {
                int babyId = resultsBean.getId();
                if (babyId != 0) {
                    cleanData();
                    activity.babyId = babyId;

                     mChildDetailFragment = new ChildDetailFragment();
                    mChildDetailFragment.setBybyId(babyId);
                    activity.changePage(mChildDetailFragment);
                }
            }
        });


        //长按删除
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dialogDelete(position);
                return true;
            }
        });
    }

    public ChildDetailFragment getChildeDettailFragment(){
        return mChildDetailFragment;
    }

    //删除dialog
    private void dialogDelete(final int position) {
        final AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setMessage("确定要删除吗");
        ad.setPositiveButton("确定",
                (dialog, which) -> {
                    deleteChild(position);
                    dialog.dismiss();
                });
        ad.setNegativeButton("取消",
                (dialog, which) -> dialog.dismiss());
        ad.show();
    }
    //删除孩子
    private void deleteChild(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChildDetailBean.ResultsBean resultsBean = mDatas.get(position);
                if (resultsBean != null) {
                    int id = resultsBean.getId();
                    String base64 = Base64.encodeToString(("{\"babyId\":\""+id+"\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "145");
                    if (content != null && content.length() > 0) {
                        ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                        if (returnStatus != null && returnStatus.getStatus() == 0) {
                            try {
                                mDatas.remove(position);
                            }catch (Exception e) {

                            }
                            Util.uiThread(() -> adapter.notifyDataSetChanged());
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public Object getData() {
        initNetData();
        return "";
    }

    private void initNetData() {
        new Thread(() -> {
            String base64 = Base64.encodeToString(("{\"userId\":\""+ userId +"\"}").getBytes(), Base64.DEFAULT);
            String replace = base64.replace("\n", "");
            String content = NetManager.getInstance().getContent(replace, "141");
            if (content != null && content.length() > 0) {
                ChildDetailBean childDetailBean = GsonUtil.parseJsonToBean(content, ChildDetailBean.class);
                if (childDetailBean != null && childDetailBean.getStatus() == 0) {
                    List<ChildDetailBean.ResultsBean> results = childDetailBean.getResults();
                    if (results != null && results.size() > 0) {
                        mDatas.clear();
                        mDatas.addAll(results);
                        Util.uiThread(() -> adapter.notifyDataSetChanged());
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                activity.finish();
                break;
            case R.id.bt_my_child:
                cleanData();
                activity.babyId = 0;
                ChildDetailFragment childDetailFragment = new ChildDetailFragment();
                childDetailFragment.setBybyId(0);
                activity.changePage(childDetailFragment);
                break;
        }
    }

    private void cleanData() {
        activity.name = "";
        activity.gender = "";
        activity.birth = "";
        activity.school ="";
        activity.kindergarten = "";
        activity.locationKindergarten = "";
        activity.gradeKindergarten = "";
        activity.timeKindergarten = "";
        activity.primary = "";
        activity.gradePrimary = "";
        activity.timePrimary = "";
        activity.locationPrimary = "";
        activity.junior = "";
        activity.gradeJunior ="";
        activity.locationJunior = "";
        activity.timeJunior = "";
    }
}
