package com.example.chen.mobilemediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 手机媒体播放器的启动页面
 * 1. splash页面
 * 2. 延时2s进入mainActivity --使用handler
 * 3. 当
 */
public class SplashActivity extends Activity {

    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //1. 延时2s后启动MainActivity --使用handler
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        },2000);



    }


    /**
     * 1. 启动mainactivity
     */
    private void startMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        //关闭当前activity
        this.finish(); //调用destroy

    }

    /**
     * spalsh页面的点击事件
     * 1. 点击后跳过延时,直接进入mainactivity
     * 2. 点击后终止 handler
     * 3. 点击后 通过单例模式(设置标示符)仅启动一次mainactivity
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("SplashActivity", "onTouchEvent");
        startMainActivity();

        return super.onTouchEvent(event);
    }


    /**
     * 页面销毁时同时回收handler 和message
     */
    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        Log.d("SplashActivity", "onDestroy()");
        super.onDestroy();
    }
}
