package com.kwsoft.kehuhua.bailiChat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by Administrator on 2016/12/7 0007.
 */

public class SelectPsnActivity extends Activity {
    private static final String TAG = "SelectPsnActivity";
    private ListView listview;
    private List<Map<String, Object>> lists = new ArrayList<>();
    SelPsnAdapter adapter = null;
    String returnMapstr, returnMapstrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_select_psn);


        CommonToolbar mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);

        mToolbar.setTitle("会话列表");
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnData();
            }
        });


        listview = (ListView) findViewById(R.id.lv_listview);

        requestData();

    }

    public void requestData() {
        String volleyUrl = Constant.sysUrl + Constant.requestListSet;
        Log.e("TAG", "学员端登陆地址 " + Constant.sysUrl + Constant.requestListSet);
        //参数
        Map<String, String> map = new HashMap<>();
        map.put(Constant.tableId, "448");
        map.put(Constant.pageId, "7688");

        Log.e(TAG, "maplogi/" + map.toString());
        //请求
        OkHttpUtils
                .post()
                .params(map)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(SelectPsnActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                        //        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: " + "  id  " + response);
                        check(response);
                    }
                });
    }

    public void check(String menuData) {
        Map<String, Object> menuMap = JSON.parseObject(menuData,
                new TypeReference<Map<String, Object>>() {
                });
        List<Map<String, Object>> menuListMap2 = null;
        if (menuMap.containsKey("dataList")) {
            menuListMap2 = (List<Map<String, Object>>) menuMap.get("dataList");
            Log.e("menuListMap2", JSON.toJSONString(menuListMap2));
        }

        if (menuListMap2 != null && menuListMap2.size() > 0) {
            for (int i = 0; i < menuListMap2.size(); i++) {
                Map<String, Object> map = menuListMap2.get(i);
                map.put("isCheck", false);
//                Map<String, Object> gmap = new HashMap<>();
//                gmap.put("title", map.get("AFM_6") + "");
                lists.add(map);
            }
        }

        adapter = new SelPsnAdapter(this, lists, "true");
        listview.setAdapter(adapter);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            returnData();
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    private void returnData() {
        List<String> returnMap = adapter.reList;
        Log.e(TAG, "onKeyDown: " + returnMap.toString());
        for (int i = 0; i < returnMap.size(); i++) {
            String[] name = returnMap.get(i).split("/");
            if (name.length == 2) {
                if (i == 0) {
                    returnMapstr = name[0];
                    returnMapstrid = name[1];
                } else {
                    returnMapstr = returnMapstr + "," + name[0];
                    returnMapstrid = returnMapstrid + "," + name[1];
                }
            }
        }
        Intent intent = new Intent();
        intent.putExtra("repsn", returnMapstr);
        intent.putExtra("repsnid", returnMapstrid);

        //通过Intent对象返回结果，调用setResult方法
        Log.e(TAG, "onKeyDown:2 " + returnMapstr + "/" + returnMapstrid);
        setResult(1, intent);
        finish();
    }
}
