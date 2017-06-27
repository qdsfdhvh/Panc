package org.seiko.panc.presenter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.reactivestreams.Publisher;
import org.seiko.panc.manager.SourceManager;
import org.seiko.panc.base.BasePresenter;
import org.seiko.panc.bean.SectionBean;
import org.seiko.panc.contract.SectionContract;
import org.seiko.panc.sited.YhNode;
import org.seiko.panc.sited.YhPair;
import org.seiko.panc.sited.YhSource;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Seiko on 2017/6/15/015. y
 */

public class SectionPresenter extends BasePresenter<SectionContract.View> implements SectionContract.Presenter {

    private int i;

    @Override
    public void getData(final String source, final String url) {
        i = 0;
        Disposable disposable = SourceManager.getInstance().rxgetSource(source)
                .flatMap(new Function<YhSource, Publisher<YhPair<YhNode, String>>>() {
                    @Override
                    public Publisher<YhPair<YhNode, String>> apply(@NonNull YhSource sd) throws Exception {
                        return sd.doGetNodeViewModel(sd.getNode(sd._section, url), url);
                    }
                })
                .concatMap(new Function<YhPair<YhNode, String>, Publisher<List<SectionBean>>>() {
                    @Override
                    public Publisher<List<SectionBean>> apply(@NonNull YhPair<YhNode, String> pair) throws Exception {
                        List<SectionBean> list = new ArrayList<>();

                        JsonElement element = new JsonParser().parse(pair.getSecond());
                        JsonArray array = element.getAsJsonArray();
                        for (JsonElement el : array) {
                            SectionBean bean = new SectionBean(el.getAsString(), i);
                            list.add(bean);
                            i++;
                        }
                        return Flowable.just(list);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SectionBean>>() {
                    @Override
                    public void accept(@NonNull List<SectionBean> list) throws Exception {
                        if (list.size() > 0 || i > 0) {
                            mView.onSuccess(list);
                        } else {
                            mView.onFailed();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable e) throws Exception {
                        e.printStackTrace();
                        if (i == 0) {
                            mView.onFailed();
                        }
                    }
                });
        mDisposables.add(disposable);
    }
}
//                .toList().toFlowable()
//                .flatMap(new Function<List<YhPair>, Publisher<List<SectionBean>>>() {
//                    @Override
//                    public Publisher<List<SectionBean>> apply(@NonNull List<YhPair> pairs) throws Exception {
//                        List<SectionBean> list = new ArrayList<>();
//
//                        int i = 0;
//                        for (YhPair pair : pairs) {
//                            JsonElement element = new JsonParser().parse(pair.getJson());
//                            JsonArray array = element.getAsJsonArray();
//                            for (JsonElement el : array) {
//                                SectionBean bean = new SectionBean(el.getAsString(), i);
//                                list.add(bean);
//                                i++;
//                            }
//                        }
//                        return Flowable.just(list);
//                    }
//                })