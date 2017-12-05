package com.neworld.youyou.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tt on 2017/7/18.
 */

public class MyApplication extends Application {
    public static Handler mHandler;
    public static Context sContext;
    private static Thread mUiThread;
    private List<Activity> oList;//用于存放所有启动的Activity的集合

    @Override
    public void onCreate() {
        super.onCreate();
        mUiThread = Thread.currentThread();
        mHandler = new Handler();
        sContext = this;
        oList = new ArrayList<>();
        initImageLoader();
        UMShareAPI.get(this);
        initShare();
    }

    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
// 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
        //判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }
    }

    private void initShare() {
        PlatformConfig.setWeixin("wxd7cf604bab22c904", "953b71a97b02be19cc8308d40a51c8b9");
        PlatformConfig.setQQZone("1106358422", "UxoB9bX57otblNSB");
        IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        msgApi.registerApp("wxd7cf604bab22c904");
    }

    private void initImageLoader() {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

    }

    public static void uiThread(Runnable action) {
        if (Thread.currentThread() != mUiThread) {
            mHandler.post(action);
        } else {
            action.run();
        }
    }
}
