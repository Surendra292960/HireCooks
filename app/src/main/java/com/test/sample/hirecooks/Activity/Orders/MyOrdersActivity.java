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
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.NewOrder.Root;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.OrderApi;

import java.util.ArrayList;
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
    private LinearLayout no_orders;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Root> newOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorders_layout);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Orders");
        user = SharedPrefManager.getInstance(this).getUser();
        appRoot = findViewById(R.id.appRoot);
        initViews();
        getMyOrders(user.getId());
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
                    newOrder = new ArrayList<>(  );
                    newOrder = response.body();
                    for(Root root:newOrder){
                        if(root.getError()==false){
                            List<OrdersTable> orders = root.getOrders_table();
                            if(orders!=null&&orders.size()!=0){
                                recyclerView.setVisibility(View.VISIBLE);
                                no_orders.setVisibility(View.GONE);
                                ordersAdapter = new MyOrdersAdapter(MyOrdersActivity.this,orders);
                                recyclerView.setAdapter(ordersAdapter);
                                ordersAdapter.notifyDataSetChanged();
                            }else{
                                recyclerView.setVisibility(View.GONE);
                                no_orders.setVisibility(View.VISIBLE);
                                order_status_text.setText("No Orders Available");
                            }
                        }else{
                            Toast.makeText( MyOrdersActivity.this, root.getMessage(), Toast.LENGTH_SHORT ).show();
                            recyclerView.setVisibility( View.GONE );
                            no_orders.setVisibility(View.VISIBLE);
                            order_status_text.setText("No Orders Available");
                        }
                    }
                } else {
                    Toast.makeText(MyOrdersActivity.this,"Failed due to: "+response.code(), Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility( View.GONE );
                    no_orders.setVisibility(View.VISIBLE);
                    order_status_text.setText("No Orders Available");
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
