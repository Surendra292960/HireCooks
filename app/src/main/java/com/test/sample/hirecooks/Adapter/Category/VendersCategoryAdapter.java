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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.test.sample.hirecooks.Activity.VenderSubCategory.VenderSubCategoryProducts;
import com.test.sample.hirecooks.Models.VendersCategory.VendersCategory;
import com.test.sample.hirecooks.R;

import java.util.List;

public class VendersCategoryAdapter extends RecyclerView.Adapter<VendersCategoryAdapter.ViewHolder> {
    private Context mCtx;
    private List<VendersCategory> categories;

    public VendersCategoryAdapter(Context mCtx, List<VendersCategory> categories) {
        this.mCtx = mCtx;
        this.categories = categories;
    }

    @Override
    public VendersCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.venders_category_text_recyclerview, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VendersCategoryAdapter.ViewHolder holder, int position) {
        VendersCategory category = categories.get(position);
        holder.categoryName.setText(category.getCategoryName());
        holder.categoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent(mCtx, VenderSubCategoryProducts.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("VendersCategory", categories.get(position));
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
        public LinearLayout categoryLayout;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            categoryLayout = itemLayoutView.findViewById(R.id.categoryLayout);
            categoryName = itemLayoutView.findViewById(R.id.category_name);
        }
    }
}