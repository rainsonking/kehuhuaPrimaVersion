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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Pop3Layout extends RelativeLayout {
    public TextView date_start,date_end;
   public Button course_third_reset,course_third_commit;
    Context context;
   public String whichType="";

    RadioGroup class_type_day_area_select_rg;
    private static final String TAG = "Pop3Layout";


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
    private String typeId="";
    private void init() {
        LayoutInflater.from(context).inflate(R.layout.expand_tab_popview3_layout, this, true);
       // setBackgroundResource(R.drawable.expand_tab_popview1_bg);
        setBackgroundResource(R.color.white);
        date_start= (TextView) findViewById(R.id.date_start);
        date_end= (TextView) findViewById(R.id.date_end);
        course_third_reset= (Button) findViewById(R.id.course_third_reset);
        course_third_commit= (Button) findViewById(R.id.course_third_commit);
        class_type_day_area_select_rg=(RadioGroup) findViewById(R.id.class_type_day_area_select_rg);

        class_type_day_area_select_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.e(TAG, "onCheckedChanged: 所选第几个 "+i);
                if(i==radioGroup.getChildAt(1).getId()){
                    typeId="366";
                }else if(i==radioGroup.getChildAt(2).getId()){
                    typeId="576";
                }else if(i==radioGroup.getChildAt(3).getId()){
                    typeId="365";
                }else{
                    typeId="";
                }
            }
        });

        setDefault();//设定默认

        date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String month=String.valueOf(monthOfYear+1);
                        if ((monthOfYear+1)<10) {
                            month="0"+String.valueOf(monthOfYear+1);
                        }
                        String day=String.valueOf(dayOfMonth);
                        if (dayOfMonth<10) {
                            day="0"+String.valueOf(dayOfMonth);
                        }

                        date_start.setText(year+"-"+month+"-"+day);
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
                       String month=String.valueOf(monthOfYear+1);
                        if ((monthOfYear+1)<10) {
                            month="0"+String.valueOf(monthOfYear+1);
                        }
                        String day=String.valueOf(dayOfMonth);
                        if (dayOfMonth<10) {
                            day="0"+String.valueOf(dayOfMonth);
                        }
                        date_end.setText(year+"-"+month+"-"+day);
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
                if (onDateSelectListener != null) {

                    onDateSelectListener.getValue(typeId,date_start.getText().toString(),date_end.getText().toString());
                }
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


    public String getWhich(){
        return whichType;
    }


    private Pop3Layout.OnDateSelectListener onDateSelectListener;
    public interface OnDateSelectListener {
        void getValue(String typeId, String startTime, String endTime);
    }

    public void setCallBackAndData(Pop3Layout.OnDateSelectListener onDateSelectListener1) {
        onDateSelectListener = onDateSelectListener1;
    }
}
