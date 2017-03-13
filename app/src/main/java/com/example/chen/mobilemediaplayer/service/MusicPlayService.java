package com.example.chen.mobilemediaplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.chen.mobilemediaplayer.IMusicPlayService;
import com.example.chen.mobilemediaplayer.R;
import com.example.chen.mobilemediaplayer.activity.AudioPlayerActivity;
import com.example.chen.mobilemediaplayer.domain.MediaItem;
import com.example.chen.mobilemediaplayer.utils.CacheUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by chen on 2017/3/10.
 * 播放音乐服务:
 */

public class MusicPlayService extends Service {

    //广播标志:通过广播更新界面
    public static final String OPEN_AUDIO = "com.example.chen.mobilemediaplayer_OPENAUDIO";


    /**
     * 设置音乐播放模式对应的标志:
     */
    public static final int PLAY_MODEL_ORDER = 10;//顺序播放
    public static final int PLAY_MODEL_SINGLE = 11;//单曲循环播放
    public static final int PLAY_MODEL_REPEATE = 12;//多曲循环

    public int playmodel = PLAY_MODEL_ORDER;//顺序循环播放播放






    private ArrayList<MediaItem> items;
    private int position; //点击音乐的index

    //实例化mediaItem 对应一条音乐信息
    private MediaItem mediaItem;

    //实例化音乐播放器
    private MediaPlayer audioMediaPlayer;


    @Override
    public void onCreate() {
        super.onCreate();
        this.playmodel = CacheUtils.getPlayModel(this,"playmodel");//jian
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        items = (ArrayList<MediaItem>) intent.getSerializableExtra("videolist");
        position = intent.getIntExtra("position", 0);
        return stub;
    }
    /**
     * 3.创建Stub
     */

    private IMusicPlayService.Stub stub = new IMusicPlayService.Stub() {
        MusicPlayService service = MusicPlayService.this;

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void playNext() throws RemoteException {
            service.playNext();
        }

        @Override
        public void playPre() throws RemoteException {
            service.playPre();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentProgress() throws RemoteException {
            return service.getCurrentProgress();
        }

        @Override
        public long getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public int getPlayModel() throws RemoteException {
            return service.getPlayModel();
        }

        @Override
        public void setPlayModel(int playmodel) throws RemoteException {
            service.setPlayModel(playmodel);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }
    };


    /**
     * 打开音频:
     */
    private void openAudio(int position) {
        Log.d("MusicPlayService--", "openAudio:+position:"+position);
        if(items!=null&&items.size()>0) {
            mediaItem = items.get(position);

            if(audioMediaPlayer!=null) {
              //  audioMediaPlayer.release();
                audioMediaPlayer.reset();
            }
                audioMediaPlayer = new MediaPlayer();
                Log.d("MusicPlayService", " audioMediaPlayer = new MediaPlayer();");

                try {

                    //播放器设置监听
                    audioMediaPlayer.setOnPreparedListener(new AudioOnPreparedListener());
                    audioMediaPlayer.setOnCompletionListener(new AudioOnCompletionListener());
                    audioMediaPlayer.setOnErrorListener(new AudioOnErrorListener());

                    audioMediaPlayer.setDataSource(mediaItem.getData());
                    audioMediaPlayer.prepareAsync();//准备异步任务


                    //当单曲循环时,播放器设置循环setLooping()

                    if(playmodel==MusicPlayService.PLAY_MODEL_SINGLE) {
                        audioMediaPlayer.setLooping(true);
                    }else {
                        audioMediaPlayer.setLooping(false);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }else {
            Toast.makeText(this, "没有音乐数据", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 播放音频
     */
    private  NotificationManager manager;
    //通知栏管理
    private void start() {
        audioMediaPlayer.start();
        Log.d("MusicPlayService--", "start");

         manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent= new Intent(this,AudioPlayerActivity.class);
        intent.putExtra("isNotification",true);//标识来自通知栏的intent


        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("影音播放器")
                .setContentText(getAudioName())
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1,notification);
    }



    /**
     * 暂停音频
     */
    private void pause() {
        audioMediaPlayer.pause();
/*
        manager.cancel(1);*/
    }

    /**
     * 播放下一曲
     */
    private void playNext() {
        if (items != null && items.size() > 0) {
            if (position >= 0 && position < items.size()-1) {
                position++;
            } else if (position == items.size()-1&&playmodel==MusicPlayService.PLAY_MODEL_REPEATE) {
                position = 0;
            }
            openAudio(position);
        }
    }

    /**
     * 播放上一曲
     */
    private void playPre() {
        if (items != null && items.size() > 0) {
            if (position > 0 && position < items.size()) {
                position--;
            } else if (position == 0&&playmodel==MusicPlayService.PLAY_MODEL_REPEATE) {//全部循环播放
                position = items.size() - 1;
            }
            openAudio(position);
        }
    }

    /**
     * 停止播放
     */
    private void stop() {
        audioMediaPlayer.stop();
    }

    /**
     * 得到当前进度
     */
    private int getCurrentProgress() {
        return audioMediaPlayer.getCurrentPosition();
    }


    /**
     * 得到时长
     */
    private long getDuration() {

        return audioMediaPlayer.getDuration();
    }

    /**
     * 得到音频名
     */
    private String getAudioName() {
        return mediaItem.getName();
    }

    /**
     * 得到演唱者
     */
    private String getArtist() {
        return mediaItem.getArtist();
    }

    /**
     * 得到播放状态:isplaying
     */
    private  boolean isPlaying(){
        return audioMediaPlayer.isPlaying();
    }
    /**
     * 得到音频路径
     */
    private String getAudioPath() {
        return null;
    }

    /**
     * 得到播放模式
     */
    private int getPlayModel() {
        return playmodel;
    }


    /**
     * 设置播放模式
     */
    private void setPlayModel(int playmodel) {
        this.playmodel = playmodel;
        //将播放模式设 --缓存到本地数据
        CacheUtils.savePlayModel(this,"playmodel",playmodel);
    }

    /**
     * 设置播放进度:
     * @param position
     */
    private void seekTo(int position){
        audioMediaPlayer.seekTo(position);
    }



    /**
     * 音频准备情况监听
     */
    private class AudioOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            /**
             * 通过广播,想activity通知更新界面
             */
            notifyChange(OPEN_AUDIO);//发送广播动作
            start();
        }

        /**
         * 发送广播更新音乐信息--在 AudioPlayerActivity.java 中接受广播
         * @param action
         */
        private void notifyChange(String action) {
            Intent intent = new Intent(action);
            intent.putExtra("position",position);//将当前音乐 index 通过广播发送到activity中
            sendBroadcast(intent);
        }
    }


    /**
     * 音频播放完成监听
     */
    private class AudioOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(playmodel==MusicPlayService.PLAY_MODEL_ORDER&&position==items.size()-1) {//顺序模式 到最后一个
                Toast.makeText(MusicPlayService.this, "播放完成", Toast.LENGTH_SHORT).show();
            }else {
                playNext();
            }

        }
    }
    /**
     * 音频播放出错监听
     */
    private class AudioOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            playNext();
            Toast.makeText(MusicPlayService.this, "播放出错,播放下一曲", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}
