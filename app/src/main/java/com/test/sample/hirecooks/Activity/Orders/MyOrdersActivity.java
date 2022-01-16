package com.test.sample.hirecooks.Activity.Orders;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import androidx.lifecycle.ViewModelProvider;
import com.test.sample.hirecooks.Activity.Search.SearchResultActivity;
import com.test.sample.hirecooks.Adapter.Orders.OrdersAdapter;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.ActivitySubCategoryBinding;
import java.util.Collections;

public class MyOrdersActivity extends BaseActivity {
    private User user;
    private OrdersAdapter ordersAdapter;
    private ActivitySubCategoryBinding binding;
    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel=new ViewModelProvider(this).get(ViewModel.class);
        user = SharedPrefManager.getInstance(this).getUser();
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding.mToolbarInterface.mToolbar.setVisibility(View.VISIBLE);
        binding.footerView.checkout.setOnClickListener(v -> startActivity( new Intent( this, PlaceOrderActivity.class )));
        binding.mToolbarInterface.search.setOnClickListener(v -> startActivity( new Intent( this, SearchResultActivity.class )));
        binding.mToolbarInterface.goBack.setOnClickListener(view1 -> finish());
        if(NetworkUtil.checkInternetConnection(this)) {
            binding.subcategoryLayout.setVisibility( View.VISIBLE );
            binding.noInternetConnectionLayout.setVisibility(View.GONE );
            getMyOrders(user.getId());
        }
        else {
            binding.subcategoryLayout.setVisibility( View.GONE );
            binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
    }

    @SuppressLint("NewApi")
    private void getMyOrders(int id) {
        viewModel.getOrdersByUserId(id).observe(this,ordersResponses ->ordersResponses.forEach(ordersResponse -> {
            if(!ordersResponse.getError()){
                if(ordersResponse.getOrders_table().size() != 0){
                    Collections.reverse(ordersResponse.getOrders_table());
                    binding.noResultFound.setVisibility(View.GONE);
                    binding.recyclerview.setVisibility(View.VISIBLE);
                    ordersAdapter = new OrdersAdapter(this,ordersResponse.getOrders_table(),"MyOrders");
                    binding.recyclerview.setAdapter(ordersAdapter);
                }
            }else{
                binding.recyclerview.setVisibility(View.GONE);
                binding.noResultFound.setVisibility(View.VISIBLE);
            }
        }));

        binding.swipeToRefresh.setOnRefreshListener(() -> new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.swipeToRefresh.setRefreshing(false);
                getMyOrders(user.getId());
            }
        }, 3000));
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getMyOrders(user.getId());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getMyOrders(user.getId());
    }
}
