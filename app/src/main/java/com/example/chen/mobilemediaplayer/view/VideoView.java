package com.example.chen.mobilemediaplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by chen on 2017/1/12.
 * 自定义VideoView :替换系统videoview
 */

public class VideoView extends android.widget.VideoView {
    //代码中创建
    public VideoView(Context context) {
        this(context,null);
    }

    /**
     * 布局文件中创建
     * @param context
     * @param attrs
     */
    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    /**
     * 样式文件中创建
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    //测量视图
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    /**
     *
     * 设置videoview 宽高
     * @param videoWidth
     * @param videoHeight
     */
    public  void  setVideoSize(int videoWidth,int videoHeight){
        //1.获取布局params
        ViewGroup.LayoutParams params = getLayoutParams();
        Log.d("VideoView", "原始:宽:" + params.width + "高:" + params.height);

        params.width = videoWidth;
        params.height = videoHeight;
        Log.d("VideoView", "原始:宽:" + params.width + "高:" + params.height);
        setLayoutParams(params);

    }


}
