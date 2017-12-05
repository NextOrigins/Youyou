package com.neworld.youyou.manager;

import android.util.Base64;

import com.google.gson.Gson;

import java.util.HashMap;

import static com.neworld.youyou.utils.GsonUtil.parseJsonToBean;

/**
 * Created by tt on 2017/9/11.
 */

public class Base64Manager {
    private static String getBase64(HashMap<String, String> map) {
        String json = new Gson().toJson(map);
        String encodeToString = Base64.encodeToString(json.getBytes(), Base64.DEFAULT);
        String replace = encodeToString.replace("\n", "");
        return replace;
    }

    public static  <T> T getToBean(String url, Class<T> clazz, HashMap<String, String> map) {
        String base64 = getBase64(map);
        if (base64 != null) {
            String content = NetManager.getInstance().getContent(base64, url);
            if (content != null && content.length() > 0) {
                return parseJsonToBean(content, clazz);
            }
        }
        return null;
    }
}
