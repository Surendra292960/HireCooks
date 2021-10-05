package com.test.sample.hirecooks.Activity.Home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.test.sample.flowingdrawer_core.ElasticDrawer;
import com.test.sample.flowingdrawer_core.FlowingDrawer;
import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Activity.Search.SearchResultActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Fragments.Home.HomeFragment;
import com.test.sample.hirecooks.Fragments.Home.MenuListFragment;
import com.test.sample.hirecooks.Fragments.MessageFragment;
import com.test.sample.hirecooks.Fragments.NotificationFragment;
import com.test.sample.hirecooks.Fragments.ProfileFragment;
import com.test.sample.hirecooks.Libraries.BedgeNotification.NotificationCountSetClass;
import com.test.sample.hirecooks.Models.FirmUsers.Example;
import com.test.sample.hirecooks.Models.FirmUsers.Firmuser;
import com.test.sample.hirecooks.Models.TokenResponse.Token;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Services.TrackerService;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.facebook.drawee.backends.pipeline.Fresco;

public class MainActivity extends BaseActivity implements OnSuccessListener<AppUpdateInfo> {
    SimpleDateFormat format = new SimpleDateFormat("yyy-dd-MM HH:mm:ss");
    SimpleDateFormat format2 = new SimpleDateFormat("yyy-dd-MM");
    DateFormat date = new SimpleDateFormat("dd MMM yyyy");
    DateFormat time = new SimpleDateFormat("HH:mm a");
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private List<Example> exampleArrayList;
    private List<Firmuser> firmuserArrayList;
    private FlowingDrawer mDrawer;
    public View appRoot, toolbar_layout;
    public Toolbar toolbar;
    private ViewPager mViewPager;
    private List<Fragment> fragmentList;
    public BottomNavigationView mNavigationView;
    public static int notificationCountCart = 0;
    private User user;
    private UserApi mService;
    private String deviceId, currentVersion;
    private Token token;
    private static final int PERMISSIONS_REQUEST = 1;
    private boolean doubleBackToExitPressedOnce = false;
    private AppUpdateManager appUpdateManager;
    private static final int REQUEST_CODE = 1234;
    private String subAddress;
    private String pinCode;
    private LatLng latLng;
    private FusedLocationProviderClient client;
    private String created_at;
    private String status;
    private GoogleSignInAccount account;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationCountCart = cartCount();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        user = SharedPrefManager.getInstance(this).getUser();

        initData();
        initViews();
       /* status = getFirmUserByDate(user.getId(),format2.format(new Date( )),format2.format(new Date( )));
        if(status.equalsIgnoreCase( "Present" )){

        }else if(status.equalsIgnoreCase( "Absent" )){
            if(user.getUserType().equalsIgnoreCase( "Rider" )){
                signInAlert();
            }
        }*/

        setupToolbar();
        setupMenu();
        getTokenFromServer(user.getId());

        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        getCurrentVersion();
        client = LocationServices.getFusedLocationProviderClient( MainActivity.this);
        account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"WrongConstant", "ClickableViewAccessibility"})
    private void initViews() {
        // Fresco.initialize(this);
        mDrawer = findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        appRoot = findViewById(R.id.appRoot);
        mNavigationView = findViewById(R.id.bye_burger);
        mViewPager = findViewById(R.id.viewpager);

        View toolbar_view = findViewById(R.id.toolbar_interface);
        toolbar=toolbar_view.findViewById(R.id.toolbar);
        toolbar_layout =  toolbar_view.findViewById(R.id.toolbar_layout);
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override public int getCount() {
                return fragmentList.size();
            }
        });

        mNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_home:
                                toolbar.setTitle("");
                                mViewPager.setCurrentItem(0);
                                return true;
                            case R.id.navigation_message:
                                toolbar.setTitle("Message");
                                mViewPager.setCurrentItem(1);
                                return true;
                            case R.id.navigation_profile:
                                toolbar.setTitle("Profile");
                                mViewPager.setCurrentItem(2);
                                return true;
                            case R.id.navigation_notification:
                                toolbar.setTitle("Notifications");
                                mViewPager.setCurrentItem(3);
                                return true;
                        }
                        return false;
                    }
                });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.e("get token",token);
                        System.out.println("get Token : "+token);
                    }
                });

        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableGPS();
        }
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
        }
    }

    private void startTrackerService() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(MainActivity.this, TrackerService.class));
            }
        },400);
    }

 /*   @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        }
    }*/

    private void getToken(final String phone, int userId, String firm_id) {
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        if(token!=null){
                            updateTokenToServer(phone,token,0,deviceId,userId,firm_id);
                        }
                        else {
                            sendTokenToServer(phone,token,0,deviceId,userId);
                        }
                    }
                });
    }

    private void updateTokenToServer(String phone, String newToken, int isServerToken, String deviceId, int userId, String firm_id) {
        mService = ApiClient.getClient().create(UserApi.class);
        Call<TokenResult> call = mService.updateToken(phone,newToken,deviceId,isServerToken,userId,firm_id);
        call.enqueue(new Callback<TokenResult>() {
            @Override
            public void onResponse(Call<TokenResult> call, Response<TokenResult> response) {
                int statusCode = response.code();
                if(statusCode==200&&response.body().getError()==false) {
                    Constants.CurrentToken = response.body();
                    SharedPrefManager.getInstance(MainActivity.this).token(Constants.CurrentToken.getToken());
                    //  ShowToast("Token Updated Successfuly");
                }
                else{
                    // ShowToast("Failedd due to :"+response.code());
                }
            }

            @Override
            public void onFailure(Call<TokenResult> call, Throwable t) {
            }
        });
    }

    private void getTokenFromServer(final int userId) {
        UserApi mService =  ApiClient.getClient().create(UserApi.class);
        Call<TokenResult> call1 = mService.getTokenByUserId(userId);
        call1.enqueue(new Callback<TokenResult>() {
            @Override
            public void onResponse(Call<TokenResult> call, Response<TokenResult> response) {
                int statusCode = response.code();
                if(statusCode==200&&response.body().getError()==false) {
                    token = response.body().getToken();
                    Constants.CurrentToken = response.body();
                    if(token!=null){
                        SharedPrefManager.getInstance(MainActivity.this).token(Constants.CurrentToken.getToken());
                        getToken(user.getPhone(),user.getId(),user.getFirmId());
                    }else{
                        getToken(user.getPhone(),user.getId(),user.getFirmId());
                    }
                }
                else{
                      ShowToast("Failed due to :"+response.body().getMessage());
                     getToken(user.getPhone(),user.getId(),user.getFirmId());
                }
            }

            @Override
            public void onFailure(Call<TokenResult> call, Throwable t) {
                System.out.println("Suree: "+t.getMessage());
            }
        });
    }

    private void sendTokenToServer(String phone, String newToken, int isServerToken, String deviceId, int userId) {
        UserApi mService = ApiClient.getClient().create(UserApi.class);
        Call<TokenResult> call1 = mService.registerToken(phone,newToken,deviceId,isServerToken,userId);
        call1.enqueue(new Callback<TokenResult>() {
            @Override
            public void onResponse(Call<TokenResult> call, Response<TokenResult> response) {
            }

            @Override
            public void onFailure(Call<TokenResult> call, Throwable t) {
                System.out.println("Suree: "+t.getMessage());
            }
        });
    }

    private void initData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(HomeFragment.newInstance());
        fragmentList.add( MessageFragment.newInstance());
        fragmentList.add(ProfileFragment.newInstance());
        fragmentList.add(NotificationFragment.newInstance());
    }

    protected void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.toggleMenu();
            }
        });
    }

    private void setupMenu() {
        FragmentManager fm = getSupportFragmentManager();
        MenuListFragment mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MenuListFragment();
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        showalertbox();
        return;
    }

    private void showalertbox() {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.exit_alert_layout,null);
        AppCompatTextView no_btn = view.findViewById(R.id.no_btn);
        AppCompatTextView exit_app_btn = view.findViewById(R.id.exit_app_btn);
        dialogBuilder.setView(view);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        no_btn.setOnClickListener( v -> {
            try {
                dialog.dismiss();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
        exit_app_btn.setOnClickListener( v -> {
            try {
                dialog.dismiss();
                finish();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
    }

    private void signInAlert() {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.exit_alert_layout,null);
        TextView ask = view.findViewById(R.id.ask);
        ask.setText( "Please Sign In !" );
        AppCompatTextView no_btn = view.findViewById(R.id.no_btn);
        AppCompatTextView exit_app_btn = view.findViewById(R.id.exit_app_btn);
        exit_app_btn.setText( "SignIn" );
        dialogBuilder.setView(view);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        no_btn.setOnClickListener( v -> {
            try {
                dialog.dismiss();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
        exit_app_btn.setOnClickListener( v -> {
            try {
                dialog.dismiss();
                if (checkLocationPermission()) {
                    getCurrentLocation();
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater ( );
        menuInflater.inflate ( R.menu.cart_menu , menu );
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_cart);
        NotificationCountSetClass.setAddToCart(MainActivity.this, item,notificationCountCart);
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }else if ( id == R.id.action_cart ) {
            startActivity(new Intent(MainActivity.this, PlaceOrderActivity.class));
            return true;
        }else if ( id == R.id.action_searchresult ) {
            startActivity(new Intent(MainActivity.this, SearchResultActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRestart(){
        notificationCountCart = cartCount();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean b = false;
        notificationCountCart = cartCount();
        if(NetworkUtil.checkInternetConnection(MainActivity.this)) {
            initData();
            HomeFragment.newInstance();
        }
    }

    private void getCurrentVersion ( ) {
        PackageManager pm = this.getPackageManager ( );
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo ( this.getPackageName ( ) , 0 );

        } catch ( PackageManager.NameNotFoundException e1 ) {
            e1.printStackTrace ( );
        }
        assert pInfo != null;
        currentVersion = pInfo.versionName;
        new GetVersionCode ( MainActivity.this , currentVersion ).execute ( );

    }

    private void startUpdate ( final AppUpdateInfo appUpdateInfo ) {
        final Activity activity = this;
        new Thread ( ( ) -> {
            try {
                appUpdateManager.startUpdateFlowForResult ( appUpdateInfo ,
                        AppUpdateType.IMMEDIATE ,
                        activity ,
                        REQUEST_CODE );
            } catch ( IntentSender.SendIntentException e ) {
                e.printStackTrace ( );
            }
        } ).start ( );
    }
    private void popupSnackbarForCompleteUpdate ( ) {
        Snackbar snackbar =
                Snackbar.make (
                        findViewById ( R.id.activity_main ) ,
                        "An update has just been downloaded." ,
                        Snackbar.LENGTH_INDEFINITE );
        snackbar.setAction ( "RESTART" , view -> appUpdateManager.completeUpdate ( ) );
        snackbar.setActionTextColor ( getResources ( ).getColor ( R.color.style_color_primary ) );
        snackbar.show ( );
    }

    @Override
    public void onSuccess(AppUpdateInfo appUpdateInfo) {
        if ( appUpdateInfo.updateAvailability ( ) == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS ) {
            startUpdate ( appUpdateInfo );
        } else if ( appUpdateInfo.installStatus ( ) == InstallStatus.DOWNLOADED ) {
            popupSnackbarForCompleteUpdate ( );
        } else if ( appUpdateInfo.updateAvailability ( ) == UpdateAvailability.UPDATE_AVAILABLE ) {
            startUpdate ( appUpdateInfo );
        }
    }

    private static class GetVersionCode extends AsyncTask< Void, String, String > {
        private WeakReference< MainActivity > activityReference;
        private String currentVersion;
        // only retain a weak reference to the activity
        GetVersionCode ( MainActivity context , String currentVersions ) {
            activityReference = new WeakReference <> ( context );
            currentVersion = currentVersions;
        }
        @Override
        protected String doInBackground ( Void... voids ) {
            String newVersion = null;
            try {
                Connection connection = Jsoup.connect ( "https://play.google.com/store/apps/details?id=app.hirecook.com.hirecook" + "&hl=en" );
                Document document = connection.timeout ( 30000 )
                        .userAgent ( "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6" )
                        .referrer ( "http://www.google.com" )
                        .get ( );
                Elements versions = document.getElementsByClass ( "htlgb" );
                for ( int i = 0 ; i < versions.size ( ) ; i++ ) {
                    newVersion = versions.get ( i ).text ( );
                    if ( Pattern.matches ( "^[0-9].[0-9].[0-9]{2}$" , newVersion ) ) {
                        break;
                    }
                }

            } catch ( Exception e ) {
                return newVersion;
            }
            return newVersion;
        }
        @Override
        protected void onPostExecute ( String onlineVersion ) {
            super.onPostExecute ( onlineVersion );
            if ( onlineVersion != null && ! onlineVersion.isEmpty ( ) ) {
                if ( ! onlineVersion.equalsIgnoreCase ( currentVersion ) ) {
                    // get a reference to the activity if it is still there
                    MainActivity activity = activityReference.get ( );
                    if ( ! activity.isFinishing ( ) ) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder ( activity );
                        builder.setTitle ( "A New Update is Available on Play Store" );
                        builder.setPositiveButton ( "Update" , ( dialog , which ) -> {
                            activity.startActivity ( new Intent ( Intent.ACTION_VIEW , Uri.parse
                                    ( "https://play.google.com/store/apps/details?id=app.hirecook.com.hirecook" ) ) );
                            dialog.dismiss ( );

                        } );
                        builder.setNegativeButton ( "Cancel" , ( dialog , which ) -> dialog.dismiss ( ) );
                        builder.setCancelable ( false );
                        AlertDialog alertdialog = builder.create ( );
                        alertdialog.show ( );
                    }
                }
            }
        }
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
                                    status = firmuser.getStatus();
                                }else{
                                    status = firmuser.getStatus();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {
                Toast.makeText( MainActivity.this,  t.getMessage(), Toast.LENGTH_SHORT ).show();
                System.out.println( "Suree :" + t.getMessage() );
            }
        } );
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
                            Toast.makeText( MainActivity.this, "Login Success", Toast.LENGTH_SHORT ).show();
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
                            Toast.makeText( MainActivity.this, "Logout Success", Toast.LENGTH_SHORT ).show();
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
        if(!status.equalsIgnoreCase( "Present" )){
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
        }else{
            Toast.makeText( this, "", Toast.LENGTH_SHORT ).show();
        }
    }

    private boolean checkLocationPermission() {
        //check the location permissions and return true or false.
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permissions granted
            //Toast.makeText(MainActivity.this, "permissions granted", Toast.LENGTH_LONG).show();
            return true;
        } else {
            //permissions NOT granted
            //if permissions are NOT granted, ask for permissions
            Toast.makeText(MainActivity.this, "Please enable permissions", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // MainActivity.this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Permissions request")
                        .setMessage("we need your permission for location in order to show you MainActivity.this example")
                        .setPositiveButton("Ok, I agree", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        if ( ActivityCompat.checkSelfPermission ( MainActivity.this , android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission ( MainActivity.this , android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            //request the last location and add a listener to get the response. then update the UI.
            client.getLastLocation ( ).addOnSuccessListener ( MainActivity.this , location -> {
                // Got last known location.
                Toast.makeText ( MainActivity.this , "location" , Toast.LENGTH_SHORT ).show ( );
                if ( location != null ) {
                    setLocation (location);
                } else {
                    Toast.makeText ( MainActivity.this , "location: IS NULL" , Toast.LENGTH_SHORT ).show ( );
                }
            } ).addOnFailureListener ( MainActivity.this , new OnFailureListener( ) {
                @Override
                public void onFailure ( @NonNull Exception e ) {
                    Toast.makeText ( MainActivity.this , "getCurrentLocation FAILED" , Toast.LENGTH_LONG ).show ( );
                }
            } );
        } else {//client
            Toast.makeText ( MainActivity.this , "getCurrentLocation ERROR" , Toast.LENGTH_LONG ).show ( );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    getCurrentLocation();
                    startTrackerService();
                } else {
                    // permission denied
                    /*TextView locationText = findViewById(R.id.locationText);
                    locationText.setText("location: permission denied");*/

                    new android.app.AlertDialog.Builder(MainActivity.this)
                            .setMessage("Cannot get the location!")
                            .setPositiveButton("OK", null)
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }
        }
    }

    private String getAddress( LatLng latLng) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        String result = null;
        try {
            List <Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);
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
