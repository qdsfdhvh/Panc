package org.seiko.panc.ui.main;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.seiko.panc.Constants;
import org.seiko.panc.Navigation;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseAdapter;
import org.seiko.panc.base.BaseViewHolder;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.bean.SitedBean;
import org.seiko.panc.glide.ImageLoader;
import org.seiko.panc.manager.ComicManager;
import org.seiko.panc.manager.PathManager;
import org.seiko.panc.service.DownloadManager;
import org.seiko.panc.utils.FileUtil;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

class ReaderAdapter extends BaseAdapter<ComicBean, ReaderAdapter.ViewHolder> {

    public int type;

    ReaderAdapter(int type) {
        this.type = type;
    }

    @Override
    protected ReaderAdapter.ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    protected void onNewBindViewHolder(ReaderAdapter.ViewHolder holder, int position) {
        holder.setData(get(position));
    }

    class ViewHolder extends BaseViewHolder<ComicBean> {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.source)
        TextView source;

        ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_grid);
        }

        @Override
        public void setData(ComicBean bean) {
            this.bean = bean;
            ImageLoader.load(mContext, iv, bean.getLogo(), bean.getUrl());
            name.setText(bean.getName());
            source.setText(bean.getSource());
        }

        @OnClick(R.id.layout)
        void showBook() {
            switch (type) {
                case Constants.TYPE_LIKE:
                case Constants.TYPE_HIST:
                    Navigation.showBook(mContext, bean.getSource(), bean.getUrl(), bean.getLogo());
                    break;
                case Constants.TYPE_DOWN:
                    Navigation.showDown(mContext, bean);
                    break;
            }
        }

        @OnLongClick
        boolean delSited() {
            new AlertDialog.Builder(mContext)
                    .setMessage("是否删除：" + bean.getName())
                    .setNegativeButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteSited();
                        }
                    })      //通知中间按钮
                    .setPositiveButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })      //通知最右按钮
                    .create()
                    .show();
            return true;
        }

        private void deleteSited() {
            remove(getAdapterPosition());
            switch (type) {
                case Constants.TYPE_LIKE:
                    ComicManager.getInstance().delLike(bean);
                    break;
                case Constants.TYPE_HIST:
                    ComicManager.getInstance().delHist(bean);
                    break;
                case Constants.TYPE_DOWN:
                    ComicManager.getInstance().delDown(bean);
                    DownloadManager.getInstance().deleteByComicUrl(bean.getUrl());
                    String path = PathManager.getBookPath(bean.getSource(), bean.getName());
                    FileUtil.deleteFile(path);
                    break;
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
