package com.example.chen.mobilemediaplayer.base.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.chen.mobilemediaplayer.activity.AudioPlayerActivity;
import com.example.chen.mobilemediaplayer.adapter.AudioItemAdapter;
import com.example.chen.mobilemediaplayer.base.BaseFragment;
import com.example.chen.mobilemediaplayer.domain.MediaItem;

import java.util.ArrayList;

/**
 * Created by chen on 2017/1/7.
 * 本地音频
 * adapter中歌词的默认地址:     private String lyricUrl = String.valueOf(Environment.getExternalStorageDirectory() + "/beijing.txt")
 *
 *
 */

public class NativeAudioPager extends BaseFragment {
    private TextView textView;
    private ArrayList<MediaItem> mediaItems; //存放音乐列表的list


    private AudioItemAdapter adapter;

    private ListView lv_native_audio_list;
    private TextView tv_native_audio_nomedia;
    private ProgressBar pb_native_audio;


    //扫描视频完成时,使用handler发送消息
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据
                tv_native_audio_nomedia.setVisibility(View.GONE);
                adapter = new AudioItemAdapter(context,mediaItems);
                lv_native_audio_list.setAdapter(adapter);

            } else {
                //无数据
                tv_native_audio_nomedia.setVisibility(View.VISIBLE);
            }

            //progressbar 隐藏
            pb_native_audio.setVisibility(View.GONE);

        }
    };



    public NativeAudioPager(Context context) {
        super(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("NativeAudioPager", "onCreate");
        return initView();
    }

    @Override
    protected View initView() {


        View view = View.inflate(context, R.layout.native_audio_pager,null);
        lv_native_audio_list = (ListView) view.findViewById(R.id.lv_native_audio_list);
        tv_native_audio_nomedia = (TextView) view.findViewById(R.id.tv_native_audio_nomedia);
        pb_native_audio = (ProgressBar) view.findViewById(R.id.pb_native_audio);



        //设置音乐点击监听
        lv_native_audio_list.setOnItemClickListener(new AudioOnItemClickListener());


        if (isInitData) {//已经初始化数据
            //扫描视频列表完成--向handler发送消息
            handler.sendEmptyMessage(0);
        }
        return view;
    }

    /**
     * 从本地扫描数据:
     */
    @Override
    public void initData() {
        getDataFromLocal();
        Log.d("NativeAudioPager", "initData()");

    }

    /**
     * 1. 扫描本地音乐数据:
     * 2. 其中歌词文件地址可以根据歌词路径下创建 lyrics 文件夹下
     */
    private void getDataFromLocal() {

        mediaItems = new ArrayList<>();

        new Thread(){
        String lyricUrl = String.valueOf(Environment.getExternalStorageDirectory() + "/beijing.txt");

            @Override
            public void run() {
                super.run();

                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //使用
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME, //视频在sd卡中显示的名称
                        MediaStore.Audio.Media.DURATION, //视频持续时间
                        MediaStore.Audio.Media.SIZE, //视频文件大小
                        MediaStore.Audio.Media.DATA, //视频绝对路径
                        MediaStore.Audio.Media.ARTIST, //视频演唱者(音乐)
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
     * 音乐列表点击事件
     */
    private class AudioOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem item = mediaItems.get(position);
            //3. 序列化 向Inten中出入视频列表 并传入点击item的位置
            Intent intent = new Intent(context,AudioPlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);//添加序列化 对象
            intent.putExtras(bundle);

            intent.putExtra("position",position);//传入点击item 的下标*/
            startActivity(intent);
        }
    }
}
