package org.seiko.panc.ui.main;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseFragment;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.contract.ReaderContract;
import org.seiko.panc.manager.NumManager;
import org.seiko.panc.presenter.ReaderPresenter;
import org.seiko.panc.ui.home.HotsFragment;
import java.util.List;
import butterknife.BindView;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class ReaderFragment extends BaseFragment<ReaderContract.View, ReaderPresenter> implements ReaderContract.View {

    @BindView(R.id.custom_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.recView)
    RecyclerView recView;

    private int type;
    private ReaderAdapter mAdapter;

    public static ReaderFragment newInstance(int type) {
        ReaderFragment fragment = new ReaderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initViews(Bundle bundle) {
        type = getArguments().getInt("type");
        mAdapter = new ReaderAdapter(type);
        recView.setLayoutManager(new GridLayoutManager(getActivity(), NumManager.READER_NUM));
        recView.addItemDecoration(mAdapter.getItemDecoration());
        recView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getData(type);
    }

    @Override
    public void onSuccess(List<ComicBean> list) {
        progressBar.setVisibility(View.GONE);
        mAdapter.clear();
        mAdapter.addAll(list);
    }

    @Override
    public void onFailed() {

    }

    @Override
    public int LayoutResID() {
        return R.layout.fragment_reader;
    }

    @Override
    public String LayoutTitle() {
        return null;
    }



}
