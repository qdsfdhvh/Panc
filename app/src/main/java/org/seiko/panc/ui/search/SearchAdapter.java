package org.seiko.panc.ui.search;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.seiko.panc.Navigation;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseAdapter;
import org.seiko.panc.base.BaseViewHolder;
import org.seiko.panc.bean.SearchBean;
import org.seiko.panc.glide.ImageLoader;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

class SearchAdapter extends BaseAdapter<SearchBean, SearchAdapter.ViewHolder> {

    @Override
    protected SearchAdapter.ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    protected void onNewBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        holder.setData(get(position));
    }

    class ViewHolder extends BaseViewHolder<SearchBean> {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.newSection)
        TextView newSection;
        @BindView(R.id.updateTime)
        TextView updateTime;
        @BindView(R.id.source)
        TextView source;

        ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_search);
        }

        @Override
        public void setData(SearchBean bean) {
            this.bean = bean;
            showText(name, bean.getName());
            showText(newSection, bean.getNewSection(), "更新至：");
            showText(updateTime, bean.getUpdateTime());
            showText(source, bean.getSource(), "来自：");
            ImageLoader.load(mContext, iv, bean.getLogo(), bean.getUrl());
        }

        void showText(TextView tv, String txt, String... param) {
            if (TextUtils.isEmpty(txt)) {
                tv.setVisibility(View.INVISIBLE);
            } else {
                if (param.length > 0 ) {
                    txt = param[0] + txt;
                }
                tv.setVisibility(View.VISIBLE);
                tv.setText(txt);
            }
        }

        @OnClick(R.id.layout)
        void showBook() {
            Navigation.showBook(mContext, bean.getSource(), bean.getUrl(), bean.getLogo());
        }

    }

}
