package com.test.sample.hirecooks.Activity.Users.FirmUsers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Libraries.RiderMapLocation.FetchURL;
import com.test.sample.hirecooks.Libraries.RiderMapLocation.TaskLoadedCallback;
import com.test.sample.hirecooks.Models.FirmUsers.Example;
import com.test.sample.hirecooks.Models.FirmUsers.Firmuser;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirmUserTracking extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    SimpleDateFormat format = new SimpleDateFormat( "yyy-dd-MM HH:mm:ss" );
    SimpleDateFormat format2 = new SimpleDateFormat( "yyy-dd-MM" );
    DateFormat date = new SimpleDateFormat( "dd MMM yyyy" );
    DateFormat time = new SimpleDateFormat( "HH:mm a" );
    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    Button getDirection;
    private Polyline currentPolyline;
    private static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
    private LinearLayout login_details_lay, ivEmptyStates;
    private ImageView spaceshipImage;
    private TextView login_time, logout_time, logout_address, login_address, user_name, user_status, current_date;
    private CircleImageView profile_image, next_day, previous_day;
    private User user;
    private UserApi mService;
    private Date mDate, date_ = null;
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_firm_user_tracking );
        initViews();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = (User) bundle.getSerializable( "FirmUser" );
            if (user != null) {
                getFirmUserByDate( user.getId(), format2.format( new Date() ), format2.format( new Date() ) );
                //Objects.requireNonNull( getSupportActionBar() ).setTitle( user.getName() );
                user_name.setText( user.getName() );
                if (user.getImage() != null) {
                    if (user.getImage().contains( "https://" )) {
                        Glide.with(this).load( APIUrl.PROFILE_URL + user.getImage() ).into( profile_image );
                    } else if (user.getImage().contains( " " )) {

                    } else {
                        Glide.with(this).load( APIUrl.PROFILE_URL + user.getImage() ).into( profile_image );
                    }
                }
            }
        }
    }

    private void initViews() {
        calendar = Calendar.getInstance();
        spaceshipImage =  findViewById( R.id.spaceshipImage );
        login_details_lay = findViewById( R.id.login_details_lay );
        login_details_lay.setVisibility( View.GONE );
        ivEmptyStates = findViewById( R.id.ivEmptyStates );
        login_time = findViewById( R.id.login_time );
        logout_time = findViewById( R.id.logout_time );
        login_address = findViewById( R.id.login_address );
        logout_address = findViewById( R.id.logout_address );
        next_day = findViewById( R.id.next_day );
        previous_day = findViewById( R.id.previous_day );
        current_date = findViewById( R.id.current_date );
        View view = findViewById( R.id.frim_user_tracking_layout );
        profile_image = view.findViewById( R.id.profile_image );
        user_name =  view.findViewById( R.id.textViewName );
        user_status =  view.findViewById( R.id.status );
        current_date.setText( format2.format( new Date() ) );
        previous_day.setOnClickListener( new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                calendar.setTime( mDate = getDate( current_date.getText().toString() ) );
                calendar.add( Calendar.DAY_OF_YEAR, -1 );
                current_date.setText( format2.format( date_ = calendar.getTime() ) );
                getFirmUserByDate( user.getId(), format2.format( date_ = calendar.getTime() ), format2.format( date_ = calendar.getTime() ) );
            }
        } );
        next_day.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.setTime( mDate = getDate( current_date.getText().toString() ) );
                calendar.add( Calendar.DAY_OF_YEAR, 1 );
                current_date.setText( format2.format( date_ = calendar.getTime() ) );
                getFirmUserByDate( user.getId(), format2.format( date_ = calendar.getTime() ), format2.format( date_ = calendar.getTime() ) );
            }
        } );
    }

    public void getFirmUserByDate(Integer userId, String from_date, String to_date) {
        System.out.println( "Suree : " + from_date + "  " + to_date );
        mService = ApiClient.getClient().create( UserApi.class );
        Call<List<Example>> call = mService.getFirmUserByDate( userId, from_date, to_date );
        call.enqueue( new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                if (response.code() == 200) {
                    for (Example example : response.body()) {
                        if (!example.getError() && example.getFirmusers() != null && example.getFirmusers().size() != 0) {
                            setUserTrackingData( example.getFirmusers() );
                        } else {
                            setUserTrackingData( example.getFirmusers() );
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {
                Toast.makeText( FirmUserTracking.this, t.getMessage(), Toast.LENGTH_SHORT ).show();
                System.out.println( "Suree :" + t.getMessage() );
            }
        } );
    }



    private void setUserTrackingData(List<Firmuser> firmuserList) {
        if (firmuserList != null && firmuserList.size() != 0) {
            ivEmptyStates.setVisibility( View.GONE );
            login_details_lay.setVisibility( View.VISIBLE );
            for (Firmuser firmuser : firmuserList) {
                if (firmuser.getStatus().equalsIgnoreCase( "Present" )) {
                    user_status.setVisibility( View.VISIBLE );
                    user_status.setText( firmuser.getStatus() );
                    user_status.setBackgroundColor( android.graphics.Color.parseColor( "#43A047" ) );
                } else if (firmuser.getStatus().equalsIgnoreCase( "Absent" )) {
                    user_status.setVisibility( View.VISIBLE );
                    user_status.setText( firmuser.getStatus() );
                    user_status.setBackgroundColor( android.graphics.Color.parseColor( "#ff6347" ) );
                }
                if (!firmuser.getSigninDate().isEmpty()) {
                    login_time.setText( time.format( getDateTime( firmuser.getSigninDate() ) ) );
                }
                if (!firmuser.getSignoutDate().isEmpty()) {
                    logout_time.setText( time.format(getDateTime( firmuser.getSignoutDate() )) );
                }
                if(!firmuser.getSigninAddress().isEmpty()&&firmuser.getSigninAddress()!=null){
                    login_address.setText( firmuser.getSigninAddress() );
                }if(!firmuser.getSignoutAddress().isEmpty()&&firmuser.getSignoutAddress()!=null){
                    logout_address.setText( firmuser.getSignoutAddress() );
                }

                if(firmuser.getSigninLat()!=null&&firmuser.getSigninLng()!=null&&firmuser.getSignoutLat()!=null&&firmuser.getSignoutLng()!=null){
                    place1 = new MarkerOptions().position(new LatLng(Double.parseDouble( firmuser.getSigninLat() ), Double.parseDouble( firmuser.getSigninLng() ))).title("Location 1");
                    place2 = new MarkerOptions().position(new LatLng(Double.parseDouble( firmuser.getSignoutLat() ), Double.parseDouble( firmuser.getSignoutLng() ))).title("Location 2");
                    MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapNearBy);
                    mapFragment.getMapAsync(this);
                    new FetchURL( FirmUserTracking.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                }
            }

        } else {
            login_details_lay.setVisibility( View.GONE );
            ivEmptyStates.setVisibility( View.VISIBLE );
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation( this, R.anim.hyperspace_jump );
            spaceshipImage.startAnimation( hyperspaceJumpAnimation );
        }
    }

    private Date getDateTime(String sentat) {
        Date date = null;
        try {
            date = format.parse( sentat );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    private Date getDate(String toString) {
        Date date = null;
        try {
            date = format2.parse( toString );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /*Map Functionality*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        mMap.addMarker(place1);
        mMap.addMarker(new MarkerOptions().position(place2.getPosition()).icon(bitmapDescriptorFromVector(this, R.drawable.ic_bike)));

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

        mMap.moveCamera( CameraUpdateFactory.newLatLng(place2.getPosition()));
        CameraPosition cameraPosition1 = new CameraPosition.Builder().target(place2.getPosition()).zoom(17).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_bike);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (user != null) {
            getFirmUserByDate( user.getId(), format2.format( new Date() ), format2.format( new Date() ) );
        }
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
        return super.onOptionsItemSelected( item );
    }
}
