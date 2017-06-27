package org.seiko.panc.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.seiko.panc.App;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseFragment;
import org.seiko.panc.bean.HotsBean;
import org.seiko.panc.contract.HotsContract;
import org.seiko.panc.manager.NumManager;
import org.seiko.panc.presenter.HotsPresenter;

import java.util.List;
import butterknife.BindView;


/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class HotsFragment extends BaseFragment<HotsContract.View, HotsPresenter> implements HotsContract.View {

    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.custom_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.load_error)
    TextView loadError;

    private HotsAdapter mAdapter;

    public static HotsFragment newInstance(final String source) {
        HotsFragment fragment = new HotsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("source", source);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initViews(Bundle bundle) {
        mAdapter = new HotsAdapter();
        recView.setLayoutManager(new GridLayoutManager(getActivity(), NumManager.HOTS_NUM));
        recView.setHasFixedSize(true);
//        recView.setRecycledViewPool(App.getInstance().getRecycledPool());
        recView.addItemDecoration(mAdapter.getItemDecoration());
        recView.setAdapter(mAdapter);
        mPresenter.getData(getArguments().getString("source"));
    }

    @Override
    public void onSuccess(List<HotsBean> list) {
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
