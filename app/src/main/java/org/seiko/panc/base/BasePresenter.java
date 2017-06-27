package org.seiko.panc.base;

import android.content.Context;

import com.google.gson.JsonObject;

import org.seiko.panc.base.mvp.IPresenter;
import org.seiko.panc.base.mvp.IView;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Seiko on 2017/5/18/018. Y
 */

public class BasePresenter<V extends IView> implements IPresenter<V> {

    protected V mView;
    protected CompositeDisposable mDisposables;
    protected Context mContext;

    @Override
    public void attachView(V view) {
        mView = view;
        mContext = view.getContext();
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void detachView() {
        mView = null;
        if (mDisposables != null && !mDisposables.isDisposed()) {
            mDisposables.clear();
        }
    }

    protected String getString(JsonObject data, String key) {
        return data.get(key) != null ? data.get(key).getAsString():"";
    }

    protected int getInt(JsonObject data, String key) {
        return data.get(key) != null ? data.get(key).getAsInt():0;
    }

}
