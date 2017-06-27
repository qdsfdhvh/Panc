package org.seiko.panc.presenter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.reactivestreams.Publisher;
import org.seiko.panc.manager.SourceManager;
import org.seiko.panc.base.BasePresenter;
import org.seiko.panc.bean.UpdatesBean;
import org.seiko.panc.contract.UpdatesContract;
import org.seiko.panc.sited.YhNode;
import org.seiko.panc.sited.YhPair;
import org.seiko.panc.sited.YhSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public class UpdatesPresenter extends BasePresenter<UpdatesContract.View> implements UpdatesContract.Presenter {

    @Override
    public void getData(final String source) {
        Disposable disposable = SourceManager.getInstance().rxgetSource(source)
                .delay(300, TimeUnit.MILLISECONDS)
                .flatMap(new Function<YhSource, Publisher<YhPair<YhNode, String>>>() {
                    @Override
                    public Publisher<YhPair<YhNode, String>> apply(@NonNull YhSource sd) throws Exception {
                        return sd.doGetNodeViewModel(sd.Updates(), "", 0);
                    }
                })
                .flatMap(new Function<YhPair<YhNode, String>, Publisher<List<UpdatesBean>>>() {
                    @Override
                    public Publisher<List<UpdatesBean>> apply(@NonNull YhPair<YhNode, String> pair) throws Exception {
                        List<UpdatesBean> list = new ArrayList<>();

                        JsonArray array = new JsonParser().parse(pair.getSecond()).getAsJsonArray();
                        for (JsonElement el : array) {
                            JsonObject n = el.getAsJsonObject();
                            String name = getString(n, "name");
                            String logo = getString(n, "logo");
                            String url = getString(n, "url");
                            String newSection = getString(n, "newSection");
                            String updateTime = getString(n, "updateTime");

                            UpdatesBean bean = new UpdatesBean();
                            bean.setName(name);
                            bean.setLogo(logo);
                            bean.setUrl(url);
                            bean.setNewSection(newSection);
                            bean.setUpdateTime(updateTime);
                            bean.setSource(source);
                            list.add(bean);
                        }

                        return Flowable.just(list);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<UpdatesBean>>() {
                    @Override
                    public void accept(@NonNull List<UpdatesBean> list) throws Exception {
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
