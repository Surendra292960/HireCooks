package com.test.sample.hirecooks.Adapter.Category;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.R;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CircularImageCategoryAdapter extends RecyclerView.Adapter<CircularImageCategoryAdapter.ViewHolder> {
    private Context mCtx;
    private List<Offer> offers;

    public CircularImageCategoryAdapter(Context mCtx, List<Offer> offers) {
        this.mCtx = mCtx;
        this.offers = offers;
    }

    @Override
    public CircularImageCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.circular_image_category, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CircularImageCategoryAdapter.ViewHolder holder, int position) {
        Offer offer = offers.get(position);
        if(offer!=null){
            holder.circular_image_category_name.setText(offer.getName());
            Picasso.with(mCtx).load(offer.getLink()).into(holder.circular_category_image);
        }
    }

    @Override
    public int getItemCount() {
        return offers==null?0:offers.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView circular_image_category_name;
        private CircleImageView circular_category_image;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            circular_image_category_name = itemLayoutView.findViewById(R.id.circular_image_category_name);
            circular_category_image = itemLayoutView.findViewById(R.id.circular_category_image);
        }
    }
}