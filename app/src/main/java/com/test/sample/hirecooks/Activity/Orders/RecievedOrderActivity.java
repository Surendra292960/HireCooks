package com.test.sample.hirecooks.Activity.Orders;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.test.sample.hirecooks.Adapter.RecievedOrdersAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.NewOrder.Order;
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
    private List<Root> newOrder;
    ArrayList<OrdersTable> mOrdersTableList;

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
        Call<List<Root>> call = mService.getCurrentOrders(order_status);
        call.enqueue(new Callback<List<Root>>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(Call<List<Root>> call, Response<List<Root>> response) {
                if (response.code() == 200 ) {

                    response.body().forEach(root -> {if(root.getError()==false){
                       mOrdersTableList = new ArrayList<>(  );
                        if(root.getOrders_table()!=null&&root.getOrders_table().size()!=0){
                            for (OrdersTable ordersTable:root.getOrders_table()){
                                OrdersTable mFilteredOrdersTable = new OrdersTable();
                                ArrayList<Order> mFilteredOrders = new ArrayList<>(  );
                                mFilteredOrdersTable.setOrder_id(ordersTable.getOrder_id());
                                mFilteredOrdersTable.setOrder_date_time(ordersTable.getOrder_date_time());
                                mFilteredOrdersTable.setTotal_amount(ordersTable.getTotal_amount());
                                mFilteredOrdersTable.setShipping_price(ordersTable.getShipping_price());
                                mFilteredOrdersTable.setPayment_type(ordersTable.getPayment_type());
                                mFilteredOrdersTable.setOrder_status(ordersTable.getOrder_status());
                                mFilteredOrdersTable.setConfirm_status(ordersTable.getConfirm_status());
                                mFilteredOrdersTable.setOrder_latitude(ordersTable.getOrder_latitude());
                                mFilteredOrdersTable.setOrder_longitude(ordersTable.getOrder_latitude());
                                mFilteredOrdersTable.setOrder_address(ordersTable.getOrder_address());
                                mFilteredOrdersTable.setOrder_sub_address(ordersTable.getOrder_sub_address());
                                mFilteredOrdersTable.setOrder_pincode(ordersTable.getOrder_pincode());
                                mFilteredOrdersTable.setUser_id(ordersTable.getUser_id());
                                mFilteredOrdersTable.setUser_name(ordersTable.getUser_name());
                                mFilteredOrdersTable.setUser_email(ordersTable.getUser_email());
                                mFilteredOrdersTable.setUser_phone(ordersTable.getUser_phone());
                                for (Order order:ordersTable.getOrders()){
                                    if(order.getOrderId()==ordersTable.getOrder_id()){
                                        if(order.getFirmId().equalsIgnoreCase( user.getFirmId() )) {
                                            mFilteredOrders.add( order );
                                            mFilteredOrdersTable.setOrders( mFilteredOrders );
                                        }
                                    }else {
                                        return;
                                    }
                                }
                                if(mFilteredOrdersTable.getOrders()!=null&&mFilteredOrdersTable.getOrders().size()!=0){
                                    mOrdersTableList.add( mFilteredOrdersTable );
                                }
                            }

                            recyclerView.setVisibility(View.VISIBLE);
                            no_orders.setVisibility(View.GONE);
                            ordersAdapter = new RecievedOrdersAdapter(RecievedOrderActivity.this,mOrdersTableList);
                            recyclerView.setAdapter(ordersAdapter);
                            ordersAdapter.notifyDataSetChanged();
                        }else{
                            recyclerView.setVisibility(View.GONE);
                            no_orders.setVisibility(View.VISIBLE);
                            order_status_text.setText("No "+order_status+" Orders Available");
                        }
                    }else{
                        Toast.makeText( RecievedOrderActivity.this, root.getMessage(), Toast.LENGTH_SHORT ).show();
                        recyclerView.setVisibility( View.GONE );
                        no_orders.setVisibility(View.VISIBLE);
                        order_status_text.setText("No "+order_status+" Orders Available");
                    }
                    });
                } else {
                    Toast.makeText(RecievedOrderActivity.this,"Failed due to: "+response.code(), Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility( View.GONE );
                    no_orders.setVisibility(View.VISIBLE);
                    order_status_text.setText("No "+order_status+" Orders Available");
                }
            }

            @Override
            public void onFailure(Call<List<Root>> call, Throwable t) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.getItem(0);
        SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setFocusable(true);
        searchItem.expandActionView();

        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(mOrdersTableList.size()!=0){
                    startSearch( query );
                }else{
                    showalertbox("No Match found");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mOrdersTableList.size()!=0){
                    startSearch( newText );
                }
                //adapter.getFilter().filter(newText);
                return false;
            }
        } );
        return super.onCreateOptionsMenu(menu);
    }

    public void showalertbox(String string) {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.show_alert_message,null);
        TextView ask = view.findViewById( R.id.ask );
        TextView textView = view.findViewById( R.id.text );
        ask.setText( string );
        textView.setText( "Alert !" );
        AppCompatTextView cancelBtn = view.findViewById(R.id.exit_app_btn);
        dialogBuilder.setView(view);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        cancelBtn.setOnClickListener( v -> {
            try {
                dialog.dismiss();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
    }

    private void startSearch(CharSequence text) {
        List<OrdersTable>  ordersTablesFilteredList = new ArrayList<>();
        try{
            if(mOrdersTableList!=null&&mOrdersTableList.size()!=0){
                for(int i=0;i<mOrdersTableList.size();i++) {
                    String order_id = "";

                    if(mOrdersTableList.get (i).getOrder_id()!=0){
                        order_id= String.valueOf( mOrdersTableList.get (i).getOrder_id());
                    }

                    if(order_id.toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                        ordersTablesFilteredList.add(mOrdersTableList.get(i));
                    }
                }

                if(ordersTablesFilteredList.size()!=0&&ordersTablesFilteredList!=null){
                    recyclerView.setVisibility(View.VISIBLE);
                    no_orders.setVisibility(View.GONE);
                    ordersAdapter = new RecievedOrdersAdapter (RecievedOrderActivity.this, ordersTablesFilteredList);
                    recyclerView.setAdapter (ordersAdapter);
                    ordersAdapter.notifyDataSetChanged();
                }else{
                    recyclerView.setVisibility(View.GONE);
                    no_orders.setVisibility(View.VISIBLE);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
