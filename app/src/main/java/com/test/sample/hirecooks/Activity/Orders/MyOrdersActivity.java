package com.test.sample.hirecooks.Activity.Orders;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.test.sample.hirecooks.Adapter.Orders.MyOrdersAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.NewOrder.Root;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.OrderApi;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private OrderApi mService;
    private User user;
    private MyOrdersAdapter ordersAdapter;
    private View appRoot;
    private TextView order_status_text;
    private LinearLayout no_orders,orders_layout,no_internet_connection_layout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Root> newOrder;
    private boolean checkNet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorders_layout);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Orders");
        initViews();
        if(NetworkUtil.checkInternetConnection(this)) {
            getMyOrders(user.getId());
            orders_layout.setVisibility( View.VISIBLE );
            no_internet_connection_layout.setVisibility(View.GONE );
        }
        else {
            orders_layout.setVisibility( View.GONE );
            no_internet_connection_layout.setVisibility( View.VISIBLE );
        }
    }

    private void initViews() {
        user = SharedPrefManager.getInstance(this).getUser();
        appRoot = findViewById(R.id.appRoot);
        orders_layout = findViewById(R.id.orders_layout);
        no_internet_connection_layout = findViewById(R.id.no_internet_connection_layout);
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
                        getMyOrders(user.getId());
                    }
                }, 3000);
            }
        });
    }

    private void getMyOrders(int id) {
        mService = ApiClient.getClient().create(OrderApi.class);
        Call<List<Root>> call = mService.getOrdersByUserId(id);
        call.enqueue(new Callback<List<Root>>() {
            @Override
            public void onResponse(Call<List<Root>> call, Response<List<Root>> response) {
                if (response.code() == 200 ) {
                    for(Root root:response.body()){
                        if(!root.getError()){
                            if(root.getOrders_table()!=null&&root.getOrders_table().size()!=0){
                                recyclerView.setVisibility(View.VISIBLE);
                                no_orders.setVisibility(View.GONE);
                                Collections.reverse(root.getOrders_table());
                                ordersAdapter = new MyOrdersAdapter(MyOrdersActivity.this,root.getOrders_table());
                                recyclerView.setAdapter(ordersAdapter);
                                ordersAdapter.notifyDataSetChanged();
                            }else{
                                recyclerView.setVisibility(View.GONE);
                                no_orders.setVisibility(View.VISIBLE);
                                order_status_text.setText("No Orders Available");
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Root>> call, Throwable t) {
                Toast.makeText(MyOrdersActivity.this,R.string.error, Toast.LENGTH_SHORT).show();
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
        getMyOrders(user.getId());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getMyOrders(user.getId());
    }
}
