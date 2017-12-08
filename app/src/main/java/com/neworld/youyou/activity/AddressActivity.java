package com.neworld.youyou.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.neworld.youyou.R;
import com.neworld.youyou.add.common.Adapter;
import com.neworld.youyou.bean.AddressBean;
import com.neworld.youyou.dialog.DialogUtils;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.LogUtils;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ThreadUtils;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置地址页面
 * si
 */
public class AddressActivity extends AppCompatActivity implements View.OnClickListener {

    private String mUserId;
    private Adapter<AddressBean.MenuListBean> mAdapter;
    private boolean fromSetting;
    private boolean fromPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        initView();
        Intent intent = getIntent();
        fromSetting = intent.getBooleanExtra("fromSetting", false);
        fromPay = intent.getBooleanExtra("fromPay", false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    private void initData() {
        ThreadUtils.newCached(this::getData);
    }

    private void initView() {
        //获取userId
        mUserId = Sputil.getString(this, "userId", "");
        RecyclerView mRecyclerView = findViewById(R.id.add_recycleView_address);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this
                , LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter = new Adapter<>(obs, addressList));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        findViewById(R.id.rl_addAddress).setOnClickListener(this);
        findViewById(R.id.iv_cancel).setOnClickListener(this);
    }

    /**
     * 设置Holder数据
     */
    private Adapter.AdapterObs<AddressBean.MenuListBean> obs = new Adapter.AdapterObs<AddressBean.MenuListBean>() {
        @Override
        public void onBind(Adapter.Holder holder, List<AddressBean.MenuListBean> bean, int position) {
            AddressBean.MenuListBean menuListBean = bean.get(position);

            TextView name = holder.find(R.id.tv_address_name);
            TextView phone = holder.find(R.id.tv_address_phone);
            TextView content = holder.find(R.id.tv_address_content);
            ImageView status = holder.find(R.id.iv_status);

            name.setText(menuListBean.getConsignee());
            phone.setText(menuListBean.getPhone());
            content.setText(menuListBean.getAddress());
            if (menuListBean.getStatus() == 0) status.setImageResource(R.mipmap.check_address);
            else status.setImageResource(R.mipmap.nochecked_address);

            //设置编辑监听
            holder.find(R.id.rl_address_editor).setOnClickListener(v -> openEdit(menuListBean));
            //设置删除监听
            holder.find(R.id.rl_address_delete).setOnClickListener(v ->
                    DialogUtils.showDialog(AddressActivity.this, "确定要删除该地址吗?"
                            , "确定", "取消", new DialogUtils.onDiaLogBtnListener() {
                                @Override
                                public void onPositiveListener(DialogInterface dialog) {
                                    new Thread(() -> {
                                        String base64 = Base64.encodeToString(("{\"addressId\":\"" + menuListBean.getId() + "\"}").getBytes(), Base64.DEFAULT);
                                        String replace = base64.replace("\n", "");
                                        String s = NetManager.getInstance().getContent(replace, "183");
                                        if (!TextUtils.isEmpty(s)) {
                                            //{"msg":"ERROR: null","status":1}
                                            if (s.contains("\"status\":0")) {
                                                ToastUtil.showToast("删除成功");
                                                initData();
                                            } else {
                                                ToastUtil.showToast("删除失败, 请稍后重试");
                                            }
                                        }
                                    }).start();
                                }

                                @Override
                                public void onNegativeListener(DialogInterface dialog) {
                                    dialog.dismiss();
                                }
                            }));

            // 在购买商品页面点击条目返回地址并关闭
            holder.find(R.id.linear_finish).setOnClickListener(v -> {
                if (fromSetting) {
                    openEdit(menuListBean);
                } else if (fromPay) {
                    setResult(66);
                } else {
                    Intent intent = getIntent();
                    intent.putExtra("msg", menuListBean.getAddress());
                    intent.putExtra("name", menuListBean.getConsignee());
                    intent.putExtra("phone", menuListBean.getPhone());
                    intent.putExtra("addressId", menuListBean.getId());
                    setResult(20, intent);
                    finish();
                }
            });

            // 设置默认地址点击事件
            holder.find(R.id.rl_check_address).setOnClickListener(view ->
                    new Thread(() -> {
                        String dataPhone = menuListBean.getPhone();
                        String dataName = menuListBean.getConsignee();
                        String address = menuListBean.getAddress();
                        String base64 = Base64.encodeToString(("{\"addressId\":\"" + menuListBean.getId()
                                + "\", \"userId\":\"" + mUserId + "\", \"phone\":\"" + dataPhone + "\", \"consignee\":\""
                                + dataName + "\", \"status\":\"" + 0 + "\",\"address\":\"" + address + "\" }").getBytes(), Base64.DEFAULT);
                        String replace = base64.replace("\n", "");
                        String s = NetManager.getInstance().getContent(replace, "182");
                        if (!TextUtils.isEmpty(s)) {
                            if (s.contains("0")) getData();
                            else ToastUtil.showToast("未知错误");

                        } else ToastUtil.showToast("网络不佳");

                    }).start());
        }

        @Override
        public int layoutId() {
            return R.layout.item_address;
        }
    };

    private boolean open;

    private void openEdit(AddressBean.MenuListBean menuListBean) {
        if (open) {
            new Handler().postDelayed(() -> open = false, 800);
            return;
        }
        Intent intent = new Intent(AddressActivity.this, AddNewAddressActivity.class);
        intent.putExtra("edit", true);
        intent.putExtra("name", menuListBean.getConsignee());
        intent.putExtra("phone", menuListBean.getPhone());
        intent.putExtra("address", menuListBean.getAddress());
        intent.putExtra("status", menuListBean.getStatus());
        intent.putExtra("ID", menuListBean.getId());
        startActivity(intent);
        open = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_addAddress:
                findViewById(R.id.rl_addAddress).setOnClickListener(null);
                new Handler().postDelayed(() -> findViewById(R.id.rl_addAddress).setOnClickListener(this), 800);
                boolean fromPay = getIntent().getBooleanExtra("fromPay", false);
                if (fromPay)
                    startActivityForResult(new Intent(this, AddNewAddressActivity.class), 666);
                else
                    startActivity(new Intent(this, AddNewAddressActivity.class));
                break;
            case R.id.iv_cancel:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 5) {
            setResult(resultCode, data);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(66);
        super.onBackPressed();
    }

    private List<AddressBean.MenuListBean> addressList = new ArrayList<>();

    public void getData() {
        String base64 = Base64.encodeToString(("{\"userId\":\"" + mUserId + "\"}").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        String content = NetManager.getInstance().getContent(replace, "180");
        if (!TextUtils.isEmpty(content)) {
            AddressBean addressBean = GsonUtil.parseJsonToBean(content, AddressBean.class);
            if (addressBean != null && addressBean.getStatus() == 0) {
                addressList.clear();
                List<AddressBean.MenuListBean> menuList = addressBean.getMenuList();
                addressList.addAll(menuList);
                Util.uiThread(() -> mAdapter.notifyDataSetChanged());
            }
        } else {
            ToastUtil.showToast("网络不佳");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
