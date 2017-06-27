package org.seiko.panc.presenter;

import android.text.TextUtils;
import org.seiko.panc.base.BasePresenter;
import org.seiko.panc.bean.BookSectionBean;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.bean.DownloadBean;
import org.seiko.panc.contract.BookDownContract;
import org.seiko.panc.manager.ComicManager;
import org.seiko.panc.manager.PathManager;
import org.seiko.panc.service.DownloadFlag;
import org.seiko.panc.service.RxDownload;
import org.seiko.panc.utils.FileUtil;

import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Seiko on 2017/6/25/025. Y
 */

public class BookDownPresenter extends BasePresenter<BookDownContract.View> implements BookDownContract.Presenter {

    private ComicBean comic;

    @Override
    public void getData(ComicBean comic) {
        this.comic = comic;
        Disposable disposable = Observable.fromIterable(comic.getSections())
                .filter(new Predicate<BookSectionBean>() {
                    @Override
                    public boolean test(@NonNull BookSectionBean bean) throws Exception {
                        return !TextUtils.isEmpty(bean.getUrl());
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<BookSectionBean>>() {
                    @Override
                    public void accept(@NonNull List<BookSectionBean> list) throws Exception {
                        mView.onSuccess(list);
                    }
                });
        mDisposables.add(disposable);
    }

    @Override
    public void onDownload(List<BookSectionBean> list) {
        Disposable disposable = Observable.fromIterable(list)
                .filter(new Predicate<BookSectionBean>() {
                    @Override
                    public boolean test(@NonNull BookSectionBean bean) throws Exception {
                        return bean.getFlag() == DownloadFlag.NORMAL;
                    }
                })
                .flatMap(new Function<BookSectionBean, ObservableSource<DownloadBean>>() {
                    @Override
                    public ObservableSource<DownloadBean> apply(@NonNull BookSectionBean section) throws Exception {
                        DownloadBean bean = new DownloadBean();
                        bean.setName(section.getName());
                        bean.setUrl(section.getUrl());
                        bean.setTitle(comic.getName());
                        bean.setFrom(comic.getUrl());
                        bean.setSource(comic.getSource());
                        bean.setProgress(0);
                        bean.setMax(0);
                        bean.setFlag(DownloadFlag.WAITING);
                        return Observable.just(bean);
                    }
                })
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Consumer<List<DownloadBean>>() {
                    @Override
                    public void accept(@NonNull List<DownloadBean> list) throws Exception {
                        mDisposables.add(RxDownload.getInstance(mContext).serviceDownload(list).subscribe());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.onClose();
                        ComicManager.getInstance().insertDown(comic); //保存到数据库
                        String path = PathManager.getBookBean(comic.getSource(), comic.getName());
                        FileUtil.save(path, comic);  //保存到本地
                    }
                });
        mDisposables.add(disposable);
    }


}
