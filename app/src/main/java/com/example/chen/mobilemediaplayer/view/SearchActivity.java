package com.example.chen.mobilemediaplayer.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.mobilemediaplayer.R;
import com.example.chen.mobilemediaplayer.utils.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by chen on 2017/3/15.
 * 搜索页面:activity
 */

public class SearchActivity extends Activity {

    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcher);
        findViews();


        TitlebarOnClickListener titlebarOnClickListener = new TitlebarOnClickListener();

        //2. 设置点击事件
        tvTitleSearchAction.setOnClickListener(titlebarOnClickListener);
        ivTitleVoiceSearch.setOnClickListener(titlebarOnClickListener);

    }

    class TitlebarOnClickListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {

            if (v == tvTitleSearchAction) {
                searcheFromBaidu();
                //关闭输入法键盘
                closeKeyBoard();


            } else if (v == ivTitleVoiceSearch) {

                startVoiceSearch();

//                showDialog();
            }


        }
    }

    /**
     * 使用讯飞语音搜索
     */
    private void startVoiceSearch() {

        //1.创建 RecognizerDialog 对象
        InitListener mInitListener = new InitListener() {
            @Override
            public void onInit(int i) {
                if (i != ErrorCode.SUCCESS) {
                    Toast.makeText(SearchActivity.this, "启动语音识别失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
        RecognizerDialog mDialog = new RecognizerDialog(this, mInitListener);

        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");//中文
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");//普通话


        //3.设置回调接口
        RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                String result = recognizerResult.getResultString();
                Log.e("MainActivity", "result ==" + result);
                String text = JsonParser.parseIatResult(result);
                //解析好的
                Log.e("MainActivity", "text ==" + text);

                String sn = null;
                // 读取json结果中的sn字段
                try {
                    JSONObject resultJson = new JSONObject(recognizerResult.getResultString());
                    sn = resultJson.optString("sn");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mIatResults.put(sn, text);

                StringBuffer resultBuffer = new StringBuffer();//拼成一句
                for (String key : mIatResults.keySet()) {
                    resultBuffer.append(mIatResults.get(key));
                }

                etTitleSearchKey.setText(resultBuffer.toString());
                etTitleSearchKey.setSelection(etTitleSearchKey.length());

            }

            @Override
            public void onError(SpeechError speechError) {
                Toast.makeText(SearchActivity.this, "语音识别失败", Toast.LENGTH_SHORT).show();
            }
        };
        mDialog.setListener(mRecognizerDialogListener);

        //4.显示 dialog，接收语音输入
        mDialog.show();

    }

    /**
     * 关闭键盘
     */

    private void closeKeyBoard() {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (im.isActive()) {
                im.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);

            }
    }

    /**
     * 从百度搜索
     */
    private void searcheFromBaidu() {


        web_view_search = findViewById(R.id.web_view_search);

        WebSettings settings = web_view_search.getSettings();
        settings.setJavaScriptEnabled(true);
        web_view_search.setWebViewClient(new WebViewClient());//打开连接时在使用webview而不是系统浏览器

        String base = "https://www.baidu.com/s?wd=";


        String key = etTitleSearchKey.getText().toString().trim();

        if (TextUtils.isEmpty(key)) {
            Toast.makeText(this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
            return;
        }

        web_view_search.loadUrl(base + key);


    }

    /**
     * 实例化视图
     */

    private TextView tvTitleSearchAction;
    private EditText etTitleSearchKey;
    private ImageView ivTitleVoiceSearch;
    private ListView lvSearchResultList;
    private ProgressBar pbSearchProgress;
    private TextView tvSearchNoContent;
    private WebView web_view_search;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-03-15 20:52:11 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        tvTitleSearchAction = (TextView) findViewById(R.id.tv_title_search_action);
        etTitleSearchKey = (EditText) findViewById(R.id.tv_title_search_key);


        ivTitleVoiceSearch = (ImageView) findViewById(R.id.iv_title_voice_search);


        lvSearchResultList = (ListView) findViewById(R.id.lv_search_result_list);
        pbSearchProgress = (ProgressBar) findViewById(R.id.pb_search_progress);
        tvSearchNoContent = (TextView) findViewById(R.id.tv_search_no_content);


    }

    private void showDialog() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        /**
         * @param recognizerResult
         * @param b                是否说话结束
         */
        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            String result = recognizerResult.getResultString();
            Log.e("MainActivity", "result ==" + result);
            String text = JsonParser.parseIatResult(result);
            //解析好的
            Log.e("MainActivity", "text ==" + text);

            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(recognizerResult.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);

            StringBuffer resultBuffer = new StringBuffer();//拼成一句
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }

            etTitleSearchKey.setText(resultBuffer.toString());
            etTitleSearchKey.setSelection(etTitleSearchKey.length());

        }

        /**
         * 出错了
         *
         * @param speechError
         */
        @Override
        public void onError(SpeechError speechError) {
            Log.e("MainActivity", "onError ==" + speechError.getMessage());

        }
    }


    class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(SearchActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
