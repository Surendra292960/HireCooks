package com.test.sample.hirecooks.Activity.Orders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.libraries.places.api.Places;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Activity.ManageAddress.SecondryAddressActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;
import com.test.sample.hirecooks.Models.Order.Order;
import com.test.sample.hirecooks.Models.Order.Results;
import com.test.sample.hirecooks.Models.TokenResponse.Token;
import com.test.sample.hirecooks.Models.TokenResponse.Tokens;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.OnClickRateLimitedDecoratedListener;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.RazorpayPayment;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.Utils.SharedPrefToken;
import com.test.sample.hirecooks.WebApis.MapApi;
import com.test.sample.hirecooks.WebApis.NotificationApi;
import com.test.sample.hirecooks.WebApis.OrderApi;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrderActivity extends BaseActivity {
    TextView editTextAddress,editAddress,editTextHirecookMoney,editTextPromo,editTextSubTotal,
            editTextDeliveryCharge,editTextGrandTotal,editTextPayableAmount,editTextCashOnDelivery,editTextPayOnline;
    private ProgressBarUtil progressBarUtil;
    private AppCompatButton shop_now;
    private FrameLayout no_result_found;
    GoogleMap mMap;
    List<Cart> cartList = new ArrayList<>();
    LocalStorage localStorage;
    Gson gson;
    private RecyclerView recyclerView;
    Double mTotal, mTotalAmount,includedDeliveryCharges,excludedDeliveryCharges;
    String orderNo;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Place Order");
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            address = (Map) bundle.getSerializable("address");
            if(address!=null){

            }
        }

        initializeViews();
        initViews();
        getCart();
        setUpCartRecyclerview();
    }

    private void getCart() {
        cartList = getCartList();
        if(cartList.size()!=0){
            no_result_found.setVisibility(View.GONE);
            excludedDeliveryCharges = 0.0;
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
            shop_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PlaceOrderActivity.this,MainActivity.class));
                }
            });
        }
    }

    private void initViews() {
        razorpayPayment = new RazorpayPayment(this);
        progressBarUtil = new ProgressBarUtil(this);
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
        orderNo = String.valueOf(100000000 + rnd.nextInt(900000000));
        localStorage = new LocalStorage(this);
        gson = new Gson();

        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlaceOrderActivity.this, SecondryAddressActivity.class));
            }
        });

        editTextCashOnDelivery.setOnClickListener(new OnClickRateLimitedDecoratedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maps!=null){
                    if(cartList!=null&&cartList.size()!=0){
                        Order order = new Order();
                        order.setOrderId(orderNo);
                        for(Cart cart:cartList){
                            //order.setOrderId(orderNo);
                            order.setProductName(cart.getName());
                            order.setProductSellRate(cart.getSellRate());
                            order.setProductDisplayRate(cart.getDisplayRate());
                            discount = (order.getProductDisplayRate() - order.getProductSellRate());
                            displayrate = (order.getProductDisplayRate());
                            discountPercentage = (discount * 100 / displayrate);
                            order.setProductDiscount(discountPercentage);
                            order.setProductQuantity(cart.getItemQuantity());
                            order.setProductTotalAmount(cart.getTotalAmount());
                            order.setOrderStatus("Pending");
                            order.setProductImage(cart.getLink());
                            order.setFirmId(cart.getFirm_id());
                            order.setUserId(user.getId());
                            order.setPaymentMethod("COD");
                            order.setOrderPlaceId(maps.getPlaceId());
                            order.setOrderLatitude(maps.getLatitude());
                            order.setOrderLongitude(maps.getLongitude());
                            order.setOrderAddress(maps.getAddress());
                            order.setOrderSubAddress(maps.getSubAddress());
                            order.setOrderPincode(maps.getPincode());
                            order.setName(user.getName());
                            order.setEmail(user.getEmail());
                            order.setPhone(user.getPhone());
                            order.setFirmLat("Null");
                            order.setFirmLng("Null");
                            order.setFirmAddress("Null");
                            order.setFirmPincode("Null");

                            if(cart.getWeight()==null){
                                order.setOrderWeight("null");
                            }else{
                                order.setOrderWeight(cart.getWeight());
                            }
                            if (SharedPrefManager.getInstance(PlaceOrderActivity.this).isLoggedIn()) {
                                if(order.getOrderId()!=null&&order.getProductName()!=null&&order.getProductSellRate()!=0&&order.getProductDisplayRate()!=0
                                        &&order.getProductQuantity()!=0&&order.getProductTotalAmount()!=0&&order.getOrderStatus()!=null&&order.getProductImage()!=null
                                        &&order.getFirmId()!=null&&order.getUserId()!=0&&order.getOrderWeight()!=null&&order.getPaymentMethod()!=null&&order.getOrderPlaceId()!=null
                                        &&order.getOrderLatitude()!=null&&order.getOrderLongitude()!=null&&order.getOrderAddress()!=null&&order.getOrderPincode()!=0
                                        &&order.getName()!=null&&order.getEmail()!=null&&order.getPhone()!=null&&order.getFirmLat()!=null&&order.getFirmLng()!=null
                                        &&order.getFirmAddress()!=null){
                                    placeOrder(order);
                                }else{
                                    Toast.makeText(PlaceOrderActivity.this,"These Item can`t be placed please remove item and try again",Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(PlaceOrderActivity.this,"Please Login First",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }else{
                    Toast.makeText(PlaceOrderActivity.this,"Please Select Address",Toast.LENGTH_LONG).show();
                }
            }
        },5000));

        editTextPayOnline.setOnClickListener(new OnClickRateLimitedDecoratedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maps!=null){
                    if(cartList!=null&&cartList.size()!=0){
                        Order order = new Order();
                        order.setOrderId(orderNo);
                        for(Cart cart:cartList){
                            //order.setOrderId(orderNo);
                            order.setProductName(cart.getName());
                            order.setProductSellRate(cart.getSellRate());
                            order.setProductDisplayRate(cart.getDisplayRate());
                            discount = (order.getProductDisplayRate() - order.getProductSellRate());
                            displayrate = (order.getProductDisplayRate());
                            discountPercentage = (discount * 100 / displayrate);
                            order.setProductDiscount(discountPercentage);
                            order.setProductQuantity(cart.getItemQuantity());
                            order.setProductTotalAmount(cart.getTotalAmount());
                            order.setOrderStatus("Pending");
                            order.setProductImage(cart.getLink());
                            order.setFirmId(cart.getFirm_id());
                            order.setUserId(user.getId());
                            order.setPaymentMethod("Paid");
                            order.setOrderPlaceId(maps.getPlaceId());
                            order.setOrderLatitude(maps.getLatitude());
                            order.setOrderLongitude(maps.getLongitude());
                            order.setOrderAddress(maps.getAddress());
                            order.setOrderSubAddress(maps.getSubAddress());
                            order.setOrderPincode(maps.getPincode());
                            order.setName(user.getName());
                            order.setEmail(user.getEmail());
                            order.setPhone(user.getPhone());
                            order.setFirmLat("Null");
                            order.setFirmLng("Null");
                            order.setFirmAddress("Null");
                            order.setFirmPincode("Null");
                            //order.setFirmName("Null");

                            if(cart.getWeight()==null){
                                order.setOrderWeight("null");
                            }else{
                                order.setOrderWeight(cart.getWeight());
                            }
                            if (SharedPrefManager.getInstance(PlaceOrderActivity.this).isLoggedIn()) {
                                if(order.getOrderId()!=null&&order.getProductName()!=null&&order.getProductSellRate()!=0&&order.getProductDisplayRate()!=0
                                        &&order.getProductQuantity()!=0&&order.getProductTotalAmount()!=0&&order.getOrderStatus()!=null&&order.getProductImage()!=null
                                        &&order.getFirmId()!=null&&order.getUserId()!=0&&order.getOrderWeight()!=null&&order.getPaymentMethod()!=null&&order.getOrderPlaceId()!=null
                                        &&order.getOrderLatitude()!=null&&order.getOrderLongitude()!=null&&order.getOrderAddress()!=null&&order.getOrderPincode()!=0
                                        &&order.getName()!=null&&order.getEmail()!=null&&order.getPhone()!=null&&order.getFirmLat()!=null&&order.getFirmLng()!=null
                                        &&order.getFirmAddress()!=null){

                                    if(mTotalAmount!=0){
                                        razorpayPayment.startPayment(String.valueOf(mTotalAmount));

                                    /*
                                     payUsingUpi("HireCook","ramveer261@oksbi","Test Payment", String.valueOf(mTotalAmount));
                                        if(status.equals("success")){
                                            placeOrder(order);
                                    }
                                    */

                                    }
                                }else{
                                    Toast.makeText(PlaceOrderActivity.this,"These Item can`t be placed please remove item and try again",Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(PlaceOrderActivity.this,"Please Login First",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }else{
                    Toast.makeText(PlaceOrderActivity.this,"Please Select Address",Toast.LENGTH_LONG).show();
                }
            }
        },5000));

        getMapDetails();
        //getDateAndTime();
    }

    private void placeOrder(Order order) {
        Gson gson = new Gson();
        String json = gson.toJson(order);
        System.out.println("Order :"+json);
        mService = ApiClient.getClient().create(OrderApi.class);
        Call<Results> call = mService.submitOrder(order.getOrderId(),order.getProductName(),order.getProductSellRate(),order.getProductDisplayRate(),
                order.getProductDiscount(),order.getProductQuantity(), order.getProductTotalAmount(),order.getOrderStatus(),order.getProductImage(),order.getFirmId(),
                order.getUserId(),order.getOrderWeight(),order.getPaymentMethod(),order.getOrderPlaceId(),order.getOrderLatitude(),order.getOrderLongitude(),
                order.getOrderAddress(),order.getOrderSubAddress(),order.getOrderPincode(),order.getName(),order.getEmail(),order.getPhone(),order.getFirmLat()
                ,order.getFirmLng(),order.getFirmAddress(),order.getFirmPincode());
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.code() == 200 &&response.body().getError()==false) {
                    Toast.makeText(PlaceOrderActivity.this,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    localStorage.deleteCart();
                    List<Order> orderList = new ArrayList<>();
                    for(Order order1:response.body().getOrder()){
                        orderList.add(order1);
                    }
                    Intent intent = new Intent(PlaceOrderActivity.this,PlacedOrderSuccessfully.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Order",order);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getTokenFromServer(orderList);
                } else {
                    Toast.makeText(PlaceOrderActivity.this,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                Toast.makeText(PlaceOrderActivity.this,R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTokenFromServer(List<Order> orderList) {
        UserApi mService =  ApiClient.getClient().create(UserApi.class);
        Call<Tokens> call1 = mService.getTokens();
        call1.enqueue(new Callback<Tokens>() {
            @Override
            public void onResponse(Call<Tokens> call, Response<Tokens> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    progressBarUtil.hideProgress();
                    List<Token> filteredToken = new ArrayList<>();
                    Token tokens = new Token();
                    for(Order order:orderList){
                        for(Token token: response.body().getTokens()){
                            if(token.getFirm_id().equalsIgnoreCase(order.getFirmId())){
                                tokens.setFirm_id(order.getFirmId());
                                tokens.setToken(token.getToken());
                                sendNotification(tokens);
                            }
                        }
                    }
                    filteredToken.add(tokens);
                    System.out.println("Order Tokens: "+filteredToken);
                    System.out.println("Suree Token  "+ SharedPrefToken.getInstance(getApplicationContext()).getTokens().getToken());
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.failed_due_to+response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Tokens> call, Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), R.string.error+t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println("Suree: "+t.getMessage());
            }
        });
    }

    private void sendNotification(Token filteredToken) {
        NotificationApi mService = ApiClient.getClient().create(NotificationApi.class);
        Call<String> call = mService.sendNotification(filteredToken.getToken(),filteredToken.getFirm_id());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200 ) {
                    localStorage.deleteCart();
                    PlaceOrderActivity.this.finish();
                    startActivity(new Intent(PlaceOrderActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println(t.toString());
                Toast.makeText(PlaceOrderActivity.this,R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDateAndTime() {
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        int minutes = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);

        String time = hour+3 + ":" + minutes + ":" + seconds;

        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        String date = day + "-" + month + "-" + year;

       // editTextDeliveryTime.setText(date+"  "+time);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
       this.finish();
    }

    private void initializeViews() {
        try {
            Places.initialize(PlaceOrderActivity.this, Constants.locationApiKey);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getMapDetails() {
        progressBarUtil.showProgress();
        MapApi mService = ApiClient.getClient().create(MapApi.class);
        Call<Result> call = mService.getMapDetails(user.getId());
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.code() == 200 && response.body() != null && response.body().getMaps() != null) {
                    progressBarUtil.hideProgress();
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
                            mMap.clear();
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
                progressBarUtil.hideProgress();
                Toast.makeText(PlaceOrderActivity.this,R.string.error+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpCartRecyclerview() {
        cartList = getCartList();
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
        private List<Cart> cartList;
        private int DisCount = 0, DisplayRate = 0, DiscountPercentage = 0;

        public CheckoutCartAdapter(Context mCtx, List<Cart> cartList) {
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
                Cart cart = cartList.get(position);
                holder.itemName.setText(cart.getName());
                holder.itemDesc.setText(cart.getDesc());

                DisCount = (cart.getDisplayRate() - cart.getSellRate());
                DisplayRate = (cart.getDisplayRate());
                DiscountPercentage = (DisCount * 100 / DisplayRate);
                holder.item_discount.setText("Save " + DiscountPercentage + " %");

                if(cart.getItemQuantity()>1){
                    holder.itemQuantity.setText(""+cart.getItemQuantity());
                    holder.itemTotalAmt.setText("₹ "+cart.getTotalAmount());
                }else{
                    holder.itemTotalAmt.setText("₹ "+cart.getSellRate());
                    holder.itemQuantity.setText(""+cart.getItemQuantity());
                }
             /*   if(cart.getWeight()!=null){
                    holder.item_weight.setVisibility(View.VISIBLE);
                    holder.item_weight.setText(cart.getWeight());
                }else{
                    holder.item_weight.setVisibility(View.GONE);
                }*/
                holder.itemImage.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_transition_animation));
                Picasso.with(mCtx).load(cart.getLink()).into(holder.itemImage);

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
            TextView itemName, itemDesc, itemTotalAmt,itemAdd,itemRemove,itemQuantity,itemDelete,item_discount/*,item_weight*/;
            ImageView itemImage;

            public CartViewHolder(View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.item_name);
                itemDesc = itemView.findViewById(R.id.item_short_desc);
                itemTotalAmt = itemView.findViewById(R.id.total_Amount);
                itemImage = itemView.findViewById(R.id.product_thumb);
                itemAdd = itemView.findViewById(R.id.add_item);
                itemRemove = itemView.findViewById(R.id.remove_item);
                itemQuantity = itemView.findViewById(R.id.item_count);
                item_discount = itemView.findViewById(R.id.item_discount);
                itemDelete = itemView.findViewById(R.id.item_delete);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
            }
        }
    }
}
