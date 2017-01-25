package com.kwsoft.version.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.widget.CalendarView;
import com.kwsoft.version.StuPra;
import com.warmtel.expandtab.ExpandPopTabView;
import com.warmtel.expandtab.KeyValueBean;
import com.warmtel.expandtab.Pop1ListView;
import com.warmtel.expandtab.Pop2ListView;
import com.warmtel.expandtab.Pop3Layout;
import com.zhy.http.okhttp.OkHttpUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.kwsoft.version.StuPra.classTypeGetSearchUrl;
import static com.kwsoft.version.StuPra.commitCourseSearch;
import static com.kwsoft.version.StuPra.diJiCiKe;

/**
 * Created by Administrator on 2016/9/6 0006.
 *
 */
public class ClassTypeCourseFragment extends Fragment {

    /**
     * 班型课表参数
     */
    private TextView mTextSelectMonth,mTodayDateValue;
    private RelativeLayout mLastMonthView;
    private RelativeLayout mNextMonthView;
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
    private ListView mListView;



    private CourseSearchResultAdapter mAdapter;
    private List<Map<String, Object>> searchDataListMap;
    private List<Map<String, Object>> searchDataListMapfirstDay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.stu_class_type_course_fragment_layout, container, false);
        ((BaseActivity)getActivity()).dialog.show();
        classTypeUrl = Constant.sysUrl + classTypeGetSearchUrl;
        requestClassCourseData(classTypeUrl);
        return view;
    }

    private void requestClassCourseData(String classTypeUrl) {
        Map<String, String> paramsMapClass = new HashMap<>();
        paramsMapClass.put(Constant.mainId, Constant.USERID);
        OkHttpUtils
                .post()
                .params(paramsMapClass)
                .url(classTypeUrl)
                .build()
                .execute(new EdusStringCallback(getActivity()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                        ((BaseActivity)getActivity()).dialog.dismiss();
                        initView("");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "返回的班型课表搜索条件数据  " + response);
                        ((BaseActivity)getActivity()).dialog.dismiss();
                        initView(response);
                    }
                });


    }

    /**
     * mainId=106     app登陆学员id
     tableId=19     内容表id  固定为19
     CT_ID=xxx      班型id  默认为接口返回第一个班型的id
     NOW_BOUT=xxx   第几次课  默认为不限
     CM_TYPE=xxx    班级类型 默认为不限
     minDate=2017-01-01    起始时间 控件选择  默认值为本月第一天
     maxDate=2017-01-31    结束时间 控件选择  默认值为本月最后一天

     */
    public void initView(String responseSearchData) {
        commitCourseSearch=new HashMap<>();
        diJiCiKe="1";
        setConfigsDatas(responseSearchData);
        expandTabView = (ExpandPopTabView) view.findViewById(R.id.expandtab_view);
        addItem1(expandTabView, mClassTypeSearchList, 0, defaultClassTypeName);
        addItem2(expandTabView, mWhichTime, 0, "第1次课");
        addItem3(expandTabView, "更多筛选");

        commitCourseSearch.put(Constant.mainId,Constant.USERID);
        commitCourseSearch.put(Constant.tableId,19+"");
        commitCourseSearch.put("CT_ID",firstClassTypeId!=null?firstClassTypeId:"");//第一个班型
        commitCourseSearch.put("NOW_BOUT","");
        commitCourseSearch.put("CM_TYPE","");
        Calendar cal;
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.getTime();
        commitCourseSearch.put("minDate",dateFormater.format(cal.getTime()) + "");
        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        commitCourseSearch.put("maxDate",dateFormater.format(cal.getTime()));
//        addItem(expandTabView, mParentLists, mChildrenListLists, "锦江区", "合江亭", "区域");
        mTextSelectMonth = (TextView) view.findViewById(R.id.txt_show_month);
        mTextSelectMonth.setText(cal.get(Calendar.YEAR)+"  "+cal.get(Calendar.MONTH)+"月");
        /**
         *
         * 课程表2初始化
         */

        mTodayDateValue= (TextView) view.findViewById(R.id.today_date_value);
        mLastMonthView = (RelativeLayout) view.findViewById(R.id.month_switch_left);
        mNextMonthView = (RelativeLayout) view.findViewById(R.id.month_switch_right);
        mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        mListView = (ListView) view.findViewById(android.R.id.list);
        View emptyView = view.findViewById(android.R.id.empty);
        mListView.setEmptyView(emptyView);
        searchDataListMapfirstDay=new ArrayList<>();
        mAdapter = new CourseSearchResultAdapter(searchDataListMapfirstDay,getActivity());
        mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListView);

        requestSearchResult();
        mLastMonthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendarView.setLastMonth();
                mTextSelectMonth.setText(mCalendarView.getDateTitle());

            }
        });
        mNextMonthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendarView.setNextMonth();
                mTextSelectMonth.setText(mCalendarView.getDateTitle());
            }
        });
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

                diJiCiKe="1";
                commitCourseSearch.put("NOW_BOUT",diJiCiKe);
                int courseNum = Integer.valueOf(num);
                mWhichTime.clear();
//                KeyValueBean keyValueBean = new KeyValueBean("course_time", "不限", "", num);
//                mWhichTime.add(keyValueBean);
                for (int i = 1; i <= courseNum; i++) {
                    KeyValueBean keyValueBeanItem = new KeyValueBean("course_time", "第" + i + "次课", i + "", num);
                    mWhichTime.add(keyValueBeanItem);
                }
                if (pop2ListView!=null) {
                    Log.e(TAG, "getValue: pop2ListView!=null ");
                    pop2ListView.setReSelectByIndex(0,1);
                }
                requestSearchResult();
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
                diJiCiKe=id;
                requestSearchResult();
            }
        });

        expandTabView.addItemToExpandTab(defaultShowText, pop2ListView);

    }

    public void addItem3(ExpandPopTabView expandTabView, String defaultShowText) {
        pop3Layout = new Pop3Layout(getActivity());
        pop3Layout.setCallBackAndData(expandTabView,new Pop3Layout.OnDateSelectListener() {
            @Override
            public void getValue(String typeId, String startTime, String endTime) {
                Log.e(TAG, "typeId :" + typeId + " ,startTime :" + startTime + " ,endTime :" + endTime);
                commitCourseSearch.put("CM_TYPE",typeId);
                commitCourseSearch.put("minDate",startTime);
                commitCourseSearch.put("maxDate",endTime);
                requestSearchResult();
            }
        });


        expandTabView.addItemToExpandTab(defaultShowText, pop3Layout);
    }
//请求搜索结果
public void requestSearchResult() {
    ((BaseActivity)getActivity()).dialog.show();
    final String volleyUrl = Constant.sysUrl + StuPra.classTypeCommitSearchUrl;
    Log.e(TAG, "学员端请求班型搜索结果地址： " + volleyUrl);
    Log.e(TAG, "学员端请求班型搜索结果参数:  "+commitCourseSearch.toString());
    //请求
    OkHttpUtils
            .post()
            .params(commitCourseSearch)
            .url(volleyUrl)
            .build()
            .execute(new EdusStringCallback(getActivity()) {
                @Override
                public void onError(Call call, Exception e, int id) {
                    ErrorToast.errorToast(mContext, e);
                    ((BaseActivity)getActivity()).dialog.dismiss();
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.e(TAG, "onResponse: " + "  id  " + response);
                    setStore(response);
                    ((BaseActivity)getActivity()).dialog.dismiss();
                }
            });
}

    private void setStore(String responseSearchResultData) {

        if (responseSearchResultData!=null) {
            try {
                Map<String, Object> searchDataMap = JSON.parseObject(responseSearchResultData,
                        new TypeReference<Map<String, Object>>() {
                        });
                    searchDataListMap = (List<Map<String, Object>>) searchDataMap.get("dataInfo");
                initData();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "setStore: 无搜索结果数据");
            }
        }

    }

    private  String firstClassTypeId;
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
                            firstClassTypeId= String.valueOf(searchDataListMap.get(i).get("CLASS_TYPE_ID"));
                            defaultClassTypeName = String.valueOf(searchDataListMap.get(i).get("CT_NAME"));
                            String num2 = String.valueOf(searchDataListMap.get(i).get("ALL_BOUT"));
//                            KeyValueBean keyValueBean2 = new KeyValueBean("course_time", "不限", "", num2);
//                            mWhichTime.add(keyValueBean2);
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
        for (int i = 0; i < searchDataListMap.size(); i++) {
            long thisDate =Long.valueOf(String.valueOf(searchDataListMap.get(i).get("START_TIME")));
            SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");
            Date dt = new Date(thisDate);
            mDatas.add(sdf.format(dt));
        }
        mDatas1 = new ArrayList<>();
        if (mDatas.size()>0) {
            mDatas1.add(mDatas.get(0));
            mTodayDateValue.setText(mDatas.get(0));
        }

        // 设置可选日期
        mCalendarView.setOptionalDate(mDatas);
        // 设置已选日期
        mCalendarView.setSelectedDates(mDatas1);
        // 设置不可以被点击
        mCalendarView.setClickable(true);

        // 设置点击事件
        mCalendarView.setOnClickDate(new CalendarView.OnClickListener() {
            @Override
            public void onClickDateListener(int year, int month, int day) {
//                Toast.makeText(getActivity(), year + "年" + month + "月" + day + "天", Toast.LENGTH_SHORT).show();
                searchDataListMapfirstDay.clear();
                // 获取已选择日期  过滤其他日期显示本日期内容
                String month1;
                if (month<10) {
                    month1="0"+month;
                }else{
                    month1=""+month;
                }
                 String thisDay=year+""+month1+""+day;
                Log.e(TAG, "onClickDateListener: thisDay "+thisDay);
                mTodayDateValue.setText(thisDay);
                if (isHasString(thisDay,mDatas)) {

                    for (int i = 0; i < searchDataListMap.size(); i++) {
                        long thisDate =Long.valueOf(String.valueOf(searchDataListMap.get(i).get("START_TIME")));
                        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");
                        Date dt = new Date(thisDate);
                        Log.e(TAG, "onClickDateListener: sdf.format(dt)  "+sdf.format(dt));
                        if (thisDay.equals(sdf.format(dt))) {
                            Log.e(TAG, "onClickDateListener: 确实相等");
                            searchDataListMapfirstDay.add(searchDataListMap.get(i));
                        }
                    }
                    Log.e(TAG, "onClickDateListener: searchDataListMapfirstDay "+searchDataListMapfirstDay.toString());


                }

                mAdapter.notifyDataSetChanged();



                List<String> dates = mCalendarView.getSelectedDates();
                for (String date : dates) {
                    Log.e("test", "date: " + date);
                }
            }
        });

        mTextSelectMonth.setText(mCalendarView.getDate());
        searchDataListMapfirstDay.clear();
        Log.e(TAG, "initData: searchDataListMapfirstDay初始化清空");
        //在此默认选择第一天有数据的课程列表
        if (mDatas.size()>0) {

            Log.e(TAG, "initData: 监测点2");
            for (int i = 0; i < searchDataListMap.size(); i++) {
                long thisDate =Long.valueOf(String.valueOf(searchDataListMap.get(i).get("START_TIME")));
                SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");
                Date dt = new Date(thisDate);

                if (mDatas.get(0).equals(sdf.format(dt))) {
                    searchDataListMapfirstDay.add(searchDataListMap.get(i));
                }
            }
            Log.e(TAG, "initData: 监测点1"+searchDataListMapfirstDay.toString());

        }
        mAdapter.notifyDataSetChanged();


    }

    public boolean isHasString(String xxStr,List<String> list) {

        for (int i = 0; i < list.size(); i++) {
            if (xxStr.equals(list.get(i))) {
                return true;
            }
        }
        return false;
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (expandTabView != null) {
            expandTabView.onExpandPopView();
        }
    }
}
