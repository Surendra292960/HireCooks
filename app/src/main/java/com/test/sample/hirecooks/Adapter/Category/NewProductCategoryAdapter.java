package com.test.sample.hirecooks.Adapter.Category;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.SubCategory.SubCategoryActivity.SubCategoryActivity;
import com.test.sample.hirecooks.Models.NewProductsCategory.NewProductCategory;
import com.test.sample.hirecooks.R;

import java.util.List;

public class NewProductCategoryAdapter extends RecyclerView.Adapter<NewProductCategoryAdapter.ViewHolder> {
    private Context mCtx;
    private List<NewProductCategory> categories;

    public NewProductCategoryAdapter(Context mCtx, List<NewProductCategory> categories) {
        this.mCtx = mCtx;
        this.categories = categories;
    }

    @Override
    public NewProductCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_product_category_cardview, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewProductCategoryAdapter.ViewHolder holder, int position) {
        NewProductCategory newProductCategory = categories.get(position);
        if(newProductCategory!=null){
            holder.new_product_category_name.setText(newProductCategory.getCategoryName());
            Picasso.with(mCtx).load(newProductCategory.getLink()).placeholder(R.drawable.no_image).into(holder.new_product_category_image);

            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(mCtx, SubCategoryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("NewProductCategory", categories.get(position));
                        intent.putExtras(bundle);
                        mCtx.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) mCtx).toBundle());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return categories==null?0:categories.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView new_product_category_name;
        private ImageView new_product_category_image;
        private CardView cardview;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            new_product_category_name = itemLayoutView.findViewById(R.id.new_product_category_name);
            new_product_category_image = itemLayoutView.findViewById(R.id.new_product_category_image);
            cardview = itemLayoutView.findViewById(R.id.cardview);
        }
    }
}