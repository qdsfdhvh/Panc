package org.seiko.panc.ui.home;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.seiko.panc.Navigation;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseAdapter;
import org.seiko.panc.base.BaseViewHolder;
import org.seiko.panc.base.ItemType;
import org.seiko.panc.bean.TagsBean;
import org.seiko.panc.bean.TagsHeadBean;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

class TagsAdapter extends BaseAdapter<ItemType, BaseViewHolder> {

    private LayoutInflater inflater;

    TagsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    protected BaseViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new mViewHolder(parent);
        } else if (viewType == 1) {
            View head = inflater.inflate(R.layout.item_tagshead, parent, false);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) head.getLayoutParams();
            params.setFullSpan(true);
            head.setLayoutParams(params);
            return new HeadHolder(head);
        }
        return null;
    }

    @Override
    protected void onNewBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof TagsAdapter.mViewHolder) {
            ((TagsAdapter.mViewHolder) holder).setData((TagsBean) get(position));
        } else if (holder instanceof TagsAdapter.HeadHolder) {
            ((TagsAdapter.HeadHolder) holder).setData((TagsHeadBean) get(position));
        }
    }

    //普通的
    class mViewHolder extends BaseViewHolder<TagsBean> {
        @BindView(R.id.tv)
        TextView tv;

        mViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_tags);
        }

        @Override
        public void setData(TagsBean bean) {
            this.bean = bean;
            tv.setText(bean.getTitle());
        }

        @OnClick(R.id.layout)
        void showTag() {
            Navigation.showTag(mContext, bean);
        }
    }

    //加粗的
    class HeadHolder extends BaseViewHolder<TagsHeadBean> {
        @BindView(R.id.tv)
        TextView tv;

        HeadHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(TagsHeadBean bean) {
            tv.setText(bean.getName());
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
