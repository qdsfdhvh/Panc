package org.seiko.panc.ui.main;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.seiko.panc.App;
import org.seiko.panc.Navigation;
import org.seiko.panc.R;
import org.seiko.panc.manager.PathManager;
import org.seiko.panc.base.BaseAdapter;
import org.seiko.panc.base.BaseViewHolder;
import org.seiko.panc.bean.SitedBean;
import org.seiko.panc.utils.FileUtil;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

class SitedAdapter extends BaseAdapter<SitedBean, SitedAdapter.ViewHolder> {

    @Override
    protected SitedAdapter.ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    protected void onNewBindViewHolder(SitedAdapter.ViewHolder holder, int position) {
        holder.setData(get(position));
    }

    class ViewHolder extends BaseViewHolder<SitedBean> {
        @BindView(R.id.tv)
        TextView tv;
        @BindView(R.id.toSwitch)
        SwitchCompat toSwitch;

        ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_sited);
        }

        @Override
        public void setData(SitedBean bean) {
            this.bean = bean;
            tv.setText(bean.getName());
            toSwitch.setChecked(bean.isSearch());
        }

        @OnClick(R.id.toSwitch)
        void setSearch() {
            boolean state = toSwitch.isChecked();
            toSwitch.setChecked(state);
            bean.setSearch(state);
            App.getInstance().getPreferenceManager().putBoolean(bean.getName(), state);
        }

        @OnClick(R.id.layout)
        void showHome() {
            Navigation.showHome(mContext, bean.getName());
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
            String path = PathManager.getSitePath(bean.getName());
            if (!TextUtils.isEmpty(path)) {
                FileUtil.deleteFile(path);
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
