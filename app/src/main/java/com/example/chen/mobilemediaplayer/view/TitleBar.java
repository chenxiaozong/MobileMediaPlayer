package com.example.chen.mobilemediaplayer.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chen.mobilemediaplayer.R;

/**
 * Created by chen on 2017/1/9.
 * 标题栏 线性布局 文件对应的实例化的类
 * 用 :com.example.chen.mobilemediaplayer.view.TitleBar 替换布局文件titilebar.xml 中的linerlayout
 */

public class TitleBar extends LinearLayout implements View.OnClickListener {
    //实例化标题栏中的视图对象
    private View tv_title;        //第一个孩子 --搜索
    private View rl_title_game;   //第一个孩子 --游戏
    private View iv_title_record; //第一个孩子 --记录


    private  Context   context;


    //1. 使用场景: 代码中实例化使用
    public TitleBar(Context context) {
        this(context,null);
        Log.d("TitleBar", "//1. 使用场景: 代码中实例化使用");

    }

    //2. 使用场景:布局文件中使用
    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        Log.d("TitleBar", "//2. 使用场景:布局文件中使用");
    }

    //3. 使用场景: 样式中使用
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        Log.d("TitleBar", "//3. 使用场景: 样式中使用");
    }

    //4. 将当前对象在布局文件中加载完成时,调用onFinishInflate
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d("TitleBar", "加载标题栏布局文件完成");
        initView();

        settitleListen();

    }

    /**
     * 设置监听
     */
    private void settitleListen() {
        tv_title.setOnClickListener(this);
        rl_title_game.setOnClickListener(this);
        iv_title_record.setOnClickListener(this);
    }

    /**
     * 实例化视图对象
      */
    private void initView() {

        //实例化
        tv_title = this.getChildAt(1);
        rl_title_game = this.getChildAt(2);
        iv_title_record = this.getChildAt(3);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title:
                Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context,SearchActivity.class);
                context.startActivity(intent);


            break;
            case R.id.rl_title_game:
                Toast.makeText(context, "游戏", Toast.LENGTH_SHORT).show();
            break;
            case R.id.iv_title_record:
                Toast.makeText(context, "记录", Toast.LENGTH_SHORT).show();
            break;
        }
    }
}
