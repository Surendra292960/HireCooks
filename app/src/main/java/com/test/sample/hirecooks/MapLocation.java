package com.test.sample.hirecooks;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
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

public class MapLocation extends AppCompatActivity {
    private EditText  lat, lng, editTextPinCode;
    private TextView location;
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
    private User user;
    private ProgressBarUtil progressBarUtil;
    private String pinCode;
    private String subAddress;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_map_location);
            progressBarUtil = new ProgressBarUtil(this);
            user = SharedPrefManager.getInstance(this).getUser();
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
            getMapDetails();
            location.setOnClickListener(v -> {
                searchLocation();
            });

            mapView.getMapAsync(googleMap -> {
                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(MapLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

            getMapDetails();
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

                    LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
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
        Geocoder geocoder = new Geocoder(MapLocation.this, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                System.out.println("Suree: "+address.getPostalCode());
                pinCode = address.getPostalCode();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }
                subAddress = address.getSubLocality()+", "+address.getLocality();
                result = address.getAddressLine(0);
                return result;
            }
            return result;
        } catch (IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
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

            else {
                if(maps != null) {
                    Map updateMaps = new Map();
                    updateMaps.setLatitude(latitude);
                    updateMaps.setLongitude(longitude);
                    updateMaps.setAddress(address);
                    updateMaps.setUserId(user.getId());
                    updateMaps.setFirm_id(user.getFirmId());
                    updateMaps.setPlaceId(maps.getPlaceId());
                    updateMaps.setSubAddress(subAddress);
                    updateMaps.setPincode(Integer.parseInt(pinCode));
                    updateMapDetails(updateMaps);
                }
                else {
                    Map postMaps = new Map();
                    postMaps.setLatitude(latitude);
                    postMaps.setLongitude(longitude);
                    postMaps.setAddress(address);
                    postMaps.setUserId(user.getId());
                    postMaps.setFirm_id(user.getFirmId());
                    postMaps.setPlaceId(placeId);
                    postMaps.setSubAddress(subAddress);
                    postMaps.setPincode(Integer.parseInt(pinCode));
                    postMapDetails(postMaps);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateMapDetails(final Map updateMaps) {
        progressBarUtil.showProgress();
        MapApi mapApi = ApiClient.getClient().create(MapApi.class);
        Call<Result> postMapDetailsResponse = mapApi.updateMapDetails(updateMaps.getUserId(),updateMaps.getLatitude(),updateMaps.getLongitude(),updateMaps.getAddress(),updateMaps.getSubAddress(),String.valueOf(updateMaps.getPincode()),updateMaps.getPlaceId(),updateMaps.getFirm_id());
        postMapDetailsResponse.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                progressBarUtil.hideProgress();
                if(response.code() == 200 && response.body() != null) {
                    try{
                        finish();
                        Toast.makeText(MapLocation.this,response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(MapLocation.this,R.string.failed_due_to+response.body().getMessage()+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(MapLocation.this,R.string.error+t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("suree: "+t.getMessage());
            }
        });
    }

    private void getMapDetails() {
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
                        location.setText(maps.getAddress());
                        lat.setText(maps.getLatitude());
                        lat.setText(maps.getLongitude());
                        mMap.clear();
                        LatLng hotelLatlng = new LatLng(Double.parseDouble(maps.getLatitude()),Double.parseDouble(maps.getLongitude()));
                        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(17).target(hotelLatlng).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    location.setText(maps.getAddress());
                    lat.setText(maps.getLatitude());
                    lng.setText(maps.getLongitude());
                    mMap.clear();

                    if(maps!=null){
                        LatLng hotelLatlng = new LatLng(Double.parseDouble(maps.getLatitude()),Double.parseDouble(maps.getLongitude()));
                        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(17).target(hotelLatlng).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                } else {
                    Toast.makeText(MapLocation.this,R.string.failed_due_to+response.body().getMessage()+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(MapLocation.this,R.string.error+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void postMapDetails(final Map postMaps) {
        progressBarUtil.showProgress();
        MapApi mapApi = ApiClient.getClient().create(MapApi.class);
        Call<Result> postMapDetailsResponse = mapApi.addMapDetails(postMaps.getLatitude(),postMaps.getLongitude(),postMaps.getAddress(),postMaps.getSubAddress(),String.valueOf(postMaps.getPincode()),postMaps.getPlaceId(),postMaps.getUserId(),postMaps.getFirm_id());
        postMapDetailsResponse.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                progressBarUtil.hideProgress();
                if(response.code() == 200 && response.body() != null) {
                    try{
                        Toast.makeText(MapLocation.this,response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(MapLocation.this,R.string.failed_due_to+response.body().getMessage()+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(MapLocation.this,R.string.error+t.getMessage(), Toast.LENGTH_SHORT).show();
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
