package com.example.chen.mobilemediaplayer.base.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.mobilemediaplayer.R;
import com.example.chen.mobilemediaplayer.activity.SystemMediaPlayerActivity;
import com.example.chen.mobilemediaplayer.adapter.NetVideoItemAdapter;
import com.example.chen.mobilemediaplayer.base.BaseFragment;
import com.example.chen.mobilemediaplayer.domain.MediaItem;
import com.example.chen.mobilemediaplayer.utils.CacheUtils;
import com.example.chen.mobilemediaplayer.utils.Constant;
import com.example.chen.mobilemediaplayer.utils.Utils;
import com.example.chen.mobilemediaplayer.view.xListview.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.provider.MediaStore;


/**
 * Created by chen on 2017/1/7.
 * 网络视频页面:
 */

public class NetVidePager extends BaseFragment {
    //private TextView textView;

    private NetVideoItemAdapter adapter ;

    private  ArrayList<MediaItem> mediaItems;

    //使用xlistView

    @ViewInject(R.id.lv_native_video_list)
     private XListView mListView;

    //使用xutils3注解方式初始化
    //@ViewInject(R.id.lv_native_video_list)
     //private ListView mListView;

    @ViewInject(R.id.tv_native_video_nomedia)
    private TextView tvNoMedia;

    @ViewInject(R.id.pb_native_video)
    private ProgressBar pbOnLoading;


    public NetVidePager(Context context) {
        super(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  initView();
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected View initView() {
        View view = View.inflate(context, R.layout.net_video_pager,null);
        //使用xutils3 注解初始化试图对象
        x.view().inject(this,view);     //this --NetVidePager.this
        setAdapterData(mediaItems);
        mListView.setOnItemClickListener(new NetListViewItemClickListener());

        // 设置下拉刷新和上拉加载更多
        mListView.setPullLoadEnable(true);
        //mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(new MyXListViewListener());

        return view;
    }

    /**
     * 初始化操作:
     * 1. 读取缓存数据
     * 2. 联网请求
     */
    @Override
    public void initData() {
        super.initData();
        onLoad();//隐藏上啦下拉提示框

        //从缓存中读取数据
        String jsonCache = CacheUtils.getTextData(context,Constant.VIDEO_URL);

        if(!TextUtils.isEmpty(jsonCache)){//先读取缓存数据
            resolerJsonData(jsonCache);
        }

        getDataByXutils();//使用xutils 从网络中获取数据 ---得到itemlist

    }

    private void getDataByXutils() {
        RequestParams param = new RequestParams(Constant.VIDEO_URL);
        x.http().get(param, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                //将数据缓存到本地
                CacheUtils.saveTextData(context,Constant.VIDEO_URL,result);
                resolerJsonData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d("NetVidePager", "onError" + ex.getMessage());

                /**联网失败:
                 * 1.有缓存数据,显示缓存数据 .提示联网失败,显示缓存数据
                 * 2. 没有缓存数据,提示没有网络
                 */
                if(mediaItems!=null&&mediaItems.size()>0) {//有缓存数据
                    Toast.makeText(context, "联网失败...", Toast.LENGTH_SHORT).show();
                }else {
                tvNoMedia.setText("联网失败...");
                tvNoMedia.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d("NetVidePager", "onCancelled" + cex.getMessage());

            }

            @Override
            public void onFinished() {
                Log.d("NetVidePager", "onFinished");

            }
        });
    }

    /**
     * 解析数据:
     * @param result
     */
    private void resolerJsonData(String result) {
        mediaItems = resolverJsonWithSysAPI(result); //解析json 数据 得到mediaItemList
        setAdapterData(mediaItems);
    }

    /**
     * 将获取的数据设置到MediaItem 中
     */
    private void setAdapterData(ArrayList<MediaItem> mediaItems) {
        //3. 设置adapter
        if(mediaItems!=null && mediaItems.size()>0) {
            adapter = new NetVideoItemAdapter(context,mediaItems);
            tvNoMedia.setVisibility(View.GONE);
        }else {
            tvNoMedia.setVisibility(View.VISIBLE);
        }
        pbOnLoading.setVisibility(View.GONE);

        mListView.setAdapter(adapter);

    }

    /**
     * 解析服务器返回的 json数据
     * 1. 使用系统解析工具
     * 2. 使用第三方框架解析
     * 方式一: 使用系统接口解析json 数据
     * @param json
     */
    private ArrayList<MediaItem> resolverJsonWithSysAPI(String json) {

        ArrayList<MediaItem> mediaItemList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");

            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = (JSONObject) jsonArray.get(i);
                    if (item != null) {
                        MediaItem mediaItem = new MediaItem();
                        String movieName = item.optString("movieName");
                        String movieTitle = item.optString("videoTitle");
                        String movieSummary = item.optString("summary");
                        String url = item.optString("url");
                        String hightUrl = item.optString("hightUrl");
                        String imageUrl = item.optString("coverImg");

                        mediaItem.setName(movieName);
                        mediaItem.setMovieTitle(movieTitle);
                        mediaItem.setData(url);
                        mediaItem.setHightData(hightUrl);
                        mediaItem.setDescSummary(movieSummary);
                        mediaItem.setImageUrl(imageUrl);
                        //将数据添加到集合中
                        mediaItemList.add(mediaItem);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItemList;
    }

    /**
     * 网络视频列表点击事件 监听
     */
    private class NetListViewItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            boolean isNetAvailable = new Utils().isNetAvailable(context);
            if(!isNetAvailable) {
                Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
            }else {



            Intent intent = new Intent(context,SystemMediaPlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist",mediaItems);//添加序列化 对象
            intent.putExtras(bundle);
            intent.putExtra("position",position-1);//传入点击item 的下标
            startActivity(intent);
            }

        }
    }

    /**
     * xlistView 下拉刷新监听
     * 1. 下拉刷新 --更新mediaItemList
     *
     */
    private class MyXListViewListener implements XListView.IXListViewListener {
        @Override
        public void onRefresh() {
            getDataByXutils(); //刷新数据mediaItems
            onLoad();//停止刷新箭头和刷新提示符
        }

        @Override
        public void onLoadMore() {
            //ArrayList<MediaItem> moreItems = new ArrayList<>();
            getMoreDataByXutils(); // 其中--使用xutils 获取网络数据是在子线程内执行 注意主线程与子线程的时间差
            onLoad();
        }
    }

    /**
     * 从网络中获取更多数据:
     * @return
     */
    private ArrayList<MediaItem> moreItems = new ArrayList<>();

    private void getMoreDataByXutils() {
        moreItems.clear(); //每次加载更多数据前,先清空
        RequestParams param = new RequestParams(Constant.VIDEO_URL);
        x.http().get(param, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                moreItems = resolverJsonWithSysAPI(result);
                mediaItems.addAll(moreItems);

                adapter.notifyDataSetChanged();//更新adapter
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(context, "联网失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 停止显示: * 刷新提示,刷新加载Progressbar和刷新箭头
     */
    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();

        String sysTime = new Utils().getSytemTime();//获取系统时间
        mListView.setRefreshTime(sysTime);
    }

}
