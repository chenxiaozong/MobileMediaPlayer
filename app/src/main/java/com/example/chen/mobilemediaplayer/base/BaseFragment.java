package com.example.chen.mobilemediaplayer.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by chen on 2017/1/6.
 * 基类:四个fragment页面继承
 * >本地视频 nativeVideoFragment
 * >本地音乐 nativeAudioFragment
 * >网络视频 netVideoFragment
 * >网络音乐 netAudioFragment
 */

public abstract class BaseFragment extends Fragment {

    //上下文---显示Fragment时需要传入此参数
    public   Context context=null;

    //成员: view
    public  View view;
    public  boolean isInitData; //标识initdata() 默认为false


    public BaseFragment(){}

    public  BaseFragment(Context context){
        this.context = context;
        this.view = initView();
    }

    /**
     * 抽象方法,初始化视图对象,子类必须实现
     * 强制子页面实现该方法，实现想要的特定的效果
     * @return
     */
    protected abstract View initView();

    /**
     * 当子页面，需要绑定数据，或者联网请求数据并且绑定的时候，重写该方法
     */
    public  void  initData(){
    }


}
