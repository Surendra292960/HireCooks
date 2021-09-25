package com.test.sample.hirecooks.Activity.Orders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.libraries.places.api.Places;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Activity.ManageAddress.SecondryAddressActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;
import com.test.sample.hirecooks.Models.NewOrder.Order;
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.NewOrder.Root;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.TokenResponse.Token;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.RazorpayPayment;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.MapApi;
import com.test.sample.hirecooks.WebApis.NotificationApi;
import com.test.sample.hirecooks.WebApis.OrderApi;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrderActivity extends BaseActivity {
    TextView editTextAddress,editAddress,editTextHirecookMoney,editTextPromo,editTextSubTotal,
            editTextDeliveryCharge,editTextGrandTotal,editTextPayableAmount;
    Button editTextCashOnDelivery,editTextPayOnline;
    private AppCompatButton shop_now;
    private LinearLayout no_result_found,no_internet_connection_layout,place_order_layout;
    GoogleMap mMap;
    LocalStorage localStorage;
    Gson gson;
    private RecyclerView recyclerView;
    Double mTotal;
    Double mTotalAmount;
    Double includedDeliveryCharges;
    Double excludedDeliveryCharges;
    int orderNo;
    private User user;
    private Map maps;
    private RadioGroup radioPromo;
    private OrderApi mService;
    private int id, discount = 0, discountPercentage = 0, displayrate = 0;
    private int Quantity = 0, sellRate = 0,displayRate = 0, SubTotal = 0,weight = 0;
    private int RESULT_OK;
    final int UPI_PAYMENT = 0;
    private String status = "";
    private Map address;
    private RazorpayPayment razorpayPayment;
    private OrdersTable orderTable;
    private Root root;
    private List<Subcategory> cartList = new ArrayList<>();
    private com.test.sample.hirecooks.Models.NewOrder.Order order;
    private List<com.test.sample.hirecooks.Models.NewOrder.Order> orderList;
    private List<com.test.sample.hirecooks.Models.NewOrder.Order> filteredList = new ArrayList<>(  );
    private List<OrdersTable> orderTableList = new ArrayList<>(  );
    private LinearLayout all_order_place_layout;
    private List<Root> rootList = new ArrayList<>(  );
    private Date location;
    ArrayList<Order> mOrdersTable = new ArrayList<>(  );
    ArrayList<Token> mTokenList = new ArrayList<>(  );
    private boolean checkNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Place Order");
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            address = (Map) bundle.getSerializable("address_book");
            if(address!=null){

            }
        }

        initializeViews();
        initViews();
        if(NetworkUtil.checkInternetConnection(this)) {
            checkNet = true;
            no_internet_connection_layout.setVisibility( View.GONE );
            getCart();
            getDateAndTime();
            setUpCartRecyclerview();
        }
        else {
            checkNet = false;
            no_result_found.setVisibility( View.GONE );
            place_order_layout.setVisibility( View.GONE );
            no_internet_connection_layout.setVisibility( View.VISIBLE );
        }
    }


    private void getCart() {
        cartList = getnewCartList();
        if(cartList.size()!=0){
            no_result_found.setVisibility(View.GONE);
            place_order_layout.setVisibility( View.VISIBLE );
            excludedDeliveryCharges = 50.0;
            mTotal = getTotalPrice();
            mTotalAmount = mTotal + excludedDeliveryCharges;
            editTextSubTotal.setText("\u20B9 "+mTotal + "");
            editTextPromo.setText("- \u20B9 "+00 + "");
            editTextHirecookMoney.setText("- \u20B9 "+00 + "");

            editTextDeliveryCharge.setText("+ \u20B9 "+excludedDeliveryCharges + "");
            editTextGrandTotal.setText("\u20B9 "+mTotalAmount + "");
            editTextPayableAmount.setText("Payable Amount "+"\u20B9 "+mTotalAmount + "");
        }else{
            no_result_found.setVisibility(View.VISIBLE);
            place_order_layout.setVisibility( View.GONE );
            shop_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PlaceOrderActivity.this,MainActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        }
    }

    private void initViews() {
        razorpayPayment = new RazorpayPayment(this);
        place_order_layout = findViewById(R.id.place_order_layout);
        no_internet_connection_layout = findViewById(R.id.no_internet_connection_layout);
        recyclerView = findViewById(R.id.cart_rv);
        no_result_found = findViewById(R.id.no_result_found);
        shop_now = findViewById(R.id.shop_now);
        radioPromo = findViewById(R.id.radioPromo);
        editTextAddress = findViewById(R.id.editTextAddress);
        editAddress = findViewById(R.id.editAddress);
        editTextSubTotal = findViewById(R.id.editTextSubTotal);
        editTextPromo = findViewById(R.id.editTextPromo);
        editTextHirecookMoney = findViewById(R.id.editTextHirecookMoney);
        editTextDeliveryCharge = findViewById(R.id.editTextDeliveryCharge);
        editTextGrandTotal = findViewById(R.id.editTextGrandTotal);
        editTextPayableAmount = findViewById(R.id.editTextPayableAmount);
        editTextCashOnDelivery = findViewById(R.id.editTextCashOnDelivery);
        editTextPayOnline = findViewById(R.id.editTextPayOnline);
        user = SharedPrefManager.getInstance(this).getUser();

        Random rnd = new Random();
        orderNo = 100000000 + rnd.nextInt(900000000);
        String uniqueID = UUID.randomUUID().toString();
        String salt = uniqueID.replaceAll( "-" ,"");
        System.out.println( "Suree : "+salt );
        localStorage = new LocalStorage(this);
        gson = new Gson();

        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlaceOrderActivity.this, SecondryAddressActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        editTextCashOnDelivery.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkNet) {
                    if (maps != null) {
                        orderList = new ArrayList<>();
                        if (cartList != null && cartList.size() != 0) {
                            orderTable = new OrdersTable();
                            orderTable.setOrder_id( orderNo );
                            orderTable.setOrder_date_time( getDateAndTime() );
                            orderTable.setTotal_amount( mTotalAmount );
                            if (orderTable.getTotal_amount() <= 200) {
                                showalertbox( "Can`t Place Order less then \u20B9  200" );
                                return;
                            }
                            orderTable.setShipping_price( excludedDeliveryCharges );
                            orderTable.setPayment_type( "COD" );
                            orderTable.setOrder_status( "Pending" );
                            orderTable.setConfirm_status( "Not_Accepted" );
                            orderTable.setOrder_latitude( maps.getLatitude() );
                            orderTable.setOrder_longitude( maps.getLongitude() );
                            orderTable.setOrder_address( maps.getAddress() );
                            orderTable.setOrder_sub_address( maps.getSubAddress() );
                            orderTable.setOrder_pincode( maps.getPincode() );
                            orderTable.setUser_id( user.getId() );
                            orderTable.setUser_name( user.getName() );
                            orderTable.setUser_email( user.getEmail() );
                            orderTable.setUser_phone( user.getPhone() );
                            for (Subcategory subcategory : cartList) {
                                order = new com.test.sample.hirecooks.Models.NewOrder.Order();
                                order.setSubcategoryid( Integer.parseInt( subcategory.getSubcategoryid() ) );
                                order.setOrderId( orderNo );
                                order.setProductUniquekey( subcategory.getProductUniquekey() );
                                order.setName( subcategory.getName() );
                                order.setSellRate( subcategory.getSellRate() );
                                order.setDisplayRate( subcategory.getDisplayRate() );
                                order.setDiscount( (subcategory.getDisplayRate() - subcategory.getSellRate()) * subcategory.getItemQuantity() );
                                order.setQuantity( subcategory.getItemQuantity() );
                                order.setTotalAmount( subcategory.getTotalAmount() );
                                order.setLink2( String.valueOf( subcategory.getImages() ) );
                                order.setFirmId( subcategory.getFirmId() );
                                order.setOrderWeight( "Not Required" );
                                order.setFirmLat( subcategory.getFirmLat() );
                                order.setFirmLng( subcategory.getFirmLng() );
                                order.setFirmAddress( subcategory.getFirmAddress() );
                                order.setFirmPincode( subcategory.getFrimPincode() );
                                order.setBrand( subcategory.getBrand() );
                                order.setGender( subcategory.getGender() );
                                order.setAge( subcategory.getAge() );
                                order.setImages( subcategory.getImages() );
                                order.setWeights( subcategory.getWeights() );
                                order.setColors( subcategory.getColors() );
                                order.setSizes( subcategory.getSizes() );
                                orderList.add( order );
                                Set<com.test.sample.hirecooks.Models.NewOrder.Order> newList = new LinkedHashSet<>( orderList );
                                filteredList = new ArrayList<>( newList );
                            }
                            orderTable.setOrders( filteredList );
                            orderTableList.add( orderTable );
                            root = new Root();
                            root.setOrders_table( orderTableList );
                            if (SharedPrefManager.getInstance( PlaceOrderActivity.this ).isLoggedIn()) {
                                placeOrder( root );
                            } else {
                                showalertbox( "Please Login First" );
                            }
                        }
                    } else {
                        showalertbox( "Please Select Address" );
                    }
                }else{
                    showalertbox( "Please Check Your internet Connection" );
                }
            }
        } );

        editTextPayOnline.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNet) {
                    if (maps != null) {
                        orderList = new ArrayList<>();
                        if (cartList != null && cartList.size() != 0) {
                            orderTable = new OrdersTable();
                            orderTable.setOrder_id( orderNo );
                            orderTable.setOrder_date_time( getDateAndTime() );
                            orderTable.setTotal_amount( mTotalAmount );
                            if (orderTable.getTotal_amount() <= 200) {
                                showalertbox( "Can`t Place Order less then \u20B9  200" );
                                return;
                            }
                            orderTable.setPayment_type( "UPI" );
                            orderTable.setOrder_status( "Pending" );
                            orderTable.setConfirm_status( "Not_Accepted" );
                            orderTable.setShipping_price( excludedDeliveryCharges );
                            orderTable.setOrder_latitude( maps.getLatitude() );
                            orderTable.setOrder_longitude( maps.getLongitude() );
                            orderTable.setOrder_address( maps.getAddress() );
                            orderTable.setOrder_sub_address( maps.getSubAddress() );
                            orderTable.setOrder_pincode( maps.getPincode() );
                            orderTable.setUser_id( user.getId() );
                            orderTable.setUser_name( user.getName() );
                            orderTable.setUser_email( user.getEmail() );
                            orderTable.setUser_phone( user.getPhone() );
                            for (Subcategory subcategory : cartList) {
                                order = new com.test.sample.hirecooks.Models.NewOrder.Order();
                                order.setSubcategoryid( Integer.parseInt( subcategory.getSubcategoryid() ) );
                                order.setOrderId( orderNo );
                                order.setProductUniquekey( subcategory.getProductUniquekey() );
                                order.setName( subcategory.getName() );
                                order.setSellRate( subcategory.getSellRate() );
                                order.setDisplayRate( subcategory.getDisplayRate() );
                                order.setDiscount( (subcategory.getDisplayRate() - subcategory.getSellRate()) * subcategory.getItemQuantity() );
                                order.setQuantity( subcategory.getItemQuantity() );
                                order.setTotalAmount( subcategory.getTotalAmount() );
                                order.setLink2( String.valueOf( subcategory.getImages() ) );
                                order.setFirmId( subcategory.getFirmId() );
                                order.setOrderWeight( "Not Required" );
                                order.setFirmLat( subcategory.getFirmLat() );
                                order.setFirmLng( subcategory.getFirmLng() );
                                order.setFirmAddress( subcategory.getFirmAddress() );
                                order.setFirmPincode( subcategory.getFrimPincode() );
                                order.setBrand( subcategory.getBrand() );
                                order.setGender( subcategory.getGender() );
                                order.setAge( subcategory.getAge() );
                                order.setImages( subcategory.getImages() );
                                order.setWeights( subcategory.getWeights() );
                                order.setColors( subcategory.getColors() );
                                order.setSizes( subcategory.getSizes() );
                                orderList.add( order );
                                Set<com.test.sample.hirecooks.Models.NewOrder.Order> newList = new LinkedHashSet<>( orderList );
                                filteredList = new ArrayList<>( newList );
                            }
                            orderTable.setOrders( filteredList );
                            orderTableList.add( orderTable );
                            root = new Root();
                            root.setOrders_table( orderTableList );

                            if (SharedPrefManager.getInstance( PlaceOrderActivity.this ).isLoggedIn()) {
                                if (mTotalAmount != 0) {
                                    razorpayPayment.startPayment( String.valueOf( mTotalAmount ) );

            /*           payUsingUpi("HireCook","ramveer261@oksbi","Test Payment", String.valueOf(mTotalAmount));
                                if(status.equals("success")){
                                    placeOrder(root);
                                }*/
                                }
                            } else {
                                showalertbox( "Please Login First" );
                            }
                        }
                    } else {
                        showalertbox( "Please Select Address" );
                    }
                }else{
                    showalertbox( "Please Check Your internet Connection" );
                }
            }
        } );
        getMapDetails();
    }

    private void placeOrder(final Root root){
        mService  = ApiClient.getClient().create(OrderApi.class);
        Call<List<Root>> call = mService.addOrder(root.getOrders_table());
        call.enqueue( new Callback<List<Root>>() {
            @Override
            public void onResponse(Call<List<Root>> call, Response<List<Root>> response) {
                if (response.code() == 200 ) {
                    for(Root root1:response.body()){
                        if(root1.getError()==false){
                            Toast.makeText(PlaceOrderActivity.this,root1.getMessage(), Toast.LENGTH_SHORT).show();
                            localStorage.deleteCart();
                            getCart();
                            for (OrdersTable ordersTable:root.getOrders_table()) {
                                for(Order order:ordersTable.getOrders() ){
                                    getTokenFromServer(order.getFirmId(),root.getOrders_table());
                                }
                            }
                        }else{
                            Toast.makeText(PlaceOrderActivity.this,root1.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(PlaceOrderActivity.this,response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Root>> call, Throwable t) {
                Toast.makeText(PlaceOrderActivity.this,R.string.error, Toast.LENGTH_SHORT).show();
            }
        } );
    }

    private void getTokenFromServer(String firm_id, List<OrdersTable> orders_table) {
        UserApi mService =  ApiClient.getClient().create(UserApi.class);
        Call<TokenResult> call1 = mService.getTokenByFirmId(user.getId(),firm_id);
        call1.enqueue(new Callback<TokenResult>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<TokenResult> call, Response<TokenResult> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    NotificationApi mService = ApiClient.getClient().create(NotificationApi.class);
                    Call<String> calls = mService.sendNotification(response.body().getToken().getToken(),firm_id);
                    calls.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.code() == 200 ) {
                                localStorage.deleteCart();
                                Intent intent = new Intent( PlaceOrderActivity.this,PlacedOrderSuccessfully.class );
                                Bundle bundle = new Bundle(  );
                                bundle.putSerializable( "OrdersTable", (Serializable) orders_table );
                                intent.putExtras( bundle );
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity( intent );
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            System.out.println(t.toString());
                            Toast.makeText(PlaceOrderActivity.this,R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<TokenResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error+t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println("Suree: "+t.getMessage());
            }
        });
    }


    private void initializeViews() {
        try {
            Places.initialize(PlaceOrderActivity.this, Constants.locationApiKey);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getMapDetails() {
        MapApi mService = ApiClient.getClient().create(MapApi.class);
        Call<Result> call = mService.getMapDetails(user.getId());
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.code() == 200 && response.body() != null && response.body().getMaps() != null) {
                    try{
                        if(address!=null){
                            maps = new Map();
                            maps.setPlaceId(address.getPlaceId());
                            maps.setLatitude(address.getLatitude());
                            maps.setLongitude(address.getLongitude());
                            maps.setFirm_id(address.getFirm_id());
                            maps.setUserId(address.getUserId());
                            maps.setPincode(address.getPincode());
                            maps.setAddress(address.getAddress());
                            maps.setSubAddress(address.getSubAddress());
                            editTextAddress.setText(address.getAddress());
                        }else{
                            maps = response.body().getMaps();
                            editTextAddress.setText(maps.getAddress());

//                            mMap.clear();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //editTextAddress.setText(maps.getAddress());
                } else {
                    Toast.makeText(PlaceOrderActivity.this,"Failed due to: "+response.errorBody(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(PlaceOrderActivity.this,R.string.error+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpCartRecyclerview() {
        cartList = getnewCartList();
        CheckoutCartAdapter adapter = new CheckoutCartAdapter(PlaceOrderActivity.this,cartList);
        recyclerView.setAdapter(adapter);
    }

    void payUsingUpi(  String name,String upiId, String note, String amount) {
        Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();
        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        // check if intent resolves
        if(null != chooser.resolveActivity(PlaceOrderActivity.this.getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(PlaceOrderActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+resultCode );
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }
    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(PlaceOrderActivity.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            //status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }
            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(PlaceOrderActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successfull: "+approvalRefNo);
                // placeOrder(order);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(PlaceOrderActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
            }
            else {
                Toast.makeText(PlaceOrderActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(PlaceOrderActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        getMapDetails();
        super.onStart();
    }


    public class CheckoutCartAdapter extends RecyclerView.Adapter<PlaceOrderActivity.CheckoutCartAdapter.CartViewHolder> {
        private Context mCtx;
        private List<Subcategory> cartList;
        private int DisCount = 0, DisplayRate = 0, DiscountPercentage = 0;

        public CheckoutCartAdapter(Context mCtx, List<Subcategory> cartList) {
            this.mCtx = mCtx;
            this.cartList = cartList;
        }

        @Override
        public PlaceOrderActivity.CheckoutCartAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.checkout_cart_adapter, parent, false);
            return new PlaceOrderActivity.CheckoutCartAdapter.CartViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(PlaceOrderActivity.CheckoutCartAdapter.CartViewHolder holder, int position) {
            if(cartList!=null){
                localStorage = new LocalStorage(mCtx);
                gson = new Gson();
                Subcategory cart = cartList.get(position);
                holder.itemName.setText(cart.getName());
                holder.itemDesc.setText(cart.getDiscription());

                DisCount = (cart.getDisplayRate() - cart.getSellRate());
                DisplayRate = (cart.getDisplayRate());
                DiscountPercentage = (DisCount * 100 / DisplayRate);
                holder.item_discount.setText("Save " + DiscountPercentage + " %");

                if(cart.getItemQuantity()>1){
                    holder.itemQuantity.setText(""+cart.getItemQuantity());
                    holder.itemSellRate.setText("₹ "+cart.getSellRate());
                }else{
                    holder.itemSellRate.setText("₹ "+cart.getSellRate());
                    holder.itemQuantity.setText(""+cart.getItemQuantity());
                }
             /*   if(cart.getWeight()!=null){
                    holder.item_weight.setVisibility(View.VISIBLE);
                    holder.item_weight.setText(cart.getWeight());
                }else{
                    holder.item_weight.setVisibility(View.GONE);
                }*/
                holder.itemImage.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_transition_animation));
                if(cart.getImages().size()!=0&&cart.getImages()!=null){
                    holder.progress_dialog.setVisibility( View.VISIBLE );
                    Picasso.with(mCtx).load(cart.getImages().get( 0 ).getImage()).into( holder.itemImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progress_dialog.setVisibility( View.GONE );
                        }

                        @Override
                        public void onError() {

                        }
                    } );
                }

                holder.itemAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sellRate = cart.getSellRate();
                        displayRate = cart.getDisplayRate();
                        String count = holder.itemQuantity.getText().toString();
                        Quantity = Integer.parseInt(count);
                        if (Quantity >= 1) {
                            Quantity++;
                            holder.itemQuantity.setText(""+(Quantity));
                            for (int i = 0; i < cartList.size(); i++) {
                                if (cartList.get(i).getId()==cart.getId()&&cartList.get(i).getName().equalsIgnoreCase(cart.getName())&&cartList.get(i).getSellRate()==cart.getSellRate()) {
                                    SubTotal = (sellRate * Quantity);
                                    cartList.get(i).setItemQuantity(Quantity);
                                    cartList.get(i).setTotalAmount(SubTotal);
                                    String cartStr = gson.toJson(cartList);
                                    localStorage.setCart(cartStr);
                                    notifyItemChanged(position);
                                    getCart();
                                }
                            }
                        }
                    }
                });

                holder.itemRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sellRate = cart.getSellRate();
                        displayRate = cart.getDisplayRate();
                        String count = holder.itemQuantity.getText().toString();
                        Quantity = Integer.parseInt(count);
                        if (Quantity > 1) {
                            Quantity--;
                            holder.itemQuantity.setText(""+(Quantity));
                            for (int i = 0; i < cartList.size(); i++) {
                                if (cartList.get(i).getId()==cart.getId()&&cartList.get(i).getName().equalsIgnoreCase(cart.getName())&&cartList.get(i).getSellRate()==cart.getSellRate()) {
                                    SubTotal = (sellRate * Quantity);
                                    cartList.get(i).setItemQuantity(Quantity);
                                    cartList.get(i).setTotalAmount(SubTotal);
                                    String cartStr = gson.toJson(cartList);
                                    localStorage.setCart(cartStr);
                                    notifyItemChanged(position);
                                    getCart();
                                }
                            }
                        }
                    }
                });

                holder.itemDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View currentFocus = ((Activity)mCtx).getCurrentFocus();
                        if (currentFocus != null) {
                            currentFocus.clearFocus();
                        }
                        cartList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartList.size());
                        Gson gson = new Gson();
                        String cartStr = gson.toJson(cartList);
                        Log.d("CART", cartStr);
                        localStorage.setCart(cartStr);
                        ((PlaceOrderActivity) mCtx).updateTotalPrice();
                        getCart();
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return cartList==null?0:cartList.size();
        }

        class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView itemName, itemDesc, itemSellRate,itemAdd,itemRemove,itemQuantity,itemDelete,item_discount/*,item_weight*/;
            ImageView itemImage;
            private ProgressBar progress_dialog;

            public CartViewHolder(View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.item_name);
                itemDesc = itemView.findViewById(R.id.item_short_desc);
                itemSellRate = itemView.findViewById(R.id.item_sellrate);
                itemImage = itemView.findViewById(R.id.product_thumb);
                itemAdd = itemView.findViewById(R.id.add_item);
                itemRemove = itemView.findViewById(R.id.remove_item);
                itemQuantity = itemView.findViewById(R.id.item_count);
                item_discount = itemView.findViewById(R.id.item_discount);
                progress_dialog = itemView.findViewById(R.id.progress_dialog);
                itemDelete = itemView.findViewById(R.id.item_delete);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity( new Intent( PlaceOrderActivity.this,MainActivity.class ) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            startActivity( new Intent( PlaceOrderActivity.this,MainActivity.class )
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
