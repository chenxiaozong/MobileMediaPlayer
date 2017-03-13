package com.example.chen.mobilemediaplayer.base.pager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chen.mobilemediaplayer.base.BaseFragment;

/**
 * Created by chen on 2017/1/7.
 * 4. 网络音乐对应Fragment
 */

public class NetAudioPager extends BaseFragment {
    private TextView textView;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected View initView() {
        textView = new TextView(context);
        textView.setText("网络音乐");
        return textView;
    }


    @Override
    public void initData() {
        Log.d("NetAudioPager", "initData()");
    }
}