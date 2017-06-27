package org.seiko.panc.presenter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.reactivestreams.Publisher;
import org.seiko.panc.bean.TagsBean;
import org.seiko.panc.manager.SourceManager;
import org.seiko.panc.base.BasePresenter;
import org.seiko.panc.bean.TagBean;
import org.seiko.panc.contract.TagContract;
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

public class TagPresenter extends BasePresenter<TagContract.View> implements TagContract.Presenter {

    @Override
    public void getData(final TagsBean tagBean, final int page) {
        Disposable disposable = SourceManager.getInstance().rxgetSource(tagBean.getSource())
                .delay(300, TimeUnit.MILLISECONDS)
                .flatMap(new Function<YhSource, Publisher<YhPair<YhNode, String>>>() {
                    @Override
                    public Publisher<YhPair<YhNode, String>> apply(@NonNull YhSource sd) throws Exception {
                        return sd.doGetNodeViewModel(sd._tag, "", page, tagBean.getUrl());
                    }
                })
                .flatMap(new Function<YhPair<YhNode, String>, Publisher<List<TagBean>>>() {
                    @Override
                    public Publisher<List<TagBean>> apply(@NonNull YhPair<YhNode, String> pair) throws Exception {
                        List<TagBean> list = new ArrayList<>();

                        JsonArray array = new JsonParser().parse(pair.getSecond()).getAsJsonArray();
                        for (JsonElement el : array) {
                            JsonObject n = el.getAsJsonObject();
                            String name = getString(n, "name");
                            String logo = getString(n, "logo");
                            String url = getString(n, "url");
                            String status = getString(n, "status");
                            String newSection = getString(n, "newSection");
                            String updateTime = getString(n, "updateTime");

                            TagBean bean = new TagBean();
                            bean.setName(name);
                            bean.setLogo(logo);
                            bean.setUrl(url);
                            bean.setStatus(status);
                            bean.setNewSection(newSection);
                            bean.setUpdateTime(updateTime);
                            bean.setSource(tagBean.getSource());
                            list.add(bean);
                        }

                        return Flowable.just(list);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TagBean>>() {
                    @Override
                    public void accept(@NonNull List<TagBean> list) throws Exception {
                        if (list.size() > 0) {
                            mView.onSuccess(list);
                        } else {
                            mView.onFailed(page);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable e) throws Exception {
                        e.printStackTrace();
                        mView.onFailed(page);
                    }
                });
        mDisposables.add(disposable);
    }
}
