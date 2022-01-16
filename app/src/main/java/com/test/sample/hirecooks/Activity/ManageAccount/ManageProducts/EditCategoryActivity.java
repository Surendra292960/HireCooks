package com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.Menue.Menue;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.ActivitySubCategoryBinding;
import com.test.sample.hirecooks.databinding.CategoryCardviewBinding;
import java.util.List;

public class EditCategoryActivity extends AppCompatActivity {
    private String categoryName;
    private Menue menue;
    private User user;
    private ViewModel viewModel;
    private ActivitySubCategoryBinding binding;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        user = SharedPrefManager.getInstance( this ).getUser();
        binding.mToolbarInterface.mToolbar.setVisibility(View.VISIBLE);
        binding.mToolbarInterface.goBack.setOnClickListener(v->finish());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            categoryName= bundle.getString("CategoryName");
            menue = (Menue) bundle.getSerializable("ProductCategory");
            if(NetworkUtil.checkInternetConnection(this)) {
                binding.subcategoryLayout.setVisibility( View.VISIBLE );
                binding.noInternetConnectionLayout.setVisibility(View.GONE );
                if(menue.getId()!=0){
                    getCategory(menue.getId());
                }else {
                    Toast.makeText( this, "Comming soon", Toast.LENGTH_SHORT ).show();
                }
            }
            else {
                binding.subcategoryLayout.setVisibility( View.GONE );
                binding.noResultFound.setVisibility( View.GONE );
                binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
            }
        }

        if(/*user.getUserType().equalsIgnoreCase( "Admin" )||*/user.getUserType().equalsIgnoreCase( "SuperAdmin" )){
            binding.add.setVisibility( View.VISIBLE );
            binding.add.setOnClickListener(v -> {
                if(menue.getId()!=0&&menue!=null){
                    Intent intent = new Intent(EditCategoryActivity.this, StartEditCategory.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("CreateCategory", menue.getId());
                    intent.putExtras(bundle1);
                    startActivity(intent);
                }
            });
        }
    }

    @SuppressLint("NewApi")
    private void getCategory(Integer id) {
        viewModel.getCategory(id).observe(this, categoryResponses -> categoryResponses.forEach(category -> {
            if (category.getCategory() != null && category.getCategory().size() != 0) {
                binding.recyclerview.setVisibility(View.VISIBLE);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
                EditCategoryAdapter mAdapter = new EditCategoryAdapter(this,category.getCategory());
                binding.recyclerview.setLayoutManager(gridLayoutManager);
                binding.recyclerview.setAdapter(mAdapter);
                //mAdapter.setCategory(category.getCategory());
            }
        }));
    }

    public class EditCategoryAdapter extends RecyclerView.Adapter<EditCategoryAdapter.ViewHolder> {
        private Context mCtx;
        private List<Category> categories;

        public EditCategoryAdapter(Context mCtx, List<Category> categories) {
            this.mCtx = mCtx;
            this.categories = categories;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new ViewHolder(CategoryCardviewBinding.inflate(LayoutInflater.from(mCtx)));
        }

        @Override
        public void onBindViewHolder(EditCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            Category category = categories.get(position);
            holder.binding.categoryName.setText(category.getName());
            Glide.with(mCtx).load(category.getLink()).into(holder.binding.categoryImage);
            holder.binding.category.setOnClickListener(v -> showalertbox(categories.get( position )));
        }

        private void showalertbox(Category category) {
            final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( EditCategoryActivity.this);
            LayoutInflater inflater = EditCategoryActivity.this.getLayoutInflater();
            View view = inflater.inflate(R.layout.edit_subcategory_alert,null);
            AppCompatTextView productsBtn = view.findViewById(R.id.no_btn);
            productsBtn.setText( "Products" );
            AppCompatTextView editBtn = view.findViewById(R.id.edit_btn);
            dialogBuilder.setView(view);
            final android.app.AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            productsBtn.setOnClickListener( v -> {
                try {
                    dialog.dismiss();
                    Intent intent = new Intent(mCtx, EditSubCategoryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Category", category);
                    intent.putExtras(bundle);
                    mCtx.startActivity(intent);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } );
            editBtn.setOnClickListener( v -> {
                try {
                    dialog.dismiss();
                    if(user.getUserType().equalsIgnoreCase("SuperAdmin")){
                        Intent intent = new Intent(mCtx, StartEditCategory.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Category", category);
                        intent.putExtras(bundle);
                        mCtx.startActivity(intent);
                    }else {
                        Toast.makeText(EditCategoryActivity.this, "Can't Edit", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } );
        }
        @Override
        public int getItemCount() {
            return categories==null?0:categories.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            CategoryCardviewBinding binding;

            public ViewHolder(@NonNull CategoryCardviewBinding itemLayoutBinding) {
                super(itemLayoutBinding.getRoot());
                binding = itemLayoutBinding;

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
