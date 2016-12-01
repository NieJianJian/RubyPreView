package com.ruby.preview;

import android.app.Application;
import android.content.Context;

import com.ruby.preview.utils.AppLifeCycleAware;

/**
 * Created by jian on 2016/12/1.
 */
public class RubyApplication extends Application {

    private static Context sAppContext;

    public static Context getApplication() {
        return sAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;

        AppLifeCycleAware.Instance.onInit(this);
    }
}
