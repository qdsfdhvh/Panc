package org.seiko.panc.ui.main;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.tbruyelle.rxpermissions2.RxPermissions;
import org.seiko.panc.Navigation;
import org.seiko.panc.R;
import org.seiko.panc.manager.PathManager;
import org.seiko.panc.manager.SourceManager;
import org.seiko.panc.base.BaseActivity;
import org.seiko.panc.base.BaseFragment;
import org.seiko.panc.sited.YhSource;
import org.seiko.panc.utils.EncryptUtil;
import org.seiko.panc.utils.FileUtil;
import org.seiko.panc.utils.HttpUtil;
import butterknife.BindView;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int FRAGMENT_NUM = 2;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    private long mExitTime = 0;
    private int mCheckItem;
    private BaseFragment mCurrentFragment;
    private SparseArray<BaseFragment> mFragmentArray;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void initViews(Bundle bundle) {
        requestPermissions();
        initDrawerToggle();
        initFragment();
        forIntent(getIntent());
    }

    private void requestPermissions() {
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean granted) throws Exception {
                        if (!granted) {  //权限被拒绝
                            throw new RuntimeException("no permission");
                        }
                    }
                });
    }

    private void initDrawerToggle() {
        /* 点击切换fragment */
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0,0) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (refreshCurrentFragment()) {
                    getFragmentManager().beginTransaction().show(mCurrentFragment).commit();
                } else {
                    getFragmentManager().beginTransaction().add(R.id.main_fragment, mCurrentFragment).commit();
                }
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initFragment() {
        mCheckItem = R.id.drawer_reader;  //初始页:搜索
        mNavigationView.setCheckedItem(mCheckItem);
        mFragmentArray = new SparseArray<>(FRAGMENT_NUM);
        refreshCurrentFragment();
        getFragmentManager().beginTransaction().add(R.id.main_fragment, mCurrentFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@android.support.annotation.NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId != mCheckItem) {
            switch (itemId) {
                case R.id.drawer_reader:
                case R.id.drawer_sited:
                    mCheckItem = itemId;
                    getFragmentManager().beginTransaction().hide(mCurrentFragment).commit();
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    break;
            }
        }
        return true;
    }

    /* 判断集合中是否有目标fragment，没有就添加 */
    private boolean refreshCurrentFragment() {
        mCurrentFragment = mFragmentArray.get(mCheckItem);
        if (mCurrentFragment == null) {
            switch (mCheckItem) {
                case R.id.drawer_reader:
                    mCurrentFragment = new ReaderMainFragment();
                    break;
                case R.id.drawer_sited:
                    mCurrentFragment = new SitedFragment();
                    break;
            }
            mFragmentArray.put(mCheckItem, mCurrentFragment);
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Navigation.showSearch(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (System.currentTimeMillis() - mExitTime > 2000) {
            Snackbar.make(mDrawerLayout, "再按一次退出", Snackbar.LENGTH_LONG).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public int LayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    public String LayoutTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public Context getContext() {
        return this;
    }

    //===================================================
    /** 安装插件 */
    private void forIntent(Intent intent) {
        if (intent == null)
            return;

        Uri uri = intent.getData();
        if (uri == null)
            return;

        String scheme = uri.getScheme();
        if (scheme.equals("sited")) {
            String webUrl =  EncryptUtil.b64_decode(uri.getQuery()).replace("sited://", "http://");

            addSource(HttpUtil.get(webUrl).blockingFirst());
        }
        else if (scheme.equals("file")) {
            try {
                ContentResolver cr = this.getContentResolver();
                final String sited = FileUtil.toString(cr.openInputStream(uri));
                addSource(sited);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void addSource(final String sited) {
        YhSource sd = SourceManager.getInstance().loadSource(sited);
        if (sd != null) {
            if (sd.dtype != 1) {
                toast("不能安装非漫画插件, 插件类型：" + sd.dtype);
                return;
            }
            FileUtil.saveText2Sdcard(PathManager.getSitePath(sd.Title()), sited);
            toast(sd.Title() + "：安装成功");
        } else {
            toast("安装失败");
        }
    }

}
