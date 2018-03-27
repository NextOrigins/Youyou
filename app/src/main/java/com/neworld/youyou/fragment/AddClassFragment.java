package com.neworld.youyou.fragment;

import android.content.Context;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.activity.AddChildActivity;
import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.SPUtil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;

/**
 * Created by tt on 2017/8/16.
 */
public class AddClassFragment extends BaseFragment implements View.OnClickListener {
    private ImageView ivCancel;
    private TextView tvTitle;
    private TextView tvSave;
    private EditText etName;
    private AddChildActivity activity;
    public int tag;
    private String userId;
    public AddClassFragment(){

    }


   /* public static final AddClassFragment newInstance(int tag)
    {
        AddClassFragment fragment = new AddClassFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("tag",tag);
        fragment.setArguments(bundle);

        return fragment ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            tag = args.getInt("tag");
            passwd = args.getstring("passwd");
        }
    }*/

    @Override
    public View createView() {
        View view = View.inflate(context, R.layout.fragment_my_child_class, null);
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_canel:
                hintKbTwo();
                activity.popBackStack();
                break;
            case R.id.tv_save:
                saveName();

                break;
        }
    }

    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&activity.getCurrentFocus()!=null){
            if (activity.getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void saveName() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String trim = etName.getText().toString().trim();
                if (trim != null && trim.length() > 0) {
                    String base64 = Base64.encodeToString(("{\"type\":\""+tag+"\", \"schoolName\":\""+activity.primary+"\", \"className\":\"\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "168");
                    if (content != null && content.length() > 0) {
                        ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
                        if (returnStatus != null && returnStatus.getStatus() == 0) {
                            Util.uiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (tag == 1) {
                                        activity.gradePrimary = trim;
                                    } else if (tag == 2) {
                                        activity.gradeJunior = trim;
                                    }
                                    hintKbTwo();
                                    activity.popBackStack();
                                }
                            });
                        }
                    }
                } else {
                    ToastUtil.showToast("请输入班级");
                }
            }
        }).start();
    }

    @Override
    public Object getData() {
        return "";
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
