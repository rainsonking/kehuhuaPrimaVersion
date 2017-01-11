package com.kwsoft.kehuhua.bailiChat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.UnsupportedEncodingException;

import okhttp3.Call;

import static com.zhy.http.okhttp.log.LoggerInterceptor.TAG;

/**
 * Created by Administrator on 2016/12/6 0006.
 */

public class AddConvActivity extends Activity {
    private EditText etConvName;
    private TextView etConvStartPsn, etConvPsn, etConvStartPsnId;
    private String convStartPsn;
    final String[] psn = {"学员1702", "人员1703", "系统1704"};
    String partPsnid, tableId, pageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_add_conv);

        etConvName = (EditText) findViewById(R.id.et_conv_name);
        etConvPsn = (TextView) findViewById(R.id.et_conv_psn);//会话参与人
        etConvStartPsn = (TextView) findViewById(R.id.et_conv_start_psn);
        etConvStartPsnId = (TextView) findViewById(R.id.et_conv_start_psn_id);

        tableId = "17796";
        pageId = "7687";

        etConvStartPsn.setText(psn[0].substring(0, 2));
        etConvStartPsnId.setText(psn[0].substring(2));
        etConvStartPsn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSingleDialog();
            }
        });
        etConvPsn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddConvActivity.this, SelectPsnActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        CommonToolbar mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);

        mToolbar.setTitle("添加会话");
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.edit_commit1));
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (partPsnid.length() > 0) {
                    commitData();
                }

            }
        });

    }

    private void commitData() {
        String volleyUrl = Constant.sysUrl + Constant.commitAdd;
        Log.e("TAG", "学员端登陆地址 " + Constant.sysUrl + Constant.commitAdd);
        String[] partPsnidArr = partPsnid.split(",");
        String params = null;
        String name = etConvName.getText().toString();
        // CodeConverter.toBrowserCode(name, "UTF-8")

        params = Constant.tableId + "=17796&" + Constant.pageId + "=7687&t0_au_17796_7687_36709=" +
                name + "&t0_au_17797_7689_36717_dz=" + partPsnidArr.length +
                "&t0_au_17796_7687_36750=" + etConvStartPsnId.getText().toString();

        for (int i = 0; i < partPsnidArr.length; i++) {
            params = params + "&t1_au_17797_7689_36723=" + partPsnidArr[i];
        }
        volleyUrl = volleyUrl + "?" + params;
//        //参数
//        Map<String, String> map = new HashMap<>();
//        map.put(Constant.tableId, "448");
//        map.put(Constant.pageId, "7688");
//        map.put("t0_au_17796_7687_36709", etConvName.getText().toString());
//
//        map.put("t0_au_17797_7689_36717_dz", partPsnidArr.length + "");
//        map.put("t0_au_17796_7687_36750", etConvStartPsnId.getText().toString());


        Log.e("AddConvActivity", "maplogi/" + volleyUrl);
        //请求
        OkHttpUtils
                .post()
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(AddConvActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                        //        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: " + "  id  " + response);
                        if (response != null && response.length() > 0) {
                            Toast.makeText(AddConvActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void setSingleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddConvActivity.this);
        builder.setIcon(R.mipmap.icon);
        builder.setTitle("");

        //    设置一个单项选择下拉框
        /**
         * 第一个参数指定我们要显示的一组下拉单选框的数据集合
         * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
         * 第三个参数给每一个单选项绑定一个监听器
         */
        builder.setSingleChoiceItems(psn, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AddConvActivity.this, psn[which], Toast.LENGTH_SHORT).show();
                // convStartPsn = psn[which];
                etConvStartPsn.setText(psn[which].substring(0, 2));
                etConvStartPsnId.setText(psn[which].substring(2));
                dialog.dismiss();
            }
        });
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        etConvStartPsn.setText(psn[which]);
//                    }
//                });
//                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            String name = data.getStringExtra("repsn");
            partPsnid = data.getStringExtra("repsnid");
            Log.e("AddConvATy", "dsfdsf" + data.getStringExtra("repsn") + partPsnid);
            etConvPsn.setText(name);
        }
    }

    static class CodeConverter {
        // 把中文字符转换为带百分号的浏览器编码

        // @param word 中文字符

        // @param encoding 字符编码
        public static String toBrowserCode(String word, String encoding)
                throws UnsupportedEncodingException {
            byte[] textByte = word.getBytes(encoding);
            StringBuilder strBuilder = new StringBuilder();

            for (int j = 0; j < textByte.length; j++) {
                // 转换为16进制字符
                String hexStr = Integer.toHexString(textByte[j] & 0xff);
                strBuilder.append("%" + hexStr.toUpperCase());
            }

            return strBuilder.toString();
        }
    }
}
