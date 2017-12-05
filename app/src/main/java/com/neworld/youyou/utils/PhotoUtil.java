package com.neworld.youyou.utils;

import android.util.Base64;

import com.neworld.youyou.bean.ReturnStatus;
import com.neworld.youyou.manager.NetManager;

/**
 * Created by user on 2017/10/31.
 */

public class PhotoUtil {

    //上传图片
    public static boolean saveNet(String userId, String imageMsg, int babyId, String url) {
        String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\",\"iconString\":\"" + imageMsg + "\", \"imageType\":\"jpg\", \"babyId\":\"" + babyId + "\"}").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        String content = NetManager.getInstance().getContent(replace, url);
        if (content != null && content.length() > 0) {
            ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
            //                dialog.dismiss();
            return returnStatus != null && returnStatus.getStatus() == 0;
        } else {
            return false;
        }
    }

    //上传图片
    public static boolean saveNet(String userId, String imageMsg, String url) {
        String base64 = Base64.encodeToString(("{\"userId\":\"" + userId + "\",\"iconString\":\"" + imageMsg + "\", \"imageType\":\"jpg\"}").getBytes(), Base64.DEFAULT);
        String replace = base64.replace("\n", "");
        String content = NetManager.getInstance().getContent(replace, url);
        if (content != null && content.length() > 0) {
            ReturnStatus returnStatus = GsonUtil.parseJsonToBean(content, ReturnStatus.class);
            return returnStatus != null && returnStatus.getStatus() == 0;
        } else {
            return false;
        }
    }
}
