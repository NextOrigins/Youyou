package com.neworld.youyou.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.neworld.youyou.manager.MyApplication;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;



/**
 * Created by heima_sy on 2016/5/19.
 */
public class Util {
    private final static String ENCODE = "UTF-8";
    /**
     * URL 解码
     *
     * @return String
     * @author lifq
     * @date 2015-3-17 下午04:09:51
     */
    public static String getURLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * URL 转码
     *
     * @return String
     * @author lifq
     * @date 2015-3-17 下午04:10:28
     */
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    //这个是在主线程去更新ui,在没有上下文的环境,
    public static void uiThread(Runnable action) {
        MyApplication.uiThread(action); // TODO : 2017-11-21修改.
    }

    //得到字符串数组信息
    public static String[] getStringArray(int resId) {
        //getResources:R
        return getResources().getStringArray(resId);
    }


    //得到资源管理的类
    public static Resources getResources() {
        return MyApplication.sContext.getResources();
    }

    //在屏幕适配时候使用,让代码中使用dip属性
    public static int getDimens(int resId) {

        return getResources().getDimensionPixelSize(resId);
    }

    //得到颜色
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    /**
     * 拿到一个随机颜色
     *
     * @return
     */
    public static int createRandomColor() {
        Random random = new Random();
        return random.nextInt(180);
    }

    // 创建一个随机的颜色
    public static int randomColor() {

        Random random = new Random();
        int red = random.nextInt(180);
        int blue = random.nextInt(180);
        int green = random.nextInt(180);
        System.out.println(red + ":" + blue + ":" + green);
        return Color.rgb(red, green, blue);
        // return 0;
    }

    //隐藏title
    public static void hideTitle(AppCompatActivity activity) {
        //隐藏状态栏
        activity.getSupportActionBar().hide();
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = activity.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
    }

   /* //设置图片的方法
    public static void setNetImage(String url, ImageView view) {
        *//**
         * 1. 图片的地址
         * 2. 图片的控件
         * 3. 图片设置
         *//*
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(OSChina.context));
        DisplayImageOptions mOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.widget_dface12) //显示图片加载中
                .showImageForEmptyUri(R.mipmap.widget_dface12) //空的图片
                .showImageOnFail(R.mipmap.widget_dface12) //错误的图片
                .cacheInMemory(true) //内存缓存要不要
                .cacheOnDisk(true) //sd卡缓存要不要
                .considerExifParams(true)//会识别图片的方向信息
                //.displayer(new FadeInBitmapDisplayer(500)).build();
                .displayer(new RoundedBitmapDisplayer(360)).build();
        ImageLoader.getInstance().displayImage(url, view, mOptions);
    }*/

   /* public static void glideSetImage(final String url, final ImageView imageView) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(OSChina.context).load(url).error(R.mipmap.widget_dface12).into(imageView);
        } else {
            DrawableTypeRequest<String> load = Glide.with(OSChina.context).load(url);
            load.placeholder(R.mipmap.widget_dface12);
            load.asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
    }*/

   /* //解析富文本
    public static void setSpannable(String content, TextView textView) {
        if (!TextUtils.isEmpty(content)) {
            String text = content.replaceAll("[\n\\s]+", " ");
            Spannable spannable = AssimilateUtils.assimilateOnlyAtUser(OSChina.context, text);
            spannable = AssimilateUtils.assimilateOnlyTag(OSChina.context, spannable);
            spannable = AssimilateUtils.assimilateOnlyLink(OSChina.context, spannable);
            spannable = InputHelper.displayEmoji(getResources(), spannable);
            textView.setText(spannable);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }*/

    //获取版本名
    public static String getVersionName(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        //参数一：要查询的应用包信息对应包名,参数二：标记，想获取什么信息，就设置什么标记，（0：基本信息）
        String versionName = "未知";

        PackageInfo packageInfo = null;
        try {
            packageInfo = manager.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return versionName;
    }

    //获取版本号
    public static int getVersionCode(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        //参数一：要查询的应用包信息对应包名,参数二：标记，想获取什么信息，就设置什么标记，（0：基本信息）
        int versionCode = 1;

        PackageInfo packageInfo = null;
        try {
            packageInfo = manager.getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String toDateString(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long toDateLong(String date) {
        long i = 0;
        try {
            Date parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
            i = parse.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    public static SimpleDateFormat getDateFormatInstance() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}