package org.seiko.panc.presenter;


import org.seiko.panc.Constants;
import org.seiko.panc.manager.ComicManager;
import org.seiko.panc.base.BasePresenter;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.contract.ReaderContract;
import java.util.ArrayList;
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
 * Created by Seiko on 2017/6/5/005. Y
 */

public class ReaderPresenter extends BasePresenter<ReaderContract.View> implements ReaderContract.Presenter {

    @Override
    public void getData(int type) {
        Disposable disposable = Observable.just(type)
                .flatMap(new Function<Integer, ObservableSource<List<ComicBean>>>() {
                    @Override
                    public ObservableSource<List<ComicBean>> apply(@NonNull Integer type) throws Exception {
                        List<ComicBean> list = new ArrayList<>();
                        switch (type) {
                            case Constants.TYPE_LIKE:
                                list = ComicManager.getInstance().loadLike();
                                break;
                            case Constants.TYPE_HIST:
                                list = ComicManager.getInstance().loadHist();
                                break;
                            case Constants.TYPE_DOWN:
                                list = ComicManager.getInstance().loadDown();
                                break;
                        }
                        return Observable.just(list);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ComicBean>>() {
                    @Override
                    public void accept(@NonNull List<ComicBean> list) throws Exception {
                        mView.onSuccess(list);
                    }
                });
        mDisposables.add(disposable);
    }

}
