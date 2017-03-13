package com.example.chen.mobilemediaplayer.base.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chen.mobilemediaplayer.R;
import com.example.chen.mobilemediaplayer.activity.SystemMediaPlayerActivity;
import com.example.chen.mobilemediaplayer.adapter.VideoItemAdapter;
import com.example.chen.mobilemediaplayer.base.BaseFragment;
import com.example.chen.mobilemediaplayer.domain.MediaItem;

import java.util.ArrayList;

/**
 * Created by chen on 2017/1/7.
 * 本地视频按钮对应的 Fragment pager
 */

public class NativeVideoPager extends BaseFragment  {

    private TextView textView;

    //初始化视图对象:
    private ListView lv_native_video_list;
    private TextView tv_native_video_nomedia;
    private ProgressBar pb_native_video;


    //视频列表
    private ArrayList<MediaItem> mediaItems;

    //lv_native_video_list 适配器
    private  VideoItemAdapter adapter ;



    //扫描视频完成时,使用handler发送消息
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据
                tv_native_video_nomedia.setVisibility(View.GONE);
                adapter = new VideoItemAdapter(context,mediaItems);
                lv_native_video_list.setAdapter(adapter);

            } else {
                //无数据
                tv_native_video_nomedia.setVisibility(View.VISIBLE);
            }

            //progressbar 隐藏
            pb_native_video.setVisibility(View.GONE);

        }
    };


    public NativeVideoPager(Context context) {
        super(context);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("NativeVideoPager", "-----init");
        return initView();
    }

    @Override
    protected View initView() {
/*
        textView = new TextView(context);
        textView.setText("本地视频");
        return textView;
*/
        View view = View.inflate(context, R.layout.native_video_pager, null);

        lv_native_video_list = (ListView) view.findViewById(R.id.lv_native_video_list);
        tv_native_video_nomedia = (TextView) view.findViewById(R.id.tv_native_video_nomedia);
        pb_native_video = (ProgressBar) view.findViewById(R.id.pb_native_video);


        //设置listview item的点击事件
        lv_native_video_list.setOnItemClickListener(new ListViewOnItmeClick());


        //解决页面切换时重复刷新视频列表问题
        if(isInitData) {
            handler.sendEmptyMessage(1);
        }
        return view;
    }

    /**
     * 初始化数据: 获取本地sd卡视频文件列表
     */

    @Override
    public void initData() {
        getDataFromLocal();
    }

    /**
     * 作用:扫描本地sd卡 获取视频文件列表
     * 1. 启用分线程加载数据
     * 2.
     * 3.如果是6.0的系统，动态获取读取sdcard的权限
     */
    private void getDataFromLocal() {

        mediaItems = new ArrayList<>();

        new Thread(){
            @Override
            public void run() {
                super.run();

                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI; //使用
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME, //视频在sd卡中显示的名称
                        MediaStore.Video.Media.DURATION, //视频持续时间
                        MediaStore.Video.Media.SIZE, //视频文件大小
                        MediaStore.Video.Media.DATA, //视频绝对路径
                        MediaStore.Video.Media.ARTIST, //视频演唱者(音乐)
                };

                Cursor cursor = contentResolver.query(uri, objs, null, null, null);

                if(cursor!=null ) {
                    while (cursor.moveToNext()){
                        String name = cursor.getString(0);
                        long duration = cursor.getLong(1);
                        long size = cursor.getLong(2);
                        String data = cursor.getString(3);
                        String artist = cursor.getString(4);

                        MediaItem mediaItem = new MediaItem(name,artist,data,size,duration);
                        mediaItems.add(mediaItem);//将视频添加到list
                    }
                    cursor.close();
                }


                //扫描视频列表完成--向handler发送消息
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    /**
     * ListView item 的点击事件
     */
    class  ListViewOnItmeClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem = mediaItems.get(position);
            Log.d("ListViewOnItmeClick", mediaItem.getData().toString());

            /*
            //1. 通过隐式意图 ,调用所有播放器,播放视频
            Intent intent = new Intent();
            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video*//*");
            context.startActivity(intent);

            */

            //2. 通过显示意图 调用系统播放器
/*            Intent intent = new Intent(context, SystemMediaPlayerActivity.class);
            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video*//*");
            startActivity(intent);*/

            //3. 序列化 向Inten中出入视频列表 并传入点击item的位置
            Intent intent = new Intent(context,SystemMediaPlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);//添加序列化 对象
            intent.putExtras(bundle);
            intent.putExtra("position",position);//传入点击item 的下标

            startActivity(intent);

        }
    }


}
