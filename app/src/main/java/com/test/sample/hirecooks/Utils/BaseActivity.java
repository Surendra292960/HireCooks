package com.test.sample.hirecooks.Utils;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity implements AddorRemoveCallbacks {
    public static final String TAG = "BaseActivity===>";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 20;
    private static final int GPS_REQUEST_CODE = 9001;
    List<Cart> cartList = new ArrayList<Cart>();
    List<Subcategory> newCartList = new ArrayList<Subcategory>();
    List<com.test.sample.hirecooks.Models.NewOrder.Order> newOrderList = new ArrayList<com.test.sample.hirecooks.Models.NewOrder.Order>();
    List<Subcategory> FavouriteList = new ArrayList<>();
    Gson gson;
    LocalStorage localStorage;
    String userJson;
    ProgressDialog progressDialog;
    private UserApi mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localStorage = new LocalStorage(getApplicationContext());
        gson = new Gson();
        userJson = localStorage.getUserLogin();
        progressDialog = new ProgressDialog(BaseActivity.this);
        cartCount();
    }

    public int cartCount() {

        gson = new Gson();
        if (localStorage.getCart() != null) {
            String jsonCart = localStorage.getCart();
            Log.d("CART : ", jsonCart);
            Type type = new TypeToken<List<Cart>>() {
            }.getType();
            cartList = gson.fromJson(jsonCart, type);
            return cartList.size();
        }
        return 0;
    }

    public List<Cart> getCartList() {
        if (localStorage.getCart() != null) {
            String jsonCart = localStorage.getCart();
            Type type = new TypeToken<List<Cart>>() {
            }.getType();
            cartList = gson.fromJson(jsonCart, type);
            return cartList;
        }
        return cartList;
    }

/*    public int newCartCount() {

        gson = new Gson();
        if (localStorage.getCart() != null) {
            String jsonCart = localStorage.getCart();
            Log.d("CART : ", jsonCart);
            Type type = new TypeToken<List<Subcategory>>() {
            }.getType();
            newCartList = gson.fromJson(jsonCart, type);
            return newCartList.size();
        }
        return 0;
    }*/

    public List<Subcategory> getnewCartList() {
        if (localStorage.getCart() != null) {
            String jsonCart = localStorage.getCart();
            Type type = new TypeToken<List<Subcategory>>() {
            }.getType();
            newCartList = gson.fromJson(jsonCart, type);
            return newCartList;
        }
        return newCartList;
    }


    public List<Subcategory> getFavourite() {
        if (localStorage.getFavourite() != null) {
            String jsonFavourite = localStorage.getFavourite();
            Type type = new TypeToken<List<Subcategory>>() {
            }.getType();
            FavouriteList = gson.fromJson(jsonFavourite, type);
            return FavouriteList;
        }
        return FavouriteList;
    }

    public Double getTotalPrice() {
        cartList = getCartList();
        Double total = 0.0;
        if (cartCount() > 0) {
            for (int i = 0; i < cartList.size(); i++) {
                total = total + Double.valueOf(cartList.get(i).getTotalAmount());
                Log.d(TAG, "Total :" + total + "");
            }
            Log.d(TAG, "Total :" + total + "");
            return total;
        }
        return total;
    }


    @Override
    public void onAddProduct() {

    }

    @Override
    public void onRemoveProduct() {

    }

    @Override
    public void updateTotalPrice() {

    }


    public void ShowToast(String text) {
        // Retrieve the Layout Inflater and inflate the layout from xml
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custome_toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
        // get the reference of TextView and ImageVIew from inflated layout
        TextView toastTextView = (TextView) layout.findViewById(R.id.toastTextView);
        ImageView toastImageView = (ImageView) layout.findViewById(R.id.toastImageView);
        // set the text in the TextView
        toastTextView.setText(text);
        // set the Image in the ImageView
        // toastImageView.setImageResource(R.drawable.ic_launcher);
        // create a new Toast using context
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG); // set the duration for the Toast
        toast.setGravity(Gravity.TOP, 0, 0);
        layout.setBackgroundResource(R.color.green_light);
        toastTextView.setShadowLayer(0, 0, 0, 0);
        toast.setView(layout); // set the inflated layout
        toast.show(); // display the custom Toast
    }

    public boolean enableGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnable = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
       if(providerEnable){
           return true;
       }else{
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("GPS is required for this app")
                    .setPositiveButton("Yes",((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent,GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();
       }
        return false;
    }

    public Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return null;
                }
            }
            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                return lastKnownLocationGPS;
            } else {
                Location loc =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                System.out.println("Suree Time::"+loc.getTime());
                System.out.println("Suree Ltlng::"+loc.getLatitude()+" "+loc.getLatitude());
                return loc;
            }
        } else {
            return null;
        }
    }

    public String getDateAndTime() {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyy-dd-MM HH:mm:ss");
        Date GetDate = new Date();
        String DateStr = timeStampFormat.format(GetDate);
        return  DateStr;
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

    public void showAlert(String message) {
        try {
            if (!this.isFinishing()) {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(getResources().getString(R.string.ok), null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } catch (Exception e) {
            printLog(BaseActivity.class,e.getMessage());
        }

    }


    public void updateUserStatus(Integer id, Integer status, String email) {
        mService = ApiClient.getClient().create( UserApi.class );
        Call<String> call = mService.updateUserStatus( id, status,email );
        call.enqueue( new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200) {
                    System.out.println( "Suree :" + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println( "Suree :" + t.getMessage() );
            }
        } );
    }

    public void printLog(Class<?> pClassName, String pStrMsg){
        Log.e(pClassName.getSimpleName(),pStrMsg);
    }

    public void EnableRuntimePermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                } else {
                    ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
            }
            if (ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this, Manifest.permission.CAMERA)) {
                } else {
                    ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
            if (ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this, Manifest.permission.READ_SMS)) {
                } else {
                    ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                }
            }
        }
    }
}
