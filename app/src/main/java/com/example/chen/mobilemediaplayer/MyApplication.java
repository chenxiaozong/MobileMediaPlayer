package com.example.chen.mobilemediaplayer;

import android.app.Application;

import org.xutils.*;
import org.xutils.BuildConfig;

/**
 * Created by chen on 2017/3/9.
 *  使用xutils 注解
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
