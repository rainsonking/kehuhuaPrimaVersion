package com.example.weixindemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChatActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	   setContentView(R.layout.activity_main);
	   new Thread(new Runnable() {
			@Override
			public void run() {
				FaceConversionUtil.getInstace().getFileText(getApplication());
			}
		}).start();
	}
	public void btnClick(View v){
		Intent intent=new Intent(ChatActivity.this,ChatMainActivity.class);
		ChatActivity.this.startActivity(intent);
	}
}
