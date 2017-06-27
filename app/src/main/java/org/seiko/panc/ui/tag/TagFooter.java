package org.seiko.panc.ui.tag;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.seiko.panc.R;
import org.seiko.panc.base.SectionItem;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Seiko on 2017/6/10/010. Y
 */

class TagFooter implements SectionItem {

    @BindView(R.id.now_page)
    TextView now_page;
    @BindView(R.id.but_prev)
    Button but_prev;
    @BindView(R.id.but_next)
    Button but_next;
    @BindView(R.id.control)
    LinearLayout control;
    @BindView(R.id.loading)
    LinearLayout loading;

    private int page;
    private TagFooterView mView;

    TagFooter(TagFooterView mView) {
        page = 1;
        this.mView = mView;
    }

    @Override
    public View createView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_tag_load, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onBind() {
        now_page.setText(String.format(Locale.CHINESE, "第%s页", page));
        if (page == 1) {
            but_prev.setVisibility(View.INVISIBLE);
        } else {
            but_prev.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.but_prev)
    void prev_page() {
        page -= 1;
        mView.toPage(page);
    }

    @OnClick(R.id.but_next)
    void next_page() {
        page += 1;
        mView.toPage(page);
    }

    int getPage() {
        return page;
    }

    interface TagFooterView {
        void toPage(int page);
    }

    void showLoad() {
        control.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        but_next.setVisibility(View.VISIBLE);
    }

    void hideLoad() {
        if (control != null) {
            control.setVisibility(View.VISIBLE);
        }
        if (loading != null) {
            loading.setVisibility(View.GONE);
        }
    }

    void isLastPage() {
        page -=1;
        but_next.setVisibility(View.INVISIBLE);
    }
}
