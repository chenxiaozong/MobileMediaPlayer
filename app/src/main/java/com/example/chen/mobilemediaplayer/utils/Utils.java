package com.example.chen.mobilemediaplayer.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chen on 2017/3/7.
 */

public class Utils {
    /**
     * 判断uri资源是否是网络资源
     * >若是网络资源,返回true
     * >否则,返回false
     * @param uri
     * @return
     */
    public boolean isNetUri(String uri){
        boolean result = false;
        if(uri!=null) {
            if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")) {
                result = true;
            }
        }
        return result;
    }



    /**
     * 得到网速:
     * 思路:思路就是每隔一个时间段就去获取这个时间段获取到的网络数据的大小，然后通过计算获得网速值。
     * @param context
     * @return
     */
    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;
    public String getNetSpeed(Context context) {

        String netSpeed = "";

        //获取当前数据大小
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)== TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB;
        //获取当前时间
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        netSpeed = String.valueOf(speed)+"Kb/s";

        return  netSpeed;
    }


    /**
     * 得到系统当前时间
     * @return
     */
    public String getSytemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return  simpleDateFormat.format(new Date());
    }


    /**
     * 检测是否能联网
     */
    public  boolean isNetAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo()!=null) {
            return  cm.getActiveNetworkInfo().isAvailable();
        }

        return false;
    }


}
