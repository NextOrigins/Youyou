package com.neworld.youyou.manager;

import com.neworld.youyou.utils.Fields;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ZHL on 2017/3/25.
 */

public class NetManager {
    private static final String TAG = "NetManager";

    private NetManager() {
    }

    private static NetManager sNetManager = new NetManager();

    public static NetManager getInstance() {
        return sNetManager;
    }

    //根据网络地址返回数据
    public String dataGet(String url) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(3000, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder().url(url).build();
            Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;   //处理异常
        }
    }

 /*   //根据网络地址返回Response 获取到的数据分String和流
    public Response response(String url) {
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(3000, TimeUnit.SECONDS)
                    .build();
            Request down = new Request.Builder().url(url).build();
            Response response = okHttpClient.newCall(down).execute();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;   //处理异常
        }
    }*/

    /**
     * @param fields
     * @param url
     * @return
     */
    public String getContent(String fields, String url) {
        Response response = getResponse(fields, url);
        try {
            String content = response.body().string();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDanContent(String url) {
        Response response = getDanResponse(url);
        try {
            String content = response.body().string();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response getDanResponse( String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3000, TimeUnit.SECONDS)
                .build();
       // MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
        MediaType JSON=MediaType.parse("application/json; charset=utf-8");
        //FormBody.Builder builder = new FormBody.Builder();
        RequestBody requestBody = RequestBody.create(JSON, url);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Response getResponse(final String string, final String url) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(3000, TimeUnit.SECONDS)
                    .build();
            //MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
            MediaType JSON=MediaType.parse("application/json; charset=utf-8");
            //FormBody.Builder builder = new FormBody.Builder();
            RequestBody requestBody = RequestBody.create(JSON, string);
            Request request = new Request.Builder()
                    .url(Fields.BASEURL + url)
                    .post(requestBody)
                    .build();
            try {
                return okHttpClient.newCall(request).execute();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
    }
}
