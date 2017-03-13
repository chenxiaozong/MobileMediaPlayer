package com.example.chen.mobilemediaplayer.domain;

import java.io.Serializable;

/**
 * Created by chen on 2017/1/9.
 * 视频列表中每一个视频文件对应的bean 对象
 *
 */

public class MediaItem implements Serializable {

    private  String name; //视频名
    private  String artist; //艺术家
    private  String data;   //绝对路径
    private  long size;     //大小
    private  long duration; //时长

    private String hightData; //高清视频路径
    private String movieTitle; //视频标题
    private String descSummary;   //视频描述
    private String imageUrl;



    public MediaItem() {
    }

    public MediaItem(String name, String artist, String data, long size, long duration) {
        this.name = name;
        this.artist = artist;
        this.data = data;
        this.size = size;
        this.duration = duration;
    }


    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getData() {
        return data;
    }

    public long getSize() {
        return size;
    }

    public long getDuration() {
        return duration;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setDuration(long duration) {
        this.duration = duration;

    }

    public String getHightData() {
        return hightData;
    }

    public void setHightData(String hightData) {
        this.hightData = hightData;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getDescSummary() {
        return descSummary;
    }

    public void setDescSummary(String descSummary) {
        this.descSummary = descSummary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "MediaItem{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", data='" + data + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", hightData='" + hightData + '\'' +
                ", movieTitle='" + movieTitle + '\'' +
                ", descSummary='" + descSummary + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
