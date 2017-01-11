package com.kwsoft.kehuhua.bailiChat;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.TestActivity;
import com.kwsoft.kehuhua.utils.MyPreferenceManager;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.version.StuPra;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush MyReceiver";
	private NotificationManager nm;
	@Override
	public void onReceive(Context context, Intent intent) {

		if (null == nm) {
			nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		}
        Bundle bundle = intent.getExtras();
		Log.e(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		String extras=bundle.getString(JPushInterface.EXTRA_EXTRA);
		Log.e(TAG, "onReceive: extras "+extras);
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.e(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.e(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 用户点击打开了通知");
            
        	//打开自定义的Activity，聊天界面
        	Intent i = new Intent(context, TestActivity.class);
        	i.putExtras(bundle);
        	//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        	context.startActivity(i);
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.e(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.e(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.e(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//发送消息至 ChatMainActivity
	private void processCustomMessage(Context context, Bundle bundle) {

		Log.e(TAG, "processCustomMessage: EXTRA_TITLE "+bundle.getString(JPushInterface.EXTRA_TITLE));
		Log.e(TAG, "processCustomMessage: EXTRA_TITLE "+bundle.getString(JPushInterface.EXTRA_TITLE));
		Log.e(TAG, "processCustomMessage: EXTRA_MESSAGE "+bundle.getString(JPushInterface.EXTRA_MESSAGE));
		Log.e(TAG, "processCustomMessage: EXTRA_EXTRA "+bundle.getString(JPushInterface.EXTRA_EXTRA));
		Log.e(TAG, "processCustomMessage: StuMainActivity.isForeground "+ ChatGoActivity.isForeground);
		Log.e(TAG, "processCustomMessage: msgIntent "+bundle.getString(JPushInterface.EXTRA_MESSAGE));

		String pra = bundle.getString(JPushInterface.EXTRA_EXTRA);//所有参数在此
		Map<String, Object> praMap = JSON.parseObject(pra,
				new TypeReference<Map<String, Object>>() {
				});
		//具体发送的联系人名称放在标题
		String sender=String.valueOf(praMap.get("sender"));
		String senderId=String.valueOf(praMap.get("senderId"));
		//会话名称--即群组名称放在标题
		String channel = String.valueOf(praMap.get("channelName"));

		String channelId = String.valueOf(praMap.get("channelId"));
		Log.e(TAG, "processCustomMessage: title "+sender);
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		if (message.length()>0) {
			for (int i = 0; i < message.length(); i++) {
				if (message.charAt(i) != ' ') {
					message = message.substring(i, message.length());
					break;
				}
			}
		}
	    //判断有无发送人
		if (StringUtils.isEmpty(senderId)) {
			Log.e(TAG, "无发送人，不提示");
			return;
		}
		//判断是不是自己发的消息，如果是则提醒
		boolean needIncreaseUnread = true;
		Log.e(TAG, "processCustomMessage: Config.myName "+ Config.myName);
		if (senderId.equalsIgnoreCase(Config.myName)) {
			Log.e(TAG, "自己发的消息，不提醒");
			needIncreaseUnread = false;
			if (!Config.IS_TEST_MODE) {
				return;
			}
		}
//		private String name;//聊天的对象，也可以理解为会话对象名称
//		private String date;//消息发送时间
//		private String text;//接到的内容，包括文字、语音、图片
//		private String time;//语音消息的时长
//		private boolean isComMeg = true;//是否是自己发送的消息
		//通过判断是否在app打开状态，决定是否进入当前页面
		Log.e(TAG, "processCustomMessage: "+ Utils.isBackground(context));
		if (!Utils.isBackground(context)) {
			Intent msgIntent = new Intent(ChatGoActivity.MESSAGE_RECEIVED_ACTION);

			msgIntent.putExtra(StuPra.KEY_MESSAGE, message);
			msgIntent.putExtra("extras", pra);
			Log.e(TAG, "processCustomMessage: 已走广播到chatGo页面 "+pra);
			context.sendBroadcast(msgIntent);
		}
//将发送者赋值给聊天界面，如果会话名称不为空，则将会话名称赋值给正在聊聊天的对象名称
		String chatting = senderId;
		if (!StringUtils.isEmpty(channel)) {
			chatting = channel;
		}
//获取正在聊天的频道,记录是否已读
		String currentChatting = MyPreferenceManager.getString(StuPra.PREF_CURRENT_CHATTING, null);
		//如果频道一样
		if (chatting.equalsIgnoreCase(currentChatting)) {
			Log.e(TAG, "正在与" + chatting + "聊天，不显示通知");
			needIncreaseUnread = false;//设置未读消息不需要增加
			if (!Config.IS_TEST_MODE) {
				return;
			}
		}
//如果需要增加未读消息，则
		if (needIncreaseUnread) {
			unreadMessage(senderId, channel);
		}
//如果以上都没有return则弹出通知
		NotificationHelper.showMessageNotification(context, nm, sender, message, channel,channelId);

	}

	// 接到消息时,为最近的聊天增加未读数
	private void unreadMessage(final String friend, final String channel) {
		new Thread() {
			public void run() {
//
//				String unReadMess = MyPreferenceManager.getString(Config.myName+"unReadMess","");
//
//				if (!unReadMess.equals("")&&!StringUtils.isEmpty(channel)) {
//
//					Map<String, Object> unReadMessMap = JSON.parseObject(unReadMess,
//							new TypeReference<Map<String, Object>>() {
//							});
//
//					Map<String, Integer> unReadMessMa= (Map<String, Integer>) unReadMessMap.get("channelIds");
//					if (unReadMessMa.get("channel")!=null) {
//
//
//
//
//					}
//
//				}





				String chattingFriend = null;
				if (StringUtils.isEmpty(channel)) {
					chattingFriend = friend;
				}

				Map<String, Object> params = new HashMap<>();
				params.put("udid", Config.udid);
				params.put("friend", chattingFriend);
				params.put("channelIds", channel);

				try {
//					HttpHelper.post(StuPra.PATH_UNREAD, params);
					MyPreferenceManager.commitString(Config.myName+"unReadMess", JSON.toJSONString(params));


				} catch (Exception e) {
					Log.e(TAG, "在此调用pushTalk的api汇报未读错误", e);
				}
			}
		}.start();
	}
}
