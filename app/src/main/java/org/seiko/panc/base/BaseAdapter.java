package org.seiko.panc.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Seiko on 2017/5/18/018. Y
 */

public abstract class BaseAdapter<T extends ItemType, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    private DataSetObservable<T> dataSet;

    public BaseAdapter() {
        dataSet = new DataSetObservable<>();
    }
    //======================================================
    protected abstract VH onNewCreateViewHolder(ViewGroup parent, int viewType);

    protected abstract void onNewBindViewHolder(VH holder, int position);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH viewHolder = createHeaderFooterViewHolder(parent, viewType);
        if (viewHolder != null) return viewHolder;
        return onNewCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (dataSet.header.is(position)) {
            dataSet.header.get(position).onBind();
        } else if (dataSet.data.is(position)) {
            onNewBindViewHolder(holder, position);
        } else if (dataSet.footer.is(position)) {
            dataSet.footer.get(position).onBind();
        } else {
            dataSet.extra.get(position).onBind();
        }
    }

    @SuppressWarnings("unchecked")
    private VH createHeaderFooterViewHolder(ViewGroup parent, int viewType) {
        List<SectionItem> tempContainer = new ArrayList<>();
        tempContainer.addAll(dataSet.header.getAll());
        tempContainer.addAll(dataSet.footer.getAll());
        tempContainer.addAll(dataSet.extra.getAll());

        for (SectionItem each : tempContainer) {
            if (each.hashCode() == viewType) {
                View view = each.createView(parent);
                return (VH) new SectionItemViewHolder(view);
            }
        }
        return null;
    }


    @Override
    public int getItemViewType(int position) {
        //用header和footer的HashCode表示它们的ItemType,
        //普通类型的数据由该数据类型的ItemType决定
        if (dataSet.header.is(position)) {
            return dataSet.header.get(position).hashCode();
        } else if (dataSet.data.is(position)) {
            return dataSet.data.get(position).itemType();
        } else if (dataSet.footer.is(position)) {
            return dataSet.footer.get(position).hashCode();
        } else {
            return dataSet.extra.get(position).hashCode();
        }
    }

    private class SectionItemViewHolder extends BaseViewHolder {

        SectionItemViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(ItemType data) {}
    }

   //======================================================
    public void addHeader(SectionItem header) {
        if (dataSet.header.size() == 0) {
            dataSet.header.add(header);
        }
    }

    public void addFooter(SectionItem footer) {
        dataSet.footer.add(footer);
    }

    public void addAll(List<? extends T> data) {
        dataSet.data.addAll(data);
        notifyDataSetChanged();
    }

    public void insertAllBack(List<? extends T> items) {
        if (dataSet.totalSize() == 0)
            addAll(items);
        else
            insertAllBack(dataSet.totalSize() - 1, items);
    }

    public void insertAllBack(int adapterPosition, List<? extends T> items) {
        dataSet.data.insertAllBack(adapterPosition, items);
        notifyItemRangeInserted(adapterPosition + 1, items.size());
    }

    public void clear() {
        dataSet.clear();
    }

    public void clearData() {
        dataSet.data.clear();
    }

    public List<T> getData() {
        return dataSet.data.getAll();
    }

    public T get(int position) {
        return dataSet.data.get(position);
    }

    public void remove(int adapterPosition) {
        dataSet.data.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    public void setReverseLayout() {
        dataSet.data.reverse();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataSet.totalSize();
    }

}
