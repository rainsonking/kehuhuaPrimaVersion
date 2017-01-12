package com.kwsoft.version.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.version.view.AsortGridView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class AssortFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private AsortGridView homeGridView;
    private List<Map<String, Object>> menuListMap = new ArrayList<>();
    private SimpleAdapter adapter;
    private List<Map<String, Object>> menuListAll;


    //下拉刷新handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x101:
                    Log.e("TAG", "学员端开始handler通知跳转后 ");
                    if (swipeRefreshLayout.isRefreshing()) {
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);//设置不刷新
                        Toast.makeText(getActivity(), "数据已刷新", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0x102:
                    Log.e("TAG", "学员端开始handler通知跳转后 ");
                    if (swipeRefreshLayout.isRefreshing()) {
                        adapter = new SimpleAdapter(getActivity(), menuListMap,
                                R.layout.fragment_assort_item, new String[]{"image", "menuName"},
                                new int[]{R.id.iv_item, R.id.tv_item});
                        homeGridView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);//设置不刷新

                        Toast.makeText(getActivity(), "数据已刷新", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assort, container, false);
        initView(view);
        getIntentData();
        homeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataProcess.toList(getActivity(), menuListMap.get(position));

            }
        });
        ButterKnife.bind(this, view);
        return view;
    }

    private void initView(View v) {
//        TextView tvTitle = (TextView) v.findViewById(R.id.textView);
//        tvTitle.setText("分类");
        homeGridView = (AsortGridView) v.findViewById(R.id.stu_home_grid);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new LoadDataThread().start();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // new LoadDataThread().start();
    }


    /**
     * 加载菜单数据的线程
     */
    class LoadDataThread extends Thread {
        @Override
        public void run() {
            //下载数据，重新设定dataList
            postLogin();
            //防止数据加载过快动画效果差
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (menuListMap != null && menuListMap.size() > 0) {
                Log.e("TAG", "学员端开始handler通知 ");
                handler.sendEmptyMessage(0x101);//通过handler发送一个更新数据的标记，适配器进行dataSetChange，然后停止刷新动画
            } else {
                handler.sendEmptyMessage(0x102);
            }
        }
    }


    /**
     * 获取mainActivity传递的菜单menuList数据
     */
    private void getIntentData() {
        Bundle menuBundle = getArguments();
        String menuStr = menuBundle.getString("menuDataMap");
        if (menuStr != null && menuStr.length() > 0) {
            menuListAll = JSON.parseObject(menuStr,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            menuListMap = DataProcess.toStuParentList(menuListAll);

            Log.e("TAG", "menuListMap初始值 " + menuListMap.toString());
            adapter = new SimpleAdapter(getActivity(), menuListMap,
                    R.layout.fragment_assort_item, new String[]{"image", "menuName"},
                    new int[]{R.id.iv_item, R.id.tv_item});
            homeGridView.setAdapter(adapter);
            homeGridView.setOnScrollListener(new SwipeListViewOnScrollListener(swipeRefreshLayout));
        } else {
            Toast.makeText(getActivity(), "暂无数据", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

//    public void onRefreshClick() {
//        new LoadDataThread().start();
//    }


    /**
     * 刷新菜单方法，单独获取菜单
     */
    private static final String TAG = "AssortFragment";

    public void postLogin() {
        if (!((BaseActivity) getActivity()).hasInternetConnected()) {
            Looper.prepare();
            Toast.makeText(getActivity(), "当前网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
            Looper.loop();
            swipeRefreshLayout.setRefreshing(false);//直接设置不刷新
        } else {
            final String volleyUrl = Constant.sysUrl + Constant.projectLoginUrl;
            Log.e("TAG", "学员端登陆地址 " + Constant.sysUrl + Constant.projectLoginUrl);

            //参数
            Map<String, String> map = new HashMap<>();
            map.put(Constant.USER_NAME, Constant.USERNAME_ALL);
            map.put(Constant.PASSWORD, Constant.PASSWORD_ALL);
            map.put(Constant.proIdName, Constant.proId);
            map.put(Constant.timeName, Constant.menuAlterTime);
            map.put(Constant.sourceName, Constant.sourceInt);
            //请求
            OkHttpUtils
                    .post()
                    .params(map)
                    .url(volleyUrl)
                    .build()
                    .execute(new EdusStringCallback(getActivity()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext, e);
                            ((BaseActivity) getActivity()).dialog.dismiss();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: " + "  id  " + id);
                            check(response);
                        }
                    });
        }

    }


    /**
     * 解析菜单数据
     */

    @SuppressWarnings("unchecked")
    private void check(String menuData) {
        Map<String, Object> stuMenuMap = Utils.str2map(menuData);
        if (stuMenuMap.containsKey("menuList")) {
            menuListAll = (List<Map<String, Object>>) stuMenuMap.get("menuList");
            Log.e("TAG", "sessionId=" + menuListAll.toString());
            menuListMap.removeAll(menuListMap);
            if (menuListAll != null && menuListAll.size() > 0) {
                menuListMap.addAll(DataProcess.toStuParentList(menuListAll));
                Log.e("TAG", "刷新后的父类菜单数据=" + menuListMap.toString());
            }
        }
//        int leg = menuListMap.size() % 3;
//        if (leg == 1) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("image", "");
//            map.put("menuName", "");
//            menuListMap.add(map);
//            menuListMap.add(map);
//        } else if (leg == 2) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("image", "");
//            map.put("menuName", "");
//            menuListMap.add(map);
//        }
    }


    /**
     * 用于解决ListView与下拉刷新的Scroll事件冲突
     */
    public static class SwipeListViewOnScrollListener implements AbsListView.OnScrollListener {

        private SwipeRefreshLayout mSwipeView;
        private AbsListView.OnScrollListener mOnScrollListener;

        SwipeListViewOnScrollListener(SwipeRefreshLayout swipeView) {
            mSwipeView = swipeView;
        }

        public SwipeListViewOnScrollListener(SwipeRefreshLayout swipeView,
                                             AbsListView.OnScrollListener onScrollListener) {
            mSwipeView = swipeView;
            mOnScrollListener = onScrollListener;
        }

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            View firstView = absListView.getChildAt(firstVisibleItem);

            // 当firstVisibleItem是第0位。如果firstView==null说明列表为空，需要刷新;或者top==0说明已经到达列表顶部, 也需要刷新
            if (firstVisibleItem == 0 && (firstView == null || firstView.getTop() == 0)) {
                mSwipeView.setEnabled(true);
            } else {
                mSwipeView.setEnabled(false);
            }
            if (null != mOnScrollListener) {
                mOnScrollListener.onScroll(absListView, firstVisibleItem,
                        visibleItemCount, totalItemCount);
            }
        }
    }
}
