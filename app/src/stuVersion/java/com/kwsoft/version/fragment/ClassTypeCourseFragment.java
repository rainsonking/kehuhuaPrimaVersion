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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.widget.CalendarView;
import com.warmtel.expandtab.ExpandPopTabView;
import com.warmtel.expandtab.KeyValueBean;
import com.warmtel.expandtab.Pop1ListView;
import com.warmtel.expandtab.Pop2ListView;
import com.warmtel.expandtab.Pop3Layout;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.kwsoft.version.StuPra.classTypeGetSearchUrl;
import static com.kwsoft.version.StuPra.commitCourseSearch;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class ClassTypeCourseFragment extends Fragment {

    /**
     * 班型课表参数
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
    private List<KeyValueBean> mClassTypeSearchList = new ArrayList<>();
    private List<KeyValueBean> mWhichTime = new ArrayList<>();
    private String classTypeUrl;
    View view;
    private String defaultClassTypeName = "班型选择";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.stu_class_type_course_fragment_layout, container, false);

        classTypeUrl = Constant.sysUrl + classTypeGetSearchUrl;
        requestClassCourseData(classTypeUrl);
        return view;
    }

    private void requestClassCourseData(String classTypeUrl) {
        Map<String, String> paramsMapClass = new HashMap<>();
        paramsMapClass.put("mainId", Constant.USERID);
        OkHttpUtils
                .post()
                .params(paramsMapClass)
                .url(classTypeUrl)
                .build()
                .execute(new EdusStringCallback(getActivity()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                        initView("");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "返回的班型课表搜索条件数据  " + response);
                        initView(response);
                    }
                });


    }

    public void initView(String responseSearchData) {
        commitCourseSearch=new HashMap<>();
        setConfigsDatas(responseSearchData);
        expandTabView = (ExpandPopTabView) view.findViewById(R.id.expandtab_view);
        addItem1(expandTabView, mClassTypeSearchList, 0, defaultClassTypeName);
        addItem2(expandTabView, mWhichTime, 0, "第几次课");
        addItem3(expandTabView, "更多筛选");


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

    Pop1ListView pop1ListView;
    Pop2ListView pop2ListView;
    Pop3Layout pop3Layout;
    public void addItem1(final ExpandPopTabView expandTabView, List<KeyValueBean> lists, int defaultSelect, String defaultShowText) {
        pop1ListView = new Pop1ListView(getActivity());
        pop1ListView.setDefaultSelectByIndex(defaultSelect);
        //popViewOne.setDefaultSelectByKey(defaultSelect);
        pop1ListView.setCallBackAndData(lists, expandTabView, new Pop1ListView.OnSelectListener() {
            @Override
            public void getValue(String key, String value, String id, String num) {
                Log.e(TAG, "key :" + key + " ,value :" + value + " ,id :" + id + " ,num :" + num);
                commitCourseSearch.put("CT_ID",id);
                int courseNum = Integer.valueOf(num);
                mWhichTime.clear();
                KeyValueBean keyValueBean = new KeyValueBean("course_time", "不限", "", num);
                mWhichTime.add(keyValueBean);
                for (int i = 1; i <= courseNum; i++) {
                    KeyValueBean keyValueBeanItem = new KeyValueBean("course_time", "第" + i + "次课", i + "", num);
                    mWhichTime.add(keyValueBeanItem);
                }
                if (pop2ListView!=null) {
                    Log.e(TAG, "getValue: pop2ListView!=null ");
                    pop2ListView.setReSelectByIndex(0,1);
                }
            }
        });

        expandTabView.addItemToExpandTab(defaultShowText, pop1ListView);

    }

    public void addItem2(ExpandPopTabView expandTabView, List<KeyValueBean> lists, int defaultSelect, String defaultShowText) {
        pop2ListView = new Pop2ListView(getActivity());
        pop2ListView.setDefaultSelectByIndex(defaultSelect);
        //popViewOne.setDefaultSelectByKey(defaultSelect);
        pop2ListView.setCallBackAndData(lists, expandTabView, new Pop2ListView.OnSelectListener() {
            @Override
            public void getValue(String key, String value, String id, String num) {
                Log.e(TAG, "key :" + key + " ,value :" + value + " ,id :" + id + " ,num :" + num);
                commitCourseSearch.put("NOW_BOUT",id);
            }
        });

        expandTabView.addItemToExpandTab(defaultShowText, pop2ListView);

    }

    public void addItem3(ExpandPopTabView expandTabView, String defaultShowText) {
        pop3Layout = new Pop3Layout(getActivity());
        pop3Layout.setCallBackAndData(new Pop3Layout.OnDateSelectListener() {
            @Override
            public void getValue(String typeId, String startTime, String endTime) {
                Log.e(TAG, "typeId :" + typeId + " ,startTime :" + startTime + " ,endTime :" + endTime);
            }
        });


        expandTabView.addItemToExpandTab(defaultShowText, pop3Layout);
    }

    private void setConfigsDatas(String responseSearchData) {
        List<Map<String, Object>> searchDataListMap=new ArrayList<>();
        if (responseSearchData!=null) {
        try {
            Map<String, Object> searchDataMap = JSON.parseObject(responseSearchData,
                    new TypeReference<Map<String, Object>>() {
                    });
            if (searchDataMap!=null) {
                searchDataListMap = (List<Map<String, Object>>) searchDataMap.get("dataInfo");
                if (searchDataListMap!=null&&searchDataListMap.size()>0) {
                    for (int i = 0; i < searchDataListMap.size(); i++) {
                        KeyValueBean keyValueBean = new KeyValueBean("CT_NAME",
                                String.valueOf(searchDataListMap.get(i).get("CT_NAME")),
                                String.valueOf(searchDataListMap.get(i).get("CLASS_TYPE_ID")),
                                String.valueOf(searchDataListMap.get(i).get("ALL_BOUT")));
                        Log.e(TAG, "setConfigsDatas: ALL_BOUT " + String.valueOf(searchDataListMap.get(i).get("ALL_BOUT")));
                        mClassTypeSearchList.add(keyValueBean);
                        if (i == 0) {
                            defaultClassTypeName = String.valueOf(searchDataListMap.get(i).get("CT_NAME"));
                            String num2 = String.valueOf(searchDataListMap.get(i).get("ALL_BOUT"));
                            KeyValueBean keyValueBean2 = new KeyValueBean("course_time", "不限", "", num2);
                            mWhichTime.add(keyValueBean2);
                            for (int j = 1; j <= Integer.valueOf(keyValueBean.getNum()); j++) {
                                KeyValueBean keyValueBeanItem = new KeyValueBean("course_time", "第" + j + "次课", j + "", num2);
                                mWhichTime.add(keyValueBeanItem);
                            }
                        }
                    }
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (expandTabView != null) {
            expandTabView.onExpandPopView();
        }
    }
}
