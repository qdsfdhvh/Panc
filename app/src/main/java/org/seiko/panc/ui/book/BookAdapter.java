package org.seiko.panc.ui.book;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.seiko.panc.bean.BookSectionBean;
import org.seiko.panc.Constants;
import org.seiko.panc.Navigation;
import org.seiko.panc.R;
import org.seiko.panc.manager.ComicManager;
import org.seiko.panc.base.BaseAdapter;
import org.seiko.panc.base.BaseViewHolder;
import org.seiko.panc.bean.ComicBean;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

class BookAdapter extends BaseAdapter<BookSectionBean, BaseViewHolder > {

    private LayoutInflater inflater;
    private ComicBean content;
    private String lastUrl;
    private int lastIndex;

    BookAdapter(Context context, String lastUrl) {
        inflater = LayoutInflater.from(context);
        this.lastUrl = lastUrl;
    }

    public void setContent(ComicBean content) {
        this.content = content;
    }

    @Override
    protected BaseViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new mViewHolder(parent);
        } else if (viewType == Constants.BEAN_HEAD) {
            View head = inflater.inflate(R.layout.item_book_sectionhead, parent, false);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) head.getLayoutParams();
            params.setFullSpan(true);
            head.setLayoutParams(params);
            return new HeadHolder(head);
        }
        return null;
    }

    @Override
    protected void onNewBindViewHolder(BaseViewHolder  holder, int position) {
        if (holder instanceof mViewHolder) {
            ((BookAdapter.mViewHolder) holder).setData(get(position));
        } else if(holder instanceof HeadHolder) {
            ((BookAdapter.HeadHolder) holder).setData(get(position));
        }
    }

    //普通的
    class mViewHolder extends BaseViewHolder<BookSectionBean> {

        @BindView(R.id.tv)
        TextView tv;
        @BindView(R.id.layout)
        FrameLayout layout;

        mViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_book_section);
        }

        @Override
        public void setData(BookSectionBean bean) {
            this.bean = bean;
            tv.setText(bean.getName());
            if (lastUrl.contains(bean.getUrl())) {
                layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                lastIndex = getAdapterPosition();
            } else {
                layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
        }

        @OnClick(R.id.layout)
        void showSection() {
            if (lastIndex != getAdapterPosition()) {
                lastUrl = bean.getUrl();
                notifyItemChanged(getAdapterPosition());
                notifyItemChanged(lastIndex);
                content.setChapter(bean.getName());
                content.setLast(bean.getUrl());
                content.setIndex(getAdapterPosition() - 1); //因为head，index-1才是sections上的焦点
                ComicManager.getInstance().insertHist(content);
            }
            Navigation.showSection(mContext, content);
        }
    }

    void saveData() {
        ComicManager.getInstance().insertLike(content);
    }

    void delData() {
        ComicManager.getInstance().delLike(content);
    }

    //加粗的
    class HeadHolder extends BaseViewHolder<BookSectionBean> {
        @BindView(R.id.tv)
        TextView tv;

        HeadHolder(View itemView) {super(itemView);}

        @Override
        public void setData(BookSectionBean bean) {tv.setText(bean.getName());}

    }

    RecyclerView.ItemDecoration getItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pot = parent.getChildAdapterPosition(view);
                if (pot != 0) {
                    int offset = parent.getWidth() / 90;
                    int topset = (int) (offset * 1.5 / 2);
                    outRect.set(offset, topset, offset, topset);
                }
            }
        };
    }

}
