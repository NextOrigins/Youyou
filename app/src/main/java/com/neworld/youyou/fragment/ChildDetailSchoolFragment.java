package com.neworld.youyou.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.activity.AddChildActivity;
import com.neworld.youyou.utils.Sputil;

/**
 * Created by tt on 2017/8/15.
 */

public class ChildDetailSchoolFragment extends BaseFragment implements View.OnClickListener {

    private ImageView ivCancel;
    private TextView tvChildTitle;
    private RelativeLayout rlChildYou;
    private TextView tvChildYou;
    private RelativeLayout rlChildXiao;
    private TextView tvChildXiao;
    private RelativeLayout rlChildChu;
    private TextView tvChildChu;
    private AddChildActivity activity;
    private String userId;

    @Override
    public View createView() {
        View view = View.inflate(context, R.layout.fragment_child_school, null);
        ivCancel = ((ImageView) view.findViewById(R.id.add_child_cancel));
        tvChildTitle = ((TextView) view.findViewById(R.id.add_child_my_name));
        activity = ((AddChildActivity) getActivity());
        rlChildYou = ((RelativeLayout) view.findViewById(R.id.add_child_you));
        tvChildYou = ((TextView) view.findViewById(R.id.tv_add_you));
        rlChildXiao = ((RelativeLayout) view.findViewById(R.id.add_child_xiao));
        tvChildXiao = ((TextView) view.findViewById(R.id.tv_add_xiao));
        rlChildChu = ((RelativeLayout) view.findViewById(R.id.add_child_chu));
        tvChildChu = ((TextView) view.findViewById(R.id.tv_add_chu));
        initUser();
        setSchoolMes();
        initView();
        return view;
    }

    private void initUser() {
        userId = Sputil.getString(context, "userId", "");
    }

    @Override
    public void onResume() {
        super.onResume();
        setSchoolMes();
    }

    private void setSchoolMes() {
        //幼儿园
        if (activity.kindergarten != null && activity.kindergarten.length() > 0) {
            tvChildYou.setText(activity.kindergarten);
        } else {
            tvChildYou.setText("");
        }
        //小学
        if (activity.primary != null && activity.primary.length() > 0) {
            tvChildXiao.setText(activity.primary);
        } else {
            tvChildXiao.setText("");
        }
        //初中
        if (activity.junior != null && activity.junior.length() > 0) {
            tvChildChu.setText(activity.junior);
        } else {
            tvChildChu.setText("");
        }

    }

    private void initView() {
        ivCancel.setOnClickListener(this);
        rlChildYou.setOnClickListener(this);
        rlChildXiao.setOnClickListener(this);
        rlChildChu.setOnClickListener(this);
    }

    @Override
    public Object getData() {
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_child_cancel:
                activity.popBackStack();
                break;
            case R.id.add_child_you:
                activity.changePage(new KindergartenFragment());
                break;
            case R.id.add_child_xiao:
                activity.changePage(new PrimaryFragment());
                break;
            case R.id.add_child_chu:
                activity.changePage(new JuniorFragment());
                break;
        }
    }
}
