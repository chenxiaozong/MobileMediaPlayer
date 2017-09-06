package com.example.chen.mobilemediaplayer.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chen.mobilemediaplayer.R;
import com.example.chen.mobilemediaplayer.domain.MediaItem;
import com.example.chen.mobilemediaplayer.utils.TimeFormatUilts;

import java.io.File;
import java.util.List;

/**
 * Created by chen on 2017/1/10.
 * 视频列表适配器
 */

public class VideoItemAdapter extends BaseAdapter {

    private Context context;
    private List<MediaItem>mediaItems ;
    private TimeFormatUilts timeUtils;

    //构造函数
    public  VideoItemAdapter( Context context,List<MediaItem> mediaItems){
        this.context = context;
        this.mediaItems = mediaItems;
        timeUtils = new TimeFormatUilts();
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder ;
        if(convertView==null) {
            convertView = View.inflate(context, R.layout.video_list_item,null);
            holder = new ViewHolder();
            holder.im_video_item = (ImageView) convertView.findViewById(R.id.im_video_item);
            holder.tv_video_item_name = (TextView) convertView.findViewById(R.id.tv_video_item_name);
            holder.tv_video_item_time= (TextView) convertView.findViewById(R.id.tv_video_item_time);
            holder.tv_video_item_size= (TextView) convertView.findViewById(R.id.tv_video_item_size);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }


        //为视图对象设置显示内容

        MediaItem item = mediaItems.get(position);
        holder.tv_video_item_name.setText(item.getName());
        //文件大小 ---转换-->字符串
        String filesize = Formatter.formatFileSize(context,item.getSize());
        holder.tv_video_item_size.setText(filesize);


        //使用glide 加载视频快照
        File moveFile = new File(item.getData());
        Uri moveUri = Uri.fromFile(moveFile);
        Glide.with(context).load(moveUri).into(holder.im_video_item);




        //时间转换
        String duration = timeUtils.stringForTime((int) item.getDuration());
        holder.tv_video_item_time.setText("时长:"+duration);

        return convertView;
    }


    static  class  ViewHolder{
        ImageView im_video_item;
        TextView tv_video_item_name;
        TextView tv_video_item_time;
        TextView tv_video_item_size;

    }

}




