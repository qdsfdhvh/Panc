package org.seiko.panc.ui.tag;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.seiko.panc.Constants;
import org.seiko.panc.R;
import org.seiko.panc.base.BackActivity;
import org.seiko.panc.bean.TagBean;
import org.seiko.panc.bean.TagsBean;
import org.seiko.panc.contract.TagContract;
import org.seiko.panc.presenter.TagPresenter;
import java.util.List;
import butterknife.BindView;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class TagActivity extends BackActivity<TagContract.View, TagPresenter> implements TagContract.View, TagFooter.TagFooterView {

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.custom_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.load_error)
    TextView loadError;

    private TagAdapter mAdapter;
    private TagFooter mFooter;

    private TagsBean bean;

    @Override
    public void initViews(Bundle bundle) {
        mFooter = new TagFooter(this);
        mAdapter = new TagAdapter();
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setHasFixedSize(true);
        recView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recView.setAdapter(mAdapter);
        mAdapter.addFooter(mFooter);
        mPresenter.getData(bean, mFooter.getPage());
    }

    @Override
    public void toPage(int page) {
        mFooter.showLoad();
        mPresenter.getData(bean, page);
    }

    @Override
    public void onSuccess(List<TagBean> list) {
        progressBar.setVisibility(View.GONE);
        mAdapter.clearData();
        mAdapter.addAll(list);
        recView.scrollToPosition(0);
        mFooter.hideLoad();
    }

    @Override
    public void onFailed(int page) {
        if (page == 1) {
            progressBar.setVisibility(View.GONE);
            loadError.setVisibility(View.VISIBLE);
        } else {
            mFooter.isLastPage();
            mFooter.hideLoad();
            toast("没有更多内容了");
        }
    }

    @Override
    public int LayoutResID() {
        return R.layout.activity_tag;
    }

    @Override
    public String LayoutTitle() {
        bean = getIntent().getParcelableExtra(Constants.EXTRA_TAGS); //因为toobar需要
        return String.valueOf("分类 - " + bean.getTitle());
    }

    @Override
    public Context getContext() {
        return this;
    }


}
