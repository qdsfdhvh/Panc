package org.seiko.panc.ui.section;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.seiko.panc.Constants;
import org.seiko.panc.R;
import org.seiko.panc.base.BackActivity;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.bean.DownloadBean;
import org.seiko.panc.bean.SectionBean;
import org.seiko.panc.contract.SectionContract;
import org.seiko.panc.manager.PathManager;
import org.seiko.panc.presenter.SectionPresenter;
import org.seiko.panc.utils.FileUtil;
import org.seiko.panc.wiget.scale.ZoomableRecyclerView;

import java.lang.reflect.Type;
import java.util.List;
import butterknife.BindView;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class SectionActivity extends BackActivity<SectionContract.View, SectionPresenter> implements SectionContract.View {

    @BindView(R.id.recView)
    ZoomableRecyclerView recView;
    @BindView(R.id.custom_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.load_error)
    TextView loadError;

    private SectionAdapter mAdapter;

    @Override
    public void initViews(Bundle bundle) {
        loadError.setTextColor(Color.WHITE);  //黑色背景，需要修改默认字体颜色

        ComicBean comic = getIntent().getParcelableExtra(Constants.EXTRA_COMIC);
        mAdapter = new SectionAdapter(comic.getLast());
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(mAdapter);
        recView.setVertical(true);
        loadLocal(comic);
    }

    private void loadLocal(ComicBean comic) {
        String path = PathManager.getSectionPath(comic.getSource(), comic.getName(), comic.getChapter());
        Type type = new TypeToken<DownloadBean>(){}.getType();
        DownloadBean downBean = FileUtil.get(path + "Section.json", type);
        if (downBean != null) {
            mAdapter.setPath(path);
            onSuccess(downBean.getUrls());
        } else {
            mPresenter.getData(comic.getSource(), comic.getLast());
        }
    }

    @Override
    public void onSuccess(List<SectionBean> list) {
        progressBar.setVisibility(View.GONE);
        mAdapter.insertAllBack(list);
    }

    @Override
    public void onFailed() {
        progressBar.setVisibility(View.GONE);
        loadError.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImmersiveFullscreen();
    }

    @Override
    public int LayoutResID() {
        return R.layout.activity_section;
    }

    @Override
    public String LayoutTitle() {
        return "";
    }

    @Override
    public Context getContext() {
        return this;
    }
}
