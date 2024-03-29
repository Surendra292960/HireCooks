package com.test.sample.hirecooks.Adapter.Offer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.SubCategory.SubCategoryActivity;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    private Context mCtx;
    private List<Category> offers;

    public OfferAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_cardview, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OfferAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category offer = offers.get(position);
        if(offer!=null){
            holder.circular_image_name.setText(offer.getName());
            holder.progress_dialog.setVisibility( View.GONE );
            Glide.with(mCtx).load(offer.getLink()).into(holder.circular_image);

            holder.offers_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(mCtx, SubCategoryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Video", offers.get(position));
                        intent.putExtras(bundle);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mCtx.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return offers==null?0:offers.size();
    }

    public void setCategory(List<Category> offer) {
        this.offers = offer;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView circular_image_name;
        private CircleImageView circular_image;
        private LinearLayout offers_layout;
        private ProgressBar progress_dialog;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            circular_image_name = itemLayoutView.findViewById(R.id.circular_image_name);
            circular_image = itemLayoutView.findViewById(R.id.circular_image);
            offers_layout = itemLayoutView.findViewById(R.id.offers_layout);
            progress_dialog = itemLayoutView.findViewById(R.id.progress_dialog);
        }
    }
}