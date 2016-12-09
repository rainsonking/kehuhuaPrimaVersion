package com.kwsoft.kehuhua.hampson.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.hampson.activity.ZoomImageActivity;
import com.kwsoft.kehuhua.utils.NoDoubleClickListener;
import com.kwsoft.kehuhua.view.ImageLoadingDrawable;

import java.util.List;


/**
 * Created by Administrator on 2016/11/14 0014.
 */

public class ZuoYeImageGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<String> datas;


    public ZuoYeImageGridViewAdapter(Context context, List<String> datas, List<String> fileNames) {
        this.context = context;
        this.datas = datas;
        List<String> fileNames1 = fileNames;


    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context).inflate(R.layout.hampson_zuoye_item_image_layout, null);
        SimpleDraweeView my_image_view = (SimpleDraweeView) view.findViewById(R.id.my_image_view);
        my_image_view.setVisibility(View.VISIBLE);
//          final String prefix=fileNames.get(i).substring(fileNames.get(i).lastIndexOf(".")+1);

        final String url = datas.get(i);
        Uri uri = Uri.parse(url);

        ImageLoadingDrawable imageLoadingDrawable = new ImageLoadingDrawable();
        GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(context.getResources());
        hierarchyBuilder
                .setProgressBarImage(imageLoadingDrawable)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
                .setFailureImage(context.getResources().getDrawable(R.mipmap.jiazaishibai));
        my_image_view.setHierarchy(hierarchyBuilder.build());
        my_image_view.setImageURI(uri);


        my_image_view.setOnClickListener(new NoDoubleClickListener() {//禁止急速连续点击
            @Override
            public void onNoDoubleClick(View view) {
                Intent intent = new Intent(context, ZoomImageActivity.class);
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        });
        return view;
    }
}