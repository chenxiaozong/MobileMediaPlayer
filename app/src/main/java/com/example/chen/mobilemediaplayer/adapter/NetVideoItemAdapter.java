package com.example.chen.mobilemediaplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.chen.mobilemediaplayer.R;
import com.example.chen.mobilemediaplayer.domain.MediaItem;
import com.example.chen.mobilemediaplayer.utils.TimeFormatUilts;
import com.squareup.picasso.Picasso;

import org.xutils.x;

import java.util.List;

/**
 * Created by chen on 2017/1/10.
 * 视频列表适配器
 */

public class NetVideoItemAdapter extends BaseAdapter {

    private Context context;
    private List<MediaItem>mediaItems ;
    private TimeFormatUilts timeUtils;

    //构造函数
    public NetVideoItemAdapter(Context context, List<MediaItem> mediaItems){
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
            //convertView = View.inflate(context, R.layout.video_list_item,null);
            convertView = View.inflate(context, R.layout.net_video_list_item,null);
            holder = new ViewHolder();
            holder.im_video_item = (ImageView) convertView.findViewById(R.id.im_video_item);
            holder.tv_movie_title = (TextView) convertView.findViewById(R.id.tv_movie_title);
            holder.tv_movie_name= (TextView) convertView.findViewById(R.id.tv_movie_name);
            holder.tv_movie_summary= (TextView) convertView.findViewById(R.id.tv_movie_sumary);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }


        //为视图对象设置显示内容

        MediaItem item = mediaItems.get(position);
        holder.tv_movie_name.setText(item.getName());
        holder.tv_movie_summary.setText(item.getDescSummary());
        holder.tv_movie_title.setText(item.getMovieTitle());


        /**
         * 方法一:  使用xutils3 设置图片
         * 使用xutils 设置图片
        String url = item.getImageUrl();
        //Log.d("NetVideoItemAdapter", url);
        x.image().bind(holder.im_video_item,url);
         */

        /**
         * 方法二: 使用Pisaco 设置图片
        Picasso.with(context)
                .load(item.getImageUrl())
                .into(holder.im_video_item);
         */

        /**
         * 使用Glide
         */
        Glide.with(context)
                .load(item.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.im_video_item);

        return convertView;
    }


    static  class  ViewHolder{
        ImageView im_video_item;
        TextView tv_movie_title;
        TextView tv_movie_name;
        TextView tv_movie_summary;

    }

}




