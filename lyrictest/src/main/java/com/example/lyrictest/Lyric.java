package com.example.lyrictest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by chen on 2017/3/14.
 * 作用: lrc 文件对应的domain
 * 1. 时间戳
 * 2.    [ti:北京北京]
         [ar:汪峰]
         [al:勇敢的心]
         [by:(5nd音乐网)www.5nd.com]
         [00:02.17]歌曲名：北京北京
         [00:04.00]演唱：汪峰
         [00:06.84]☆◇鉁稀↘愛★ 制
         [00:08.62]
         [00:31.16]当我走在这里的每一条街道
         [00:37.32]我的心似乎从来都不能平静
 [00:45.23]就让花朵妒忌红名和电气致意
 */

public class Lyric {

    private  String title;   //[ti:北京]
    private  String artist;  //[ar:汪峰]
    private  String by;      //[by:汪峰]
    private  String al;      //[by:汪峰]


    private ArrayList<LineLyric>  lineLyrics  = new ArrayList<>();


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getAl() {
        return al;
    }

    public void setAl(String al) {
        this.al = al;
    }

    public ArrayList<LineLyric> getLineLyrics() {
        //1. 对歌词按时间排序

        Collections.sort(this.lineLyrics, new Comparator<LineLyric>() {
            @Override
            public int compare(LineLyric o1, LineLyric o2) { //将list从小到大排序
                if(o1.getTimePoint()>o2.getTimePoint()) {
                    return 1;
                }else if(o1.getTimePoint()<o2.getTimePoint()) {
                    return -1;
                }else {
                    return 0;
                }
            }
        });

        //2. 设置每行歌词的显示时间
        for (int i=0 ;i<lineLyrics.size()-1;i++){
            long delta = lineLyrics.get(i+1).getTimePoint()-lineLyrics.get(i).getTimePoint();
            lineLyrics.get(i).setShowTtime(delta);
        }
        //最后一行设置显示时间为1s
       // lineLyrics.get(lineLyrics.size()-1).setShowTtime(1000);
        return lineLyrics;
    }

    public void setLineLyrics(ArrayList<LineLyric> lineLyrics) {

        this.lineLyrics = lineLyrics;
    }

    public static class LineLyric {

        public long getTimePoint() {
            return timePoint;
        }

        public void setTimePoint(long timePoint) {
            this.timePoint = timePoint;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }


        @Override
        public String toString() {
            return "LineLyric{" +
                    "timePoint=" + timePoint +
                    ", content='" + content + '\'' +
                    ", showTtime=" + showTtime +
                    '}';
        }

        long timePoint; //每行歌词对应的时间
        String content;//每行歌词对应的内容
        long showTtime = 1000;

        public long getShowTtime() {
            return showTtime;
        }

        public void setShowTtime(long showTtime) {
            this.showTtime = showTtime;
        }


    }

    @Override
    public String toString() {
        return "Lyric{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", by='" + by + '\'' +
                ", al='" + al + '\'' +
                ", lineLyrics=" + lineLyrics +
                '}';
    }
}
