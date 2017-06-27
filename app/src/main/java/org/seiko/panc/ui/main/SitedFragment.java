package org.seiko.panc.ui.main;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseFragment;
import org.seiko.panc.bean.SitedBean;
import org.seiko.panc.contract.SitedContract;
import org.seiko.panc.manager.NumManager;
import org.seiko.panc.presenter.SitedPresenter;
import java.util.List;
import butterknife.BindView;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class SitedFragment extends BaseFragment<SitedContract.View, SitedPresenter> implements SitedContract.View {

    @BindView(R.id.custom_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.recView)
    RecyclerView recView;

    private SitedAdapter mAdapter;

    @Override
    public void initViews(Bundle bundle) {
        mAdapter = new SitedAdapter();
        GridLayoutManager glm = new GridLayoutManager(getActivity(), NumManager.SITED_NUM);
        glm.setRecycleChildrenOnDetach(true);
        recView.setLayoutManager(glm);
        recView.setHasFixedSize(true);
        recView.addItemDecoration(mAdapter.getItemDecoration());
        recView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.getData();
        }
    }

    @Override
    public void onSuccess(List<SitedBean> list) {
        progressBar.setVisibility(View.GONE);
        mAdapter.clear();
        mAdapter.addAll(list);
    }

    @Override
    public void onFailed() {
        progressBar.setVisibility(View.GONE);
        toast("没有安装插件");
    }

    @Override
    public int LayoutResID() {
        return R.layout.fragment_main_sited;
    }

    @Override
    public String LayoutTitle() {
        return null;
    }

}
