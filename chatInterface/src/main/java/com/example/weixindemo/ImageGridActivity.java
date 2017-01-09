package com.example.weixindemo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weixindemo.ImageGridAdapter.TextCallback;

public class ImageGridActivity extends Activity implements OnClickListener{
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapter adapter;
	AlbumHelper helper;
	Button bt;
	TextView tv;
	
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				Toast.makeText(ImageGridActivity.this, "���ѡ��9��ͼƬ", 400).show();
				break;

			default:
				break;
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	   setContentView(R.layout.activity_image_grid);
	   helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		dataList = (List<ImageItem>) getIntent().getSerializableExtra(EXTRA_IMAGE_LIST);
		initView();
		bt = (Button) findViewById(R.id.bt);
		tv = (TextView)findViewById(R.id.quxiao);
		tv.setOnClickListener(this);
		bt.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{
				ArrayList<String> list = new ArrayList<String>();
				Collection<String> c = adapter.map.values();
				Iterator<String> it = c.iterator();
				for (; it.hasNext();)
				{
					list.add(it.next());
				}

				if (Bimp.act_bool)
				{
					Intent intent = new Intent(ImageGridActivity.this,
							ChatMainActivity.class);
					startActivity(intent);
					Bimp.act_bool = false;
				}
				for (int i = 0; i < list.size(); i++)
				{
					if (Bimp.drr.size() < 9)
					{
						Bimp.drr.add(list.get(i));
					}
				}
				finish();
			}

		});
	}
	private void initView(){
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,
				mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback()
		{
			public void onListen(int count)
			{
				bt.setText("���" + "(" + count + ")");
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				adapter.notifyDataSetChanged();
			}
		});
	}
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.quxiao) {
			Intent intent = new Intent(ImageGridActivity.this, TestPicActivity.class);
			startActivity(intent);

		}
		
	}
	
	}


