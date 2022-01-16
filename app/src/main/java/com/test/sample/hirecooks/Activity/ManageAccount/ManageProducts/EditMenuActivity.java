package com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.test.sample.hirecooks.Adapter.ManageAccount.ProductsCategoryAdapter;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.ActivitySubCategoryBinding;

public class EditMenuActivity extends AppCompatActivity {
    private User user;
    private ActivitySubCategoryBinding binding;
    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        user = SharedPrefManager.getInstance( this ).getUser();
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        binding.mToolbarInterface.mToolbar.setVisibility(View.VISIBLE);
        binding.mToolbarInterface.goBack.setOnClickListener(v->finish());
        if(NetworkUtil.checkInternetConnection(this)) {
            binding.subcategoryLayout.setVisibility( View.VISIBLE );
            binding.noInternetConnectionLayout.setVisibility(View.GONE );
            if(user.getUserType().equalsIgnoreCase( "Admin" )||user.getUserType().equalsIgnoreCase( "SuperAdmin" )||user.getUserType().equalsIgnoreCase( "Manager" )) {
                binding.subcategoryLayout.setVisibility( View.VISIBLE );
                getCategory();
            }else{
                binding.subcategoryLayout.setVisibility( View.GONE );
            }
        }
        else {
            binding.subcategoryLayout.setVisibility( View.GONE );
            binding.noResultFound.setVisibility( View.GONE );
            binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
    }

    private void getCategory() {
        viewModel.getMenue().observe(this, menues -> {
            if(menues.size()!=0){
                binding.recyclerview.setVisibility(View.VISIBLE);
                binding.recyclerview.setLayoutManager(new GridLayoutManager(this, 4));
                ProductsCategoryAdapter adapter = new ProductsCategoryAdapter( EditMenuActivity.this);
                binding.recyclerview.setAdapter ( adapter );
                adapter.setMenue(menues);
            }
        });
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
