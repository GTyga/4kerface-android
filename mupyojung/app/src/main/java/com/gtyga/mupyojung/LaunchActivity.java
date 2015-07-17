package com.gtyga.mupyojung;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class LaunchActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_launch);

		Handler handler = new Handler () {
			@Override
			public void handleMessage(Message msg) {
				finish();
			}
		};
		handler.sendEmptyMessageDelayed(0, 1500);
	}
	
}
