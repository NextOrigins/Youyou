package com.neworld.youyou.activity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.neworld.youyou.MainActivityKt;
import com.neworld.youyou.R;
import com.neworld.youyou.add.StudentActivity;
import com.neworld.youyou.bean.AddressBean;
import com.neworld.youyou.bean.ChildDetailBean;
import com.neworld.youyou.bean.PaymentBean;
import com.neworld.youyou.bean.ReturnChatBean;
import com.neworld.youyou.manager.NetManager;
import com.neworld.youyou.utils.GsonUtil;
import com.neworld.youyou.utils.Sputil;
import com.neworld.youyou.utils.ToastUtil;
import com.neworld.youyou.utils.Util;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView tvCancel;
    private RelativeLayout rlStudentMsg;
    private ImageView ivIcon;
    private TextView tvContent;
    private TextView tvLevel;
    private TextView tvTime;
    private TextView tvLocation;
    private TextView tvMessage;
    private TextView tvCount;
    private ConstraintLayout rlContent;
    private PaymentBean.MenuListBean menuList;
    private TextView tvName;
    List<ChildDetailBean.ResultsBean> mDatas = new ArrayList<>();
    private Button btOrder;
    private String userId;
    private double money;
    private int orderId;
    private IWXAPI wxApi;
    private int subjectId;
    private int typeId;
    private String ip;
    private ImageView ivJian;
    private String studentName = "";
    private TextView mTv_sendAddress;
    private ImageView mGive_iv_book;
    private TextView mGive_tv_bookmoney;
    private TextView mGive_tv_bookname;
    private TextView mTotal_tv_money;
    private TextView mTv_expressFee;
    private RelativeLayout mRelativeLayout;
    private TextView tvMoney;

    private final int STUDENT_NAME = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initUser();
        initWifi();
        initView();
        initData();
        initStudent();
    }

    //获取wifi服务
    private void initWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        ip = (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                (ipAddress >> 24 & 0xFF);
    }
    /*public String getIp() {
        try {

            for (Enumeration<networkinterface> en = NetworkInterface

                    .getNetworkInterfaces(); en.hasMoreElements();) {

                //NetworkInterface intf = en.nextElement();

                for (Enumeration<inetaddress> ipAddr = intf.getInetAddresses(); ipAddr

                        .hasMoreElements();) {

                    InetAddress inetAddress = ipAddr.nextElement();
                    // ipv4地址
                    if (!inetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(inetAddress
                            .getHostAddress())) {

                        Log.e("TAG", "ipv4=" + inetAddress.getHostAddress());
                        return inetAddress.getHostAddress();

                    }

                }

            }

        } catch (Exception ex) {

        }

        return "192.168.1.0";

    }*/

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            //Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    private void initUser() {
        userId = Sputil.getString(PaymentActivity.this, "userId", "");
        wxApi = WXAPIFactory.createWXAPI(PaymentActivity.this, "wxd7cf604bab22c904", true);
        wxApi.registerApp("wxd7cf604bab22c904");
    }

    private void initStudent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "141");

                if (content != null && content.length() > 0) {
                    ChildDetailBean childDetailBean = GsonUtil.parseJsonToBean(content, ChildDetailBean.class);
                    if (childDetailBean != null && childDetailBean.getStatus() == 0) {
                        List<ChildDetailBean.ResultsBean> results = childDetailBean.getResults();
                        if (results != null && results.size() > 0) {
                            mDatas.addAll(results);
                        }
                    }
                }
            }
        }).start();
    }

    private void initData() {
        Bundle extras = getIntent().getExtras();
        orderId = extras.getInt("orderId");
        subjectId = extras.getInt("subjectId");
        typeId = extras.getInt("typeId");
        String examinee_name = extras.getString("examinee_name", "");
        if (examinee_name != null && examinee_name.length() > 0) {
            studentName = examinee_name;
            String number = Sputil.getString(PaymentActivity.this, "number", "");
            tvName.setText(examinee_name + "/" + number);
            rlStudentMsg.setClickable(false);
            ivJian.setVisibility(View.GONE);
        }
        if (orderId != 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\",\"orderId\":\"" + orderId + "\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "163");

                    if (content != null && content.length() > 0) {
                        PaymentBean paymentBean = GsonUtil.parseJsonToBean(content, PaymentBean.class);
                        if (paymentBean != null && paymentBean.getStatus() == 0) {
                            menuList = paymentBean.getMenuList();
                            final PaymentBean.ResultsBean resultsBean = paymentBean.getResults();
                            if (menuList != null) {
                                Util.uiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        setData(menuList, resultsBean);
                                    }
                                });
                            }
                        }
                    }
                }
            }).start();

            new Thread(() -> {
                String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\"}").getBytes(), Base64.DEFAULT);
                String replace = base64.replace("\n", "");
                String content = NetManager.getInstance().getContent(replace, "180");
                if (!TextUtils.isEmpty(content)) {
                    AddressBean addressBean = GsonUtil.parseJsonToBean(content, AddressBean.class);
                    if (addressBean != null && addressBean.getStatus() == 0) {
                        List<AddressBean.MenuListBean> menuList = addressBean.getMenuList();
                        for (AddressBean.MenuListBean bean : menuList) {
                            if (bean.getStatus() == 0) {
                                runOnUiThread(() -> {
                                    mTv_sendAddress.setHint("");
                                    mTv_sendAddress.setText(bean.getAddress());
                                });
                                return;
                            }
                        }
                    }
                } else {
                    ToastUtil.showToast("网络不佳");
                }
            }).start();
        }
    }

    //设置数据
    private void setData(PaymentBean.MenuListBean menuList, PaymentBean.ResultsBean resultsBean) {
        String subject_date = menuList.getSubject_date();
        //设置时间
        if (subject_date != null && subject_date.length() > 0) {
            tvTime.setText(subject_date);
        } else {
            tvTime.setText("");
        }
        //设置级别
        String type_name = menuList.getType_name();
        if (type_name != null && type_name.length() > 0) {
            tvLevel.setText(type_name);
        } else {
            tvLevel.setText("");
        }
        //设置地点
        String address = menuList.getAddress();
        if (address != null && address.length() > 0) {
            tvLocation.setText(address);
        } else {
            tvLocation.setText("");
        }
        //设置图像
        String comment_img = menuList.getComment_img();
        if (comment_img != null && comment_img.length() > 0) {
            Glide.with(PaymentActivity.this).load(comment_img).into(ivIcon);
        }
        //设置标题
        String title = menuList.getTitle();
        if (title != null && title.length() > 0) {
            tvContent.setText(title);
        } else {
            tvContent.setText("");
        }
        money = menuList.getMoney();
        if (resultsBean != null) {
            setResultsDatas(resultsBean);
        } else {
            mRelativeLayout.setVisibility(View.GONE);
        }

        //设置价格
        tvCount.setText("¥" + money);
        mTotal_tv_money.setText("商品共¥" + money);

        tvMoney.setText("¥" + menuList.getMoney());
    }

    private void initView() {
        tvCancel = (ImageView) findViewById(R.id.iv_cancel);
        rlStudentMsg = (RelativeLayout) findViewById(R.id.student_msg);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvLevel = (TextView) findViewById(R.id.tv_level);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvMessage = (TextView) findViewById(R.id.person_tv_gender);
        tvCount = (TextView) findViewById(R.id.tv_price);
        rlContent = (ConstraintLayout) findViewById(R.id.rl_content);
        tvName = (TextView) findViewById(R.id.student_name);
        btOrder = (Button) findViewById(R.id.bt_order);
        ivJian = (ImageView) findViewById(R.id.iv_jiantou);
        tvMoney = $(R.id.tv_money);
        findViewById(R.id.rl_send_address).setOnClickListener(this); //收货地址
        mTv_sendAddress = (TextView) findViewById(R.id.tv_send_address); // 显示收货地址
        //设置图书
        mGive_iv_book = (ImageView) findViewById(R.id.give_iv_book);
        mGive_tv_bookmoney = (TextView) findViewById(R.id.give_tv_bookmoney);
        mGive_tv_bookname = (TextView) findViewById(R.id.give_tv_bookname);
        mTotal_tv_money = (TextView) findViewById(R.id.total_tv_money); //设置总价钱
        mTv_expressFee = (TextView) findViewById(R.id.tv_expressFee);  //快递费
        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl_Presenter);

        tvCancel.setOnClickListener(this);
        rlStudentMsg.setOnClickListener(this);
        rlContent.setOnClickListener(this);
        btOrder.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                finish();
                break;
            case R.id.student_msg:
//                changActivity();
                startActivityForResult(new Intent(this, StudentActivity.class), STUDENT_NAME);
                break;
            case R.id.rl_content:
                Intent intent = new Intent();
                intent.setClass(PaymentActivity.this, SubjectActivity.class);
                Bundle bundle = new Bundle();
                if (menuList != null && menuList.getId() != 0) {
                    bundle.putInt("subjectId", menuList.getId());
                }
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.bt_order:
                if (!TextUtils.isEmpty(studentName) && !TextUtils.isEmpty(mTv_sendAddress.getText())) {
                    orderMoney();
                } else if (TextUtils.isEmpty(studentName)) {
                    ToastUtil.showToast("请输入考生姓名");
                } else {
                    ToastUtil.showToast("请选择收货地址");
                }
                break;
            case R.id.rl_send_address:   //点击设置地址
                startActivityForResult(new Intent(this, AddressActivity.class), 4);
                break;
        }
    }

    //支付订单 NSDictionary *param = @{@"userId":userId,@"money":self.money,@"subjectId":self.subje
    // ctStr,@"typeId":self.type,@"babyName":self.payNameStr,@"phone":self.phoneNumber,@"spbill_create_ip":[GetIP getIPAddress],@"orderId":self.orderId};
    private void orderMoney() {
        btOrder.setOnClickListener(null);
        final String number = Sputil.getString(PaymentActivity.this, "number", "");
        if (number != null && number.length() > 0 && orderId != 0 && subjectId != 0 && typeId != 0 && ip != null && ip.length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\", \"money\":\"" + money + "\", \"subjectId\":\"" + subjectId + "\"," +
                            "  \"typeId\":\"" + typeId + "\", \"babyName\":\"" + studentName + "\", \"phone\":\"" + number + "\"," +
                            " \"spbill_create_ip\":\"" + ip + "\", \"orderId\":\"" + orderId + "\"}").getBytes(), Base64.DEFAULT);
                    String replace = base64.replace("\n", "");
                    String content = NetManager.getInstance().getContent(replace, "139");
                    if (content != null && content.length() > 0) {
                        ReturnChatBean returnChatBean = GsonUtil.parseJsonToBean(content, ReturnChatBean.class);
                        if (returnChatBean != null && returnChatBean.getStatus() == 0) {
                            weChatPay(returnChatBean);
                        } else {
                            MainActivityKt.showSnackBar(findViewById(R.id.view_group), "商品卖完了哦亲_", 1000);
                        }
                    }
                    SystemClock.sleep(300);
                    runOnUiThread(() -> {
                        btOrder.setOnClickListener(PaymentActivity.this);
//                        btOrder.setText("立即支付");
                    });
                }
            }).start();
        }
    }

    //支付
    private void weChatPay(ReturnChatBean returnChatBean) {
        if (null != returnChatBean) { //判断是否为空。丢一个toast，给个提示。比如服务器异常，错误啥的 return; }
            IWXAPI api = WXAPIFactory.createWXAPI(this, returnChatBean.getAppid());
            if (!api.isWXAppInstalled()) {
                ToastUtil.showToast(getString(R.string.text_uninstalled_wchat));
                return;
            } //data 根据服务器返回的json数据创建的实体类对象
            PayReq req = new PayReq();
            req.appId = returnChatBean.getAppid();
            req.prepayId = returnChatBean.getPrepayid();
            req.nonceStr = returnChatBean.getNoncestr();
            req.timeStamp = returnChatBean.getTimeStamp();
            req.sign = returnChatBean.getSign();
            req.partnerId = "1480432402";
            req.packageValue = "Sign=WXPay";
            api.registerApp(returnChatBean.getAppid());
            //发起请求
            api.sendReq(req);
        }
    }

    //选择考生信息
    private void changActivity() {
        if (mDatas.size() != 0) {
            startActivityForResult(new Intent(PaymentActivity.this, AllStudentActivity.class), 2);
        } else {
            Intent intent = new Intent();
            intent.setClass(PaymentActivity.this, AddChildActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("isFromPay", true);
            intent.putExtras(bundle);
            startActivityForResult(intent, 10);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STUDENT_NAME) {
            if (data != null) {
                String name = data.getStringExtra("studentName");
                studentName = name;
                if (!TextUtils.isEmpty(name)) {
                    String number = Sputil.getString(PaymentActivity.this, "number", "");
                    if (TextUtils.isEmpty(number))
                        number = "testNum";

                    CharSequence c = name + "/" + number; // 2017.11.15
                    tvName.setText(c);
                }
            }
        } else if (requestCode == 10) {
            if (data != null) {
                String stringExtra = data.getStringExtra("name");
                studentName = stringExtra;
                if (stringExtra != null && stringExtra.length() > 0) {
                    String number = Sputil.getString(PaymentActivity.this, "number", "");
                    tvName.setText(stringExtra + "/" + number);
                }
            }
        } else if (resultCode == 20) {
            if (data == null) throw new NullPointerException("检查AddressActivity 80行");
            mTv_sendAddress.setHint("");
            mTv_sendAddress.setText(data.getStringExtra("msg"));
        }
    }

    public void setResultsDatas(PaymentBean.ResultsBean resultsDatas) {

        //书名
        String bookName = resultsDatas.getBookName();
        if (!TextUtils.isEmpty(bookName)) {

            mGive_tv_bookname.setText(bookName);
        }
        //书本图片
        String iconImg = resultsDatas.getIconImg();
        if (!TextUtils.isEmpty(iconImg)) {
            Glide.with(this).load(iconImg).into(mGive_iv_book);
        }
        //书本价钱
        double price = resultsDatas.getPrice();
        money += price;
        mGive_tv_bookmoney.setText("¥" + price);
        int expressFee = resultsDatas.getExpressFee();
        //快递费
        money += expressFee;
        mTv_expressFee.setText("快递费" + expressFee + "元");
    }

    private <T extends View> T $(int res) {
        return findViewById(res);
    }
}
