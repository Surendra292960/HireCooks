package com.test.sample.hirecooks.Adapter.Cooks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.Cooks.CooksActivity;
import com.test.sample.hirecooks.Models.CooksPromotion.CooksPromotion;
import com.test.sample.hirecooks.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CooksPromotionAdapter extends RecyclerView.Adapter<CooksPromotionAdapter.ViewHolder> {
    private Context mCtx;
    private List<CooksPromotion> cooksPromotion;

    public CooksPromotionAdapter(Context mCtx, List<CooksPromotion> cooksPromotion) {
        this.mCtx = mCtx;
        this.cooksPromotion = cooksPromotion;
    }

    @Override
    public CooksPromotionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cooks_promotion_adapter, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CooksPromotionAdapter.ViewHolder holder, int position) {
        CooksPromotion cooksPromotions = cooksPromotion.get(position);
        if(cooksPromotions!=null){
            holder.tag_name.setVisibility(View.VISIBLE);
            Picasso.with(mCtx).load(cooksPromotions.getLink()).into(holder.imageView);
            holder.tag_name.setText(cooksPromotions.getTagName());
            holder.cook_name.setText(cooksPromotions.getCookName());
            Picasso.with(mCtx).load(cooksPromotions.getRoundIcon()).placeholder(R.drawable.ic_profile_user).into(holder.round_icon);
            holder.cooks_promotion_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(mCtx, CooksActivity.class);
                    bundle.putSerializable("Cooks",cooksPromotions);
                    intent.putExtras(intent);
                    mCtx.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cooksPromotion==null?0:cooksPromotion.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView tag_name,cook_name;
        private CircleImageView round_icon;
        private LinearLayout cooks_promotion_lay;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imageView = itemLayoutView.findViewById(R.id.imageView);
            tag_name = itemLayoutView.findViewById(R.id.tag_name);
            round_icon = itemLayoutView.findViewById(R.id.round_icon);
            cook_name = itemLayoutView.findViewById(R.id.cook_name);
            cooks_promotion_lay = itemLayoutView.findViewById(R.id.cooks_promotion_lay);
        }
    }
}