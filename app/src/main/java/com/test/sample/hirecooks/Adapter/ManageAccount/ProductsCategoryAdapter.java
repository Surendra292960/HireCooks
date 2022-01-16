package com.test.sample.hirecooks.Adapter.ManageAccount;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.Category.CategoryActivity;
import com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts.EditCategoryActivity;
import com.test.sample.hirecooks.Activity.SubCategory.SubCategoryActivity;
import com.test.sample.hirecooks.Models.Menue.Menue;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.databinding.VendersRecyclerviewBinding;

import java.util.List;

public class ProductsCategoryAdapter extends RecyclerView.Adapter<ProductsCategoryAdapter.ViewHolder>  {
    private Context mCtx;
    private List<Menue> menus;

    public ProductsCategoryAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductsCategoryAdapter.ViewHolder(VendersRecyclerviewBinding.inflate(LayoutInflater.from(mCtx)));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Menue menu = menus.get(position);
        holder.binding.menueName.setVisibility(View.VISIBLE);
        holder.binding.menueName.setText(menu.getName());
        Glide.with(mCtx).load(menu.getLink()).into(holder.binding.vendersImage);
        holder.binding.venderLayout.setOnClickListener(view -> {
            Intent intent = new Intent(mCtx, EditCategoryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("ProductCategory",menus.get(position));
            intent.putExtras(bundle);
            mCtx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return menus==null ? 0 : menus.size();
    }

    public void setMenue(List<Menue> menus) {
        this.menus = menus;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        VendersRecyclerviewBinding binding;

        public ViewHolder(@NonNull VendersRecyclerviewBinding recyclerviewBinding) {
            super(recyclerviewBinding.getRoot());
            binding = recyclerviewBinding;
        }
    }
}
