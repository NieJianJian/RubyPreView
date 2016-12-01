package com.ruby.preview.utils;

import android.app.Application;

import com.testin.agent.TestinAgent;

/**
 * Created by jian on 2016/12/1.
 * 应用程序 生命周期，初始化一些数据
 */
public class AppLifeCycleAware {

    public static AppLifeCycleAware Instance = new AppLifeCycleAware();
    private static final String TESTIN_APPKEY = "27f4f38f73db19573ca32f8da667b935";

    public final void onInit(Application application) {
        TestinAgent.init(application, TESTIN_APPKEY, null);
    }

}
