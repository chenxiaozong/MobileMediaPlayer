package com.example.chen.mobilemediaplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.mobilemediaplayer.R;
import com.example.chen.mobilemediaplayer.domain.MediaItem;
import com.example.chen.mobilemediaplayer.utils.TimeFormatUilts;
import com.example.chen.mobilemediaplayer.utils.Utils;
import com.example.chen.mobilemediaplayer.view.VitamioVideoView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

//import android.media.MediaPlayer;
//import com.example.chen.mobilemediaplayer.view.VideoView;

//import android.widget.VideoView;

public class VitamioMediaPlayerActivity extends Activity implements View.OnClickListener {

    private Utils utils = new Utils();

    //是否是网络的uri
    private boolean isNetUri = false;


    //监听视频卡顿 方式
    private boolean isSystemBufferListen = false; //使用系统的监听卡顿方式

    //handler msg标识
    private final int PROGRESS = 1;

    //延时自动隐藏控制面板
    private static final int HIDE_CONTROLPAD = 2;

    private static final int MSG_NET_SPEED = 3; //发送消息测网速

    //private VideoView video_view; --替换为vitamio
    private VitamioVideoView video_view;


    //视频资源uri
    private Uri uri;

    private LinearLayout llTopSystemInfo;
    private TextView tv_video_name;
    private TextView tvSystemTime;
    private ImageView tvVideoElectricity;
    private LinearLayout llVideoControllerVoice;
    private Button btVideoControllerVoice;
    private SeekBar sbVideoControllerVoice;
    private Button sbVideoControllerSwitch;
    private LinearLayout llVideoPlayingBar;
    private TextView tvVideoControllerTime;
    private SeekBar sbVideoControllerPlayingProgress;
    private TextView tvVideoControllerDuration;
    private LinearLayout llVideoControllerPlay;
    private Button btControllerReturn;
    private Button btControllerPre;
    private Button btControllerPause;
    private Button btControllerNext;
    private Button btControllerScreen;

    //缓冲提示布局文件初始化
    private TextView tv_net_buffer_info;
    private LinearLayout ll_media_buffer;
    //视频加载布局文件初始化
    private LinearLayout ll_media_loading;
    private TextView tv_loading_netspeed;


    //系统信息提示
    private TextView tv_system_controller_info;

    private TextView tv_system_battery;

    //监听电量变化的广播
    private VideoBatteryReceiver receiver;

    //手势识别器
    private GestureDetector detector;


    /**
     * 实例化AudioManager 调节音量
     * --initData 中初始化
     */
    private AudioManager am;
    private int maxVoice = 0;
    private int currentVoice = 0;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-01-11 15:07:47 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        llTopSystemInfo = (LinearLayout) findViewById(R.id.ll_top_system_info);
        tv_video_name = (TextView) findViewById(R.id.tv_video_name);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        tvVideoElectricity = (ImageView) findViewById(R.id.tv_video_electricity);
        llVideoControllerVoice = (LinearLayout) findViewById(R.id.ll_video_controller_voice);
        btVideoControllerVoice = (Button) findViewById(R.id.bt_video_controller_voice);
        sbVideoControllerVoice = (SeekBar) findViewById(R.id.sb_video_controller_voice);
        sbVideoControllerSwitch = (Button) findViewById(R.id.sb_video_controller_switch);
        llVideoPlayingBar = (LinearLayout) findViewById(R.id.ll_video_playing_bar);
        tvVideoControllerTime = (TextView) findViewById(R.id.tv_video_controller_time);
        sbVideoControllerPlayingProgress = (SeekBar) findViewById(R.id.sb_video_controller_playing_progress);
        tvVideoControllerDuration = (TextView) findViewById(R.id.tv_video_controller_duration);
        llVideoControllerPlay = (LinearLayout) findViewById(R.id.ll_video_controller_play);
        btControllerReturn = (Button) findViewById(R.id.bt_controller_return);
        btControllerPre = (Button) findViewById(R.id.bt_controller_pre);
        btControllerPause = (Button) findViewById(R.id.bt_controller_pause);
        btControllerNext = (Button) findViewById(R.id.bt_controller_next);
        btControllerScreen = (Button) findViewById(R.id.bt_controller_screen);
        tv_system_battery = (TextView) findViewById(R.id.tv_system_battery);
        tv_system_controller_info = (TextView) findViewById(R.id.tv_system_controller_info);

        //网络缓存
        tv_net_buffer_info = (TextView) findViewById(R.id.tv_net_buffer_info); //缓冲提示textview
        ll_media_buffer = (LinearLayout) findViewById(R.id.ll_media_buffer);    //缓冲提示layout


        //onloading 视频加载
        ll_media_loading = (LinearLayout) findViewById(R.id.ll_media_loading);
        tv_loading_netspeed = (TextView) findViewById(R.id.tv_loading_netspeed);


        btVideoControllerVoice.setOnClickListener(this);
        sbVideoControllerSwitch.setOnClickListener(this);
        btControllerReturn.setOnClickListener(this);
        btControllerPre.setOnClickListener(this);
        btControllerPause.setOnClickListener(this);
        btControllerNext.setOnClickListener(this);
        btControllerScreen.setOnClickListener(this);

    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-01-11 15:07:47 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btVideoControllerVoice) {
            // Handle clicks for btVideoControllerVoice
            //Toast.makeText(this, "音量控制-静音", Toast.LENGTH_SHORT).show();
            switchMuteStatus();//切换静音状态

        } else if (v == sbVideoControllerSwitch) {
            // Handle clicks for sbVideoControllerSwitch
            Toast.makeText(this, "切换播放器", Toast.LENGTH_SHORT).show();
        } else if (v == btControllerReturn) {
            // Handle clicks for btControllerReturn
            this.finish();// finishActivity
            Log.d("SystemMediaPlayerActivi", "播放控制:返回");
        } else if (v == btControllerPre) {
            // Handle clicks for btControllerPre
            Log.d("SystemMediaPlayerActivi", "播放控制:上一曲");
            playPreVideo();
        } else if (v == btControllerPause) {//播放/暂停
            // Handle clicks for btControllerPause
            startAndPause();
        } else if (v == btControllerNext) {
            // Handle clicks for btControllerNext
            Log.d("SystemMediaPlayerActivi", "播放控制:下一曲");
            playNextVideo();
        } else if (v == btControllerScreen) {
            // Handle clicks for btControllerScreen
            Log.d("SystemMediaPlayerActivi", "播放控制:全屏");
            setVideoScreenType();
        }

    }

    /**
     * 切换静音状态
     * 1. 点击按钮时
     * >将seekbar设置为0
     * >将音量设置为 0
     * >isMute 设置为true
     * <p>
     * 2.再次点击时  isMute 设置为false
     * >将seekbar设置为 currentVoice
     * >将音量设置为 currentVoice
     * >isMute 设置为false
     */
    private boolean isMute = false;

    private void switchMuteStatus() {
        if (!isMute) {
            sbVideoControllerVoice.setProgress(0);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            //isMute = true;
            Toast.makeText(this, "音量控制:静音", Toast.LENGTH_SHORT).show();
            //btVideoControllerVoice.setBackgroundResource(R.drawable.btn_voice_ismute_selector);

        } else {
            sbVideoControllerVoice.setProgress(currentVoice);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
            //isMute = false;
            Toast.makeText(this, "音量控制:还原", Toast.LENGTH_SHORT).show();
            //btVideoControllerVoice.setBackgroundResource(R.drawable.btn_voice_selector);
        }
        isMute = !isMute;

        /**
         * 切换静音键背景
         */
        updateVoiceBtnIcon();
    }

    /**
     * 更新静音按钮背景图片(selector)--
     */
    private void updateVoiceBtnIcon() {
        if (isMute) {
            btVideoControllerVoice.setBackgroundResource(R.drawable.btn_voice_ismute_selector);
        } else {
            btVideoControllerVoice.setBackgroundResource(R.drawable.btn_voice_selector);
        }
    }

    /**
     * 设置视频播放模式：全屏（拉伸） /自适应
     * 1. 全屏：将视频拉伸为铺满屏幕
     * 2. 自适应：按视频长/宽 适应屏幕长/宽 进行播放
     * 3. 设置按钮背景色 随播放模式变化（全屏、默认）
     * <p>
     * 定义全局变量：
     *
     * @ screenWidth :int
     * @ screeHeight :int
     * @ videoWidth  :int
     * @ videoHeight :int
     * @ isFullScreen: boolean
     */

    private boolean isFullScreen = false;  //是否是全屏  ：默认不是全屏
    private int screenWidth = 0;           //屏幕宽 --initData 中出初始化
    private int screeHeight = 0;           //屏幕高

    private int videoWidth = 0;            //视频宽 --videoView 准备过程中获取
    private int videoHeight = 0;           //视频高


    private static int DEFAULT_SCREEN = 1;
    private static int FULL_SCREEN = 2;

    private void setVideoScreenType() {
        if (isFullScreen) {
            //默认-非全屏
            setVideoScreenSize(DEFAULT_SCREEN);
            btControllerScreen.setBackgroundResource(R.drawable.video_controller_screen_full_selector);
            isFullScreen = false;
        } else {
            //设置全屏播放
            setVideoScreenSize(FULL_SCREEN);
            isFullScreen = true;
            btControllerScreen.setBackgroundResource(R.drawable.video_controller_screen_default_selector);
        }
    }

    /**
     * 设置视频播放尺寸：默认大小播放
     * 1. 按长或者宽拉伸为适应屏幕播放
     *
     * @param screenSize
     */
    private void setVideoScreenSize(int screenSize) {
        if (screenSize == DEFAULT_SCREEN) {//默认播放--
            //video_view.setVideoSize(videoWidth,videoHeight);
            int playWidth = 0;
            int playHeight = 0;

            /**
             * 判断填充时，那个边先到达最大值
             */
            if (videoHeight * screenWidth > videoWidth * screeHeight) {
                playHeight = screeHeight;
                playWidth = videoWidth * screeHeight / videoHeight;
            } else if (videoHeight * screenWidth < screeHeight * videoWidth) {
                playHeight = screenWidth * videoHeight / videoWidth;
                playWidth = screenWidth;
            }

            video_view.setVideoSize(playWidth, playHeight);
            Toast.makeText(this, "自适应播放:width:" + playWidth + "height:" + playHeight, Toast.LENGTH_SHORT).show();

        } else if (screenSize == FULL_SCREEN) {//全屏播放--填充为屏幕尺寸播放
            video_view.setVideoSize(screenWidth, screeHeight);
            Toast.makeText(this, "全屏播放:width:" + screenWidth + "height:" + screeHeight, Toast.LENGTH_SHORT).show();
        }


    }


    /**
     * 控制视频播放/暂停
     */
    private void startAndPause() {
        Log.d("SystemMediaPlayerActivi", "播放控制:暂停/开始");
        if (video_view.isPlaying()) {//正在播放,点击后暂停
            //设置暂停
            video_view.pause();
            btControllerPause.setBackgroundResource(R.drawable.video_controller_start_selector);

        } else {//正在暂停,点击后播放
            video_view.start();
            btControllerPause.setBackgroundResource(R.drawable.video_controller_pause_selector);
        }
    }

    /**
     * 播放前一个视频:
     */
    private void playPreVideo() {


        if (vediolist != null && vediolist.size() > 0) {//传入视频列表--多个视频

            if (position == 0) {//当前视频是第一个视频
                Toast.makeText(this, "当前视频是列表中第一个视频", Toast.LENGTH_SHORT).show();
            } else if (position >= 1) {//有前一个视频---播放前一个视频
                position = position - 1;
                MediaItem item = vediolist.get(position);
                tv_video_name.setText(item.getName());
                video_view.setVideoPath(item.getData());


                //判断是否是网络视频:
                isNetUri = utils.isNetUri(item.getData());

                //播放前提示onloading
                ll_media_loading.setVisibility(View.VISIBLE);
            }

        } else if (uri != null) { //2. 传入的是单个视频
            Toast.makeText(this, "仅有一个视频", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有需要播放的视频", Toast.LENGTH_SHORT).show();
        }
        // setButtonStatus();在onPrepare中调用


    }

    /**
     * 播放下一个视频:
     * 1. 传入的是视频列表
     * 2. 传入的事uri
     */
    private void playNextVideo() {
        if (vediolist != null && vediolist.size() > 0) {
            //position < size()-1
            if (position < vediolist.size() - 1) {//存在下一个视频1.播放下一个视频 2.提示开始播放下一个视频 3.设置视频名 4.按钮设置亮色
                position++;
                MediaItem item = vediolist.get(position);
                video_view.setVideoPath(item.getData());

                //判断是否是网络视频
                isNetUri = utils.isNetUri(item.getData());

                Toast.makeText(this, "开始下一个视频:" + item.getName(), Toast.LENGTH_SHORT).show();
                tv_video_name.setText(item.getName());

                //播放前提示onloading
                ll_media_loading.setVisibility(View.VISIBLE);


            } else if (position == vediolist.size() - 1) { //当前视频是列表中最后一个
                Toast.makeText(this, "当前视频是最后一个视频", Toast.LENGTH_SHORT).show();
            }

        } else if (uri != null) { //只有一个视频--uri
            Toast.makeText(this, "仅有一个视频", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有视频", Toast.LENGTH_SHORT).show();
        }

        //设置按钮状态--在onPrepare中调用
        //  setButtonStatus();


    }

    /**
     * 设置前后按钮的状态:在onPrepare中调用
     */
    private void setButtonStatus() {
        if (vediolist != null && vediolist.size() > 0) {//多个视频
            if (position == 0) {
                btControllerPre.setBackgroundResource(R.drawable.btn_pre_gray);
            } else {
                btControllerPre.setBackgroundResource(R.drawable.video_controller_pre_selector);
            }
            if (position == vediolist.size() - 1) {//最后一个
                btControllerNext.setBackgroundResource(R.drawable.btn_next_gray);
            } else {
                btControllerNext.setBackgroundResource(R.drawable.video_controller_next_selector);
            }

        } else if (uri != null) {
            btControllerPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btControllerNext.setBackgroundResource(R.drawable.btn_next_gray);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化vitamioActivity
        Vitamio.isInitialized(this);

        setContentView(R.layout.activity_vitamio_media_player);
        video_view = (VitamioVideoView) findViewById(R.id.video_view);
        //4. 自定义 实例化视频播放控制菜单
        findViews();

        //5. 初始化数据--注册电量变化广播
        initData();

        handler.sendEmptyMessage(MSG_NET_SPEED);//发送消息,测网速


        //6. 从Intent 中获取数据
        getData();
        setPlayListener();        //1. 设置监听
        setUri();                 //2. 设置视频uri
        //3. 设置控制按钮
        //video_view.setMediaController(new MediaController(this)); --使用自定义的控制按钮
    }

    /**
     * 1. 从Inten中获取视频列表
     * 2. 获取Intent中uri (若通过uri传入 视频,则获取uri)
     * 3. 获取Inent中点击位置
     */
    private List<MediaItem> vediolist;
    private int position;

    private void getData() {
        vediolist = (List<MediaItem>) this.getIntent().getSerializableExtra("videolist");
        position = this.getIntent().getIntExtra("position", 0);
        uri = getIntent().getData(); //来自文件夹,图片浏览器

        setData();//设置数据:设置uri 设置视频名称

    }

    /**
     * 设置数据:
     * 1. 传入视频列表: 设置视频名称 ②设置视频播放路径
     * 2. 传入视频Uri:  设置视频 Uri
     * 3. 其它  没有传入视频数据
     */
    private void setData() {
        //设置数据
        if (vediolist != null && vediolist.size() > 0) {//传入视频列表
            MediaItem item = vediolist.get(position);

            //判断是否是网络视频:
            isNetUri = utils.isNetUri(item.getData());

            tv_video_name.setText(item.getName());
            video_view.setVideoPath(item.getData());
        } else if (uri != null) {//从uri 中获取播放视频(来自文件夹或者图片浏览器)---被其它软件调用时

            //判断是否是网络视频
            isNetUri = utils.isNetUri(uri.toString());
            setUri();
            setVideoName();
        } else {
            Toast.makeText(this, "没有待播放数据", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 设置视频名称
     * 当intent传入的是视频Uri设置视频名称---同个截取uri中视频路径
     */
    private void setVideoName() {
        String uriString = uri.toString();
        String videoName = uriString.substring(uriString.lastIndexOf("/") + 1);
        tv_video_name.setText(videoName);
    }

    /**
     * 初始化数据:
     * 1. 注册电量广播:
     */
    private void initData() {
        //1. 注册电量广播
        receiver = new VideoBatteryReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);//过滤电量变化的广播
        registerReceiver(receiver, intentFilter);// 注册


        //2. 实例化手势识别器
        detector = new GestureDetector(this, new VideoGestureDetector());


        //3. 获取设备屏幕尺寸
        getScreenSize();

        //4. 获取设备音量
        getAudioVoice();

    }

    /**
     * 获取设备音量并初始化voicebar 的状态
     */
    private void getAudioVoice() {
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        initVoiceSeekBar();
    }

    /**
     * 初始化 音量滑动条的状态:
     * 1. 设置seekbar最大值:maxVoice
     * 2.设置音量大小为currentVoice
     */
    private void initVoiceSeekBar() {
        sbVideoControllerVoice.setMax(maxVoice);
        sbVideoControllerVoice.setProgress(currentVoice);
    }

    /**
     * 获取屏幕尺寸
     */
    private void getScreenSize() {
        //方法一：过时方法
        //screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        //screeHeight = getWindowManager().getDefaultDisplay().getHeight();

        //方法二：
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        screenWidth = display.widthPixels;
        screeHeight = display.heightPixels;
    }

    /**
     * 得到系统当前时间
     *
     * @return
     */
    private String getSytemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 设置播放的uri
     */
    private void setUri() {
        if (uri != null) {
            video_view.setVideoURI(uri);
        }
    }

    /**
     * videoview 设置监听
     */
    private void setPlayListener() {
        //1. 视频准备 监听
        video_view.setOnPreparedListener(new MyOnPreparedListener());


        //2. 播放出错监听
        video_view.setOnErrorListener(new MyOnErrorListener());


        //3. 播放完成监听
        video_view.setOnCompletionListener(new MyOnCompletionListener());

        //4. 设置播放进度seekbar 拖动视频进度监听
        sbVideoControllerPlayingProgress.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
        //5. 设置音量seekbar
        sbVideoControllerVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());


        //6. 设置网络视频卡顿监听

        if (isSystemBufferListen) { //使用系统的缓冲卡顿监听
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //大于等于17版本使用以下方法--系统封装API
                video_view.setOnInfoListener(new MyOnInfoListener());
            } else {
                // 低于17版本使用以下方法--
                isSystemBufferListen = false; //使用自定义方式监听
            }
        }
    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d("MyOnInfoListener", "开始卡顿");
                    ll_media_buffer.setVisibility(View.VISIBLE);
                    tv_net_buffer_info.setText("视频加载中....80kb/s");//设置缓冲提示内容
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    ll_media_buffer.setVisibility(View.GONE); //隐藏缓冲提示框

                    break;
            }

            return false;
        }
    }


    /**
     * MyOnPreparedListener：视频准备状态的监听类  *
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            //获取视频尺寸
            videoHeight = mp.getVideoHeight();
            videoWidth = mp.getVideoWidth();

            ll_media_loading.setVisibility(View.GONE);


            video_view.start();
            Log.d("MyOnPreparedListener", "vitamio 播放:-----");

            /**
             * 设置进度条进度更新
             * 1. 关联视频时长和进度条的max
             * 2. 发送handler消息更新seekbar
             * 3. 设置视频总时间
             * 4. 设置视频名称 tv_video_name 从uri 中截取视频名-----转移到setUri中设置
             * 5. 设置视频控制面板按钮状态
             * 6. 设置视频控制面板3s 后自动隐藏 --handler发送 延时3s的消息
             */

            //video_view.setVideoSize(200,200); //设置自定义视频的宽高--测试

            //video_view.setVideoSize(mp.getVideoWidth(),mp.getVideoHeight()); //按视频实际宽高设置video_view

/*            //设置视频名称 --转移到setUri中设置
            String uriString = uri.toString();
            String videoName = uriString.substring(uriString.lastIndexOf("/")+1);
            tv_video_name.setText(videoName);*/

            int duration = (int) video_view.getDuration(); //获取视频时长
            sbVideoControllerPlayingProgress.setMax(duration);  //设置seekbar最大值

            tvVideoControllerDuration.setText(new TimeFormatUilts().stringForTime(duration)); //设置视频时长textview
            handler.sendEmptyMessage(PROGRESS); //发送handler消息 ---更新进度
            setButtonStatus();

            //6. 发送延时消息,3s 后隐藏控制菜单
            handler.removeMessages(HIDE_CONTROLPAD);
            handler.sendEmptyMessageDelayed(HIDE_CONTROLPAD, 3000);


        }
    }


    //上次视频播放进度  --用于检测视频卡顿
    private int prePosition = 0;
    /**
     * 使用handler处理消息
     * 1. 更新seekbar 视频进度
     * 2. 设置系统时间 每一秒更新一次
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_NET_SPEED://没隔2s测试一次网速
                    //1. 获取网速
                    String netSpeed = utils.getNetSpeed(VitamioMediaPlayerActivity.this);

                    //2. 更新界面
                    tv_loading_netspeed.setText("视频加载中..." + netSpeed);
                    tv_net_buffer_info.setText("视频缓冲中..." + netSpeed);


                    //3. 移除handler消息, 延时2s后重新发送消息
                    handler.removeMessages(MSG_NET_SPEED);
                    handler.sendEmptyMessageDelayed(MSG_NET_SPEED, 2000);
                    break;

                case HIDE_CONTROLPAD://延时3s隐藏控制面板
                    setControlPadGone(); //隐藏控制面板
                    break;

                case PROGRESS: //更新seekbar视频进度
                    //获取当前时间
                    int duration = (int) video_view.getDuration();
                    int currentPosition = (int) video_view.getCurrentPosition();

                    //1. 设置seekbar 进度与视频播放进度同步
                    sbVideoControllerPlayingProgress.setProgress(currentPosition);
                    //2. 设置视频进度时间
                    tvVideoControllerTime.setText(new TimeFormatUilts().stringForTime(currentPosition));


                    //4. 设置系统时间
                    tvSystemTime.setText(getSytemTime());


                    /**
                     * 5. 网络视频显示缓冲   * 2017/03/07 - 14:30
                     */

                    if (isNetUri) {

                        //设置缓冲进度
                        int buffer = video_view.getBufferPercentage();
                        int totalBuffer = buffer * sbVideoControllerPlayingProgress.getMax();
                        int secondProgress = totalBuffer / 100;

                        sbVideoControllerPlayingProgress.setSecondaryProgress(secondProgress);

                    } else {
                        sbVideoControllerPlayingProgress.setSecondaryProgress(0);
                    }


                    //6. 监听视频卡顿--
                    if (!isSystemBufferListen) {
                        videoBufferInfo(currentPosition);
                    }
                    Log.d("VitamioMediaPlayerActiv", "isSystemBufferListen:" + isSystemBufferListen);


                    //3. 移除handler消息, 延时1s后重新发送消息
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };

    /**
     * 监听视频卡顿--自定义方式
     *
     * @param currentPosition
     */
    private void videoBufferInfo(int currentPosition) {
        if (!isSystemBufferListen && video_view.isPlaying()) { //使用自定义检测方式
            int deltaBuffer = currentPosition - prePosition;
            prePosition = currentPosition;

            if (deltaBuffer <= 500 && deltaBuffer >= 0) {
                ll_media_buffer.setVisibility(View.VISIBLE);
                tv_net_buffer_info.setText("正中加载中");
            } else if (deltaBuffer < 0) {//手指定位到播放位置前
                //prePosition = 0;
                //ll_media_buffer.setVisibility(View.GONE);
            } else {
                //不卡了
                ll_media_buffer.setVisibility(View.GONE);
            }
        } else {//当没有正在播放视频(不卡顿时,隐藏缓冲提示)
            ll_media_buffer.setVisibility(View.GONE);
        }
    }

    /**
     * 类:视频播放出错监听
     * 播放出错情况
     * 1. 视频格式不支持 --跳转到万能解码器,继续播放
     * 2. 网络中断
     * > 网络中断
     * > 网络断断续续---重新播放
     * <p>
     * 3. 视频中断(视频错误)--本地文件有空白
     */
    class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(VitamioMediaPlayerActivity.this, "视频播放出错", Toast.LENGTH_SHORT).show();

            showErrorDialog();
            return true;//弹出系统对话框
            //return false;//弹出系统对话框
        }
    }

    /**
     * 弹出播放错误对话框
     *
     */
    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("无法播放视频");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        //AlertDialog dialog = builder.create();
        builder.show();
    }


    /**
     * 类:视频播放完成监听
     */
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(VitamioMediaPlayerActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
            playNextVideo();
        }
    }

    /**
     * 播放进度条拖动:
     * SeekBar 拖动监听实现类     *
     */
    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        /**
         * seekbar 拖动时触发
         *
         * @param seekBar
         * @param progress
         * @param fromUser :为true 表示seekbar 的改变是由用户引起的
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {//
                video_view.seekTo(progress);
            }
        }

        //seekbar 按下时触发
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_CONTROLPAD);

        }

        //seekbar 松开时触发
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_CONTROLPAD, 3000);
        }
    }

    /**
     * 音量:seekbar 拖动监听
     */
    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                //判断是否静音
                if (progress > 0) {
                    isMute = false; //当滑动音量条时,需要将静音状态改为false
                    // sbVideoControllerVoice.setBackgroundResource(R.drawable.btn_voice_selector);
                } else {
                    isMute = true;
                    // sbVideoControllerVoice.setBackgroundResource(R.drawable.btn_voice_ismute_selector);

                }

                updateVoice(progress); //更新进度条和音量
                updateVoiceBtnIcon();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_CONTROLPAD);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_CONTROLPAD, 3000);
        }
    }

    /**
     * 更新音量
     */
    private void updateVoice(int progress) {
        //Log.d("VoiceOnSeekBarChangeLis", "progress:" + progress);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);//最后一个参数可为1/0
        currentVoice = progress;
        sbVideoControllerVoice.setProgress(progress);
    }


    /**
     * 接收电量变化广播的类
     */
    class VideoBatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setBattery(level);
        }
    }


    /**
     * 手势识别器监听类
     * 1. 长按: onLongPress   :播放/暂停
     * 2. 单击: onSingleTapConfirmed   :显示/隐藏控制菜单
     * 3. 双击: onDoubleTap            :播放/暂停
     * 4. 按下: onDown
     * 5. 按下不动:onshowPress
     */
    class VideoGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            Log.d("VideoGestureDetector", "onLongPress");
            startAndPause();
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d("VideoGestureDetector", "onDoubleTap");
            startAndPause();//切换播放和暂停
            return super.onDoubleTap(e);

        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("VideoGestureDetector", "onSingleTapConfirmed");
            //显示/隐藏控制菜单
            showOrHideControlButton();//切换控制栏显示或者隐藏
            return super.onSingleTapConfirmed(e);
        }


        @Override
        public void onShowPress(MotionEvent e) {
            Log.d("VideoGestureDetector", "onShowPress");
            super.onShowPress(e);

        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("VideoGestureDetector", "onDown");
            return super.onDown(e);
        }
    }

    /**
     * 显示/隐藏控制菜单
     */
    private boolean isControlPadShow = true; //控制面板显示的状态 true 为显示

    private void showOrHideControlButton() {

        /*方式一:通过设置boolean标识切换显示和隐藏
        if(isControlPadShow) {
            setControlPadGone();
            isControlPadShow = false;
        }else {
            isControlPadShow = true;
            setControlPadShow();
        }*/


        //方式二:通过视图对象,获取其显示状态
        //int visibility = llVideoControllerVoice.getVisibility(); //visibility==0 代表显示   ...==8
        int visibility1 = llVideoControllerPlay.getVisibility();
        if (visibility1 == 0) {
            setControlPadGone();
        } else {
            setControlPadShow();
        }

    }

    /**
     * 显示控制面板
     * 1. 音量 :llVideoControllerVoice
     * 2. 控制按钮 :llVideoControllerPlay
     * 3. 播放进度条:llVideoPlayingBar
     * <p>
     * 4. 显示控制面板后: 发送延时消息,3s后隐藏控制面板
     */
    private void setControlPadShow() {
        llVideoControllerVoice.setVisibility(View.VISIBLE);
        llVideoControllerPlay.setVisibility(View.VISIBLE);
        llVideoPlayingBar.setVisibility(View.VISIBLE);

        //延时3s 隐藏控制面板
        handler.removeMessages(HIDE_CONTROLPAD);
        handler.sendEmptyMessageDelayed(HIDE_CONTROLPAD, 3000);//延时3s 后隐藏控制菜单
    }

    /**
     * 隐藏控制面板
     */
    private void setControlPadGone() {
        llVideoControllerVoice.setVisibility(View.GONE); //隐藏音量控制栏布局
        llVideoControllerPlay.setVisibility(View.GONE);  //隐藏播放控制栏布局
        llVideoPlayingBar.setVisibility(View.GONE);      //隐藏播放进度条
    }


    /**
     * 设置播放页面的电量值
     */
    private void setBattery(int level) {
        tv_system_battery.setText(level + "%");
        if (level <= 0) {
            tvVideoElectricity.setBackgroundResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            tvVideoElectricity.setBackgroundResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            tvVideoElectricity.setBackgroundResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            tvVideoElectricity.setBackgroundResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            tvVideoElectricity.setBackgroundResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            tvVideoElectricity.setBackgroundResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            tvVideoElectricity.setBackgroundResource(R.drawable.ic_battery_100);
        }
    }


    /**
     * activiy生命周期
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("SystemMediaPlayerActivi", "onStart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("SystemMediaPlayerActivi", "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("SystemMediaPlayerActivi", "onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("SystemMediaPlayerActivi", "onStop()");
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);//移除所有消息
        
        if (receiver != null) {//释放广播资源
            unregisterReceiver(receiver);
            receiver = null;
        }


        super.onDestroy();
        Log.d("SystemMediaPlayerActivi", "onDestroy()");
    }


    /**
     * 滑动屏幕改变音量相关变量声明
     */
    private float startY; //按下处y坐标
    private float endY;   //松开处y坐标
    private float distanceY;//y坐标移动的距离
    private float rangY;// y方向可移动的最大距离


    private int downBright; //触摸按下时,原来亮度大小

    private int mVoice;   //改变音量大小
    private int downVoice; //原来音量

    private float startX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);//将触屏事件传递给手势识别器

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                rangY = Math.min(screeHeight, screenWidth);

                if (startX < screenWidth / 4) {//按下位置在屏幕左侧1/4 ---------屏幕亮度调节
                    downBright = getScreenBrightness();//获取当前屏幕亮度,标识isAutoBrightness
                } else if (startX > 3 * screenWidth / 4) {

                    downVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                    handler.removeMessages(HIDE_CONTROLPAD); //移除延时隐藏面板消息

                }

                break;
            case MotionEvent.ACTION_MOVE:
                endY = event.getY();
                distanceY = startY - endY;
                if (startX < screenWidth / 4) {//屏幕左侧 --调节亮度
                    //设置屏幕亮度
                    setScreenBrightness(distanceY);

                } else if (startX > screenWidth * 3 / 4) { //屏幕3/4右侧
                    updateVoiceWithTouch();//调节音量
                }

                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE_CONTROLPAD, 500);//重新发送延时隐藏面板消息
                tv_system_controller_info.setVisibility(View.GONE);
                break;

        }

        return super.onTouchEvent(event);
    }

    /**
     * 设置屏幕亮度:
     *
     * @param distanceY
     */
    private void setScreenBrightness(float distanceY) {

//        //判断是否开启自动调节亮度 若开启-> 关闭自动调节
//        if (isAutoBrightness()) {//若当前activiy是自动调节亮度,则取消其自动调节亮度
//            stopAutoBrightness();
//        }

        float delta = (distanceY / screeHeight) * 255;//根据y方向移动距离--> 亮度值
        if (delta != 0) {

            int brightness = (int) Math.min(Math.max(getScreenBrightness() + delta, 0), 255);

            WindowManager.LayoutParams lp = this.getWindow().getAttributes();
            lp.screenBrightness = brightness / 255f;
            this.getWindow().setAttributes(lp);

            //3.显示亮度调节信息 info
            tv_system_controller_info.setVisibility(View.VISIBLE);
            tv_system_controller_info.setText("亮度:" + brightness);
        }
    }

    /**
     * 判断是否开启自动亮度调节
     * @return
     */
    private boolean isAutoBrightness() {
        ContentResolver resolver = this.getContentResolver();
        boolean isAutoBrightness = true;

        try {
            isAutoBrightness= Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return  isAutoBrightness;
    }


    /**
     * 获取当前Activiy的亮度
     *
     * @return
     */
    public int getScreenBrightness() {
        int currentBright = 0;
        ContentResolver resolver = this.getContentResolver();
        try {
            currentBright = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
            //判断是否自动调节亮度
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return currentBright;
    }

    /**
     * 停止Activiy自动亮度调节
     * 若不停止,设置不起作用
     */
    public void stopAutoBrightness(/*Activity activity*/) {
        Settings.System.putInt(this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 开启亮度自动调节 *
     */
    public void startAutoBrightness(/*Activity activity*/) {
        Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }


    /**
     * 根据触摸,调节音量
     */
    private void updateVoiceWithTouch() {
        mVoice = (int) ((distanceY / rangY) * maxVoice);
        int voice = Math.min(Math.max(0, downVoice + mVoice), maxVoice);
        if (mVoice != 0) {
            if (voice == 0) {
                isMute = true;
            } else {
                isMute = false;
            }
            updateVoice(voice);
            updateVoiceBtnIcon();
            sbVideoControllerVoice.setProgress(voice); //设置音量seekbar进度

            //显示音量seekbar
            if (llVideoControllerVoice.getVisibility() == View.GONE) {
                llVideoControllerVoice.setVisibility(View.VISIBLE);
            }

            //显示音量调节信息
            tv_system_controller_info.setVisibility(View.VISIBLE);
            tv_system_controller_info.setText("音量:" + voice);
        }
    }

    /**
     * 监听按键,调整音量
     *
     * @param keyCode
     * @param event
     * @return
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //1. 判读按键并更新音量
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //1. 更新音量值
            if (currentVoice > 0) {
                currentVoice--; //更新currentVoice
            } else {
                currentVoice = 0;
                isMute = true;
            }
            updateVoice(currentVoice);
            updateVoiceBtnIcon();
            //2. 显示音量控制seekbar
            if (llVideoControllerVoice.getVisibility() == View.GONE) {
                llVideoControllerVoice.setVisibility(View.VISIBLE);
            }
            // 3. 清除handler消息的计时
            handler.removeMessages(HIDE_CONTROLPAD);
            handler.sendEmptyMessageDelayed(HIDE_CONTROLPAD, 2000);//重新发送延时隐藏面板消息
            return true; //若return false ;则未消耗按键,会同时显示系统音量进度条

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            //1. 更新音量值
            if (currentVoice < maxVoice) {
                currentVoice++;
            } else {
                currentVoice = maxVoice;
            }
            isMute = false;
            updateVoice(currentVoice);
            updateVoiceBtnIcon();

            //2. 显示音量控制seekbar
            if (llVideoControllerVoice.getVisibility() == View.GONE) {
                llVideoControllerVoice.setVisibility(View.VISIBLE);
            }
            //3. 清除handler消息的计时
            handler.removeMessages(HIDE_CONTROLPAD);
            handler.sendEmptyMessageDelayed(HIDE_CONTROLPAD, 2000);//重新发送延时隐藏面板消息
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}