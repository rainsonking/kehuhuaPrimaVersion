package com.kwsoft.kehuhua.hampson.activity;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import me.relex.photodraweeview.PhotoDraweeView;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

/**
 * Created by Administrator on 2016/11/14 0014.
 *
 *
 */

public class ZoomImageActivity extends BaseActivity{
    String url;
    private CommonToolbar mToolbar;
    PhotoDraweeView my_image_view;
    private static final String TAG = "ZoomImageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image_layout);
        getInfoData();
        initView();
    }

    private void getInfoData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
//            fileName= bundle.getString("fileName");
//            prefix= bundle.getString("fileEnd");
            Log.e(TAG, "getInfoData: 图片放大之后的url "+url);

        }

    }

    @Override
    public void initView() {
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle("查看大图");
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        my_image_view=(PhotoDraweeView)findViewById(R.id.photo_drawee_view);
        Uri uri = Uri.parse(url);


        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setUri(uri);
        controller.setOldController(my_image_view.getController());
// You need setControllerListener
        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null || my_image_view == null) {
                    return;
                }
                my_image_view.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        });
        my_image_view.setController(controller.build());
//
//        ImageLoadingDrawable imageLoadingDrawable = new ImageLoadingDrawable();
//        GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(getResources());
//        hierarchyBuilder
//                .setProgressBarImage(imageLoadingDrawable)
//                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
//                .setFailureImage(getResources().getDrawable(R.mipmap.jiazaishibai));
//        my_image_view.setHierarchy(hierarchyBuilder.build());


    }
}
