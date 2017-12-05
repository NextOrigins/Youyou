package com.neworld.youyou.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.neworld.youyou.MainActivity;
import com.neworld.youyou.R;
import com.neworld.youyou.activity.FocusActivity;
import com.neworld.youyou.activity.PublishActivity;
import com.neworld.youyou.utils.NetUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;

/**
 * @author by tt on 2017/8/31.
 */

public class ParentPageFragment extends BaseFragment implements View.OnClickListener {

    private View view;

    @Override
    public View createView() {
        view = View.inflate(context, R.layout.fragment_parent_page, null);
        initUser();
        initView();
        return view;
    }

    private void initView() {
        ImageView ivParentPublish = (ImageView) view.findViewById(R.id.iv_parent_publish);
        ImageView ivFocus = ((ImageView) view.findViewById(R.id.iv_parent_focus));
        ivParentPublish.setOnClickListener(this);
        ivFocus.setOnClickListener(this);
        FragmentManager supportFragmentManager =  getActivity().getSupportFragmentManager();
        supportFragmentManager.beginTransaction().replace(R.id.fl_content, new ParentFragment()).commit();
    }

    private void initUser() {
        String userId = Sputil.getString(context, "userId", "");
    }
    @Override
    public Object getData() {
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_parent_focus:
                startActivity(new Intent(getContext(), FocusActivity.class));
                break;
            case R.id.iv_parent_publish:
                if (!NetUtil.isNetworkConnected(this.getContext())) {
                    ToastUtil.showToast("请连接网络");
                    break;
                }
                startActivityForResult(new Intent(getContext(), PublishActivity.class), 11);
                break;
        }
    }
}
