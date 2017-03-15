package com.example.chen.mobilemediaplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.mobilemediaplayer.IMusicPlayService;
import com.example.chen.mobilemediaplayer.R;
import com.example.chen.mobilemediaplayer.domain.MediaItem;
import com.example.chen.mobilemediaplayer.service.MusicPlayService;
import com.example.chen.mobilemediaplayer.utils.TimeFormatUilts;
import com.example.chen.mobilemediaplayer.view.ShowLyricView;

import java.io.Serializable;
import java.util.List;


/**
 * 音乐播放器界面
 */
public class AudioPlayerActivity extends Activity implements View.OnClickListener {

    private ImageView ivMusicPlaying;
    private TextView tvMusicName;
    private TextView tvMusicAuthor;
    private ShowLyricView tvMusicLyrics;
    private LinearLayout llAudioControlPad;
    private LinearLayout llAudioPlayingBar;
    private TextView tvAudioInfoCurrent;
    private SeekBar sbAudioControllerPlayingProgress;
    private TextView tvAudioInfoDuration;
    private LinearLayout llVideoControllerPlay;
    private Button btMusicPlayModel;
    private Button btControllerPre;
    private Button btControllerPause;
    private Button btControllerNext;
    private Button btControllerLyrics;


    private int position = 0;//被点击音乐在list中的坐标

    private List<MediaItem> audioItems;
    private IMusicPlayService service;

    private boolean isPlaying = true;
    private boolean isNotification;// 标识intent是从通知栏启动--true;


    private BroadcastReceiver receiver; //实例化广播接收器

    private TimeFormatUilts timeUtils = new TimeFormatUilts();

    //创建handler 发送1s 延时消息,更新播放进度和时间
    private final int UPDATE_PROGRESS = 1; //更新播放进度

    private final int UPDATE_LYRIC = 2; //发送更新歌词消息
    private boolean isNewLyric = false; //标识是否需要更新歌词地址(切换歌曲,或者从通知栏进入时需要更新地址)


    /**
     * 创建handler
     * 1. 更新播放进度 和播放时间onse
     * 2.
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case UPDATE_LYRIC://跟新显示歌词
                    try {

                        if (isNewLyric) {//需要更新歌词地址
                            //歌曲路径: "/storage/sdcard0/刘惜君 - 悠蓝曲.mp3"
                            //storage/sdcard0/刘惜君 - 悠蓝曲.+.lrc (.txt)
                            String audioPath = service.getAudioPath();
                            String path = audioPath.substring(0,audioPath.lastIndexOf("."));
                            String lyricPath = path+".lrc";
                            tvMusicLyrics.setLyricUrl(lyricPath);

                            isNewLyric = false;
                        }


                        //1. 获取当前进度
                        int timePoint = service.getCurrentProgress();//得到当前时间
                        //2. 将进度传入ShowLyricView.java-->index
                        tvMusicLyrics.setTimePoint(timePoint);//设置时间-->index

                        //3. 发送消息
                        handler.removeMessages(UPDATE_LYRIC);
                        handler.sendEmptyMessage(UPDATE_LYRIC);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

                case UPDATE_PROGRESS:
                    try {
                        int currentPosition = service.getCurrentProgress();//当前时长
                        int duration = (int) service.getDuration(); //总时长
                        tvAudioInfoCurrent.setText(timeUtils.stringForTime(currentPosition));//跟进时间

                        sbAudioControllerPlayingProgress.setMax(duration);
                        sbAudioControllerPlayingProgress.setProgress(currentPosition);//更新播放进度

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    handler.removeMessages(UPDATE_PROGRESS);
                    handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
                    break;
            }
        }
    };


    //service连接状态监听
    private ServiceConnection connection = new ServiceConnection() {
        /**
         * 绑定服务成功时:调用此方法
         * @param name
         * @param binder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = IMusicPlayService.Stub.asInterface(binder);
            if (service != null) {
                try {
                    //判断是否是通过通知栏启动的intent
                    if (!isNotification) {//从列表启动的播放器
                        service.openAudio(position);
                    } else {//从通知栏进入播放音乐的service
                        showAudioInfoView();//显示歌曲信息(包含发送handler消息更新进度)
                        isNewLyric = true;
                        handler.sendEmptyMessage(UPDATE_LYRIC);//从通知栏进入
                    }

                    showButtonPlayModel();//更新播放模式按钮背景图标
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 连接服务失败时,调用此方法
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (service != null) {
                try {
                    service.stop();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                service = null;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews(); //初始化试图对象

        initData();

        getDataFromIntent();//获取数据
        startBindService();//绑定服务

        updateMusicAnimation();//更新播放动画

    }

    /**
     * 初始化数据:
     * 1. 创建并接受广播
     */
    private void initData() {
        //1. 注册广播 --在ondestroy中取消注册
        receiver = new AudioReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayService.OPEN_AUDIO);//添加广播过滤动作

        registerReceiver(receiver, intentFilter);
    }


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-03-11 11:05:23 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_audio_player);


        ivMusicPlaying = (ImageView) findViewById(R.id.iv_music_playing);
        tvMusicName = (TextView) findViewById(R.id.tv_music_name);
        tvMusicAuthor = (TextView) findViewById(R.id.tv_music_author);
        tvMusicLyrics = (ShowLyricView) findViewById(R.id.tv_music_lyrics);
        llAudioControlPad = (LinearLayout) findViewById(R.id.ll_audio_control_pad);
        llAudioPlayingBar = (LinearLayout) findViewById(R.id.ll_audio_playing_bar);
        tvAudioInfoCurrent = (TextView) findViewById(R.id.tv_audio_info_current);
        sbAudioControllerPlayingProgress = (SeekBar) findViewById(R.id.sb_audio_controller_playing_progress);
        tvAudioInfoDuration = (TextView) findViewById(R.id.tv_audio_info_duration);
        llVideoControllerPlay = (LinearLayout) findViewById(R.id.ll_video_controller_play);
        btMusicPlayModel = (Button) findViewById(R.id.bt_music_play_model);
        btControllerPre = (Button) findViewById(R.id.bt_controller_pre);
        btControllerPause = (Button) findViewById(R.id.bt_controller_pause);
        btControllerNext = (Button) findViewById(R.id.bt_controller_next);
        btControllerLyrics = (Button) findViewById(R.id.bt_controller_lyrics);

        btMusicPlayModel.setOnClickListener(this);
        btControllerPre.setOnClickListener(this);
        btControllerPause.setOnClickListener(this);
        btControllerNext.setOnClickListener(this);
        btControllerLyrics.setOnClickListener(this);

        //进度条设置监听
        sbAudioControllerPlayingProgress.setOnSeekBarChangeListener(new AudioOnSeekBarChangeListener());
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-03-11 11:05:23 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btMusicPlayModel) {//切换播放模式
            setPlayModel();//设置播放模式
        } else if (v == btControllerPre) {//播放上一曲
            playPreAudio();
        } else if (v == btControllerPause) {//暂停--播放
            pauseAndStart();//切换播放--暂停
        } else if (v == btControllerNext) {
            playNetAudio();
        } else if (v == btControllerLyrics) {
            Toast.makeText(this, "btMusicPlayModel", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 播放下一曲:
     */
    private void playNetAudio() {
        try {
            service.playNext();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放上一曲:
     * 1. 播放模式:单曲循环 -->播放上一曲
     */
    private void playPreAudio() {
        try {
            service.playPre();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击按钮:设置播放模式
     * 顺序-->单曲-->随机-->顺序
     */
    private void setPlayModel() {
        try {
            int playmodel = service.getPlayModel();
            if (playmodel == MusicPlayService.PLAY_MODEL_ORDER) {//顺序播放
                playmodel = MusicPlayService.PLAY_MODEL_SINGLE;
                Toast.makeText(this, "单曲播放", Toast.LENGTH_SHORT).show();
            } else if (playmodel == MusicPlayService.PLAY_MODEL_SINGLE) {//切换到单曲循环
                playmodel = MusicPlayService.PLAY_MODEL_REPEATE;
                Toast.makeText(this, "循环播放", Toast.LENGTH_SHORT).show();
            } else if (playmodel == MusicPlayService.PLAY_MODEL_REPEATE) {
                playmodel = MusicPlayService.PLAY_MODEL_ORDER;
                Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
            } else {
                playmodel = MusicPlayService.PLAY_MODEL_ORDER;
            }
            service.setPlayModel(playmodel);

            showButtonPlayModel();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示playmodelbutton 的背景图片
     */
    private void showButtonPlayModel() {
        try {
            int playmodel = service.getPlayModel();
            if (playmodel == MusicPlayService.PLAY_MODEL_ORDER) {//顺序播放
                btMusicPlayModel.setBackgroundResource(R.drawable.music_paly_model_selector_order);
            } else if (playmodel == MusicPlayService.PLAY_MODEL_SINGLE) {//切换到单曲循环
                btMusicPlayModel.setBackgroundResource(R.drawable.music_paly_model_selector_signal);
            } else if (playmodel == MusicPlayService.PLAY_MODEL_REPEATE) {//切换到随机播放
                btMusicPlayModel.setBackgroundResource(R.drawable.music_paly_model_selector_repeate);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    /**
     * 切换播放和暂停
     */
    private void pauseAndStart() {
        if (service != null) {
            try {
                isPlaying = service.isPlaying();

                if (isPlaying) {
                    //暂停
                    //按钮--->指示播放
                    service.pause();
                    btControllerPause.setBackgroundResource(R.drawable.music_play_start_selector);
                } else {
                    //开始播放
                    //按钮--->指示暂停
                    service.start();
                    btControllerPause.setBackgroundResource(R.drawable.music_play_pause_selector);
                }

                isPlaying = !isPlaying;//更新播放状态

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            updateMusicAnimation();//切换播放动画
        }
    }


    /**
     * 启动并绑定服务
     * 运行情况:
     * 1. 点击item 进入播放界面
     * 2. 通过通知栏进入界面
     */
    private void startBindService() {
        Intent intent = new Intent(this, MusicPlayService.class);
        intent.setAction("com.example.chen.mobilemediaplayer_MUSIC_PLAY");


        Bundle bundle = new Bundle();
        bundle.putSerializable("videolist", (Serializable) audioItems);//添加序列化 对象
        intent.putExtras(bundle);

        intent.putExtra("position", position);//传入点击item 的下标-->service*/

        //connection
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        startService(intent);//使用此方法,防止实例化多个service


    }

    /**
     * 从Inten中得到音乐列表信息
     */
    private void getDataFromIntent() {
        //从音乐列表页进入 或从通知栏进入时 需要获取数据
        audioItems = (List<MediaItem>) this.getIntent().getSerializableExtra("videolist");

        isNotification = this.getIntent().getBooleanExtra("isNotification", false);//是否从通知栏启动

        position = this.getIntent().getIntExtra("position", 0);
    }


    /**
     * 设置音乐播放页面动画
     */
    private void updateMusicAnimation() {
        ivMusicPlaying.setBackgroundResource(R.drawable.music_animation);
        AnimationDrawable rockAnimation = (AnimationDrawable) ivMusicPlaying.getBackground();
        if (isPlaying) {
            rockAnimation.start();
        } else {
            rockAnimation.stop();
        }
    }

    /**
     * 广播接受器:接受service发出的开始播放音乐广播
     * 1.更新界面音乐信息
     * 2.跟新界面音乐播放模式的按钮状态
     */
    private class AudioReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //int index = intent.getIntExtra("position",0);
            showAudioInfoView();//更新音乐信息---歌曲时长,演唱者,歌曲名

            isNewLyric = true;
            handler.sendEmptyMessage(UPDATE_LYRIC);// 准备好歌曲后 发送handler通知更新歌词--切换歌曲,更新进度
        }
    }

    /**
     * 显示播放界面的音乐信息
     * 1. 显示演唱者
     * 2. 显示歌曲名
     * 3. 显示歌曲时长
     * 4. 发送延时1s 的handler消息 更新进度
     */
    private void showAudioInfoView() {
        try {
            tvMusicAuthor.setText(service.getArtist());//作家
            tvMusicName.setText(service.getAudioName());//歌曲名
            tvAudioInfoDuration.setText(timeUtils.stringForTime((int) service.getDuration()));//时长


        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d("AudioReceiver11", "showAudioInfoView()");
        //发送消息更新进度和时间
        handler.sendEmptyMessage(UPDATE_PROGRESS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);//null 表示移除所有消息和回调


        //解绑服务
        if (connection != null) {
            unbindService(connection);
            connection = null;
        }

        //取消注册广播
        if (receiver != null) {
            unregisterReceiver(receiver);

            receiver = null;
        }
    }

    /**
     * 音乐播放进度条监听:
     */
    private class AudioOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
