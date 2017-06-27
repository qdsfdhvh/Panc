package org.seiko.panc.service;

import org.seiko.panc.App;
import org.seiko.panc.bean.DownloadBean;
import org.seiko.panc.bean.DownloadBeanDao;
import org.seiko.panc.bean.DownloadBeanDao.Properties;

import java.util.List;

/**
 * Created by Seiko on 2017/6/26/026. Y
 */

public class DownloadManager {

    private static DownloadManager mInstance;

    public static DownloadManager getInstance() {
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new DownloadManager();
                }
            }
        }
        return mInstance;
    }

    private DownloadBeanDao mBeanDao;

    private DownloadManager() {
        mBeanDao = App.getInstance().getDaoSession().getDownloadBeanDao();
    }

    public List<DownloadBean> loadDown(String url) {
        return mBeanDao.queryBuilder()
                .orderDesc(Properties.From)
                .where(Properties.From.eq(url))
                .build()
                .list();
    }

    public void InsertOrUpdate(DownloadBean bean) {
        DownloadBean cache = queryDown("where URL=?", bean.getUrl());

        if (cache == null) {
            long Id = mBeanDao.insert(bean);
            bean.setId(Id);
        } else if (bean.getId() != null) {
            mBeanDao.update(bean);
        }
    }

    public void delete(Long key) {
        if (key != null) {
            mBeanDao.deleteByKey(key);
        }
    }

    public DownloadStatus queryStatus(String url) {
        DownloadBean bean = queryDown("where URL=?", url);
        if (bean != null) {
            DownloadStatus status = new DownloadStatus();
            status.setFlag(bean.getFlag() == null ? 0:bean.getFlag());
            status.setProgress(bean.getProgress());
            status.setMax(bean.getMax() == null ? 0:bean.getMax());
            return status;
        }
        return null;
    }


    public DownloadBean queryDown(String where, String... params) {
        List<DownloadBean> queryRaw = mBeanDao.queryRaw(where, params);
        if (queryRaw != null && queryRaw.size() > 0) {
            return queryRaw.get(0);
        }
        return null;
    }

    public void deleteByComicUrl(String url) {
        mBeanDao.queryBuilder()
                .where(Properties.From.eq(url))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }
}
