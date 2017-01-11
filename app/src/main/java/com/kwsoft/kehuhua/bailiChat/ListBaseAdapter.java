package com.kwsoft.kehuhua.bailiChat;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kwsoft.kehuhua.adcustom.R;

import java.util.List;
import java.util.Map;

import static com.kwsoft.version.StuPra.chanelLatestMsg;
import static com.kwsoft.version.StuPra.chanelLatestTime;
import static com.kwsoft.version.StuPra.channelImageUrl;
import static com.kwsoft.version.StuPra.customChannelName;
import static com.kwsoft.version.StuPra.defaultChannelName;


/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class ListBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private List<Map<String, Object>> mDatas;
    private Context mContext;
    public OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public ListBaseAdapter(List<Map<String, Object>> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 加载数据item的布局，生成VH返回
        View v = LayoutInflater.from(mContext).inflate(R.layout.chat_conversation_list_item, parent, false);
        v.setOnClickListener(this);
        return new ComViewHolder(v);
    }

    private static final String TAG = "ListBaseAdapter";

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder thisholder, int position) {
        if (thisholder instanceof ComViewHolder) {
            final ComViewHolder holder = (ComViewHolder) thisholder;
            Map<String, Object> map = getData(position);
            //会话图片
            String channelImage1 = String.valueOf(map.get(channelImageUrl));
            String channelImage = "http://bailitianxia-edus.oss-cn-beijing.aliyuncs.com/2016-12-23/8765f3d3572c11df71c5ef7d652762d0f603c291.jpg?Expires=1482634687&OSSAccessKeyId=vGYn0xGpshJEzwyt&Signature=RULDLA7NIUCDQZRQuNXzcDWsTQU%3D";

            Log.e(TAG, "onBindViewHolder: channelImage "+channelImage);
            Uri uri;
            if (!channelImage.equals("")&&!channelImage.equals("null")) {
                uri = Uri.parse(channelImage);
            }else{
                uri = Uri.parse("asset:///chat_default_group_image.png");
            }
            holder.group_image.setImageURI(uri);
//会话对象名称
            String channelName = String.valueOf(map.get(customChannelName));
            String defaultName = String.valueOf(map.get(defaultChannelName));
            holder.tv_title.setText(!channelName.equals("null") ? channelName : defaultName);
//最后一次的聊天内容，暂时取的全部聊天内容
            String latestMsg = String.valueOf(map.get(chanelLatestMsg));
            holder.tv_psn_name.setText(!latestMsg.equals("null") ? latestMsg : "");
//最后一次聊天的时间
            String latestTime = String.valueOf(map.get(chanelLatestTime));
            holder.tv_time.setText(!latestTime.equals("null") ? latestTime : "");
//设置tag
            holder.itemView.setTag(map);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view, JSON.toJSONString(view.getTag()));
        }
    }

    // 可复用的VH
    class ComViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title;
        public TextView tv_psn_name, tv_time;
        SimpleDraweeView group_image;

        public ComViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_psn_name = (TextView) itemView.findViewById(R.id.tv_psn_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            group_image=(SimpleDraweeView) itemView.findViewById(R.id.group_image);
        }
    }


    /**
     * 获取单项数据
     */

    private Map<String, Object> getData(int position) {

        return mDatas.get(position);
    }

    /**
     * 获取全部数据
     */
    public List<Map<String, Object>> getDatas() {

        return mDatas;
    }

    /**
     * 清除数据
     */
    public void clearData() {

        mDatas.clear();
        notifyItemRangeRemoved(0, mDatas.size());
    }

}
