package com.test.sample.hirecooks.Adapter.Offer;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.SubCategory.SubCategoryActivity.SubCategoryActivity;
import com.test.sample.hirecooks.Models.OfferCategory.OffersCategory;
import com.test.sample.hirecooks.R;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder> {
    private Context mCtx;
    private List<OffersCategory> offers;

    public OfferAdapter(Context mCtx, List<OffersCategory> offers) {
        this.mCtx = mCtx;
        this.offers = offers;
    }

    @Override
    public OfferAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_cardview, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OfferAdapter.ViewHolder holder, int position) {
        OffersCategory offer = offers.get(position);
        if(offer!=null){
            holder.circular_image_name.setText(offer.getCategoryName());
            Picasso.with(mCtx).load(offer.getLink()).placeholder(R.drawable.no_image).into(holder.circular_image);
            holder.offers_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(mCtx, SubCategoryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("offersCategory", offers.get(position));
                        intent.putExtras(bundle);
                        mCtx.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) mCtx).toBundle());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return offers==null?0:offers.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView circular_image_name;
        private CircleImageView circular_image;
        private LinearLayout offers_layout;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            circular_image_name = itemLayoutView.findViewById(R.id.circular_image_name);
            circular_image = itemLayoutView.findViewById(R.id.circular_image);
            offers_layout = itemLayoutView.findViewById(R.id.offers_layout);
        }
    }
}