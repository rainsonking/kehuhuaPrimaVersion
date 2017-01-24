package com.kwsoft.version;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.kwsoft.kehuhua.adcustom.ExampleUtil;
import com.kwsoft.kehuhua.adcustom.MessagAlertActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.bailiChat.SessionFragment;
import com.kwsoft.kehuhua.bean.MainFragmentsTab;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.sessionService.SessionService;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.MyPreferenceManager;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.kehuhua.widget.CnToolbar;
import com.kwsoft.kehuhua.widget.FragmentTabHost;
import com.kwsoft.kehuhua.zxing.TestScanActivity;
import com.kwsoft.version.fragment.AllCourseFragment;
import com.kwsoft.version.fragment.AssortFragment;
import com.kwsoft.version.fragment.MeFragment;
import com.kwsoft.version.fragment.StuFragmentTabAdapter;
import com.kwsoft.version.fragment.StudyFragment;
import com.pgyersdk.update.PgyUpdateManager;

import java.util.ArrayList;
import java.util.List;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;




/**
 * 学员端看板界面
 * wyl
 */
public class StuMainActivity extends BaseActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks {
    private LayoutInflater mInflater;
    private FragmentTabHost mTabhost;
    private List<MainFragmentsTab> mTabs = new ArrayList<>(5);


    private static final String TAG = "StuMainActivity";
    StuFragmentTabAdapter stutabAdapter;
    private RadioGroup radioGroup;
    private RadioButton radio3;
    private String arrStr, menuList, menuDataMap;//看板数据、课程表数据、主菜单数据
    public CnToolbar mToolbar;
    SharedPreferences sPreferences;
    private String useridOld;
    AssortFragment menuFragment;
    private String hideMenuList;//获取我的界面中的tableid pageid 个人资料
    private String feedbackInfoList;//反馈信息
    String admissInfoContent;//入学通知内容
    public static boolean isForeground = false;
    private SessionService.MyBinder myBinder;
    public ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (SessionService.MyBinder) service;
            myBinder.startDownload();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_main);
//        getSupportActionBar().hide();
        CloseActivityClass.activityList.add(this);
        sPreferences = getSharedPreferences(Constant.proId, MODE_PRIVATE);
        //useridOld = sPreferences.getString("useridOld", "");
        MyPreferenceManager.init(mContext);
        initView();
        initTab();
//        if (!Constant.USERID.equals(useridOld)) {
//            initDialog();
//            sPreferences.edit().putString("useridOld", Constant.USERID).apply();
//        }
        PgyUpdateManager.register(this);
        Utils.startPollingService(mContext, 2*60, SessionService.class, SessionService.ACTION,connection);//启动20分钟一次的轮询获取session服务
        registerMessageReceiver();  // used for receive msg

    }
    private void initTab() {
        MainFragmentsTab tab_home = new MainFragmentsTab(StudyFragment.class,R.string.home,R.drawable.stu_foot_first_selector);
        MainFragmentsTab tab_assort = new MainFragmentsTab(AssortFragment.class,R.string.assort,R.drawable.stu_foot_forth_selector);
        MainFragmentsTab tab_course = new MainFragmentsTab(AllCourseFragment.class,R.string.course,R.drawable.stu_foot_second_selector);
        MainFragmentsTab tab_session = new MainFragmentsTab(SessionFragment.class,R.string.session,R.drawable.teach_foot_fifth_selector);
        MainFragmentsTab tab_mine = new MainFragmentsTab(MeFragment.class,R.string.mine,R.drawable.stu_foot_third_selector);

        mTabs.add(tab_home);
        mTabs.add(tab_assort);
        mTabs.add(tab_course);
        mTabs.add(tab_session);
        mTabs.add(tab_mine);
        mInflater = LayoutInflater.from(this);
        mTabhost = (FragmentTabHost) this.findViewById(android.R.id.tabhost);
        mTabhost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);

        for (MainFragmentsTab tab : mTabs){
            TabHost.TabSpec tabSpec = mTabhost.newTabSpec(getString(tab.getTitle()));
            tabSpec.setIndicator(buildIndicator(tab));
            mTabhost.addTab(tabSpec,tab.getFragment(),null);
        }

        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabhost.setCurrentTab(0);
        mTabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                Log.e(TAG, "onTabChanged: s "+s);
                switch (s) {
                    case "首页":
                        mToolbar.setTitle("主页");
                        mToolbar.hideClassTypeSelect();
                        break;
                    case "课表":
                        mToolbar.hideTitleView();
                        mToolbar.showClassTypeSelect();
                        break;
                    case "我":
                        mToolbar.setTitle("个人中心");
                        mToolbar.hideClassTypeSelect();
                        break;
                    case "分类":
                        mToolbar.setTitle("分类");
                        mToolbar.hideClassTypeSelect();
                        break;
                    case "消息":
                        mToolbar.setTitle("消息");
                        mToolbar.hideClassTypeSelect();
                        break;

                }
            }
        });


    }
    private View buildIndicator(MainFragmentsTab tab){
        View view =mInflater.inflate(R.layout.tab_indicator,null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        TextView text = (TextView) view.findViewById(R.id.txt_indicator);
        img.setBackgroundResource(tab.getIcon());
        text.setText(tab.getTitle());
        return  view;
    }
    @Override
    public void initView() {
        mToolbar = (CnToolbar) findViewById(R.id.stu_toolbar);
//        Resources resources = mContext.getResources().getDrawable(R.drawable.nav_news);
//        Drawable drawable = resources.getDrawable(R.drawable.nav_news);
//        mToolbar.setRightButtonIcon(mContext.getResources().getDrawable(R.drawable.nav_news));
        mToolbar.setLeftButtonIcon(mContext.getResources().getDrawable(R.drawable.nav_scan_code));
        mToolbar.setTitle("学员端");
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(StuMainActivity.this, MessagAlertActivity.class);
                startActivity(intent2);
            }
        });

        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionGen.with(StuMainActivity.this)
                        .addRequestCode(105)
                        .permissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request();
            }
        });
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

    @PermissionSuccess(requestCode = 105)
    public void doCapture() {
        toCamera();
    }

    @PermissionFail(requestCode = 105)
    public void doFailedCapture() {
        Toast.makeText(StuMainActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();
    }

    public void toCamera() {

        Intent intent = new Intent(StuMainActivity.this, TestScanActivity.class);
        startActivityForResult(intent, 1);
    }

    private static long exitTime = 0;// 退出时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - exitTime) > 1000) {

                String msg = "再按一次退出";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                exitTime = System.currentTimeMillis();
            } else {
                Toast.makeText(this, "直接退出", Toast.LENGTH_SHORT).show();
//                CloseActivityClass.exitClient(this);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                toCamera();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
        Utils.stopPollingService(this, SessionService.class, SessionService.ACTION);
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());
            }
        }
    }

    private void setCostomMsg(String msg) {
        Log.e(TAG, "setCostomMsg: msg " + msg);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();}
}

