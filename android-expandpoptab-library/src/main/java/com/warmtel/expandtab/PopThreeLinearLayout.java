package com.warmtel.expandtab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class PopThreeLinearLayout extends RelativeLayout {


    public interface OnSelectListener {
        void getValue(String showText, String parentKey, String childrenKey);
    }

    public PopThreeLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public PopThreeLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.expand_tab_popview3_layout, this, true);
        setBackgroundResource(R.drawable.expand_tab_popview1_bg);
    }

}
