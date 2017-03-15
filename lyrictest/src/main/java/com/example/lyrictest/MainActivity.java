package com.example.lyrictest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static com.example.lyrictest.R.id.tv_show_lyric;

public class MainActivity extends Activity implements View.OnClickListener {

    private TextView tv_lyric;
    private Button bt_show_lyric;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_lyric = (TextView)findViewById(R.id.tv_lyric);
        bt_show_lyric = (Button)findViewById(tv_show_lyric);
        bt_show_lyric.setOnClickListener(this);


    }

    private void getDataFromFile() {
        String filePahth = String.valueOf(Environment.getExternalStorageDirectory()+"/beijing.txt");

        File file = new File(filePahth);
        if(file.exists()) {
            Log.d("MainActivity_11", "文件存在");


        }else {
            Log.d("MainActivity_11", "文件不存在");
        }
    }


    @Override
    public void onClick(View v) {
        Toast.makeText(this, "点击", Toast.LENGTH_SHORT).show();

        getDataFromFile();

        String filePahth = String.valueOf(Environment.getExternalStorageDirectory()+"/beijing.txt");
        File lrcfile = new File(filePahth);

        LrcUtils lrcUtils = new LrcUtils();
        lrcUtils.readLyricFile(lrcfile);

        Log.d("MainActivity", lrcUtils.lyric.getTitle());
        Log.d("MainActivity", lrcUtils.lyric.getArtist());
        Log.d("MainActivity", lrcUtils.lyric.getAl());
        Log.d("MainActivity", lrcUtils.lyric.getBy());


        for (int i=0;i<lrcUtils.lyric.getLineLyrics().size();i++){
            Log.d("MainActivity", "lrcUtils.lyric.getLineLyrics().get(i):" + lrcUtils.lyric.getLineLyrics().get(i));
        }



    }
}
