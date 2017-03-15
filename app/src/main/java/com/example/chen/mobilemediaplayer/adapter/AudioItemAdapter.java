package com.example.chen.mobilemediaplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.mobilemediaplayer.R;
import com.example.chen.mobilemediaplayer.domain.MediaItem;
import com.example.chen.mobilemediaplayer.utils.TimeFormatUilts;

import java.util.List;

/**
 * Created by chen on 2017/1/10.
 * 视频列表适配器
 */

public class AudioItemAdapter extends BaseAdapter {

    private Context context;
    private List<MediaItem>mediaItems ;
    private TimeFormatUilts timeUtils;

    //构造函数
    public AudioItemAdapter(Context context, List<MediaItem> mediaItems){
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
            convertView = View.inflate(context, R.layout.audio_list_item,null);
            holder = new ViewHolder();
            holder.im_audio_item = (ImageView) convertView.findViewById(R.id.im_audio_item);
            holder.tv_audio_item_name= (TextView) convertView.findViewById(R.id.tv_audio_item_name);
            holder.tv_audio_item_author = (TextView) convertView.findViewById(R.id.tv_audio_item_author);
            holder.tv_audio_item_time= (TextView) convertView.findViewById(R.id.tv_audio_item_time);
            holder.tv_audio_item_size= (TextView) convertView.findViewById(R.id.tv_audio_item_size);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }


        MediaItem item = mediaItems.get(position);
        //设置音乐名称
        holder.tv_audio_item_name.setText(item.getName());
        //设置音乐大小            文件大小 ---转换-->字符串
        String filesize = Formatter.formatFileSize(context,item.getSize());
        holder.tv_audio_item_size.setText(filesize);

        //3. 设置音乐演唱者
        holder.tv_audio_item_author.setText(item.getArtist());

        //4. 设置音乐时长              时间转换
        String duration = timeUtils.stringForTime((int) item.getDuration());
        holder.tv_audio_item_time.setText("时长:"+duration);

        return convertView;
    }


    static  class  ViewHolder{
        ImageView im_audio_item;
        TextView tv_audio_item_name;
        TextView tv_audio_item_author;
        TextView tv_audio_item_time;
        TextView tv_audio_item_size;


    }

}




