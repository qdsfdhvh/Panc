package org.seiko.panc.base;

import android.view.View;
import org.seiko.panc.base.mvp.IView;
import java.lang.reflect.ParameterizedType;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */
@SuppressWarnings("all")
public abstract class BackActivity<V extends IView, T extends BasePresenter<V>> extends BaseActivity {

    public T mPresenter;

    @Override
    protected void initBack() {
        super.initBack();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    protected void initPresent() {
        super.initPresent();
        mPresenter = getInstance(this);
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    public T getInstance(Object o) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass().getGenericSuperclass())).getActualTypeArguments()[1]).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }


}
