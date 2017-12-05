package com.neworld.youyou.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.neworld.youyou.fragment.BaseFragment;

import java.util.HashMap;

/**
 * Created by tt on 2017/9/12.
 */

public class DataTransmissionManager {
   public static Bundle getData(Intent intent, Context context, Class clazz){
        intent.setClass(context, clazz);
        Bundle bundle = new Bundle();
        return bundle;
   }

    public static void putString (Intent intent, Context context, Class clazz, HashMap<String, String> map) {
        Bundle bundle = getData(intent, context, clazz);
        for(HashMap.Entry<String, String> entry : map.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    public static void putString (Intent intent, BaseFragment context, Class clazz, HashMap<String, String> map, int requestCode) {
        Bundle bundle = getData(intent, context.getContext(), clazz);
        for(HashMap.Entry<String, String> entry : map.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        intent.putExtras(bundle);

        context.startActivityForResult(intent,requestCode);
    }
}
