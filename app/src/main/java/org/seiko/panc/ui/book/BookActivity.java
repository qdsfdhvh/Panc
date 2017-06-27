package org.seiko.panc.ui.book;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.seiko.panc.Navigation;
import org.seiko.panc.R;
import org.seiko.panc.manager.ComicManager;
import org.seiko.panc.base.BackActivity;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.contract.BookContract;
import org.seiko.panc.presenter.BookPresenter;
import butterknife.BindView;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class BookActivity extends BackActivity<BookContract.View, BookPresenter> implements BookContract.View {

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.custom_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.load_error)
    TextView loadError;

    private String url;
    private BookAdapter mAdapter;
    private ComicBean content;
    private boolean isFavorite;

    @Override
    public void initViews(Bundle bundle) {
        String source = getIntent().getStringExtra("source");
        url = getIntent().getStringExtra("url");
        String lastUrl = ComicManager.getInstance().queryLast(url);
        mAdapter = new BookAdapter(this, lastUrl==null? "" :lastUrl);
        recView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recView.setHasFixedSize(true);
        recView.addItemDecoration(mAdapter.getItemDecoration());
        recView.setAdapter(mAdapter);
        mPresenter.getData(source, url);
    }

    @Override
    public void onSuccess(ComicBean content) {
        if (TextUtils.isEmpty(content.getLogo())) {
            content.setLogo(getIntent().getStringExtra("logo"));
        }
        progressBar.setVisibility(View.GONE);
        mAdapter.addHeader(new BookHeader(content));
        mAdapter.setContent(content);
        mAdapter.addAll(content.getSections());
        this.content = content;
        int index = ComicManager.getInstance().queryLastIndex(url);
        recView.scrollToPosition(index);
    }

    @Override
    public void onFailed() {
        progressBar.setVisibility(View.GONE);
        loadError.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        MenuItem item = menu.findItem(R.id.action_favorite);
        isFavorite = ComicManager.getInstance().queryUrl(url);
        setItemCheckable(item, isFavorite); // 是否收藏
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (content == null) {
            return false;
        }

        switch (item.getItemId()) {
            case R.id.action_favorite:
                if (isFavorite) {
                    mAdapter.delData();
                    setItemCheckable(item, false);
                    toast("删除收藏");
                    isFavorite = false;
                } else {
                    mAdapter.saveData();
                    setItemCheckable(item, true);
                    toast("收藏成功");
                    isFavorite = true;
                }
                return true;
            case R.id.action_down:
                Navigation.showBookDown(this, content);
                return true;
            case R.id.action_export:
                mAdapter.setReverseLayout();
                return true;
            case R.id.action_onweb:
                Navigation.showOutWeb(this, getIntent().getStringExtra("url"));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setItemCheckable(MenuItem item, boolean checkable) {
        if (checkable) {
            item.setCheckable(true);
            item.setIcon(R.drawable.ic_favorite_white_24dp);
        } else {
            item.setCheckable(false);
            item.setIcon(R.drawable.ic_favorite_border_white_24dp);
        }
    }

    @Override
    public int LayoutResID() {
        return R.layout.activity_book;
    }

    @Override
    public String LayoutTitle() {
        return "目录";
    }

}
