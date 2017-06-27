package org.seiko.panc.ui.down;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.seiko.panc.Constants;
import org.seiko.panc.R;
import org.seiko.panc.base.BackActivity;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.bean.DownloadBean;
import org.seiko.panc.contract.DownContract;
import org.seiko.panc.manager.NumManager;
import org.seiko.panc.presenter.DownPresenter;
import java.util.List;
import butterknife.BindView;

/**
 * Created by Seiko on 2017/6/24/024. Y
 */

public class DownActivity extends BackActivity<DownContract.View, DownPresenter> implements DownContract.View {

    @BindView(R.id.recView)
    RecyclerView recView;

    private DownAdapter mAdapter;

    @Override
    public void initViews(Bundle bundle) {
        ComicBean comic = getIntent().getParcelableExtra(Constants.EXTRA_COMIC);
        mAdapter = new DownAdapter(this);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setHasFixedSize(true);
        recView.addItemDecoration(mAdapter.getItemDecoration());
        recView.setAdapter(mAdapter);
        mPresenter.getData(comic);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.destroy();
        }
    }

    @Override
    public void onSuccess(List<DownloadBean> list) {
        mAdapter.addAll(list);
    }

    @Override
    public int LayoutResID() {
        return R.layout.activity_down;
    }

    @Override
    public String LayoutTitle() {
        return "下载";
    }

    @Override
    public Context getContext() {
        return this;
    }

}
