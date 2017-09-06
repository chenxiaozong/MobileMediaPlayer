package com.example.chen.mobilemediaplayer;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.xutils.x;

/**
 * Created by chen on 2017/3/9.
 *  使用xutils 注解
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
      //  x.Ext.setDebug(BuildConfig.DEBUG);

        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=59af874e");
//        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5795c210");

    }
}
