package org.seiko.panc.ui.home;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.seiko.panc.App;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseFragment;
import org.seiko.panc.bean.UpdatesBean;
import org.seiko.panc.contract.UpdatesContract;
import org.seiko.panc.presenter.UpdatesPresenter;

import java.util.List;
import butterknife.BindView;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class UpdatesFragment extends BaseFragment<UpdatesContract.View, UpdatesPresenter> implements UpdatesContract.View  {

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.custom_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.load_error)
    TextView loadError;

    private UpdatesAdapter mAdapter;

    public static UpdatesFragment newInstance(final String source) {
        UpdatesFragment fragment = new UpdatesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source", source);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initViews(Bundle bundle) {
        mAdapter = new UpdatesAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
//        llm.setRecycleChildrenOnDetach(true);
        recView.setLayoutManager(llm);
        recView.setHasFixedSize(true);
//        recView.setRecycledViewPool(App.getInstance().getRecycledPool());
        recView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recView.setAdapter(mAdapter);
        mPresenter.getData(getArguments().getString("source"));
    }

    @Override
    public void onSuccess(List<UpdatesBean> list) {
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
        return R.layout.fragment_home;
    }

    @Override
    public String LayoutTitle() {
        return null;
    }


}
