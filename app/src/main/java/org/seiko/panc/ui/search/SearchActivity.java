package org.seiko.panc.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.seiko.panc.R;
import org.seiko.panc.base.BackActivity;
import org.seiko.panc.bean.SearchBean;
import org.seiko.panc.contract.SearchContract;
import org.seiko.panc.presenter.SearchPresenter;
import org.seiko.panc.wiget.ClearEditText;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Seiko on 2017/6/6/006. Y
 */

public class SearchActivity extends BackActivity<SearchContract.View, SearchPresenter> implements SearchContract.View {

    @BindView(R.id.search_text_layout)
    TextInputLayout mInputLayout;
    @BindView(R.id.search_keyword_input)
    ClearEditText mEditText;
    @BindView(R.id.search_action_button)
    FloatingActionButton mActionButton;
    @BindView(R.id.recView)
    RecyclerView recView;
    @BindView(R.id.custom_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.load_error)
    TextView loadError;

    private SearchAdapter mAdapter;
    private InputMethodManager imm;

    @Override
    public void initViews(Bundle bundle) {
        setConfig();
        mAdapter = new SearchAdapter();
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recView.setAdapter(mAdapter);
    }

    private void setConfig() {
        /* 监听回车键 */
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mActionButton.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.search_action_button)
    void onSearchButtonClick() {
        final String text = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            mInputLayout.setError("内容不能为空");
        } else {
            mInputLayout.setError("");
            if (imm == null) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            }
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

            progressBar.setVisibility(View.VISIBLE);
            mAdapter.clear();
            mPresenter.getData(text);
        }
    }

    @Override
    public void onSuccess(List<SearchBean> list) {
        mAdapter.insertAllBack(list);
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
        return R.layout.activity_search;
    }

    @Override
    public String LayoutTitle() {
        return "搜索";
    }

    @Override
    public Context getContext() {
        return this;
    }
}
