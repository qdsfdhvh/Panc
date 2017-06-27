package org.seiko.panc.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

public abstract class BaseViewHolder<T extends ItemType> extends RecyclerView.ViewHolder {

    protected Context mContext;
    protected T bean;

    public BaseViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    public BaseViewHolder(ViewGroup parent, @LayoutRes int res) {
        super(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
        ButterKnife.bind(this, itemView);
        mContext = parent.getContext();
    }

    public abstract void setData(T bean);

}
