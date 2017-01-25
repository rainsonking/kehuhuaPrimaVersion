package com.kwsoft.version.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kwsoft.version.StuPra.diJiCiKe;

/**
 * Created by Administrator on 2016/10/19 0019.
 *
 */

public class CourseSearchResultAdapter extends BaseAdapter {

    public List<Map<String, Object>> SearchResultList = new ArrayList<>();
    public Context mcontext;
    public LayoutInflater mInflater;
    public CourseSearchResultAdapter(List<Map<String, Object>> SearchResultList, Context mcontext) {
        this.SearchResultList = SearchResultList;
        this.mcontext = mcontext;
        this.mInflater = LayoutInflater.from(mcontext);

    }

    @Override
    public int getCount() {
        return SearchResultList.size();
    }

    @Override
    public Object getItem(int position) {
        return SearchResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.fragment_course_search_result_item, null);

            viewHolder = new ViewHolder();
            viewHolder.start_time = (TextView)convertView.findViewById(R.id.start_time);
            viewHolder.end_time = (TextView)convertView.findViewById(R.id.end_time);
            viewHolder.class_num = (TextView)convertView.findViewById(R.id.class_num);
            viewHolder.type_value = (TextView)convertView.findViewById(R.id.type_value);
            viewHolder.course_name = (TextView)convertView.findViewById(R.id.course_name);
            viewHolder.which_class = (TextView)convertView.findViewById(R.id.which_class);
            viewHolder.teacher_name = (TextView)convertView.findViewById(R.id.teacher_name);
            viewHolder.school_area = (TextView)convertView.findViewById(R.id.school_area);
            viewHolder.class_room = (TextView)convertView.findViewById(R.id.class_room);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Map<String, Object> map=SearchResultList.get(position);
        long thisDate1 =Long.valueOf(String.valueOf(String.valueOf(map.get("START_TIME"))));
        long thisDate2 =Long.valueOf(String.valueOf(String.valueOf(map.get("END_TIME"))));
        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm");
        Date dt1 = new Date(thisDate1);
        Date dt2 = new Date(thisDate2);

        viewHolder.start_time.setText(sdf.format(dt1));
        viewHolder.end_time.setText(sdf.format(dt2));

        Map<String,String> courseInfo= (Map<String, String>) map.get("courseInfo");
        viewHolder.class_num.setText(courseInfo.get("classNum"));
        String classTimeType;
        switch (String.valueOf(map.get("CM_TYPE"))){
            case "366":
                classTimeType="白班";
                break;
            case "365":
                classTimeType="周末班";
                break;
            case "576":
                classTimeType="晚班";
                break;
            default:
                classTimeType="无上课时间类型";
                break;
        }
        viewHolder.type_value.setText(classTimeType);
        viewHolder.course_name.setText(courseInfo.get("courseName"));
        viewHolder.which_class.setText("第"+diJiCiKe+"次课");
        viewHolder.teacher_name.setText(courseInfo.get("teacherName"));
        viewHolder.school_area.setText(String.valueOf(map.get("OS_NAME")));
        viewHolder.class_room.setText(String.valueOf(map.get("CLASSROOM_NAME")).equals("null")?"未排教室":String.valueOf(map.get("CLASSROOM_NAME")));
        return convertView;
    }

    public class ViewHolder{
        TextView start_time,end_time,
                class_num,type_value,course_name,
                which_class,teacher_name,school_area,class_room;
    }
}
