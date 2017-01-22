package com.kwsoft.version.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.widget.CalendarView;
import com.kwsoft.version.dto.ConfigAreaDTO;
import com.kwsoft.version.dto.ConfigsDTO;
import com.kwsoft.version.dto.ConfigsMessageDTO;
import com.warmtel.expandtab.ExpandPopTabView;
import com.warmtel.expandtab.KeyValueBean;
import com.warmtel.expandtab.PopOneListView;
import com.warmtel.expandtab.PopThreeLinearLayout;
import com.warmtel.expandtab.PopTwoListView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.kwsoft.version.StuPra.classTypeGetSearchUrl;

/**
 * Created by Administrator on 2016/9/6 0006.
 *
 */
public class ClassTypeCourseFragment extends Fragment {

    /**
     * 班型课表参数
     *
     */
    private TextView mTextSelectMonth;
    private ImageButton mLastMonthView;
    private ImageButton mNextMonthView;
    private CalendarView mCalendarView;
    private List<String> mDatas;
    private List<String> mDatas1;
    //班型课表搜索条件
    private ExpandPopTabView expandTabView;
    private List<KeyValueBean> mParentLists = new ArrayList<>();
    private List<ArrayList<KeyValueBean>> mChildrenListLists = new ArrayList<>();
    private List<KeyValueBean> mPriceLists;
    private List<KeyValueBean> mSortLists;
    private List<KeyValueBean> mFavorLists;
    private String classTypeUrl;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stu_class_type_course_fragment_layout, container, false);
        initView(view);
        classTypeUrl=Constant.sysUrl +classTypeGetSearchUrl;
        requestClassCourseData(classTypeUrl);
        return view;
    }

    private void requestClassCourseData(String classTypeUrl) {
            Map<String, String> paramsMapClass = new HashMap<>();
            paramsMapClass.put("mainId",Constant.USERID);
            OkHttpUtils
                    .post()
                    .params(paramsMapClass)
                    .url(classTypeUrl)
                    .build()
                .execute(new EdusStringCallback(getActivity()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "返回的班型课表搜索条件数据  "+response);
//                        setStore(response);
                    }
                });


    }

    public void initView(View view) {
        setConfigsDatas();
        expandTabView = (ExpandPopTabView) view.findViewById(R.id.expandtab_view);
        addItem(expandTabView, mPriceLists, "不限", "班型");
        addItem(expandTabView, mFavorLists, "不限", "课次");
        addItem(expandTabView,"更多筛选");


//        addItem(expandTabView, mParentLists, mChildrenListLists, "锦江区", "合江亭", "区域");
        /**
         *
         * 课程表2初始化
         */
        mTextSelectMonth = (TextView) view.findViewById(R.id.txt_select_month);
        mLastMonthView = (ImageButton) view.findViewById(R.id.img_select_last_month);
        mNextMonthView = (ImageButton) view.findViewById(R.id.img_select_next_month);
        mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        mLastMonthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendarView.setLastMonth();
                mTextSelectMonth.setText(mCalendarView.getDate());
            }
        });
        mNextMonthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendarView.setNextMonth();
                mTextSelectMonth.setText(mCalendarView.getDate());
            }
        });

        // 初始化可选日期
        initData();

        // 设置可选日期
        mCalendarView.setOptionalDate(mDatas);
        // 设置已选日期
//        mCalendarView.setSelectedDates(mDatas1);
        // 设置不可以被点击
        mCalendarView.setClickable(true);

        // 设置点击事件
        mCalendarView.setOnClickDate(new CalendarView.OnClickListener() {
            @Override
            public void onClickDateListener(int year, int month, int day) {
                Toast.makeText(getActivity(), year + "年" + month + "月" + day + "天", Toast.LENGTH_SHORT).show();

                // 获取已选择日期
                List<String> dates = mCalendarView.getSelectedDates();
                for (String date : dates) {
                    Log.e("test", "date: " + date);
                }
            }
        });

        mTextSelectMonth.setText(mCalendarView.getDate());
    }
    public void addItem(ExpandPopTabView expandTabView, List<KeyValueBean> lists, String defaultSelect, String defaultShowText) {
        PopOneListView popOneListView = new PopOneListView(getActivity());
        popOneListView.setDefaultSelectByValue(defaultSelect);
        //popViewOne.setDefaultSelectByKey(defaultSelect);
        popOneListView.setCallBackAndData(lists, expandTabView, new PopOneListView.OnSelectListener() {
            @Override
            public void getValue(String key, String value) {
                Log.e("tag", "key :" + key + " ,value :" + value);
            }
        });

        expandTabView.addItemToExpandTab(defaultShowText, popOneListView);
    }


    public void addItem(ExpandPopTabView expandTabView, String defaultShowText) {
        PopThreeLinearLayout popThreeLinearLayout = new PopThreeLinearLayout(getActivity());
        expandTabView.addItemToExpandTab(defaultShowText, popThreeLinearLayout);
    }
    public void addItem(ExpandPopTabView expandTabView, List<KeyValueBean> parentLists,
                        List<ArrayList<KeyValueBean>> childrenListLists, String defaultParentSelect, String defaultChildSelect, String defaultShowText) {
        PopTwoListView popTwoListView = new PopTwoListView(getActivity());
        popTwoListView.setDefaultSelectByValue(defaultParentSelect, defaultChildSelect);
//        distanceView.setDefaultSelectByKey(defaultParent, defaultChild);
        popTwoListView.setCallBackAndData(expandTabView, parentLists, childrenListLists, new PopTwoListView.OnSelectListener() {
            @Override
            public void getValue(String showText, String parentKey, String childrenKey) {
                Log.e("tag", "showText :" + showText + " ,parentKey :" + parentKey + " ,childrenKey :" + childrenKey);
            }
        });
        expandTabView.addItemToExpandTab(defaultShowText, popTwoListView);
    }
    public String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
    private void setConfigsDatas() {

        try {
            InputStream is =  getActivity().getResources().getAssets().open("searchType");
            String searchTypeJson = readStream(is);
            ConfigsMessageDTO messageDTO = JSONObject.parseObject(searchTypeJson, ConfigsMessageDTO.class);
            ConfigsDTO configsDTO = messageDTO.getInfo();
            mPriceLists = configsDTO.getPriceType();
            mSortLists = configsDTO.getSortType();
            mFavorLists = configsDTO.getSortType();

            List<ConfigAreaDTO> configAreaListDTO = configsDTO.getCantonAndCircle();
            for (ConfigAreaDTO configAreaDTO : configAreaListDTO) {
                KeyValueBean keyValueBean = new KeyValueBean();
                keyValueBean.setKey(configAreaDTO.getKey());
                keyValueBean.setValue(configAreaDTO.getValue());
                mParentLists.add(keyValueBean);

                ArrayList<KeyValueBean> childrenLists = new ArrayList<>();
                for (KeyValueBean keyValueBean1 : configAreaDTO.getBusinessCircle()) {
                    childrenLists.add(keyValueBean1);
                }
                mChildrenListLists.add(childrenLists);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //课表2初始化数据
    private void initData() {
        mDatas = new ArrayList<>();
        mDatas.add("20161101");
        mDatas.add("20161102");
        mDatas.add("20161103");
        mDatas.add("20161116");
        mDatas.add("20161117");
        mDatas.add("20161126");
        mDatas.add("20161110");
        mDatas.add("20161111");
        mDatas.add("20161112");

        mDatas1 = new ArrayList<>();
        mDatas1.addAll(mDatas);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(expandTabView != null){
            expandTabView.onExpandPopView();
        }
    }
}
