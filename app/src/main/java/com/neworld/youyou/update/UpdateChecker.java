package com.neworld.youyou.update;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class UpdateChecker {


    public static void checkForDialog(Activity context) {
        new CheckUpdateTask(context, 1, true).execute();

    }


    public static void checkForNotification(Activity context) {

        new CheckUpdateTask(context, 2, false).execute();

    }


}
