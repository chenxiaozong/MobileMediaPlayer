package com.example.chen.mobilemediaplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.chen.mobilemediaplayer.service.MusicPlayService;

/**
 * Created by chen on 2017/3/10.
 *  数据缓存工具类:
 *  1. 缓存文本数据到
 */


public class CacheUtils {
    /**
     * 1. 缓存文本数据到preferences
     * @param context
     * @param key
     * @param value
     */
    public  static  void saveTextData(Context context, String key,String value){
        SharedPreferences preferences = context.getSharedPreferences("mediaPlayer",Context.MODE_PRIVATE);
        preferences.edit().putString(key,value).commit();//保存数据

    }

    /**
     * 2. 得到缓存的文本数据:
     * @param context
     * @param key
     * @return
     */
    public static String getTextData(Context context,String key){
        SharedPreferences preferences = context.getSharedPreferences("mediaPlayer",Context.MODE_PRIVATE);
        return  preferences.getString(key,"");

    }


    /**
     * 3. 保存音乐播放模式: 单曲,循环,顺序.... int playmodel
     */
    public static void savePlayModel(Context context,String key,int value){
        SharedPreferences preferences = context.getSharedPreferences("mediaPlayer",Context.MODE_PRIVATE);
        preferences.edit().putInt(key,value).commit();//根据Key--value 保存整形数据
    }

    public  static int getPlayModel(Context context,String key){
        SharedPreferences preferences = context.getSharedPreferences("mediaPlayer",context.MODE_PRIVATE);
        return preferences.getInt(key, MusicPlayService.PLAY_MODEL_ORDER);//默认返回顺序播放
    }

}
