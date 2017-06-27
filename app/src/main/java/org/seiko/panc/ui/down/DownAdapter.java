package org.seiko.panc.ui.down;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.seiko.panc.Navigation;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseAdapter;
import org.seiko.panc.base.BaseViewHolder;
import org.seiko.panc.bean.ComicBean;
import org.seiko.panc.bean.DownloadBean;
import org.seiko.panc.manager.PathManager;
import org.seiko.panc.service.DownloadEvent;
import org.seiko.panc.service.DownloadFlag;
import org.seiko.panc.service.DownloadManager;
import org.seiko.panc.service.DownloadStatus;
import org.seiko.panc.service.RxDownload;
import org.seiko.panc.utils.FileUtil;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

class DownAdapter extends BaseAdapter<DownloadBean, DownAdapter.ViewHolder> {

    private CompositeDisposable mDisposables;
    private RxDownload mRxDownload;

    DownAdapter(Context context) {
        mDisposables = new CompositeDisposable();
        mRxDownload = RxDownload.getInstance(context);
    }

    @Override
    protected DownAdapter.ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    protected void onNewBindViewHolder(DownAdapter.ViewHolder holder, int position) {
        holder.setData(get(position));
    }

    class ViewHolder extends BaseViewHolder<DownloadBean> {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.progress)
        TextView progress;
        @BindView(R.id.progress1)
        ProgressBar progress1;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.down)
        Button down;

        ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_down);
        }

        @Override
        public void setData(final DownloadBean bean) {
            this.bean = bean;
            name.setText(bean.getName());

            setProgress(bean.getProgress(), bean.getMax());
            setStatus(bean.getFlag());

            mDisposables.add(mRxDownload.receiveStatus(bean.getUrl())
                    .subscribe(new Consumer<DownloadEvent>() {
                        @Override
                        public void accept(@NonNull DownloadEvent event) throws Exception {
                            if (event.getFlag() == DownloadFlag.FAILED) {
                                Throwable throwable = event.getError();
                                Log.w("TAG", throwable);
                                return;
                            }

                            DownloadStatus status = event.getDownloadStatus();
                            setProgress(status.getProgress(), status.getMax());
                            setStatus(status.getFlag());
                            bean.setFlag(status.getFlag());
                        }
                    }));
        }

        private void setStatus(int flag) {
            switch (flag) {
                case DownloadFlag.COMPLETED:
                    down.setClickable(false);
                    status.setText("已完成");
                    break;
                case DownloadFlag.WAITING:
                    down.setClickable(true);
                    status.setText("等待中");
                    break;
                case DownloadFlag.STARTED:
                    down.setClickable(true);
                    status.setText("下载中");
                    break;
            }
        }

        private void setProgress(int pro, int max) {
            progress.setText(String.valueOf(pro + "/" + max));
            progress1.setMax(max);
            progress1.setProgress(pro);
        }


        @OnClick(R.id.down)
        void startdown() {
            mDisposables.add(RxDownload.getInstance(mContext).serviceDownload(bean).subscribe());
        }

        @OnClick(R.id.layout)
        void showSection() {
            String path = PathManager.getBookBean(bean.getSource(), bean.getTitle());
            Type type = new TypeToken<ComicBean>(){}.getType();
            ComicBean comic = FileUtil.get(path, type);
            if (comic != null) {
                comic.setChapter(bean.getName());
                comic.setLast(bean.getUrl());
                Navigation.showSection(mContext, comic);
            }
        }

        @OnLongClick(R.id.layout)
        boolean delDown() {
            new AlertDialog.Builder(mContext)
                    .setMessage("是否删除：" + bean.getName())
                    .setNegativeButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete();
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

        private void delete() {
            remove(getAdapterPosition());
            DownloadManager.getInstance().delete(bean.getId());
            String path = PathManager.getSectionPath(bean.getSource(), bean.getTitle(), bean.getName());
            FileUtil.deleteFile(path);
        }
    }


    void destroy() {
        if (mDisposables != null && !mDisposables.isDisposed()) {
            mDisposables.clear();
        }
    }

    RecyclerView.ItemDecoration getItemDecoration() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int offset = parent.getWidth() / 90;
                int topset = (int) (offset * 1.5 / 2);
                outRect.set(0, 0, 0, topset);
            }
        };
    }
}
