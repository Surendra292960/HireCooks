package com.test.sample.hirecooks.Activity.Orders;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.test.sample.hirecooks.Adapter.Orders.RecievedorderDetailAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.NewOrder.Order;
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.NewOrder.Root;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.OrderApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecievedOrderActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private OrderApi mService;
    private User user;
    private LinearLayout appRoot;
    private RecievedOrdersAdapter ordersAdapter;
    private TextView order_status_text;
    private LinearLayout no_orders;
    BottomNavigationView bottomNavigationView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static String status = null;
    private List<Root> newOrder;
    ArrayList<OrdersTable> mOrdersTableList;
    private static String order_id;
    private boolean checkNet = false;

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
        if(NetworkUtil.checkInternetConnection(this)) {
            if(user.getUserType().equalsIgnoreCase( "Admin" )||user.getUserType().equalsIgnoreCase( "Manager" )){
                initViews();
            }
            checkNet = true;
            getCurrentOrders("Pending");
            bottomNavigationView=findViewById(R.id.bottom_nav);
            bottomNavigationView.setOnNavigationItemSelectedListener( item -> {
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
            checkNet = false;
            showAlert(getResources().getString(R.string.no_internet));
        }
    }

    private void initViews() {
        appRoot = findViewById(R.id.appRoot);
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
                                    Collections.reverse(mOrdersTableList);
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

    private void getOrdersByOrderId(String orderid) {
        order_id = orderid;
        mService = ApiClient.getClient().create(OrderApi.class);
        Call<List<Root>> call = mService.getOrdersByOrderId(orderid);
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
                                    Collections.reverse(mOrdersTableList);
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
                            order_status_text.setText("No Orders Available");
                        }
                    }else{
                        recyclerView.setVisibility( View.GONE );
                        no_orders.setVisibility(View.VISIBLE);
                        order_status_text.setText("No Orders Available");
                    }
                    });
                } else {
                    recyclerView.setVisibility( View.GONE );
                    no_orders.setVisibility(View.VISIBLE);
                    order_status_text.setText("No Orders Available");
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


    public class RecievedOrdersAdapter extends RecyclerView.Adapter<RecievedOrdersAdapter.OrdersViewHolder> {
        private Context mCtx;
        private List<OrdersTable> orderList;
        private List<OrdersTable> updateOrderStatus;
        private List<Order> order;
        User user = SharedPrefManager.getInstance(mCtx).getUser();
        private String status;

        public RecievedOrdersAdapter(Context mCtx, List<OrdersTable> orderList) {
            this.mCtx = mCtx;
            this.orderList = orderList;
            this.order = order;
        }

        @Override
        public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.recieved_orders_cardview, parent, false);
            return new OrdersViewHolder(view);
        }

        @Override
        public void onBindViewHolder(OrdersViewHolder holder, int position) {
            final OrdersTable ordersTable = orderList.get(position);
            if(ordersTable!=null) {
                holder.user_name.setText("Name  :  "+ ordersTable.getUser_name());
                holder.payment_method.setText("Payment  :  "+"COD");
                holder.shipping_charge.setText("Shipping Charge  :  \u20B9 "+ordersTable.getShipping_price());
                holder.phone_number.setText("Phone  :  "+ordersTable.getUser_phone());
                holder.itemTotalAmount.setText("Total Amount  :   \u20B9 " + ordersTable.getTotal_amount());
                holder.order_id.setText("# " + ordersTable.getOrder_id());
                holder.order_date_time.setText("Order On  :  "+ordersTable.getOrder_date_time());
                holder.order_address.setText("Address  :  "+ordersTable.getOrder_address());
                holder.lottie_delivery.setVisibility( View.GONE );
                holder.lottie_prepaire.setVisibility( View.GONE );
                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
                holder.image.startAnimation(animation1);
                if (ordersTable.getOrder_status().equalsIgnoreCase("Cancelled")) {
                    holder.item_status.setTextColor( Color.RED);
                    holder.item_status.setText(ordersTable.getOrder_status());
                    holder.update_order_layout.setVisibility(View.GONE);
                }else {
                    holder.item_status.setText(ordersTable.getOrder_status());
                    holder.update_order_layout.setVisibility(View.VISIBLE);
                }
                if (ordersTable.getConfirm_status().equalsIgnoreCase("Confirmed")) {
                    holder.order_confirm.setVisibility(View.VISIBLE);
                    holder.order_confirm.setBackgroundColor( ContextCompat.getColor(mCtx, R.color.style_color_primary));
                    holder.order_confirm.setText(ordersTable.getConfirm_status());
                    holder.accept_order.setVisibility(View.GONE);
                    holder.image.clearAnimation();
                }else{
                    holder.accept_order.setVisibility(View.VISIBLE);
                    holder.order_confirm.setVisibility(View.GONE);

                    holder.accept_order.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            status = "Confirmed";
                            ordersTable.setConfirm_status( status );
                            acceptOrders(holder,ordersTable.getOrder_id(),ordersTable);
                            holder.accept_order.setVisibility(View.GONE);
                            holder.order_confirm.setVisibility(View.VISIBLE);
                            holder.order_confirm.setBackgroundColor( ContextCompat.getColor(mCtx, R.color.style_color_primary));
                            holder.image.clearAnimation();
                        }
                    });
                }

                holder.itemImage.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_transition_animation));
                holder.cardlist_item.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_scale_animation));

                holder.order_pending.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        status = "Pending";
                        ordersTable.setOrder_status( status );
                        if (ordersTable.getConfirm_status().equalsIgnoreCase("Confirmed")) {
                            updateOrderStatus(holder,ordersTable.getOrder_id(),ordersTable);
                        }else{
                            showalertbox(v,"Accept Order First");
                        }
                    }
                });
                holder.order_prepairing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        status = "Prepairing";
                        ordersTable.setOrder_status( status );
                        if (ordersTable.getConfirm_status().equalsIgnoreCase("Confirmed")) {
                            updateOrderStatus(holder,ordersTable.getOrder_id(),ordersTable);
                        }else{
                            showalertbox(v,"Accept Order First");
                        }
                    }
                });  holder.order_delivered.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        status = "Delivered";
                        ordersTable.setOrder_status( status );
                        if (ordersTable.getConfirm_status().equalsIgnoreCase("Confirmed")) {
                            updateOrderStatus(holder,ordersTable.getOrder_id(),ordersTable);
                        }else{
                            showalertbox(v,"Accept Order First");
                        }
                    }
                });  holder.order_cancelled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        status = "Cancelled";
                        ordersTable.setOrder_status( status );
                        updateOrderStatus(holder,ordersTable.getOrder_id(),ordersTable);
                    }
                });  holder.order_ontheway.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        status = "On The Way";
                        ordersTable.setOrder_status( status );
                        if (ordersTable.getConfirm_status().equalsIgnoreCase("Confirmed")) {
                            updateOrderStatus(holder,ordersTable.getOrder_id(), ordersTable);
                        }else{
                            showalertbox(v,"Accept Order First");
                        }
                    }
                }); holder.order_details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ordersTable.getOrders()!=null&&ordersTable.getOrders().size()!=0) {
                            if(holder.order_product_layout.getVisibility()==View.GONE){
                                holder.order_product_layout.setVisibility( View.VISIBLE );
                                RecievedorderDetailAdapter adapter = new RecievedorderDetailAdapter( mCtx,ordersTable.getOrders() );
                                holder.order_product_recycler.setAdapter( adapter );
                            }else if(holder.order_product_layout.getVisibility()==View.VISIBLE){
                                holder.order_product_layout.setVisibility( View.GONE );
                            }

                        }else{
                            holder.order_product_layout.setVisibility( View.GONE );
                        }
                    }
                });
            }
        }

        public void showalertbox(View views, String string) {
            final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( mCtx);
            View view =  LayoutInflater.from(views.getRootView().getContext()).inflate(R.layout.show_alert_message, null);
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


        public void showAlertDialog(Context context, User user, Order order) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("What do you wants to do ?");
            builder.setMessage("Are you sure");
            builder.setPositiveButton("Order Details", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!user.getUserType().equalsIgnoreCase("User")&&!user.getUserType().equalsIgnoreCase("Cook")) {
                        Intent intent = new Intent(mCtx, RecievedOrderDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Orders", order);
                        intent.putExtras(bundle);
                        intent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mCtx.startActivity(intent);
                    }
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
        private void updateOrderStatus(OrdersViewHolder holder, Integer id, OrdersTable ordersTable) {
            updateOrderStatus = new ArrayList<>(  );
            updateOrderStatus.add( ordersTable );
            Gson gson = new Gson();
            String json = gson.toJson( updateOrderStatus );
            System.out.println( "Suree ; "+json );
            OrderApi mService = ApiClient.getClient().create(OrderApi.class);
            Call<List<Root>> call = mService.updateOrderStatus(id,updateOrderStatus);
            call.enqueue(new Callback<List<Root>>() {
                @Override
                public void onResponse(Call<List<Root>> call, Response<List<Root>> response) {
                    if (response.code() == 200 ) {
                        for(Root root:response.body()){
                            if(root.getError()==false){
                                Toast.makeText(mCtx,root.getMessage(),Toast.LENGTH_LONG).show();
                                holder.accept_order.setVisibility(View.GONE);
                                getCurrentOrders(status);
                            }
                        }

                    } else {
                        Toast.makeText(mCtx,status,Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Root>> call, Throwable t) {
                    Toast.makeText(mCtx,R.string.error, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void acceptOrders(OrdersViewHolder holder, Integer id, OrdersTable order_confirm) {
            updateOrderStatus = new ArrayList<>(  );
            updateOrderStatus.add( order_confirm );
            OrderApi mService = ApiClient.getClient().create(OrderApi.class);
            Call<List<Root>> call = mService.acceptOrders(id,updateOrderStatus);
            call.enqueue(new Callback<List<Root>>() {
                @Override
                public void onResponse(Call<List<Root>> call, Response<List<Root>> response) {
                    if (response.code() == 200 ) {
                        for(Root root:response.body()){
                            if(root.getError()==false){
                                Toast.makeText(mCtx,root.getMessage(),Toast.LENGTH_LONG).show();
                                holder.accept_order.setVisibility(View.GONE);
                            }else{
                                Toast.makeText(mCtx,root.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(mCtx,response.code(),Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Root>> call, Throwable t) {
                    Toast.makeText(mCtx,R.string.error, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderList==null?0:orderList.size();
        }

        class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView shipping_charge,itemName,user_name,payment_method,phone_number, itemTotalAmount, item_count, order_id, order_date_time,order_address,item_status,image,item_with_quantity;
            ImageView itemImage;
            AppCompatButton order_details;
            CardView cardlist_item;
            LinearLayout update_order_layout,order_product_layout;
            RecyclerView order_product_recycler;
            LottieAnimationView lottie_delivery,lottie_prepaire;
            AppCompatButton accept_order,order_pending,order_prepairing,order_delivered,order_cancelled,order_ontheway,order_confirm;

            public OrdersViewHolder(View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.item_name);
                itemTotalAmount = itemView.findViewById(R.id.order_totalAmount);
                itemImage = itemView.findViewById(R.id.product_thumb);
                //item_count = itemView.findViewById(R.id.item_quantity);
                order_id = itemView.findViewById(R.id.order_id);
                order_date_time = itemView.findViewById(R.id.order_date_time);
                order_address = itemView.findViewById(R.id.order_address);
                cardlist_item = itemView.findViewById(R.id.cardlist_item);
                image = itemView.findViewById(R.id.image);
                accept_order = itemView.findViewById(R.id.accept_order);
                order_pending = itemView.findViewById(R.id.order_pending);
                order_prepairing = itemView.findViewById(R.id.order_prepairing);
                order_delivered = itemView.findViewById(R.id.order_delivered);
                order_cancelled = itemView.findViewById(R.id.order_cancelled);
                order_ontheway = itemView.findViewById(R.id.order_ontheway);
                item_status = itemView.findViewById(R.id.item_status);
                order_confirm = itemView.findViewById(R.id.confim_order);
                item_with_quantity = itemView.findViewById(R.id.item_with_quantity);
                lottie_delivery = itemView.findViewById(R.id.lottie_delivery);
                lottie_prepaire = itemView.findViewById(R.id.lottie_prepaire);
                update_order_layout = itemView.findViewById(R.id.update_order_layout);
                order_product_layout = itemView.findViewById(R.id.order_product_layout);
                order_product_recycler = itemView.findViewById(R.id.order_product_recycler);
                order_details = itemView.findViewById(R.id.order_details);
                payment_method = itemView.findViewById(R.id.payment_method);
                user_name = itemView.findViewById(R.id.user_name);
                shipping_charge = itemView.findViewById(R.id.shipping_charge);
                phone_number = itemView.findViewById(R.id.user_phone_number);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                System.out.println("click: " + getPosition());
            }
        }
    }

}
