package org.seiko.panc.wiget;

/**
 * Created by k on 2016/8/11. Y
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;

public class MyImageView extends AppCompatImageView {
//屏幕宽
    float screen_width;
    private Bitmap mBitmap;
    //倍数
    float Multiple = 0, pic_height = 0, pic_init_width = 0;
    float pic_init_height = 0;

    private boolean mReady;
    private boolean mSetupPending;

    public MyImageView(Context context) {
        super(context);
        init();
    }

    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //得到屏幕的宽度
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        screen_width = wm.getDefaultDisplay().getWidth();
        init();
    }

    private void init() {
        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    private void reSize() {
        LayoutParams lp = this.getLayoutParams();
        //把图片的宽度设置为占满屏幕
        lp.width = (int) screen_width;
        //求出宽占满屏幕后与原来的倍数
        Multiple = screen_width / pic_init_width;
        //让图片的高度随着宽度的变化而变化，即约束比例
        pic_height = pic_init_height * Multiple;
        lp.height = (int) pic_height;
        //最后把图片的宽高设置为ImageView的宽高即可
        this.setLayoutParams(lp);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
        reSize();
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null) {
            return;
        }
        //得到图片的初始值
        pic_init_height = mBitmap.getHeight();
        pic_init_width = mBitmap.getWidth();
        invalidate();
    }

}

