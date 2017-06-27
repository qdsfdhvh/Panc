package org.seiko.panc.presenter;

import android.text.TextUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.reactivestreams.Publisher;
import org.seiko.panc.App;
import org.seiko.panc.manager.SourceManager;
import org.seiko.panc.base.BasePresenter;
import org.seiko.panc.bean.SearchBean;
import org.seiko.panc.contract.SearchContract;
import org.seiko.panc.sited.YhNode;
import org.seiko.panc.sited.YhPair;
import org.seiko.panc.sited.YhSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public class SearchPresenter extends BasePresenter<SearchContract.View> implements SearchContract.Presenter {

    private Semaphore semaphore;

    public SearchPresenter() {
        semaphore = new Semaphore(3);
    }
    @Override
    public void getData(final String keyword) {
        Disposable disposable = Flowable.fromIterable(SourceManager.getInstance().getSourceNames())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(@NonNull String s) throws Exception {
                        return App.getInstance().getPreferenceManager().getBoolean(s, true);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<String, Publisher<YhSource>>() {
                    @Override
                    public Publisher<YhSource> apply(@NonNull String s) throws Exception {
                        semaphore.acquire();
                        return SourceManager.getInstance().rxgetSource(s).doFinally(new Action() {
                            @Override
                            public void run() throws Exception {
                                semaphore.release();
                            }
                        });
                    }
                })
                .observeOn(Schedulers.io())
                .filter(new Predicate<YhSource>() {
                    @Override
                    public boolean test(@NonNull YhSource sd) throws Exception {
                        return sd != null && sd._search != null;
                    }
                })
                .flatMap(new Function<YhSource, Publisher<YhPair<YhNode, String>>>() {
                    @Override
                    public Publisher<YhPair<YhNode, String>> apply(@NonNull YhSource sd) throws Exception {
                        return sd.doGetNodeViewModel(sd._search, keyword, 1);
                    }
                })
                .filter(new Predicate<YhPair<YhNode, String>>() {
                    @Override
                    public boolean test(@NonNull YhPair<YhNode, String> pair) throws Exception {
                        return !TextUtils.isEmpty(pair.getSecond()) && !"[]".equals(pair.getSecond());
                    }
                })
                .flatMap(new Function<YhPair<YhNode, String>, Publisher<List<SearchBean>>>() {
                    @Override
                    public Publisher<List<SearchBean>> apply(@NonNull YhPair<YhNode, String> pair) throws Exception {
                        List<SearchBean> list = new ArrayList<>();
                        JsonArray array = new JsonParser().parse(pair.getSecond()).getAsJsonArray();
                        for (JsonElement el : array) {
                            JsonObject n = el.getAsJsonObject();
                            String name = getString(n, "name");
                            String logo = getString(n, "logo");
                            String url = getString(n, "url");
                            String newSection = getString(n, "newSection");
                            String updateTime = getString(n, "updateTime");

                            SearchBean bean = new SearchBean();
                            bean.setName(name);
                            bean.setLogo(logo);
                            bean.setUrl(url);
                            bean.setNewSection(newSection);
                            bean.setUpdateTime(updateTime);
                            bean.setSource(pair.getFirst().source());
                            list.add(bean);
                        }
                        return Flowable.just(list);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<SearchBean>>() {
                    @Override
                    public void accept(@NonNull List<SearchBean> list) throws Exception {
                        mView.onSuccess(list);

                    }
                });
        mDisposables.add(disposable);
    }

}
