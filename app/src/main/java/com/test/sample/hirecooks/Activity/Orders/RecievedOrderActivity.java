package com.test.sample.hirecooks.Activity.Orders;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.test.sample.hirecooks.Activity.Search.SearchResultActivity;
import com.test.sample.hirecooks.Adapter.Orders.OrdersAdapter;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.ActivitySubCategoryBinding;

import java.util.Collections;

public class RecievedOrderActivity extends BaseActivity {
    private User user;
    private OrdersAdapter ordersAdapter;
    private static String status = null;
    private ActivitySubCategoryBinding binding;
    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().addFlags(flags);
        getWindow().setExitTransition(new Explode());
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        binding.mToolbarInterface.mToolbar.setVisibility(View.VISIBLE);
        binding.bottomNavigationView.setVisibility(View.VISIBLE);
        binding.footerView.checkout.setOnClickListener(v -> startActivity( new Intent( this, PlaceOrderActivity.class )));
        binding.mToolbarInterface.search.setOnClickListener(v -> startActivity( new Intent( this, SearchResultActivity.class )));
        binding.mToolbarInterface.goBack.setOnClickListener(view1 -> finish());
        user = SharedPrefManager.getInstance(this).getUser();
        if(NetworkUtil.checkInternetConnection(this)) {
            binding.subcategoryLayout.setVisibility( View.VISIBLE );
            binding.noInternetConnectionLayout.setVisibility( View.GONE );
            if(user.getUserType().equalsIgnoreCase( "Admin" )||user.getUserType().equalsIgnoreCase( "Manager" )){
                initViews();
            }
            getCurrentOrders("Pending");
            binding.bottomNavigationView.setOnNavigationItemSelectedListener( item -> {
                if (item.getItemId()==R.id.order_new) {
                    getCurrentOrders("Pending");
                }else if (item.getItemId()==R.id.order_cancel) {
                    getCurrentOrders("Cancelled");
                }else if (item.getItemId()==R.id.order_processing) {
                    getCurrentOrders("Prepairing");
                }else if (item.getItemId()==R.id.order_shipping) {
                    getCurrentOrders("On The Way");
                }else if (item.getItemId()==R.id.order_shipped) {
                    getCurrentOrders("Delivered");
                }
                return true;
            } );
        }
        else {
            binding.subcategoryLayout.setVisibility( View.GONE );
            binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
    }

    private void initViews() {
        binding.swipeToRefresh.setOnRefreshListener(() -> new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.swipeToRefresh.setRefreshing(false);
                getCurrentOrders(status);
            }
        }, 3000));

        NestedScrollView nested_content = findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY < oldScrollY) { // up
                //animateToolabarNavigation(false);
                animateBottomNavigation(false);
            }
            if (scrollY > oldScrollY) { // down
                 //animateToolabarNavigation(true);
                animateBottomNavigation(true);
            }
        });
    }

    boolean isBottomAnchorNavigationHide = false;

    private void animateBottomNavigation(final boolean hide) {
        if (isBottomAnchorNavigationHide && hide || !isBottomAnchorNavigationHide && !hide) return;
        isBottomAnchorNavigationHide = hide;
        int moveY = hide ? (2 * binding.bottomNavigationView.getHeight()) : 0;
        binding.bottomNavigationView.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    @SuppressLint("NewApi")
    private void getCurrentOrders(String order_status) {
        viewModel.getCurrentOrders(order_status).observe(this,ordersResponses ->ordersResponses.forEach(ordersResponse -> {
            if(!ordersResponse.getError()){
                if(ordersResponse.getOrders_table().size() != 0){
                    Collections.reverse(ordersResponse.getOrders_table());
                    binding.noResultFound.setVisibility(View.GONE);
                    binding.recyclerview.setVisibility(View.VISIBLE);
                    ordersAdapter = new OrdersAdapter(RecievedOrderActivity.this,ordersResponse.getOrders_table(),"RecievedOrder");
                    binding.recyclerview.setAdapter(ordersAdapter);
                }
            }else{
                binding.recyclerview.setVisibility(View.GONE);
                binding.noResultFound.setVisibility(View.VISIBLE);
            }
        }));
    }

    @SuppressLint("NewApi")
    private void getOrdersByOrderId(String orderid) {
        viewModel.getOrdersByOrderId(orderid).observe(this,ordersResponses ->ordersResponses.forEach(ordersResponse -> {
            if(!ordersResponse.getError()){
                if(ordersResponse.getOrders_table().size() != 0){
                    Collections.reverse(ordersResponse.getOrders_table());
                    binding.noResultFound.setVisibility(View.GONE);
                    binding.recyclerview.setVisibility(View.VISIBLE);
                    ordersAdapter = new OrdersAdapter(RecievedOrderActivity.this,ordersResponse.getOrders_table(),"RecievedOrder");
                    binding.recyclerview.setAdapter(ordersAdapter);
                }
            }else{
                binding.recyclerview.setVisibility(View.GONE);
                binding.noResultFound.setVisibility(View.VISIBLE);
            }
        }));
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
    protected void onResume() {
        super.onResume();
        getCurrentOrders("Pending");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.getItem(0);
        SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint( "Enter Order Id" );
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setFocusable(true);
        searchItem.expandActionView();

        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getOrdersByOrderId(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getOrdersByOrderId(newText);
                return false;
            }
        } );
        return super.onCreateOptionsMenu(menu);
    }
}
