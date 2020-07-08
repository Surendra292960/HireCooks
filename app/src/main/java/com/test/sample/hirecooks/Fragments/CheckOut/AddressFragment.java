package com.test.sample.hirecooks.Fragments.CheckOut;

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
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.libraries.places.api.Places;
import com.google.gson.Gson;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.BaseActivity;
import com.test.sample.hirecooks.MapLocation;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;
import com.test.sample.hirecooks.Models.Order.Order;
import com.test.sample.hirecooks.Models.Order.Results;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.OnClickRateLimitedDecoratedListener;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.Utils.SharedPrefToken;
import com.test.sample.hirecooks.WebApis.MapApi;
import com.test.sample.hirecooks.WebApis.NotificationApi;
import com.test.sample.hirecooks.WebApis.OrderApi;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class AddressFragment extends Fragment {
    Context context;
    EditText editTextAddress,editTextDeliveryTime;
    TextView editTextHirecookMoney,editTextPromo,editTextSubTotal,editTextDeliveryCharge,editTextGrandTotal,editTextPayableAmount,editTextCashOnDelivery,editTextPayOnline;
    private ProgressBarUtil progressBarUtil;
    GoogleMap mMap;
    List<Cart> cartList = new ArrayList<>();
    LocalStorage localStorage;
    Gson gson;
    Double mTotal, mDeliveryCharges, mTotalAmount;
    String id,orderNo;
    private User user;
    private Map maps;
    private RadioGroup radioPromo;
    private OrderApi mService;
    private int discount = 0, discountPercentage = 0, displayrate = 0;
    private int RESULT_OK;
    final int UPI_PAYMENT = 0;
    private String status = "";

    public AddressFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_address, container, false);
        context = container.getContext();
        progressBarUtil = new ProgressBarUtil(getActivity());
        radioPromo = v.findViewById(R.id.radioPromo);
        editTextAddress = v.findViewById(R.id.editTextAddress);
        editTextDeliveryTime = v.findViewById(R.id.editTextDeliveryTime);
        editTextSubTotal = v.findViewById(R.id.editTextSubTotal);
        editTextPromo = v.findViewById(R.id.editTextPromo);
        editTextHirecookMoney = v.findViewById(R.id.editTextHirecookMoney);
        editTextDeliveryCharge = v.findViewById(R.id.editTextDeliveryCharge);
        editTextGrandTotal = v.findViewById(R.id.editTextGrandTotal);
        editTextPayableAmount = v.findViewById(R.id.editTextPayableAmount);
        editTextCashOnDelivery = v.findViewById(R.id.editTextCashOnDelivery);
        editTextPayOnline = v.findViewById(R.id.editTextPayOnline);
        user = SharedPrefManager.getInstance(getActivity()).getUser();

        Random rnd = new Random();
        orderNo = String.valueOf(100000 + rnd.nextInt(900000));

        localStorage = new LocalStorage(getContext());
        gson = new Gson();
        cartList = ((BaseActivity) getActivity()).getCartList();

        mTotal = ((BaseActivity) getActivity()).getTotalPrice();
        mDeliveryCharges = 0.0;
        mTotalAmount = mTotal + mDeliveryCharges;
        editTextSubTotal.setText("\u20B9 "+mTotal + "");
        editTextPromo.setText("- \u20B9 "+00 + "");
        editTextHirecookMoney.setText("- \u20B9 "+00 + "");
        editTextDeliveryCharge.setText("+ \u20B9 "+mDeliveryCharges + "");
        editTextGrandTotal.setText("\u20B9 "+mTotalAmount + "");
        editTextPayableAmount.setText("Payable Amount "+"\u20B9 "+mTotalAmount + "");

        editTextAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),MapLocation.class));
            }
        });

        editTextCashOnDelivery.setOnClickListener(new OnClickRateLimitedDecoratedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maps!=null){
                    if(cartList!=null&&cartList.size()!=0){
                        for(Cart cart:cartList){
                            Order order = new Order();
                            order.setOrderId(orderNo);
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
                            if (SharedPrefManager.getInstance(getActivity()).isLoggedIn()) {
                                if(order.getOrderId()!=null&&order.getProductName()!=null&&order.getProductSellRate()!=0&&order.getProductDisplayRate()!=0
                                        &&order.getProductQuantity()!=0&&order.getProductTotalAmount()!=0&&order.getOrderStatus()!=null&&order.getProductImage()!=null
                                        &&order.getFirmId()!=null&&order.getUserId()!=0&&order.getOrderWeight()!=null&&order.getPaymentMethod()!=null&&order.getOrderPlaceId()!=null
                                        &&order.getOrderLatitude()!=null&&order.getOrderLongitude()!=null&&order.getOrderAddress()!=null&&order.getOrderPincode()!=0
                                        &&order.getName()!=null&&order.getEmail()!=null&&order.getPhone()!=null&&order.getFirmLat()!=null&&order.getFirmLng()!=null
                                        &&order.getFirmAddress()!=null){
                                    placeOrder(order);
                                }else{
                                    Toast.makeText(getActivity(),"These Item can`t be placed please remove item and try again",Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(getActivity(),"Please Login First",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }else{
                    Toast.makeText(getActivity(),"Please Select Address",Toast.LENGTH_LONG).show();
                }
            }
        },5000));

        editTextPayOnline.setOnClickListener(new OnClickRateLimitedDecoratedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maps!=null){
                    if(cartList!=null&&cartList.size()!=0){
                        for(Cart cart:cartList){
                            Order order = new Order();
                            order.setOrderId(orderNo);
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

                            if(cart.getWeight()==null){
                                order.setOrderWeight("null");
                            }else{
                                order.setOrderWeight(cart.getWeight());
                            }
                            if (SharedPrefManager.getInstance(getActivity()).isLoggedIn()) {
                                if(order.getOrderId()!=null&&order.getProductName()!=null&&order.getProductSellRate()!=0&&order.getProductDisplayRate()!=0
                                        &&order.getProductQuantity()!=0&&order.getProductTotalAmount()!=0&&order.getOrderStatus()!=null&&order.getProductImage()!=null
                                        &&order.getFirmId()!=null&&order.getUserId()!=0&&order.getOrderWeight()!=null&&order.getPaymentMethod()!=null&&order.getOrderPlaceId()!=null
                                        &&order.getOrderLatitude()!=null&&order.getOrderLongitude()!=null&&order.getOrderAddress()!=null&&order.getOrderPincode()!=0
                                        &&order.getName()!=null&&order.getEmail()!=null&&order.getPhone()!=null&&order.getFirmLat()!=null&&order.getFirmLng()!=null
                                        &&order.getFirmAddress()!=null){

                                    if(mTotalAmount!=0){
                                        payUsingUpi("HireCook","kamal.bunkar07@okaxis","Test Payment", String.valueOf(mTotalAmount));
                                        if(status.equals("success")){
                                            placeOrder(order);
                                        }
                                    }
                                }else{
                                    Toast.makeText(getActivity(),"These Item can`t be placed please remove item and try again",Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(getActivity(),"Please Login First",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }else{
                    Toast.makeText(getActivity(),"Please Select Address",Toast.LENGTH_LONG).show();
                }
            }
        },5000));

        getMapDetails();
        getDateAndTime();
        return v;
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
                    Toast.makeText(getActivity(),response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    localStorage.deleteCart();
                    getTokenFromServer(order.getFirmId(),order.getUserId());
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                } else {
                    Toast.makeText(getActivity(),response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                Toast.makeText(getActivity(),R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTokenFromServer(String firm_id, int userId) {
        UserApi mService =  ApiClient.getClient().create(UserApi.class);
        Call<TokenResult> call1 = mService.getTokenByFirmId(firm_id,userId);
        call1.enqueue(new Callback<TokenResult>() {
            @Override
            public void onResponse(Call<TokenResult> call, Response<TokenResult> response) {
                int statusCode = response.code();
                if(statusCode==200&&response.body().getError()==false) {
                    progressBarUtil.hideProgress();
                    TokenResult tokenResult = response.body();
                    sendNotification(firm_id,tokenResult.getToken().getToken());
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println("Suree Token  "+SharedPrefToken.getInstance(getApplicationContext()).getTokens().getToken());
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.failed_due_to+response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TokenResult> call, Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), R.string.error+t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println("Suree: "+t.getMessage());
            }
        });
    }

    private void sendNotification(String firmId, String token) {
        NotificationApi mService = ApiClient.getClient().create(NotificationApi.class);
        Call<String> call = mService.sendNotification(token,firmId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200 ) {
                    localStorage.deleteCart();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(),R.string.error, Toast.LENGTH_SHORT).show();
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

        editTextDeliveryTime.setText(date+"  "+time);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (getFragmentManager().getBackStackEntryCount() != 0) {
                getFragmentManager().popBackStack();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Address");
        initializeViews();
    }

    private void initializeViews() {
        try {
            Places.initialize(getActivity(), Constants.locationApiKey);
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
                        maps = response.body().getMaps();
                        editTextAddress.setText(maps.getAddress());
                        mMap.clear();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    editTextAddress.setText(maps.getAddress());
                } else {
                    Toast.makeText(getActivity(),"Failed due to: "+response.errorBody(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getActivity(),R.string.error+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        if(null != chooser.resolveActivity(getActivity().getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(getActivity(),"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
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
        if (isConnectionAvailable(getActivity())) {
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
                Toast.makeText(getActivity(), "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successfull: "+approvalRefNo);
                // placeOrder(order);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(getActivity(), "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);
            }
            else {
                Toast.makeText(getActivity(), "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);
            }
        } else {
            Log.e("UPI", "Internet issue: ");
            Toast.makeText(getActivity(), "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
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
}
