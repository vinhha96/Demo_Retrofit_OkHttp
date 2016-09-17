package vn.com.vng.zalopay.utils;

import android.content.Context;
import android.support.annotation.DrawableRes;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by AnhHieu on 9/15/16.
 * *
 */

public class FrescoImageLoader implements ImageLoader<SimpleDraweeView> {

    final Context context;

    public FrescoImageLoader(Context context) {
        this.context = context;
    }

    @Override
    public void loadImage(SimpleDraweeView target, String url, @DrawableRes int placeHolder, @DrawableRes int error, ScaleType scaleType) {
        GenericDraweeHierarchy hierarchy = target.getHierarchy();
        hierarchy.setPlaceholderImage(placeHolder);
        hierarchy.setFailureImage(error);

        if (ScaleType.CENTER_CROP == scaleType) {
        } else if (ScaleType.FIT_CENTER == scaleType) {
        }

        target.setImageURI(url);
    }

    @Override
    public void loadImage(SimpleDraweeView target, @DrawableRes int resourceId) {

    }

    @Override
    public void loadImage(SimpleDraweeView target, String url) {
        target.setImageURI(url);
    }
}
