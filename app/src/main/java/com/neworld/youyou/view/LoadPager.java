package com.neworld.youyou.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;


import com.neworld.youyou.R;
import com.neworld.youyou.utils.NetworkObs;
import com.neworld.youyou.utils.Util;

import java.util.List;

import okhttp3.Response;

import static com.neworld.youyou.view.LoadPager.STATUS.LOADING;


/**
 * 加载页面
 */

public abstract class LoadPager extends FrameLayout {

    private View mLoadView; //加载页面
    private View mErrorView; //错误页面
    private View mSuccessView; //成功页面
//    private NetworkObs netObs;

    public LoadPager(@NonNull Context context) {
        this(context, null);
    }

    public LoadPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadPager(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        initBroadcast();
        init();
    }

//    NetworkObs.NetworkState l = new NetworkObs.NetworkState() {
//        @Override
//        public void onWifiConnected() {
//            mSTATUS = LOADING;
//            changPage();
//            requestData();
//        }

//        @Override
//        public void onMobileConnected() {
//            mSTATUS = LOADING;
//            changPage();
//            requestData();
//        }

//        @Override
//        public void onNetworkUnknown() {
//            mSTATUS = ERROR;
//            changPage();
//        }
//    };

    private void initBroadcast() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        filter.addAction("android.net.wifi.STATE_CHANGE");
//        netObs = new NetworkObs(l);
//        getContext().registerReceiver(netObs, filter);
    }

    private void init() {
        if (mLoadView == null) {
            mLoadView = View.inflate(getContext(), R.layout.page_loading, null);
        }
        if (mErrorView == null) {
            mErrorView = View.inflate(getContext(), R.layout.page_error, null);
            FrameLayout button = (FrameLayout) mErrorView.findViewById(R.id.page_tv);

            button.setOnClickListener(v -> {
                mSTATUS = LOADING;
                changPage();
                requestData();
            });
        }
        if (mSuccessView == null) {
            mSuccessView = createSuccessView();
            if (mSuccessView == null) {
                throw new RuntimeException("必须传入布局");
            }
        }
        //添加界面
        addView(mLoadView);
        addView(mErrorView);
        addView(mSuccessView);
        //切换UI
        changPage();
        //根据网络数据自动切换UI
        showPage();
    }

    //根据当前的状态去切换页面
    private void changPage() {
        mLoadView.setVisibility(GONE);
        mErrorView.setVisibility(GONE);
        mSuccessView.setVisibility(GONE);
        switch (mSTATUS) {
            case LOADING:
                mLoadView.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                mErrorView.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                mSuccessView.setVisibility(View.VISIBLE);
                break;
        }
    }

    //根据网络数据自动切换ui
    public void showPage() {
        requestData();
    }

    private void requestData() {
        new Thread(() -> {
            //SystemClock.sleep(time);
            //得到对象
            Object object = getNetData();
            //检测数据
            mSTATUS = checkData(object);
            //主线程切换界面
            //切换界面
            Util.uiThread(this::changPage);
        }).start();
    }

    //返回当前的状态
    private STATUS checkData(Object object) {
        /*
         * 1. 判断当前的数据
         * 2. 如果是空,返回错误的状态
         * 3. 如果不为空,判断是否是对象  // TODO : 此方法最终只是判断是否为null，如果为null则加载error界面
         * 4. 如果是集合,那么判断个数是否大于0 // TODO : 但是只要有网的状态是不可能为null的，服务器会返回错误码也不会返回null
         * // TODO : 所以最终有网的状态会返回成功.
         */
        if (object == null) {
            return STATUS.ERROR;
        } else {
            if (object instanceof Response) {
                List list = (List) object; // 你判断Response类型强转成集合，不怕ClassCastException吗？
                if (list != null) { // 你就这么判断大于0的啊 谁教你的java
                    //大于0说明有数据  // 你是怎么看出来大于0的兄弟，代码穿透眼吗。直达底层？
                    return STATUS.SUCCESS;
                } else {
                    return STATUS.ERROR;
                }
            } else {
                //不是集合
                return STATUS.SUCCESS;
            }
        }
    }

    public void unBindNetworkObs() {
//        getContext().unregisterReceiver(netObs);
    }

    enum STATUS {
        LOADING,    //加载中
        ERROR,      //错误
        SUCCESS     //成功
    }

    private STATUS mSTATUS = LOADING; //加载中的状态

    //成功的界面
    public abstract View createSuccessView();

    //获取数据 谁用谁传
    public abstract Object getNetData();

}