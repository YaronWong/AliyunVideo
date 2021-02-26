/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.alivcsolution;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.aliyun.common.httpfinal.QupaiHttpFinal;

/**
 * Created by Mulberry on 2018/2/24.
 */
public class MutiApplication extends Application {

    private String mLogPath;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        QupaiHttpFinal.getInstance().initOkHttpFinal();

//        AlivcSdkCore.register(getApplicationContext()); // 注册啥？如果崩溃再打开，排除了部分so
//        AlivcSdkCore.setLogLevel(AlivcSdkCore.AlivcLogLevel.AlivcLogWarn);
//        AlivcSdkCore.setDebugLoggerLevel(AlivcSdkCore.AlivcDebugLoggerLevel.AlivcDLClose);

//        EffectService.setAppInfo(getResources().getString(R.string.app_name), getPackageName(), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);

    }


}
