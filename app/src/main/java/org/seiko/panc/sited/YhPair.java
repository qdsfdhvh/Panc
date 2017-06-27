package org.seiko.panc.sited;

/**
 * Created by Seiko on 2017/6/12/012. Y
 */

public class YhPair<F, S> {

    private F first;
    private S second;

    private YhPair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public static <F, S> YhPair<F, S> create(F first, S second) {
        return new YhPair<>(first, second);
    }

}
