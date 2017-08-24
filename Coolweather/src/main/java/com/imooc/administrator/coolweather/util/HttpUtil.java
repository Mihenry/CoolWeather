package com.imooc.administrator.coolweather.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 向服务器发送请求
 */

public class HttpUtil {
    /**参数Callback是okhttp3.Callback包下的类,且该类是一个回调类*/
    public static void sendOkHttpRequest(String address, Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
