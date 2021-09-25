package com.test.sample.hirecooks.Adapter.Images;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.R;

import static com.test.sample.hirecooks.Utils.APIUrl.BASE_URL;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    private String[] userImages;
    private Context mCtx;

    public ImagesAdapter(@NonNull Context mCtx, @NonNull String[] userImages) {
        this.mCtx = mCtx;
        this.userImages=userImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String image = userImages[position];
        Picasso.with(mCtx).load(BASE_URL+image).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return userImages==null ? 0 : userImages.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}