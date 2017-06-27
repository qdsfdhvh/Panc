package org.seiko.panc.presenter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.reactivestreams.Publisher;
import org.seiko.panc.manager.SourceManager;
import org.seiko.panc.base.BasePresenter;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.contract.BookContract;
import org.seiko.panc.sited.YhNode;
import org.seiko.panc.sited.YhPair;
import org.seiko.panc.sited.YhSource;
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

public class BookPresenter extends BasePresenter<BookContract.View> implements BookContract.Presenter {

    @Override
    public void getData(final String source, final String url) {
        Disposable disposable = SourceManager.getInstance().rxgetSource(source)
                .delay(200, TimeUnit.MILLISECONDS)
                .flatMap(new Function<YhSource, Publisher<YhPair<YhNode, String>>>() {
                    @Override
                    public Publisher<YhPair<YhNode, String>> apply(@NonNull YhSource sd) throws Exception {
                        return sd.doGetNodeViewModel(sd.getNode(sd._book, url), "", 0, url);
                    }
                })
                .flatMap(new Function<YhPair<YhNode, String>, Publisher<ComicBean>>() {
                    @Override
                    public Publisher<ComicBean> apply(@NonNull YhPair<YhNode, String> pair) throws Exception {
                        ComicBean bean = new ComicBean();

                        JsonElement element = new JsonParser().parse(pair.getSecond());
                        JsonObject data = element.getAsJsonObject();
                        String name   = getString(data, "name");
                        String logo   = getString(data, "logo");
                        String author = getString(data, "author");
                        String intro  = getString(data, "intro");
                        String updateTime = getString(data, "updateTime");

                        bean.setUrl(url);
                        bean.setName(name);
                        bean.setLogo(logo);
                        bean.setAuthor(author);
                        bean.setIntro(intro);
                        bean.setUpdateTime(updateTime);
                        bean.setSource(source);

                        JsonArray s2 = data.getAsJsonArray("sections");
                        for (JsonElement el:s2) {
                            JsonObject n = el.getAsJsonObject();
                            String u1 = getString(n, "url");
                            String n1 = getString(n, "name");
                            if ("".contains(u1)) {
                                bean.addHead(n1);
                            } else  {
                                bean.add(n1, u1);
                            }
                        }
                        return Flowable.just(bean);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ComicBean>() {
                    @Override
                    public void accept(@NonNull ComicBean bean) throws Exception {
                        mView.onSuccess(bean);
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
