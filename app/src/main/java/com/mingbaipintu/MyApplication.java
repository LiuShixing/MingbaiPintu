package com.mingbaipintu;

import android.app.Application;
import android.content.Context;

/**
 * Created by DanDan on 2015/10/11.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取Context
        context = getApplicationContext();
    }
    //返回
    public static Context getContextObject() {
        return context;
    }
}

