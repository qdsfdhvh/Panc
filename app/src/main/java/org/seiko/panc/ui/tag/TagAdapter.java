package org.seiko.panc.ui.tag;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.seiko.panc.Navigation;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseAdapter;
import org.seiko.panc.base.BaseViewHolder;
import org.seiko.panc.bean.TagBean;
import org.seiko.panc.glide.ImageLoader;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

class TagAdapter extends BaseAdapter<TagBean, TagAdapter.ViewHolder> {

    @Override
    protected TagAdapter.ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    protected void onNewBindViewHolder(TagAdapter.ViewHolder holder, int position) {
        holder.setData(get(position));
    }

    class ViewHolder extends BaseViewHolder<TagBean> {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.newSection)
        TextView newSection;
        @BindView(R.id.updateTime)
        TextView updateTime;

        ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_tag);
        }

        @Override
        public void setData(TagBean bean) {
            this.bean = bean;
            name.setText(bean.getName());
            showImg(iv, bean.getLogo());
            showText(name, bean.getName());
            showText(newSection, bean.getNewSection(), "更新到：");
            String status = "";
            if (!TextUtils.isEmpty(bean.getStatus())) {
                status = "[" + bean.getStatus() + "]";
            }
            showText(updateTime, bean.getUpdateTime(), status);
        }

        void showText(TextView tv, String txt, String... param) {
            if (TextUtils.isEmpty(txt)) {
                tv.setVisibility(View.GONE);
            } else {
                if (param.length > 0 ) {
                    txt = param[0] + txt;
                }
                tv.setVisibility(View.VISIBLE);
                tv.setText(txt);
            }
        }

        void showImg(ImageView iv, String url) {
            if (TextUtils.isEmpty(url)) {
                iv.setVisibility(View.GONE);
            } else {
                iv.setVisibility(View.VISIBLE);
                ImageLoader.load(mContext, iv, url, bean.getUrl());
            }
        }

        @OnClick(R.id.layout)
        void showBook() {
            Navigation.showBook(mContext, bean.getSource(), bean.getUrl(), bean.getLogo());
        }

    }

}
