package org.seiko.panc.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import org.seiko.panc.bean.SectionBean;
import org.seiko.panc.ui.section.SectionCallBack;
import org.seiko.panc.wiget.TextDrawable;

/**
 * Created by Seiko on 2017/3/26. Y
 */

public class ImageLoader {


    public static void load(Context context, ImageView iv, String url) {
        load(context, iv, url, null);
    }

    public static void load(Context context, ImageView iv, String url, String ref) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        getRequest(context, url, ref)
                .dontAnimate()
                .dontTransform()
                .into(iv);
    }

    //===============================================================
    /** section专用 */
    public static void load2(Context context, final ImageView iv, SectionBean bean, final String ref) {
        if (TextUtils.isEmpty(bean.getUrl())) {
            return;
        }
        TextDrawable drawable = getTextDrawable((bean.getIndex() + 1));
        iv.setImageDrawable(drawable);

        getRequest(context, bean.getUrl(), ref)
                .asBitmap()
                .placeholder(drawable)
                .error(getError())
                .dontAnimate()
                .dontTransform()
                .into(iv);
    }

    //===============================================================
    /** 生成请求 */
    private static DrawableTypeRequest getRequest(Context context, String url, String ref) {
        //本地
        if (url.indexOf("/Panc/") > 0) {
            return Glide.with(context).load(url);
        }

        //网络
        LazyHeaders.Builder builder = new LazyHeaders.Builder();
        if (ref != null) {
            builder.addHeader("Referer", ref);
//            builder.addHeader("Cookie", "isAdult=1");
//            builder.addHeader("Accept", "*/*");
//            builder.addHeader("Accept-Language", "zh-CN;q=0.8");
        }

        return Glide.with(context).load(new GlideUrl(url, builder.build()));
    }

    //===============================================================
    private static TextDrawable getTextDrawable(int index) {
        return getTextDrawable(String.valueOf(index));
    }

    /** 生成占位图 */
    private static TextDrawable getTextDrawable(String name) {
        if (TextUtils.isEmpty(name)) {
            name = "正在加载";
        }

        return TextDrawable.builder()
                .beginConfig()
                .fontSize(80)
                .bold()
                .textColor(Color.WHITE)
                .endConfig()
                .buildRect(name, Color.parseColor("#424242"));
    }

    private static TextDrawable textError;

    private static TextDrawable getError() {
        if (textError == null) {
            textError = getTextDrawable("加载失败");
        }
        return textError;
    }
}
