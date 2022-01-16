package com.test.sample.hirecooks.Activity.Favourite;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.lifecycle.ViewModelProvider;

import com.test.sample.hirecooks.Adapter.SubCategory.SubcategoryAdapter;
import com.test.sample.hirecooks.Fragments.Home.HolderFragment;
import com.test.sample.hirecooks.Fragments.Home.HomeFragment;
import com.test.sample.hirecooks.Models.Chat.ListObject;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.ActivitySubCategoryBinding;

import java.util.List;

public class FavouriteActivity extends BaseActivity {
    private ActivitySubCategoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding.mToolbarInterface.mToolbar.setVisibility(View.VISIBLE);
        binding.mToolbarInterface.goBack.setOnClickListener(v->finish());
        if(NetworkUtil.checkInternetConnection(this)) {
            binding.subcategoryLayout.setVisibility( View.VISIBLE );
            binding.noInternetConnectionLayout.setVisibility(View.GONE );
            getWishList();
        }
        else {
            binding.subcategoryLayout.setVisibility( View.GONE );
            binding.noResultFound.setVisibility( View.GONE );
            binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
    }

    @SuppressLint("NewApi")
    public void getWishList() {
        List<Subcategory> subcategoryList;
        subcategoryList = getFavourite();
        if (subcategoryList.size() != 0) {
            binding.noResultFound.setVisibility(View.GONE);
            binding.recyclerview.setVisibility(View.VISIBLE);
            SubcategoryAdapter adapter = new SubcategoryAdapter(FavouriteActivity.this, subcategoryList, ListObject.TYPE_CARDVIEW_HORIZONTAL);
            binding.recyclerview.setAdapter(adapter);
        }else{
            binding.noResultFound.setVisibility(View.VISIBLE);
        }
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
