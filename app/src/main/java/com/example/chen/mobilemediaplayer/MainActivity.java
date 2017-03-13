package com.example.chen.mobilemediaplayer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.example.chen.mobilemediaplayer.base.BaseFragment;
import com.example.chen.mobilemediaplayer.base.pager.NativeAudioPager;
import com.example.chen.mobilemediaplayer.base.pager.NativeVideoPager;
import com.example.chen.mobilemediaplayer.base.pager.NetAudioPager;
import com.example.chen.mobilemediaplayer.base.pager.NetVidePager;

import java.util.ArrayList;
import java.util.List;


/**
 * 主界面:
 * 1. 标题栏
 * 2. 中间fragment
 * 3. 底部按钮栏 --默认选中(本地视频)
 */
public class MainActivity extends FragmentActivity {

    private RadioGroup rg_main;
    private FrameLayout fl_main_content;

    private List<BaseFragment> fragmentsPagers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rg_main = (RadioGroup)findViewById(R.id.rg_main);
        fl_main_content = (FrameLayout)findViewById(R.id.fl_main_content);


        //底部按钮栏点击事件
        rg_main.setOnCheckedChangeListener(new BottomRGListener());



        //将四个页面对应的Framgment 添加到list中
        fragmentsPagers = new ArrayList<>();
        fragmentsPagers.add(new NativeVideoPager(this));
        fragmentsPagers.add(new NativeAudioPager(this));
        fragmentsPagers.add(new NetVidePager(this));
        fragmentsPagers.add(new NetAudioPager(this));

        //设置默认选中第一个(放于监听下侧)
        rg_main.check(R.id.rb_main_native_video);
    }

 private  int position;

    class BottomRGListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_main_native_video:
                    position = 0;
                    break;
                case R.id.rb_main_native_audio:
                    position = 1;
                    break;
                case R.id.rb_main_net_video:
                    position = 2;
                    break;
                case R.id.rb_main_net_audio:
                    position = 3;
                    break;
                default:
                    position = 0;
                    break;


            }

            //根据position显示Fragment
            setFragment();

        }
    }

    /**
     * 设置要显示的Fragment
     * 1. 获取fragmentmanager
     * 2. 开启事务
     * 3. replace替换
     * 4. 提交事务
     */

    private void setFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fl_main_content,getBaseFragment());
        fragmentTransaction.commit();

    }

    /**
     * 获取baseFragment
     * @return
     */
    private BaseFragment getBaseFragment() {
        BaseFragment basePager = fragmentsPagers.get(position);

        if(basePager!=null&& !basePager.isInitData){
            basePager.initData();
            basePager.isInitData = true;
        }
        return  basePager;
    }


}
