package com.kwsoft.version.fragment;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/1/22 0022.
 *
 */

public class EdusViewPager  extends ViewPager {
    private boolean isCanScroll = true;
    public EdusViewPager(Context context) {
        super(context);
    }
    public EdusViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setScanScroll(boolean isCanScroll) {//设置方法
        this.isCanScroll = isCanScroll;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if(isCanScroll){
            //setScanScroll设为true可以左右滑动
            return super.onInterceptTouchEvent(arg0);
        }else{
            //setScanScroll设为false则不能左右滑动
            return false;
        }
    }
}
