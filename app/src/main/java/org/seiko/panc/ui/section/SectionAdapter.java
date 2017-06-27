package org.seiko.panc.ui.section;

import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import org.seiko.panc.R;
import org.seiko.panc.base.BaseAdapter;
import org.seiko.panc.base.BaseViewHolder;
import org.seiko.panc.bean.SectionBean;
import org.seiko.panc.glide.ImageLoader;
import org.seiko.panc.manager.PathManager;
import org.seiko.panc.wiget.ScaleImageView;

import java.io.File;

import butterknife.BindView;

/**
 * Created by Seiko on 2017/6/5/005. Y
 */

class SectionAdapter extends BaseAdapter<SectionBean, SectionAdapter.ViewHolder> {

    private String refUrl;
    private String path;
//    private SparseArray<Bitmap[]> sparseArray;

    SectionAdapter(String refUrl) {
        this.refUrl = refUrl;
//        sparseArray = new SparseArray<>();
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    protected SectionAdapter.ViewHolder onNewCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    protected void onNewBindViewHolder(SectionAdapter.ViewHolder holder, int position) {
        holder.setData(get(position));
    }

    class ViewHolder extends BaseViewHolder<SectionBean> {
        @BindView(R.id.iv)
        ScaleImageView iv;
//        @BindView(R.id.recView)
//        RecyclerView recView;

        ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_section);
        }

        @Override
        public void setData(final SectionBean bean) {
            this.bean = bean;

            if (!TextUtils.isEmpty(path) && !bean.getUrl().contains("/Panc/download/")) {
                String file = PathManager.getSectionImgPath(path, bean.getIndex());
                if (new File(file).exists()) {
                    Log.d("SectionAdapter", "本地图片：" + file);
                    bean.setUrl(file);
                }
            }

            ImageLoader.load2(mContext, iv, bean, refUrl);
            //bitmap回收有问题
//            final String url = bean.getUrl();
//            Bitmap[] mBitmap = sparseArray.get(url.hashCode());
//            if (mBitmap != null) {
//                loadImg(recView, mBitmap);
//            } else {
//                iv.setVisibility(View.VISIBLE);
//                SectionCallBack callback = new SectionCallBack() {
//                    @Override
//                    public void run(Bitmap bitmap) {
//                        Bitmap[] mBitmap = getBitmaps(bitmap);
//                        sparseArray.put(url.hashCode(), mBitmap);
//                        loadImg(recView, mBitmap);
//                    }
//                };
//                ImageLoader.load2(mContext, iv, bean, refUrl, callback);
//            }
        }

//        private void loadImg(RecyclerView recView, Bitmap[] bitmaps) {
//            iv.setVisibility(View.GONE);
//            recView.setLayoutManager(getLayoutManager());
//            recView.setRecycledViewPool(getRecycledPool());
//            recView.setAdapter(getAdapter(bitmaps));
//        }
//
//        private LinearLayoutManager layoutManager;
//        private LinearLayoutManager getLayoutManager() {
//            if (layoutManager == null) {
//                layoutManager = new LinearLayoutManager(mContext);
//                layoutManager.setRecycleChildrenOnDetach(true);
//                layoutManager.setOrientation(OrientationHelper.VERTICAL);
//            }
//            return layoutManager;
//        }
//
//        private MyRecyclerAdapter recycleAdapter;
//        private MyRecyclerAdapter getAdapter(Bitmap[] bitmaps) {
//            if (recycleAdapter == null) {
//                recycleAdapter = new MyRecyclerAdapter();
//            }
//            recycleAdapter.setBitmaps(bitmaps);
//            return recycleAdapter;
//        }
//
//        private RecyclerView.RecycledViewPool mRecycledPool;
//        private RecyclerView.RecycledViewPool getRecycledPool() {
//            if (mRecycledPool == null) {
//                mRecycledPool = new RecyclerView.RecycledViewPool();
//            }
//            return mRecycledPool;
//        }
    }

//    private Bitmap[] getBitmaps(final Bitmap bm) {
//        int ImgWidth = bm.getWidth();
//        int ImgHeight = bm.getHeight();
//
//        int sh = 2000; //小于4096
//        int c = ImgHeight / sh;
//        int count = ImgHeight % sh==0?c:c+1;
//
//        Bitmap[] bs = new Bitmap[count];
//        for(int i=0;i<count;i++) {
//            int height = i==(count-1) ? ImgHeight - i * sh:sh;
//            bs[i] = Bitmap.createBitmap(bm, 0, sh*i, ImgWidth, height);
//            if(bs[i] == null){
//                throw new IllegalArgumentException("bitmap is null,pos at " + i);
//            }
//        }
//        bm.recycle();
//        return bs;
//    }

//    void recycle() {
//        if (sparseArray != null) {
//            for (int i=0;i<sparseArray.size();i++) {
//                Bitmap[] bitmaps = sparseArray.valueAt(i);
//                for (Bitmap bitmap : bitmaps) {
//                    if (bitmap != null && !bitmap.isRecycled()) {
//                        bitmap.recycle();
//                    }
//                }
//            }
//            sparseArray.clear();
//            sparseArray = null;
//        }
//    }
}
