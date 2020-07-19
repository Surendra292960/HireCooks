package com.test.sample.hirecooks.Activity.Home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.test.sample.flowingdrawer_core.ElasticDrawer;
import com.test.sample.flowingdrawer_core.FlowingDrawer;
import com.test.sample.hirecooks.Activity.Cart.CartActivity;
import com.test.sample.hirecooks.Activity.Search.SearchResultActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Fragments.Home.HomeFragment;
import com.test.sample.hirecooks.Fragments.OrdersFragment;
import com.test.sample.hirecooks.Fragments.ProfileFragment;
import com.test.sample.hirecooks.Fragments.SearchFragment;
import com.test.sample.hirecooks.Libraries.BedgeNotification.NotificationCountSetClass;
import com.test.sample.hirecooks.Fragments.Home.MenuListFragment;
import com.test.sample.hirecooks.Models.TokenResponse.Token;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Services.TrackerService;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    private FlowingDrawer mDrawer;
    public View appRoot,toolbar_layout;
    public Toolbar toolbar;
    private ViewPager mViewPager;
    private List<Fragment> fragmentList;
    public BottomNavigationView mNavigationView;
    public static int notificationCountCart = 0;
    private User user;
    private UserApi mService;
    private String deviceId;
    private Token token;
    private static final int PERMISSIONS_REQUEST = 1;
    private boolean doubleBackToExitPressedOnce = false;

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
        setupToolbar();
        setupMenu();
        getTokenFromServer(user.getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"WrongConstant", "ClickableViewAccessibility"})
    private void initViews() {
        Fresco.initialize(this);
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
                            case R.id.navigation_orders:
                                toolbar.setTitle("My Orders");
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

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("get token",newToken);
                System.out.println("get Token : "+newToken);
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
        startService(new Intent(this, TrackerService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        }
    }

    private void getToken(final String phone, int userId, String firm_id) {
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("get token",newToken);
                if(!newToken.isEmpty()&&userId!=0){
                    if(token!=null){
                        updateTokenToServer(phone,newToken,0,deviceId,userId,firm_id);
                    }
                    else {
                        sendTokenToServer(phone,newToken,0,deviceId,userId);
                    }
                }else{
                   // ShowToast("Token Not Generated");
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
                  //  ShowToast("Failed due to :"+response.body().getMessage());
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
        fragmentList.add(OrdersFragment.newInstance());
        fragmentList.add(ProfileFragment.newInstance());
        fragmentList.add(SearchFragment.newInstance());
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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,"Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }
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
            finish();
            startActivity(new Intent(MainActivity.this, CartActivity.class));
            return true;
        }else if ( id == R.id.action_searchresult ) {
            finish();
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

        } else {
            showAlert(getResources().getString(R.string.no_internet));
        }
    }
}
