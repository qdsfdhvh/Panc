package org.seiko.panc.rx;

import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * List之间的转换
 */

public class ToAnotherList<T, R> implements ObservableTransformer<List<T>, List<R>> {

    private Function<T, R> func;

    public ToAnotherList(Function<T, R> func) {
        this.func = func;
    }

    @Override
    public ObservableSource<List<R>> apply(@NonNull Observable<List<T>> upstream) {
        return upstream.flatMap(new Function<List<T>, ObservableSource<T>>() {
            @Override
            public ObservableSource<T> apply(@NonNull List<T> ts) throws Exception {
                return Observable.fromIterable(ts);
            }
        }).map(func).toList().toObservable();
    }
}
