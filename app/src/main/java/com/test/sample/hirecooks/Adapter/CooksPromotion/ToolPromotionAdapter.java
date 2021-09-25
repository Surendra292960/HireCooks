package com.test.sample.hirecooks.Adapter.CooksPromotion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.R;

import java.util.List;

public class ToolPromotionAdapter extends RecyclerView.Adapter<ToolPromotionAdapter.ViewHolder> {
    private Context mCtx;
    private List<Offer> offers;

    public ToolPromotionAdapter(Context mCtx, List<Offer> offers) {
        this.mCtx = mCtx;
        this.offers = offers;
    }

    @Override
    public ToolPromotionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cooks_promotion_adapter, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ToolPromotionAdapter.ViewHolder holder, int position) {
        Offer offer = offers.get(position);
        if(offer!=null){
            holder.progress_dialog.setVisibility( View.VISIBLE );
            Picasso.with(mCtx).load(offer.getLink()).into( holder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progress_dialog.setVisibility( View.GONE );
                }

                @Override
                public void onError() {

                }
            } );
        }
    }

    @Override
    public int getItemCount() {
        return offers==null?0:offers.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ProgressBar progress_dialog;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imageView = itemLayoutView.findViewById(R.id.imageView);
            progress_dialog = itemLayoutView.findViewById(R.id.progress_dialog);
        }
    }
}