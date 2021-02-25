/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.alivcsolution;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;



import com.aliyun.common.httpfinal.QupaiHttpFinal;
import com.aliyun.sys.AlivcSdkCore;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.aliyun.svideo.base.http.EffectService;

/**
 * Created by Mulberry on 2018/2/24.
 */
public class MutiApplication extends Application {
    /**
     * 友盟数据统计key值
     */
    private static final String UM_APP_KEY = "5c6e4e6cb465f5ff47001s0e";
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
        AlivcSdkCore.register(getApplicationContext());
        AlivcSdkCore.setLogLevel(AlivcSdkCore.AlivcLogLevel.AlivcLogWarn);
        AlivcSdkCore.setDebugLoggerLevel(AlivcSdkCore.AlivcDebugLoggerLevel.AlivcDLClose);
        if (TextUtils.isEmpty(mLogPath)){
            //保证每次运行app生成一个新的日志文件
            long time = System.currentTimeMillis();
            mLogPath = getExternalFilesDir("Log").getAbsolutePath()+"/log_"+time+".log";
            AlivcSdkCore.setLogPath(mLogPath);
        }
        /**
         * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
         * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
         * UMConfigure.init调用中appkey和channel参数请置为null）。
         */
        UMConfigure.init(this, UM_APP_KEY, "Aliyun", UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        
        

        EffectService.setAppInfo(getResources().getString(R.string.app_name), getPackageName(), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
    }


}
