package com.test.sample.hirecooks.Activity.ManageAddress;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.test.sample.hirecooks.Adapter.ManageAddress.PlacesAutoCompleteAdapter;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Maps;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Common;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.Utils.TrackGPS;
import com.test.sample.hirecooks.WebApis.MapApi;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchAddress extends BaseActivity implements PlacesAutoCompleteAdapter.ClickListener {
    private EditText lat, lng, editTextPinCode, searchBar;
    private Button saveMapDetailsBtn;
    private GoogleMap mMap;
    private MapView mapView;
    private TextView locationText, change_location;
    private TrackGPS trackGPS;
    private double mLatitude, mLongitude;
    private DecimalFormat df2 = new DecimalFormat(".##########");
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private String TAG = "MAPLOCATION";
    private ProgressBarUtil progressBarUtil;
    private User user;
    private String address, latitude, longitude,placeId, subAddress, pinCode;
    private RecyclerView recyclerView;
    RelativeLayout searchbar_interface_layout;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private StringBuilder mResult;
    PlacesClient placesClient;
    private LinearLayout mBottomSheet;
    private BottomSheetDialog enterLocationbottomSheetDialog,searchLocationbottomSheetDialog;
    protected PlaceDetectionClient placeDetectionClient;
    private BottomSheetBehavior mBehavior;
    private ImageButton cancel,cancel_search_location;
    private Map map;
    private List<Map> mapList;
    ProgressBar progress_bar;
    private TextView enterAddress,change_enter_location,house_number,floor,landmark,location_tag,save_address,current_location,Other,Office,Home,Work;
    private MapApi mService = Common.getAPI();
    CompositeDisposable compositeDisposable=new CompositeDisposable();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_search_address);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Your Location");
            Bundle bundle = getIntent().getExtras();
            if(bundle!=null){
                map = (Map) bundle.getSerializable("address");
                if(map!=null){
                    mapList = new ArrayList<>();
                    mapList.add(map);
                }
            }

            progressBarUtil = new ProgressBarUtil(this);
            user = SharedPrefManager.getInstance(this).getUser();
            mapView = findViewById(R.id.google_map_view);
            current_location = findViewById(R.id.current_location);
            locationText = findViewById(R.id.location_et);
            change_location = findViewById(R.id.change_location);
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
            change_location.setOnClickListener(v -> {
                searchLocationbottomSheetDialog(change_location);
            });

            mapView.getMapAsync(googleMap -> {
                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(SearchAddress.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchAddress.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.setMyLocationEnabled(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();

                if (trackGPS.canGetLocation()) {
                    mLatitude = trackGPS.getLatitude();
                    mLongitude = trackGPS.getLongitude();
                }

                LatLng latLng;
                String add;
                if(map!=null){
                    latLng = new LatLng(Double.parseDouble(map.getLatitude()), Double.parseDouble(map.getLongitude()));
                    if(map.getAddress()!=null){
                        locationText.setText(map.getAddress());
                        enterAddress.setText(map.getAddress());
                        lat.setText(map.getLatitude());
                        lng.setText(map.getLongitude());
                    }
                }else{
                    if(mapList!=null){
                        mapList.clear();
                    }
                    latLng = new LatLng(mLatitude, mLongitude);
                    add = getAddress(latLng);
                    locationText.setText(add);
                    enterAddress.setText(add);
                    address = add;
                    lat.setText(df2.format(mLatitude) + "");
                    lng.setText(df2.format(mLongitude) + "");
                }
                mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location"));
                final CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mMap.setOnMapClickListener(latLng1 -> {
                    lat.setText(df2.format(latLng1.latitude) + "");
                    lng.setText(df2.format(latLng1.longitude) + "");
                    String add1 = getAddress(latLng1);
                    locationText.setText(add1);
                    enterAddress.setText(add1);
                    address = add1;
                    if(mapList!=null){
                        mapList.clear();
                    }
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng1).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
                    CameraPosition cameraPosition1 = new CameraPosition.Builder().target(latLng1).zoom(17).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                });
            });

            saveMapDetailsBtn.setOnClickListener(v ->
                    enterLocationbottomSheetDialog(change_location));

            searchLocationBottomSheetDialog();
            enterLocationBottomSheetDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchLocationBottomSheetDialog() {
        if (searchLocationbottomSheetDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.searchlocation_sliding_layout, null);
            recyclerView = view.findViewById(R.id.recyclerview_address);
            searchbar_interface_layout = view.findViewById(R.id.searchbar_interface_layout);
            mBottomSheet = view.findViewById(R.id.bottomSheet);
            searchBar = view.findViewById(R.id.searchBar);
            cancel_search_location = view.findViewById(R.id.cancel_search_location);
            cancel_search_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchLocationbottomSheetDialogClose(cancel_search_location);
                }
            });
            searchBar.addTextChangedListener(filterTextWatcher);
            mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAutoCompleteAdapter.setClickListener(this);
            recyclerView.setAdapter(mAutoCompleteAdapter);
            mAutoCompleteAdapter.notifyDataSetChanged();
            searchLocationbottomSheetDialog = new BottomSheetDialog(this);
            searchLocationbottomSheetDialog.setContentView(view);
        }
    }

    private void enterLocationBottomSheetDialog() {
        if (enterLocationbottomSheetDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.add_address_sliding_layout, null);
            mBottomSheet = view.findViewById(R.id.enteraddress_bottomSheet);
            enterAddress = view.findViewById(R.id.address);
            house_number = view.findViewById(R.id.house_number);
            floor = view.findViewById(R.id.floor);
            landmark = view.findViewById(R.id.landmark);
            location_tag = view.findViewById(R.id.location_tag);
            save_address = view.findViewById(R.id.save_address);
            Home = view.findViewById(R.id.Home);
            Work = view.findViewById(R.id.work);
            Office = view.findViewById(R.id.Office);
            Other = view.findViewById(R.id.Other);
            Home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLocationTag("Home");
                }
            });  Office.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLocationTag("Office");
                }
            });  Other.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLocationTag("Other");
                }
            });  Work.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getLocationTag("Work");
                }
            });
            save_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setData();
                }
            });
            change_enter_location = view.findViewById(R.id.change_enter_location);
            change_enter_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchLocationbottomSheetDialog(change_enter_location);
                    enterLocationbottomSheetDialogClose(change_enter_location);
                }
            });
            if(map!=null){
                enterAddress.setText(map.getAddress());
            }else{
                enterAddress.setText(address);
            }

            cancel = view.findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterLocationbottomSheetDialogClose(cancel);
                }
            });

            enterLocationbottomSheetDialog = new BottomSheetDialog(this);
            enterLocationbottomSheetDialog.setContentView(view);
        }
    }

    private void getLocationTag(String text) {
       if(text.equalsIgnoreCase("Home")){
           Home.setBackground(getResources().getDrawable(R.drawable.round_border_button));
           location_tag.setText(text);
       }else{
           Home.setBackground(getResources().getDrawable(R.drawable.blue_button_background));
           Home.setTextColor(getResources().getColor(R.color.dark_gray));
       }
       if(text.equalsIgnoreCase("Other")){
           Other.setBackground(getResources().getDrawable(R.drawable.round_border_button));
           location_tag.setText(text);
       }else {
           Other.setBackground(getResources().getDrawable(R.drawable.blue_button_background));
           Other.setTextColor(getResources().getColor(R.color.dark_gray));
       }
       if(text.equalsIgnoreCase("Work")){
           Work.setBackground(getResources().getDrawable(R.drawable.round_border_button));
           location_tag.setText(text);
       }else{
           Work.setBackground(getResources().getDrawable(R.drawable.blue_button_background));
           Work.setTextColor(getResources().getColor(R.color.dark_gray));
       }
       if(text.equalsIgnoreCase("Office")){
           Office.setBackground(getResources().getDrawable(R.drawable.round_border_button));
           location_tag.setText(text);
       }else{
           Office.setBackground(getResources().getDrawable(R.drawable.blue_button_background));
           Office.setTextColor(getResources().getColor(R.color.dark_gray));
       }
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    @Override
    public void click(Place place) {
        Toast.makeText(this, place.getAddress() + ", " + place.getLatLng().latitude + place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
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
                    LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    getAddress(latLng);

                    if(map!=null){
                        enterAddress.setText(map.getAddress());
                        placeId = map.getPlaceId();
                        lat.setText(map.getLatitude());
                        lng.setText(map.getLatitude());
                    }else{
                        if(mapList!=null){
                            mapList.clear();
                        }
                        locationText.setText(place.getName() + "," + place.getAddress());
                        enterAddress.setText(place.getName() + "," + place.getAddress());
                        address = place.getName() + "," + place.getAddress();
                        placeId = place.getId();
                        lat.setText(df2.format(Objects.requireNonNull(place.getLatLng()).latitude) + "");
                        lng.setText(df2.format(Objects.requireNonNull(place.getLatLng().longitude)) + "");
                    }

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
        Geocoder geocoder = new Geocoder(SearchAddress.this, Locale.getDefault());
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

    public void setData() {
        try{
            if(mapList!=null&&mapList.size()!=0){
                for (Map map:mapList){
                    map.setHouseNumber(house_number.getText().toString());
                    map.setFloor(floor.getText().toString());
                    map.setLandmark(landmark.getText().toString());
                    map.setLocationType(location_tag.getText().toString());
                    validateFields(map);
                }

            }else{
                if(mapList!=null) {
                    mapList.clear();
                }

                String latitude = lat.getText().toString().trim();
                String longitude = lng.getText().toString().trim();
                String mplaceId = null;
                if(placeId!=null){
                    mplaceId = placeId;
                }else{
                    mplaceId = "Not_Available";
                }
                    Map map = new Map();
                    map.setLatitude(latitude);
                    map.setLongitude(longitude);
                    map.setAddress(address);
                    map.setSubAddress(subAddress);
                    map.setPincode(Integer.parseInt(pinCode));
                    map.setPlaceId(mplaceId);
                    map.setUserId(user.getId());
                    map.setHouseNumber(house_number.getText().toString());
                    map.setFloor(floor.getText().toString());
                    map.setLandmark(landmark.getText().toString());
                    map.setLocationType(location_tag.getText().toString());
                    if(!user.getFirmId().equalsIgnoreCase("Not_Available")){
                        map.setFirm_id(user.getFirmId());
                    }else{
                        map.setFirm_id(user.getFirmId());
                    }

                    validateFields(map);
            }
           // postMapDetails(map);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void validateFields(Map map) {
       /* ValidationClass validationClass = new ValidationClass();
        ValidationCallBack validationCallBack = validationClass.getValidationType(SearchAddress.this,"ADDRESS");
        if(validationCallBack.addressValidation(map)){

        }*/
        //validationClass.
        String address = map.getAddress();
        String latitude = map.getLatitude();
        String longitude = map.getLongitude();
        String placeId = map.getPlaceId();
        String pinCode = String.valueOf( map.getPincode() );
        String subAddress = map.getSubAddress();
        int userId = SharedPrefManager.getInstance( this ).getUser().getId();
        String mHouseNumber = map.getHouseNumber();
        String mLandmark = map.getLandmark();
        String mFloor = map.getFloor();
        String mLocationTag = map.getLocationType();
        String firmId = null;
        if (user.getFirmId().equalsIgnoreCase( "Not_Available" )) {
            firmId = map.getFirm_id();
        } else {
            firmId = map.getFirm_id();
        }

        if (TextUtils.isEmpty( address )) {
            Toast.makeText( this, "fill Address", Toast.LENGTH_SHORT ).show();
            return;
        }if (TextUtils.isEmpty( latitude )) {
            Toast.makeText( this, "fill latitude", Toast.LENGTH_SHORT ).show();
            return;
        }if (TextUtils.isEmpty( longitude )) {
            Toast.makeText( this, "fill longitude", Toast.LENGTH_SHORT ).show();
            return;
        }if (TextUtils.isEmpty( placeId )) {
            Toast.makeText( this, "fill placeId", Toast.LENGTH_SHORT ).show();
            return;
        }if (TextUtils.isEmpty( pinCode )) {
            Toast.makeText( this, "fill pinCode", Toast.LENGTH_SHORT ).show();
            return;
        }if (TextUtils.isEmpty( subAddress )) {
            Toast.makeText( this, "fill subAddress", Toast.LENGTH_SHORT ).show();
            return;
        }if (userId == 0) {
            Toast.makeText( this, "fill userId", Toast.LENGTH_SHORT ).show();
            return;
        }if (TextUtils.isEmpty( firmId )) {
            Toast.makeText( this, "fill firmId", Toast.LENGTH_SHORT ).show();
            return;
        }if (TextUtils.isEmpty( mHouseNumber )) {
            Toast.makeText( this, "fill houseNumber", Toast.LENGTH_SHORT ).show();
            return;
        }if (TextUtils.isEmpty( mFloor )) {
            Toast.makeText( this, "fill floor Number", Toast.LENGTH_SHORT ).show();
            return;
        }if (TextUtils.isEmpty( mLandmark )) {
            Toast.makeText( this, "fill landmark", Toast.LENGTH_SHORT ).show();
            return;
        }if (TextUtils.isEmpty( mLocationTag )) {
            Toast.makeText( this, "fill LocationTag", Toast.LENGTH_SHORT ).show();
            return;
        }
        ApiServiceCall(map);

    }

    private void ApiServiceCall(Map map) {
        Gson gson = new Gson();
        String json = gson.toJson( map );
        System.out.println( "Suree: "+json );
        progressBarUtil.showProgress();
        mService.createAddress(map.getLatitude(),map.getLongitude(),map.getAddress(),map.getSubAddress(), String.valueOf( map.getPincode() ),
                map.getPlaceId(),map.getUserId(),map.getFirm_id(),map.getHouseNumber(),map.getFloor(),map.getLandmark(),map.getLocationType())
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Maps>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Maps result) {
                        if(!result.getError()){
                            ShowToast( result.getMessage() );
                            SearchAddress.this.finish();
                        }else{
                            ShowToast( result.getMessage() );
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        progressBarUtil.hideProgress();
                        ShowToast(t.getMessage());
                        System.out.println("New data received: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        progressBarUtil.hideProgress();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LatLng latLng=new LatLng(mLatitude,mLongitude);
        getAddress(latLng);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    public void searchLocationbottomSheetDialog(View view) {
        searchLocationbottomSheetDialog.show();
    }

    public void searchLocationbottomSheetDialogClose(View view) {
        searchLocationbottomSheetDialog.hide();
    }

    public void enterLocationbottomSheetDialog(View view) {
        enterLocationbottomSheetDialog.show();
    }

    public void enterLocationbottomSheetDialogClose(View view) {
        enterLocationbottomSheetDialog.hide();
    }

}
