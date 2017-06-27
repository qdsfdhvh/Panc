package org.seiko.panc.presenter;

import org.seiko.panc.base.BasePresenter;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.bean.DownloadBean;
import org.seiko.panc.contract.DownContract;
import org.seiko.panc.service.DownloadManager;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Seiko on 2017/6/25/025. Y
 */

public class DownPresenter extends BasePresenter<DownContract.View> implements DownContract.Presenter {

    @Override
    public void getData(ComicBean comic) {
        Disposable disposable = Observable.just(comic)
                .flatMap(new Function<ComicBean, ObservableSource<List<DownloadBean>>>() {
                    @Override
                    public ObservableSource<List<DownloadBean>> apply(@NonNull ComicBean comicBean) throws Exception {
                        List<DownloadBean> list = DownloadManager.getInstance().loadDown(comicBean.getUrl());
                        return Observable.just(list);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<DownloadBean>>() {
                    @Override
                    public void accept(@NonNull List<DownloadBean> list) throws Exception {
                        mView.onSuccess(list);
                    }
                });
        mDisposables.add(disposable);
    }

}
