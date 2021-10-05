package com.test.sample.hirecooks.Adapter.Category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Scene;

import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.SubCategory.SubCategoryActivity;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context mCtx;
    private List<Category> categories;
    Scene aScene;
    private ViewGroup sceneRoot;

    public CategoryAdapter(Context mCtx, List<Category> categories) {
        this.mCtx = mCtx;
        this.categories = categories;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_cardview, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.progress_dialog.setVisibility( View.GONE );
        Glide.with(mCtx).load(category.getLink()).into( holder.categoryImage);
        holder.categoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, SubCategoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Video", categories.get(position));
                intent.putExtras(bundle);
                intent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                mCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories==null?0:categories.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public ImageView categoryImage;
        private ProgressBar progress_dialog;
        public LinearLayout categoryLayout;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            categoryLayout = itemLayoutView.findViewById(R.id.category);
            categoryName = itemLayoutView.findViewById(R.id.category_name);
            categoryImage = itemLayoutView.findViewById(R.id.category_image);
            progress_dialog = itemLayoutView.findViewById(R.id.progress_dialog);

       /*     itemLayoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });*/
        }
    }
}