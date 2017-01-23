package com.warmtel.expandtab;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Pop3Layout extends RelativeLayout {
   public Button date_start,date_end,course_third_reset,course_third_commit;
    Context context;
   public String whichType="";

    RadioGroup class_type_day_area_select_rg;
    private static final String TAG = "Pop3Layout";
    
    
    
    public interface OnSelectListener {
        void getValue(String showText, String parentKey, String childrenKey);
    }

    public Pop3Layout(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public Pop3Layout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }
    Calendar cal;
    private void init() {
        LayoutInflater.from(context).inflate(R.layout.expand_tab_popview3_layout, this, true);
        setBackgroundResource(R.drawable.expand_tab_popview1_bg);
        date_start= (Button) findViewById(R.id.date_start);
        date_end= (Button) findViewById(R.id.date_end);
        course_third_reset= (Button) findViewById(R.id.course_third_reset);
        course_third_commit= (Button) findViewById(R.id.course_third_commit);
        class_type_day_area_select_rg=(RadioGroup) findViewById(R.id.class_type_day_area_select_rg);

        class_type_day_area_select_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.e(TAG, "onCheckedChanged: 所选第几个 "+i);
//                whichType;
            }
        });

        setDefault();//设定默认

        date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date_start.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });


        date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date_end.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        course_third_reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefault();
            }
        });

        course_third_commit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommit();
            }
        });
    }

    public void setDefault(){

        //设置按钮默认选择不限
        ((RadioButton) class_type_day_area_select_rg.findViewById(R.id.day_area_limitless)).setChecked(true);

        //设定date_start为本月第一天
        SimpleDateFormat dateFormater = new SimpleDateFormat(
                "yyyy-MM-dd");
        cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.getTime();

        date_start.setText(dateFormater.format(cal.getTime()) + "");
        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        date_end.setText(dateFormater.format(cal.getTime()));
    }

    public void setCommit(){
//获取班级类别


    }

    public String getWhich(){
        return whichType;
    }
}
