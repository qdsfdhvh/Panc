package org.seiko.panc.presenter;

import org.seiko.panc.manager.PathManager;
import org.seiko.panc.base.BasePresenter;
import org.seiko.panc.bean.SitedBean;
import org.seiko.panc.contract.SitedContract;
import org.seiko.panc.rx.ToAnotherList;
import org.seiko.panc.utils.FileUtil;
import java.io.File;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public class SitedPresenter extends BasePresenter<SitedContract.View> implements SitedContract.Presenter {

    @Override
    public void getData() {
        Disposable disposable = FileUtil.loadFile(PathManager.sitePath)
                .compose(new ToAnotherList<>(new Function<File, SitedBean>() {
                    @Override
                    public SitedBean apply(@NonNull File file) throws Exception {
                        return new SitedBean(file);
                    }
                }))
//                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SitedBean>>() {
                    @Override
                    public void accept(@NonNull List<SitedBean> list) throws Exception {
                        mView.onSuccess(list);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mView.onFailed();
                    }
                });
        mDisposables.add(disposable);

    }
}
