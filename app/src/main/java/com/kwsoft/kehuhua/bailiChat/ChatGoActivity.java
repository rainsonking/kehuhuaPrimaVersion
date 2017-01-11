package com.kwsoft.kehuhua.bailiChat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.weixindemo.CameraActivity;
import com.example.weixindemo.ChatMsgEntity;
import com.example.weixindemo.ChatMsgViewAdapter;
import com.example.weixindemo.FaceRelativeLayout;
import com.example.weixindemo.ScaleImageFromSdcardActivity;
import com.example.weixindemo.SoundMeter;
import com.example.weixindemo.pullDownLoadMore.XCPullToLoadMoreListView;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.MyPreferenceManager;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.version.StuPra;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.utils.ImageCaptureManager;
import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.tableId;


public class ChatGoActivity extends Activity implements OnClickListener {
    private Button mBtnSend, mBtnBack;
    private EditText mEditTextContent;
//    private XCPullToLoadMoreListView mListView;
    private RelativeLayout mBottom;
    private TextView mBtnRcd, chat_title;
    private ChatMsgViewAdapter mAdapter;
    private List<ChatMsgEntity> mDateArrays = new ArrayList<>();

    //语音
    private ImageView chatting_mode_btn, volume;
    private boolean btn_voice = false;
    private LinearLayout del_re;
    private int flag = 1;
    private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
            voice_rcd_hint_tooshort;
    private View rcChat_popup;
    private Handler mHandler = new Handler();
    private boolean isShosrt = false;
    private ImageView img1, sc_img1, btn_photo;
    private String voiceName;
    private long startVoiceT, endVoiceT;
    private SoundMeter mSensor;
    //private ImageButton btn_face;

    public static final int SHOW_ALL_PICTURE = 0x14;//查看图片
    public static final int SHOW_PICTURE_RESULT = 0x15;//查看图片返回
    public static final int CLOSE_INPUT = 0x01;//关闭软键盘
    public static Handler handlerInput;//用于软键盘+
    //private String photoName;
    public ImageView iv_sec, iv_third, iv_four, iv_fifth;

    //调用系统相册-选择图片
    private static final int IMAGE = 90;
    private ArrayList<String> imgPaths = new ArrayList<>();
    private ListView mInnerListView;
    private XCPullToLoadMoreListView mPdListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.weixindemo.R.layout.chat);
        //设置启动不弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				FaceConversionUtil.getInstace().getFileText(getApplication());
//			}
//		}).start();
        registerMessageReceiver();
        initView();
        getSessionPragramData();
    }

    public void initView() {
        //btn_face = (ImageButton)findViewById(R.id.btn_face);
        chat_title = (TextView) findViewById(com.example.weixindemo.R.id.chat_title);
        btn_photo = (ImageView) findViewById(com.example.weixindemo.R.id.btn_photo);
        //btn_photo = (ImageView)findViewById(com.example.weixindemo.R.id.btn_photo);
        mPdListView = (XCPullToLoadMoreListView) findViewById(com.example.weixindemo.R.id.listview);
        mBtnSend = (Button) findViewById(com.example.weixindemo.R.id.btn_send);
        mBtnBack = (Button) findViewById(com.example.weixindemo.R.id.btn_back);
        chatting_mode_btn = (ImageView) findViewById(com.example.weixindemo.R.id.ivPopUp);
        mEditTextContent = (EditText) findViewById(com.example.weixindemo.R.id.et_sendmessage);
        mBtnRcd = (TextView) findViewById(com.example.weixindemo.R.id.btn_rcd);
        mBottom = (RelativeLayout) findViewById(com.example.weixindemo.R.id.btn_bottom);
        del_re = (LinearLayout) this.findViewById(com.example.weixindemo.R.id.del_re);
        volume = (ImageView) this.findViewById(com.example.weixindemo.R.id.volume);
        img1 = (ImageView) this.findViewById(com.example.weixindemo.R.id.img1);
        sc_img1 = (ImageView) this.findViewById(com.example.weixindemo.R.id.sc_img1);
        rcChat_popup = this.findViewById(com.example.weixindemo.R.id.rcChat_popup);
        voice_rcd_hint_rcding = (LinearLayout) this
                .findViewById(com.example.weixindemo.R.id.voice_rcd_hint_rcding);
        voice_rcd_hint_loading = (LinearLayout) this
                .findViewById(com.example.weixindemo.R.id.voice_rcd_hint_loading);
        voice_rcd_hint_tooshort = (LinearLayout) this
                .findViewById(com.example.weixindemo.R.id.voice_rcd_hint_tooshort);
        mSensor = new SoundMeter();
        mBtnBack.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
        //btn_photo.setOnClickListener(this);
        mEditTextContent.setOnClickListener(this);
        //btn_face.setOnClickListener(this);
        iv_sec = (ImageView) findViewById(com.example.weixindemo.R.id.iv_sec);
        iv_sec.setOnClickListener(this);

        iv_third = (ImageView) findViewById(com.example.weixindemo.R.id.iv_third);
        iv_third.setOnClickListener(this);

        chatting_mode_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_voice) {
                    mBtnRcd.setVisibility(View.GONE);
                    mBottom.setVisibility(View.VISIBLE);
                    btn_voice = false;
                    chatting_mode_btn.setImageResource(com.example.weixindemo.R.drawable.first);
                    ((FaceRelativeLayout) findViewById(com.example.weixindemo.R.id.FaceRelativeLayout)).hideFaceView();
                } else {
                    mBtnRcd.setVisibility(View.VISIBLE);
                    mBottom.setVisibility(View.GONE);
                    chatting_mode_btn.setImageResource(com.example.weixindemo.R.drawable.first);
                    btn_voice = true;
                    ((FaceRelativeLayout) findViewById(com.example.weixindemo.R.id.FaceRelativeLayout)).hideFaceView();
                }
            }
        });
        mBtnRcd.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mEditTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //String text = mEditTextContent.getText().toString();
//				if(!(text.length()>0)){
//					//Toast.makeText(ChatMainActivity.this,mEditTextContent.getText()+"请输入聊天内容", 2).show();
//					btn_photo.setVisibility(View.VISIBLE);
//					//mBtnSend.setVisibility(View.GONE);
//				}else{
//					//Toast.makeText(ChatMainActivity.this,"输入的聊天内容为："+mEditTextContent.getText(), 2).show();
//					btn_photo.setVisibility(View.GONE);
//					//mBtnSend.setVisibility(View.VISIBLE);
//				}
            }

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                                          int arg3) {
            }
        });


        mPdListView.setOnRefreshListener(new XCPullToLoadMoreListView.OnRefreshListener(){


            @Override
            public void onPullDownLoadMore() {
                Log.v("czm", "onRefreshing");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {





//                        List<String> list = new ArrayList<String>();
//                        int i = 200 - mList.size() - 10 + 1;
//                        int count = 0;
//                        while (count < 10) {
//                            list.add("Item " + i);
//                            i++;
//                            count++;
//                        }
//                        mList.addAll(0, list);
//                        mAdapter.notifyDataSetChanged();
//                        mPTLListView.onRefreshComplete();
                    }
                }, 1000);
            }
        });
    }

    //按下语音录制
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!Environment.getExternalStorageDirectory().exists()) {
            Toast.makeText(this, "无内存卡，请安装..", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (btn_voice) {
            System.out.println("1");
            int[] location = new int[2];
            mBtnRcd.getLocationInWindow(location);
            int btn_rc_Y = location[1];
            int btn_rc_X = location[0];
            int[] del_location = new int[2];
            del_re.getLocationInWindow(del_location);
            int del_Y = del_location[1];
            int del_x = del_location[0];
            if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
                if (!Environment.getExternalStorageDirectory().exists()) {
                    Toast.makeText(this, "无内存卡，请安装..", Toast.LENGTH_SHORT).show();
                    return false;
                }
                System.out.println("2");
                if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {//判断手势按下的位置是否是语音录制按钮的范围内
                    System.out.println("3");
                    mBtnRcd.setBackgroundResource(com.example.weixindemo.R.drawable.voice_rcd_btn_pressed);
                    rcChat_popup.setVisibility(View.VISIBLE);
                    voice_rcd_hint_loading.setVisibility(View.VISIBLE);
                    voice_rcd_hint_rcding.setVisibility(View.GONE);
                    voice_rcd_hint_tooshort.setVisibility(View.GONE);
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (!isShosrt) {
                                voice_rcd_hint_loading.setVisibility(View.GONE);
                                voice_rcd_hint_rcding
                                        .setVisibility(View.VISIBLE);
                            }
                        }
                    }, 300);
                    img1.setVisibility(View.VISIBLE);
                    del_re.setVisibility(View.GONE);
                    startVoiceT = SystemClock.currentThreadTimeMillis();
                    voiceName = startVoiceT + ".amr";
                    start(voiceName);
                    flag = 2;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {
                System.out.println("4");
                mBtnRcd.setBackgroundResource(com.example.weixindemo.R.drawable.voice_rcd_btn_nor);
                if (event.getY() >= del_Y
                        && event.getY() <= del_Y + del_re.getHeight()
                        && event.getX() >= del_x
                        && event.getX() <= del_x + del_re.getWidth()) {
                    rcChat_popup.setVisibility(View.GONE);
                    img1.setVisibility(View.VISIBLE);
                    del_re.setVisibility(View.GONE);
                    stop();
                    flag = 1;
                    File file = new File(Environment.getExternalStorageDirectory() + "/"
                            + voiceName);
                    if (file.exists()) {
                        file.delete();
                    }
                } else {
                    voice_rcd_hint_rcding.setVisibility(View.GONE);
                    stop();
                    endVoiceT = SystemClock.currentThreadTimeMillis();
                    flag = 1;
                    int time = (int) ((endVoiceT - startVoiceT) / 50);
                    if (time < 1) {
                        isShosrt = true;
                        voice_rcd_hint_loading.setVisibility(View.GONE);
                        voice_rcd_hint_rcding.setVisibility(View.GONE);
                        voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                voice_rcd_hint_tooshort
                                        .setVisibility(View.GONE);
                                rcChat_popup.setVisibility(View.GONE);
                                isShosrt = false;
                            }
                        }, 500);
                        return false;
                    }
                    ChatMsgEntity entity = new ChatMsgEntity();
                    entity.setDate(getDate());
                    entity.setName("古月哥欠");
                    entity.setMsgType(false);
                    entity.setTime(time + "\"");
                    entity.setText(voiceName);
                    mDateArrays.add(entity);
                    mAdapter.notifyDataSetChanged();
                    mInnerListView.setSelection(mInnerListView.getCount() - 1);
                    rcChat_popup.setVisibility(View.GONE);
                }
            }
            if (event.getY() < btn_rc_Y) {//手势按下的位置不在语音录制按钮的范围内
                System.out.println("5");
                Animation mLitteAnimation = AnimationUtils.loadAnimation(this,
                        com.example.weixindemo.R.anim.cancel_rc);
                Animation mBigAnimation = AnimationUtils.loadAnimation(this,
                        com.example.weixindemo.R.anim.cancel_rc2);
                img1.setVisibility(View.GONE);
                del_re.setVisibility(View.VISIBLE);
                del_re.setBackgroundResource(com.example.weixindemo.R.drawable.voice_rcd_cancel_bg);
                if (event.getY() >= del_Y
                        && event.getY() <= del_Y + del_re.getHeight()
                        && event.getX() >= del_x
                        && event.getX() <= del_x + del_re.getWidth()) {
                    del_re.setBackgroundResource(com.example.weixindemo.R.drawable.voice_rcd_cancel_bg_focused);
                    sc_img1.startAnimation(mLitteAnimation);
                    sc_img1.startAnimation(mBigAnimation);
                }
            } else {

                img1.setVisibility(View.VISIBLE);
                del_re.setVisibility(View.GONE);
                del_re.setBackgroundResource(0);
            }
        }
        return super.onTouchEvent(event);
    }

    private void start(String name) {
        mSensor.start(name);
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private void stop() {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        volume.setImageResource(com.example.weixindemo.R.drawable.amp1);
    }

    private static final int POLL_INTERVAL = 300;

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stop();
        }
    };
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            updateDisplay(amp);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };
    //	private String[] msgArray = new String[]{"阳光总在风雨后","坚持就是胜利","哈哈...","最近活动挺多的嘞","忙啊",
//			"忙起来充实嘞","话是这么说...","怎么！！？","呵呵，没事..日子都是这样一天天过的","????","对了，有什么好看的电影介绍下呗","速七看了么？还有国产的战狼也还不错的哟","早看了，想去看赤道.....","恩恩，那个听说也不错，找机会一起呗！"};
//	private String[] dataArray = new String[]{"2015-05-08 13:01","2015-05-08 13:02","2015-05-08 13:03","2015-05-08 13:03","2015-05-08 13:04",
//			"2015-05-08 13:04","2015-05-08 13:05","2015-05-08 13:05","2015-05-08 13:06","2015-05-08 13:06",
//			"2015-05-08 13:07","2015-05-08 13:07","2015-05-08 13:07","2015-05-08 13:08"};
//	private static int COUNT = 0;//初始的消息数目
    private List<Map<String, Object>> jsonData = new ArrayList<>(); //初始化数据
    private List<Map<String, Object>> deltaDatas = new ArrayList<>(); //新增加的数据


    private String dataId = "";//即为会话对象Id
    private String title = "";//即为会话对象Id

    public void getSessionPragramData() {
        dataId = getIntent().getStringExtra("dataId");
        title = getIntent().getStringExtra("channelName");
        chat_title.setText(title);
        Log.e(TAG, "getSessionPragramData: 聊天界面收到的dataId " + dataId);
        Log.e(TAG, "getSessionPragramData: 聊天界面收到的title " + title);
        MyPreferenceManager.commitString(StuPra.PREF_CURRENT_CHATTING, dataId);

        getCount();

    }

    private void getCount() {
        String volleyUrl = Constant.sysUrl + Constant.requestListSet;
        Log.e("TAG", "开始获取聊天界面数据，这是地址 " + Constant.sysUrl + Constant.requestListSet);

        //参数
        Map<String, String> map = new HashMap<>();
        map.put(tableId, "373");
        map.put(Constant.pageId, "4698");
        map.put(Constant.mainTableId, "370");
        map.put(Constant.mainPageId, "4685");
        map.put(Constant.mainId, dataId);
        map.put(Constant.start, start + "");
        map.put(Constant.limit, "0");
        Log.e(TAG, "getData: 获取聊天信息的参数" + map.toString());

        //请求
        OkHttpUtils
                .post()
                .url(volleyUrl)
                .params(map)
                .build()
                .execute(new EdusStringCallback(ChatGoActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
//						mRefreshLayout.finishRefresh();
//                      ((BaseActivity) getActivity()).dialog.dismiss();
//						backStart();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: " + "  id  " + response);
                        checkCount(response);//解析聊天数据
                    }
                });



    }

    private void checkCount(String response) {
        Map<String, Object> menuMap = JSON.parseObject(response,
                new TypeReference<Map<String, Object>>() {
                });
        if (menuMap!=null&&menuMap.containsKey("dataCount")) {
         String dataCountStr=String.valueOf(menuMap.get("dataCount"));
            Log.e(TAG, "checkCount: dataCountStr"+""+"聊天消息总数 "+dataCountStr);
            if (Utils.stringIsInteger(dataCountStr)) {
                dataCount=Integer.valueOf(dataCountStr);
              int yushu=  dataCount%limit;
                Log.e(TAG, "checkCount: yushu "+yushu);
//                if (yushu>20) {
//                    start= dataCount-yushu+1;
//                    Log.e(TAG, "checkCount: start1 "+start);
//                }else{
                    start= dataCount-yushu+1;
                    Log.e(TAG, "checkCount: start2 "+start);
//                }
                getData();
            }
        }
    }


    private int start = 0;
    private final int limit = 20;
    private int dataCount=0;

    private void getData() {
        String volleyUrl = Constant.sysUrl + Constant.requestListSet;
        Log.e("TAG", "开始获取聊天界面数据，这是地址 " + Constant.sysUrl + Constant.requestListSet);

        //参数
        Map<String, String> map = new HashMap<>();
        map.put(tableId, "373");
        map.put(Constant.pageId, "4698");
        map.put(Constant.mainTableId, "370");
        map.put(Constant.mainPageId, "4685");
        map.put(Constant.mainId, dataId);
        map.put(Constant.start, start + "");
        map.put(Constant.limit, limit + "");
        Log.e(TAG, "getData: 获取聊天信息的参数" + map.toString());

        //请求
        OkHttpUtils
                .post()
                .url(volleyUrl)
                .params(map)
                .build()
                .execute(new EdusStringCallback(ChatGoActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
//						mRefreshLayout.finishRefresh();
////                        ((BaseActivity) getActivity()).dialog.dismiss();
//						backStart();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: " + "  id  " + response);
                        check(response);//解析聊天数据
                    }
                });

    }

    private static final String TAG = "ChatGoActivity";

    //解析聊天数据，使其转换至mDateArrays
    public void check(String menuData) {
        Map<String, Object> menuMap = JSON.parseObject(menuData,
                new TypeReference<Map<String, Object>>() {
                });
        List<Map<String, Object>> menuListMap2 = null;
        if (menuMap.containsKey("dataList")) {
            menuListMap2 = (List<Map<String, Object>>) menuMap.get("dataList");
            Log.e(TAG, "check: 聊天消息网络获取后的消息总数 " + menuMap.get("dataCount") + "");


            if (menuListMap2 != null && menuListMap2.size() > 0) {
                for (int i = 0; i < menuListMap2.size(); i++) {
                    Map<String, Object> map = menuListMap2.get(i);
//                    if (start > 0) {//如果不是第一次加载数据
//                        deltaDatas.add(map);
//                    } else {//如果是第一次加载
                        jsonData.add(map);
//                    }
                }

                Log.e(TAG, "check: 接收到的所有数据处理之后：jsonData  " + jsonData.toString());
                Log.e(TAG, "check: 接收到的所有数据处理之后：deltaDatas  " + deltaDatas.toString());

                // Collections.reverse(lists);   //这行就是将list的内容反转，下面再装进adapter里，就可以倒序显示了
                //  adapter.notifyDataSetChanged();
                // listview.setSelection(listview.getBottom());
                // listview.setSelection(listview.getBottom());
            }
            initData();
        }
    }

    public void initData() {
        for (int i = 0; i < jsonData.size(); i++) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(String.valueOf(jsonData.get(i).get(StuPra.messageSendDate)));//获取并设置消息发送时间

            String wantedUserId = String.valueOf(jsonData.get(i).get(StuPra.messageUserId));//获取userId用来判断是不是自己发的消息
            Log.e(TAG, "initData: wantedUserId " + wantedUserId);
            Log.e(TAG, "initData: Constant.USERID " + Constant.USERID);
            if (wantedUserId.equals(Constant.USERID)) {
                entity.setName(StuPra.messageFromMe);//直接设置自己的名称
                entity.setMsgType(false);//false为自己
            } else {
                String notMeName=String.valueOf(jsonData.get(i).get(StuPra.messageFromName));
                entity.setName(notMeName.equals("null")? StuPra.messageFromSys:notMeName);//获取并设置对方名称
                entity.setMsgType(true);//true为其他人
            }
            entity.setText(String.valueOf(jsonData.get(i).get(StuPra.messageContent)));//获取并设置消息内容
            mDateArrays.add(entity);
        }
        mAdapter = new ChatMsgViewAdapter(this, mDateArrays);
        mInnerListView = mPdListView.getListView();

        mInnerListView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == com.example.weixindemo.R.id.btn_back) {
            finish();

        } else if (i == com.example.weixindemo.R.id.btn_send) {
            send();
            ((FaceRelativeLayout) findViewById(com.example.weixindemo.R.id.FaceRelativeLayout)).hideFaceView();

        } else if (i == com.example.weixindemo.R.id.iv_sec) {
            PermissionGen.with(ChatGoActivity.this)
                    .addRequestCode(100)
                    .permissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request();

            //new PopupWindows(ChatGoActivity.this, btn_photo);
            // 隐藏表情选择框
            //((FaceRelativeLayout) findViewById(com.example.weixindemo.R.id.FaceRelativeLayout)).hideFaceView();

        } else if (i == com.example.weixindemo.R.id.iv_third) {
            PermissionGen.with(ChatGoActivity.this)
                    .addRequestCode(101)
                    .permissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .request();

            //new PopupWindows(ChatGoActivity.this, btn_photo);
            // 隐藏表情选择框
            //((FaceRelativeLayout) findViewById(com.example.weixindemo.R.id.FaceRelativeLayout)).hideFaceView();

        } else if (i == com.example.weixindemo.R.id.et_sendmessage) {
            //        	mEditTextContent.addTextChangedListener(new TextWatcher() {
//				@Override
//				public void afterTextChanged(Editable s) {
//					String text = mEditTextContent.getText().toString();
//					if(!(text.length()>0)){
//						//Toast.makeText(ChatMainActivity.this,mEditTextContent.getText()+"请输入聊天内容", 2).show();
//						btn_photo.setVisibility(View.VISIBLE);
//						mBtnSend.setVisibility(View.GONE);
//					}else{
//						//Toast.makeText(ChatMainActivity.this,"输入的聊天内容为："+mEditTextContent.getText(), 2).show();
//						btn_photo.setVisibility(View.GONE);
//						mBtnSend.setVisibility(View.VISIBLE);
//					}
//				}
//				@Override
//				public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
//				}
//				@Override
//				public void beforeTextChanged(CharSequence s, int arg1, int arg2,
//						int arg3) {
//				}
//			});
            // 隐藏表情选择框
            ((FaceRelativeLayout) findViewById(com.example.weixindemo.R.id.FaceRelativeLayout)).hideFaceView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @PermissionSuccess(requestCode = 100)
    public void doCapture() {
        PhotoPicker.builder()
                .setPhotoCount(9)//挑选照片
                .setShowCamera(false)//不显示拍照功能
                .setSelected(imgPaths)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .start(ChatGoActivity.this, PhotoPicker.REQUEST_CODE);
    }

    @PermissionFail(requestCode = 100)
    public void doFailedCapture() {
        Toast.makeText(ChatGoActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();
    }
    private ImageCaptureManager captureManager;
    @PermissionSuccess(requestCode = 101)
    public void doCamera() {
        try {
            if(captureManager == null){
                captureManager = new ImageCaptureManager(ChatGoActivity.this);
            }
            Intent   intent = captureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PermissionFail(requestCode = 101)
    public void doFailedCamera() {
        Toast.makeText(ChatGoActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();
    }

    //发送消息
    public void send() {
        String conString = mEditTextContent.getText().toString();

        if (conString.length() > 0) {
            String volleyUrl = Constant.sysUrl + Constant.commitAdd;
            Log.e("TAG", "发送消息接口地址 " + Constant.sysUrl + Constant.commitAdd);

            //参数
            Map<String, String> map = new HashMap<>();
            map.put(Constant.tableId, "17798");
            map.put(Constant.pageId, "7691");
            map.put("t0_au_17798_7691_36729", dataId);
            map.put("t0_au_17798_7691_36737", "1697");//文字类型消息发送参数
            map.put("t0_au_17798_7691_36731", conString);
            map.put("t0_au_17798_7691_36752", "");
            map.put("t0_au_17798_7691_36751", "");
            map.put("t0_au_17798_7691_36740", StuPra.configRole);//发送者类型教师角色值1703  学员角色值1702
            //附加参数
            map.put("t0_au_17798_7691_36739", "17798");
            map.put("strFlag", "appMsg");
            map.put("userType", StuPra.aliasTitle);//教师user,学员stu
            map.put("sendType", "4");
            Log.e(TAG, "发送消息参数  " + map.toString());
            //请求
            OkHttpUtils
                    .post()
                    .url(volleyUrl)
                    .params(map)
                    .build()
                    .execute(new EdusStringCallback(ChatGoActivity.this) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext, e);
                            //        dialog.dismiss();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e("ChatActivity", "onResponse: " + "  id  " + response);
                            // check(response);
                            if (response != null && response.length() > 0) {
                                mEditTextContent.setText("");//发送成功后设置text为空
//								etContent.setText("");
//								commitData();
                            }
                        }
                    });
        }
//
//			ChatMsgEntity entity = new ChatMsgEntity();
//			entity.setDate(getDate());
//			entity.setName("古月哥欠");
//			entity.setMsgType(false);
//			entity.setText(conString);
//			mDateArrays.add(entity);
//			mAdapter.notifyDataSetChanged();
//			mListView.setSelection(mListView.getCount()-1);

    }

    public String getDate() {
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));

        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + "-" + hour + ":" + mins);
        return sbBuffer.toString();
    }

    private void updateDisplay(double signalEMA) {

        switch ((int) signalEMA) {
            case 0:
            case 1:
                volume.setImageResource(com.example.weixindemo.R.drawable.amp1);
                break;
            case 2:
            case 3:
                volume.setImageResource(com.example.weixindemo.R.drawable.amp2);

                break;
            case 4:
            case 5:
                volume.setImageResource(com.example.weixindemo.R.drawable.amp3);
                break;
            case 6:
            case 7:
                volume.setImageResource(com.example.weixindemo.R.drawable.amp4);
                break;
            case 8:
            case 9:
                volume.setImageResource(com.example.weixindemo.R.drawable.amp5);
                break;
            case 10:
            case 11:
                volume.setImageResource(com.example.weixindemo.R.drawable.amp6);
                break;
            default:
                volume.setImageResource(com.example.weixindemo.R.drawable.amp7);
                break;
        }
    }


    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            super(mContext);

            View view = View.inflate(mContext, com.example.weixindemo.R.layout.item_popubwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext, com.example.weixindemo.R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view.findViewById(com.example.weixindemo.R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, com.example.weixindemo.R.anim.push_bottom_in_2));
            setWidth(LayoutParams.FILL_PARENT);
            setHeight(LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(com.example.weixindemo.R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(com.example.weixindemo.R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(com.example.weixindemo.R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
//				photo();
//				dismiss();
                    Intent intent = new Intent();
                    Intent intent_camera = getPackageManager().getLaunchIntentForPackage("com.android.camera");
                    if (intent_camera != null) {
                        intent.setPackage("com.android.camera");
                    }
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    ChatGoActivity.this.startActivityForResult(intent, TAKE_PICTURE);
                    dismiss();
                }
            });
            bt2.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
//				Intent intent = new Intent(ChatMainActivity.this,
//						TestPicActivity.class);
//				startActivity(intent);
//				dismiss();
                /*Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//调用android的图库
				startActivity(intent);
				dismiss();*/
                    Intent intent = new Intent(ChatGoActivity.this, ScaleImageFromSdcardActivity.class);
                    ChatGoActivity.this.startActivityForResult(intent, SHOW_ALL_PICTURE);
                    dismiss();
                    overridePendingTransition(com.example.weixindemo.R.anim.in_from_right, com.example.weixindemo.R.anim.out_to_left);//设置切换动画，从右边进入，左边退出
                }
            });
            bt3.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK && null != data) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(ChatGoActivity.this, "未找到SDK", Toast.LENGTH_SHORT).show();
                return;
            }
            new android.text.format.DateFormat();
            String name = android.text.format.DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            Bundle bundle = data.getExtras();
            //获取相机返回的数据，并转换为图片格式
            Bitmap bitmap;
            String filename = null;
            bitmap = (Bitmap) bundle.get("data");
            FileOutputStream fout = null;
            //定义文件存储路径
            File file = new File("/sdcard/cloudteam/");
            if (!file.exists()) {
                file.mkdirs();
            }
            filename = file.getPath() + "/" + name;
            try {
                fout = new FileOutputStream(filename);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fout.flush();
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(ChatGoActivity.this, CameraActivity.class);
            intent.putExtra("camera", filename);
            ChatGoActivity.this.startActivityForResult(intent, SHOW_CAMERA);
        } else if (requestCode == SHOW_CAMERA && resultCode == SHOW_CAMERA_RESULT) {
            Bundle bundle = data.getExtras();
            Object camera = bundle.get("imgUrl");
            Log.d("TAG", "需要发送照相的图片到服务器" + camera.toString());
            //将图片发送到聊天界面
            if (camera.toString().length() > 0) {
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setDate(getDate());
                entity.setName("古月哥欠");
                entity.setMsgType(false);
                entity.setText("[" + camera.toString() + "]");
                mDateArrays.add(entity);
                mAdapter.notifyDataSetChanged();
                mEditTextContent.setText("");
                mInnerListView.setSelection(mInnerListView.getCount() - 1);
            }
        } else if (requestCode == SHOW_ALL_PICTURE && resultCode == SHOW_PICTURE_RESULT) {
            List<String> bmpUrls = new ArrayList<String>();

            Bundle bundle = data.getExtras();
            Object[] selectPictures = (Object[]) bundle.get("selectPicture");
            for (int i = 0; i < selectPictures.length; i++) {
                Log.d("TAG", "selectPictures[i]" + selectPictures[i]);
                String bmpUrl = ScaleImageFromSdcardActivity.map.get(Integer.parseInt(selectPictures[i].toString()));
                bmpUrls.add(bmpUrl);
                ChatMsgEntity entity = new ChatMsgEntity();
                entity.setDate(getDate());
                entity.setName("古月哥欠");
                entity.setMsgType(false);
                entity.setText("[" + bmpUrl + "]");
                mDateArrays.add(entity);
                mAdapter.notifyDataSetChanged();
                mEditTextContent.setText("");
                mInnerListView.setSelection(mInnerListView.getCount() - 1);
            }
            Toast.makeText(ChatGoActivity.this, "选择图片数" + selectPictures.length, Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                imgPaths.clear();
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                Log.e(TAG, "onActivityResultwyl: " + photos.size());
                imgPaths.addAll(photos);
            }
        }else if (resultCode == RESULT_OK && requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO) {
            if(captureManager.getCurrentPhotoPath() != null) {
                captureManager.galleryAddPic();
                // 照片地址
                String imagePaht = captureManager.getCurrentPhotoPath();
                Log.e(TAG, "onActivityResult: imagePaht"+imagePaht);
            }
        }
    }

    //加载图片
    private void showImage(String imaePath) {
        Bitmap bm = BitmapFactory.decodeFile(imaePath);
        Log.e("imagpath=", imaePath);
        //((ImageView)findViewById(R.id.image)).setImageBitmap(bm);
    }

    private static final int TAKE_PICTURE = 0x000000;
    private static final int SHOW_CAMERA = 0x000001;
    private static final int SHOW_CAMERA_RESULT = 0x000002;
    private String path = "";


    public void photo() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStorageDirectory() + "/myimage/");
            if (!dir.exists()) dir.mkdirs();

            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(dir, String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            path = file.getPath();
            Uri imageUri = Uri.fromFile(file);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);


        } else {
            Toast.makeText(ChatGoActivity.this, "没有储存卡", Toast.LENGTH_LONG).show();
        }
    }

    public void head_xiaohei(View v) { // 标题栏 返回按钮

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && ((FaceRelativeLayout) findViewById(com.example.weixindemo.R.id.FaceRelativeLayout)).hideFaceView()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static boolean isForeground = false;
    //for receive customer msg from jpush server
    private MessageReceiver myReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";


    public void registerMessageReceiver() {
        myReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(myReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(StuPra.KEY_MESSAGE);
                String extras = intent.getStringExtra("extras");

                Log.e(TAG, "onReceive: messge " + messge);
                Log.e(TAG, "onReceive: extras " + extras);

                Map<String, Object> extrasMap = JSON.parseObject(extras,
                        new TypeReference<Map<String, Object>>() {
                        });


                if (extrasMap.get("channelId").equals(dataId)) {//如果频道相符则

                    ChatMsgEntity entity = new ChatMsgEntity();
                    entity.setDate(String.valueOf(extrasMap.get("sendTime")));//获取并设置消息发送时间

                    String wantedUserId = String.valueOf(extrasMap.get("senderId"));//获取userId用来判断是不是自己发的消息

                    if (wantedUserId.equals(Config.myName)) {
                        entity.setName(StuPra.messageFromMe);//直接设置自己的名称
                        entity.setMsgType(false);//false为自己
                    } else {
                        entity.setName(String.valueOf(extrasMap.get("sender")));//获取并设置对方名称
                        entity.setMsgType(true);//true为其他人
                    }
                    entity.setText(messge);//获取并设置消息内容
                    mDateArrays.add(entity);
                    mAdapter.notifyDataSetChanged();
                    mInnerListView.setSelection(mInnerListView.getCount() - 1);
                }


            }
        }
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();

    }
}