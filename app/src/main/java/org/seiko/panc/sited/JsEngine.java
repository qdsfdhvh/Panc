package org.seiko.panc.sited;

import android.app.Application;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Seiko on 2017/4/15. Y
 */

class JsEngine {

    private V8 engine;

    JsEngine(final Application app) {
        Observable.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        engine = V8.createV8Runtime(null, app.getApplicationInfo().dataDir);
                    }
                });
    }

    synchronized void loadJs(final String funs) {
        Observable.just(funs)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        engine.executeScript(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable e) throws Exception {
                        Util.log("JsEngine.loadJs", e.getMessage(), e);
                    }
                });

    }

    Observable<String> rxCallJs(final String fun, final String... args) {
        return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                        String json = "[]";
                        try {
                            V8Array params = new V8Array(engine);
                            for (String p : args) {
                                params.push(p);
                            }
                            json = engine.executeStringFunction(fun, params);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        e.onNext(json);
                        e.onComplete();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io());
    }

}
