package com.kwsoft.kehuhua.fragments;

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

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.widget.CalendarView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/20 0020.
 */

public class MonthlyCalendarFragment extends Fragment {
    private TextView mTextSelectMonth;
    private ImageButton mLastMonthView;
    private ImageButton mNextMonthView;
    private CalendarView mCalendarView;

    private List<String> mDatas;
    private List<String> mDatas1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_month_course,container,false);
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
        return  view;
    }
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

}
