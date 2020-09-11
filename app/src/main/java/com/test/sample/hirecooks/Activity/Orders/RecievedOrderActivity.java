package com.test.sample.hirecooks.Activity.Orders;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.test.sample.hirecooks.Adapter.RecievedOrdersAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Models.Order.Order;
import com.test.sample.hirecooks.Models.Order.Results;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.OrderApi;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecievedOrderActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private OrderApi mService;
    private User user;
    private RecievedOrdersAdapter ordersAdapter;
    private View appRoot;
    private TextView order_status_text;
    private LinearLayout no_orders;
    BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static String status = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        getWindow().addFlags(flags);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
        }
        setContentView(R.layout.activity_recieved_order);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recieved Orders");
        user = SharedPrefManager.getInstance(this).getUser();
        appRoot = findViewById(R.id.appRoot);
        initViews();
        getCurrentOrders("Pending");

        bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recieved_orders_recycler);
        no_orders = findViewById(R.id.no_orders);
        order_status_text = findViewById(R.id.order_status_text);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        getCurrentOrders(status);
                    }
                }, 3000);
            }
        });

        NestedScrollView nested_content = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    // animateSearchBar(false);
                    animateBottomNavigation(false);
                }
                if (scrollY > oldScrollY) { // down
                    // animateSearchBar(true);
                    animateBottomNavigation(true);
                }
            }
        });
    }

    boolean isBottomAnchorNavigationHide = false;

    private void animateBottomNavigation(final boolean hide) {
        if (isBottomAnchorNavigationHide && hide || !isBottomAnchorNavigationHide && !hide) return;
        isBottomAnchorNavigationHide = hide;
        int moveY = hide ? (2 * bottomNavigationView.getHeight()) : 0;
        bottomNavigationView.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private void getCurrentOrders(String order_status) {
        status = order_status;
        mService = ApiClient.getClient().create(OrderApi.class);
        Call<Results> call = mService.getCurrentOrders(user.getFirmId(),order_status);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.code() == 200 ) {
                    List<Order> orders = response.body().getOrder();
                    if(orders!=null&&orders.size()!=0){
                        recyclerView.setVisibility(View.VISIBLE);
                        no_orders.setVisibility(View.GONE);
                        ordersAdapter = new RecievedOrdersAdapter(RecievedOrderActivity.this,orders);
                        recyclerView.setAdapter(ordersAdapter);
                    }else{
                        recyclerView.setVisibility(View.GONE);
                        no_orders.setVisibility(View.VISIBLE);
                        order_status_text.setText("No "+order_status+" Orders Available");
                    }

                } else {
                    Toast.makeText(RecievedOrderActivity.this,"Failed due to: "+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                Toast.makeText(RecievedOrderActivity.this,R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
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
        getCurrentOrders("Pending");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getCurrentOrders("Pending");
    }
}
