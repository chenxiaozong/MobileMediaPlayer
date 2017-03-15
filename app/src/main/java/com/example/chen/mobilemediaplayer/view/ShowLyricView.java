package com.example.chen.mobilemediaplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.example.chen.mobilemediaplayer.domain.Lyric;
import com.example.chen.mobilemediaplayer.utils.DensityUtils;
import com.example.chen.mobilemediaplayer.utils.LrcUtils;

import org.xutils.common.util.DensityUtil;

import java.io.File;


/**
 * Created by chen on 2017/3/14.
 * 歌词显示
 */

public class ShowLyricView extends TextView {

    private Lyric lyric;//歌词
    private String lyricUrl="";


    private Paint paint; //绿色画笔,绘制当前行
    private Paint whitePaint;//白色画笔,绘制非当前行



    private float width;
    private float hight;
    private int index ; //当前行在歌词列表中的索引
    private float textHight ; //一行歌词文本所占高度
    private float showTime; //某一行应该显示的时间
    private float pushTime = 0;   //当前播放时间点

/*    public ShowLyricView(Context context) {
        super(context);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }*/


    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);


    }

    /**
     * 初始化试图
     */
    private void initView(Context context) {
        textHight = DensityUtils.dip2px(context,16);//15dp -->像素

        Log.d("ShowLyricView", "textHight:" + textHight);

        setLyricUrl(lyricUrl);

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(textHight+5);

        paint.setAntiAlias(true);       //设置抗锯齿
        paint.setTextAlign(Paint.Align.CENTER);//居中对齐


        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(textHight);

        whitePaint.setAntiAlias(true);       //设置抗锯齿
        whitePaint.setTextAlign(Paint.Align.CENTER);//居中对齐


    }

    public void setLyricUrl(String lyricUrl) {

        //获取歌曲文件
        File lrcfile = new File(lyricUrl);

        LrcUtils lrcUtils = new LrcUtils();
        lrcUtils.readLyricFile(lrcfile);
        if(lrcUtils.lyric!=null) {
            lyric = lrcUtils.lyric;
        }else {
            lyric = null;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w; //获取textview 的宽度
        hight = h; //获取textview 的高度

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //当歌词不为空时, 绘制歌词
        if (lyric != null) {
            //根据歌词显示时间,平移画布,实现歌词平滑移动
            /**
             * 原理:
             * 1.  当前句已经显示时间 /显示时间 =  平移高度 / 行的高度
             *  --> pushTime /showTime = push/textHight
             *  --> pushTime == (current - lyric.getLineLyrics().get(index).getTimePoint());
             */

            float pushHight;
            if(showTime==0) {
                pushHight = 0;
            }else {
                pushHight= (pushTime/showTime)*textHight+textHight ;
            }
            canvas.translate(0,-pushHight);

            //1. 绘制当前行歌词
            String lineContent = lyric.getLineLyrics().get(index).getContent();//index 对应行的歌词内容
            canvas.drawText(lineContent, width / 2, hight / 2, paint);


            //前歌词
            float tempY = hight / 2;
            for (int i = index - 1; i > 0; i--) {
                String preContent = lyric.getLineLyrics().get(i).getContent();
                tempY = tempY - textHight-textHight/2; //textHight/4 为设置的行间距
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitePaint);
            }

            //2. 绘制之后行
            tempY = hight / 2;
            for (int i = index + 1; i < lyric.getLineLyrics().size(); i++) {
                String preContent = lyric.getLineLyrics().get(i).getContent();
                tempY = tempY + textHight+textHight/2;
                if (tempY > hight) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitePaint);
            }

        } else {
            canvas.drawText("没有歌词", width / 2, hight / 2, paint);
        }


    }

    /**
     * 根据播放进度 :确定应该显示高亮的行
     *
     * @param current
     */
    public void setTimePoint(int current) {

        if (lyric != null && lyric.getLineLyrics().size() > 0) {//歌词有文件

            for (int i = 1; i < lyric.getLineLyrics().size(); i++) {

                int pre = (int) lyric.getLineLyrics().get(i-1).getTimePoint();
                int p = (int) lyric.getLineLyrics().get(i).getTimePoint();

                if(current>=pre&&current<p) {
                    index = i-1;
                    showTime = (int) lyric.getLineLyrics().get(index).getShowTtime();
                    pushTime = (int) (current - lyric.getLineLyrics().get(index).getTimePoint());
                    break;
                }
            }
        }
        //重新绘制歌词
        invalidate();// 主线程执行
    }

}
