package org.seiko.panc;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import com.squareup.leakcanary.LeakCanary;

import org.seiko.panc.bean.DaoMaster;
import org.seiko.panc.bean.DaoSession;
import org.seiko.panc.manager.PreferenceManager;
import okhttp3.OkHttpClient;

/**
 * Created by Seiko on 2017/5/18/018. Y
 */

public class App extends Application {

    private static OkHttpClient mHttpClient;
    private static App mCurrent;

    private String _basePath;
    private PreferenceManager mPreferenceManager;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mCurrent = this;
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "comic.db", null);
        mDaoSession = new DaoMaster(devOpenHelper.getWritableDb()).newSession();
        loadLeak();
    }

    private void loadLeak() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    public static App getInstance(){
        return mCurrent;
    }

    public static Context getContext() {
        return mCurrent.getApplicationContext();
    }

    public static OkHttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new OkHttpClient();
        }
        return mHttpClient;
    }

    public String getBasePath() {
        if (_basePath == null) {
            _basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Panc/";
        }
        return _basePath;
    }

    public PreferenceManager getPreferenceManager() {
        if (mPreferenceManager == null) {
            mPreferenceManager = new PreferenceManager(getApplicationContext());
        }
        return mPreferenceManager;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

}
