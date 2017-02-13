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
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.widget.CalendarView;
import com.kwsoft.version.StuPra;
import com.zhy.http.okhttp.OkHttpUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class CourseFragmentMonth extends Fragment {
    private Map<String, String> commitCourseSearchMy;
    /**
     * 班型课表参数
     */
    private TextView mTextSelectMonth, mTodayDateValue;
    private RelativeLayout mLastMonthView;
    private RelativeLayout mNextMonthView;
    private CalendarView mCalendarView;
    private List<String> mDatas;
    private List<String> mDatas1;
    //班型课表搜索条件
    private View view;
    private ListView mListView;

    private static final String TAG = "CourseFragmentMonth";
    private CourseSearchResultAdapter mAdapter;
    private List<Map<String, Object>> searchDataListMap;
    private List<Map<String, Object>> searchDataListMapfirstDay;
    private PullToRefreshScrollView pull_refresh_scrollview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.course_fragment_month_layout, container, false);
        ((BaseActivity) getActivity()).dialog.show();
        initView();
        requestSearchResult();
        return view;
    }

    /**
     * mainId=106     app登陆学员id
     * tableId=19     内容表id  固定为19
     * CT_ID=xxx      班型id  默认为接口返回第一个班型的id
     * NOW_BOUT=xxx   第几次课  默认为不限
     * CM_TYPE=xxx    班级类型 默认为不限
     * minDate=2017-01-01    起始时间 控件选择  默认值为本月第一天
     * maxDate=2017-01-31    结束时间 控件选择  默认值为本月最后一天
     */
    public void initView() {
        commitCourseSearchMy = new HashMap<>();
        commitCourseSearchMy.put(Constant.mainId, Constant.USERID);
        commitCourseSearchMy.put(Constant.tableId, 19 + "");
        Calendar cal;
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.getTime();
        commitCourseSearchMy.put("minDate", dateFormater.format(cal.getTime()) + "");
        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        commitCourseSearchMy.put("maxDate", dateFormater.format(cal.getTime()));
        mTextSelectMonth = (TextView) view.findViewById(R.id.txt_show_month);
        mTextSelectMonth.setText(cal.get(Calendar.YEAR) + "  " + (cal.get(Calendar.MONTH)+1) + "月");
        pull_refresh_scrollview = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
        //上拉监听函数
        pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pull_refresh_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                //执行刷新函数
                requestSearchResult();
            }
        });
        /**
         *
         * 课程表2初始化
         */
        mTodayDateValue = (TextView) view.findViewById(R.id.today_date_value);
        mLastMonthView = (RelativeLayout) view.findViewById(R.id.month_switch_left);
        mNextMonthView = (RelativeLayout) view.findViewById(R.id.month_switch_right);
        mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        mListView = (ListView) view.findViewById(android.R.id.list);
        View emptyView = view.findViewById(android.R.id.empty);
        mListView.setEmptyView(emptyView);
        searchDataListMapfirstDay = new ArrayList<>();
        mAdapter = new CourseSearchResultAdapter(searchDataListMapfirstDay, getActivity());
        mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListView);


        mLastMonthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendarView.setLastMonth();
                mTextSelectMonth.setText(mCalendarView.getDateTitle());
                commitCourseSearchMy.put("minDate", mCalendarView.getDate() + "-01");
                commitCourseSearchMy.put("maxDate", mCalendarView.getDate() + "-"+mCalendarView.getmaxDay());
                requestSearchResult();
            }
        });
        mNextMonthView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendarView.setNextMonth();
                commitCourseSearchMy.put("minDate", mCalendarView.getDate() + "-01");
                commitCourseSearchMy.put("maxDate", mCalendarView.getDate() + "-"+mCalendarView.getmaxDay());
                mTextSelectMonth.setText(mCalendarView.getDateTitle());
                requestSearchResult();
            }
        });
    }

    //请求搜索结果
    public void requestSearchResult() {
        if (((BaseActivity) getActivity()).hasInternetConnected()) {
            if (!pull_refresh_scrollview.isRefreshing()) {
                ((BaseActivity) getActivity()).dialog.show();
            }
            final String volleyUrl = Constant.sysUrl + StuPra.monthCourseCommitSearchUrl;
            Log.e(TAG, "学员端请求班型搜索结果地址： " + volleyUrl);
            Log.e(TAG, "学员端请求班型搜索结果参数:  " + commitCourseSearchMy.toString());
            //请求
            OkHttpUtils
                    .post()
                    .params(commitCourseSearchMy)
                    .url(volleyUrl)
                    .build()
                    .execute(new EdusStringCallback(getActivity()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext, e);
                            pull_refresh_scrollview.onRefreshComplete();
                            ((BaseActivity) getActivity()).dialog.dismiss();

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: " + "  id  " + response);
                            setStore(response);
                            pull_refresh_scrollview.onRefreshComplete();
                            ((BaseActivity) getActivity()).dialog.dismiss();

                        }
                    });
        } else {
            pull_refresh_scrollview.onRefreshComplete();
            ((BaseActivity) getActivity()).dialog.dismiss();
        }
    }

    private void setStore(String responseSearchResultData) {
        if (responseSearchResultData != null) {
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

    //课表2初始化数据
    private void initData() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < searchDataListMap.size(); i++) {
            long thisDate = Long.valueOf(String.valueOf(searchDataListMap.get(i).get("START_TIME")));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date dt = new Date(thisDate);
            mDatas.add(sdf.format(dt));
        }
        mDatas1 = new ArrayList<>();
        if (mDatas.size() > 0) {
            mDatas1.add(mDatas.get(0));
            Log.e(TAG, "initData:  mDatas.get(0) " + mDatas.get(0));
            mTodayDateValue.setText(mDatas.get(0));
        } else {
            mTodayDateValue.setText("本月无");
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
                if (month < 10) {
                    month1 = "0" + month;
                } else {
                    month1 = "" + month;
                }
                String day1;
                if (day < 10) {
                    day1 = "0" + day;
                } else {
                    day1 = "" + day;
                }
                String thisDay = year + "" + month1 + "" + day1;
                Log.e(TAG, "onClickDateListener: thisDay " + thisDay);
                mTodayDateValue.setText(thisDay);
                if (isHasString(thisDay, mDatas)) {

                    for (int i = 0; i < searchDataListMap.size(); i++) {
                        long thisDate = Long.valueOf(String.valueOf(searchDataListMap.get(i).get("START_TIME")));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        Date dt = new Date(thisDate);
                        Log.e(TAG, "onClickDateListener: sdf.format(dt)  " + sdf.format(dt));
                        if (thisDay.equals(sdf.format(dt))) {
                            Log.e(TAG, "onClickDateListener: 确实相等");
                            searchDataListMapfirstDay.add(searchDataListMap.get(i));
                        }
                    }
                    Log.e(TAG, "onClickDateListener: searchDataListMapfirstDay " + searchDataListMapfirstDay.toString());


                }

                mAdapter.notifyDataSetChanged();


                List<String> dates = mCalendarView.getSelectedDates();
                for (String date : dates) {
                    Log.e("test", "date: " + date);
                }
            }
        });

        mTextSelectMonth.setText(mCalendarView.getDateTitle());
        searchDataListMapfirstDay.clear();
        Log.e(TAG, "initData: searchDataListMapfirstDay初始化清空");
        //在此默认选择第一天有数据的课程列表
        if (mDatas.size() > 0) {

            Log.e(TAG, "initData: 监测点2");
            for (int i = 0; i < searchDataListMap.size(); i++) {
                long thisDate = Long.valueOf(String.valueOf(searchDataListMap.get(i).get("START_TIME")));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                Date dt = new Date(thisDate);

                if (mDatas.get(0).equals(sdf.format(dt))) {
                    searchDataListMapfirstDay.add(searchDataListMap.get(i));
                }
            }
            Log.e(TAG, "initData: 监测点1" + searchDataListMapfirstDay.toString());

        }
        mAdapter.notifyDataSetChanged();


    }

    public boolean isHasString(String xxStr, List<String> list) {

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
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
