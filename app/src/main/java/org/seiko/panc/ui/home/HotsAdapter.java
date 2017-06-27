package org.seiko.panc.ui.home;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.seiko.panc.Navigation;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseAdapter;
import org.seiko.panc.base.BaseViewHolder;
import org.seiko.panc.bean.HotsBean;
import org.seiko.panc.glide.ImageLoader;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

class HotsAdapter extends BaseAdapter<HotsBean, HotsAdapter.ViewHolder> {

    @Override
    protected HotsAdapter.ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    protected void onNewBindViewHolder(HotsAdapter.ViewHolder holder, int position) {
        holder.setData(get(position));
    }

    class ViewHolder extends BaseViewHolder<HotsBean> {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.name)
        TextView name;

        ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_hots);
        }

        @Override
        public void setData(HotsBean bean) {
            this.bean = bean;
            name.setText(bean.getName());
            ImageLoader.load(mContext, iv, bean.getLogo(), bean.getUrl());
        }

        @OnClick(R.id.layout)
        void showBook() {
            Navigation.showBook(mContext, bean.getSource(), bean.getUrl(), bean.getLogo());
        }
    }

    RecyclerView.ItemDecoration getItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int offset = parent.getWidth() / 90;
                outRect.set(offset, 0, offset, (int) (offset * 1.5));
            }
        };
    }

}
