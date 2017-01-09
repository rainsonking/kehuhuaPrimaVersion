package com.example.weixindemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

public class TestPicActivity extends Activity implements OnClickListener{
	List<ImageBucket> dataList;
	GridView gridView;
	ImageBucketAdapter adapter;//
	AlbumHelper helper;
	TextView tv;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static Bitmap bimap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_bucket);

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		initData();
		initView();
	}
	private void initData()
	{
		dataList = helper.getImagesBucketList(false);
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);
	}
	public void initView(){
		gridView = (GridView) findViewById(R.id.gridview);
		tv = (TextView)findViewById(R.id.quxiao);
		tv.setOnClickListener(this);
		adapter = new ImageBucketAdapter(TestPicActivity.this, dataList);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				Intent intent = new Intent(TestPicActivity.this,
						ImageGridActivity.class);
				intent.putExtra(TestPicActivity.EXTRA_IMAGE_LIST,
						(Serializable) dataList.get(position).imageList);
				startActivity(intent);
				finish();
			}

		});
	}
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.quxiao) {
			Intent intent = new Intent(TestPicActivity.this, ChatMainActivity.class);
			startActivity(intent);

		}


	}
}
