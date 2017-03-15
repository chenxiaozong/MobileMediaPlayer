// IMusicPlayService.aidl
package com.example.chen.mobilemediaplayer;

// Declare any non-default types here with import statements

interface IMusicPlayService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);



    /**
     * 打开音频:
     */
      void  openAudio(int position);

    /**
     * 播放音频
     */

      void  start();

    /**
     * 暂停音频
     */
      void  pause();

    /**
     * 播放下一曲
     */
      void  playNext();

    /**
     * 播放上一曲
     */
      void  playPre();


    /**
     * 停止播放
     */
      void  stop();

    /**
     * 得到当前进度
     */
      int  getCurrentProgress();


    /**
     * 得到时长
     */
      long  getDuration();

    /**
     * 得到音频名
     */
      String  getAudioName();

    /**
     * 得到演唱者
     */
      String  getArtist();


    /**
     * 得到音频路径
     */
      String  getAudioPath();

    /**
     * 得到播放模式
     */
      int  getPlayModel();


    /**
     * 设置播放模式
     */
      void  setPlayModel(int playmodel);


    /**
     * 得到播放状态:isplaying
     */
      boolean isPlaying();
    /**
     * 设置播放进度:
     * @param position
     */
    void seekTo(int position);
    /**
     * 得到当前音乐在列表中的index
     */

     int getAudioItemIndex();
}
