package cn.nchu.wuxi.xlivemeet.utils;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;


public class OKHttpSingleton
{
    private static OkHttpClient singleton;
    private OKHttpSingleton(){

    }
    public static OkHttpClient getInstance() {
        if (singleton == null)
        {
            synchronized (OKHttpSingleton.class)
            {
                if (singleton == null)
                {
                    //统一添加Stetho拦截器，用于调试
                    singleton = new OkHttpClient()
                            .newBuilder()
                            .addNetworkInterceptor(new StethoInterceptor())
                            .build();
                }
            }
        }
        return singleton;
    }
}