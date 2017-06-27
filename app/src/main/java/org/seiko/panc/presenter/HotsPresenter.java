package org.seiko.panc.presenter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.reactivestreams.Publisher;
import org.seiko.panc.manager.SourceManager;
import org.seiko.panc.base.BasePresenter;
import org.seiko.panc.bean.HotsBean;
import org.seiko.panc.contract.HotsContract;
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
 * Created by Seiko on 2017/6/5/005. Y
 */

public class HotsPresenter extends BasePresenter<HotsContract.View> implements HotsContract.Presenter {

    @Override
    public void getData(final String source) {
        Disposable disposable = SourceManager.getInstance().rxgetSource(source)
//                .delay(200, TimeUnit.MILLISECONDS)
                .flatMap(new Function<YhSource, Publisher<YhPair<YhNode, String>>>() {
                    @Override
                    public Publisher<YhPair<YhNode, String>> apply(@NonNull YhSource sd) throws Exception {
                        return sd.doGetNodeViewModel(sd.Hots(), "", 0);
                    }
                })
                .flatMap(new Function<YhPair<YhNode, String>, Publisher<List<HotsBean>>>() {
                    @Override
                    public Publisher<List<HotsBean>> apply(@NonNull YhPair<YhNode, String> pair) throws Exception {
                        List<HotsBean> list = new ArrayList<>();

                        JsonArray array = new JsonParser().parse(pair.getSecond()).getAsJsonArray();
                        for (JsonElement el : array) {
                            JsonObject n = el.getAsJsonObject();
                            String name = getString(n, "name");
                            String logo = getString(n, "logo");
                            String url = getString(n, "url");

                            HotsBean bean = new HotsBean();
                            bean.setName(name);
                            bean.setLogo(logo);
                            bean.setUrl(url);
                            bean.setSource(source);
                            list.add(bean);
                        }
                        return Flowable.just(list);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<HotsBean>>() {
                    @Override
                    public void accept(@NonNull List<HotsBean> list) throws Exception {
                        if (list.size() > 0) {
                            mView.onSuccess(list);
                        } else {
                            mView.onFailed();
                        }
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
