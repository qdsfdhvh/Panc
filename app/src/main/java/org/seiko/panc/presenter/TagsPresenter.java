package org.seiko.panc.presenter;

import android.text.TextUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.reactivestreams.Publisher;
import org.seiko.panc.manager.SourceManager;
import org.seiko.panc.base.BasePresenter;
import org.seiko.panc.base.ItemType;
import org.seiko.panc.bean.TagsBean;
import org.seiko.panc.bean.TagsHeadBean;
import org.seiko.panc.contract.TagsContract;
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

public class TagsPresenter extends BasePresenter<TagsContract.View> implements TagsContract.Presenter {

    @Override
    public void getData(final String source) {
        Disposable disposable = SourceManager.getInstance().rxgetSource(source)
                .delay(500, TimeUnit.MILLISECONDS)
                .flatMap(new Function<YhSource, Publisher<YhPair<YhNode, String>>>() {
                    @Override
                    public Publisher<YhPair<YhNode, String>> apply(@NonNull YhSource sd) throws Exception {
                        return sd.doGetNodeViewModel(sd.Tags(), "", 0);
                    }
                })
                .flatMap(new Function<YhPair<YhNode, String>, Publisher<List<ItemType>>>() {
                    @Override
                    public Publisher<List<ItemType>> apply(@NonNull YhPair<YhNode, String> pair) throws Exception {
                        List<ItemType> list = new ArrayList<>();

                        if (pair.getFirst().hasItems()) {
                            for (YhNode n1 : pair.getFirst().items()) {
                                add(list, n1.group, n1.title , n1.url, source);
                            }
                            return Flowable.fromIterable(list).toList().toFlowable();
                        }

                        JsonElement element = new JsonParser().parse(pair.getSecond());
                        JsonArray array = element.getAsJsonArray();
                        for (JsonElement el:array) {
                            JsonObject n = el.getAsJsonObject();
                            String url = getString(n, "url");
                            String title = getString(n, "title");
                            String group = getString(n, "group");

                            add(list, group, title , url, source);
                        }
                        return Flowable.just(list);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ItemType>>() {
                    @Override
                    public void accept(@NonNull List<ItemType> list) throws Exception {
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

    private void add(List<ItemType> list, String group, String title, String url, String source) {
        if (!TextUtils.isEmpty(group)) {
            list.add(new TagsHeadBean(group, ""));
        }
        list.add(new TagsBean(source, title, url));
    }

}
