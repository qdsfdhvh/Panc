package org.seiko.panc.ui.book;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.seiko.panc.bean.BookSectionBean;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseAdapter;
import org.seiko.panc.base.BaseViewHolder;
import org.seiko.panc.service.DownloadFlag;
import org.seiko.panc.service.DownloadManager;
import org.seiko.panc.service.DownloadStatus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

class BookDownAdapter extends BaseAdapter<BookSectionBean, BookDownAdapter.ViewHolder> {

    @Override
    protected BookDownAdapter.ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    protected void onNewBindViewHolder(BookDownAdapter.ViewHolder holder, int position) {
        holder.setData(get(position));
    }

    class ViewHolder extends BaseViewHolder<BookSectionBean> {
        @BindView(R.id.layout)
        LinearLayout layout;
        @BindView(R.id.tv)
        TextView tv;
        @BindView(R.id.check)
        CheckBox check;

        ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_book_down);
        }

        @Override
        public void setData(BookSectionBean bean) {
            this.bean = bean;
            tv.setText(bean.getName());
            DownloadStatus status= DownloadManager.getInstance().queryStatus(bean.getUrl());
            if (status != null) {
                bean.setFlag(status.getFlag());
            }

            switch (bean.getFlag()) {
                case DownloadFlag.WAITING:
                case DownloadFlag.COMPLETED:
                    layout.setAlpha(0.7f);
                    layout.setClickable(false);
                    check.setChecked(true);
                    break;
                default:
                    layout.setAlpha(1f);
                    layout.setClickable(true);
                    if (bean.getFlag() == DownloadFlag.NORMAL) {
                        check.setChecked(true);
                    } else {
                        check.setChecked(false);
                    }
                    break;
            }
        }

        @OnClick(R.id.layout)
        void toDown() {
            if (check.isChecked()) {
                check.setChecked(false);
                bean.setFlag(0);
            } else {
                check.setChecked(true);
                bean.setFlag(DownloadFlag.NORMAL);
            }
        }

    }

    RecyclerView.ItemDecoration getItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int offset = parent.getWidth() / 90;
                int topset = (int) (offset * 1.5 / 2);
                outRect.set(offset, topset, offset, topset);
            }
        };
    }
}
