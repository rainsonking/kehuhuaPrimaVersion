package com.kwsoft.version.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.version.StuMainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/6 0006.
 *
 */
public class AllCourseFragment extends BaseFragment implements View.OnClickListener {

    private List<Fragment> newsList = new ArrayList<Fragment>();
    private EdusViewPager viewPager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_course, container, false);
        viewPager = (EdusViewPager) view.findViewById(R.id.one_course);
        initCourseFragment();
        try {

            ((StuMainActivity) getActivity()).mToolbar.getRadio().setOnCheckedChangeListener(
                    new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            if (i==radioGroup.getChildAt(0).getId()) {
                                viewPager.setCurrentItem(0);
                            }else if(i==radioGroup.getChildAt(1).getId()){
                                viewPager.setCurrentItem(1);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    FragAdapter adapter;
    public void initCourseFragment() {

        Fragment classTypeCourseFragment = new ClassTypeCourseFragment();
        Fragment courseFragment = new CourseFragment();

        newsList.add(courseFragment);
        newsList.add(classTypeCourseFragment);

        //设置viewpager适配器

        adapter = new FragAdapter(getActivity().getSupportFragmentManager(),newsList);
        viewPager.setAdapter(adapter);
        //两个viewpager切换不重新加载
        viewPager.setOffscreenPageLimit(2);
        //设置默认
        viewPager.setCurrentItem(0);
        viewPager.setScanScroll(false);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    @Override
    public void onClick(View v) {

    }

    public class FragAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public FragAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            // TODO Auto-generated constructor stub
            mFragments=fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            return mFragments.get(arg0);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mFragments.size();
        }

    }
}
