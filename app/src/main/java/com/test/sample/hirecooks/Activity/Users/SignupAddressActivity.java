package com.test.sample.hirecooks.Activity.Users;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.BaseActivity;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.TrackGPS;
import com.test.sample.hirecooks.WebApis.MapApi;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupAddressActivity extends BaseActivity {
    private EditText  lat, lng, editTextPinCode, location;
    Button saveMapDetailsBtn;
    GoogleMap mMap;
    MapView mapView;
    private TrackGPS trackGPS;
    private double mLatitude, mLongitude;
    private Map maps;
    private int mMapId ;
    private DecimalFormat df2 = new DecimalFormat(".##########");
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    String TAG = "MAPLOCATION",placeId;
    private View appRoot;
    private ProgressBarUtil progressBarUtil;
    private String subAddress,pinCode;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_signup_address);
            progressBarUtil = new ProgressBarUtil(this);
            Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            appRoot = findViewById(R.id.appRoot);
            mapView = findViewById(R.id.google_map_view);
            location = findViewById(R.id.location_et);
            lat = findViewById(R.id.lat_et);
            lng = findViewById(R.id.lng_et);
            editTextPinCode = findViewById(R.id.editTextPinCode);
            saveMapDetailsBtn = findViewById(R.id.save_map);
            trackGPS = new TrackGPS(this);
            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            try {
                // Initialize Places.
                Places.initialize(getApplicationContext(), Constants.locationApiKey);
                // Create a new Places client instance.
                // PlacesClient placesClient = Places.createClient(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            location.setOnClickListener(v -> {
                searchLocation();
            });

            mapView.getMapAsync(googleMap -> {
                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(SignupAddressActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignupAddressActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.setMyLocationEnabled(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();

                if(trackGPS.canGetLocation()) {
                    mLatitude = trackGPS.getLatitude();
                    mLongitude = trackGPS.getLongitude();
                }

                LatLng latLng = new LatLng(mLatitude,mLongitude);
                String add = getAddress(latLng);
                location.setText(add);
                lat.setText(df2.format(mLatitude)+"");
                lng.setText(df2.format(mLongitude)+"");
                final CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.setOnMapClickListener(latLng1 -> {
                    lat.setText(df2.format(latLng1.latitude)+"");
                    lng.setText(df2.format(latLng1.longitude)+"");
                    String add1 = getAddress(latLng1);
                    location.setText(add1);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in " + lat +" "+ lng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    CameraPosition cameraPosition1 = new CameraPosition.Builder().target(latLng1).zoom(17).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                });
            });

            saveMapDetailsBtn.setOnClickListener(v -> validateFields());

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchLocation() {
        location.setText("");
        lat.setText("");
        lng.setText("");
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(getApplicationContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                    location.setText(place.getName() + "," + place.getAddress());

                    LatLng latLng = new LatLng(place.getLatLng().longitude, place.getLatLng().longitude);
                    getAddress(latLng);

                    placeId = place.getId();
                    lat.setText(df2.format(Objects.requireNonNull(place.getLatLng()).latitude) + "");
                    lng.setText(df2.format(place.getLatLng().longitude) + "");

                    if (mMap != null) {
                        mMap.clear();
                        CameraPosition cameraPosition1 = new CameraPosition.Builder().target(place.getLatLng()).zoom(17).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                    }
                    Log.i(TAG, "Place: " + place.getName());
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i(TAG, status.getStatusMessage());
                }/*else if (resultCode == RESULT_CANCELED) {
                 *//* The user canceled the operation. *//*
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(SignupAddressActivity.this, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
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
        } catch (IOException e) {
            Log.e("SignupAddressActivity", "Unable connect to Geocoder", e);
            return result;
        }
    }

    public void validateFields() {
        try{
            String address = location.getText().toString();
            String latitude = lat.getText().toString();
            String longitude = lng.getText().toString();

            if(address.isEmpty()) {
                location.setError("Should not be empty");
                location.requestFocus();
            }
            else if(latitude.isEmpty()) {
                lat.setError("Should not be empty");
                lat.requestFocus();
            }
            else if(longitude.isEmpty()) {
                lng.setError("Should not be empty");
                lng.requestFocus();
            }
            if(placeId==null){
                placeId = "Not_Available";
            }

            else {
                Map postMaps = new Map();
                postMaps.setLatitude(latitude);
                postMaps.setLongitude(longitude);
                postMaps.setAddress(address);
                postMaps.setSubAddress(subAddress);
                postMaps.setPincode(Integer.parseInt(pinCode));
                postMaps.setUserId(Constants.SIGNUP_USER.getId());
                postMaps.setFirm_id(Constants.SIGNUP_USER.getFirmId());
                postMaps.setPlaceId(placeId);
                postMapDetails(postMaps);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void postMapDetails(final Map postMaps) {
        progressBarUtil.showProgress();
        MapApi mapApi = ApiClient.getClient().create(MapApi.class);
        Call<Result> postMapDetailsResponse = mapApi.addMapDetails(postMaps.getLatitude(),postMaps.getLongitude(),postMaps.getAddress(),postMaps.getSubAddress(),String.valueOf(postMaps.getPincode()),postMaps.getPlaceId(),postMaps.getUserId(),postMaps.getFirm_id());
        postMapDetailsResponse.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                progressBarUtil.hideProgress();
                if(response.code() == 200 && response.body() != null&&response.body().getError()==false) {
                    try{
                        ShowToast("Location Saved Successfully");
                        finish();
                        startActivity(new Intent(SignupAddressActivity.this,UserSignInActivity.class));
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
                progressBarUtil.hideProgress();
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
