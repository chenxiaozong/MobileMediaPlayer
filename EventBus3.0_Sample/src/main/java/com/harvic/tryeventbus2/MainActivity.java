package com.harvic.tryeventbus2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.harvic.other.FirstEvent;
import com.harvic.other.SecondEvent;
import com.harvic.other.ThirdEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MainActivity extends Activity {

	private Button btn;
	private EventBus eventBus;
	private  TextView tv_event_bus_msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//1.注册
		EventBus.getDefault().register(this);

		//
		btn = (Button) findViewById(R.id.btn_try);
		tv_event_bus_msg = (TextView)findViewById(R.id.tv_event_bus_msg);

		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						SecondActivity.class);
				startActivity(intent);
			}
		});
	}


	@Subscribe(threadMode = ThreadMode.MAIN,sticky = false,priority = 80)
	public void testHello(FirstEvent event) {
		Log.d("harvic_log", "testHello:" + event.getMsg()+",priority=80"+",sticky==false");
		Log.d("harvic_log","Thread-name=="+Thread.currentThread().getName());

		String msg = event.getMsg();
		tv_event_bus_msg.setText(msg);

	}

	@Subscribe(threadMode = ThreadMode.MAIN,sticky = true,priority = 71)
	public void onEventMainThread(FirstEvent event) {
		Log.d("harvic_log", "onEventMainThread:" + event.getMsg()+",priority=71,sticky = true");
		Log.d("harvic_log","Thread-name=="+Thread.currentThread().getName());

		String msg = event.getMsg();
		tv_event_bus_msg.setText(msg);

	}

	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onEventBackgroundThread(SecondEvent event){
		Log.d("harvic_log", "onEventBackground:" + event.getMsg());
		Log.d("harvic_log","Thread-name=="+Thread.currentThread().getName());

		String msg = event.getMsg();
		tv_event_bus_msg.setText(msg);
	}
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onEventAsync(SecondEvent event){
		Log.d("harvic_log", "onEventAsync:" + event.getMsg());
		Log.d("harvic_log","Thread-name=="+Thread.currentThread().getName());

		String msg = event.getMsg();
		tv_event_bus_msg.setText(msg);
	}


	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onEvent(ThirdEvent event) {
		Log.d("harvic_log", "OnEvent:" + event.getMsg());
		Log.d("harvic_log","Thread-name=="+Thread.currentThread().getName());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//取消注册
		EventBus.getDefault().unregister(this);
	}
}
