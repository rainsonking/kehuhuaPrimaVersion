package com.kwsoft.kehuhua.sessionService;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.kwsoft.kehuhua.config.Constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/29 0029.
 */

public class SessionService extends Service {
    private String volleyUrl;
    private Map<String, String> map;
    public static final String ACTION = "com.kwsoft.kehuhua.sessionService.SessionService";
    private static final String TAG = "SessionService";
    private MyBinder mBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: oncreate");
        initnetPara();
    }

    private void initnetPara() {
        //地址
        volleyUrl = Constant.sysUrl + "login_interfaceLogin.do";
        //参数
        map = new HashMap<>();
        map.put(Constant.USER_NAME, Constant.USERNAME_ALL);
        map.put(Constant.PASSWORD, Constant.PASSWORD_ALL);
        map.put(Constant.proIdName, Constant.proId);
        map.put(Constant.timeName, Constant.menuAlterTime);
        map.put(Constant.sourceName, Constant.sourceInt);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        reGetSession();
        return super.onStartCommand(intent, flags, startId);

    }

    public void reGetSession() {
        //请求
        OkHttpUtils
                .post()
                .params(map)
                .url(volleyUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "请求session失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "请求session成功");

                    }
                });
    }

    @Override
    public void onStart(Intent intent, int startId) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        public void startDownload() {
            Log.d("TAG", "startDownload() executed");
            // 执行具体的下载任务
            reGetSession();
        }
    }
}