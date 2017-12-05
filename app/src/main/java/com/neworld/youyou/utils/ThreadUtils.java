package com.neworld.youyou.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by asus on 2017/9/29.
 */

public class ThreadUtils {

    private static ExecutorService mExecutorService;

    public static void newCached(Runnable runnable){
        if (mExecutorService==null) {
            mExecutorService = Executors.newCachedThreadPool();
        }
        mExecutorService.execute(runnable);
    }
}
