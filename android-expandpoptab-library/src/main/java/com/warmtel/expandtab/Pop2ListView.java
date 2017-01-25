package com.warmtel.expandtab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

public class Pop2ListView extends RelativeLayout {
    private ListView mListView;
    private PopViewAdapter mAdapter;
    private OnSelectListener mOnSelectListener;
    private ExpandPopTabView mExpandPopTabView;
    private String mDefaultParentText = null;
    private String mDefaultParentkey = null;
    private int mDefaultIndex = 0;
    public interface OnSelectListener {
         void getValue(String key, String value, String id, String num);
    }

    public void setOnSelectListener(ExpandPopTabView expandPopTabView,OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
        mExpandPopTabView = expandPopTabView;
    }
public void refresh(){
    mAdapter.notifyDataSetChanged();

}
    public Pop2ListView(Context context) {
        super(context);
        init(context);
    }

    public Pop2ListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public Pop2ListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.expand_tab_popview1_layout, this, true);
       // setBackgroundResource(R.drawable.expand_tab_popview1_bg);
        setBackgroundResource(R.color.white);
        mListView = (ListView) findViewById(android.R.id.list);
        View emptyView = findViewById(android.R.id.empty);
        mListView.setEmptyView(emptyView);
        mAdapter = new PopViewAdapter(context);
        mAdapter.setTextSize(14);
        //mAdapter.setSelectorResId(R.drawable.expand_tab_popview1_select, R.drawable.expand_tab_popview2_chilred_item_selector);
        mAdapter.setSelectorResId(R.drawable.expand_tab_popview1_select_d, R.drawable.expand_tab_popview1_select_b);
        //mAdapter.setSelectorText();

        mListView.setAdapter(mAdapter);
        /**
         * mListView.setOnItemClickListener() 无响应，重新定义列表选项单击接口
         */

        mAdapter.setOnItemClickListener(new PopViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PopViewAdapter adapter, int position) {
                if (mOnSelectListener != null) {
                    KeyValueBean KeyValueBean = (KeyValueBean) adapter.getItem(position);
                    String showValue = KeyValueBean.getValue();
                    onSelectItemExandPopView(showValue);
                    mOnSelectListener.getValue(KeyValueBean.getKey(), showValue,KeyValueBean.getId(),KeyValueBean.getNum());
                }
            }
        });
    }

    /**
     * 关闭弹窗，显示选中项
     * @param showValue
     */
    public void onSelectItemExandPopView(String showValue){
        mExpandPopTabView.onExpandPopView();
        mExpandPopTabView.setToggleButtonTextByPosition(showValue,position2);
    }

    /**
     * 设置默认选中项通过内容
     * 注:在 setCallBackAndData()方法前执行有效
     * @param text1
     */
    public void setDefaultSelectByValue(String text1){
        mDefaultParentText = text1;
    }

    /**
     * 当班型选择的时候，更新第几次课为不限的选项
     */
    int position2=1;
    public void setReSelectByIndex(int index,int position) {
        if (mOnSelectListener != null) {
            position2=position;
            mAdapter.setSelectedPositionNotify(index);
            KeyValueBean KeyValueBean = (KeyValueBean) mAdapter.getItem(index);
            String showValue = KeyValueBean.getValue();
            onSelectItemExandPopView(showValue);

        }
    }


    public void setDefaultSelectByIndex(int index){
        mDefaultIndex = index;
    }
    /**
     * 设置默认选中项通过关键字Key
     * 注:在 setCallBackAndData()方法前执行有效
     * @param key1
     */
    public void setDefaultSelectByKey(String key1){
        mDefaultParentkey = key1;
    }
    public void setAdapterData(List<KeyValueBean> itemValues) {
        mAdapter.setList(itemValues);
    }

    public void setCallBackAndData(List<KeyValueBean> itemValues, ExpandPopTabView expandPopTabView, OnSelectListener selectListener) {
//        if(mDefaultParentText != null && !mDefaultParentText.equals("")){
//            mAdapter.setSelectorText(mDefaultParentText);
//        }else if(mDefaultParentkey != null && !mDefaultParentkey.equals("")) {
//                for (KeyValueBean keyValueBean : itemValues) {
//                    if (keyValueBean.getKey().equals(mDefaultParentkey)) {
//                        mAdapter.setSelectorText(keyValueBean.getValue());
//                        break;
//                    }
//                }
//            }else{
        if (itemValues.size()>0) {
            mAdapter.setSelectorText(itemValues.get(mDefaultIndex).getValue());
        }else{
            mAdapter.setSelectorText("");
        }
//        }

        mAdapter.setList(itemValues);
        mOnSelectListener = selectListener;
        mExpandPopTabView = expandPopTabView;
    }
}
