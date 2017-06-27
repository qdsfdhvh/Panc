package org.seiko.panc.ui.home;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.seiko.panc.App;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseFragment;
import org.seiko.panc.base.ItemType;
import org.seiko.panc.contract.TagsContract;
import org.seiko.panc.manager.NumManager;
import org.seiko.panc.presenter.TagsPresenter;

import java.util.List;
import butterknife.BindView;


/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class TagsFragment extends BaseFragment<TagsContract.View, TagsPresenter> implements TagsContract.View  {

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.custom_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.load_error)
    TextView loadError;

    private TagsAdapter mAdapter;

    public static TagsFragment newInstance(final String source) {
        TagsFragment fragment = new TagsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source", source);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initViews(Bundle bundle) {
        mAdapter = new TagsAdapter(getActivity());
        recView.setLayoutManager(new StaggeredGridLayoutManager(NumManager.TAGS_NUM, StaggeredGridLayoutManager.VERTICAL));
        recView.setHasFixedSize(true);
        recView.addItemDecoration(mAdapter.getItemDecoration());
        recView.setAdapter(mAdapter);
        mPresenter.getData(getArguments().getString("source"));
    }

    @Override
    public void onSuccess(List<ItemType> list) {
        mAdapter.addAll(list);
        progressBar.setVisibility(View.GONE);
        loadError.setVisibility(View.GONE);
    }

    @Override
    public void onFailed() {
        progressBar.setVisibility(View.GONE);
        loadError.setVisibility(View.VISIBLE);
    }

    @Override
    public int LayoutResID() {
        return R.layout.fragment_tags;
    }

    @Override
    public String LayoutTitle() {
        return null;
    }

}
