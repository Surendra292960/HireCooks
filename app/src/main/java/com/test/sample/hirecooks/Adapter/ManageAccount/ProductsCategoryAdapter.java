package com.test.sample.hirecooks.Adapter.ManageAccount;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Scene;

import com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts.EditCategoryActivity;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.R;

import java.util.List;

public class ProductsCategoryAdapter extends RecyclerView.Adapter<ProductsCategoryAdapter.ViewHolder> {
    private Context mCtx;
    private List<Offer> categories;
    Scene aScene;
    private ViewGroup sceneRoot;


    public ProductsCategoryAdapter(Context mCtx, List<Offer> categories) {
        this.mCtx = mCtx;
        this.categories = categories;
    }

    @Override
    public ProductsCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_text, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProductsCategoryAdapter.ViewHolder holder, int position) {
        Offer category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(mCtx, EditCategoryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Category", categories.get(position));
                    intent.putExtras(bundle);
                    mCtx.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) mCtx).toBundle());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories==null?0:categories.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            categoryName = itemLayoutView.findViewById(R.id.category_text);
        }
    }
}