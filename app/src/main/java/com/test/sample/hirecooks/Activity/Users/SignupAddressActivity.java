package com.test.sample.hirecooks.Activity.Users;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.WebApis.MapApi;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupAddressActivity extends BaseActivity {
    private TextView locationText;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private FusedLocationProviderClient client;
    private GoogleSignInAccount account;
    private String subAddress;
    private String pinCode;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_signup_address );
        initViews();

        if (checkLocationPermission()) {
            getCurrentLocation();
        }
    }

    private void initViews ( ) {
        client = LocationServices.getFusedLocationProviderClient(SignupAddressActivity.this);
        account = GoogleSignIn.getLastSignedInAccount(SignupAddressActivity.this);
        locationText = findViewById(R.id.location_et);

        locationText.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    getCurrentLocation();
                }
            }
        } );
    }

    private void setLocation(Location location) {
        latLng = new LatLng( location.getLatitude(), location.getLongitude() );
        locationText.setText( String.valueOf( getAddress( latLng ) ) );

        Map postMaps = new Map();
        postMaps.setLatitude(String.valueOf( location.getLatitude() ));
        postMaps.setLongitude(String.valueOf( location.getLongitude( )));
        postMaps.setAddress(getAddress( latLng ));
        postMaps.setSubAddress(subAddress);
        postMaps.setPincode(Integer.parseInt(pinCode));
        postMaps.setUserId( Constants.SIGNUP_USER.getId());
        postMaps.setFirm_id(Constants.SIGNUP_USER.getFirmId());
        postMaps.setPlaceId("Not_Available");
        postMapDetails(postMaps);
    }

    private boolean checkLocationPermission() {
        //check the location permissions and return true or false.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permissions granted
            Toast.makeText(getApplicationContext(), "permissions granted", Toast.LENGTH_LONG).show();
            return true;
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions
            Toast.makeText(getApplicationContext(), "Please enable permissions", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permissions request")
                        .setMessage("we need your permission for location in order to show you this example")
                        .setPositiveButton("Ok, I agree", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SignupAddressActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
    }

    public void getCurrentLocation() {
        if ( ActivityCompat.checkSelfPermission ( this , android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission ( this , android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            //request the last location and add a listener to get the response. then update the UI.
            client.getLastLocation ( ).addOnSuccessListener ( this , location -> {
                // Got last known location.
                if ( location != null ) {
                    setLocation (location);
                } else {
                    Toast.makeText ( SignupAddressActivity.this , "location: IS NULL" , Toast.LENGTH_SHORT ).show ( );
                }
            } ).addOnFailureListener ( this , new OnFailureListener( ) {
                @Override
                public void onFailure ( @NonNull Exception e ) {
                    Toast.makeText ( SignupAddressActivity.this , "getCurrentLocation FAILED" , Toast.LENGTH_LONG ).show ( );
                }
            } );
        } else {//client
            Toast.makeText ( this , "getCurrentLocation ERROR" , Toast.LENGTH_LONG ).show ( );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    getCurrentLocation();
                } else {
                    // permission denied
                    /*TextView locationText = findViewById(R.id.locationText);
                    locationText.setText("location: permission denied");*/

                    new AlertDialog.Builder(SignupAddressActivity.this)
                            .setMessage("Cannot get the location!")
                            .setPositiveButton("OK", null)
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
        }
    }

    private String getAddress( LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String result = null;
        try {
            List < Address > addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                pinCode = address.getPostalCode();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }
                result = address.getAddressLine(0);
                subAddress = address.getSubLocality()+", "+address.getLocality();
                return result;
            }
            return result;
        } catch ( IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }
    }


    public void postMapDetails(final Map postMaps) {
        MapApi mapApi = ApiClient.getClient().create(MapApi.class);
        Call<Result> postMapDetailsResponse = mapApi.addMapDetails(postMaps.getLatitude(),postMaps.getLongitude(),postMaps.getAddress(),postMaps.getSubAddress(),
                String.valueOf(postMaps.getPincode()),postMaps.getPlaceId(),postMaps.getUserId(),postMaps.getFirm_id());
        postMapDetailsResponse.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                int statusCode = response.code();
                if(statusCode == 200) {
                    try{
                        ShowToast("Location Saved Successfully");
                        finish();
                        startActivity(new Intent(SignupAddressActivity.this,UserSignInActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    ShowToast("Failed due to :"+response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                ShowToast("Please Check Internet Connection");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
}
