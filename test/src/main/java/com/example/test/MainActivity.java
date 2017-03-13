package com.example.test;

import android.app.Activity;
import android.support.annotation.XmlRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import org.xutils.x;

public class MainActivity extends Activity {

    private ImageView iv_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_test = (ImageView)findViewById(R.id.iv_test);

        x.image().bind(iv_test,"http://img5.mtime.cn/mg/2017/03/01/164416.40461950.jpg");
    }
}
