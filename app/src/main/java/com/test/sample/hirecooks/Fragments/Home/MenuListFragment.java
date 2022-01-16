package com.test.sample.hirecooks.Fragments.Home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.test.sample.hirecooks.Activity.Cooks.CooksActivity;
import com.test.sample.hirecooks.Activity.Favourite.FavouriteActivity;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Activity.Orders.MyOrdersActivity;
import com.test.sample.hirecooks.Activity.Users.UserSignInActivity;
import com.test.sample.hirecooks.Activity.subscription.SubscriptionActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.FirmUsers.Example;
import com.test.sample.hirecooks.Models.FirmUsers.Firmuser;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.Utils.UploadCallBack;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuListFragment extends Fragment implements UploadCallBack {
    SimpleDateFormat format = new SimpleDateFormat("yyy-dd-MM HH:mm:ss");
    SimpleDateFormat format2 = new SimpleDateFormat("yyy-dd-MM");
    DateFormat date = new SimpleDateFormat("dd MMM yyyy");
    DateFormat time = new SimpleDateFormat("HH:mm a");
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private User user = SharedPrefManager.getInstance(getActivity()).getUser();
    private UserApi mService;
    private FirebaseAuth mAuth;
    private SwitchCompat drawer_switch;
    MainActivity mainActivity;
    private FusedLocationProviderClient client;
    private String subAddress;
    private String pinCode;
    private LatLng latLng;
    private List<Example> exampleArrayList;
    private List<Firmuser> firmuserArrayList;
    private String created_at;
    private LinearLayout switch_lay;
    private TextView date_,time_;

    public static MenuListFragment newInstance() {
        Bundle args = new Bundle();
        MenuListFragment fragment = new MenuListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        mAuth = FirebaseAuth.getInstance();
        TextView userName = view.findViewById(R.id.title);
        TextView userPhone = view.findViewById(R.id.phone);
        drawer_switch = view.findViewById(R.id.drawer_switch);
        switch_lay = view.findViewById(R.id.switch_lay);
        date_ = view.findViewById(R.id.date_);
        time_ = view.findViewById(R.id.time_);
        userName.setText("Welcome:  "+user.getName());
        userPhone.setText("Ph:  "+user.getPhone());
        getFirmUserByDate(user.getId(),format2.format(new Date( )),format2.format(new Date( )));
        client = LocationServices.getFusedLocationProviderClient( mainActivity);
        if(user.getUserType().equalsIgnoreCase( "Rider" )||user.getUserType().equalsIgnoreCase( "Employee" )){
            switch_lay.setVisibility( View.VISIBLE );

        }else{
            switch_lay.setVisibility( View.GONE );
        }
        drawer_switch.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer_switch.isChecked()) {
                    if (checkLocationPermission()) {
                        getCurrentLocation();
                    }
                }else{
                    if (checkLocationPermission()) {
                        getCurrentLocation();
                    }
                }
            }
        } );

        NavigationView vNavigation = view.findViewById(R.id.vNavigation);
        vNavigation.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.menu_home) {
                startActivity( new Intent(getActivity(), MainActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }else if (id == R.id.logout) {
                if(!drawer_switch.getText().toString().equalsIgnoreCase( "Present" )){
                    logout();
                }else{
                    showalertbox("Logout Firm User First");
                }
            } else if (id == R.id.menuelist_destination) {
                startActivity( new Intent(getActivity(), FavouriteActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            } else if (id == R.id.menu_terms_and_condition) {
                startActivity( new Intent(getActivity(), SubscriptionActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            } else if (id == R.id.menu_cooks) {
                Intent intent = new Intent( getActivity(), CooksActivity.class );
                intent.putExtra(  "type", "AllCooks"  );
                intent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( intent );
            } else if (id == R.id.menu_myorders) {
                startActivity( new Intent(getActivity(), MyOrdersActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            } else if (id == R.id.menu_share) {
             /*   Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "HireCook app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);*/
            }
            return false;
        });

        return view;
    }

    public void showalertbox(String string) {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( mainActivity);
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

    @Override
    public void onProgressUpdate(int percentage) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach( context );
        mainActivity = (MainActivity)context;
    }

    private void logout() {
        updateUserStatus(user.getId(),0,user.getEmail());
       // mAuth.signOut();
        SharedPrefManager.getInstance(getActivity()).logout();
        startActivity(new Intent(getActivity(), UserSignInActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
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

    public void getFirmUserByDate(Integer userId, String from_date, String to_date) {
        mService = ApiClient.getClient().create( UserApi.class );
        Call<List<Example>> call = mService.getFirmUserByDate(userId, from_date,to_date );
        call.enqueue( new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                if (response.code() == 200) {
                    exampleArrayList = new ArrayList<>(  );
                    firmuserArrayList = new ArrayList<>(  );
                    for(Example example:response.body()){
                        exampleArrayList.add( example );
                        if(!example.getError()&&example.getFirmusers()!=null&&example.getFirmusers().size()!=0){
                            for(Firmuser firmuser:example.getFirmusers()) {
                                firmuserArrayList.add( firmuser );
                                created_at = firmuser.getCreatedAt();
                                if (firmuser.getStatus().equalsIgnoreCase( "Present" )) {
                                    drawer_switch.setText( firmuser.getStatus() );
                                    drawer_switch.setChecked( true );
                                    drawer_switch.setTextColor( android.graphics.Color.parseColor( "#43A047" ) );
                                    time_.setTextColor( android.graphics.Color.parseColor( "#2d5d82"));
                                    date_.setTextColor( android.graphics.Color.parseColor( "#2d5d82"));
                                    date_.setText( "Since : "+date.format( getDateTime(firmuser.getSigninDate( ))));
                                    time_.setText("Time : "+ time.format( getDateTime(firmuser.getSigninDate( ))));
                                    System.out.println( "Suree status:" + firmuser.getStatus() );
                                }else if(firmuser.getStatus().equalsIgnoreCase( "Absent" )){
                                    drawer_switch.setText( firmuser.getStatus() );
                                    drawer_switch.setChecked( false );
                                    drawer_switch.setTextColor( android.graphics.Color.parseColor( "#ff6347"));
                                    time_.setTextColor( android.graphics.Color.parseColor( "#ff6347"));
                                    date_.setTextColor( android.graphics.Color.parseColor( "#ff6347"));
                                    date_.setText( "Last Seen : "+date.format( getDateTime(firmuser.getSignoutDate( ))));
                                    time_.setText("Time : "+ time.format( getDateTime(firmuser.getSignoutDate( ))));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {
                Toast.makeText( mainActivity,  t.getMessage(), Toast.LENGTH_SHORT ).show();
                System.out.println( "Suree :" + t.getMessage() );
            }
        } );
    }

    private Date getDateTime(String sentat) {
        Date date= null;
        try{
            date = format.parse(sentat);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    public void firmUserSignIn(List<Example> example) {
        mService = ApiClient.getClient().create( UserApi.class );
        Call<List<Example>> call = mService.firmUserSignIn( example );
        call.enqueue( new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                if (response.code() == 200) {
                    for(Example example1:response.body()){
                        if(!example1.getError()){
                            Toast.makeText( mainActivity, "Login Success", Toast.LENGTH_SHORT ).show();
                            getFirmUserByDate(user.getId(),format2.format(new Date( )),format2.format(new Date( )));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {
                System.out.println( "Suree :" + t.getMessage() );
            }
        } );
    }

    public void firmUserSignOut(Integer id, List<Example> example) {
        mService = ApiClient.getClient().create( UserApi.class );
        Call<List<Example>> call = mService.firmUserSignOut(id, example );
        call.enqueue( new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                if (response.code() == 200) {
                    for(Example example1:response.body()){
                        if(!example1.getError()){
                            Toast.makeText( mainActivity, "Logout Success", Toast.LENGTH_SHORT ).show();
                            getFirmUserByDate(user.getId(),format2.format(new Date( )),format2.format(new Date( )));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {
                System.out.println( "Suree :" + t.getMessage() );
            }
        } );
    }


    private void setLocation(Location location) {
        latLng = new LatLng( location.getLatitude(), location.getLongitude() );
        if(exampleArrayList!=null&&exampleArrayList.size()!=0&&drawer_switch.getText().toString().equalsIgnoreCase( "Present" )){
            int id=0;
            List<Example> examplesList = new ArrayList<>(  );
            List<Firmuser> firmusersList = new ArrayList<>(  );
            Example example = new Example();
            for (Firmuser firmuser:firmuserArrayList){
                id = firmuser.getId();
                firmuser.setSigninAddress(firmuser.getSigninAddress());
                firmuser.setSignoutAddress(getAddress( latLng ));
                firmuser.setSigninLat(firmuser.getSigninLat());
                firmuser.setSigninLng(firmuser.getSigninLng());
                firmuser.setSignoutLat(""+location.getLatitude());
                firmuser.setSignoutLng(""+location.getLongitude());
                firmuser.setSignoutDate(format.format(new Date( )));
                firmuser.setStatus("Absent");
                firmuser.setUserId( firmuser.getUserId());
                firmuser.setFirmId( firmuser.getFirmId() );
                firmuser.setUserType(firmuser.getUserType());
                firmuser.setSigninDate(firmuser.getSigninDate());
                firmuser.setCreatedAt(firmuser.getCreatedAt());
                firmusersList.add( firmuser );
            }
            example.setFirmusers( firmusersList );
            examplesList.add( example );
            Gson gson = new Gson();
            String json = gson.toJson( examplesList );
            System.out.println( "Suree logout: "+json );
            firmUserSignOut(id, examplesList);
        }else if(created_at!=null&&created_at.equalsIgnoreCase( format2.format( new Date(  ) ))&&drawer_switch.getText().toString().equalsIgnoreCase( "Absent" )){
            int id=0;
            List<Example> examplesList = new ArrayList<>(  );
            List<Firmuser> firmusersList = new ArrayList<>(  );
            Example example = new Example();
            for (Firmuser firmuser:firmuserArrayList){
                id = firmuser.getId();
                firmuser.setSigninAddress(firmuser.getSigninAddress());
                firmuser.setSignoutAddress(" ");
                firmuser.setSigninLat(firmuser.getSigninLat());
                firmuser.setSigninLng(firmuser.getSigninLng());
                firmuser.setSignoutLat(" ");
                firmuser.setSignoutLng(" ");
                firmuser.setSignoutDate(" ");
                firmuser.setStatus("Present");
                firmuser.setUserId( firmuser.getUserId());
                firmuser.setFirmId( firmuser.getFirmId() );
                firmuser.setUserType(firmuser.getUserType());
                firmuser.setSigninDate(firmuser.getSigninDate());
                firmuser.setCreatedAt(firmuser.getCreatedAt());
                firmusersList.add( firmuser );
            }
            example.setFirmusers( firmusersList );
            examplesList.add( example );
            Gson gson = new Gson();
            String json = gson.toJson( examplesList );
            System.out.println( "Suree logout: "+json );
            firmUserSignOut(id, examplesList);
        }
        else {
            latLng = new LatLng( location.getLatitude(), location.getLongitude() );
            List<Example> examplesList = new ArrayList<>(  );
            List<Firmuser> firmusersList = new ArrayList<>(  );
            Example example = new Example();
            Firmuser firmuser = new Firmuser( );
            if(user!=null|| Constants.SIGNUP_USER!=null){
                firmuser.setName( user.getName() );
                if(user.getId()!=0&&user.getFirmId()!=null){
                    firmuser.setUserId(String.valueOf(  user.getId() ));
                    firmuser.setFirmId( user.getFirmId() );
                    firmuser.setUserType(user.getUserType());
                }else if(Constants.SIGNUP_USER.getId()!=0&&Constants.SIGNUP_USER.getFirmId()!=null){
                    firmuser.setUserId(String.valueOf( Constants.SIGNUP_USER.getId() ));
                    firmuser.setFirmId(Constants.SIGNUP_USER.getFirmId());
                    firmuser.setUserType(Constants.SIGNUP_USER.getUserType());
                }
            }
            firmuser.setSigninAddress(getAddress( latLng ));
            firmuser.setSignoutAddress("");
            firmuser.setSigninLat(""+location.getLatitude());
            firmuser.setSigninLng(""+location.getLongitude( ));
            firmuser.setSignoutLat("");
            firmuser.setSignoutLng("");
            firmuser.setSignoutDate("");
            firmuser.setStatus("Present");
            firmuser.setSigninDate(format.format(new Date( )));
            firmuser.setCreatedAt(format2.format(new Date( )));
            firmusersList.add( firmuser );
            example.setFirmusers( firmusersList );
            examplesList.add( example );
            Gson gson = new Gson();
            String json = gson.toJson( examplesList );
            System.out.println( "Suree login: "+json );
            firmUserSignIn(examplesList);
        }
    }

    private boolean checkLocationPermission() {
        //check the location permissions and return true or false.
        if (ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permissions granted
            //Toast.makeText(mainActivity, "permissions granted", Toast.LENGTH_LONG).show();
            return true;
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions
            Toast.makeText(mainActivity, "Please enable permissions", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // mainActivity thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Permissions request")
                        .setMessage("we need your permission for location in order to show you mainActivity example")
                        .setPositiveButton("Ok, I agree", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mainActivity, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
    }

    public void getCurrentLocation() {
        if ( ActivityCompat.checkSelfPermission ( mainActivity , android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission ( mainActivity , android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            //request the last location and add a listener to get the response. then update the UI.
            client.getLastLocation ( ).addOnSuccessListener ( mainActivity , location -> {
                // Got last known location.
                if ( location != null ) {
                    setLocation (location);
                } else {
                    Toast.makeText ( mainActivity , "location: IS NULL" , Toast.LENGTH_SHORT ).show ( );
                }
            } ).addOnFailureListener ( mainActivity , new OnFailureListener( ) {
                @Override
                public void onFailure ( @NonNull Exception e ) {
                    Toast.makeText ( mainActivity , "getCurrentLocation FAILED" , Toast.LENGTH_LONG ).show ( );
                }
            } );
        } else {//client
            Toast.makeText ( mainActivity , "getCurrentLocation ERROR" , Toast.LENGTH_LONG ).show ( );
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

                    new AlertDialog.Builder(mainActivity)
                            .setMessage("Cannot get the location!")
                            .setPositiveButton("OK", null)
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
        }
    }

    private String getAddress( LatLng latLng) {
        Geocoder geocoder = new Geocoder(mainActivity, Locale.getDefault());
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
}