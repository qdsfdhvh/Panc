package org.seiko.panc.base;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.seiko.panc.App;
import org.seiko.panc.base.mvp.IView;
import java.lang.reflect.ParameterizedType;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */
@SuppressWarnings("all")
public abstract class BaseFragment<V extends IView, T extends BasePresenter<V>> extends Fragment implements IView {

    public T mPresenter;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(LayoutResID(), container, false);
        unbinder = ButterKnife.bind(this, view);
        initPresent();
        initViews(savedInstanceState);
        return view;
    }

    private void initPresent() {
        mPresenter = getInstance(this);
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroyView();
        unbinder.unbind();
    }

    public T getInstance(Object o) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass().getGenericSuperclass())).getActualTypeArguments()[1]).newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    //======================================
    protected void toast(final String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
