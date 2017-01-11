package com.kwsoft.kehuhua.bailiChat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kwsoft.kehuhua.adcustom.R;

import java.util.zip.Adler32;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class NotificationHelper {

    @SuppressWarnings("deprecation")
    public static void showMessageNotification(
            Context context, NotificationManager nm,
            String title, String msgContent, String channel,String channelId) {

        boolean isChannel = !StringUtils.isEmpty(channelId);//是否有channel名称
        String chatting;
        if (isChannel) {

            chatting = " (" + channel + ")";
            Log.e(TAG, "showMessageNotification: chattingOK "+chatting);
        }
        else {
            chatting = "";
            Log.e(TAG, "showMessageNotification: chattingNO "+chatting);
        }
        Log.e(TAG, "showMessageNotification: channelName"+channel);
        Intent select = new Intent();
        select.setClass(context, ChatGoActivity.class);
        select.putExtra("dataId", channelId);//会话对象id
        select.putExtra("channelName", channel);//所在会话窗口的名称
//        select.putExtra("sender", channel);//所在会话窗口的名称
//        select.putExtra(StuPra.KEY_CHATTING, chatting);
//        select.putExtra(StuPra.KEY_IS_CHANNEL, isChannel);
        select.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
//        Notification notification = new Notification(R.drawable.ic_launcher, msgContent, System.currentTimeMillis());
        if (isChannel) {
            title = title + chatting;
        }
        
        int notificationId = getNofiticationID(title);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, select, PendingIntent.FLAG_UPDATE_CURRENT);
//        notification.setLatestEventInfo(context, title, msgContent, contentIntent);
        Notification notification = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(msgContent)
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.icon)
                .setWhen(System.currentTimeMillis())
                .build();
        showMessageNotificationLocal(nm, notification, notificationId);
    }

    private static void showMessageNotificationLocal(NotificationManager nm, Notification notification, int notificationId) {
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        boolean needSound = Config.NOTIFICATION_NEED_SOUND;
        boolean needVibrate = Config.NOTIFICATION_NEED_VIBRATE;
        
    	if (needSound && needVibrate) {
        	notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        } else if (needSound){
        	notification.defaults = Notification.DEFAULT_SOUND;
        } else if (needVibrate) {
        	notification.defaults = Notification.DEFAULT_VIBRATE;
        }
    	
        nm.notify(notificationId, notification);
    }
    
    public static int getNofiticationID(String friend) {
        if (TextUtils.isEmpty(friend)) {
            return 0;
        }
        
        int nId = 0;
        Adler32 adler32 = new Adler32();
        adler32.update(friend.getBytes());
        nId = (int) adler32.getValue();
        if (nId < 0) {
            nId = Math.abs(nId);
        }
        
        if (nId < 0) {
            nId = Math.abs(nId);
        }
        return nId;
    }


}
