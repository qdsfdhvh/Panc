package org.seiko.panc.ui.book;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.seiko.panc.R;
import org.seiko.panc.base.SectionItem;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.glide.ImageLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Seiko on 2017/6/10/010. Y
 */

class BookHeader implements SectionItem {

    @BindView(R.id.book_logo)
    ImageView book_logo;
    @BindView(R.id.book_name)
    TextView book_name;
    @BindView(R.id.book_author)
    TextView book_author;
    @BindView(R.id.book_intro)
    TextView book_intro;
    @BindView(R.id.source)
    TextView source;

    private ComicBean bean;
    private Context mContext;

    BookHeader(ComicBean bean) {
        this.bean = bean;
    }

    @Override
    public View createView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_book, parent, false);
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        params.setFullSpan(true);
        ButterKnife.bind(this, view);
        mContext = parent.getContext();
        return view;
    }

    @Override
    public void onBind() {
        ImageLoader.load(mContext, book_logo, bean.getLogo(), bean.getUrl());
        book_name.setText(bean.getName());
        book_author.setText(bean.getAuthor());
        book_intro.setText(bean.getIntro());
        source.setText(String.valueOf("来自：" + bean.getSource()));
    }

}
