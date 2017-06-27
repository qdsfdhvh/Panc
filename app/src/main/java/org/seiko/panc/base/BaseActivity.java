package org.seiko.panc.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import org.seiko.panc.R;
import org.seiko.panc.base.mvp.IView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Seiko on 2017/5/18/018. Y
 */

public abstract class BaseActivity extends AppCompatActivity implements IView {

    @Nullable @BindView(R.id.custom_toolbar)
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LayoutResID());
        ButterKnife.bind(this);
        initToolbar();
        initPresent();
        initBack();
        initViews(savedInstanceState);
    }

    protected void initToolbar() {
        if (mToolbar != null) {
            mToolbar.setTitle(LayoutTitle());
            setSupportActionBar(mToolbar);
        }
    }

    protected void initPresent() {

    }

    protected void initBack() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    //======================================
    protected void toast(final String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /** 全屏 */
    public void setImmersiveFullscreen() {
        Window window = getWindow();

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        int uiOpts = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        window.getDecorView().setSystemUiVisibility(uiOpts);
    }
}
