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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.test.sample.hirecooks.Adapter.RecievedOrdersAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.BaseActivity;
import com.test.sample.hirecooks.Models.Order.Order;
import com.test.sample.hirecooks.Models.Order.Results;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.OrderApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecievedOrderActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private OrderApi mService;
    private User user;
    private List<Order> ordersList;
    private List<Order> orders;
    private RecievedOrdersAdapter ordersAdapter;
    private View appRoot;
    private TextView order_status_text;
    private LinearLayout no_orders;
    BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        getOrders();

        bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.order_new) {
                    if(ordersList!=null){
                        orders = new ArrayList<>();
                        for(Order order: ordersList){
                            if(order.getOrderStatus().equalsIgnoreCase("Pending")){
                                orders.add(order);
                                if(orders!=null&&orders.size()!=0){
                                    recyclerView.setVisibility(View.VISIBLE);
                                    no_orders.setVisibility(View.GONE);
                                    ordersAdapter = new RecievedOrdersAdapter(RecievedOrderActivity.this,orders);
                                    recyclerView.setAdapter(ordersAdapter);
                                }else{
                                    recyclerView.setVisibility(View.GONE);
                                    no_orders.setVisibility(View.VISIBLE);
                                    order_status_text.setText("No New Orders Available");
                                }
                            }
                        }
                    }
                }else if (item.getItemId()==R.id.order_cancel) {
                    if(ordersList!=null){
                        orders = new ArrayList<>();
                        for(Order order: ordersList){
                            if(order.getOrderStatus().equalsIgnoreCase("Cancelled")){
                                orders.add(order);
                                if(orders!=null&&orders.size()!=0){
                                    recyclerView.setVisibility(View.VISIBLE);
                                    no_orders.setVisibility(View.GONE);
                                    ordersAdapter = new RecievedOrdersAdapter(RecievedOrderActivity.this,orders);
                                    recyclerView.setAdapter(ordersAdapter);
                                }else{
                                    recyclerView.setVisibility(View.GONE);
                                    no_orders.setVisibility(View.VISIBLE);
                                    order_status_text.setText("No Cancelled Orders Available");
                                }
                            }
                        }
                    }
                }else if (item.getItemId()==R.id.order_processing) {
                    if(ordersList!=null){
                        orders = new ArrayList<>();
                        for(Order order: ordersList){
                            if(order.getOrderStatus().equalsIgnoreCase("Prepairing")){
                                orders.add(order);
                                if(orders!=null&&orders.size()!=0){
                                    recyclerView.setVisibility(View.VISIBLE);
                                    no_orders.setVisibility(View.GONE);
                                    ordersAdapter = new RecievedOrdersAdapter(RecievedOrderActivity.this,orders);
                                    recyclerView.setAdapter(ordersAdapter);
                                }else{
                                    recyclerView.setVisibility(View.GONE);
                                    no_orders.setVisibility(View.VISIBLE);
                                    order_status_text.setText("No Prepairing Orders Available");
                                }
                            }
                        }
                    }
                }else if (item.getItemId()==R.id.order_shipping) {
                    if(ordersList!=null){
                        orders = new ArrayList<>();
                        for(Order order: ordersList){
                            if(order.getOrderStatus().equalsIgnoreCase("On The Way")){
                                orders.add(order);
                                if(orders!=null&&orders.size()!=0){
                                    recyclerView.setVisibility(View.VISIBLE);
                                    no_orders.setVisibility(View.GONE);
                                    ordersAdapter = new RecievedOrdersAdapter(RecievedOrderActivity.this,orders);
                                    recyclerView.setAdapter(ordersAdapter);
                                }else{
                                    recyclerView.setVisibility(View.GONE);
                                    no_orders.setVisibility(View.VISIBLE);
                                    order_status_text.setText("No Shipping Orders Available");
                                }
                            }
                        }
                    }
                }else if (item.getItemId()==R.id.order_shipped) {
                    if(ordersList!=null){
                        orders = new ArrayList<>();
                        for(Order order: ordersList){
                            if(order.getOrderStatus().equalsIgnoreCase("Delivered")){
                                orders.add(order);
                                if(orders!=null&&orders.size()!=0){
                                    recyclerView.setVisibility(View.VISIBLE);
                                    no_orders.setVisibility(View.GONE);
                                    ordersAdapter = new RecievedOrdersAdapter(RecievedOrderActivity.this,orders);
                                    recyclerView.setAdapter(ordersAdapter);
                                }else{
                                    recyclerView.setVisibility(View.GONE);
                                    no_orders.setVisibility(View.VISIBLE);
                                    order_status_text.setText("No Delivered Orders Available");
                                }
                            }
                        }
                    }
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
                        getOrders();
                    }
                }, 3000);
            }
        });
    }

    private void getOrders() {
        mService = ApiClient.getClient().create(OrderApi.class);
        Call<Results> call = mService.getOrder();
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.code() == 200 ) {
                    List<Order> orders = response.body().getOrder();
                    ordersList = new ArrayList<>();
                    if(orders!=null&&orders.size()!=0){
                        for(Order order:orders){
                            if(order!=null&&order.getFirmId().equalsIgnoreCase(user.getFirmId())){
                                if(order!=null&&!user.getUserType().equalsIgnoreCase("User")) {
                                    if (order.getFirmId().equalsIgnoreCase(user.getFirmId())) {
                                        ordersList.add(order);
                                    }
                                }
                            }
                        }
                    }
                    if(ordersList!=null&&ordersList.size()!=0) {
                        orders = new ArrayList<>();
                        for (Order order : ordersList) {
                            if (order.getOrderStatus().equalsIgnoreCase("Pending")) {
                                orders.add(order);
                                if (orders != null && orders.size() != 0) {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    no_orders.setVisibility(View.GONE);
                                    ordersAdapter = new RecievedOrdersAdapter(RecievedOrderActivity.this, orders);
                                    recyclerView.setAdapter(ordersAdapter);
                                } else {
                                    recyclerView.setVisibility(View.GONE);
                                    no_orders.setVisibility(View.VISIBLE);
                                    order_status_text.setText("No New Orders Available");
                                }
                            }
                        }
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
    public void onResume() {
        super.onResume();
        getOrders();
    }

    @Override
    public void onStop() {
        super.onStop();
        getOrders();
    }
}
