package com.jbufa.firefighting.common;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;


public class AppContext extends Application {

    private static AppContext app;

    public AppContext() {
        app = this;
    }

    public static synchronized AppContext getInstance() {
        if (app == null) {
            app = new AppContext();
        }
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerUncaughtExceptionHandler();
        initSDK();
    }


    public void initSDK() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    // 注册App异常崩溃处理器
    private void registerUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
    }

}