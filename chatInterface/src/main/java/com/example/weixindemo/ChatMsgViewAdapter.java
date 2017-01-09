package com.example.weixindemo;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatMsgViewAdapter extends BaseAdapter{
	
     public static interface IMsgViewType{
    	 int IMVT_COM_MSG = 0;
 		int IMVT_TO_MSG = 1;
     }
     private static final String TAG  = ChatMsgViewAdapter.class.getSimpleName();
     private List<ChatMsgEntity> coll;
     private LayoutInflater mInflater;
 	 private MediaPlayer mMediaPlayer = new MediaPlayer();
     private Context ctx;
     Resources res;
     
   public ChatMsgViewAdapter(Context context,List<ChatMsgEntity> coll){
	   ctx = context;
	   this.coll = coll;
	   mInflater= LayoutInflater.from(context);
	   res=context.getResources();
   }
     
	@Override
	public int getCount() {
		return coll.size();
	}

	@Override
	public Object getItem(int position) {
		return coll.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}
	@Override
	public int getItemViewType(int position) {
		ChatMsgEntity entity = coll.get(position);
		if(entity.getMsgType()){
			return IMsgViewType.IMVT_COM_MSG;
		}else{
			return IMsgViewType.IMVT_TO_MSG;
		}
	}
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public View getView(final int postion, View converView, ViewGroup parent) {
		final ChatMsgEntity entity = coll.get(postion);
		boolean isComMsg = entity.getMsgType();
		ViewHolder viewHolder = null;
		if(converView==null){
			if(isComMsg){
				converView=mInflater.inflate(R.layout.chat_left, null);
			}else{
				converView=mInflater.inflate(R.layout.chat_right, null);
			}
			viewHolder = new ViewHolder();
			viewHolder.tvSendTime = (TextView)converView.findViewById(R.id.tv_sendtime);
			viewHolder.tvContent = (TextView)converView.findViewById(R.id.tv_chatcontent);
			viewHolder.tvTime = (TextView)converView.findViewById(R.id.tv_time);
			viewHolder.tvUserName = (TextView)converView.findViewById(R.id.tv_username);
		    ViewHolder.isComMsg = isComMsg;
		    converView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)converView.getTag();
		}
		viewHolder.tvSendTime.setText(entity.getDate());
		if(entity.getText().contains(".amr")){
			viewHolder.tvTime.setText(entity.getTime());
			viewHolder.tvContent.setText("");
			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
		}else{
//			viewHolder.tvContent.setText(entity.getText());
//			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
			SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(ctx, entity.getText());
			viewHolder.tvContent.setText(spannableString);
			viewHolder.tvTime.setText(entity.getTime());
		}
		viewHolder.tvContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(entity.getText().contains(".amr")){
					playMusic(android.os.Environment.getExternalStorageDirectory()+"/"+entity.getText()) ;
				}
				else{
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
				   // String id=entity.getText().toString();
					bundle.putInt("ID", postion);
					intent.putExtras(bundle);
					intent.setClass(ctx, PhotoActivity.class);
					ctx.startActivity(intent);
				}
			}
		});
		viewHolder.tvUserName.setText(entity.getName());
		return converView;
	}
 static class ViewHolder{
	public TextView 
	       tvSendTime,
	       tvUserName,
	       tvContent,
	       tvTime;
	public static boolean isComMsg = true;
	 
 }

 /**
	 * @Description
	 * @param name
	 */
	private void playMusic(String name) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void stop() {
	}
}
