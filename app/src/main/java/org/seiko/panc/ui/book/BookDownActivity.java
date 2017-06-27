package org.seiko.panc.ui.book;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import org.seiko.panc.Constants;
import org.seiko.panc.Navigation;
import org.seiko.panc.bean.BookSectionBean;
import org.seiko.panc.R;
import org.seiko.panc.base.BackActivity;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.contract.BookDownContract;
import org.seiko.panc.manager.NumManager;
import org.seiko.panc.presenter.BookDownPresenter;
import org.seiko.panc.service.DownloadFlag;

import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by Seiko on 2017/6/24/024. Y
 */

public class BookDownActivity extends BackActivity<BookDownContract.View, BookDownPresenter> implements BookDownContract.View {

    @BindView(R.id.recView)
    RecyclerView recView;

    private BookDownAdapter mAdapter;

    @Override
    public void initViews(Bundle bundle) {
        ComicBean comic = getIntent().getParcelableExtra(Constants.EXTRA_COMIC);
        mAdapter = new BookDownAdapter();
        recView.setLayoutManager(new GridLayoutManager(this, NumManager.SITED_NUM));
        recView.setHasFixedSize(true);
        recView.addItemDecoration(mAdapter.getItemDecoration());
        recView.setAdapter(mAdapter);
        mPresenter.getData(comic);
    }


    @Override
    public void onSuccess(List<BookSectionBean> list) {
        mAdapter.addAll(list);
    }

    @Override
    public void onClose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast("导入成功");
                onBackPressed();
            }
        });
    }

    @OnClick(R.id.start_down)
    void startDown() {
        mPresenter.onDownload(mAdapter.getData());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_down, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done_all:
                Observable.fromIterable(mAdapter.getData())
                        .filter(new Predicate<BookSectionBean>() {
                            @Override
                            public boolean test(@NonNull BookSectionBean bean) throws Exception {
                                return bean.getFlag() == 0;
                            }
                        })
                        .subscribe(new Consumer<BookSectionBean>() {
                            @Override
                            public void accept(@NonNull BookSectionBean bookSectionBean) throws Exception {
                                bookSectionBean.setFlag(DownloadFlag.NORMAL);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {

                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int LayoutResID() {
        return R.layout.activity_bookdown;
    }

    @Override
    public String LayoutTitle() {
        return "选择";
    }

    @Override
    public Context getContext() {
        return this;
    }


}
