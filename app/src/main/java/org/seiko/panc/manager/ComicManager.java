package org.seiko.panc.manager;

import android.text.TextUtils;

import org.seiko.panc.App;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.bean.ComicBeanDao;
import org.seiko.panc.bean.ComicBeanDao.Properties;

import java.util.Date;
import java.util.List;

/**
 * Created by Seiko on 2017/6/19/019. Y
 */

public class ComicManager {

    private static ComicManager mInstance;

    public static ComicManager getInstance() {
        if (mInstance == null) {
            synchronized (ComicManager.class) {
                if (mInstance == null) {
                    mInstance = new ComicManager();
                }
            }
        }
        return mInstance;
    }

    private ComicBeanDao mComicDao;

    private ComicManager() {
        mComicDao = App.getInstance().getDaoSession().getComicBeanDao();
    }

    public List<ComicBean> loadLike() {
        return mComicDao.queryBuilder()
                .orderDesc(Properties.Like)
                .where(Properties.Like.isNotNull())
                .build()
                .list();
    }

    public List<ComicBean> loadHist() {
        return mComicDao.queryBuilder()
                .orderDesc(Properties.Hist)
                .where(Properties.Hist.isNotNull())
                .build()
                .list();
    }

    public List<ComicBean> loadDown() {
        return mComicDao.queryBuilder()
                .orderDesc(Properties.Down)
                .where(Properties.Down.isNotNull())
                .build()
                .list();
    }

    public void insertLike(ComicBean comic) {
        comic.setLike(new Date().getTime());
        updateOrInsert(comic);
    }

    public void insertHist(ComicBean comic) {
        comic.setHist(new Date().getTime());
        updateOrInsert(comic);
    }

    public void insertDown(ComicBean comic) {
        comic.setDown(new Date().getTime());
        updateOrInsert(comic);
    }

    private void updateOrInsert(ComicBean comic) {
        ComicBean cacheComic = queryComic("where URL=?", comic.getUrl());
        if (cacheComic != null) {
            comic.setId(cacheComic.getId());
            if (comic.getLike() == null && cacheComic.getLike() != null) {
                comic.setLike(cacheComic.getLike());
            }
            if (comic.getDown() == null && cacheComic.getDown() != null) {
                comic.setDown(cacheComic.getDown());
            }
            if (comic.getHist() == null && cacheComic.getHist() != null) {
                comic.setHist(cacheComic.getHist());
            }
            if (TextUtils.isEmpty(comic.getLast()) && cacheComic.getLast() != null) {
                comic.setLast(cacheComic.getLast());
            }

            mComicDao.update(comic);
            return;
        }
        mComicDao.insert(comic);
    }

    /**
     * 根据条件查找多条数据
     */
    public List<ComicBean> queryComics(String where, String... params) {
        return mComicDao.queryRaw(where, params);
    }


    /**
     * 是否为收藏
     */
    public boolean queryUrl(String url) {
        List<ComicBean> queryRaw = mComicDao.queryRaw("where URL=?", url);
        return queryRaw != null && queryRaw.size() > 0 && queryRaw.get(0).getLike() != null;
    }

    /**
     * 查找记录
     */
    public String queryLast(String url) {
        ComicBean comic = queryComic("where URL=?", url);
        if (comic != null) {
            return comic.getLast();
        }
        return null;
    }

    public int queryLastIndex(String url) {
        ComicBean comic = queryComic("where URL=?", url);
        if (comic != null) {
            return comic.getIndex();
        }
        return 0;
    }

    /**
     * 删除收藏
     */
    public void delLike(ComicBean comic) {
        if (comic.getId() == null) {
            comic = queryComic("where URL=?", comic.getUrl());
        }
        if (comic.getHist() != null || comic.getDown() != null) {
            comic.setLike(null);
            mComicDao.update(comic);
            return;
        }
        mComicDao.delete(comic);
    }

    /**
     * 删除历史
     */
    public void delHist(ComicBean comic) {
        if (comic.getId() == null) {
            comic = queryComic("where URL=?", comic.getUrl());
        }
        if (comic.getLike() != null || comic.getDown() != null) {
            comic.setDown(null);
            mComicDao.update(comic);
            return;
        }
        mComicDao.delete(comic);
    }

    public void delDown(ComicBean comic) {
        if (comic.getId() == null) {
            comic = queryComic("where URL=?", comic.getUrl());
        }
        if (comic.getLike() != null || comic.getHist() != null) {
            comic.setDown(null);
            mComicDao.update(comic);
            return;
        }
        mComicDao.delete(comic);
    }

    /**
     * 根据条件查找单条数据
     */
    public ComicBean queryComic(String where, String... params) {
        List<ComicBean> queryRaw = mComicDao.queryRaw(where, params);
        if (queryRaw != null && queryRaw.size() > 0) {
            return queryRaw.get(0);
        }
        return null;
    }
}
