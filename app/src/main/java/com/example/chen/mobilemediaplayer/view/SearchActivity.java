package com.example.chen.mobilemediaplayer.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.mobilemediaplayer.R;

/**
 * Created by chen on 2017/3/15.
 * 搜索页面:activity
 */

public  class SearchActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcher);
        findViews();


        TitlebarOnClickListener titlebarOnClickListener = new TitlebarOnClickListener();

        //2. 设置点击事件
        tvTitleSearch.setOnClickListener(titlebarOnClickListener);
        ivTitleVoiceSearch.setOnClickListener(titlebarOnClickListener);

    }

class TitlebarOnClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {

        if(v==tvTitleSearch) {
            Toast.makeText(SearchActivity.this, "文本搜索", Toast.LENGTH_SHORT).show();

        }else if(v==ivTitleVoiceSearch) {
            Toast.makeText(SearchActivity.this, "语音搜索", Toast.LENGTH_SHORT).show();

        }

/*        switch (v.getId()){
            case R.id.tv_title_search:
                break;
            case R.id.iv_title_voice_search:
                break;

        }*/

    }
}
    /**
     * 实例化视图
     */

    private TextView tvTitleSearch;
    private ImageView ivTitleVoiceSearch;
    private ListView lvSearchResultList;
    private ProgressBar pbSearchProgress;
    private TextView tvSearchNoContent;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-03-15 20:52:11 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        tvTitleSearch = (TextView)findViewById( R.id.tv_title_search );
        ivTitleVoiceSearch = (ImageView)findViewById( R.id.iv_title_voice_search );
        lvSearchResultList = (ListView)findViewById( R.id.lv_search_result_list );
        pbSearchProgress = (ProgressBar)findViewById( R.id.pb_search_progress );
        tvSearchNoContent = (TextView)findViewById( R.id.tv_search_no_content );
    }

}
