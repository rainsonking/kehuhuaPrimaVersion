package com.kwsoft.version.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.version.Common.AppConfig;
import com.kwsoft.version.Common.CacheCommon;
import com.kwsoft.version.Common.DataCleanManager;
import com.kwsoft.version.Common.FileUtil;
import com.kwsoft.version.Common.MethodsCompat;
import com.kwsoft.version.FeedbackActivity;
import com.kwsoft.version.ResetPwdActivity;
import com.kwsoft.version.StuInfoActivity;
import com.kwsoft.version.StuLoginActivity;
import com.kwsoft.version.StuPra;
import com.pgyersdk.update.PgyUpdateManager;
import com.zhy.http.okhttp.OkHttpUtils;

import org.kymjs.kjframe.Core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static android.R.attr.path;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.kwsoft.kehuhua.adcustom.R.id.imageView;
import static com.kwsoft.kehuhua.config.Constant.tableId;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class MeFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.tv_clean_cache)
    TextView tvCleanCache;
    @Bind(R.id.stu_name)
    TextView stuName;
    @Bind(R.id.stu_phone)
    TextView stuPhone;
    @Bind(R.id.stu_school_area)
    TextView stuSchoolArea;
    @Bind(R.id.stu_version)
    TextView stuVersion;
    @Bind(R.id.stu_head_image)
    ImageView stuHeadImage;
    private static String fileName;//头像名称


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);
        initData();

        Bitmap image = ((BitmapDrawable)stuHeadImage.getDrawable()).getBitmap();
        saveImageToGallery(getActivity(), image);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        caculateCacheSize();
    }

    public void initData() {
       // tvCleanCache.setText(getCache());
        stuName.setText(Constant.loginName);
        stuPhone.setText(Constant.USERNAME_ALL);
//        stuSchoolArea.setText("北京校区");
        try {
            //开始获取版本号
            String stuVersionCode = "v " + Utils.getVersionName(getActivity());
            stuVersion.setText(stuVersionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

//获取校区
        requestSet();
    }

    private void getMeTableId(String meStr, List<Map<String, Object>> meListMap) {
        if (meStr != null&&meStr.length()>0) {
            try {
                meListMap = JSON.parseObject(meStr,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (meListMap != null && meListMap.size() > 0) {
                for (int i = 0; i < meListMap.size(); i++) {
                    Map<String, Object> map = meListMap.get(i);
                    String menuName = map.get("menuName").toString();
                    if (menuName.contains("个人资料")) {
                        Constant.stuPerPAGEID = map.get("pageId").toString();
                        Constant.stuPerTABLEID = map.get("tableId").toString();
                        Log.e("pagetable", Constant.stuPerPAGEID + "/" + Constant.stuPerTABLEID);
                        break;
                    }
//                    else if (menuName.contains("反馈信息")){
//                        Constant.stuBackPAGEID = map.get("pageId").toString();
//                        Constant.stuBackTABLEID = map.get("tableId").toString();
//                    }
                }
                 requestSet();
            } else {
                Toast.makeText(getActivity(), "无菜单数据", Toast.LENGTH_SHORT).show();
            }
            Log.e("TAG", "获得学员端菜单数据：" + meStr);

        }else {
            Toast.makeText(getActivity(),"暂无个人数据",Toast.LENGTH_SHORT).show();
        }
    }

    public void requestSet() {

        final String volleyUrl = Constant.sysUrl + Constant.requestListSet;
        Log.e("TAG", "学员端请求个人信息地址：" + volleyUrl);

        //参数
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(tableId, Constant.stuPerTABLEID);
        paramsMap.put(Constant.pageId, Constant.stuPerPAGEID);
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(getActivity()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: " + "  id  " + response);
                        setStore(response);
                    }
                });
    }


    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {
        String jsonData1 = jsonData.replaceAll("00:00:00", "");
        Map<String, Object> stuInfoMap = null;
        try {
            stuInfoMap = Utils.str2map(jsonData1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> dataList;
        try {
            assert stuInfoMap != null;
            dataList = (List<Map<String, Object>>) stuInfoMap.get("dataList");
            Map<String, Object> pageSetMap = (Map<String, Object>) stuInfoMap.get("pageSet");
            List<Map<String, Object>> pageSet = (List<Map<String, Object>>) pageSetMap.get("fieldSet");
            Log.e("pageSet", pageSet.toString());
            String fieldCnName, fieldAliasName = "";
            for (int i = 0; i < pageSet.size(); i++) {
                Map<String, Object> map = pageSet.get(i);
                fieldCnName = map.get("fieldCnName") + "";
                if (fieldCnName.equals("校区")) {
                    fieldAliasName = map.get("fieldAliasName") + "";
                    break;
                }
            }
            Map<String, Object> map = dataList.get(0);
            if ((fieldAliasName.length() > 0) && (map.containsKey(fieldAliasName))) {
                String school = (String) map.get(fieldAliasName);
                stuSchoolArea.setText(school);
            } else {
                stuSchoolArea.setText("");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            stuSchoolArea.setText("");
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.stu_head_image, R.id.stu_version_layout, R.id.stu_log_out, R.id.stu_resetPwd, R.id.stu_info_data, R.id.ll_stu_clear_cache, R.id.ll_stu_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stu_head_image:
                break;
            case R.id.stu_log_out:
                Intent intentLogout = new Intent(getActivity(), StuLoginActivity.class);
                startActivity(intentLogout);
                getActivity().finish();
                break;
            case R.id.stu_resetPwd:
                Intent intent = new Intent(getActivity(), ResetPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.stu_info_data:
                Intent intentStuInfo = new Intent(getActivity(), StuInfoActivity.class);
                startActivity(intentStuInfo);
                break;
            case R.id.ll_stu_clear_cache:
               // dialog1();
                onClickCleanCache();
                break;
            case R.id.ll_stu_feedback:
                Intent intent1 = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent1);
                break;
            case R.id.stu_version_layout:
                //检测更新
                PgyUpdateManager.register(getActivity());
                break;
            default:
                break;
        }
    }

    private final int CLEAN_SUC = 1001;
    private final int CLEAN_FAIL = 1002;

    private void onClickCleanCache() {
        CacheCommon.getConfirmDialog(getActivity(), "是否清空缓存?", new DialogInterface.OnClickListener
                () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clearAppCache();
                //  tvCleanCache.setText("0KB");
            }
        }).show();
    }

    /**
     * 计算缓存的大小
     */
    private void caculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getActivity().getFilesDir();
        File cacheDir = getActivity().getCacheDir();

        fileSize += FileUtil.getDirSize(filesDir);
        fileSize += FileUtil.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat
                    .getExternalCacheDir(getActivity());
            fileSize += FileUtil.getDirSize(externalCacheDir);
            fileSize += FileUtil.getDirSize(new File(
                    org.kymjs.kjframe.utils.FileUtils.getSDCardPath()
                            + File.separator + "KJLibrary/cache"));
        }
        if (fileSize > 0)
            cacheSize = FileUtil.formatFileSize(fileSize);
        Log.e("cachesize=",cacheSize);
        tvCleanCache.setText(cacheSize);
    }

    /**
     * 清除app缓存
     *
     * @param
     */
    public void clearAppCache() {

        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    myclearaAppCache();
                    msg.what = CLEAN_SUC;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = CLEAN_FAIL;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 清除app缓存
     */
    public void myclearaAppCache() {
        DataCleanManager.cleanDatabases(getActivity());
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(getActivity());
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataCleanManager.cleanCustomCache(MethodsCompat.getExternalCacheDir(getActivity()).getPath());
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
        Core.getKJBitmap().cleanCache();
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(getActivity()).remove(key);
    }

    /**
     * 清除保存的缓存
     */
    public Properties getProperties() {
        return AppConfig.getAppConfig(getActivity()).get();
    }

    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CLEAN_FAIL:
                    Toast.makeText(getActivity(), "清除失败", Toast.LENGTH_SHORT).show();
                    break;
                case CLEAN_SUC:
                    tvCleanCache.setText("0KB");
                    Toast.makeText(getActivity(), "清除成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };


    private void dialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否确认清除缓存?"); //设置内容
        //builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                clearCache();
                tvCleanCache.setText(getCache());
                Toast.makeText(getActivity(), "清除成功", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                Toast.makeText(getActivity(), "取消", Toast.LENGTH_SHORT).show();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }


    public String getCache() {
        String cache = "";
        try {
//           cache = DataCleanManager.getVolleyCache(getActivity());
            cache = DataCleanManager.getTotalCacheSize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }

    public void clearCache() {
        try {
            DataCleanManager.cleanExternalCache(getActivity());

            DataCleanManager.cleanInternalCache(getActivity());


            // cache = DataCleanManager.getTotalCacheSize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存的图片路径传入进去，最后通知图库更新。
     * @param context
     * @param bmp
     */

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
       // String fileName = System.currentTimeMillis() + ".jpg";
         fileName = "hpshead" + ".jpg";
        File file = new File(appDir, fileName);
        StuPra.hpsStuHeadPath = file.getPath();
        Log.e("hpstushead", StuPra.hpsStuHeadPath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }


}
