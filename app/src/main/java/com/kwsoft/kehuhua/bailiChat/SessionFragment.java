package com.kwsoft.kehuhua.bailiChat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.kwsoft.version.StuPra.channelListPageId;
import static com.kwsoft.version.StuPra.channelListTableId;
import static com.kwsoft.version.StuPra.customChannelName;
import static com.kwsoft.version.StuPra.defaultChannelName;

/**
 * Created by Administrator on 2016/12/12 0012.
 *
 */

public class SessionFragment extends Fragment {
    private static final String TAG = "SessionFragment";


    private String tableId, pageId;


    private int totalNum = 0;
    private int start = 0;
    private final int limit = 200;
    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
//    private static final int STATE_MORE = 2;
    private int state = STATE_NORMAL;
    private RecyclerView mRecyclerView;
    private MaterialRefreshLayout mRefreshLayout;
    private CommonToolbar mToolbar;

    private List<Map<String, Object>> datas = new ArrayList<>();
    private ListBaseAdapter adapter;
    private List<Map<String, Object>> childDatas = new ArrayList<>();
    private SimpleDraweeView sysMessage;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment_session, container, false);
        tableId = "17796";
        initView(view);
        initRefreshLayout();
        return view;
    }

    public void initView(View view) {
//        sysMessage=(SimpleDraweeView) view.findViewById(R.id.sys_message_img);
//        Uri uri = Uri.parse("asset:///chat_system_message.png");
//        sysMessage.setImageURI(uri);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.lv);
        mRefreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh_layout);
//        mToolbar = (CommonToolbar) view.findViewById(R.id.common_toolbar);
//        mToolbar.setTitle("会话列表");
//        mToolbar.hideLeftImageButton();
//
//        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.ic_add_pic));
//        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), AddConvActivity.class);
//                startActivity(intent);
//            }
//        });

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
//        mRecyclerView.addItemDecoration(new RecyclerViewDivider(
//                     getActivity(), LinearLayoutManager.VERTICAL, 10, ContextCompat.getColor(getActivity(), R.color.red)));
    }

    @Override
    public void onResume() {
        super.onResume();
        datas.clear();
        childDatas.clear();
        requestData();
    }

    private void initRefreshLayout() {
//        mRefreshLayout.setLoadMore(true);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                datas.clear();
                childDatas.clear();
                refreshData();
            }

//            @Override
//            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
//                if (adapter != null && adapter.getItemCount() < totalNum) {
//                    Log.e(TAG, "onRefreshLoadMore: " + adapter.getItemCount());
//                    loadMoreData();
//                } else {
//                    Snackbar.make(mRecyclerView, "没有更多了", Snackbar.LENGTH_SHORT).show();
//                    mRefreshLayout.finishRefreshLoadMore();
//                }
//            }
        });
    }


    /**
     * 下拉刷新方法
     */
    private void refreshData() {
        start = 0;
        state = STATE_REFREH;
        requestData();
    }

//    /**
//     * 上拉加载方法
//     */
//    private void loadMoreData() {
//        start += limit;
//        state = STATE_MORE;
//        requestData();
//    }

    /**
     * 分动作展示数据
     */
    private void showData() {
        Log.e(TAG, "showData: " + state);
        switch (state) {
            case STATE_NORMAL:
                normalRequest();
                break;
            case STATE_REFREH:

                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(0);
                    mRefreshLayout.finishRefresh();
//                    if (datas.size() == 0) {
//                        Snackbar.make(mRecyclerView, "本页无数据", Snackbar.LENGTH_SHORT).show();
//                    } else {
//                        Log.e(TAG, "showData: 执行了共x条");
//                        Snackbar.make(mRecyclerView, "共" + totalNum + "条", Snackbar.LENGTH_SHORT).show();
//                    }

                }

                break;
//            case STATE_MORE:
//                if (adapter != null) {
//                    //  adapter.addData(adapter.getDatas().size(), datas, childTab);
//                    datas.addAll(childDatas);
//                    adapter.notifyDataSetChanged();
//                    mRecyclerView.scrollToPosition(adapter.getDatas().size());
//                    mRefreshLayout.finishRefreshLoadMore();
//                    Snackbar.make(mRecyclerView, "更新了" + childDatas.size() + "条数据", Snackbar.LENGTH_SHORT).show();
//                }
//                break;
        }
    }

    public void requestData() {
        // if (hasInternetConnected()) {
        //地址
        String volleyUrl = Constant.sysUrl + Constant.requestListSet;
        Log.e("TAG", "学员端登陆地址 " + Constant.sysUrl + Constant.requestListSet);
        //参数
        Map<String, String> map = new HashMap<>();
        map.put(Constant.tableId, channelListTableId);
        map.put(Constant.pageId, channelListPageId);
        map.put(Constant.start, start + "");
        map.put(Constant.limit, limit + "");
        Log.e("TAG", "学员端登陆map " + map.toString());
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
                        mRefreshLayout.finishRefresh();
//                        ((BaseActivity) getActivity()).dialog.dismiss();
//                        backStart();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("", "onResponse: " + "  id  " + response);
                        if (!response.equals("")) {
                            check(response);
                        }else{
                            Toast.makeText(getActivity(), "无会话数据", Toast.LENGTH_SHORT).show();
                            mRefreshLayout.finishRefresh();
                        }

                    }
                });
//        } else {
////            ((BaseActivity) getActivity()).dialog.dismiss();
//            mRefreshLayout.finishRefresh();
//            Toast.makeText(getActivity(), "请连接网络", Toast.LENGTH_SHORT).show();
//            backStart();
//        }
    }

//    public void backStart() {
//
//        //下拉失败后需要将加上limit的strat返还给原来的start，否则会获取不到数据
//        if (state == STATE_MORE) {
//            //start只能是limit的整数倍
//            if (start > limit) {
//                start -= limit;
//            }
//            mRefreshLayout.finishRefreshLoadMore();
//        }
//    }

    public void check(String menuData) {
        Map<String, Object> menuMap = JSON.parseObject(menuData,
                new TypeReference<Map<String, Object>>() {
                });
        List<Map<String, Object>> menuListMap2 = null;
        if (menuMap.containsKey("dataList")) {
            menuListMap2 = (List<Map<String, Object>>) menuMap.get("dataList");
            Log.e("menuListMap2", JSON.toJSONString(menuListMap2));
        }
        childDatas.clear();
        totalNum = Integer.parseInt(menuMap.get("dataCount") + "");
        Log.e("menuListMap2num", menuMap.get("dataCount") + "");
        if (menuListMap2 != null && menuListMap2.size() > 0) {
            for (int i = 0; i < menuListMap2.size(); i++) {
                Map<String, Object> map = menuListMap2.get(i);
                if (start > 0) {
                    childDatas.add(map);
                } else {
                    datas.add(map);
                }
            }
        }
        showData();
    }

    private void normalRequest() {
        adapter = new ListBaseAdapter(datas, getActivity());
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        //  mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL));
        // mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//               mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
//        mRecyclerView.addItemDecoration(new RecyclerViewDivider(
//                getActivity(), LinearLayoutManager.VERTICAL, 10, ContextCompat.getColor(getActivity(), R.color.red)));

        adapter.setOnItemClickListener(new ListBaseAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                Log.e("TAG", "data " + data);
                // Toast.makeText(ConvListActivity.this, data, Toast.LENGTH_SHORT).show();
                toItem(data);
            }
        });
        //((BaseActivity) getActivity()).dialog.dismiss();
    }

    private void toItem(String data) {
        Map<String, Object> menuMap = JSON.parseObject(data,
                new TypeReference<Map<String, Object>>() {
                });
//        Log.e(TAG, "onItemClick: CHANNELID" + menuMap.get("CHANNELID").toString());
//        Log.e(TAG, "onItemClick: CHANNELNAME" + menuMap.get("CHANNELNAME").toString());
        Intent intent = new Intent(getActivity(), ChatGoActivity.class);
        intent.putExtra("dataId", String.valueOf(menuMap.get("CHANNELID")));
        String channelName = String.valueOf(menuMap.get(customChannelName));
        String defaultName = String.valueOf(menuMap.get(defaultChannelName));
        intent.putExtra("channelName", channelName.equals("null")?defaultName:channelName);
        Log.e(TAG, "toItem: CHANNELID "+String.valueOf(menuMap.get("CHANNELID")));
        Log.e(TAG, "toItem: CHANNELNAME "+String.valueOf(menuMap.get("CHANNELNAME")));
        startActivity(intent);
    }


//    public void requestData() {
//       // lists.clear();
//        String volleyUrl = Constant.sysUrl + Constant.requestListSet;
//        Log.e(TAG, "学员端登陆地址 " + Constant.sysUrl + Constant.requestListSet);
//        //参数
//        Map<String, String> map = new HashMap<>();
//        map.put(Constant.tableId, tableId);
//        map.put(Constant.pageId, "7686");
//        map.put(Constant.start, start+"");
//        map.put(Constant.limit, limit+"");
//
//
//        Log.e(TAG, "maplogi/" + map.toString());
//        //请求
//        OkHttpUtils
//                .post()
//                .params(map)
//                .url(volleyUrl)
//                .build()
//                .execute(new EdusStringCallback(getActivity()) {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        ErrorToast.errorToast(mContext, e);
//                        //        dialog.dismiss();
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        Log.e(TAG, "onResponse: " + "  id  " + response);
//                        check(response);
//
//                    }
//                });
//    }

//    public void check(String menuData) {
//        Log.e(TAG, "check: menuData "+menuData);
//        try {
//            Map<String, Object> menuMap = JSON.parseObject(menuData,
//                    new TypeReference<Map<String, Object>>() {
//                    });
//            List<Map<String, Object>> menuListMap2 = null;
//            if (menuMap.containsKey("dataList")) {
//                menuListMap2 = (List<Map<String, Object>>) menuMap.get("dataList");
//                Log.e("menuListMap2", JSON.toJSONString(menuListMap2));
//            }
//
//            if (menuListMap2 != null && menuListMap2.size() > 0) {
//                for (int i = 0; i < menuListMap2.size(); i++) {
//                    Map<String, Object> map = menuListMap2.get(i);
//                  //  lists.add(map);
//                }
//            }
//
//            setAdapter();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void setAdapter() {
//     //   adapter = new ConvListBaseAdapter(getActivity(),lists);
////        lv_view.setAdapter(adapter);
////        lv_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                Log.e(TAG, "onItemClick: " + i + "/" + lists.get(i).get("T_17796_0").toString());
////                Intent intent = new Intent(getActivity(), ChatActivity.class);
////                intent.putExtra("tableId", lists.get(i).get("T_17796_0").toString());
////                startActivity(intent);
////            }
////        });
//    }
}
