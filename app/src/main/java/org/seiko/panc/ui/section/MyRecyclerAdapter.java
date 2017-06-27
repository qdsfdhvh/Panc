package org.seiko.panc.ui.section;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import org.seiko.panc.R;

/**
 * Created by k on 2016/8/11. Y
 */
class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
    private Bitmap bitmap[] = new Bitmap[5];

    void setBitmaps(Bitmap[] id) {
        this.bitmap = id;
    }

    @Override
    public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section_layout, parent, false);
        return new MyRecyclerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.image.setImageBitmap(bitmap[position]);
    }

    @Override
    public int getItemCount() {
        return bitmap.length;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
