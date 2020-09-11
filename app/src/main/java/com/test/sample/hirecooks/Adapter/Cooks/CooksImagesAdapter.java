package com.test.sample.hirecooks.Adapter.Cooks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImages;
import com.test.sample.hirecooks.R;

import java.util.List;

public class CooksImagesAdapter extends RecyclerView.Adapter<com.test.sample.hirecooks.Adapter.Cooks.CooksImagesAdapter.ViewHolder> {
    private Context mCtx;
    private List<CooksImages> cooksImages;

    public CooksImagesAdapter(Context mCtx, List<CooksImages> cooksImages) {
        this.mCtx = mCtx;
        this.cooksImages = cooksImages;
    }

    @Override
    public com.test.sample.hirecooks.Adapter.Cooks.CooksImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cooks_promotion_adapter, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(com.test.sample.hirecooks.Adapter.Cooks.CooksImagesAdapter.ViewHolder holder, int position) {
        CooksImages cooksImage = cooksImages.get(position);
        if(cooksImage!=null){
            Picasso.with(mCtx).load(cooksImage.getLink()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return cooksImages==null?0:cooksImages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imageView = itemLayoutView.findViewById(R.id.imageView);
        }
    }
}