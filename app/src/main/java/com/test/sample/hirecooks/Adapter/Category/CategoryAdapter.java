package com.test.sample.hirecooks.Adapter.Category;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.SubCategory.SubCategoryActivity;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.databinding.CategoryCardviewBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context mCtx;
    private List<Category> categories;
    private User user;
    public CategoryAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new CategoryAdapter.ViewHolder(CategoryCardviewBinding.inflate(LayoutInflater.from(mCtx)));
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        user = SharedPrefManager.getInstance(mCtx).getUser();
        Category category = categories.get(position);
        holder.binding.categoryName.setText(category.getName());
        Glide.with(mCtx).load(category.getLink()).into( holder.binding.categoryImage);
        holder.binding.category.setOnClickListener(v -> {
            Intent intent = new Intent(mCtx, SubCategoryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("Category", categories.get(position));
            intent.putExtras(bundle);
            mCtx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories==null?0:categories.size();
    }

    public void setCategory(List<Category> category) {
        this.categories = category;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CategoryCardviewBinding binding;

        public ViewHolder(@NonNull CategoryCardviewBinding itemLayoutViewBinding) {
            super(itemLayoutViewBinding.getRoot());
            binding =itemLayoutViewBinding;
        }
    }
}